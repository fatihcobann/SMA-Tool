package com.codewithmad.smatool.model;

public class Post {
    private String kullanici;
    private String displayName;
    private String metin;
    private String tarih;
    private String id;

    public Post() {}

    public Post(String kullanici, String displayName, String metin) {
        this.kullanici = kullanici;
        this.displayName = displayName;
        this.metin = metin;
    }

    public String getKullanici() { return kullanici; }
    public String getDisplayName() { return displayName != null ? displayName : kullanici; }
    public String getMetin() { return metin; }
    public String getTarih() { return tarih; }
    public String getId() { return id; }

    @Override
    public String toString() {
        return String.format("@%s: %s", kullanici, metin);
    }
}