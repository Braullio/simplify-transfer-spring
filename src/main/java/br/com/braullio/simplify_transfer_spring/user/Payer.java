package br.com.braullio.simplify_transfer_spring.user;

import java.math.BigDecimal;

import jakarta.persistence.Entity;

@Entity
public class Payer extends User {
	public Payer(
		String fullName,
		String email,
		String taxNumber,
		String passwordHash,
		String passwordSalt,
		BigDecimal balance) {
			super.setUserType(UserType.COMUM);
			super.setFullName(fullName);
			super.setEmail(email);
			super.setTaxNumber(taxNumber);
			super.setPasswordHash(passwordHash);
			super.setPasswordSalt(passwordSalt);
			super.setBalance(balance);
	}
}