package br.com.nevvesdev.openfinance.adapter.in.web.controller;

import br.com.nevvesdev.openfinance.adapter.in.web.dto.ConsentResponse;
import br.com.nevvesdev.openfinance.adapter.in.web.dto.CreateConsentRequest;
import br.com.nevvesdev.openfinance.application.consent.CreateConsentUseCase;
import br.com.nevvesdev.openfinance.application.consent.GetConsentUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/consents")
public class ConsentController {

    private final CreateConsentUseCase createConsentUseCase;
    private final GetConsentUseCase getConsentUseCase;

    public ConsentController(CreateConsentUseCase createConsentUseCase,
                             GetConsentUseCase getConsentUseCase) {
        this.createConsentUseCase = createConsentUseCase;
        this.getConsentUseCase = getConsentUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConsentResponse create(@Valid @RequestBody CreateConsentRequest request) {
        var consent = createConsentUseCase.execute(
                request.loggedUserCpf(),
                request.businessEntityCnpj(),
                request.permissions(),
                request.expirationDateTime()
        );
        return ConsentResponse.from(consent);
    }

    @GetMapping("/{id}")
    public ConsentResponse findById(@PathVariable UUID id) {
        return ConsentResponse.from(getConsentUseCase.execute(id));
    }
}