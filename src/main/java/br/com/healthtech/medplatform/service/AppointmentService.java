package br.com.healthtech.medplatform.service;

import br.com.healthtech.medplatform.domain.Appointment;
import br.com.healthtech.medplatform.domain.enums.AppointmentStatus;
import br.com.healthtech.medplatform.dto.request.RequestAppointment;
import br.com.healthtech.medplatform.dto.response.AppointmentResponse;
import br.com.healthtech.medplatform.exception.throwables.ConflictException;
import br.com.healthtech.medplatform.exception.throwables.NotFoundException;
import br.com.healthtech.medplatform.repository.AppointmentRepository;
import br.com.healthtech.medplatform.repository.UserRepository;
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

    public AppointmentResponse registerAppointment(RequestAppointment request) {
        if (!verifyIfDateIsPossible(request)) {
            throw new ConflictException("This date and time are already compromised or invalid");
        }
        Appointment newAppointment = create(request);
        return new AppointmentResponse(request.scheduledDate(), request.medicId().toString());
    }

    private Appointment create(RequestAppointment request) {
        return Appointment.builder()
                .status(AppointmentStatus.ON_ANALYSIS)
                .client(userRepository.findById(request.clientId()).orElseThrow(() -> new NotFoundException("Client not found")))
                .medic(userRepository.findById(request.medicId()).orElseThrow(() -> new NotFoundException("Medic not found")))
                .madeDate(LocalDateTime.now())
                .scheduledDate(request.scheduledDate())
                .build();
    }

    private boolean verifyIfDateIsPossible(RequestAppointment request) {
        return !appointmentRepository.existsByMedicIdAndScheduledDate(request.medicId(), request.scheduledDate())
                &&
                !appointmentRepository.existsByClientIdAndScheduledDate(request.clientId(), request.scheduledDate());

    }
}
