package com.mediateca;

public class Libro extends MaterialEscrito {
    private String author;
    private int pages;

    public Libro(String id, String title, String publisher, String author, int pages) {
        super(id, title, publisher);
        this.author = author;
        this.pages = pages;
    }

    @Override
    public void displayInfo() {
        System.out.println("Libro: " + getTitle() + " by " + author + ", " + pages + " pages, published by " + getPublisher());
    }

    public String getAuthor() {
        return author;
    }

    public int getPages() {
        return pages;
    }
}