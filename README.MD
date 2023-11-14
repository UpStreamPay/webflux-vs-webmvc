# WebFlux vs Web MVC

In the context of Java 21 virtual Threads, we wanted to compare performances of Web API in following contexts:
* Spring Web MVC with platform Threads
* Spring Web MVC with virtual Threads
* Spring WebFlux

## Test apps
Both applications are exposing a simple REST API 
* GET product by ID
* GET customer by ID
* POST an order calling a discount-api

Applications are using a PostgresSQL database.

## Load test
Load test is done with [Gatling](https://gatling.io/).
Scenario is located in `load-test` folder.

## Data generator
Data generation is done with a custom app in `data-generator` folder.
