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
2. [Who has done what?](docs/tasks.pdf)
3. [Git Structure](docs/git.md)    
4. [Database Model](docs/databasemodel.md)    
5. [Module Diagram](docs/modulediagram.md)  
6. [Coverage Report](docs/coveragereport.md)  
7. [Default Data](docs/defaultdata.md)  
  
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
|--|--|--|--|
| Cinema  | Cinema is responsible for storing and CRUD operations | Cinemas, Rooms | REST |
| Creditcard | Creditcard is responsible for storing and CRUD operations | Creditcards | GraphQL |
| Docs | Documentation content |  |  |
| E2E-tests | End to end test for testing every module in the application | Everyone |  | 
| Eureka | Eureka is responsible for service discovery |  |  |
| Gateway | Gateway is responsible for proxying and guiding the users request to the correct service |  |  |
| Movie | Movie is responsible for storing and CRUD operations | Genres, Movies, NowPlayings | REST |
| Order | Order is responsible for storing an CRUD operations  | Coupons, Tickets, Invoices | REST |
| Report | Total report coverage for cinema application | All |  |
| User | Order is responsible for storing an CRUD operations | Users | GraphQL |
| Utils | Common helpers/validators and etc.. |  |  |
  
  
## Work progress  

For detailed description on who has done what in this project, please see the attached document [Who has done what?](docs/tasks.pdf).

The rest:
- Eureka
- E2E tests
- Frontend
- NowPlayings Service
- Invoices Service

Has been made by pair-programming an 


## End-to-end-tests

What we have tested in e2e-tests

## Swagger

Explain how to access swagger on browser and how to use it
