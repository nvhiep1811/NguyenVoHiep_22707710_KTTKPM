package resilience4j.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import resilience4j.service.CheckoutService;

@RestController
public class CheckoutController {
  private final CheckoutService checkoutService;

  public CheckoutController(CheckoutService checkoutService) {
    this.checkoutService = checkoutService;
  }

  @GetMapping("/checkout")
  public Mono<Map<String, Object>> checkout(@RequestParam int amount) {
    return checkoutService.checkout(amount);
  }
}