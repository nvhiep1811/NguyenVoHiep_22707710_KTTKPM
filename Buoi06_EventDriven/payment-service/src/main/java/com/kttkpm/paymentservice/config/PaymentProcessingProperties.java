package com.kttkpm.paymentservice.config;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.payment")
public class PaymentProcessingProperties {

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private double successRate = 0.8;

    private List<String> failureReasons = new ArrayList<>(
            List.of("Insufficient balance", "Payment gateway timeout", "Card rejected"));

    public double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }

    public List<String> getFailureReasons() {
        return failureReasons;
    }

    public void setFailureReasons(List<String> failureReasons) {
        this.failureReasons = failureReasons;
    }
}
