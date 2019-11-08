package com.gabizou.cameraiq.demo.impl.repo;

import com.datastax.driver.core.Row;
import com.gabizou.cameraiq.demo.api.User;
import com.gabizou.cameraiq.demo.api.UserRegistration;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.pcollections.OrderedPSet;
import org.pcollections.POrderedSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collector;

@Singleton
public class UserRepository {

    public static final String SELECT_ALL_USERS =
        "SELECT NEW com.gabizou.cameraiq.demo.api.User(g.id, g.firstName, g" +
            ".lastName, g.email, g.address, g.phoneNumber) FROM UserJpaEntity g";
    public static final String USER_TABLE_NAME = "user_data";
    public static final String USER_ID_COLUMN = "user_id";
    public static final String USER_FIRST_NAME = "user_first_name";
    public static final String USER_LAST_NAME = "user_last_name";
    public static final String USER_EMAIL = "user_email";
    public static final String USER_PHONE_NUMBER = "user_phone_number";
    public static final String USER_ADDRESS = "user_address";
    public static final String CREATE_USER_TABLE = "CREATE TABLE " + UserRepository.USER_TABLE_NAME + "("
                                                   + UserRepository.USER_ID_COLUMN + " uuid PRIMARY KEY, "
                                                   + UserRepository.USER_FIRST_NAME + " text, "
                                                   + UserRepository.USER_LAST_NAME + " text, "
                                                   + UserRepository.USER_EMAIL + " text, "
                                                   + UserRepository.USER_ADDRESS + " text, "
                                                   + UserRepository.USER_PHONE_NUMBER + " text"
                                                   + ");";
    // SELECT FROM user_data WHERE user_id=someUUID-00123-30ba-ac345;
    public static final String SELECT_USER_BY_UUID = "SELECT FROM "
                                                     + UserRepository.USER_TABLE_NAME
                                                     + " WHERE " + UserRepository.USER_ID_COLUMN + "=";
    public static final String CREATE_USER = "INSERT INTO " + UserRepository.USER_TABLE_NAME + "("
                                             + UserRepository.USER_ID_COLUMN + ", "
                                             + UserRepository.USER_FIRST_NAME + ", "
                                             + UserRepository.USER_LAST_NAME + ", "
                                             + UserRepository.USER_EMAIL + ", "
                                             + UserRepository.USER_ADDRESS + ", "
                                             // Note that the UUID is stored strictly without ' quotes
                                             // because CQL's statements require that UUID types are not quoted, like strings
                                             // blame the interpreter....
                                             + UserRepository.USER_PHONE_NUMBER + ") VALUES (%s, '%s', '%s', '%s', '%s', '%s');";


    final CassandraSession session;
    final PersistentEntityRegistry registry;

    @Inject
    public UserRepository(final CassandraSession session, final PersistentEntityRegistry registry) {
        this.session = session;
        this.registry = registry;
        this.session.executeCreateTable(UserRepository.CREATE_USER_TABLE);
    }



    public CompletionStage<User> lookupUser(final UUID uuid) {
        return this.session.selectOne(UserRepository.SELECT_USER_BY_UUID + uuid.toString())
            .thenApplyAsync(row -> {
                if (!row.isPresent()) {
                    throw new IllegalStateException("Missing data for uuid: " + uuid);
                }
                return UserRepository.getUserFromRow(row.get());
            });
    }

    public User saveUser(final User user) {
        final String
            query =
            String
                .format(UserRepository.CREATE_USER, user.uuid.toString(), user.info.firstName, user.info.lastName, user.info.email, user.info.address,
                    user.info.phoneNumber);
        this.session.executeWrite(query);
        return user;
    }

    public CompletionStage<POrderedSet<User>> getUsers() {
        return this.session.selectAll("SELECT * FROM user." + UserRepository.USER_TABLE_NAME)
            .thenApply(rows -> {
                final ArrayList<User> users = new ArrayList<>();
                for (Row userRow : rows) {
                    final User user = UserRepository.getUserFromRow(userRow);
                    users.add(user);
                }
                // For some reason, the compiler does not like trying to use streams with a mapper and collector.
                // Complains something about generics which, honestly, the above function is
                // doing the same thing, just not parallelized.
                return OrderedPSet.from(users);
//                return rows.stream()
//                    .map(UserRepository::getUserFromRow)
//                    .collect(Collector.of(ArrayList::new, Collection::add, (left, right) -> {
//                        left.addAll(right);
//                        return left;
//                    }, OrderedPSet::from));
            });
    }

    private static User getUserFromRow(Row userRow) {
        final UUID userId = userRow.getUUID(0);
        final String firstName = userRow.getString(1);
        final String lastName = userRow.getString(2);
        final String email = userRow.getString(3);
        final String address = userRow.getString(4);
        final String phoneNumber = userRow.getString(5);
        final UserRegistration userInfo = new UserRegistration(firstName, lastName, address, email, phoneNumber);
        return new User(userId, userInfo);
    }

    public static Collector<User, List<User>, OrderedPSet<User>> toImmutableSet() {
        return Collector.of(ArrayList::new, Collection::add, (left, right) -> {
            left.addAll(right);
            return left;
        }, OrderedPSet::from);
    }

}
