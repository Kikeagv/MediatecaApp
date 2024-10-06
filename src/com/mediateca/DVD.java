package com.mediateca;

public class DVD extends MaterialAudiovisual {
    private String director;

    public DVD(String id, String title, String genre, String director) {
        super(id, title, genre);
        this.director = director;
    }

    @Override
    public void displayInfo() {
        System.out.println("DVD: " + getTitle() + " directed by " + director + ", genre: " + getGenre());
    }

    public String getDirector() {
        return director;
    }
}
