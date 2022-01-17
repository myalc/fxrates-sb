package com.myalc.fxrates.repository.external.exchangeratesapiio;

import java.util.HashMap;
import java.util.Map;

import com.myalc.fxrates.dto.external.exchangeratesapiio.ExchangeratesapiioLatestRates;
import com.myalc.fxrates.model.LatestRates;
import com.myalc.fxrates.repository.external.IExternalRepository;

import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service("exchangeratesapiioRepository")
public class ExchangeratesapiioRepository implements IExternalRepository {

    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${serviceProvider:fixerio}")
    private String serviceProvider;

    @Value("${exchangeratesapiio.latestrate.url}")
    private String url;

    @Value("${exchangeratesapiio.acces_key:na}")
    private String key;
    
    private final Logger logger = LoggerFactory.getLogger(ExchangeratesapiioRepository.class);

    @Override
    public LatestRates getLatestExchangeRate(String sourceCurrency, String targetCurrency) {
        throw new ServiceException("Not implemented");
    }

    @Override
    @Cacheable(cacheNames = "ExchangeratesapiioService", cacheManager = "ExchangeratesapiIOMgr", keyGenerator = "keyGenerator")
    public LatestRates getLatestExchangeRate(String sourceCurrency) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("access_key", key);
        params.put("base", sourceCurrency);
        url = url + "?access_key={access_key}&base={base}";
        ExchangeratesapiioLatestRates result = restTemplate.getForObject(url, ExchangeratesapiioLatestRates.class, params);

        if (result.isSuccess()) {
            LatestRates r = new LatestRates(serviceProvider, result.getSuccess(), result.getTimestamp(), sourceCurrency, result.getDate(), result.getRates());
            logger.info("getLatestExchangeRate for {}: {}", sourceCurrency, r.toString());
            return r;

        } else {
            String errMsg = String.format("Cannot get latest rates from %s. Request: %s %s", serviceProvider, url, params.toString());
            logger.error(errMsg);
            throw new ServiceException(errMsg);
        }
    }
}
