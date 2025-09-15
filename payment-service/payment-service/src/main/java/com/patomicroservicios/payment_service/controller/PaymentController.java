package com.patomicroservicios.payment_service.controller;

import com.patomicroservicios.payment_service.dto.request.PaymentCreateDTO;
import com.patomicroservicios.payment_service.dto.response.PaymentDTO;
import com.patomicroservicios.payment_service.exceptions.PaymentNotFoundException;
import com.patomicroservicios.payment_service.service.IPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    IPaymentService paymentService;

    @Operation(
            summary = "Create a new payment",
            description = "Creates a new payment for a given order."
    )
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentDTO> createPayment(@RequestBody PaymentCreateDTO paymentCreateDTO,
                                                    Authentication authentication) {
        // El JWT completo
        Jwt jwt = (Jwt) authentication.getPrincipal();

        String userIdOrClient = jwt.getSubject();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !paymentService.isClient(paymentCreateDTO.getOrderId(), userIdOrClient)) {
            throw new AccessDeniedException("User is not the owner of this payment");
        }

        return ResponseEntity.ok(paymentService.newPayment(paymentCreateDTO));
    }

    @Operation(
            summary = "Get payment by order ID",
            description = "Retrieves the payment information associated with the specified order ID. Each order has at most one payment."
    )
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<PaymentDTO> getPaymentByOrderId(@PathVariable Long orderId,
                                                          Authentication authentication) {
        // El JWT completo
        Jwt jwt = (Jwt) authentication.getPrincipal();

        String userIdOrClient = jwt.getSubject();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !paymentService.isClient(orderId, userIdOrClient)) {
            throw new AccessDeniedException("User is not the owner of this payment");
        }
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }


    @Operation(
            summary = "Cancel a payment",
            description = "Cancels the payment associated with the given order ID. Completed payments cannot be canceled."
    )
    @PutMapping("/order/{orderId}/cancel")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PaymentDTO> cancelPayment(@PathVariable Long orderId,
                                                    Authentication authentication) {
        // El JWT completo
        Jwt jwt = (Jwt) authentication.getPrincipal();

        String userIdOrClient = jwt.getSubject();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !paymentService.isClient(orderId, userIdOrClient)) {
            throw new AccessDeniedException("User is not the owner of this invoice");
        }

        return ResponseEntity.ok(paymentService.cancelPayment(orderId));
    }
}
