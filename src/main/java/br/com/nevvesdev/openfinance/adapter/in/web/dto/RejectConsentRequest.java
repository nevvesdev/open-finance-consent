package br.com.nevvesdev.openfinance.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record RejectConsentRequest(@NotBlank String reason) {}