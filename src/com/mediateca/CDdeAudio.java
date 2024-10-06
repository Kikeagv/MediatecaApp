package com.mediateca;

public class CDdeAudio extends MaterialAudiovisual {
    private int duration;

    public CDdeAudio(String id, String title, String genre, int duration) {
        super(id, title, genre);
        this.duration = duration;
    }

    @Override
    public void displayInfo() {
        System.out.println("CD de Audio: " + getTitle() + ", genre: " + getGenre() + ", duration: " + duration + " minutes");
    }

    public int getDuration() {
        return duration;
    }
}