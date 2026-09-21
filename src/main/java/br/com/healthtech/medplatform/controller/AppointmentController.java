package br.com.healthtech.medplatform.controller;

import br.com.healthtech.medplatform.domain.User;
import br.com.healthtech.medplatform.dto.request.RequestAppointment;
import br.com.healthtech.medplatform.dto.response.AppointmentResponse;
import br.com.healthtech.medplatform.service.serviceclass.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/register")
    public ResponseEntity<AppointmentResponse> schedule(
            @RequestBody @Valid RequestAppointment request,
            @AuthenticationPrincipal User loggedUser) {

        System.out.println("[API] Appointment registration requested by user: " + loggedUser.getEmail());
        System.out.println("[API] Authenticated User ID: " + loggedUser.getId());

        AppointmentResponse response = appointmentService.registerAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/edit")
    public ResponseEntity<AppointmentResponse> patchAppointment(
            @PathVariable Long id,
            @RequestBody @Valid RequestAppointment request,
            @AuthenticationPrincipal User loggedUser) {

        System.out.println("[API] Appointment edit requested by user: " + loggedUser.getEmail());

        AppointmentResponse response = appointmentService.editAppointment(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
