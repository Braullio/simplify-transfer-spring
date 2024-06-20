package br.com.braullio.simplify_transfer_spring.api.notification.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class NotificationRequest {
	@JsonProperty("amount")
	private BigDecimal amount;

	@JsonProperty("payerId")
	private Long payer;

	@JsonProperty("payeeId")
	private Long payee;

	public NotificationRequest(BigDecimal amount, Long payerId, Long payeeId) {
		this.amount = amount;
		this.payer = payerId;
		this.payee = payeeId;
	}
}
