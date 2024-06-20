package br.com.braullio.simplify_transfer_spring.transaction.response;

import br.com.braullio.simplify_transfer_spring.transaction.Transaction;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionResponse {
	private final Long payer;
	private final Long payee;
	private final BigDecimal value;
	private final String message;
	private final Long transaction_id;
	private final LocalDateTime created_at;

	public TransactionResponse(Transaction transaction) {
		this.message = "Transaction created successfully";
		this.transaction_id = transaction.getId();
		this.payer = transaction.getPayer().getId();
		this.payee = transaction.getPayee().getId();
		this.value = transaction.getAmount();
		this.created_at = transaction.getCreatedAt();
	}
}
