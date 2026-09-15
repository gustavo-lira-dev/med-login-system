package br.com.healthtech.medplatform.service;

import br.com.healthtech.medplatform.domain.Appointment;
import br.com.healthtech.medplatform.domain.User;
import br.com.healthtech.medplatform.dto.request.RequestAppointment;
import br.com.healthtech.medplatform.dto.response.AppointmentResponse;
import br.com.healthtech.medplatform.exception.throwables.ConflictException;
import br.com.healthtech.medplatform.exception.throwables.NotFoundException;
import br.com.healthtech.medplatform.repository.AppointmentRepository;
import br.com.healthtech.medplatform.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Appointment Service Unit Tests")
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @Nested
    @DisplayName("Register Appointment Flow")
    class RegisterAppointmentFlow {

        @Test
        @DisplayName("Should schedule an appointment successfully when date is available and users exist")
        void registerAppointment_Success() {
            // Arrange
            Long clientId = 1L;
            Long medicId = 2L;
            String medicEmail = "doctor.house@healthtech.com";
            LocalDateTime scheduledDate = LocalDateTime.now().plusDays(2);
            var request = new RequestAppointment(scheduledDate, medicId, clientId);

            var clientUser = User.builder().id(clientId).email("patient@test.com").build();
            var medicUser = User.builder().id(medicId).email(medicEmail).build();

            // Mocking business rule check (Both actors are free)
            when(appointmentRepository.existsByMedicIdAndScheduledDate(medicId, scheduledDate)).thenReturn(false);
            when(appointmentRepository.existsByClientIdAndScheduledDate(clientId, scheduledDate)).thenReturn(false);

            // Mocking database retrieval (Each user found ONCE)
            when(userRepository.findById(clientId)).thenReturn(Optional.of(clientUser));
            when(userRepository.findById(medicId)).thenReturn(Optional.of(medicUser));

            // Act
            AppointmentResponse response = appointmentService.registerAppointment(request);

            // Assert
            assertNotNull(response);
            assertEquals(scheduledDate, response.scheduledDate());
            assertEquals(medicEmail, medicUser.getEmail()); //

            // Verify database constraints and single I/O calls
            verify(userRepository, times(1)).findById(clientId);
            verify(userRepository, times(1)).findById(medicId);
            verify(appointmentRepository, times(1)).save(any(Appointment.class));
        }

        @Test
        @DisplayName("Should throw ConflictException and completely bypass user fetching when date is compromised")
        void registerAppointment_ThrowsConflict_WhenMedicOrClientIsBusy() {
            // Arrange
            Long clientId = 1L;
            Long medicId = 2L;
            LocalDateTime scheduledDate = LocalDateTime.now().plusDays(1);
            var request = new RequestAppointment(scheduledDate, medicId, clientId);

            // Mocking timetable collision
            when(appointmentRepository.existsByMedicIdAndScheduledDate(medicId, scheduledDate)).thenReturn(true);

            // Act & Assert
            assertThrows(ConflictException.class, () -> appointmentService.registerAppointment(request));

            // Core Quality Assurance: Ensure NO unnecessary user queries are executed if date is blocked
            verify(userRepository, never()).findById(anyLong());
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        @DisplayName("Should throw NotFoundException when client does not exist in database")
        void registerAppointment_ThrowsNotFound_WhenClientMissing() {
            // Arrange
            Long clientId = 99L;
            Long medicId = 2L;
            LocalDateTime scheduledDate = LocalDateTime.now().plusDays(1);
            var request = new RequestAppointment(scheduledDate, medicId, clientId);

            when(appointmentRepository.existsByMedicIdAndScheduledDate(medicId, scheduledDate)).thenReturn(false);
            when(appointmentRepository.existsByClientIdAndScheduledDate(clientId, scheduledDate)).thenReturn(false);

            // Client missing
            when(userRepository.findById(clientId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NotFoundException.class, () -> appointmentService.registerAppointment(request));

            // Medic should never be searched if Client step fails early
            verify(userRepository, never()).findById(medicId);
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }

        @Test
        @DisplayName("Should throw NotFoundException when medic does not exist in database")
        void registerAppointment_ThrowsNotFound_WhenMedicMissing() {
            // Arrange
            Long clientId = 1L;
            Long medicId = 88L;
            LocalDateTime scheduledDate = LocalDateTime.now().plusDays(1);
            var request = new RequestAppointment(scheduledDate, medicId, clientId);

            var clientUser = User.builder().id(clientId).email("patient@test.com").build();

            when(appointmentRepository.existsByMedicIdAndScheduledDate(medicId, scheduledDate)).thenReturn(false);
            when(appointmentRepository.existsByClientIdAndScheduledDate(clientId, scheduledDate)).thenReturn(false);

            // Client found, but Medic missing
            when(userRepository.findById(clientId)).thenReturn(Optional.of(clientUser));
            when(userRepository.findById(medicId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NotFoundException.class, () -> appointmentService.registerAppointment(request));

            verify(appointmentRepository, never()).save(any(Appointment.class));
        }
    }
}


