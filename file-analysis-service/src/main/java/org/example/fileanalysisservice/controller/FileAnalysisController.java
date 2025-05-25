package org.example.fileanalysisservice.controller;

import org.example.fileanalysisservice.service.FileAnalysisService;
import org.example.fileanalysisservice.domain_entity.FileStats;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analysis")
public class FileAnalysisController {

    private final FileAnalysisService service;

    public FileAnalysisController(FileAnalysisService service) {
        this.service = service;
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> analyze(@PathVariable("id") Long id) {
        try {
            FileStats stats = service.analyze(id);
            return ResponseEntity.ok(stats);
        } catch (FileAnalysisService.FileNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (FileAnalysisService.ExternalServiceUnavailableException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ex.getMessage());
        } catch (FileAnalysisService.ExternalServiceException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error during file analysis");
        }
    }
}
