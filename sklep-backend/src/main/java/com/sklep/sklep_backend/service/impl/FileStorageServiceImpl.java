package com.sklep.sklep_backend.service.impl;

import com.sklep.sklep_backend.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    @Override
    public String storeFile(MultipartFile file) throws IOException {
        File uploadDir = new File(System.getProperty("user.dir") + "/uploads");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        if (file.isEmpty()) {
            return null;
        }
        String name = UUID.randomUUID() + "-" + file.getOriginalFilename();
        File uploadedFile = new File(uploadDir + "/" + name);
        file.transferTo(uploadedFile);

        return name;
    }

}
