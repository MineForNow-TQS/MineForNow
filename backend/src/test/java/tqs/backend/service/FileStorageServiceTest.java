package tqs.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

    @InjectMocks
    private FileStorageService fileStorageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // Configure the service to use the JUnit managed temp directory
        ReflectionTestUtils.setField(Objects.requireNonNull(fileStorageService), "uploadDir", tempDir.toString());
        fileStorageService.init();
    }

    @Test
    void whenStoreFileJpg_thenFileIsStoredWithJpgExtension() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes());

        String uniqueFileName = fileStorageService.storeFile(file);

        assertNotNull(uniqueFileName);
        assertTrue(uniqueFileName.endsWith(".jpg"));
        assertTrue(Files.exists(tempDir.resolve(uniqueFileName)));
    }

    @Test
    void whenStoreFilePng_thenFileIsStoredWithPngExtension() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "test image content".getBytes());

        String uniqueFileName = fileStorageService.storeFile(file);

        assertNotNull(uniqueFileName);
        assertTrue(uniqueFileName.endsWith(".png"));
    }

    @Test
    void whenStoreInvalidContentType_thenThrowException() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test content".getBytes());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            fileStorageService.storeFile(file);
        });

        assertTrue(exception.getMessage().contains("Tipo de ficheiro não suportado"));
    }

    @Test
    void whenStoreEmptyFile_thenThrowException() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.jpg",
                "image/jpeg",
                new byte[0]);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            fileStorageService.storeFile(file);
        });

        assertEquals("Ficheiro vazio", exception.getMessage());
    }

    @Test
    void whenGetFilePath_thenReturnsCorrectPath() {
        String fileName = "test-file.jpg";
        Path resolvedPath = fileStorageService.getFilePath(fileName);

        assertEquals(tempDir.resolve(fileName), resolvedPath);
    }

    @Test
    void whenFileExists_thenReturnTrue() throws IOException {
        String fileName = "existing.jpg";
        Files.createFile(tempDir.resolve(fileName));

        assertTrue(fileStorageService.fileExists(fileName));
    }

    @Test
    void whenFileDoesNotExist_thenReturnFalse() {
        assertFalse(fileStorageService.fileExists("non-existent.jpg"));
    }
}
