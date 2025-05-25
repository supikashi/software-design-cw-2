package org.example.fileanalysisservice.db_entity;

import jakarta.persistence.*;

@Entity
@Table(name = "file_analysis_results")
public class FileAnalysisResult {
    @Id
    private Long fileId;

    private long paragraphs;
    private long words;
    private long characters;

    public FileAnalysisResult() {}
    public FileAnalysisResult(Long fileId, long paragraphs, long words, long characters) {
        this.fileId = fileId;
        this.paragraphs = paragraphs;
        this.words = words;
        this.characters = characters;
    }

    public Long getFileId() { return fileId; }
    public void setFileId(Long fileId) { this.fileId = fileId; }
    public long getParagraphs() { return paragraphs; }
    public void setParagraphs(long paragraphs) { this.paragraphs = paragraphs; }
    public long getWords() { return words; }
    public void setWords(long words) { this.words = words; }
    public long getCharacters() { return characters; }
    public void setCharacters(long characters) { this.characters = characters; }
}
