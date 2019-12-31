package com.gabizou.demo.impl

import com.datastax.driver.core.Row
import com.gabizou.demo.api.{User, UserId, UserRegistration}
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession
import org.apache.log4j.LogManager

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class UserRepository(val session: CassandraSession) {
  session.executeCreateTable(UserRepository.CREATE_USER_TABLE)
    .onComplete {
      case Success(_) => UserRepository.LOGGER.debug("Completed UserRepository table creation")
      case Failure(exception) => UserRepository.LOGGER.warn("Failed to create table, %s", exception)
    }(ExecutionContext.global)

  def lookupUser(userId: UserId): Future[User] = {
    session.selectOne(UserRepository.SELECT_USER_BY_UUID + userId.userId.toString)
      .map {
        case Some(value) => UserRepository.getUserFromRow(value)
        case _ => throw new IllegalStateException("Missing user-data for id: " + userId)
      }(UserRepository.executor)
  }

  def saveUser(user: User): Future[User] = {
    session.prepare(UserRepository.CREATE_USER)
      .map(f => {
        val statement = f.bind()
            .setUUID(UserRepository.USER_ID_COLUMN, user.userId.userId)
          .setString(UserRepository.USER_LAST_NAME, user.info.lastName)
          .setString(UserRepository.USER_EMAIL, user.info.email)
          .setString(UserRepository.USER_PHONE_NUMBER, user.info.phoneNumber)
          .setString(UserRepository.USER_ADDRESS, user.info.address)
        UserRepository.LOGGER.debug("Prepared bound statement: " + statement.preparedStatement.getQueryString)
        statement
      })(UserRepository.executor)
      .map(stmnt => session.executeWrite(stmnt))(UserRepository.executor)
      .map(_ => {
        UserRepository.LOGGER.debug("Completed save of user: " + user)
        user
      })(UserRepository.executor)
  }

  def getUsers: Future[Set[User]] = {
    session.selectAll("SELECT * FROM user." + UserRepository.USER_TABLE_NAME)
      .map(row => row.toSet.map(UserRepository.getUserFromRow))(UserRepository.executor)
  }
}

object UserRepository {
  def getUserFromRow(row: Row): User = {
    LOGGER.debug("Converting Found Row into User: " + row)
    val userId = row.getUUID(UserRepository.USER_ID_COLUMN)
    val firstName = row.getString(UserRepository.USER_FIRST_NAME)
    val lastName = row.getString(USER_LAST_NAME)
    val email = row.getString(USER_EMAIL)
    val address = row.getString(USER_ADDRESS)
    val phoneNumber = row.getString(USER_PHONE_NUMBER)
    val registration = new UserRegistration(firstName, lastName, email, address, phoneNumber)
    val user = new User(new UserId(userId), registration)
    LOGGER.debug("Converted User: " + user)
    user
  }

  private val LOGGER = LogManager.getLogger("com.gabizou.services." + classOf[UserRepository].getSimpleName)

  private val USER_TABLE_NAME = "user_data"
  private val USER_ID_COLUMN = "user_id"
  private val USER_FIRST_NAME = "user_first_name"
  private val USER_LAST_NAME = "user_last_name"
  private val USER_EMAIL = "user_email"
  private val USER_PHONE_NUMBER = "user_phone_number"
  private val USER_ADDRESS = "user_address"
  private val CREATE_USER_TABLE = "CREATE TABLE " + UserRepository.USER_TABLE_NAME + "(" + UserRepository.USER_ID_COLUMN + " uuid PRIMARY KEY, " + UserRepository.USER_FIRST_NAME + " text, " + UserRepository.USER_LAST_NAME + " text, " + UserRepository.USER_EMAIL + " text, " + UserRepository.USER_ADDRESS + " text, " + UserRepository.USER_PHONE_NUMBER + " text" + ");"
  // SELECT FROM user_data WHERE user_id=someUUID-00123-30ba-ac345;
  private val SELECT_USER_BY_UUID = "SELECT * FROM " + UserRepository.USER_TABLE_NAME + " WHERE " + UserRepository.USER_ID_COLUMN + "="
  private val CREATE_USER = "INSERT INTO " + UserRepository.USER_TABLE_NAME + "(" + UserRepository.USER_ID_COLUMN + ", " + UserRepository.USER_FIRST_NAME + ", " + UserRepository.USER_LAST_NAME + ", " + UserRepository.USER_EMAIL + ", " + UserRepository.USER_ADDRESS + ", " + // Note that the UUID is stored strictly without ' quotes
    // because CQL's statements require that UUID types are not quoted, like strings
    // blame the interpreter....
    UserRepository.USER_PHONE_NUMBER + ") VALUES (?, ?, ?, ?, ?, ?);"
  implicit val executor = ExecutionContext.global


}
