package br.com.healthtech.medplatform.service.serviceclass;

import br.com.healthtech.medplatform.domain.Appointment;
import br.com.healthtech.medplatform.domain.enums.AppointmentStatus;
import br.com.healthtech.medplatform.domain.enums.UserRole;
import br.com.healthtech.medplatform.dto.request.RequestAppointment;
import br.com.healthtech.medplatform.dto.response.AppointmentResponse;
import br.com.healthtech.medplatform.exception.throwables.BadRequestException;
import br.com.healthtech.medplatform.exception.throwables.ConflictException;
import br.com.healthtech.medplatform.exception.throwables.NotFoundException;
import br.com.healthtech.medplatform.repository.AppointmentRepository;
import br.com.healthtech.medplatform.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    @Transactional(rollbackOn = Exception.class)
    public AppointmentResponse registerAppointment(RequestAppointment request) {
        var client = userRepository.findById(request.clientId())
                .orElseThrow(() -> new NotFoundException("Client not found"));
        var medic = userRepository.findById(request.medicId())
                .orElseThrow(() -> new NotFoundException("Medic not found"));

        if (client.getRole() != UserRole.CLIENT || medic.getRole() != UserRole.MEDIC) {
            throw new BadRequestException("Role fields are invalid");
        }

        if (verifyIfDateIsPossible(request)) {
            throw new ConflictException("This date and time are already compromised or invalid");
        }

        Appointment newAppointment = Appointment.builder()
                .status(AppointmentStatus.ON_ANALYSIS)
                .client(client)
                .medic(medic)
                .madeDate(LocalDateTime.now())
                .scheduledDate(request.scheduledDate())
                .build();

        appointmentRepository.save(newAppointment);

        return new AppointmentResponse(
                "Appointment successfully scheduled at given date and time; send an email to your doctor!",
                request.scheduledDate(),
                medic.getEmail()
        );
    }

    @Transactional(rollbackOn = Exception.class)
    public AppointmentResponse editAppointment(Long id, RequestAppointment request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));

        if (verifyIfDateIsPossible(request)) {
            throw new ConflictException("This date and time are already compromised or invalid");
        }

        var client = userRepository.findById(request.clientId())
                .orElseThrow(() -> new NotFoundException("Client not found"));
        var medic = userRepository.findById(request.medicId())
                .orElseThrow(() -> new NotFoundException("Medic not found"));

        if (client.getRole() != UserRole.CLIENT || medic.getRole() != UserRole.MEDIC) {
            throw new BadRequestException("Role fields are invalid");
        }

        appointment.setStatus(AppointmentStatus.ON_ANALYSIS);
        appointment.setScheduledDate(request.scheduledDate());
        appointment.setClient(client);
        appointment.setMedic(medic);

        appointmentRepository.save(appointment);

        return new AppointmentResponse(
                "Appointment successfully updated; send an email to your medic!",
                request.scheduledDate(),
                medic.getEmail()
        );
    }

    private boolean verifyIfDateIsPossible(RequestAppointment request) {
        return appointmentRepository.existsByMedicIdAndScheduledDate(request.medicId(), request.scheduledDate())
                ||
                appointmentRepository.existsByClientIdAndScheduledDate(request.clientId(), request.scheduledDate());
    }
}
