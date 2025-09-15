package com.patomicroservicios.invoice_service.controller;

import com.patomicroservicios.invoice_service.dto.InvoiceGetDTO;
import com.patomicroservicios.invoice_service.service.IInvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

    @Autowired
    IInvoiceService invoiceService;

    @Operation(
        summary = "Get invoice by order ID",
        description = "Retrieve the invoice details associated with a specific order ID. "
                + "If no invoice exists for the given order, an error will be returned.")
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<InvoiceGetDTO> getInvoiceByOrderId(@PathVariable Long orderId,
                                                             Authentication authentication) {

        // El JWT completo
        Jwt jwt = (Jwt) authentication.getPrincipal();

        // El "sub" del token (puede ser userId si es un usuario humano, o client_id si es un microservicio)
        String userIdOrClient = jwt.getSubject();

        // Verificamos si el rol es ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Si no es admin ni system, solo el dueño puede acceder
        if (!isAdmin && !invoiceService.isOwnerOfOrder(orderId, userIdOrClient)) {
            throw new AccessDeniedException("User is not the owner of this invoice");
        }

        InvoiceGetDTO invoice = invoiceService.getInvoiceByOrderId(orderId);
        return ResponseEntity.ok(invoice);
    }

//    @GetMapping("/{invoiceId}/pdf")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
//    public ResponseEntity<byte[]> getInvoicePdf(@PathVariable Long invoiceId,
//                                                Authentication authentication) {
//        // El JWT completo
//        Jwt jwt = (Jwt) authentication.getPrincipal();
//
//        String userIdOrClient = jwt.getSubject();
//
//        boolean isAdmin = authentication.getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
//
//        if (!isAdmin && !invoiceService.isClient(invoiceId, userIdOrClient)) {
//            throw new AccessDeniedException("User is not the owner of this invoice");
//        }
//
//        byte[] pdfBytes = invoiceService.generateInvoicePdf(invoiceId);
//
//        return ResponseEntity.ok()
//                .header("Content-Disposition", "attachment; filename=factura-" + invoiceId + ".pdf")
//                .contentType(MediaType.APPLICATION_PDF)
//                .body(pdfBytes);
//    }

    @GetMapping("/{invoiceId}/pdf")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<byte[]> getInvoicePdf(@PathVariable Long invoiceId,
                                                Authentication authentication) {

        // El JWT completo
        Jwt jwt = (Jwt) authentication.getPrincipal();

        // El "sub" del token (puede ser userId si es un usuario humano, o client_id si es un microservicio)
        String userIdOrClient = jwt.getSubject();

        // Verificamos si el rol es ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Si no es admin ni system, solo el dueño puede acceder
        if (!isAdmin) {
            Long orderId = invoiceService.getOrderByInvoiceId(invoiceId);
            if (!invoiceService.isOwnerOfOrder(orderId, userIdOrClient)) {
                throw new AccessDeniedException("User is not the owner of this invoice");
            }
        }
            byte[] pdfBytes = invoiceService.generateInvoicePdf(invoiceId);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=factura-" + invoiceId + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        }

}
