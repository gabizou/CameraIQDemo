package com.gabizou.demo.impl

import java.time.Instant

import akka.Done
import com.gabizou.demo.{UserCreated, UserEvent}
import com.gabizou.demo.api.{User, UserId}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import org.apache.log4j.LogManager

class UserEntity extends PersistentEntity {
  override type Command = UserCommand[_]
  override type Event = UserEvent
  override type State = UserState

  sealed trait UserState {
    def users: Set[UserId]
    def containsUser(userId: UserId): Boolean
  }
  case object EmptyState extends UserState {
    override def containsUser(userId: UserId): Boolean = false

    override def users: Set[UserId] = Set.empty
  }
  case class PopulatedState(set: Set[UserId]) extends UserState {
    override def containsUser(userId: UserId): Boolean = set.contains(userId)

    override def users: Set[UserId] = set
  }
  override def initialState: UserState = EmptyState

  override def behavior: Behavior = {
    case EmptyState => Actions().onCommand[CreateUser, User] {
      case (command: CreateUser, value, state) =>
        val id = command.registration.userId
        if (state.containsUser(id)) {
          UserEntity.LOGGER.warn("Duplicate user found by id: " + id)
          value.invalidCommand(s"User $id is already created")
          value.done
        }
        value.thenPersist(UserCreated(id, Instant.now)) { _ =>
          value.reply(command.registration)
        }
    }
        .onEvent {
          case (UserCreated(userId, _), state) =>
            UserEntity.LOGGER.debug("Updating User State with (" + state.users.size + 1 + ") users.")
            PopulatedState(state.users + userId)
        }
      .onReadOnlyCommand[GetUser, Done] {
      case(command:GetUser, value, state) =>
        val id = command.userId
        if (!state.containsUser(id)) {
          value.invalidCommand(s"User $id does not exist")
        }
        UserEntity.LOGGER.debug(s"Fetching registered user $id")
        value.reply(Done)
      }
  }
}
object UserEntity {
  private val LOGGER = LogManager.getLogger(classOf[UserEntity])

}

case class UserState(set: Set[UserId]) {

}