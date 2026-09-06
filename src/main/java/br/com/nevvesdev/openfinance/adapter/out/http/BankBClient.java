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
public class BankBClient implements BankClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public BankBClient(RestTemplate restTemplate,
                       @Value("${banks.bank-b.base-url:https://localhost:8080}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public String getBankName() {
        return "Bank B";
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> fetchAccounts() {
        var response = restTemplate.exchange(
                baseUrl + "/mock/bank-b/accounts",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        var body = response.getBody();
        return body != null ? (List<Map<String, Object>>) body.get("data") : List.of();
    }

    @Override
    public List<Map<String, Object>> fetchCreditCards() {
        return List.of(); // Bank B não oferece cartões
    }
}