package com.gabizou.cameraiq.demo.impl;

import com.datastax.driver.core.Row;
import com.gabizou.cameraiq.demo.api.User;
import com.gabizou.cameraiq.demo.api.UserRegistration;
import com.gabizou.cameraiq.demo.util.DemoFunctional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.pcollections.POrderedSet;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

@Singleton
public class UserRepository {

    private static final String USER_TABLE_NAME = "user_data";
    private static final String USER_ID_COLUMN = "user_id";
    private static final String USER_FIRST_NAME = "user_first_name";
    private static final String USER_LAST_NAME = "user_last_name";
    private static final String USER_EMAIL = "user_email";
    private static final String USER_PHONE_NUMBER = "user_phone_number";
    private static final String USER_ADDRESS = "user_address";
    private static final String CREATE_USER_TABLE = "CREATE TABLE " + UserRepository.USER_TABLE_NAME + "("
                                                   + UserRepository.USER_ID_COLUMN + " uuid PRIMARY KEY, "
                                                   + UserRepository.USER_FIRST_NAME + " text, "
                                                   + UserRepository.USER_LAST_NAME + " text, "
                                                   + UserRepository.USER_EMAIL + " text, "
                                                   + UserRepository.USER_ADDRESS + " text, "
                                                   + UserRepository.USER_PHONE_NUMBER + " text"
                                                   + ");";
    // SELECT FROM user_data WHERE user_id=someUUID-00123-30ba-ac345;
    private static final String SELECT_USER_BY_UUID = "SELECT FROM "
                                                     + UserRepository.USER_TABLE_NAME
                                                     + " WHERE " + UserRepository.USER_ID_COLUMN + "=";
    private static final String CREATE_USER = "INSERT INTO " + UserRepository.USER_TABLE_NAME + "("
                                             + UserRepository.USER_ID_COLUMN + ", "
                                             + UserRepository.USER_FIRST_NAME + ", "
                                             + UserRepository.USER_LAST_NAME + ", "
                                             + UserRepository.USER_EMAIL + ", "
                                             + UserRepository.USER_ADDRESS + ", "
                                             // Note that the UUID is stored strictly without ' quotes
                                             // because CQL's statements require that UUID types are not quoted, like strings
                                             // blame the interpreter....
                                             + UserRepository.USER_PHONE_NUMBER + ") VALUES (?, '?', '?', '?', '?', '?');";


    private final CassandraSession session;

    @Inject
    public UserRepository(final CassandraSession session) {
        this.session = session;
        this.session.executeCreateTable(UserRepository.CREATE_USER_TABLE);
    }



    CompletionStage<User> lookupUser(final UUID uuid) {
        return this.session.selectOne(UserRepository.SELECT_USER_BY_UUID + uuid.toString())
            .thenApplyAsync(row -> row.map(UserRepository::getUserFromRow)
                .orElseThrow(() -> new IllegalStateException("Missing user-data for id: " + uuid)));
    }

    CompletionStage<User> saveUser(final User user) {
        return this.session.prepare(UserRepository.CREATE_USER)
            .thenApply(statement -> statement.bind()
                .setUUID(UserRepository.USER_ID_COLUMN, user.userId.uuid)
                .setString(UserRepository.USER_FIRST_NAME, user.info.firstName)
                .setString(UserRepository.USER_LAST_NAME, user.info.lastName)
                .setString(UserRepository.USER_EMAIL, user.info.email)
                .setString(UserRepository.USER_PHONE_NUMBER, user.info.phoneNumber)
                .setString(UserRepository.USER_ADDRESS, user.info.address)
            ).thenApply(this.session::executeWrite)
            .thenApply(done -> user);
    }

    CompletionStage<POrderedSet<User>> getUsers() {
        return this.session.selectAll("SELECT * FROM user." + UserRepository.USER_TABLE_NAME)
            .thenApply(rows -> rows.stream()
                .map(UserRepository::getUserFromRow)
                .collect(DemoFunctional.toImmutableSet()));
    }

    private static User getUserFromRow(Row userRow) {
        final UUID userId = userRow.getUUID(UserRepository.USER_ID_COLUMN);
        final String firstName = userRow.getString(UserRepository.USER_FIRST_NAME);
        final String lastName = userRow.getString(UserRepository.USER_LAST_NAME);
        final String email = userRow.getString(UserRepository.USER_EMAIL);
        final String address = userRow.getString(UserRepository.USER_ADDRESS);
        final String phoneNumber = userRow.getString(UserRepository.USER_PHONE_NUMBER);
        final UserRegistration userInfo = new UserRegistration(firstName, lastName, address, email, phoneNumber);
        return new User(userId, userInfo);
    }

}
