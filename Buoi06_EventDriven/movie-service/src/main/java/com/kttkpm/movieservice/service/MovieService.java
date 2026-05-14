package com.kttkpm.movieservice.service;

import com.kttkpm.movieservice.domain.Movie;
import com.kttkpm.movieservice.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Optional<Movie> getMovieById(String id) {
        return movieRepository.findById(id);
    }

    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public Movie updateMovie(String id, Movie movieDetails) {
        return movieRepository.findById(id).map(movie -> {
            movie.setTitle(movieDetails.getTitle());
            movie.setDescription(movieDetails.getDescription());
            movie.setDuration(movieDetails.getDuration());
            movie.setGenre(movieDetails.getGenre());
            movie.setPosterUrl(movieDetails.getPosterUrl());
            movie.setShowTimes(movieDetails.getShowTimes());
            return movieRepository.save(movie);
        }).orElseThrow(() -> new RuntimeException("Movie not found"));
    }

    public void deleteMovie(String id) {
        movieRepository.deleteById(id);
    }

    public void updateAvailableSeats(String movieId, String showTimeId, int seatsToChange) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found: " + movieId));

        if (movie.getShowTimes() != null) {
            for (int i = 0; i < movie.getShowTimes().size(); i++) {
                if (movie.getShowTimes().get(i).getId().equals(showTimeId)) {
                    int currentSeats = movie.getShowTimes().get(i).getAvailableSeats() != null
                            ? movie.getShowTimes().get(i).getAvailableSeats()
                            : 0;
                    int nextSeats = currentSeats + seatsToChange;
                    if (nextSeats < 0) {
                        throw new IllegalArgumentException("Not enough seats for showTimeId: " + showTimeId);
                    }
                    movie.getShowTimes().get(i).setAvailableSeats(nextSeats);
                    movieRepository.save(movie);
                    return;
                }
            }
        }
        throw new IllegalArgumentException("Showtime not found: " + showTimeId);
    }
}
