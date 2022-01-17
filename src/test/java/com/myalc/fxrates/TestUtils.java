package com.myalc.fxrates;

import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myalc.fxrates.dto.api.CalculationRequest;
import com.myalc.fxrates.dto.api.CalculationResponse;
import com.myalc.fxrates.dto.external.exchangeratesapiio.ExchangeratesapiioLatestRates;
import com.myalc.fxrates.dto.external.fixerio.FixerioError;
import com.myalc.fxrates.dto.external.fixerio.FixerioLatestRates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;



@Component
public class TestUtils {

    private final Logger logger = LoggerFactory.getLogger(FunctionalTest.class);

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;

    public static final Long ts = 1642325375000L;   // millis
    public static final String date = "2022-01-16";
    
    public MockRestServiceServer createMockServer(boolean success, String provider) throws Exception {
        
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
        logger.info("createMockServer: success: {}, provider: {} --> {}", success, provider, server.toString());
        
        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 1.22334455);
        rates.put("GBP", 1.222233344);
        rates.put("JPY", 2.3333333);
        

        if (success) {
            if ("fixerio".equals(provider)) {

                FixerioLatestRates mockResponse = new FixerioLatestRates(true, ts, "USD", date, rates, null);
                logger.info("Expected mockResponse: {}", mockResponse.toString());

                server.expect(requestTo(matchesPattern(".*data.fixer.*")))
                    //.andExpect(method(HttpMethod.GET))
                    .andRespond(withSuccess(objectMapper.writeValueAsString(mockResponse), MediaType.APPLICATION_JSON));

            } else if ("exchangeratesapiio".equals(provider)) {
                
                ExchangeratesapiioLatestRates mockResponse = new ExchangeratesapiioLatestRates(true, ts, "USD", date, rates);
                logger.info("Expected mockResponse: {}", mockResponse.toString());

                server.expect(requestTo(matchesPattern(".*data.exchangeratesapiio.*")))
                    //.andExpect(method(HttpMethod.GET))
                    .andRespond(withSuccess(objectMapper.writeValueAsString(mockResponse), MediaType.APPLICATION_JSON));

            } else
                throw new Exception(String.format("provider: %s is not supported.", provider));
        
        } else {
            if ("fixerio".equals(provider)) {
        
                FixerioLatestRates mockResponse = new FixerioLatestRates();
                FixerioError error = new FixerioError(101, "invalid_access_key_test", "You have not supplied a valid API Access Key. [Technical Support: support@apilayer.com]");
                mockResponse.setSuccess(false);
                mockResponse.setError(error);
                logger.info("Expected mockResponse: {}", mockResponse.toString());

                server.expect(requestTo(matchesPattern(".*data.fixer.*")))
                    //.andExpect(method(HttpMethod.GET))
                    .andRespond(withSuccess(objectMapper.writeValueAsString(mockResponse), MediaType.APPLICATION_JSON));

            } else if ("exchangeratesapiio".equals(provider)) {
                
                ExchangeratesapiioLatestRates mockResponse = new ExchangeratesapiioLatestRates();
                mockResponse.setSuccess(false);
                logger.info("Expected mockResponse: {}", mockResponse.toString());

                server.expect(requestTo(matchesPattern(".*data.exchangeratesapiio.*")))
                    //.andExpect(method(acceptedMethod))
                    .andRespond(withSuccess(objectMapper.writeValueAsString(mockResponse), MediaType.APPLICATION_JSON));

            } else
                throw new Exception(String.format("provider: %s is not supported.", provider));
        }

        return server;
    }

    public void calculationApiTest(Double amount, String sourceCurrency, String targetCurrency, Double checkAmount, String serviceProvider, MockMvc mvc, MockRestServiceServer mockServer) throws Exception {
        
        MvcResult result = mvc.perform(post("/api/v1/investment/currency/calculation")
            .content(objectMapper.writeValueAsString(new CalculationRequest(amount, sourceCurrency, targetCurrency)))
            .contentType(MediaType.APPLICATION_JSON))
            //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andReturn();

        String jsonStr = result.getResponse().getContentAsString();
        CalculationResponse response = objectMapper.readValue(jsonStr, CalculationResponse.class);
        assertTrue(Math.abs(checkAmount - response.getTargetAmount()) > 0.00000000001 ? false: true);
    }

    public Double getCalculationFromList(List<CalculationResponse> list, String sourceCurrency, String targetCurrency) {
        return list.stream().filter(e -> e.getSourceCurrency().equals(sourceCurrency) && e.getTargetCurrency().equals(targetCurrency)).map(e -> e.getTargetAmount()).findAny().orElse(0.0);
    }

    public List<CacheManager> getCacheMgrs(BeanFactory beanFactory, List<String> names) {
        List<CacheManager> l = new ArrayList<>();
        names.stream().forEach(e -> {
            l.add(beanFactory.getBean(e, CacheManager.class));
        });
        logger.info("cacheManagers: {}", l.toString());
        return l;
    }

    public List<String> getCacheDetails(List<CacheManager> caches) {
        List<String> l = new ArrayList<>();
        for (CacheManager mgr: caches) {
            for (String name : mgr.getCacheNames()) {
                l.add(String.format("cacheName:%s, cache:%s, native:%s", name, mgr.getCache(name), mgr.getCache(name).getNativeCache().toString()));
            }
        }
        return l;
    }
    
    public void clearCaches(List<CacheManager> caches) {
        for (CacheManager mgr: caches) {
            for (String name : mgr.getCacheNames()) {
                logger.info("clearCache name: {}, cache: {}, native: {}", name, mgr.getCache(name), mgr.getCache(name).getNativeCache().toString());
                mgr.getCache(name).clear();
            }
        }
    }






}
