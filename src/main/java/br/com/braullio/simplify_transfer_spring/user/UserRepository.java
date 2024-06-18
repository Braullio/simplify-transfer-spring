package br.com.braullio.simplify_transfer_spring.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long>{
	@Query("SELECT p FROM Payer p WHERE p.id = :id")
    Optional<Payer> findPayerById(@Param("id") Long id);

    @Query("SELECT p FROM Payee p WHERE p.id = :id")
    Optional<Payee> findPayeeById(@Param("id") Long id);
}
