package br.com.nevvesdev.openfinance.adapter.out.http;

import br.com.nevvesdev.openfinance.application.aggregation.BankClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class BankAClient implements BankClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public BankAClient(RestTemplate restTemplate,
                       @Value("${banks.bank-a.base-url:https://localhost:8080}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public String getBankName() {
        return "Bank A";
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> fetchAccounts() {
        var response = restTemplate.exchange(
                baseUrl + "/mock/bank-a/accounts",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        var body = response.getBody();
        return body != null ? (List<Map<String, Object>>) body.get("data") : List.of();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> fetchCreditCards() {
        var response = restTemplate.exchange(
                baseUrl + "/mock/bank-a/credit-cards",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        var body = response.getBody();
        return body != null ? (List<Map<String, Object>>) body.get("data") : List.of();
    }
}