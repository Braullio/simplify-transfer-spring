package br.com.braullio.simplify_transfer_spring.transaction;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionDTO(
		@NotNull(message = "Value cannot be null") BigDecimal value,
		@NotNull(message = "Payer cannot be null") Long payer,
		@NotNull(message = "Payee cannot be null") Long payee) {
}
