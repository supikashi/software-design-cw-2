package org.example.fileanalysisservice.repository;

import org.example.fileanalysisservice.db_entity.FileAnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileAnalysisResultRepository
        extends JpaRepository<FileAnalysisResult, Long> {
}