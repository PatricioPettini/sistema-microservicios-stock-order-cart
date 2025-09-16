package com.patomicroservicios.stock_service.controller;

import com.patomicroservicios.stock_service.dto.request.ProductQuantityDTO;
import com.patomicroservicios.stock_service.dto.request.StockCreateDTO;
import com.patomicroservicios.stock_service.dto.response.StockGetDTO;
import com.patomicroservicios.stock_service.service.IStockService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    @Autowired
    IStockService stockService;

    @Operation(
            summary = "Register Stock",
            description = "This endpoint allows registering the stock of a product for the first time"
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StockGetDTO> addStock(@Valid @RequestBody StockCreateDTO stock) {
        StockGetDTO created = stockService.addStock(stock);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Get All Stock",
            description = "This endpoint allows retrieving the stock of all registered products"
    )
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StockGetDTO>> getStockList() {
        List<StockGetDTO> stockList = stockService.getAllStock();
        return ResponseEntity.ok(stockList);
    }

    @Operation(
            summary = "Get Stock by Product",
            description = "This endpoint allows retrieving the stock of a specific product"
    )
    @GetMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StockGetDTO> getStock(@PathVariable Long productId) {
        StockGetDTO stock = stockService.getStock(productId);
        return ResponseEntity.ok(stock);
    }

    // Aumentar stock total
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    @PutMapping("/total/add")
    public ResponseEntity<StockGetDTO> addTotalStock(@RequestBody ProductQuantityDTO productQuantity){
        StockGetDTO stock = stockService.addTotalStock(productQuantity);
        return ResponseEntity.ok(stock);
    }

    // Disminuir stock total
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    @PutMapping("/total/subtract")
    public ResponseEntity<List<StockGetDTO>> subtractTotalStock(@RequestBody List<ProductQuantityDTO> dtoList){
        List<StockGetDTO> stock = stockService.subtractTotalStock(dtoList);
        return ResponseEntity.ok(stock);
    }

    // Aumentar stock reservado
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    @PutMapping("/reserved/add")
    public ResponseEntity<StockGetDTO> addReservedStock(@RequestBody ProductQuantityDTO productQuantity){
        StockGetDTO stock = stockService.addReservedStock(productQuantity);
        return ResponseEntity.ok(stock);
    }

    // Disminuir stock reservado
    @PutMapping("/reserved/subtract")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    public ResponseEntity<List<StockGetDTO>> subtractReservedStock(@RequestBody List<ProductQuantityDTO> dtoList){
        System.out.println("entro al endpoint");
        List<StockGetDTO> stock = stockService.subtractReservedStock(dtoList);
        return ResponseEntity.ok(stock);
    }



}
