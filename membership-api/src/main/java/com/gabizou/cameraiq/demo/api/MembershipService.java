package com.gabizou.cameraiq.demo.api;

import akka.NotUsed;
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
import org.pcollections.OrderedPSet;
import org.pcollections.POrderedSet;
import org.pcollections.PSequence;
import org.taymyr.lagom.javadsl.openapi.OpenAPIService;

import java.util.UUID;

@OpenAPIDefinition(
    info = @Info(
        title = "MembershipService",
        description = "A micro-service that manages memberships between Organizations and Users.",
        contact = @Contact(
            name = "Gabriel Harris-Rouquette",
            url = "https://gabizou.com/",
            email = "gabriel@gabizou.com"
        ),
        license = @License(
            name = "CC0 - Public Domain Dedication",
            url = "https://creativecommons.org/publicdomain/zero/1.0/"
        )
    ),
    tags = {
        @Tag(name = "organization",
            description = "Organization related services"),
        @Tag(name = "user",
            description = "User related services"
        ),
        @Tag(name = "membership",
            description = "Membesrhip related services"
        )
    }
)
public interface MembershipService extends OpenAPIService {

    /**
     * Adds the provided {@link User user} to the {@link Organization
     * organization}
     * by {@code name}.
     *
     * @param organizationName The organization's name
     * @return The organization as a response
     */
    @Operation(
        method = "POST",
        summary = "Submits a request to add the passed in UserId to the organization by name",
        description = "With the UserId as request data, using the Organization by name included in the " +
            "path call, will attempt to create a new Membership. The Membership is registered with the " +
            "service as a UserId and OrganizationId coupling only.",
        parameters = @Parameter(
            name = "organizationName",
            in = ParameterIn.PATH,
            description = "The Organization by name in the path",
            examples = @ExampleObject(
                name = "ExampleCompany",
                value = "ExampleCompany"
            ),
            required = true
        ),
        requestBody = @RequestBody(
            description = "The UserId object, represented as a UUID string",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserId.class),
                examples = @ExampleObject(
                    name = "John Doe's UserId",
                    summary = "John Doe's user id, the UUID Type 5 based on their initial email",
                    value = "\"3eda6d3a-54f3-59a3-8904-d5eabbccd1f2\""
                )
            ),
            required = true
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "The created membership object",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Membership.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "No found organization"
            )
        }
    )
    ServiceCall<UserId, Membership> addMember(String organizationName);

    /**
     * Gets the {@link Membership membership} of the {@link UserId user}
     * for the {@link Organization organization by name}.
     *
     * @param organizationName The organization's name
     * @return The membership as a response
     */
    @Operation(
        method = "GET",
        summary = "Gets the Membership object for the Organization and User",
        parameters = @Parameter(
            in = ParameterIn.PATH,
            name = "organizationName",
            description = "The organization by name"
        ),
        requestBody = @RequestBody(
            description = "The UserId requested for the membership",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserId.class),
                examples = @ExampleObject(
                    name = "John Doe's UserId",
                    summary = "John Doe's user id, the UUID Type 5 based on their initial email",
                    value = "\"3eda6d3a-54f3-59a3-8904-d5eabbccd1f2\""
                )
            )
        ),
        responses = @ApiResponse(
            responseCode = "200",
            description = "The found membership",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    implementation = Membership.class,
                    description = "The found membership between the user and organization"
                )
            )
        )
    )
    ServiceCall<UserId, Membership> getMembership(final String organizationName);

    /**
     * Removes the requested {@link User User} from the queried
     * {@link Organization Organization}.
     *
     * @param organizationName The organization name
     */
    ServiceCall<UserId, NotUsed> removeMember(String organizationName);

    /**
     * Gets all {@link User users} part of an {@link Organization
     * organization}.
     *
     * @param organizationName The organization name
     * @return The sequence of users
     */
    ServiceCall<NotUsed, POrderedSet<User>> getMembers(String organizationName);

    /**
     * Gets the {@link PSequence list} of {@link Organization Organizations} that a
     * {@link UserId User} may belong to.
     *
     * @param id The user id
     * @return The sequence (list) of organizations
     */
    ServiceCall<NotUsed, POrderedSet<Organization>> getOrganizations(UserId id);

    /**
     * Prunes all memberships of the provided {@link UserId User id}.
     *
     * @param userId The user id
     * @return
     */
    ServiceCall<NotUsed, NotUsed> pruneAllMembershipsFor(UserId userId);

    @Override
    default Descriptor descriptor() {
        return this.withOpenAPI(Service.named("membership")
            .withCalls(
                Service.restCall(Method.GET, "/api/organization/:organizationName/members", this::getMembers),
                Service.restCall(Method.POST, "/api/organization/:organizationName/members", this::addMember),
                Service.restCall(Method.DELETE, "/api/organization/:organizationName/members", this::removeMember),
                Service.restCall(Method.GET, "/api/user/:id/memberships", this::getOrganizations),
                Service.restCall(Method.DELETE, "/api/user/:id/memberships", this::pruneAllMembershipsFor),
                Service.restCall(Method.POST, "/api/organization/:organizationName/member", this::getMembership)
            )
            .withPathParamSerializer(UserId.class, PathParamSerializers.required("UserId", UserId::fromString, UserId::toString))
            .withPathParamSerializer(OrganizationId.class, PathParamSerializers.required("OrganizationId", OrganizationId::fromString, OrganizationId::toString))
            .withAutoAcl(true)
        );
    }

}
