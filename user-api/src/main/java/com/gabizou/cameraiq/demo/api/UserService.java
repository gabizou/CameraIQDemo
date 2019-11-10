package com.gabizou.cameraiq.demo.api;

import akka.NotUsed;
import akka.japi.Pair;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.deser.PathParamSerializers;
import com.lightbend.lagom.javadsl.api.transport.Method;
import org.pcollections.POrderedSet;

import java.util.Set;
import java.util.UUID;

/**
 * The USer service interface.
 *
 * <p>Describes all functions that Lagom needs to know about to
 * serve and consume {@link User}.</p>
 */
public interface UserService extends Service {

    /**
     * Gets a single {@link User user} by {@link UserId uuid}.
     *
     * @param uuid
     * @return
     */
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
    ServiceCall<UserRegistration, User> createUser();

    /**
     * Gets a list of all users.
     *
     * @return The list of all users
     */
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
        return Service.named("users")
            .withCalls(
                Service.restCall(Method.POST, "/api/user/", this::createUser),
                Service.restCall(Method.GET, "/api/user/:uuid", this::lookupUser),
                Service.restCall(Method.GET, "/api/user/", this::getUsers),
                Service.restCall(Method.POST, "/api/user/", this::getUsersByIds)
            )
            .withPathParamSerializer(UserId.class, PathParamSerializers.required("UserId", UserId::fromString, UserId::toString))
            .withAutoAcl(true);
    }
}
