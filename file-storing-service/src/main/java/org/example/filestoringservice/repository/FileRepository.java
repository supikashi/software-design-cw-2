package org.example.filestoringservice.repository;

import org.example.filestoringservice.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileEntity, Long> {}