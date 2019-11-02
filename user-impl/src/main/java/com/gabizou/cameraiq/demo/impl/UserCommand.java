package com.gabizou.cameraiq.demo.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gabizou.cameraiq.demo.api.User;
import com.gabizou.cameraiq.demo.api.UserRegistration;
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
        public final UserRegistration registration;

        @JsonCreator
        public CreateUser(final UserRegistration registration) {
            this.registration = registration;
        }

    }


}
