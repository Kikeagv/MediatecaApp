package com.mediateca;

public abstract class Material {
    private String id;
    private String title;

    public Material(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public abstract void displayInfo();
}
