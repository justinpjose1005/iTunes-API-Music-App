package com.example.lybrateassignment;

public class Track {
    String artist_name;
    String artist_id;
    String track_name;
    String preview_url;
    String artwork_url;

    public Track(String artist_name, String artist_id, String track_name, String preview_url, String artwork_url) {
        this.artist_name = artist_name;
        this.artist_id = artist_id;
        this.track_name = track_name;
        this.preview_url = preview_url;
        this.artwork_url = artwork_url;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public String getArtist_id() {
        return artist_id;
    }

    public String getTrack_name() {
        return track_name;
    }

    public String getPreview_url() {
        return preview_url;
    }

    public String getArtwork_url() {
        return artwork_url;
    }
}
