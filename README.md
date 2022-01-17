# FX Rates Application

Simple foreign exchange application provides some APIs as below:
* **Exchange Rate API** gets currency pair and responds with exchange rate
* **Conversion API** performs convertion by given amount, source currency, and target currency.
* **Conversion List API** gets transaction id or transaction date and responds with a list of conversions


## Local run
```sh
sudo apt-get install openjdk-8-jdk-headless
sudo apt-get install maven

cd fxrates-sb
maven clean package
mvn clean spring-boot:run
mvn clean package; java -jar target/fxrates-1.0-SNAPSHOT.jar com.myalc.fxrates.Application
```

## API Docs
* [http://localhost:8080/api-docs](http://localhost:8080/api-docs)
* [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
* [https://editor.swagger.io/](https://editor.swagger.io/)