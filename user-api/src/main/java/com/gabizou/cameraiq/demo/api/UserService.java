package com.gabizou.cameraiq.demo.api;

import akka.NotUsed;
import akka.japi.Pair;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.deser.PathParamSerializers;
import com.lightbend.lagom.javadsl.api.transport.Method;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pcollections.POrderedSet;
import org.taymyr.lagom.javadsl.openapi.OpenAPIService;

import java.util.Set;
import java.util.UUID;

/**
 * The USer service interface.
 *
 * <p>Describes all functions that Lagom needs to know about to
 * serve and consume {@link User}.</p>
 */
@OpenAPIDefinition(
    info = @Info(
        title = "UserService",
        description = "A micro-service that manages User creation, lookup, and deletion",
        contact = @Contact(
            name = "Gabriel Harris-Rouquette",
            url = "https://gabizou.com/",
            email = "gabriel@gabizou.com"
        ),
        license = @License(
            name = "CC0",
            url = "https://creativecommons.org/publicdomain/zero/1.0/"
        )
    ),
    tags = {
        @Tag(
            name = "user",
            description = "User related services"
        )
    }
)
public interface UserService extends OpenAPIService {

    /**
     * Gets a single {@link User user} by {@link UserId uuid}.
     *
     * @param uuid
     * @return
     */
    @Operation(
        method = "GET",
        description = "Looks up a User object by their UserId (UUID)",
        parameters = @Parameter(
            name = "uuid",
            in = ParameterIn.PATH,
            description = "The user UUID, similarly represented as a UserId"
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "The registered User",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        implementation = User.class,
                        description = "The found user by the id"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "No found user"
            )
        }
    )
    ServiceCall<NotUsed, User> lookupUser(UserId uuid);

    /**
     * Creates a single {@link User}.
     *
     * @return The user object corresponding to the new or existing
     * registration. If
     * if already
     * registered or newly
     * created
     */
    @Operation(
        method = "POST",
        summary = "Submit a request to register the UserRegistration object as a new User in the service",
        description = "Submitting a request to create a new user",
        requestBody = @RequestBody(
            description = "The user registration object",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserRegistration.class),
                examples = @ExampleObject(
                    name = "John Doe",
                    summary = "A demonstration of a user registration for John Doe",
                    value = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"address\":\"100 Main St, AnVille, AnyState, 12345\",\"email\":\"john.doe@somewhere.com\",\"phoneNumber\":\"123-456-7890\"}"
                )
            ),
            required = true
        )
    )
    ServiceCall<UserRegistration, User> createUser();

    /**
     * Gets a list of all users.
     *
     * @return The list of all users
     */
    @Operation(
        method = "GET",
        summary = "Gets all users "
    )
    ServiceCall<NotUsed, POrderedSet<User>> getUsers();

    /**
     * Gets a {@link Set set} of {@link User users} by the provided set of
     * id's, and provides the pairing of found users, and any errors as a
     * result.
     *
     * @return
     */
    ServiceCall<Set<UserId>, Pair<POrderedSet<User>, Set<String>>> getUsersByIds();

    @Override
    default Descriptor descriptor() {
        return this.withOpenAPI(Service.named("users")
            .withCalls(
                Service.restCall(Method.POST, "/api/user/", this::createUser),
                Service.restCall(Method.GET, "/api/user/:uuid", this::lookupUser),
                Service.restCall(Method.GET, "/api/user/", this::getUsers),
                Service.restCall(Method.POST, "/api/user/", this::getUsersByIds)
            )
            .withPathParamSerializer(UserId.class, PathParamSerializers.required("UserId", UserId::fromString, UserId::toString))
            .withAutoAcl(true)
        );
    }
}
