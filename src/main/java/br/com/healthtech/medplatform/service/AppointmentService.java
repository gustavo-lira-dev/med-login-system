package br.com.healthtech.medplatform.service;

import br.com.healthtech.medplatform.domain.Appointment;
import br.com.healthtech.medplatform.domain.enums.AppointmentStatus;
import br.com.healthtech.medplatform.dto.request.RequestAppointment;
import br.com.healthtech.medplatform.dto.response.AppointmentResponse;
import br.com.healthtech.medplatform.exception.throwables.ConflictException;
import br.com.healthtech.medplatform.exception.throwables.NotFoundException;
import br.com.healthtech.medplatform.repository.AppointmentRepository;
import br.com.healthtech.medplatform.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
// @AllArgsConstructor(onConstructor = @__(@Autowired))
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    private final UserRepository userRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository, UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    @Transactional(rollbackOn =  Exception.class)
    public AppointmentResponse registerAppointment(RequestAppointment request) {
        if (!verifyIfDateIsPossible(request)) {
            throw new ConflictException("This date and time are already compromised or invalid");
        }

        var client = userRepository.findById(request.clientId())
                .orElseThrow(() -> new NotFoundException("Client not found"));
        var medic = userRepository.findById(request.medicId())
                .orElseThrow(() -> new NotFoundException("Medic not found"));

        Appointment newAppointment = Appointment.builder()
                .status(AppointmentStatus.ON_ANALYSIS)
                .client(client)
                .medic(medic)
                .madeDate(LocalDateTime.now())
                .scheduledDate(request.scheduledDate())
                .build();

        appointmentRepository.save(newAppointment);

        return new AppointmentResponse(request.scheduledDate(), medic.getEmail());
    }

    private boolean verifyIfDateIsPossible(RequestAppointment request) {
        return !appointmentRepository.existsByMedicIdAndScheduledDate(request.medicId(), request.scheduledDate())
                &&
                !appointmentRepository.existsByClientIdAndScheduledDate(request.clientId(), request.scheduledDate());

    }
}
