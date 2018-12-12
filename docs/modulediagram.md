[Back to README](../README.md)

## Module Diagram

**Module deciption:**
- Dotted lines are showing the communication between Redis and the services.
- Whole lines are showing direct communication between services.
- Yellow boxes represents services in the application
- Green boxes represents duplicates of the same service

**Tech deciption:**
- Redis is used to store distributed user session tokens
- Ribbon is used for load balancing of communications between modules internally
- RabbitMQ is used for message delivery between authentication and user, when a user signs up it automatic gets created a user in its DB.
- Gateway uses Spring Cloud Gateway to proxy calls to the correct service internally
- Eureka is used for service discovery and together with ribbon creates Client Side Load Balancing  


![Module Diagram](imgs/Module%20diagram.png)