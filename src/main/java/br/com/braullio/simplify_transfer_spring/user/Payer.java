package br.com.braullio.simplify_transfer_spring.user;

import java.math.BigDecimal;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@DiscriminatorValue("COMUM")
public class Payer extends User {
    public Payer(String fullName, String email, String taxNumber, String passwordHash, String passwordSalt, BigDecimal balance) {
        super(fullName, email, taxNumber, passwordHash, passwordSalt, balance, UserType.COMUM);
    }
}