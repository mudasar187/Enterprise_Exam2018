## Enterprise 2 - PG6100    

## Exam Fall 2018    

## Cinema Application    

#### Travis  (Master branch)  
[![Build Status](https://travis-ci.com/mudasar187/Enterprise_Exam2018.svg?token=v251k9AGWGPGijfDozX8&branch=master)](https://travis-ci.com/mudasar187/Enterprise_Exam2018)    
      
## Students 
- 703830: [Mudasar Ahmad](https://github.com/mudasar187)
- 704293: [Endre Synnes](https://github.com/synend16)
- 703960: [Christian Marker](https://github.com/MiniMarker)
    - Christian forgot sign out of his other GitHub-profile (ExamUser) before he made a commit to this project. So that's the reason there is one commit from this user. 
    
## Attachments 
1. [Exam 2018 Document](docs/PG6100Exam.pdf)   
2. [Distribution of work](docs/workdistribution.md)
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
This Cinema Application is an exam project in PG6100 - Enterprise 2.
  
## How to run

**Make sure that your Docker desktop-application has allocated enough CPU-cores and RAM!!** 
**When we developed this application we have allocated 4-cores and 8GB of RAM**

1. Clone or fork this project.
2. To install the application:
    - `mvn clean install` (to run with tests)
    - `mvn clean install -DskipTests` (to run without tests)
        - If you run without tests and your computer supports hyperthreading you can optimize the installment process by doing this cmd instead `mvn -T 2C clean install -DskipTests`.
3. Run `docker-compose up` you can add `-d` flag for detach mode.
4. Run the DefaultData script, as described in this file [Default Data](docs/defaultdata.md)
5. Visit the application on [localhost:8080](http://localhost:8080)
6. Enjoy :)

**Run individual modules**
Since this is a microservice application, you can run each component/module individually, but with limited functionality. No communication with other external components/modules will work.
ex. If you want to create a new NowPlaying, the service needs to get information from a Cinema and Room to fill inn the seats available. This connection is mocked in the tests, 
but if you try to do a POST request directly to the service without running the cinema module as well this will fail with a 503.
  
## How to test application  
Every module contains unit tests for testing each service in isolation. When you install the application with `mvn clean install` these tests will run automatically.
Some tests are ignored by this command (E2E-tests). To run these tests you need to run this manually. These tests are located in the e2etest module.

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
The task stated that every team-member had to create at least one RESTful API endpoint each.
I our case every team member had to create two APIs, all of these provides POST, PUT, PATCH, GET, and DELETE methods to modify the Entities in the DB.

### Who made what?
- Christian
    - Made API endpoints and tests for **Coupon** and **Ticket**, and tests for **User** and **CreditCard**.
- Mudasar
    - Made API endpoints and tests for **Cinema** and **Room**, and API endpoints for **User**.
- Endre
    - Made API endpoints and tests for **Movie** and **Genre**, and API endpoints for **CreditCard**.

After these APIs had been made there has been done some minor tweaks made by other team-members later in the process, when we experienced bugs in the code.

For detailed description on who has done what in this project, please see the attached document [Who has done what?](docs/tasks.pdf).

The rest:
- Eureka
- E2E tests
- Frontend
- Gateway
- Utils
- Authentication Service
- NowPlayings Service
- Invoices Service

Has been made in collaboration between the team members with pair programming or splitted up the modules into bits for individual work.

In the final steps of the development, not all computers in the group were able to start and run the application. So there was a lot of pair programming done on Mudasar's and Endre's computers.

## Utils
Utils is a module that contains a lot of helper-classes and functions for maintaining a consistent setup between all the different modules in this project. <br/> 
This module contains he following helpers:

- Logging
    - Logging interface
    - Exception messages
- Exception handlers
    - RestResponseEntityExceptionHandler 
    - Handle 500 Exceptions to match our setup with WrappedResponses
        - InternalException
    - Handle custom Exceptions thrown by our program like:
        - ConflictException
        - NotFoundException
        - UserInputValidationException
        - PreconditionException
- Cache
    - Every application that scans this module will implement caching by default.
    - ETagHandler
        - This is a generic class for calculating an ETag for a response.
- Convertion
    - ConvertionHandler
        - This handles conversion from a timestamp as string (yyyy-mm-dd hh:mm:ss) to ZonedDateTime.
- Dtos and ResponseDtos
    - All of the Dtos and responseDtos for the entire project, this is helpful because some Services is using other services's dtos to communicate.
- HalLinks
    - PageDtoGenerator
        - Generic class for generation of PageDtos for responses
    - HalLinkGenerator
        - Generic class for generation of HalLinks and pagination for responses
- Validation
    - This module contains different types of validators.
        - ValidateId: 
            - Check and convert the id given as parameter in a URI
            - Throws exception if fail
        - ValidateTimeFormat: 
            - Checks if the timeformat given in the creation of an entity (yyyy-mm-dd hh:mm:ss) matches a RegEx
            - Throws exception if fail
        - ValidateSeatFormat: 
            - Checks if the format of a seat given in the creation of an entity matches a RegEx
            - Throws exception if fail
        - ValidateLimitAndOffset
            - Checks if offset and limit given in the parameters is valid
            - Throws exception if fails
            
## End-to-end-tests

What we have tested in e2e-tests

## Ports
| Service | Local | Docker Gateway path |
|--|--|--|
| Authentication | -- | auth-service/** |
| Cinema | 7086 | cinema-service/** |
| CreditCard | 7085 | creditcard-service/** |
| Eureka | 8761 | 9100 |
| Frontend | 8080 | 8080 |
| Movie | 7083 | movie-service/** |
| Order | 7082 | order-service/** |
| User | 7081 | user-service/** |

## Swagger
Some documentation on the setup an which HTTP-methods are available in an endpoint cam be found in swagger.

**Local**<br/>
To find out which port the service is running on, please check the table above <br/>
URL: http://localhost:PORT/swagger-ui.html

**Docker**<br/>
To find out which gateway path the service is running on, please check the table above <br/>
URL: http://localhost:10000/Docker-Gateway-path/swagger-ui.html
