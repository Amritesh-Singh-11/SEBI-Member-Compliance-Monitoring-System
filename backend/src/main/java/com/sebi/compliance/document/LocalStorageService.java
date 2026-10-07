package com.sebi.compliance.document;

import com.sebi.compliance.common.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService {

    private final Path rootLocation;

    public LocalStorageService(@Value("${app.storage.local-dir:./storage/documents}") String storageDir) {
        this.rootLocation = Paths.get(storageDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new ApiException("Could not initialize local storage location", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String store(MultipartFile file, String subDirectory) {
        if (file.isEmpty()) {
            throw new ApiException("Failed to store empty file.", HttpStatus.BAD_REQUEST);
        }

        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        if (filename.contains("..")) {
            throw new ApiException("Cannot store file with relative path outside current directory " + filename, HttpStatus.BAD_REQUEST);
        }

        String extension = getFileExtension(filename);
        String storageKey = (subDirectory != null ? subDirectory + "/" : "") + UUID.randomUUID() + "." + extension;

        try {
            Path targetPath = this.rootLocation.resolve(storageKey).normalize();
            Files.createDirectories(targetPath.getParent());
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            return storageKey;
        } catch (IOException e) {
            throw new ApiException("Failed to store file " + filename, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Resource loadAsResource(String storageKey) {
        try {
            Path file = this.rootLocation.resolve(storageKey).normalize();
            if (!file.startsWith(this.rootLocation)) {
                throw new ApiException("Path traversal attempt detected", HttpStatus.BAD_REQUEST);
            }
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ApiException("Could not read file: " + storageKey, HttpStatus.NOT_FOUND);
            }
        } catch (MalformedURLException e) {
            throw new ApiException("Could not read file: " + storageKey, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            Path file = this.rootLocation.resolve(storageKey).normalize();
            Files.deleteIfExists(file);
        } catch (IOException e) {
            // Ignore
        }
    }

    @Override
    public String calculateChecksum(InputStream inputStream) {
        try {
            return DigestUtils.md5DigestAsHex(inputStream);
        } catch (IOException e) {
            return "unknown-checksum";
        }
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }
}
