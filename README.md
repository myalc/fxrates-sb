# FX Rates Application

Simple foreign exchange application provides some APIs as below:
* **Exchange Rate API** gets currency pair and responds with exchange rate
* **Conversion API** performs convertion by given amount, source currency, and target currency.
* **Conversion List API** gets transaction id or transaction date and responds with a list of conversions


## Functionality
* OpenAPI Specification version 3 [https://swagger.io/specification/](https://swagger.io/specification/)
* Caching external service providers (using caffeine)
* Caching API responses
* Consumes external service providers based on configured cache expiration value
* Selects from database based on configured cache expiration value
* Custom exception handling


## Installation
```sh
sudo apt-get install openjdk-8-jdk-headless
sudo apt-get install maven
```

## Test and run
```sh
cd fxrates-sb

mvn spring-boot:run -Dmaven.test.skip 
mvn spring-boot:run

maven clean package
mvn clean package
java -jar target/fxrates-1.0-SNAPSHOT.jar com.myalc.fxrates.Application
```

## API Docs
* [http://localhost:8080/api-docs](http://localhost:8080/api-docs)
* [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
* [https://editor.swagger.io/](https://editor.swagger.io/)