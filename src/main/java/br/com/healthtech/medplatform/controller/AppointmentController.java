package br.com.healthtech.medplatform.controller;

import br.com.healthtech.medplatform.dto.request.RequestAppointment;
import br.com.healthtech.medplatform.dto.response.AppointmentResponse;
import br.com.healthtech.medplatform.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;


    @PostMapping("/register")
    public ResponseEntity<AppointmentResponse> schedule(@RequestBody @Valid RequestAppointment request) {
            AppointmentResponse response = appointmentService.registerAppointment(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
