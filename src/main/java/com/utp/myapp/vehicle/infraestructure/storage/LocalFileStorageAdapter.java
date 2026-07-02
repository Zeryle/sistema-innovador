package com.utp.myapp.vehicle.infraestructure.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class LocalFileStorageAdapter {

    private final Path uploadDir;

    public LocalFileStorageAdapter(@Value("${app.storage.upload-dir:./uploads}") String uploadDirPath) {
        this.uploadDir = Paths.get(uploadDirPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public String store(MultipartFile file, String subfolder) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String storedFilename = UUID.randomUUID() + extension;

        try {
            Path targetDir = uploadDir.resolve(subfolder);
            Files.createDirectories(targetDir);
            Path targetPath = targetDir.resolve(storedFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + subfolder + "/" + storedFilename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}
