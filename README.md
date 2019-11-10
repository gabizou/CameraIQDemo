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

### To Run This Locally

1) Clone the repo
2) Run `mvn lagom:runAll`
3) In a separate command session (lagom's maven command keeps the services running until `<RETURN>` is entered): `cd ./scripts`
4) Run any scripts to generate data/modify data/etc.

API documentation can be viewed [here]() `// TODO - Due to the implementation of Memberships being still a WIP, I've yet to use Swagger.io to make
 the OpenAPI docs`

### Todo's

- Ensure Defensive Programming Practices
    - Validate ignored return results of statements
    - Verify statement side effects
- Migrate from [low-level CQL](user-impl/src/main/java/com/gabizou/cameraiq/demo/impl/UserRepository.java) to [Achilles]
- Make use of Logger for various operations
- OpenAPI generation with [Lagom-OpenAPI]

### Problems with Maven

- If a maven plugin (e.g. Lagom) does not provide configuration parameters for some runtime aspect (e.g. logging),
  then it is really difficult to configure that aspect (for logging, one has to use VM arguments instead).
  
- Finding the sources of Maven plugins (e.g., Lagom) is difficult -- Some IDEs (e.g. IntelliJ) do not show the Maven plugins
  in the searchable classpath.
  
- Lack of configurations for certain aspects of Lagom's plugin (like logging)
- Unable to search sources of Lagom-maven plugin as it's not a dependency

### A bit of explanation about this implementation

#### Separation of Services

A traditional implementation of a `User` `Organization` and membership *as-a-service* app would 
likely use some CRUD based setup with Spring framework and JPA. [My original implementation](https://github.com/gabizou/CameraIQDemo/tree/9fb2a1e9388b37d2d2851797344df31a84a4e25f) did this,
and issues quickly arose where I'd be unable to handle multiple requests to removing a `User` as a
member of an `Organization`, and already having some difficulty separating Domain contexts, I elected
to listen to some advice to learn to use [Lagom]. Because **Lagom** believes in an 
[Event Sourcing] design, it's also heavily designed for making *domain contexts* belonging 
to one single service. Somewhat like the infamous quote about Linux: "Do one thing, and do it well".

So, I designed the API implementation to be micro-serviced based where the 
implementation is split up into **three** contexts:
- [UserService]
- [OrganizationService]
- [MembershipService]

Each of these API services handle their own contexts: Users are managed by the `UserService`,
`Organizations` are handled by the `OrganizationService`, and `Memberships` are managed by the `MembershipService`.
This allows for a clean separation of data control, flexibility, and scalability of services.

#### EventSourcing as a backup

Because each service has specific events they control through their service calls,
we can take advantage of the Event Journal to always have a clear recovery state of the
system/service based on it's state of persisted events. Because events are persisted only
when a command has successfully been executed, we gain reproduability of the webapp running into issues,
replicating corner cases, and writing integration tests to verify what events have been
persisted for each service.

The largest plus to all of this is that by using `UUID`'s for most services, we can trace
the actions/reactions of these API calls to their side effect events (whether a `User` was created,
`Organization` name changed, `User` address updated, etc.) while relying
solely on the event journal. 

A caveat is when a design for *user data* is a purgeable option.

#### GDPR And You

As most of the Internet services users/developers/project managers/etc. are aware, the EU's GDPR
requirements stipulate that all user-specific data must be purgeable from a system,
ignoring reasonable backups that will eventually be purged after an expiration time.
This primarily means that a `User` by the name of `John Smith` must be allowed to have all of
their data removed from the system, effectively deleting the `User` from the `UserService`, and all
other services that are dependent on that `User` (remember, `Membership`s are managed by a `MembershipService`
of which has a dependency and read-action on `UserService` events).

This situation is further exacerbated by the fact that an *Event Journal* is meant to be
***append-only***. And since events are persisted in all their glory, we **cannot** store
user generated data in those events. So, what do we do? 

#### Separation of Business Data and Application Data

We can all agree that a `UserService` provides `User`s and `MembershipService`s
provide `Membership`s, which refer to `User`s and `Organization`s. But, because we can effectively 
separate the user identifying information to a `UUID` as a *key* for that `User` data, 
`MembershipService` doesn't really rely on the `UserRegistration` information (`firstName`, `lastName`, `address`, `phoneNumber`, etc.),
but relies only on the `User`'s `UUID`. So, we can persist the literal state of the `UserService` 
by having a playback of events to create the set of `User`s by a `UUID`, and a **separate persistence** 
managing the actual user data such that a `User` removal from the service
as a simple `DeleteUser` event, removing the `User` by `UUID` from the service state,
and have the event journal in tact, including the creation and modification of a `User` by 
some `UUID` that now has no valid `UserRegistration` data.

#### Read-Event-Handling

But what of other services that rely on knowing when a `User` has been removed from
the system? Easy, Lagom has [ReadSideEventProcessors] that can define event handlers (similar
to pub-sub) but make cross service communication happen through those services API's. Case
in point: `Membership`s registers a `DeleteUser` event handler, to invoke an API call to delete
all `Membership`s belonging to the `User` by `UUID`. And of course, the `MembershipService` will still
perform that operation, throw the bulk of events for the various `Membership`s being
removed, and then store those events in the event journal. The benefit here is that we can
see that a `Membership` being comprised solely of `User`'s `UUID` and an identifier 
for an `Organization`, none of that information is actually user-defined data, but
rather `UUID` references to both. 

At this point, the `MembershipService` event journal becomes our for-free data store
for all existing `Membership`s.

#### User-Data vs Application-Data

From the previous bits, we can see there's a clear distinction of what is *actually*
`User` defined data, and how the system is designed to handle deletion requests,
without the possibility of needing downtime to "purge" said data, since an event
persisting that request will spread throughout the nodes via EventJournal, and leads
to a clean [eventual consistency]. Because our user data is specific to an initial `UUID`, 
we can rely on that singular `UUID` to be the primary key for all user data, related
for all services. Removing that data becomes easy, as exemplified by the
[UserRepository]. Since this user-data is purgeable, the only queryable option is
for the `UserEntity` to allow us to request that `User` information, if the event journal*
proves that that `User` should still exist. By relying on that `UserEntity` having
the `UserEvent`s to re-create the state of the `UserEntity`, other services
making requests to locate a `User` through the `UserService` will also be able
to separate their application-data-by-event-journaling and user-generated data based 
on their respective domain contexts. 

A clear example is `Organization`s. An `Organization` is referred to by humans by
their `String` names. Fortunately, we can design the entirety of the `Organization`'s
data to be replaceable, and of course, purgeable, retaining only the event journal to
tell us that an `Organization` may or may not have existed by a specific `UUID`.

### User-provided Application-generated Data

To generate these `UUID`'s for the various services, I've elected to take the approach of
defining a primary key for:
- `User`s to be the  `UUID Type 5` of the registered email
- `Organization`s to be the `UUID Type 5` of the original name on creation

What this allows to happen: legacy lookups when a `User` has changed their email,
redirection when an `Organization` has it's name changed, sym-link-like reactions 
on `Organization` renames and `User` email lookups. Because this is now
Application-generated data, these `UUID`'s can be retained and "owned" by
the system, thereby allowing our EventJournals using these id's to be entirely
**append-only**.

### Closing

I realize that this readme is very conversational and might be patronizing, but
believe me that my intentions are to explain this webapp demo from a zero-percent
knowledge of any of the concepts/technologies used, sans some obvious definitions, as referred
by in this README with links to various websites otherwise (lagom documentation, articles
for research on techniques/solutions/etc.).

What I hope this will show is a more advanced example than what the current
[lagom examples](https://github.com/lagom/lagom-samples/tree/20d68d91cf392d93ed44aee029d5e8da71e68fa6)
provide, as many docs currently do not address the possible issues when designing a new service with
GDPR in mind, complete user-data replacement, and cross-service interoperability.


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