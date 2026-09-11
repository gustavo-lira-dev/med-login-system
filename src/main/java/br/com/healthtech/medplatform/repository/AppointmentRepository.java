package br.com.healthtech.medplatform.repository;

import br.com.healthtech.medplatform.domain.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}
