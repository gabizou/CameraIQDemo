##CameraIQ RESTful API Demo

As a code assessmenet, the demo RESTful API allows for the following:
- Creating `User`s and `Organization`s
- Adding `User`s to `Organization`s (represented by an intermediary model called
 `Membership`)
- Removing `User`s from `Organization`s
- Some basic transactional management with Hibernate
- `UserController` and `OrganizationController` are separated from the `User
`|`OrganizationServices`s.


Run the app with SpringBoot: `gradlew bootRun`

### API
####`User` Management

Operation | Request | Data | Description
---|---|---|---
`GET` | `localhost:8080/api/v1/users` | | Gets a list of all current users
`POST` | `localhost:8080/api/v1/users/addUser` | {User object} | Adds the provided user
`GET` | `localhost:8080/api/v1/users/{id}` | N/A | Gets the `User` object by it's id
`PUT` | `localhost:8080/api/v1/users/{id}` | User object | Modifies the existing user by the id to fill in additional information or modify.

####`Organization` Management

Operation | Request | Data | Description
---|---|---|----
`GET` | `localhost:8080/api/v1/organizations` | | Gets a list of all current organizations including members
`POST` | `localhost:8080/api/v1/organizations` | {Organization Object} | Adds the organization object provided it's not been added by name
`GET` | `localhost:8080/api/v1/organizations/{id}` | organization id | Gets the organization by id, listing all members
`PUT` | `localhost:8080/api/v1/organizations/{id}` | {"id", Organization Object} | Modifies the existing organization by id
`PUT` | `localhost:8080/api/v1/organizations/{id}/addUser/{userId}` | {"id", "userid"} | Adds the provided User to the provided organization, assuming both are existing
`DELETE` | `localhost:8080/api/v1/organizations/{id}/delUser/{userId}` | {"id", "userId"} | Removes the provided user from the organization

