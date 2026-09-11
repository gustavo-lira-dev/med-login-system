package br.com.healthtech.medplatform.repository;

import br.com.healthtech.medplatform.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
