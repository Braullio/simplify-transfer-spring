package br.com.braullio.simplify_transfer_spring.user;

import java.math.BigDecimal;

import jakarta.persistence.Entity;

@Entity
public class Payee extends User {
    public Payee(
		String fullName,
		String email,
		String taxNumber,
		String passwordHash,
		String passwordSalt,
		BigDecimal balance) {
			super.setUserType(UserType.LOJISTA);
			super.setFullName(fullName);
			super.setEmail(email);
			super.setTaxNumber(taxNumber);
			super.setPasswordHash(passwordHash);
			super.setPasswordSalt(passwordSalt);
			super.setBalance(balance);
	}
}