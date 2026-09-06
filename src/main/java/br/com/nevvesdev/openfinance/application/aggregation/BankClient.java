package br.com.nevvesdev.openfinance.application.aggregation;

import java.util.List;
import java.util.Map;

public interface BankClient {
    String getBankName();
    List<Map<String, Object>> fetchAccounts();
    List<Map<String, Object>> fetchCreditCards();
}