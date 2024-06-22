package br.com.braullio.simplify_transfer_spring.user;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.braullio.simplify_transfer_spring.exception.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public abstract class User {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
    @Column(name = "user_type", insertable = false, updatable = false)
	private UserType userType;

	@Column(name = "full_name", nullable = false)
	private String fullName;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "tax_number", nullable = false, unique = true)
	private String taxNumber;

	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	@Column(name = "password_salt", nullable = false)
	private String passwordSalt;

	@Column(name = "balance")
	private BigDecimal balance;

    @Column(name = "created_at")
    private final LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

	protected User(String fullName, String email, String taxNumber, String passwordHash, String passwordSalt, BigDecimal balance, UserType userType) {
        this.fullName = fullName;
        this.email = email;
        this.taxNumber = taxNumber;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.balance = balance;
        this.userType = userType;
    }

	public void debit(BigDecimal amount) {
		if (balance.compareTo(amount) >= 0) {
			balance = balance.subtract(amount);
		} else {
			throw new BadRequestException("Insufficient balance in Payer");
		}
	}

	public void credit(BigDecimal amount) {
		balance = balance.add(amount);
	}
}
