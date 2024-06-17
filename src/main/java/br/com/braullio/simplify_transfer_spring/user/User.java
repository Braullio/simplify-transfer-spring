package br.com.braullio.simplify_transfer_spring.user;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="users")
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class User {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
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
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
