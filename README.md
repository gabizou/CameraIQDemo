## CameraIQ RESTful API Demo

A simple webapp created with [Lagom] and [Maven]. Designed with the following:

Objective:

Design and implement a simple application serving a RESTful API to perform operations on 
Organization as well as Users. Please implement the challenge in any of the Java Web Frameworks.
We are looking to see how you design and implement your model as well as your application. 
We expect to be able to trace an endpoint down to the data transfer objects (DTO) that represent
Organizations and Users. Feel free to provide additional documentation (UML, SQL scripts,
comments, etc) that may communicate your design choices. We expect your API to support the 
following operations:

- Create a single `Organization`
- Create a single `User`
- Add a `User` to an `Organization`
- Delete a `User` from an `Organization`
- Read all `User`s who belong to a specific `Organization` 
- Read all `Organization`s to which a `User` belongs

### Requirements to Run

- Java 8 (Lagom v1.5.x is not compatible)
- Maven 3.5.1

1) Clone repository
1) Run `mvn -Dlogger.file=./demo-utils/src/main/resources/debug.xml lagom:runAll -f pom.xml`
1) In a separate cmd session, open `./scripts/`
1) Run any one of the scripts for demonstration, log output will emit debugging information from the services and repositories.

 Generated OpenAPI docs can be seen

- [UserService](https://app.swaggerhub.com/apis-docs/gabizou/user-service/1.0.0)
- [OrganizationService](https://app.swaggerhub.com/apis-docs/gabizou/organization-service/1.0.0)
- [MembershipService](https://app.swaggerhub.com/apis-docs/gabizou/membership-service/1.0.0)

[Lagom]:https://lagomframework.com/
[Maven]:https://maven.apache.org/
[Event Sourcing]:https://docs.microsoft.com/en-us/previous-versions/msp-n-p/jj591559(v=pandp.10)?redirectedfrom=MSDN
[UserService]:user-api/src/main/java/com/gabizou/cameraiq/demo/api/UserService.java
[OrganizationService]:organization-api/src/main/java/com/gabizou/cameraiq/demo/api/OrganizationService.java
[MembershipService]:membership-api/src/main/java/com/gabizou/cameraiq/demo/api/MembershipService.java
[ReadSideEventProcessors]:https://www.lagomframework.com/documentation/1.5.x/java/api/index.html?com/lightbend/lagom/javadsl/persistence/ReadSideProcessor.html
[eventual consistency]:https://www.erikheemskerk.nl/event-sourcing-eventual-consistency-responding-to-events/
[UserRepository]:user-impl/src/main/java/com/gabizou/cameraiq/demo/impl/repo/UserRepository.java
[Achilles]:https://github.com/doanduyhai/Achilles
[Lagom-OpenAPI]:https://github.com/taymyr/lagom-openapi#12-generate-java-dsl