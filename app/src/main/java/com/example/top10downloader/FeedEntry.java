package com.example.top10downloader;

public class FeedEntry {

    private String name;
    private String artist;
    private String releaseDate;
    private String summary;
    private String imageUrl;

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getArtist() {
        return artist;
    }

    void setArtist(String artist) {
        this.artist = artist;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    String getSummary() {
        return summary;
    }

    void setSummary(String summary) {
        this.summary = summary;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    void setImageUrl(String image) {
        this.imageUrl = image;
    }

    @Override
    public String toString() {
        return "FeedEntry{" +
                "name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
