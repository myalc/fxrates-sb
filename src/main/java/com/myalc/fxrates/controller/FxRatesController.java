package com.myalc.fxrates.controller;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.myalc.fxrates.dto.CustomAppError;
import com.myalc.fxrates.dto.api.CalculationRequest;
import com.myalc.fxrates.dto.api.CalculationResponse;
import com.myalc.fxrates.dto.api.PaginatedCalculationResponse;
import com.myalc.fxrates.dto.api.RateResponse;
import com.myalc.fxrates.exception.CustomException;
import com.myalc.fxrates.model.Calculation;
import com.myalc.fxrates.model.Rate;
import com.myalc.fxrates.service.FxRatesService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@Configuration
@Validated
@OpenAPIDefinition(info = @Info(title = "FX Rates API", version = "3.0", description = "Foreign exchange rates information"))
@ApiResponse(description = "Bad Request", responseCode = "400", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CustomAppError.class))} )
@ApiResponse(description = "Not Found", responseCode = "404", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CustomAppError.class))} )
@ApiResponse(description = "Method Not Allowed", responseCode = "405", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CustomAppError.class))} )
@ApiResponse(description = "Unsupported Media Type", responseCode = "415", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CustomAppError.class))} )
@ApiResponse(description = "Internal Server Error", responseCode = "500", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CustomAppError.class))} )
public class FxRatesController {

    @Autowired
    private FxRatesService fxRatesService;

    private final Logger logger = LoggerFactory.getLogger(FxRatesController.class);
    
    @GetMapping(path = "/api/v1/investment/currency/exchangerates/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(description = "OK", responseCode = "200", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RateResponse.class))} )
    @Cacheable(cacheNames = "FxRate", cacheManager = "FxRatesMgr", keyGenerator = "keyGenerator")
	public ResponseEntity<RateResponse> latestExchangeRate(@RequestParam @Size(min = 3, max=3) String sourceCurrency, @RequestParam @Size(min = 3, max=3) String targetCurrency) {
        
        Rate rate = fxRatesService.getLatestExchangeRate(sourceCurrency, targetCurrency);
        logger.info("Latest rate for {} --> {} is {}", sourceCurrency, targetCurrency, rate.getRate());
        
        return new ResponseEntity<>(new RateResponse(rate.getSourceCurrency(), rate.getTargetCurrency(), rate.getSqlTimestamp(), rate.getSqlDate(), rate.getRate()), HttpStatus.OK);
	}

    @PostMapping(path = "/api/v1/investment/currency/calculation", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(description = "OK", responseCode = "200", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CalculationResponse.class))} )
    @Cacheable(cacheNames = "FxCalculation", cacheManager = "FxRatesMgr", keyGenerator = "keyGenerator")
    public ResponseEntity<CalculationResponse> calculation(@Valid @RequestBody CalculationRequest request) {
    
        Calculation calculation = fxRatesService.getCalculation(request.getAmount(), request.getSourceCurrency(), request.getTargetCurrency());
        logger.info("calculation for {} {} --> {} is {}", request.getAmount(), request.getSourceCurrency(), request.getTargetCurrency(), calculation.getTargetAmount());
        return new ResponseEntity<>(new CalculationResponse(calculation.getTargetAmount(), calculation.getTransactionId().toString()), HttpStatus.OK);
    }


    @GetMapping(path = "/api/v1/investment/currency/calculations", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(description = "OK", responseCode = "200", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PaginatedCalculationResponse.class))} )
    @ApiResponse(description = "Conflict", responseCode = "409", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CustomAppError.class))} )
    @Cacheable(cacheNames = "PaginatedCalculations", cacheManager = "FxRatesMgr", keyGenerator = "keyGenerator")
    public ResponseEntity<PaginatedCalculationResponse> calculations(@RequestParam(required = false)
                                                  @Pattern(regexp = "^[0-9]{4}-((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01])|(0[469]|11)-(0[1-9]|[12][0-9]|30)|(02)-(0[1-9]|[12][0-9]))$", message = "Please provide a valid ISO date") String date, 
                                                  @RequestParam(required = false)
                                                  @Size(min = 10, max=50) String transactionId,
                                                  @RequestParam(defaultValue = "0") Integer page,
                                                  @RequestParam(defaultValue = "3") Integer size) {
        
        if ((StringUtils.isNotBlank(date) && StringUtils.isNotBlank(transactionId)) || (StringUtils.isBlank(date) && StringUtils.isBlank(transactionId))) {
            throw new CustomException(HttpStatus.CONFLICT, "Conflict", "One of the date or transactionId parameters must be given!");
        }

        PaginatedCalculationResponse paginated = fxRatesService.getCalculations(date, transactionId, page, size);
        logger.info("calculations for parameter: {}, page: {}, size:{} --> currentSize: {}, totalItems: {}, totalPages: {}, list: {}", StringUtils.isNotBlank(date) ? date : transactionId, page, size, paginated.getCalculations().size(), paginated.getTotalItems(), paginated.getTotalPages(), paginated.getCalculations());

        return new ResponseEntity<>(paginated, HttpStatus.OK);
    }
    
}
