package tqs.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import tqs.backend.config.SecurityConfig;
import tqs.backend.security.JwtUtils;
import tqs.backend.service.FileStorageService;
import tqs.backend.security.UserDetailsServiceImpl;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.context.ActiveProfiles;

@WebMvcTest(FileController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class FileControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private FileStorageService fileStorageService;

        // Security constraints mocks needed for SecurityConfig
        @MockBean
        private UserDetailsServiceImpl userDetailsService;
        @MockBean
        private JwtUtils jwtUtils;

        @Test
        void whenUploadFileAuthenticated_thenReturnUrl() throws Exception {
                MockMultipartFile file = new MockMultipartFile(
                                "file",
                                "test.jpg",
                                "image/jpeg",
                                "test image".getBytes());

                String generatedFileName = "uuid-test.jpg";
                given(fileStorageService.storeFile(any())).willReturn(generatedFileName);

                mockMvc.perform(multipart("/api/files/upload")
                                .file(file)
                                .with(user("owner@example.com").password("pass").roles("OWNER")))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.fileName").value(generatedFileName))
                                .andExpect(jsonPath("$.url").value("/api/files/" + generatedFileName));
        }

        @Test
        void whenUploadFileUnauthenticated_thenReturnUnauthorized() throws Exception {
                MockMultipartFile file = new MockMultipartFile(
                                "file",
                                "test.jpg",
                                "image/jpeg",
                                "test image".getBytes());

                mockMvc.perform(multipart("/api/files/upload")
                                .file(file))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void whenUploadInvalidContentType_thenReturnBadRequest() throws Exception {
                MockMultipartFile file = new MockMultipartFile(
                                "file",
                                "test.txt",
                                "text/plain",
                                "test content".getBytes());

                mockMvc.perform(multipart("/api/files/upload")
                                .file(file)
                                .with(user("owner@example.com").password("pass").roles("OWNER")))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").exists());
        }

        @Test
        void whenGetFileExists_thenReturnResource() throws Exception {
                String fileName = "test.jpg";

                // Actually, creating a dummy file for the test to read is safer.
                Path tempFile = java.nio.file.Files.createTempFile("test-controller", ".jpg");
                java.nio.file.Files.write(tempFile, "fake image".getBytes());

                given(fileStorageService.getFilePath(fileName)).willReturn(tempFile);

                mockMvc.perform(get("/api/files/{fileName}", fileName))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.IMAGE_JPEG));

                // Clean up
                java.nio.file.Files.deleteIfExists(tempFile);
        }

        @Test
        void whenGetFileNotFound_thenReturn404() throws Exception {
                String fileName = "missing.jpg";
                Path missingPath = Paths.get("non-existent.jpg");
                given(fileStorageService.getFilePath(fileName)).willReturn(missingPath);

                mockMvc.perform(get("/api/files/{fileName}", fileName))
                                .andExpect(status().isNotFound());
        }
}
