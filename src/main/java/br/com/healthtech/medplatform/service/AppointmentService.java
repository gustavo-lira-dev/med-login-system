package br.com.healthtech.medplatform.service;

import br.com.healthtech.medplatform.domain.Appointment;
import br.com.healthtech.medplatform.domain.enums.AppointmentStatus;
import br.com.healthtech.medplatform.dto.request.RequestAppointment;
import br.com.healthtech.medplatform.dto.response.AppointmentResponse;
import br.com.healthtech.medplatform.repository.AppointmentRepository;
import br.com.healthtech.medplatform.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    private final UserRepository userRepository;

    private Appointment create(RequestAppointment request) {
        return Appointment.builder()
                .status(AppointmentStatus.ON_ANALYSIS)
                .client(userRepository.findById(request.clientId()).get())
                .medic(userRepository.findById(request.medicId()).get())
                .madeDate(LocalDateTime.now())
                .scheduledDate(request.scheduledDate())
                .build();
    }
}
