package com.mediateca;

public enum MaterialType {
    BOOKS, MAGAZINES, CDS, DVDS;

    public static MaterialType fromString(String type) {
        switch (type.toUpperCase()) {
            case "BOOKS": return BOOKS;
            case "MAGAZINES": return MAGAZINES;
            case "CDS": return CDS;
            case "DVDS": return DVDS;
            default: throw new IllegalArgumentException("Unknown material type: " + type);
        }
    }
}