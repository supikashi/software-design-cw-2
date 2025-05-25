package org.example.fileanalysisservice.service;

import org.example.fileanalysisservice.db_entity.FileAnalysisResult;
import org.example.fileanalysisservice.domain_entity.FileStats;
import org.example.fileanalysisservice.repository.FileAnalysisResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class FileAnalysisService {

    private final RestTemplate restTemplate;
    private final FileAnalysisResultRepository repository;

    public FileAnalysisService(RestTemplate restTemplate,
                               FileAnalysisResultRepository repository) {
        this.restTemplate = restTemplate;
        this.repository = repository;
    }

    public FileStats analyze(Long fileId) {
        return repository.findById(fileId)
                .map(e -> new FileStats(e.getParagraphs(), e.getWords(), e.getCharacters(), true))
                .orElseGet(() -> {
                    String content;
                    try {
                        content = restTemplate.getForObject(
                                "http://file-storing-service/files/{id}",
                                String.class,
                                fileId
                        );
                    } catch (HttpStatusCodeException ex) {
                        if (ex.getStatusCode().is4xxClientError()) {
                            throw new FileNotFoundException("File not found in storage: " + fileId);
                        }
                        throw new ExternalServiceException(
                                "Storage service error: " + ex.getStatusCode(), ex);
                    } catch (ResourceAccessException ex) {
                        throw new ExternalServiceUnavailableException("Storage service is unavailable", ex);
                    }

                    if (content == null) {
                        throw new FileNotFoundException("Empty content returned for file: " + fileId);
                    }

                    String[] paras = content.split("(?m)(?:\\r?\\n){2,}");
                    long paragraphs = paras.length;
                    String[] wordsArr = content.trim().isEmpty() ? new String[0] : content.trim().split("\\s+");
                    long words = wordsArr.length;
                    long characters = content.length();

                    FileAnalysisResult result = new FileAnalysisResult(fileId, paragraphs, words, characters);
                    repository.save(result);

                    return new FileStats(paragraphs, words, characters, false);
                });
    }

    public static class FileNotFoundException extends RuntimeException {
        public FileNotFoundException(String msg) { super(msg); }
    }
    public static class ExternalServiceException extends RuntimeException {
        public ExternalServiceException(String msg, Throwable cause) { super(msg, cause); }
    }
    public static class ExternalServiceUnavailableException extends RuntimeException {
        public ExternalServiceUnavailableException(String msg, Throwable cause) { super(msg, cause); }
    }
}