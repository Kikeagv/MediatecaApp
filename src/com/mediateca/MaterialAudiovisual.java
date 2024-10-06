package com.mediateca;

public abstract class MaterialAudiovisual extends Material {
    private String genre;

    public MaterialAudiovisual(String id, String title, String genre) {
        super(id, title);
        this.genre = genre;
    }

    public String getGenre() {
        return genre;
    }
}
