package com.gabizou.cameraiq.demo.impl;

import akka.Done;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gabizou.cameraiq.demo.api.User;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;

/**
 * Defines all commands that User entities support.
 *
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
    final class UpdateUser implements UserCommand, CompressedJsonable,
        PersistentEntity.ReplyType<User> {
        public final User user;

        @JsonCreator
        public UpdateUser(final User user) {
            this.user = user;
        }
    }

    @JsonDeserialize
    final class RemoveUser implements UserCommand, CompressedJsonable,
        PersistentEntity.ReplyType<Done> {
        public final User removing;

        @JsonCreator
        public RemoveUser(final User removing) {
            this.removing = removing;
        }
    }


}
