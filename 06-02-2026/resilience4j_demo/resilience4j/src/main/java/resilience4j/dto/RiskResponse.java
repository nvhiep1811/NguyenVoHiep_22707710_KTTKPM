package resilience4j.dto;

public record RiskResponse(String engine, int amount, int score) {}
