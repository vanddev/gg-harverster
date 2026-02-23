package gg.harvester.igdb.game;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.hibernate.orm.panache.Panache;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class GameRepository implements PanacheRepository<Game> {

    public List<Integer> findGameIds(List<Integer> idsToFind) {

        if (idsToFind == null || idsToFind.isEmpty()) {
            // Return list of all game IDs using a typed JPQL query to avoid Panache projection mapping
            return Panache.getEntityManager()
                    .createQuery("select g.id from Game g", Integer.class)
                    .getResultList();
        }

        return Panache.getEntityManager()
                .createQuery("select g.id from Game g where g.id in :ids", Integer.class)
                .setParameter("ids", idsToFind)
                .getResultList();
    }

}
