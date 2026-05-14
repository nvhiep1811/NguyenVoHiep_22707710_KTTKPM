package com.kttkpm.movieservice.config;

import com.kttkpm.movieservice.domain.Movie;
import com.kttkpm.movieservice.domain.ShowTime;
import com.kttkpm.movieservice.repository.MovieRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner seedMovies(MovieRepository movieRepository) {
        return args -> {
            if (movieRepository.count() > 0) return;

            Movie m1 = new Movie("Inception", "A mind-bending thriller about dreams within dreams.", 148, "Sci-Fi", "https://picsum.photos/seed/inception/600/360");
            m1.setShowTimes(List.of(
                    new ShowTime(Instant.now().plus(1, ChronoUnit.DAYS), 120, 120, 9.99),
                    new ShowTime(Instant.now().plus(2, ChronoUnit.DAYS), 120, 120, 11.50)
            ));

            Movie m2 = new Movie("The Grand Adventure", "An epic journey across remote lands.", 132, "Adventure", "https://picsum.photos/seed/adventure/600/360");
            m2.setShowTimes(List.of(
                    new ShowTime(Instant.now().plus(3, ChronoUnit.DAYS), 90, 90, 8.5),
                    new ShowTime(Instant.now().plus(4, ChronoUnit.DAYS), 90, 90, 10.0)
            ));

            movieRepository.saveAll(List.of(m1, m2));
        };
    }
}
