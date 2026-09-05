package br.com.nevvesdev.openfinance.adapter.in.web.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record ExtendConsentRequest(@NotNull @Future OffsetDateTime newExpirationDateTime) {}