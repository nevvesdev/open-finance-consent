package br.com.nevvesdev.openfinance.adapter.in.web.controller;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mock/bank-b")
public class MockBankBController {

    private static final List<Map<String, Object>> ACCOUNTS = List.of(
            Map.of(
                    "accountId", "b9c8d7e6-0001-0001-0001-000000000001",
                    "brandName", "Banco B",
                    "type", "CONTA_CORRENTE",
                    "currency", "BRL",
                    "number", "789012",
                    "checkDigit", "1",
                    "availableAmount", new BigDecimal("2100.75"),
                    "blockedAmount", new BigDecimal("200.00"),
                    "automaticallyInvestedAmount", new BigDecimal("0.00")
            )
    );

    @GetMapping("/accounts")
    public Map<String, Object> getAccounts() {
        return Map.of("data", ACCOUNTS, "bank", "Bank B", "totalRecords", ACCOUNTS.size());
    }

    @GetMapping("/accounts/{accountId}/balances")
    public Map<String, Object> getBalance(@PathVariable String accountId) {
        return ACCOUNTS.stream()
                .filter(a -> a.get("accountId").equals(accountId))
                .findFirst()
                .map(a -> Map.<String, Object>of(
                        "data", Map.of(
                                "availableAmount", a.get("availableAmount"),
                                "blockedAmount", a.get("blockedAmount"),
                                "automaticallyInvestedAmount", a.get("automaticallyInvestedAmount")
                        )
                ))
                .orElse(Map.of("error", "Account not found"));
    }
}