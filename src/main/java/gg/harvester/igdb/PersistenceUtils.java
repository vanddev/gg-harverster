package gg.harvester.igdb;

import io.quarkus.logging.Log;
import jakarta.persistence.EntityManager;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PersistenceUtils {

    /**
     * Genérico para persistir entidades se ainda não existirem.
     *
     * @return int
     */
    public static <D, E extends BaseEntity> Map<Integer, E> persistAllGeneric(
            EntityManager em,
            Set<D> dtos,
            Class<E> entityClass,
            Function<D, Integer> idGetter,
            Function<D, E> dtoToEntity) {
        if (dtos == null || dtos.isEmpty()) return Map.of();

        Set<Integer> idsToCheck = new HashSet<>();
        for (D dto : dtos) {
            if (dto == null) continue;
            Integer id = idGetter.apply(dto);
            if (id != null) idsToCheck.add(id);
        }

        if (idsToCheck.isEmpty()) return Map.of();

        String entityName = entityClass.getSimpleName();


        Map<Integer, E> savedEntities = em.createQuery(
                "SELECT e FROM " + entityName + " e WHERE e.id IN :ids", entityClass
        ).setParameter("ids", idsToCheck)
                .getResultStream().collect(Collectors.toMap(e -> e.id, e -> e));


        Map<Integer, E> newEntities = new HashMap<>();

        for (D dto : dtos) {
            if (dto == null) continue;
            Integer id = idGetter.apply(dto);
            if (id != null && !savedEntities.containsKey(id)) {
                newEntities.put(id, dtoToEntity.apply(dto));
            }
        }

        for (var entity : newEntities.values()) {
            em.persist(entity);
        }

        if (!newEntities.isEmpty()) {
            Log.infof("Persisted %d new %s entities", newEntities.size(), entityName);
        } else {
            Log.debugf("No new %s entities to persist", entityName);
        }
        savedEntities.putAll(newEntities);

        return savedEntities;
    }

    /**
     * Estrutura para representar um lote de persistência genérica.
     */
    public static class Batch<D, E extends BaseEntity> {
        public final Set<D> dtos;
        public final Class<E> entityClass;
        public final Function<D, Integer> idGetter;
        public final Function<D, E> dtoToEntity;

        public Batch(Set<D> dtos, Class<E> entityClass,
                     Function<D, Integer> idGetter,
                     Function<D, E> dtoToEntity) {
            this.dtos = dtos;
            this.entityClass = entityClass;
            this.idGetter = idGetter;
            this.dtoToEntity = dtoToEntity;
        }
    }

    /**
     * Executa vários persistAllGeneric em sequência, em uma única transação.
     *
     * @return Map of entities by Id mapped by Entity class name
     */
    public static Map<String, Map<Integer, ? extends BaseEntity>> persistAllBatch(EntityManager em, List<Batch<?, ? extends BaseEntity>> batches) {
        if (batches == null || batches.isEmpty()) return null;
        HashMap<String, Map<Integer, ? extends BaseEntity>> batchesResult = new HashMap<>();
        try {
            int totalPersisted = 0;
            for (var batch : batches) {
                var result = persistAllGenericInternal(em, batch);
                totalPersisted += result.size();
                batchesResult.put(batch.entityClass.getSimpleName(), result);
            }
            Log.infof("Batch persist completed successfully. Total new entities persisted: %d", totalPersisted);
        } catch (Exception e) {
            Log.error("Error during batch persist. Transaction rolled back.", e);
            throw e;
        } finally {
            em.flush();
        }

        return batchesResult;
    }

    private static <D, E extends BaseEntity> Map<Integer, E> persistAllGenericInternal(EntityManager em, Batch<D, E> batch) {
       return persistAllGeneric(em, batch.dtos, batch.entityClass, batch.idGetter, batch.dtoToEntity);
    }
}
