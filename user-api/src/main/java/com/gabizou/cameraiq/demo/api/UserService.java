package com.gabizou.cameraiq.demo.api;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.deser.PathParamSerializers;
import com.lightbend.lagom.javadsl.api.transport.Method;
import org.pcollections.POrderedSet;

import java.util.UUID;

/**
 * The USer service interface.
 *
 * <p>Describes all functions that Lagom needs to know about to
 * serve and consume {@link User}.</p>
 */
public interface UserService extends Service {

    /**
     * Gets a single {@link User user} by {@link UUID uuid}.
     *
     * @param uuid
     * @return
     */
    ServiceCall<NotUsed, User> lookupUser(UUID uuid);

    /**
     * Creates a single {@link User}.
     *
     * @return The user object corresponding to the new or existing
     * registration. If
     * if already
     * registered or newly
     * created
     */
    ServiceCall<UserRegistration, User> createUser();

    /**
     * Gets a list of all users.
     *
     * @return The list of all users
     */
    ServiceCall<NotUsed, POrderedSet<User>> getUsers();

    @Override
    default Descriptor descriptor() {
        return Service.named("users")
            .withCalls(
                Service.pathCall("/api/user/", this::createUser),
                Service.pathCall("/api/user/:id", this::lookupUser),
                Service.restCall(Method.GET, "/api/user/", this::getUsers)
            )
            .withPathParamSerializer(UUID.class, PathParamSerializers.required("UUID", UUID::fromString, UUID::toString))
            .withAutoAcl(true);
    }
}
