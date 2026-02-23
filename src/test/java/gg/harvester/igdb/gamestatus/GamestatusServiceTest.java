package gg.harvester.igdb.gamestatus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GamestatusServiceTest {

    @Mock
    GamestatusRepository repository;

    @Mock
    GamestatusClient gamestatusClient;

    @Test
    void returnsFromRepositoryIfPresent() {
        Gamestatus existing = mock(Gamestatus.class);
        when(repository.findByStatus("Released")).thenReturn(Optional.of(existing));

        var service = new GamestatusService(gamestatusClient, repository);
        var result = service.findReleasedStatus();

        assertSame(existing, result);
        verify(gamestatusClient, never()).fetchGameStatus(anyString());
    }

    @Test
    void fetchesParsesAndPersistsWhenNotFound() {
        when(repository.findByStatus("Released")).thenReturn(Optional.empty());

        ArrayList<GameStatusDTO> dtoList = new ArrayList<>();
        GameStatusDTO dto = new GameStatusDTO(
                0,
                "Released"
        );
        dtoList.add(dto);
        when(gamestatusClient.fetchGameStatus(anyString())).thenReturn(dtoList);

        Gamestatus parsed = mock(Gamestatus.class);

        try (MockedStatic<Gamestatus> mocked = mockStatic(Gamestatus.class)) {
            mocked.when(() -> Gamestatus.parseDTO(dto)).thenReturn(parsed);

            var service = new GamestatusService(gamestatusClient, repository);
            var result = service.findReleasedStatus();

            assertSame(parsed, result);
            verify(repository).persist(parsed);
        }
    }

    @Test
    void returnsNullWhenClientReturnsEmpty() {
        when(repository.findByStatus("Released")).thenReturn(Optional.empty());
        when(gamestatusClient.fetchGameStatus(anyString())).thenReturn(new LinkedList<>());

        var service = new GamestatusService(gamestatusClient, repository);
        var result = service.findReleasedStatus();

        assertNull(result);
        verify(repository, never()).persist(any(Gamestatus.class));
    }
}
