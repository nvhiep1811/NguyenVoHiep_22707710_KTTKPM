package com.kttkpm.movieservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kttkpm.movieservice.controller.MovieController;
import com.kttkpm.movieservice.domain.Movie;
import com.kttkpm.movieservice.domain.ShowTime;
import com.kttkpm.movieservice.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
public class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @Autowired
    private ObjectMapper objectMapper;

    private Movie sampleMovie;

    @BeforeEach
    public void setUp() {
        sampleMovie = new Movie("Inception", "A mind-bending thriller", 148, "Sci-Fi", "http://inception-poster.jpg");
        sampleMovie.setId("movie-123");
        ShowTime showTime = new ShowTime(Instant.now(), 100, 100, 120000.0);
        showTime.setId("show-123");
        sampleMovie.getShowTimes().add(showTime);
    }

    @Test
    public void testGetAllMovies() throws Exception {
        when(movieService.getAllMovies()).thenReturn(Arrays.asList(sampleMovie));

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("movie-123"))
                .andExpect(jsonPath("$[0].title").value("Inception"))
                .andExpect(jsonPath("$[0].showTimes[0].id").value("show-123"));
    }

    @Test
    public void testGetMovieById_Found() throws Exception {
        when(movieService.getMovieById("movie-123")).thenReturn(Optional.of(sampleMovie));

        mockMvc.perform(get("/api/movies/movie-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("movie-123"))
                .andExpect(jsonPath("$.title").value("Inception"));
    }

    @Test
    public void testGetMovieById_NotFound() throws Exception {
        when(movieService.getMovieById("movie-999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/movies/movie-999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateMovie() throws Exception {
        when(movieService.createMovie(any(Movie.class))).thenReturn(sampleMovie);

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleMovie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("movie-123"))
                .andExpect(jsonPath("$.title").value("Inception"));
    }

    @Test
    public void testUpdateMovie_Success() throws Exception {
        when(movieService.updateMovie(eq("movie-123"), any(Movie.class))).thenReturn(sampleMovie);

        mockMvc.perform(put("/api/movies/movie-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleMovie)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("movie-123"));
    }

    @Test
    public void testDeleteMovie() throws Exception {
        mockMvc.perform(delete("/api/movies/movie-123"))
                .andExpect(status().isOk());
    }
}
