package com.myalc.fxrates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.annotation.PostConstruct;

import com.myalc.fxrates.controller.FxRatesController;
import com.myalc.fxrates.repository.external.IExternalRepository;
import com.myalc.fxrates.repository.external.exchangeratesapiio.ExchangeratesapiioRepository;
import com.myalc.fxrates.repository.external.fixerio.FixerioRepository;

import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestGatewaySupport;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.containsString;

//@ActiveProfiles("test")
//@SpringBootTest
//@TestInstance(Lifecycle.PER_CLASS)
//@AutoConfigureMockMvc
public class ControllerTests {
    
    /*@MockBean
    private FxRatesController fxRatesController;

    @Autowired
    private MockMvc mvc;

    
    @Autowired
    private FixerioRepository fixerioRepository;

    @Autowired
    private ExchangeratesapiioRepository exchangeratesapiioRepository;

    private IExternalRepository externalRepository;

    private final BeanFactory beanFactory;

    @Value("${serviceProvider:fixerio}")
    private String serviceProvider;

    private final Logger logger = LoggerFactory.getLogger(FunctionalTests.class);

    @Autowired    
    public ControllerTests(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }*/
    
    /*@BeforeAll
    private void wire() {
        if ("exchangeratesapiio".equals(serviceProvider))
            externalRepository = this.beanFactory.getBean("exchangeratesapiioRepository", ExchangeratesapiioRepository.class);
        else
            externalRepository = this.beanFactory.getBean("fixerioRepository", FixerioRepository.class);
        logger.info("serviceProvider: {}, externalRepository: {}", serviceProvider, externalRepository.toString());
    }*/

    


    /*@Autowired
    RestTemplate restTemplate;

    private MockRestServiceServer mockServer;*/
 
    /*@BeforeAll
    public void setUp() {
        RestGatewaySupport gateway = new RestGatewaySupport();
        gateway.setRestTemplate(restTemplate);
        mockServer = MockRestServiceServer.createServer(gateway);
        logger.info("mockServer: {}", mockServer.toString());
    }*/

    //@Test
    public void testClient2() throws Exception {

        /*this.mvc.perform(get("/api/v1/investment/currency/exchangerates/latest?sourceCurrency=USD&targetCurrency=EUR").contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());*/

        /*mvc.perform(get("/api/v1/investment/currency/exchangerates/latest")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content()
            .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            //.andExpect(jsonPath("$[0].name", is("bob"))
            );*/
        assertTrue(true);
    }

    



    //@Test
    public void fixerioRepositoryTestWithSuccess() {
        
        /*mockServer.expect(requestTo(matchesPattern(".*exact-example-url.com.*")))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess("response", MediaType.APPLICATION_JSON));*/



        //logger.info("restTemplate: {}", restTemplate.toString());
        
        //this.mockServer.expect(requestTo("http://localhost:8080")).andRespond(withSuccess("hello", MediaType.TEXT_PLAIN));
        String greeting = "hello";
        //assertEquals(greeting, "hello");
        //mockServer.verify();
        
        /*mockServer.expect(once(), requestTo("http://localhost:8080")).andRespond(withSuccess("{message : 'under construction'}", MediaType.APPLICATION_JSON));
        String result = service.getRootResource();
        System.out.println("testGetRootResourceOnce: " + result);
        mockServer.verify();
        assertEquals("{message : 'under construction'}", result);*/

        assertTrue(true);
    }


    /*@Test
    public void testClient1() throws Exception {
        assertTrue(true);
    }*/



}
