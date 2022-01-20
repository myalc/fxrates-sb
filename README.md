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

## Local test and run
```sh
cd fxrates-sb
mvn spring-boot:run -Dmaven.test.skip 
mvn spring-boot:run
mvn clean package
java -jar target/fxrates-1.0-SNAPSHOT.jar com.myalc.fxrates.Application
```

## Container
```sh
docker images
docker build -t myalc/fxrates .
docker run -it myalc/fxrates sh
docker run -d -p 8080:8080 -e FIXERIO_ACCESS_KEY='fixeriokey' -e PROFILE='default' myalc/fxrates
docker run -d -p 8080:8080 -e FIXERIO_ACCESS_KEY='fixeriokey' -e PROFILE='staging' myalc/fxrates
docker run -d -p 8080:8080 -e FIXERIO_ACCESS_KEY='fixeriokey' -e PROFILE='prod' myalc/fxrates
docker run -d -p 8080:8080 -e FIXERIO_ACCESS_KEY='fixeriokey' -e EXCHANGERATESAPIIO_ACCESS_KEY='exchangeratesipkey' -e PROFILE='prod' myalc/fxrates

docker ps -a
docker exec -it <id> sh
> echo $PROFILE
> echo $FIXERIO_ACCESS_KEY
> echo $EXCHANGERATESAPIIO_ACCESS_KEY
> exit

docker logs -f <id>
docker kill <id>
docker container prune
docker rmi <image-id>
```

## Docker compose
```sh
cd fxrates-sb
docker-compose build
docker-compose up
```

Use following command to get token from keycloak
```sh
docker exec -i app /bin/sh -c "curl -X POST -H 'Content-Type: application/x-www-form-urlencoded' -d 'grant_type=password' -d 'client_id=fxrates-app' -d 'client_secret=na3W0tkdwIBVCen3KCiiuo1xnqkQWl3w' -d 'username=myalc' -d 'password=123456' 'http://keycloak:8080/auth/realms/fxrates/protocol/openid-connect/token' | jq -r '.access_token'"
```

## Keycloak
* [http://localhost:8080/auth/admin](http://localhost:8080/auth/admin) (admin/123456)

## Postgres
* Keycloak is using potgres database. *keycloak/db/postgres_data* directory contains postgres files. 
* Use [pgAdmin](https://www.pgadmin.org/download/) to connect.



## API Docs
* [http://localhost:9090/api-docs](http://localhost:9090/api-docs)
* [http://localhost:9090/swagger-ui/index.html](http://localhost:9090/swagger-ui/index.html)
* [https://editor.swagger.io/](https://editor.swagger.io/)

## H2 Database
* [http://localhost:9090/h2-console](http://localhost:9090/h2-console)