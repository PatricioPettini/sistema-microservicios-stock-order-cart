package com.patomicroservicios.stock_service.service;

import com.patomicroservicios.stock_service.exceptions.ProductNotFoundException;
import com.patomicroservicios.stock_service.exceptions.StockNotRegisteredException;
import com.patomicroservicios.stock_service.exceptions.StockAlreadyRegisteredException;
import com.patomicroservicios.stock_service.exceptions.InsufficientStockException;
import com.patomicroservicios.stock_service.dto.request.ProductQuantityDTO;
import com.patomicroservicios.stock_service.dto.request.StockCreateDTO;
import com.patomicroservicios.stock_service.dto.response.ProductGetDTO;
import com.patomicroservicios.stock_service.dto.response.StockGetDTO;
import com.patomicroservicios.stock_service.model.Stock;
import com.patomicroservicios.stock_service.producer.NotificationProducer;
import com.patomicroservicios.stock_service.repository.IStockRepository;
import com.patomicroservicios.stock_service.repository.ProductAPI;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import java.util.ArrayList;
import java.util.List;

@Service
public class StockService implements IStockService{

    @Autowired
    IStockRepository stockRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ProductAPI productAPI;

    @Autowired
    NotificationProducer notificationProducer;

    //registrar stock de un producto
    // register stock for a product
    @Override
    public StockGetDTO addStock(StockCreateDTO st) {
        ProductGetDTO productGetDTO=productAPI.getProduct(st.getProductId());

        if(productGetDTO.isFallback()) throw new ProductNotFoundException(st.getProductId());

        //validate if product stock is registered
        if (stockRepository.findByProductId(st.getProductId()).isPresent()) {
            throw new StockAlreadyRegisteredException(st.getProductId());
        }

        Stock stock= mapToStock(st);

        return saveAndReturn(stock);
    }

    private static Stock mapToStock(StockCreateDTO st) {
        return Stock.builder()
                .productId(st.getProductId())
                .totalQuantity(st.getTotalQuantity())
                .build();
    }

    private StockGetDTO saveAndReturn(Stock stock) {
        return toDto(stockRepository.save(stock));
    }

    @Override
    @CircuitBreaker(name = "default", fallbackMethod = "fallbackStockNotFound")
    @Retry(name = "retryGetStock")
    public StockGetDTO getStock(Long productId) {
        Stock stock = getStockEntity(productId);
        return toDto(stock);
    }

    public StockGetDTO fallbackStockNotFound(Long productId, Throwable throwable) {

        return StockGetDTO.builder()
                .id(999L)
                .productId(productId)
                .totalQuantity(999)
                .reservedQuantity(999)
                .fallback(true)
                .build();
    }

    private Stock getStockEntity(Long productId) {
        return stockRepository.findByProductId(productId)
                .orElseThrow(()->new StockNotRegisteredException(productId));
    }

    public StockGetDTO toDto(Stock stock){
        return modelMapper.map(stock, StockGetDTO.class);
    }

    @Override
    public List<StockGetDTO> getAllStock() {
        return stockRepository.findAll().stream()
                .map(this::toDto)   // acá usamos ModelMapper
                .toList();
    }

    public StockGetDTO addReservedStock(ProductQuantityDTO dto) {
        System.out.println("id prod: " + dto.getProductId()); // o logger.info
        System.out.println("cantidad: " + dto.getQuantity()); // o logger.info
        validateQuantityIsPositive(dto.getQuantity());

        Stock stock = getStockEntity(dto.getProductId());
        validateSufficientAvailableStock(stock, dto.getQuantity());

        stock.setReservedQuantity(stock.getReservedQuantity() + dto.getQuantity());

        validateLowStockToNotify(stock);

        return saveAndReturn(stock);
    }

    private void validateQuantityIsPositive(int quantity) {
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be higher than 0");
    }

    private void validateSufficientAvailableStock(Stock stock, int requestedQuantity) {
        int available = stock.getTotalQuantity() - stock.getReservedQuantity();
        if (available < requestedQuantity)
            throw new InsufficientStockException(stock.getProductId());
    }

    @Override
    public List<StockGetDTO> subtractReservedStock(List<ProductQuantityDTO> dtoList) {

        List<StockGetDTO> stockList= new ArrayList<>();

        for (ProductQuantityDTO dto : dtoList) {
            int toSubtract = dto.getQuantity();
            validateQuantityIsPositive(toSubtract);
            Stock stock = getStockEntity(dto.getProductId());

            int reserved = stock.getReservedQuantity();

            if (reserved < toSubtract) {
                throw new IllegalArgumentException(
                        String.format("Product ID %d: quantity to subtract (%d) is higher than reserved stock (%d)",
                                dto.getProductId(), toSubtract, reserved)
                );
            }

            stock.setReservedQuantity(reserved - toSubtract);
            stockList.add(toDto(stockRepository.save(stock)));
        }

        return stockList;
    }

    @Override
    public StockGetDTO addTotalStock(ProductQuantityDTO productQuantity) {
        validateQuantityIsPositive(productQuantity.getQuantity());
        Stock stock=getStockEntity(productQuantity.getProductId());
        stock.setTotalQuantity(stock.getTotalQuantity()+ productQuantity.getQuantity());
        return saveAndReturn(stock);
    }

    @Override
    public List<StockGetDTO> subtractTotalStock(@RequestBody List<ProductQuantityDTO> dtoList) {
        List<StockGetDTO> stockList = new ArrayList<>();

        for (ProductQuantityDTO dto : dtoList) {
            Stock stock = getStockEntity(dto.getProductId());

            int quantity = dto.getQuantity();
            int total = stock.getTotalQuantity();
            int reserved = stock.getReservedQuantity();

            validateSubtractTotalStock(dto.getProductId(), quantity, total, reserved);

            stock.setTotalQuantity(total - quantity);
            stock.setReservedQuantity(reserved-quantity);
            validateLowStockToNotify(stock);

            stockList.add(toDto(stockRepository.save(stock)));
        }

        return stockList;
    }

    private void validateLowStockToNotify(Stock stock) {
        System.out.println("DEBUG - total: " + stock.getTotalQuantity());
        System.out.println("DEBUG - reserved: " + stock.getReservedQuantity());
        System.out.println("DEBUG - difference: " + (stock.getTotalQuantity() - stock.getReservedQuantity()));
        int quantity=stock.getTotalQuantity()- stock.getReservedQuantity();
        if(quantity<=10) {
            System.out.println("🔔 sending alert - low stock for product: " + stock.getProductId());
            notificationProducer.sendLowStockAlert(stock.getProductId(), quantity);
        }
    }

    private void validateSubtractTotalStock(Long productId, int quantity, int total, int reserved) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be higher than 0");
        }

        if (quantity > total) {
            throw new IllegalArgumentException(
                    String.format("Product ID %d: quantity to subtract (%d) is higher than total stock (%d)",
                            productId, quantity, total)
            );
        }
    }





}
