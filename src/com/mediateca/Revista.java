package com.mediateca;

public class Revista extends MaterialEscrito {
    private String periodicity;

    public Revista(String id, String title, String publisher, String periodicity) {
        super(id, title, publisher);
        this.periodicity = periodicity;
    }

    @Override
    public void displayInfo() {
        System.out.println("Revista: " + getTitle() + ", periodicity: " + periodicity + ", published by " + getPublisher());
    }

    public String getPeriodicity() {
        return periodicity;
    }
}
