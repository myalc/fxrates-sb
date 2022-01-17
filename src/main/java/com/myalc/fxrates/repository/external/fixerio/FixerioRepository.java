package com.myalc.fxrates.repository.external.fixerio;

import java.util.HashMap;
import java.util.Map;

import com.myalc.fxrates.dto.external.fixerio.FixerioLatestRates;
import com.myalc.fxrates.exception.ServiceException;
import com.myalc.fxrates.model.LatestRates;
import com.myalc.fxrates.repository.external.IExternalRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service("fixerioRepository")
public class FixerioRepository implements IExternalRepository {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${fixerio.latestrate.url}")
    private String url;

    @Value("${fixerio.acces_key:na}")
    private String key;

    @Value("${serviceProvider:fixerio}")
    private String serviceProvider;
    
    private final Logger logger = LoggerFactory.getLogger(FixerioRepository.class);

    @Override
    public LatestRates getLatestExchangeRate(String sourceCurrency, String targetCurrency) {
        throw new ServiceException("Not implemented");
    }

    @Override
    @Cacheable(cacheNames = "FixerioService", cacheManager = "FixerIOMgr", keyGenerator = "keyGenerator")
    public LatestRates getLatestExchangeRate(String sourceCurrency) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("access_key", key);
        params.put("base", sourceCurrency);
        url = url + "?access_key={access_key}&base={base}";
        FixerioLatestRates result = restTemplate.getForObject(url, FixerioLatestRates.class, params);
        
        if (result.isSuccess()) {
            LatestRates r = new LatestRates(serviceProvider, result.getSuccess(), result.getTimestamp(), sourceCurrency, result.getDate(), result.getRates());
            logger.info("getLatestExchangeRate for {}: {}", sourceCurrency, r.toString());
            return r;

        } else {
            String errMsg = String.format("Cannot get latest rates from %s. Request: %s %s Error: %s", serviceProvider, url, params.toString(), result.getError().toString());
            logger.error(errMsg);
            throw new ServiceException(errMsg);
        }

    }   
}
