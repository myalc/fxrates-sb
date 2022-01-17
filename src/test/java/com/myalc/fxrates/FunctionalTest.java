package com.myalc.fxrates;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.myalc.fxrates.service.FxRatesService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FunctionalTest {

    @Autowired
	private FxRatesService fxRatesService;

    private final Logger logger = LoggerFactory.getLogger(FunctionalTest.class);

	public void calculateTest1() {
		Double c = fxRatesService.calculate(4.0, 2.0);
		Double expected = 8.0;
        logger.info("calculation: {}, expected: {}, abs: {}", c, expected, Math.abs(expected - c));
		assertTrue(Math.abs(expected - c) > 0.00000000001 ? false: true);
	}

	public void calculateTest2() {
		Double c = fxRatesService.calculate(15.44444, 0.1245);
		Double expected = 1.92283278;
        logger.info("calculation: {}, expected: {}, abs: {}", c, expected, Math.abs(expected - c));
		assertTrue(Math.abs(expected - c) > 0.00000000001 ? false: true);
	}
    
}
