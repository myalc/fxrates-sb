package com.myalc.fxrates;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myalc.fxrates.dto.CustomAppError;
import com.myalc.fxrates.dto.api.CalculationRequest;
import com.myalc.fxrates.dto.api.CalculationResponse;
import com.myalc.fxrates.dto.api.PaginatedCalculationResponse;
import com.myalc.fxrates.dto.api.RateResponse;
import com.myalc.fxrates.model.LatestRates;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


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
    @Test
    public void openapiJsonTest() throws Exception {
        MvcResult result =  mvc.perform(get("/api-docs"))
            //.andDo(print())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andReturn();
        String jsonStr = result.getResponse().getContentAsString();
        assertTrue(jsonStr.length() > 250);
    }

    @Test
    public void notFoundTest() throws Exception {
        mvc.perform(get("/api/v1/investment"))
            //.andDo(print())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code", is("404")))
            .andExpect(jsonPath("$.message", is("Not Found")));
    }

    @Test
    public void notAllowedTest() throws Exception {
        mvc.perform(get("/api/v1/investment/currency/calculation"))
            //.andDo(print())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(status().isMethodNotAllowed())
            .andExpect(jsonPath("$.code", is("405")))
            .andExpect(jsonPath("$.message", is("Method Not Allowed")));
    }

    @Test
    public void mediaTypeTest() throws Exception {
        mvc.perform(post("/api/v1/investment/currency/calculation").contentType(MediaType.TEXT_PLAIN).content(objectMapper.writeValueAsString(new CalculationRequest(4.5, "USD", "EUR"))))
            //.andDo(print())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnsupportedMediaType())
            .andExpect(jsonPath("$.code", is("415")))
            .andExpect(jsonPath("$.message", is("Unsupported Media Type")));
    }

    @Test
    public void badRequestTest() throws Exception {
        mvc.perform(get("/api/v1/investment/currency/exchangerates/latest"))
            //.andDo(print())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code", is("400")))
            .andExpect(jsonPath("$.message", is("Bad Request")));
    }

    @Test
    public void conflictTest() throws Exception {
        mvc.perform(get("/api/v1/investment/currency/calculations")
            .param("date", TestUtils.date)
            .param("transactionId", "f85511ec-bad9-44fe-bff0-5ab0f8745ff1")
            .contentType(MediaType.APPLICATION_JSON))
            //.andDo(print())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code", is("409")))
            .andExpect(jsonPath("$.message", is("Conflict")));
    }


    /* Tests with mocked external repository */

    @Test
    public void latestExchangeratesApiWithSuccessTest() throws Exception {
        
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
    public void latestExchangeratesApiWithErrorTest() throws Exception {
        
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

    // Cache info, check cache.configs expiration values, assuming FxRatesMgr:2, FixerIOMgr:4, ExchangeratesapiIOMgr:4
    // mgr = FixerIOMgr, name = FixerioService, key = FixerioRepository#getLatestExchangeRate#USD --> LatestRates
    // mgr = FxRatesMgr, name = FxRate, key = FxRatesController#latestExchangeRate#USD#EUR --> ResponseEntity<RateResponse>
    // mgr = FxRatesMgr, name = FxRate, key = FxRatesController#latestExchangeRate#USD#GBP --> ResponseEntity<RateResponse>
    // mgr = FxRatesMgr, name = FxRate, key = FxRatesController#latestExchangeRate#USD#JPY --> ResponseEntity<RateResponse>
    // mgr = FxRatesMgr, name = FxCalculation, key = FxRatesController#calculation#{ amount='3.0', sourceCurrency='USD', targetCurrency='EUR'} --> ResponseEntity<CalculationResponse>
    // mgr = FxRatesMgr, name = FxCalculation, key = FxRatesController#calculation#{ amount='3.0', sourceCurrency='USD', targetCurrency='GBP'} --> ResponseEntity<CalculationResponse>
    // mgr = FxRatesMgr, name = FxCalculation, key = FxRatesController#calculation#{ amount='3.0', sourceCurrency='USD', targetCurrency='JPY'} --> ResponseEntity<CalculationResponse>
    // mgr = FxRatesMgr, name = PaginatedCalculations, key = FxRatesController#calculations#2022-01-16#0#3 --> ResponseEntity<PaginatedCalculationResponse>
    
    List<String> mgrNames = Arrays.asList("FxRatesMgr", "FixerIOMgr", "ExchangeratesapiIOMgr");

    @Test
    //@Disabled
    public void latestExchangeratesApiCacheTest() throws Exception {

        testUtils.clearDb();
        testUtils.clearCaches(testUtils.getCacheMgrs(beanFactory, mgrNames));

        latestExchangeratesApiWithSuccessTest();

        LatestRates lr = testUtils.getCacheValue(beanFactory, "FixerIOMgr", "FixerioService", "FixerioRepository#getLatestExchangeRate#USD", LatestRates.class);
        logger.info("Cache: LatestRates -> {}", lr.toString());
        assertTrue(lr.getProvider().equals(serviceProvider));
        assertTrue(lr.getSuccess());
        assertTrue(lr.getBase().equals("USD"));

        @SuppressWarnings("unchecked")
        ResponseEntity<RateResponse> rreEur = testUtils.getCacheMgr(beanFactory, "FxRatesMgr").getCache("FxRate").get("FxRatesController#latestExchangeRate#USD#EUR", ResponseEntity.class);
        logger.info("Cache: RateResponse USD->EUR ------> {}", rreEur.getBody());
        assertTrue(rreEur.getBody().getSourceCurrency().equals("USD"));
        assertTrue(rreEur.getBody().getTargetCurrency().equals("EUR"));

        @SuppressWarnings("unchecked")
        ResponseEntity<RateResponse> rreGbp = testUtils.getCacheMgr(beanFactory, "FxRatesMgr").getCache("FxRate").get("FxRatesController#latestExchangeRate#USD#GBP", ResponseEntity.class);
        logger.info("Cache: RateResponse USD->GBP ------> {}", rreGbp.getBody());
        assertTrue(rreGbp.getBody().getSourceCurrency().equals("USD"));
        assertTrue(rreGbp.getBody().getTargetCurrency().equals("GBP"));

        // caches expired ?
        // sleep for 2.1 seconds and check FxRatesMgr again
        Thread.sleep(2100);
        @SuppressWarnings("unchecked")
        ResponseEntity<RateResponse> rreEur2 = testUtils.getCacheMgr(beanFactory, "FxRatesMgr").getCache("FxRate").get("FxRatesController#latestExchangeRate#USD#EUR", ResponseEntity.class);
        assertTrue(rreEur2 == null);
        @SuppressWarnings("unchecked")
        ResponseEntity<RateResponse> rreGbp2 = testUtils.getCacheMgr(beanFactory, "FxRatesMgr").getCache("FxRate").get("FxRatesController#latestExchangeRate#USD#EUR", ResponseEntity.class);
        assertTrue(rreGbp2 == null);

        // sleep for additional 2 seconds and check FixerIOMgr again
        Thread.sleep(2000);
        LatestRates lr2 = testUtils.getCacheValue(beanFactory, "FixerIOMgr", "FixerioService", "FixerioRepository#getLatestExchangeRate#USD", LatestRates.class);
        assertTrue(lr2 == null);
        
    }

    @Test
    //@Disabled
    public void calculationApiCacheTest() throws Exception {

        testUtils.clearDb();
        testUtils.clearCaches(testUtils.getCacheMgrs(beanFactory, mgrNames));
        
        calculationApiTest();

        LatestRates lr = testUtils.getCacheValue(beanFactory, "FixerIOMgr", "FixerioService", "FixerioRepository#getLatestExchangeRate#USD", LatestRates.class);
        logger.info("Cache: LatestRates -> {}", lr.toString());
        assertTrue(lr.getProvider().equals(serviceProvider));
        assertTrue(lr.getSuccess());
        assertTrue(lr.getBase().equals("USD"));

        @SuppressWarnings("unchecked")
        ResponseEntity<CalculationResponse> cre = testUtils.getCacheMgr(beanFactory, "FxRatesMgr").getCache("FxCalculation").get("FxRatesController#calculation#{ amount='3.0', sourceCurrency='USD', targetCurrency='EUR'}", ResponseEntity.class);
        logger.info("Cache: CalculationResponse ------> {}", cre.getBody());
        assertTrue(Math.abs(3.6700336499999997 - cre.getBody().getTargetAmount()) > 0.00000000001 ? false: true);

        // caches expired ?
        // sleep for 2.1 seconds and check FxRatesMgr again
        Thread.sleep(2100);
        @SuppressWarnings("unchecked")
        ResponseEntity<CalculationResponse> cre2 = testUtils.getCacheMgr(beanFactory, "FxRatesMgr").getCache("FxCalculation").get("FxRatesController#calculation#{ amount='3.0', sourceCurrency='USD', targetCurrency='EUR'}", ResponseEntity.class);
        assertTrue(cre2 == null);

        // sleep for additional 2 seconds and check FixerIOMgr again
        Thread.sleep(2000);
        LatestRates lr2 = testUtils.getCacheValue(beanFactory, "FixerIOMgr", "FixerioService", "FixerioRepository#getLatestExchangeRate#USD", LatestRates.class);
        assertTrue(lr2 == null);
    }

    @Test
    //@Disabled
    public void calculationsApiCacheTest() throws Exception {

        testUtils.clearDb();
        testUtils.clearCaches(testUtils.getCacheMgrs(beanFactory, mgrNames));
        
        calculationsApiTest();

        LatestRates lr = testUtils.getCacheValue(beanFactory, "FixerIOMgr", "FixerioService", "FixerioRepository#getLatestExchangeRate#USD", LatestRates.class);
        logger.info("Cache: LatestRates -> {}", lr.toString());
        assertTrue(lr.getProvider().equals(serviceProvider));
        assertTrue(lr.getSuccess());
        assertTrue(lr.getBase().equals("USD"));

        @SuppressWarnings("unchecked")
        ResponseEntity<PaginatedCalculationResponse> pcre = testUtils.getCacheMgr(beanFactory, "FxRatesMgr").getCache("PaginatedCalculations").get("FxRatesController#calculations#2022-01-16#0#3", ResponseEntity.class);
        logger.info("Cache: PaginatedCalculationResponse ------> {}", pcre.getBody());
        assertTrue(pcre.getBody().getTotalItems() == 3);
        assertTrue(pcre.getBody().getTotalPages() == 1);

        // caches expired ?
        // sleep for 2.1 seconds and check FxRatesMgr again
        Thread.sleep(2100);
        @SuppressWarnings("unchecked")
        ResponseEntity<PaginatedCalculationResponse> pcre2 = testUtils.getCacheMgr(beanFactory, "FxRatesMgr").getCache("PaginatedCalculations").get("FxRatesController#calculations#2022-01-16#0#3", ResponseEntity.class);
        assertTrue(pcre2 == null);

        // sleep for additional 2 seconds and check FixerIOMgr again
        Thread.sleep(2000);
        LatestRates lr2 = testUtils.getCacheValue(beanFactory, "FixerIOMgr", "FixerioService", "FixerioRepository#getLatestExchangeRate#USD", LatestRates.class);
        assertTrue(lr2 == null);
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
