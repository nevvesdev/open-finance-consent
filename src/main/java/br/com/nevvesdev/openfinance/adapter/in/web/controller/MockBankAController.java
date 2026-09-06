package br.com.nevvesdev.openfinance.adapter.in.web.controller;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/mock/bank-a")
public class MockBankAController {

    // Dados simulados do Banco A — contas correntes e poupança
    private static final List<Map<String, Object>> ACCOUNTS = List.of(
            Map.of(
                    "accountId", "a1b2c3d4-0001-0001-0001-000000000001",
                    "brandName", "Banco A",
                    "type", "CONTA_CORRENTE",
                    "currency", "BRL",
                    "number", "123456",
                    "checkDigit", "7",
                    "availableAmount", new BigDecimal("4500.00"),
                    "blockedAmount", new BigDecimal("0.00"),
                    "automaticallyInvestedAmount", new BigDecimal("0.00")
            ),
            Map.of(
                    "accountId", "a1b2c3d4-0002-0002-0002-000000000002",
                    "brandName", "Banco A",
                    "type", "CONTA_POUPANCA",
                    "currency", "BRL",
                    "number", "654321",
                    "checkDigit", "3",
                    "availableAmount", new BigDecimal("12300.50"),
                    "blockedAmount", new BigDecimal("500.00"),
                    "automaticallyInvestedAmount", new BigDecimal("1000.00")
            )
    );

    private static final List<Map<String, Object>> CREDIT_CARDS = List.of(
            Map.of(
                    "creditCardAccountId", "cc-a1b2-0001-0001-0001-000000000001",
                    "brandName", "Banco A",
                    "name", "Banco A Platinum",
                    "productType", "PLATINUM",
                    "creditLimit", new BigDecimal("15000.00"),
                    "availableCreditLimit", new BigDecimal("8750.00")
            )
    );

    @GetMapping("/accounts")
    public Map<String, Object> getAccounts() {
        return Map.of("data", ACCOUNTS, "bank", "Bank A", "totalRecords", ACCOUNTS.size());
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

    @GetMapping("/credit-cards")
    public Map<String, Object> getCreditCards() {
        return Map.of("data", CREDIT_CARDS, "bank", "Bank A", "totalRecords", CREDIT_CARDS.size());
    }
}