package tqs.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import tqs.backend.exception.FileStorageException;

/**
 * Serviço para armazenamento de ficheiros (imagens de veículos).
 */
@Service
public class FileStorageService {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    private Path uploadPath;

    @PostConstruct
    public void init() {
        uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new FileStorageException("Não foi possível criar o diretório de uploads", e);
        }
    }

    /**
     * Guarda um ficheiro e retorna o nome único gerado.
     * 
     * @param file ficheiro a guardar
     * @return nome único do ficheiro guardado
     * @throws IOException se ocorrer erro ao guardar
     */
    public String storeFile(MultipartFile file) throws IOException {
        // Validar ficheiro
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Ficheiro vazio");
        }

        // Determinar extensão baseada no Content-Type
        String contentType = file.getContentType();
        String fileExtension;

        if ("image/jpeg".equals(contentType)) {
            fileExtension = ".jpg";
        } else if ("image/png".equals(contentType)) {
            fileExtension = ".png";
        } else if ("image/gif".equals(contentType)) {
            fileExtension = ".gif";
        } else if ("image/webp".equals(contentType)) {
            fileExtension = ".webp";
        } else {
            throw new IllegalArgumentException("Tipo de ficheiro não suportado: " + contentType);
        }

        // Gerar nome único
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        // Guardar ficheiro
        Path targetLocation = uploadPath.resolve(uniqueFileName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
        }

        return uniqueFileName;
    }

    /**
     * Obtém o caminho para um ficheiro.
     * 
     * @param fileName nome do ficheiro
     * @return Path para o ficheiro
     */
    public Path getFilePath(String fileName) {
        return uploadPath.resolve(fileName).normalize();
    }

    /**
     * Verifica se um ficheiro existe.
     * 
     * @param fileName nome do ficheiro
     * @return true se existir
     */
    public boolean fileExists(String fileName) {
        Path filePath = getFilePath(fileName);
        return Files.exists(filePath);
    }
}
