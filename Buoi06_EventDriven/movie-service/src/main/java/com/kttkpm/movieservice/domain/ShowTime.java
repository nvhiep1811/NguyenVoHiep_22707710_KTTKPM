package com.kttkpm.movieservice.domain;

import org.springframework.data.annotation.Id;
import org.bson.types.ObjectId;

import java.time.Instant;

public class ShowTime {

    @Id
    private String id;
    private Instant datetime;
    private Integer totalSeats;
    private Integer availableSeats;
    private Double price;

    public ShowTime() {
        this.id = new ObjectId().toHexString();
    }

    public ShowTime(Instant datetime, Integer totalSeats, Integer availableSeats, Double price) {
        this.id = new ObjectId().toHexString();
        this.datetime = datetime;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getDatetime() {
        return datetime;
    }

    public void setDatetime(Instant datetime) {
        this.datetime = datetime;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
