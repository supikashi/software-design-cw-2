package org.example.filestoringservice.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.filestoringservice.entity.FileEntity;
import org.example.filestoringservice.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final FileRepository repo;

    public FileStorageService(FileRepository repo) {
        this.repo = repo;
    }

    public Long store(MultipartFile file) throws Exception {
        FileEntity e = new FileEntity();
        e.setFilename(file.getOriginalFilename());
        e.setContent(file.getBytes());
        return repo.save(e).getId();
    }

    public FileEntity getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("File not found: " + id));
    }
}
