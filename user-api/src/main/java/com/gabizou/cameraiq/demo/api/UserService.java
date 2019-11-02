package com.gabizou.cameraiq.demo.api;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.api.broker.kafka.KafkaProperties;
import com.lightbend.lagom.javadsl.api.deser.PathParamSerializers;
import org.pcollections.PSequence;

import javax.annotation.Nullable;
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
     * @param id
     * @return
     */
    ServiceCall<NotUsed, User> user(String id);

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

    ServiceCall<NotUsed, PSequence<User>> getUsers(@Nullable Integer pageNo, @Nullable Integer pageSize);

    @Override
    default Descriptor descriptor() {
        return Service.named("users")
            .withCalls(
                    Service.pathCall("/api/user/", this::createUser),
                Service.pathCall("/api/user/:id", this::user),
                Service.pathCall("/api/user?pageNo&pageSize", this::getUsers)
            )
            .withPathParamSerializer(UUID.class, PathParamSerializers.required("UUID", UUID::fromString, UUID::toString))
            .withAutoAcl(true);
    }
}
