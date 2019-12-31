package com.gabizou.demo

import java.time.Instant

import com.gabizou.demo.api.UserId
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, AggregateEventTagger}

sealed trait UserEvent extends AggregateEvent[UserEvent] {
  override final def aggregateTag: AggregateEventTagger[UserEvent] = UserEvent.aggregate
}

object UserEvent {
  implicit val aggregate: AggregateEventTagger[UserEvent] = AggregateEventTag.sharded(20)
}

/**
  * A simplified event marking the creation of a [[User]] within the system,
  * but barring any specific data of the user, only recording the [[UUID]] and
  * [[Instant]]. This event allows for us to facilitate easy user data
  * deletion requests without affecting the event journal.
  */
case class UserCreated(val userId: UserId, val timestamp: Instant) extends UserEvent

case class DeletedUser(val userId: UserId, val timestamp: Instant) extends UserEvent