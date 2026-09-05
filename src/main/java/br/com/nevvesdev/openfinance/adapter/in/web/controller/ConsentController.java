package br.com.nevvesdev.openfinance.adapter.in.web.controller;

import br.com.nevvesdev.openfinance.adapter.in.web.dto.ConsentResponse;
import br.com.nevvesdev.openfinance.adapter.in.web.dto.CreateConsentRequest;
import br.com.nevvesdev.openfinance.adapter.in.web.dto.ExtendConsentRequest;
import br.com.nevvesdev.openfinance.adapter.in.web.dto.RejectConsentRequest;
import br.com.nevvesdev.openfinance.application.consent.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/consents")
public class ConsentController {

    private final CreateConsentUseCase createConsentUseCase;
    private final GetConsentUseCase getConsentUseCase;
    private final AuthoriseConsentUseCase authoriseConsentUseCase;
    private final RejectConsentUseCase rejectConsentUseCase;
    private final RevokeConsentUseCase revokeConsentUseCase;
    private final ExtendConsentUseCase extendConsentUseCase;

    public ConsentController(CreateConsentUseCase createConsentUseCase,
                             GetConsentUseCase getConsentUseCase,
                             AuthoriseConsentUseCase authoriseConsentUseCase,
                             RejectConsentUseCase rejectConsentUseCase,
                             RevokeConsentUseCase revokeConsentUseCase,
                             ExtendConsentUseCase extendConsentUseCase) {
        this.createConsentUseCase = createConsentUseCase;
        this.getConsentUseCase = getConsentUseCase;
        this.authoriseConsentUseCase = authoriseConsentUseCase;
        this.rejectConsentUseCase = rejectConsentUseCase;
        this.revokeConsentUseCase = revokeConsentUseCase;
        this.extendConsentUseCase = extendConsentUseCase;
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

    @PatchMapping("/{id}/authorise")
    public ConsentResponse authorise(@PathVariable UUID id) {
        return ConsentResponse.from(authoriseConsentUseCase.execute(id));
    }

    @PatchMapping("/{id}/reject")
    public ConsentResponse reject(@PathVariable UUID id,
                                  @Valid @RequestBody RejectConsentRequest request) {
        return ConsentResponse.from(rejectConsentUseCase.execute(id, request.reason()));
    }

    @DeleteMapping("/{id}")
    public ConsentResponse revoke(@PathVariable UUID id,
                                  @RequestParam(defaultValue = "Revoked by user") String reason) {
        return ConsentResponse.from(revokeConsentUseCase.execute(id, reason));
    }

    @PostMapping("/{id}/extends")
    public ConsentResponse extend(@PathVariable UUID id,
                                  @Valid @RequestBody ExtendConsentRequest request) {
        return ConsentResponse.from(extendConsentUseCase.execute(id, request.newExpirationDateTime()));
    }
}