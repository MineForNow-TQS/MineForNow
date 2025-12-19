package tqs.backend.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
                "file.upload-dir=./test-uploads"
})
class FileControllerIT {

        @Autowired
        private MockMvc mockMvc;

        private final Path testUploadDir = Paths.get("./test-uploads").toAbsolutePath().normalize();

        @BeforeEach
        void setUp() throws IOException {
                Files.createDirectories(testUploadDir);
        }

        @AfterEach
        void tearDown() throws IOException {
                FileSystemUtils.deleteRecursively(testUploadDir);
        }

        @Test
        @WithMockUser(username = "owner@example.com", roles = "OWNER")
        void shouldUploadAndRetrieveFile() throws Exception {
                // 1. Upload File using fileStorageService logic implicitly tested via
                // controller
                MockMultipartFile file = new MockMultipartFile(
                                "file",
                                "integration-test.png",
                                "image/png",
                                "integration test content".getBytes());

                String responseJson = mockMvc.perform(multipart("/api/files/upload")
                                .file(file))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.fileName").exists())
                                .andExpect(jsonPath("$.url").exists())
                                .andReturn().getResponse().getContentAsString();

                // Extract filename from response (simple string parsing for IT)
                String fileName = responseJson.split("fileName\":\"")[1].split("\"")[0];

                // 2. Verify file exists on disk
                assertTrue(Files.exists(testUploadDir.resolve(fileName)));

                // 3. Retrieve file via GET endpoint
                mockMvc.perform(get("/api/files/" + fileName))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(Objects.requireNonNull(MediaType.IMAGE_PNG)))
                                .andExpect(content()
                                                .bytes(Objects.requireNonNull("integration test content".getBytes())));
        }

        @Test
        void shouldFailUploadWhenNotAuthenticated() throws Exception {
                MockMultipartFile file = new MockMultipartFile(
                                "file",
                                "test.jpg",
                                "image/jpeg",
                                "content".getBytes());

                mockMvc.perform(multipart("/api/files/upload")
                                .file(file))
                                .andExpect(status().isUnauthorized());
        }
}
