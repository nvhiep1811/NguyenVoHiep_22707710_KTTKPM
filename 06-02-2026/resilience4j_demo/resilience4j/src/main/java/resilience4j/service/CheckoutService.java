package resilience4j.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;


@Service
public class CheckoutService {
  private final NodeRiskReactiveClient nodeClient;

  public CheckoutService(NodeRiskReactiveClient nodeClient) {
    this.nodeClient = nodeClient;
  }

  public Mono<Map<String, Object>> checkout(int amount) {
    return nodeClient.getRisk(amount)
        .map(risk -> Map.of("amount", amount, "status", "OK", "risk", risk))
        .onErrorResume(ex -> Mono.just(Map.of(
            "amount", amount,
            "status", "FALLBACK",
            "reason", ex.getClass().getSimpleName()
        )));
  }
}