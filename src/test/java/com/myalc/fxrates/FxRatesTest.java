package com.myalc.fxrates;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.BeanFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.platform.commons.annotation.Testable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestGatewaySupport;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import net.minidev.json.writer.CollectionMapper.MapClass;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;


import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myalc.fxrates.dto.CustomAppError;
import com.myalc.fxrates.dto.api.CalculationRequest;
import com.myalc.fxrates.dto.api.CalculationResponse;
import com.myalc.fxrates.dto.api.PaginatedCalculationResponse;
import com.myalc.fxrates.dto.api.RateResponse;
import com.myalc.fxrates.dto.external.exchangeratesapiio.ExchangeratesapiioLatestRates;
import com.myalc.fxrates.dto.external.fixerio.FixerioError;
import com.myalc.fxrates.dto.external.fixerio.FixerioLatestRates;
import com.myalc.fxrates.model.LatestRates;
import com.myalc.fxrates.repository.db.LatestRatesRepository;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Import(value = {FunctionalTest.class, TestUtils.class})
@TestInstance(Lifecycle.PER_CLASS)
public class FxRatesTest {
    
    private final Logger logger = LoggerFactory.getLogger(FxRatesTest.class);
    
    @Autowired
    private MockMvc mvc;

    @Value("${serviceProvider:fixerio}")
    private String serviceProvider;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    TestUtils testUtils;

    @Autowired
    private LatestRatesRepository latestRatesRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final BeanFactory beanFactory;
    
    @Autowired    
    public FxRatesTest(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @BeforeAll
	void contextLoads() throws SQLException {
        Connection connection = DataSourceUtils.getConnection(jdbcTemplate.getDataSource());
        String dbProductName = connection.getMetaData().getDatabaseProductName();
        assertTrue("H2".equals(dbProductName));
	}


    /* API tests */



    /* Tests with mocked external repository */
   @Test
    public void latestExchangeratesApiWithSuccess() throws Exception {
        
        MockRestServiceServer mockServer = testUtils.createMockServer(true, serviceProvider);

        // target EUR
        mvc.perform(get("/api/v1/investment/currency/exchangerates/latest")
            .param("sourceCurrency", "USD").param("targetCurrency", "EUR")    
            .contentType(MediaType.APPLICATION_JSON))
            //.andDo(print())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sourceCurrency", is("USD")))
            .andExpect(jsonPath("$.targetCurrency", is("EUR")))
            .andExpect(jsonPath("$.timestamp", is(TestUtils.ts)))
            .andExpect(jsonPath("$.date", is(TestUtils.date)))
            .andExpect(jsonPath("$.rate", is(1.22334455)));

        // target GBP
        mvc.perform(get("/api/v1/investment/currency/exchangerates/latest")
            .param("sourceCurrency", "USD").param("targetCurrency", "GBP")        
            .contentType(MediaType.APPLICATION_JSON))
            //.andDo(print())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sourceCurrency", is("USD")))
            .andExpect(jsonPath("$.targetCurrency", is("GBP")))
            .andExpect(jsonPath("$.timestamp", is(TestUtils.ts)))
            .andExpect(jsonPath("$.date", is(TestUtils.date)))
            .andExpect(jsonPath("$.rate", is(1.222233344)));

        mockServer.reset();
    }

    @Test
    public void latestExchangeratesApiWithError() throws Exception {
        
        MockRestServiceServer mockServer = testUtils.createMockServer(false, serviceProvider);

        // target EUR
        MvcResult result = mvc.perform(get("/api/v1/investment/currency/exchangerates/latest")
            .param("sourceCurrency", "EUR").param("targetCurrency", "USD")
            .contentType(MediaType.APPLICATION_JSON))
            //.andDo(print())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.code", is("500")))
            .andExpect(jsonPath("$.message", is("Internal Server Error"))).andReturn();

        String jsonStr = result.getResponse().getContentAsString();
        CustomAppError error = objectMapper.readValue(jsonStr, CustomAppError.class);
        assertTrue(error.getErrors().stream().filter(e -> e.contains("invalid_access_key_test")).findAny().orElse("NA").equals("NA") ? false : true);

        mockServer.reset();
    }
    
    @Test
    public void calculationApiTest() throws Exception {
        MockRestServiceServer mockServer = testUtils.createMockServer(true, serviceProvider);
        testUtils.calculationApiTest(3.0, "USD", "EUR", 3.67003365, serviceProvider, mvc, mockServer);
        mockServer.reset();
    }

    @Test
    public void calculationsApiTest() throws Exception {
        
        latestRatesRepository.deleteAll();

        MockRestServiceServer mockServer = testUtils.createMockServer(true, serviceProvider);
        testUtils.calculationApiTest(3.0, "USD", "EUR", 3.67003365, serviceProvider, mvc, mockServer);
        testUtils.calculationApiTest(3.0, "USD", "GBP", 3.666700032, serviceProvider, mvc, mockServer);
        testUtils.calculationApiTest(3.0, "USD", "JPY", 6.9999999, serviceProvider, mvc, mockServer);

        MvcResult result = mvc.perform(get("/api/v1/investment/currency/calculations")
            .param("date", TestUtils.date)
            .contentType(MediaType.APPLICATION_JSON))
            //.andDo(print())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalItems", is(3)))
            .andExpect(jsonPath("$.currentPage", is(0)))
            .andReturn();
        
        String jsonStr = result.getResponse().getContentAsString();
        PaginatedCalculationResponse response = objectMapper.readValue(jsonStr, PaginatedCalculationResponse.class);
        assertTrue(Math.abs(3.67003365 - testUtils.getCalculationFromList(response.getCalculations(), "USD", "EUR") ) > 0.00000000001 ? false: true);
        assertTrue(Math.abs(3.666700032 - testUtils.getCalculationFromList(response.getCalculations(), "USD", "GBP") ) > 0.00000000001 ? false: true);
        assertTrue(Math.abs(6.9999999 - testUtils.getCalculationFromList(response.getCalculations(), "USD", "JPY") ) > 0.00000000001 ? false: true);
        
        mockServer.reset();
    }

    /* Caching tests */
    @Test
    public void latestExchangeratesApiCacheTest() throws Exception {
        
        List<String> mgrNames = Arrays.asList("FxRatesMgr", "FixerIOMgr", "ExchangeratesapiIOMgr");
        List<String> cacheNames = Arrays.asList("FxRate", "FixerioService", "ExchangeratesapiioService");
        List<String> details; 
        List<CacheManager> cacheMgrs = testUtils.getCacheMgrs(beanFactory, mgrNames);

        testUtils.clearCaches(cacheMgrs);
        latestExchangeratesApiWithSuccess();
        details = testUtils.getCacheDetails(cacheMgrs);
        //logger.warn("details1 ------------> {}", details.toString());

        LatestRates lr = cacheMgrs.get(1).getCache("FixerioService").get("FixerioRepository#getLatestExchangeRate#USD", LatestRates.class);
        logger.warn("LatestRates -----> {}", lr.toString());

        // TODO: implement




        testUtils.clearCaches(testUtils.getCacheMgrs(beanFactory, mgrNames));
        details = testUtils.getCacheDetails(cacheMgrs);
        //logger.warn("details2 ------------> {}", details.toString());

        assertTrue(true);
    }



    /* Functional tests */
    @Autowired
    FunctionalTest functionalTests;

    @Test
	void calculateTest1() {
		functionalTests.calculateTest1();
	}

    @Test
	void calculateTest2() {
		functionalTests.calculateTest2();
	}
}
