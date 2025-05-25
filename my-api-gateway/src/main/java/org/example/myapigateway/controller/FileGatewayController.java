package org.example.myapigateway.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@Tag(name = "File upload")
public class FileGatewayController {

    private final RestTemplate rest;

    @Autowired
    public FileGatewayController(RestTemplate rest) {
        this.rest = rest;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            body.add("file", resource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Long> response = rest.postForEntity(
                    "http://file-storing-service/files",
                    requestEntity,
                    Long.class
            );
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());

        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .body("File storage service error: " + ex.getResponseBodyAsString());
        } catch (ResourceAccessException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("File storage service is unavailable: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error during file upload: " + ex.getMessage());
        }
    }

    @GetMapping(value = "/download/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> download(@PathVariable Long id) {
        try {
            String url = "http://file-storing-service/files/" + id;
            String content = rest.getForObject(url, String.class);
            if (content == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("File not found: " + id);
            }
            return ResponseEntity.ok(content);

        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .body("File storage service error: " + ex.getResponseBodyAsString());
        } catch (ResourceAccessException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("File storage service is unavailable: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error during file download: " + ex.getMessage());
        }
    }

    @GetMapping(value = "/analysis/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> analyze(@PathVariable Long id) {
        String url = "http://file-analysis-service/analysis/" + id;
        try {
            ResponseEntity<String> response = rest.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    String.class
            );
            return ResponseEntity
                    .status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(response.getBody());

        } catch (HttpStatusCodeException ex) {
            return ResponseEntity
                    .status(ex.getStatusCode())
                    .body(ex.getResponseBodyAsString());

        } catch (ResourceAccessException ex) {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Analysis service is unavailable");
        }
    }
}
