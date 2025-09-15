package com.patomicroservicios.order_service.contoller;

import com.patomicroservicios.order_service.dto.response.OrderGetDTO;
import com.patomicroservicios.order_service.service.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/order")
@RestController
public class OrderController {

    @Autowired
    IOrderService orderService;

    @Operation(
            summary = "Get Order by Id",
            description = "Returns a single order identified by its unique ID"
    )
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('SYSTEM')")
    public ResponseEntity<OrderGetDTO> getOrder(@PathVariable Long orderId,
                                                Authentication authentication) {
        // El JWT completo
        Jwt jwt = (Jwt) authentication.getPrincipal();

        // El "sub" del token (puede ser userId si es un usuario humano, o client_id si es un microservicio)
        String userIdOrClient = jwt.getSubject();

        // Verificamos si el rol es ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Verificamos si es SYSTEM (llamadas internas de otros microservicios)
        boolean isSystem = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SYSTEM"));

        OrderGetDTO order=orderService.getOrder(orderId);
        // Si no es admin ni system, solo el dueño puede acceder
        if (!isAdmin && !isSystem && !orderService.isClient(orderId, userIdOrClient)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @Operation(
            summary = "Cancel Order"
    )
    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<OrderGetDTO> cancelOrder(@PathVariable Long orderId,
                                                   Authentication authentication) {

        // El JWT completo
        Jwt jwt = (Jwt) authentication.getPrincipal();

        // El "sub" del token (puede ser userId si es un usuario humano, o client_id si es un microservicio)
        String userIdOrClient = jwt.getSubject();

        // Verificamos si el rol es ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        OrderGetDTO order=orderService.getOrder(orderId);
        // Si no es admin ni system, solo el dueño puede acceder
        if (!isAdmin && !orderService.isClient(orderId, userIdOrClient)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    @Operation(
            summary = "Create Order",
            description = "Creates a new order by cart id and returns the created resource."
    )
    @PostMapping("/{cartId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<OrderGetDTO> createOrder(@PathVariable Long cartId,
                                                   Authentication authentication) {
        // El JWT completo
        Jwt jwt = (Jwt) authentication.getPrincipal();

        // El "sub" del token (puede ser userId si es un usuario humano, o client_id si es un microservicio)
        String userIdOrClient = jwt.getSubject();

        // Verificamos si el rol es ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Si no es admin ni system, solo el dueño puede acceder
        if (!isAdmin && !orderService.isCartOwner(cartId, userIdOrClient)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(orderService.createOrder(cartId));
    }

}
