package br.com.braullio.simplify_transfer_spring.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
	@Query("SELECT p FROM Payer p WHERE p.id = :id")
	Payer findPayerById(@Param("id") Long id);

    @Query("SELECT p FROM Payee p WHERE p.id = :id")
    User findPayeeById(@Param("id") Long id);
}
