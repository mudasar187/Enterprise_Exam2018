## Enterprise 2 - PG6100    

## Exam Fall 2018    

## Cinema Application    

#### Travis  (Master branch)  
[![Build Status](https://travis-ci.com/mudasar187/Enterprise_Exam2018.svg?token=v251k9AGWGPGijfDozX8&branch=master)](https://travis-ci.com/mudasar187/Enterprise_Exam2018)    
      
## Students 
- 703830: [Mudasar Ahmad](https://github.com/mudasar187)
- 704293: [Endre Synnes](https://github.com/synend16)
- 703960: [Christian Marker](https://github.com/MiniMarker)
   
    
## Table of content 
1. [Exam 2018 Document](docs/PG6100Exam.pdf)    
2. [Git Structure](docs/git.md)    
3. [Database Model](docs/databasemodel.md)    
4. [Module Diagram](docs/modulediagram.md)  
5. [Coverage Report](docs/coveragereport.md)  
6. [Default Data](docs/defaultdata.md)  
  
## Technology
- Production
Kotlin, Spring framework, GraphQL, Postgres, Netflix Eureka, Netflix Hystrix, Netflix Ribbon, RabbitMQ, AMQP, Redis, Docker / Docker-Compose

- Testing
H2, RestAssured, WireMock, TestContainers

  
## About the cinema application  
  
Tell about the cinema application  
  
## How to run  
  
Tell how to run the application  
  
## How to test application  
  
How to test the application  

## Project structure

| Module | Description | Services | Technology | 
|--|--|
| Cinema  | Cinema is responsible for storing and CRUD operations | cinemas, rooms | REST |
| Creditcard | Creditcard is responsible for storing and CRUD operations | creditcards | GraphQL
| Docs | Documentation content | | |
| E2E-tests | End to end test for testing every module in the application | Everyone | None | 
| Eureka | bla bla |
| Gateway | Gateway is responsible for proxying and guiding the users request to the correct service |
| Movie | Movie is responsible for storing and CRUD operations |
| Order | Order is responsible for storing an CRUD operations  | Genre, Movie, NowPlaying | REST |
| Report | Total report coverage for cinema application | All | None |
| User | Order is responsible for storing an CRUD operations | UserService | GraphQl |
| Utils | Common helpers/validators and etc.. | None | None |
  
  
## Work progress  

How we worked, and who did what and etc...  


## End-to-end-tests

What we have tested in e2e-tests

## Swagger

Explain how to access swagger on browser and how to use it