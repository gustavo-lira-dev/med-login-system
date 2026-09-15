package br.com.healthtech.medplatform.repository;

import br.com.healthtech.medplatform.domain.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByMedicIdAndScheduledDate(Long medicId, LocalDateTime scheduledDate);
    boolean existsByClientIdAndScheduledDate(Long clientId, LocalDateTime scheduledDate);
}
