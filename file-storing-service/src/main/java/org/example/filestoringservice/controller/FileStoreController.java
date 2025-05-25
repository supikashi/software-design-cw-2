package org.example.filestoringservice.controller;

import jakarta.persistence.EntityNotFoundException;
import org.example.filestoringservice.entity.FileEntity;
import org.example.filestoringservice.service.FileStorageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/files")
public class FileStoreController {

    private final FileStorageService service;

    public FileStoreController(FileStorageService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            Long id = service.store(file);
            return ResponseEntity.ok(id);
        } catch (MultipartException ex) {
            return ResponseEntity.badRequest().body("Invalid file upload request: " + ex.getMessage());
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing file: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error during file upload: " + ex.getMessage());
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> download(@PathVariable Long id) {
        try {
            FileEntity e = service.getById(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + e.getFilename() + "\"")
                    .body(e.getContent());
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("File not found: " + id);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error during file download: " + ex.getMessage());
        }
    }
}
