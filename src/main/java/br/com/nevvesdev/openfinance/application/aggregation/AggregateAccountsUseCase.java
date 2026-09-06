package br.com.nevvesdev.openfinance.application.aggregation;

import br.com.nevvesdev.openfinance.domain.consent.Consent;
import br.com.nevvesdev.openfinance.domain.consent.ConsentPermission;
import br.com.nevvesdev.openfinance.domain.consent.ConsentRepository;
import br.com.nevvesdev.openfinance.domain.consent.ConsentStatus;
import br.com.nevvesdev.openfinance.domain.consent.exception.ConsentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AggregateAccountsUseCase {

    private static final Logger log = LoggerFactory.getLogger(AggregateAccountsUseCase.class);

    private final ConsentRepository consentRepository;
    private final List<BankClient> bankClients;

    public AggregateAccountsUseCase(ConsentRepository consentRepository,
                                    List<BankClient> bankClients) {
        this.consentRepository = consentRepository;
        this.bankClients = bankClients;
    }

    public Map<String, Object> execute(UUID consentId) {
        var consent = consentRepository.findById(consentId)
                .orElseThrow(() -> new ConsentException("Consent not found: " + consentId));

        validateConsent(consent);

        var result = new HashMap<String, Object>();

        if (consent.getPermissions().contains(ConsentPermission.ACCOUNTS_READ)) {
            result.put("accounts", aggregateAccounts());
        }

        if (consent.getPermissions().contains(ConsentPermission.CREDIT_CARDS_ACCOUNTS_READ)) {
            result.put("creditCards", aggregateCreditCards());
        }

        result.put("consentId", consentId);
        result.put("status", consent.getStatus());
        return result;
    }

    private void validateConsent(Consent consent) {
        if (consent.getStatus() != ConsentStatus.AUTHORISED) {
            throw new ConsentException(
                    "Consent must be AUTHORISED to aggregate data. Current status: " + consent.getStatus()
            );
        }
        if (consent.isExpired()) {
            throw new ConsentException("Consent is expired");
        }
    }

    private List<Map<String, Object>> aggregateAccounts() {
        var accounts = new ArrayList<Map<String, Object>>();
        bankClients.forEach(client -> {
            try {
                var fetched = client.fetchAccounts();
                fetched.forEach(account -> {
                    var enriched = new HashMap<>(account);
                    enriched.put("source", client.getBankName());
                    accounts.add(enriched);
                });
            } catch (Exception e) {
                log.warn("Failed to fetch accounts from {}: {}", client.getBankName(), e.getMessage());
            }
        });
        return accounts;
    }

    private List<Map<String, Object>> aggregateCreditCards() {
        var cards = new ArrayList<Map<String, Object>>();
        bankClients.forEach(client -> {
            try {
                var fetched = client.fetchCreditCards();
                fetched.forEach(card -> {
                    var enriched = new HashMap<>(card);
                    enriched.put("source", client.getBankName());
                    cards.add(enriched);
                });
            } catch (Exception e) {
                log.warn("Failed to fetch credit cards from {}: {}", client.getBankName(), e.getMessage());
            }
        });
        return cards;
    }
}