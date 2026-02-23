package gg.harvester.igdb.platform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlatformServiceTest {

    @Mock
    PlatformRepository repository;

    @Mock
    PlatformClient platformClient;

    @InjectMocks
    PlatformService service; // criado com platformClient + repository

    @Test
    void findByName_whenRepositoryHasPlatform_returnsPlatform() {
        // Arrange
        Platform expected = new Platform();
        expected.id = 1;
        expected.name = "PS5";

        when(repository.findByName("PS5")).thenReturn(Optional.of(expected));

        // Act
        Platform result = service.findByName("PS5");

        // Assert
        assertNotNull(result);
        assertEquals(expected, result);
        verify(repository, times(1)).findByName("PS5");
        verifyNoInteractions(platformClient);
    }

    @Test
    void findByName_whenNotInRepository_callsFetchPlatform() {
        // Arrange
        when(repository.findByName("NewPlatform")).thenReturn(Optional.empty());

        Platform produced = new Platform();
        produced.id = 99;
        produced.name = "NewPlatform";

        // spy para verificar que fetchPlatform é invocado e controlar seu retorno
        PlatformService spyService = spy(new PlatformService(platformClient, repository));
        doReturn(produced).when(spyService).fetchPlatform("NewPlatform");

        // Act
        Platform result = spyService.findByName("NewPlatform");

        // Assert
        assertNotNull(result);
        assertEquals(99, result.id);
        verify(repository, times(1)).findByName("NewPlatform");
        verify(spyService, times(1)).fetchPlatform("NewPlatform");
    }

    @Test
    void findByName_whenRepositoryThrows_propagatesException() {
        // Arrange
        when(repository.findByName("boom")).thenThrow(new RuntimeException("DB error"));

        // Act / Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.findByName("boom"));
        assertEquals("DB error", ex.getMessage());
        verify(repository, times(1)).findByName("boom");
    }

    @Test
    void findByName_whenRepositoryEmpty_andFetchReturnsNull_returnsNull() {
        // Arrange
        when(repository.findByName("Missing")).thenReturn(Optional.empty());

        PlatformService spyService = Mockito.spy(new PlatformService(platformClient, repository));
        doReturn(null).when(spyService).fetchPlatform("Missing");

        // Act
        Platform result = spyService.findByName("Missing");

        // Assert
        assertNull(result);
        verify(repository, times(1)).findByName("Missing");
        verify(spyService, times(1)).fetchPlatform("Missing");
    }
}
