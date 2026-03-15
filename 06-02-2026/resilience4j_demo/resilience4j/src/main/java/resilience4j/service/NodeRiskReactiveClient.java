package resilience4j.service;

import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadRegistry;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.reactor.retry.RetryOperator;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import io.github.resilience4j.reactor.timelimiter.TimeLimiterOperator;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import resilience4j.dto.RiskResponse;

@Service
public class NodeRiskReactiveClient {

  private final WebClient webClient;

  private final CircuitBreaker cb;
  private final Retry retry;
  private final TimeLimiter tl;
  private final ThreadPoolBulkhead tpb;

  public NodeRiskReactiveClient(
      WebClient nodeWebClient,
      CircuitBreakerRegistry cbRegistry,
      RetryRegistry retryRegistry,
      TimeLimiterRegistry tlRegistry,
      ThreadPoolBulkheadRegistry tpbRegistry
  ) {
    this.webClient = nodeWebClient;
    this.cb = cbRegistry.circuitBreaker("nodeRisk");
    this.retry = retryRegistry.retry("nodeRisk");
    this.tl = tlRegistry.timeLimiter("nodeRisk");
    this.tpb = tpbRegistry.bulkhead("nodeRisk");
  }

  public Mono<RiskResponse> getRisk(int amount) {
    Mono<RiskResponse> base =
        webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/risk-score").queryParam("amount", amount).build())
            .retrieve()
            .bodyToMono(RiskResponse.class)
            // logic policies (reactive-friendly)
            .transformDeferred(TimeLimiterOperator.of(tl))
            .transformDeferred(RetryOperator.of(retry))
            .transformDeferred(CircuitBreakerOperator.of(cb));

    // ThreadPoolBulkhead isolation (pool + queue) for the downstream call
    return Mono.fromCompletionStage(
        tpb.executeSupplier(() -> base.toFuture().join())
    );
  }
}