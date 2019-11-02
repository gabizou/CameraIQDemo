package com.gabizou.cameraiq.demo.impl;

import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.serialization.Jsonable;

public interface UserEvent extends Jsonable, AggregateEvent<UserEvent> {

    String getName();



}
