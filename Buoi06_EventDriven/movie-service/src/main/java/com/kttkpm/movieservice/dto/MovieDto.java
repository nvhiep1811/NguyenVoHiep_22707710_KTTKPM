package com.kttkpm.movieservice.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MovieDto {
    private String id;
    private String title;
    private String description;
    private Integer duration;
    private String genre;
    private String posterUrl;
    private List<ShowTimeDto> showTimes = new ArrayList<>();
    private Instant createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public List<ShowTimeDto> getShowTimes() {
        return showTimes;
    }

    public void setShowTimes(List<ShowTimeDto> showTimes) {
        this.showTimes = showTimes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
