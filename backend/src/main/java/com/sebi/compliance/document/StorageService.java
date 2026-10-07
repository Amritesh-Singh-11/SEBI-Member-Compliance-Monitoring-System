package com.sebi.compliance.document;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface StorageService {
    String store(MultipartFile file, String subDirectory);
    Resource loadAsResource(String storageKey);
    void delete(String storageKey);
    String calculateChecksum(InputStream inputStream);
}
