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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pcollections.POrderedSet;
import org.taymyr.lagom.javadsl.openapi.OpenAPIService;

import java.util.UUID;

@OpenAPIDefinition(
    info = @Info(
        title = "OrganizationService",
        description = "A micro-service that manages Organization creation, lookup, and deletion",
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
    tags = @Tag(name = "organization", description = "Organization related services")
)
public interface OrganizationService extends OpenAPIService {

    /**
     * Gets an {@link Organization} by name.
     *
     * @param name The name of the organization
     * @return The organization
     */
    @Operation(
        method = "GET",
        description = "Looks up an organization by name",
        parameters = @Parameter(
            name = "name",
            in = ParameterIn.PATH,
            description = "The organization name",
            example = "ExampleCompany"
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "The registered Organization",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        implementation = Organization.class,
                        description = "The Organization comprised of it's id and registration info"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "No found organization"
            )
        }
    )
    ServiceCall<NotUsed, Organization> organization(String name);


    @Operation(
        method = "GET",
        description = "Looks up an organization by OrganizationID",
        parameters = @Parameter(
            name = "name",
            in = ParameterIn.PATH,
            description = "The organization ID",
            example = ""
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "The registered Organization",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        implementation = Organization.class,
                        description = "The Organization comprised of it's id and registration info"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "No found organization"
            )
        }
    )
    ServiceCall<NotUsed, Organization> getOrganization(OrganizationId orgId);

    /**
     * Creates a new {@link Organization} via {@link OrganizationRegistration registration object}
     * to facilitate unmodifiable and persistent infomration of the organization.
     * @return
     */
    @Operation(
        method = "POST",
        summary = "Submit a request to register the OrganizationRegistration object as a new Organization in the service",
        description = "Submit a request to create a new Organization",
        requestBody = @RequestBody(
            description = "The organization registration object",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = OrganizationRegistration.class),
                examples = @ExampleObject(
                    name = "ExampleCompany",
                    summary = "An OrganizationRegistration object for ExampleCompany",
                    value = "{\"name\":\"ExampleCompany\",\"address\":\"123 Sample Blvd. Representation City, Object\",\"phoneNumber\":\"123-456-7890\"}"
                )
            ),
            required = true
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "The created and registered Organization",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        implementation = Organization.class,
                        description = "The created and registered organization."
                    ),
                    examples = {
                        @ExampleObject(
                            name = "ExampleCompany",
                            summary = "The returned registered organization object",
                            value = "{\"name\":\"ExampleCompany\",\"address\":\"123 Sample Blvd. Representation City, Object\",\"phoneNumber\":\"123-456-7890\"}"
                        )
                    }
                )
            )
        }
    )
    ServiceCall<OrganizationRegistration, Organization> createOrganization();

    /**
     * Gets all registered {@link Organization Organizations}.
     *
     * @return The organizations
     */
    @Operation(
        method = "GET",
        summary = "Gets all registered organizations",
        description = "Requests from storage all registered Organizations within this service",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "The set of organizations and their data registered within this service",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                        schema = @Schema(implementation = Organization.class)
                    )
                )
            )
        }

    )
    ServiceCall<NotUsed, POrderedSet<Organization>> getOrganizations();

    @Override
    default Descriptor descriptor() {
        return this.withOpenAPI(Service.named("organization")
            .withCalls(
                Service.pathCall("/api/organization/", this::createOrganization),
                Service.pathCall("/api/organization/:name", this::organization),
                Service.pathCall("/api/organization/", this::getOrganizations),
                Service.restCall(Method.POST, "/api/organization/:orgId", this::getOrganization)
            )
            .withPathParamSerializer(UUID.class, PathParamSerializers.required("UUID", UUID::fromString, UUID::toString))
            .withPathParamSerializer(OrganizationId.class, PathParamSerializers.required("OrganizationId", OrganizationId::fromString, OrganizationId::toString))
            .withAutoAcl(true)
        );
    }

}
