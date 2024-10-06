package com.mediateca;

public abstract class MaterialEscrito extends Material {
    private String publisher;

    public MaterialEscrito(String id, String title, String publisher) {
        super(id, title);
        this.publisher = publisher;
    }

    public String getPublisher() {
        return publisher;
    }
}