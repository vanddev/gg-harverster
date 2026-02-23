package gg.harvester.igdb.gametype;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GametypeServiceTest {

    @Mock
    GametypeRepository repository;

    @Test
    void returnsFromRepositoryIfPresent() {
        Gametype existing = mock(Gametype.class);
        when(repository.findByType("Edition")).thenReturn(Optional.of(existing));

        var service = new GametypeService(repository);
        var result = service.findEditionType();

        assertSame(existing, result);
        verify(repository, never()).persist(any(Gametype.class));
    }

    @Test
    void createsPersistsAndReturnsWhenNotFound() {
        when(repository.findByType("Edition")).thenReturn(Optional.empty());

        var service = new GametypeService(repository);
        var result = service.findEditionType();

        assertNotNull(result);
        assertEquals(20, result.id);
        assertEquals("Edition", result.type);
        verify(repository).persist(result);
    }
}