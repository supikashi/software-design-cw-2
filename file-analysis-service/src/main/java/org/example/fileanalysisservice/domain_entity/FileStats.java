package org.example.fileanalysisservice.domain_entity;

public class FileStats {
    private long paragraphs;
    private long words;
    private long characters;
    private Boolean isItFromDatabase;

    public FileStats(long paragraphs, long words, long characters, Boolean isItFromDatabase) {
        this.paragraphs = paragraphs;
        this.words = words;
        this.characters = characters;
        this.isItFromDatabase = isItFromDatabase;
    }

    public long getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(long paragraphs) {
        this.paragraphs = paragraphs;
    }

    public long getWords() {
        return words;
    }

    public void setWords(long words) {
        this.words = words;
    }

    public long getCharacters() {
        return characters;
    }

    public void setCharacters(long characters) {
        this.characters = characters;
    }

    public Boolean getItFromDatabase() {
        return isItFromDatabase;
    }

    public void setItFromDatabase(Boolean itFromDatabase) {
        isItFromDatabase = itFromDatabase;
    }
}
