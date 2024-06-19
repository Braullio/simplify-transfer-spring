package br.com.braullio.simplify_transfer_spring.transaction;

import br.com.braullio.simplify_transfer_spring.user.Payer;
import br.com.braullio.simplify_transfer_spring.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name="transactions")
public class Transaction {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "payer_id")
	private Payer payer;

	@ManyToOne
	@JoinColumn(name = "payee_id")
	private User payee;

	@Column(name = "amount")
	private BigDecimal amount;

	@Column(name = "created_at")
	private final LocalDateTime createdAt = LocalDateTime.now();

	public Transaction(BigDecimal amount, Payer payer, User payee) throws Exception {
		this.payee = payee;
		this.payer = payer;
		this.amount = amount;

		this.validade();
	}

	private void validade() throws Exception {
		if (this.payer.getId().equals(this.payee.getId())) {
			throw new Exception("Transaction no permitid!");
		}

		if (this.payee == null || this.payer == null) {
			throw new Exception("Transaction payer or payee is null");
		}
	}
}
