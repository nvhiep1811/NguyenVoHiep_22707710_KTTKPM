package resilience4j.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

  @Bean
  public WebClient nodeWebClient() {
    return WebClient.builder()
            .baseUrl("http://localhost:4000")
            .build();
  }
}
