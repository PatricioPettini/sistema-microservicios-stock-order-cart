package com.patomicroservicios.stock_service.service;

import com.patomicroservicios.stock_service.dto.request.ProductQuantityDTO;
import com.patomicroservicios.stock_service.dto.request.StockCreateDTO;
import com.patomicroservicios.stock_service.dto.response.StockGetDTO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface IStockService {
    StockGetDTO addStock(StockCreateDTO stock);
    StockGetDTO getStock(Long productId);
    List<StockGetDTO> getAllStock();
    StockGetDTO addReservedStock(ProductQuantityDTO productQuantity);
    List<StockGetDTO> subtractReservedStock(List<ProductQuantityDTO> dtoList);
    StockGetDTO addTotalStock(ProductQuantityDTO productQuantity);
    List<StockGetDTO> subtractTotalStock(@RequestBody List<ProductQuantityDTO> dtoList);
}

