package com.gabizou.cameraiq.demo.impl;

import akka.Done;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gabizou.cameraiq.demo.api.User;
import com.gabizou.cameraiq.demo.api.UserId;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;
import org.pcollections.POrderedSet;

/**
 * Defines all commands that User entities support.
 * <p>
 * By convention, the commands are inner classes of
 * this interface, since it's easier to see the overall
 * picture of what commands are supported, namely:
 * <ul>
 *     <li>Changing username</li>
 *     <li>Changing email</li>
 *     <li>Changing name</li>
 *     <li>Changing phone number</li>
 *     <li>Changing password</li>
 * </ul>
 */
public interface UserCommand extends Jsonable {

    @JsonDeserialize
    final class CreateUser implements UserCommand, CompressedJsonable,
        PersistentEntity.ReplyType<User> {
        public final User registration;

        @JsonCreator
        public CreateUser(final User registration) {
            this.registration = registration;
        }
    }

    @JsonDeserialize
    public class GetUser implements UserCommand, CompressedJsonable,
        PersistentEntity.ReplyType<Done> {

        public final UserId uuid;

        @JsonCreator
        public GetUser(final UserId uuid) {
            this.uuid = uuid;
        }
    }

    @JsonDeserialize
    public class GetAllUsers implements UserCommand, CompressedJsonable,
        PersistentEntity.ReplyType<POrderedSet<User>> {

    }
}
