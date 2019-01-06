[![Build Status](https://travis-ci.org/mudasar187/Enterprise_Exam2018.svg?branch=master)](https://travis-ci.org/mudasar187/Enterprise_Exam2018)    

## Enterprise 2 - PG6100    

## Exam Fall 2018    

## Cinema Application

## Students 
- 703830: [Mudasar Ahmad](https://github.com/mudasar187)
- 704293: [Endre Synnes](https://github.com/synend16)
- 703960: [Christian Marker](https://github.com/MiniMarker)
    - Christian forgot sign out of his other GitHub-profile (ExamUser) before he made a commit to this project. So that's the reason there is one commit from this user. 
    
## Attachments 
1. [Exam 2018 Document](docs/PG6100Exam.pdf)   
2. [Distribution of work](docs/workdistribution.md)
3. [Git Structure](docs/git.md)    
4. [Database Model](docs/imgs/EarDiadram(1).png)    
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
**When we developed this application we have allocated 4-cores and 10GB of RAM**

1. Clone or fork this project.
2. To install the application:
    - `mvn clean install` (to run with tests)
    - `mvn clean install -DskipTests` (to run without tests)
        - If you run without tests and your computer supports hyperthreading you can optimize the installment process by doing this cmd instead `mvn -T 2C clean install -DskipTests`.
3. Run `docker-compose up` you can add `-d` flag for detach mode.
4. Run the DefaultData script, as described in this file [Default Data](docs/defaultdata.md)
    - After this there are two available users, one admin and one user. if you want to make a new admin user you need to insert this 
    key/value pair in the create registrationDto: `"secretPassword": "2y12wePwvk5P63kb8XqlvXcWeqpW6cNdbY8xPn6gazUIRMhJTYuBfvW6"` together with the rest of the user information. 
    - login details for admin is: `admin/admin`
    - login details for user is `foo/foo` 
5. Visit the application on [localhost:8080](http://localhost:8080)
6. Enjoy :)

**Run individual modules** <br/>
Since this is a microservice application, you can run each component/module individually, but with limited functionality. No communication with other external components/modules will work. <br/>
Ex. if you want to create a new NowPlaying, the service needs to get information from a Cinema and Room to fill inn the seats available. This connection is mocked in the tests, 
but if you try to do a POST request directly to the service without running the cinema module as well this will fail with a 503. <br/>
If you really want to run an component independently you can run the **LocalApplicationRunner** that is located in the the test folder for following modules: Cinema, CreditCard, Movie, Order, User. Eureka can also be ran seperatly, but this runner is located in its main folder.
  
## How to test application  
Every module contains unit tests for testing each service in isolation. When you install the application with `mvn clean install` these tests will run automatically.
Some tests are ignored by this command (ex. E2E-tests). To run these tests you need to run this manually. These tests are located in the e2etest module.

## End-to-end-tests
End-to-end tests is testing all of our controllers in an Docker environment made with Test-containers. <br/>
We build the Docker environment with loading the same docker-compose.yml file that the application uses. 
After this is up and running and are time cap'ed to 300 seconds. After this we test all the available modules to check if they work as intended.

Here we have tested several endpoints against our whole microservices.

We have one file for testing the authentication and one for testing several endpoints against the microservice via gateway.

We encountered that sometimes its fail to run `docker-compose up -d`, but after one retry it worked. So if some tests fail, just try to run again.

Notice that you need to run `mvn clean install` to build the jar-files to tests the e2e-tests because its depends on jar files.


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

For detailed description on who has done what in this project, please see the attached document [Who has done what?](docs/workdistribution.md).

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
            
## Ports
| Service | Local | Docker Gateway path |
|--|--|--|
| Authentication | -- | localhost:10000/auth-service/** |
| Cinema And Room | localhost:7086/cinemas/** | localhost:10000/cinema-service/** |
| CreditCard | localhost:7085/graphql | localhost:10000/creditcard-service/graphql |
| Eureka | localhost:8761 | localhost:9100 |
| Frontend | localhost:8080 | localhost:8080 |
| Movie | localhost:7083/movies/** | localhost:10000/movie-service/movies/** |
| NowPlaying | localhost:7083/now-playings/** | localhost:10000/movie-service/now-playings/** |
| Genre | localhost:7083/genres/** | localhost:10000/movie-service/genres/** |
| Invoice | localhost:7082/invoices/** | localhost:10000/order-service/invoices/** |
| Coupon | localhost:7082/coupons/** | localhost:10000/order-service/coupons/** |
| Ticket | localhost:7082/tickets/** | localhost:10000/order-service/tickets/** |
| User | localhost:7081/graphql | localhost:10000/user-service/graphql |


## Swagger & GraphiQL
| URL | Local Available | Docker Available |
|--|--|--|
| Authentication | NOT | localhost:10000/auth-service/swagger-ui.html |
| Cinema And Room | localhost:7086/swagger-ui.html | localhost:10000/auth-service/swagger-ui.html |
| CreditCard | localhost:7085/graphiql | NOT |
| Movie | localhost:7083/swagger-ui.html | localhost:10000/movie-service/swagger-ui.html |
| Invoice | localhost:7082/swagger-ui.html | localhost:10000/order-service/swagger-ui.html |
| User | localhost:7081/graphiql | NOT |

All these have username/password: `admin/admin`

GraphiQL is not accessible when running in Docker, because SpringWebSecurity blocks all queries when run in docker