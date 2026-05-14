package com.kttkpm.movieservice;

import com.kttkpm.movieservice.domain.Movie;
import com.kttkpm.movieservice.domain.ShowTime;
import com.kttkpm.movieservice.repository.MovieRepository;
import com.kttkpm.movieservice.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateAvailableSeats() {
        // Arrange
        String movieId = "movie1";
        String showTimeId = "showTime1";
        int initialSeats = 100;
        int seatsToDecrease = -2;

        ShowTime showTime = new ShowTime(Instant.now(), 100, initialSeats, 150000.0);
        showTime.setId(showTimeId);

        List<ShowTime> showTimes = new ArrayList<>();
        showTimes.add(showTime);

        Movie movie = new Movie("Spider-Man", "Action", 120, "Action", "url");
        movie.setId(movieId);
        movie.setShowTimes(showTimes);

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        // Act
        movieService.updateAvailableSeats(movieId, showTimeId, seatsToDecrease);

        // Assert
        assertEquals(98, movie.getShowTimes().get(0).getAvailableSeats());
        verify(movieRepository, times(1)).save(movie);
    }
}
