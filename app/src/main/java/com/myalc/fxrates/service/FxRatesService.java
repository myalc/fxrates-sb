package com.myalc.fxrates.service;

import javax.annotation.PostConstruct;

import com.myalc.fxrates.dto.api.PaginatedCalculationResponse;
import com.myalc.fxrates.dto.mapper.PaginatedCalculationMapper;
import com.myalc.fxrates.model.Calculation;
import com.myalc.fxrates.model.LatestRates;
import com.myalc.fxrates.model.Rate;
import com.myalc.fxrates.model.mapper.CalculationMapper;
import com.myalc.fxrates.model.mapper.RateMapper;
import com.myalc.fxrates.repository.db.CalculationRepository;
import com.myalc.fxrates.repository.db.LatestRatesRepository;
import com.myalc.fxrates.repository.db.RateRepository;
import com.myalc.fxrates.repository.external.exchangeratesapiio.ExchangeratesapiioRepository;
import com.myalc.fxrates.repository.external.fixerio.FixerioRepository;
import com.myalc.fxrates.repository.external.IExternalRepository;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FxRatesService implements IFxRatesService {

    @Autowired
    private CalculationRepository calculationRepository;

    @Autowired
    private RateRepository rateRepository;

    @Autowired
    private LatestRatesRepository latestRatesRepository;

    private IExternalRepository externalRepository;

    @Value("${serviceProvider:fixerio}")
    private String serviceProvider;

    private final Logger logger = LoggerFactory.getLogger(FxRatesService.class);

    private final BeanFactory beanFactory;

    @Autowired
    private RateMapper rateMapper;

    @Autowired
    private CalculationMapper calculationMapper;

    @Autowired
    private PaginatedCalculationMapper paginatedCalculationMapper;

    @Autowired    
    public FxRatesService(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @PostConstruct
    private void wire() {
        if ("exchangeratesapiio".equals(serviceProvider))
            externalRepository = this.beanFactory.getBean("exchangeratesapiioRepository", ExchangeratesapiioRepository.class);
        else
            externalRepository = this.beanFactory.getBean("fixerioRepository", FixerioRepository.class);
        logger.info("serviceProvider: {}, externalRepository: {}", serviceProvider, externalRepository.toString());
    }

    @Override
    public Rate getLatestExchangeRate(String sourceCurrency, String targetCurrency) {
        
        LatestRates latestRates = externalRepository.getLatestExchangeRate(sourceCurrency);
        latestRatesRepository.save(latestRates);

        Rate rate = rateMapper.mapToRate(latestRates, targetCurrency);
        rateRepository.save(rate);
        logger.info("getLatestExchangeRate --> sourceCurrency: {}, targetCurrency: {} -->  {}", rate.toString());

        return rate;
    }

    @Override
    public Calculation getCalculation(Double amount, String sourceCurrency, String targetCurrency) {
        
        Rate rate = this.getLatestExchangeRate(sourceCurrency, targetCurrency);
        Double targetAmount = this.calculate(amount, rate.getRate());

        Calculation calculation = calculationMapper.mapTCalculation(rate, amount, targetAmount);
        calculationRepository.save(calculation);
        logger.info("getCalculation --> amount: {}, sourceCurrency: {}, targetCurrency: {} --> {}", amount, sourceCurrency, targetCurrency, calculation.toString());
        
        return calculation;
    }

    @Override
    public PaginatedCalculationResponse getCalculations(String date, String transactionId, Integer page, Integer size) {
        
        Pageable paging = PageRequest.of(page, size);
        Page<Calculation> pages;
        
        if (StringUtils.isNotBlank(date))
            pages = calculationRepository.findBySqlDate(java.sql.Date.valueOf(date), paging);
        else if (StringUtils.isNotBlank(transactionId))
            pages = calculationRepository.findByTransactionId(java.util.UUID.fromString(transactionId), paging);
        else
            pages = calculationRepository.findAll(paging); // never works, both parameters cannot present

        PaginatedCalculationResponse response = paginatedCalculationMapper.mapToPaginatedCalculation(pages);
        logger.info("getCalculations --> parameter: {}, page: {}, size:{} --> {}", StringUtils.isNotBlank(date) ? date : transactionId, page, size, response.toShortString());
        
        return response;
    }
    
}
