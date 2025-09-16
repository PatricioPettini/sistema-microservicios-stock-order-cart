package com.patomicroservicios.invoice_service.service;

import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.patomicroservicios.invoice_service.dto.InvoiceGetDTO;
import com.patomicroservicios.invoice_service.dto.OrderGetDTO;
import com.patomicroservicios.invoice_service.dto.ProductDTO;
import com.patomicroservicios.invoice_service.dto.UserDTO;
import com.patomicroservicios.invoice_service.exceptions.InvoiceAlreadyExistsException;
import com.patomicroservicios.invoice_service.exceptions.InvoiceNotFoundException;
import com.patomicroservicios.invoice_service.exceptions.OrderNotFoundException;
import com.patomicroservicios.invoice_service.model.Invoice;
import com.patomicroservicios.invoice_service.model.Product;
import com.patomicroservicios.invoice_service.repository.IInvoiceRepository;
import com.patomicroservicios.invoice_service.repository.OrderAPI;
import com.patomicroservicios.invoice_service.repository.UserAPI;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.ByteArrayOutputStream;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;


@Service
public class InvoiceService implements IInvoiceService{

    @Autowired
    OrderAPI orderAPI;

    @Autowired
    UserAPI userAPI;

    @Autowired
    IInvoiceRepository invoiceRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    InvoiceNumberGenerator invoiceNumberGenerator;

    @Override
    public void createInvoice(Long orderId) {
        OrderGetDTO order = getOrder(orderId);
        if (order.isFallback()) throw new OrderNotFoundException(orderId);

        if (invoiceRepository.findInvoiceByOrderId(orderId).isPresent()) throw new InvoiceAlreadyExistsException(orderId);

        Invoice invoice = Invoice.builder()
                .invoiceNumber(invoiceNumberGenerator.generate(orderId))
                .userId(order.getUserId())
                .subtotalPrice(order.getSubtotalPrice())
                .taxes(order.getTaxes())
                .totalPrice(order.getTotalPrice())
                .orderId(orderId)
                .build();

        List<Product> details = mapToProductEntityList(order.getItems(), invoice);
        invoice.setDetails(details);

        invoiceRepository.save(invoice); // cascade guarda invoice + details
    }

    private List<Product> mapToProductEntityList(List<ProductDTO> productList, Invoice invoice) {
        return productList.stream()
                .map(p -> {
                    Product detail = modelMapper.map(p, Product.class);
                    detail.setInvoice(invoice);
                    return detail;
                })
                .toList();
    }

    private List<ProductDTO> mapToProductDTOList(List<Product> productList){
        return productList.stream()
                .map(p->modelMapper.map(p,ProductDTO.class))
                .toList();
    }

    @Override
    public InvoiceGetDTO getInvoiceByOrderId(Long orderId) {
        return toInvoiceDTO(invoiceRepository.findInvoiceByOrderId(orderId)
                .orElseThrow(()-> new InvoiceNotFoundException(orderId)));
    }

    private InvoiceGetDTO toInvoiceDTO(Invoice invoice){
        InvoiceGetDTO invoiceGetDTO=modelMapper.map(invoice,InvoiceGetDTO.class);
        invoiceGetDTO.setDetails(mapToProductDTOList(invoice.getDetails()));

        return invoiceGetDTO;
    }

    private UserDTO getUser(String userId){
        return userAPI.getUser(userId);
    }

    public byte[] generateInvoicePdf(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        UserDTO user = getUser(invoice.getUserId());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // LOGO (opcional: usa una ruta o URL a la imagen)
        try {
            Image logo = new Image(ImageDataFactory.create("src/main/resources/static/logo.png"));
            logo.setWidth(100);
            document.add(logo);
        } catch (Exception e) {
            document.add(new Paragraph("Mi Empresa S.A.").setBold().setFontSize(16));
        }

        // CABECERA
        document.add(new Paragraph("FACTURA")
                .setBold()
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("Número: " + invoice.getInvoiceNumber()).setFontSize(12));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        document.add(new Paragraph("Fecha: " + invoice.getCreatedAt().format(formatter)).setFontSize(12));

        // ✅ Datos del cliente
        document.add(new Paragraph("Cliente: " + user.getFirstName() + " " + user.getLastName()).setFontSize(12));
        document.add(new Paragraph("DNI: " + user.getDni()).setFontSize(12));
        document.add(new Paragraph("Email: " + user.getEmail()).setFontSize(12));
        document.add(new Paragraph("\n"));

        // TABLA DE PRODUCTOS
        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2, 1, 2, 2}))
                .useAllAvailableWidth();

// Encabezados con fondo gris
        table.addHeaderCell(new Cell().add(new Paragraph("Producto")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Marca")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Cantidad")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Precio Unitario")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Subtotal")).setBackgroundColor(ColorConstants.LIGHT_GRAY));

        for (Product p : invoice.getDetails()) {
            // Producto
            table.addCell(new Cell().add(new Paragraph(p.getName())));

            // Marca (null-safe)
            table.addCell(new Cell().add(new Paragraph(
                    p.getBrand() != null ? p.getBrand() : "N/A"
            )));

            // Cantidad
            table.addCell(new Cell().add(new Paragraph(String.valueOf(p.getQuantity()))));

            // Precio unitario
            table.addCell(new Cell().add(new Paragraph(p.getUnitPrice().toString())));

            // Subtotal
            table.addCell(new Cell().add(new Paragraph(p.getSubtotalPrice().toString())));
        }

        document.add(table);


        // ESPACIO ANTES DE TOTALES
        document.add(new Paragraph("\n"));

        // SUBTOTAL, IMPUESTOS, TOTAL
        Table totals = new Table(UnitValue.createPercentArray(new float[]{3, 1}))
                .setHorizontalAlignment(HorizontalAlignment.RIGHT);

        // Subtotal
        totals.addCell(new Cell().add(new Paragraph("Subtotal:")).setBorder(Border.NO_BORDER));
        totals.addCell(
                new Cell()
                        .add(new Paragraph(
                                invoice.getDetails().stream()
                                        .map(Product::getSubtotalPrice)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                                        .toString()
                        ))
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setBorder(Border.NO_BORDER)
        );

        // Impuestos
        totals.addCell(new Cell().add(new Paragraph("Impuestos:")).setBorder(Border.NO_BORDER));
        totals.addCell(
                new Cell()
                        .add(new Paragraph(invoice.getTaxes().toString()))
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setBorder(Border.NO_BORDER)
        );

        // Total
        totals.addCell(new Cell().add(new Paragraph("TOTAL:")).setBold().setFontSize(12).setBorder(Border.NO_BORDER));
        totals.addCell(
                new Cell()
                        .add(new Paragraph(invoice.getTotalPrice().toString()))
                        .setBold().setFontSize(12)
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setBorder(Border.NO_BORDER)
        );

        document.add(totals);

        // PIE DE PÁGINA
        document.add(new Paragraph("\n\nGracias por su compra.")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10)
                .setFontColor(ColorConstants.GRAY));

        document.close();

        return baos.toByteArray();
    }

    @Override
    public boolean isOwnerOfOrder(Long orderId, String userId) {
        return getOrder(orderId).getUserId().equals(userId);
    }

    private OrderGetDTO getOrder(Long orderId){
        return orderAPI.getOrder(orderId);
    }

    @Override
    public Long getOrderByInvoiceId(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"))
                .getOrderId();
    }
}