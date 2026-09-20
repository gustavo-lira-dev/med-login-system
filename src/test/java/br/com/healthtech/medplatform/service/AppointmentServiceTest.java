package br.com.healthtech.medplatform.service;

import br.com.healthtech.medplatform.domain.Appointment;
import br.com.healthtech.medplatform.domain.User;
import br.com.healthtech.medplatform.domain.enums.UserRole;
import br.com.healthtech.medplatform.dto.request.RequestAppointment;
import br.com.healthtech.medplatform.dto.response.AppointmentResponse;
import br.com.healthtech.medplatform.exception.throwables.BadRequestException;
import br.com.healthtech.medplatform.exception.throwables.ConflictException;
import br.com.healthtech.medplatform.exception.throwables.NotFoundException;
import br.com.healthtech.medplatform.repository.AppointmentRepository;
import br.com.healthtech.medplatform.repository.UserRepository;
import br.com.healthtech.medplatform.service.serviceclass.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Appointment Service Unit Tests")
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @Captor
    private ArgumentCaptor<Appointment> appointmentCaptor;

    private Long clientId;
    private Long medicId;
    private LocalDateTime scheduledDate;
    private RequestAppointment requestAppointment;
    private User client;
    private User medic;

    @BeforeEach
    void setUp() {
        clientId = 1L;
        medicId = 2L;
        scheduledDate = LocalDateTime.now().plusDays(1);

        requestAppointment = new RequestAppointment(scheduledDate, medicId, clientId);

        client = new User();
        client.setId(clientId);
        client.setRole(UserRole.CLIENT);

        medic = new User();
        medic.setId(medicId);
        medic.setRole(UserRole.MEDIC);
        medic.setEmail("doctor@hospital.com");
    }

    @Test
    void registerAppointmentShouldSaveAndReturnResponseWhenSuccessful() {
        when(userRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(userRepository.findById(medicId)).thenReturn(Optional.of(medic));
        when(appointmentRepository.existsByMedicIdAndScheduledDate(medicId, scheduledDate)).thenReturn(false);
        when(appointmentRepository.existsByClientIdAndScheduledDate(clientId, scheduledDate)).thenReturn(false);

        AppointmentResponse response = appointmentService.registerAppointment(requestAppointment);

        verify(appointmentRepository).save(appointmentCaptor.capture());
        Appointment savedAppointment = appointmentCaptor.getValue();

        assertThat(response).isNotNull();
        assertThat(response.scheduledDate()).isEqualTo(scheduledDate);
        assertThat(response.medicEmail()).isEqualTo("doctor@hospital.com");
        assertThat(savedAppointment.getClient()).isEqualTo(client);
        assertThat(savedAppointment.getMedic()).isEqualTo(medic);
    }

    @Test
    void registerAppointmentShouldThrowNotFoundExceptionWhenClientDoesNotExist() {
        when(userRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.registerAppointment(requestAppointment))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Client not found");

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void registerAppointmentShouldThrowBadRequestExceptionWhenRolesAreInvalid() {
        client.setRole(UserRole.MEDIC);
        when(userRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(userRepository.findById(medicId)).thenReturn(Optional.of(medic));

        assertThatThrownBy(() -> appointmentService.registerAppointment(requestAppointment))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Role fields are invalid");

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void registerAppointmentShouldThrowConflictExceptionWhenDateIsAlreadyCompromised() {
        when(userRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(userRepository.findById(medicId)).thenReturn(Optional.of(medic));
        when(appointmentRepository.existsByMedicIdAndScheduledDate(medicId, scheduledDate)).thenReturn(true);

        assertThatThrownBy(() -> appointmentService.registerAppointment(requestAppointment))
                .isInstanceOf(ConflictException.class)
                .hasMessage("This date and time are already compromised or invalid");

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void editAppointmentShouldUpdateAndReturnResponseWhenSuccessful() {
        Long appointmentId = 10L;
        Appointment existingAppointment = new Appointment();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.existsByMedicIdAndScheduledDate(medicId, scheduledDate)).thenReturn(false);
        when(appointmentRepository.existsByClientIdAndScheduledDate(clientId, scheduledDate)).thenReturn(false);
        when(userRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(userRepository.findById(medicId)).thenReturn(Optional.of(medic));

        AppointmentResponse response = appointmentService.editAppointment(appointmentId, requestAppointment);

        verify(appointmentRepository).save(existingAppointment);
        assertThat(response).isNotNull();
        assertThat(existingAppointment.getClient()).isEqualTo(client);
        assertThat(existingAppointment.getMedic()).isEqualTo(medic);
    }
}


