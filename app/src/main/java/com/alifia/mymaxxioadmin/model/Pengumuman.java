package com.alifia.mymaxxioadmin.model;

public class Pengumuman {
    private String judulPengumuman;
    private String isiPengumuman;
    private String namaTingkatan;
    private String namaRegional;
    private String namaChapter;

    public Pengumuman() {

    }

    public Pengumuman(String judulPengumuman, String isiPengumuman, String namaTingkatan, String namaRegional, String namaChapter) {
        this.judulPengumuman = judulPengumuman;
        this.isiPengumuman = isiPengumuman;
        this.namaTingkatan = namaTingkatan;
        this.namaRegional = namaRegional;
        this.namaChapter = namaChapter;
    }

    // Getter and Setter methods

    public String getJudulPengumuman() {
        return judulPengumuman;
    }

    public void setJudulPengumuman(String judulPengumuman) {
        this.judulPengumuman = judulPengumuman;
    }

    public String getIsiPengumuman() {
        return isiPengumuman;
    }

    public void setIsiPengumuman(String isiPengumuman) {
        this.isiPengumuman = isiPengumuman;
    }

    public String getNamaTingkatan() {
        return namaTingkatan;
    }

    public void setNamaTingkatan(String namaTingkatan) {
        this.namaTingkatan = namaTingkatan;
    }

    public String getNamaRegional() {
        return namaRegional;
    }

    public void setNamaRegional(String namaRegional) {
        this.namaRegional = namaRegional;
    }

    public String getNamaChapter() {
        return namaChapter;
    }

    public void setNamaChapter(String namaChapter) {
        this.namaChapter = namaChapter;
    }
}
