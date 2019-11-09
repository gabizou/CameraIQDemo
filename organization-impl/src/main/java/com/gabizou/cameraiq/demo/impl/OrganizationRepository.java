package com.gabizou.cameraiq.demo.impl;

import com.datastax.driver.core.Row;
import com.gabizou.cameraiq.demo.api.Organization;
import com.gabizou.cameraiq.demo.api.OrganizationRegistration;
import com.google.inject.Inject;
import com.google.inject.Singleton;
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
public class OrganizationRepository {

    public static final String ORG_TABLE_NAME = "org_data";
    public static final String ORG_ID_COLUMN = "org_id";
    public static final String ORG_NAME = "org_name";
    public static final String ORG_PHONE_NUMBER = "org_phone_number";
    public static final String ORG_ADDRESS = "org_address";
    public static final String CREATE_ORG_TABLE = "CREATE TABLE " + OrganizationRepository.ORG_TABLE_NAME + "("
                                                  + OrganizationRepository.ORG_ID_COLUMN + " uuid PRIMARY KEY, "
                                                  + OrganizationRepository.ORG_NAME + " text, "
                                                  + OrganizationRepository.ORG_ADDRESS + " text, "
                                                  + OrganizationRepository.ORG_PHONE_NUMBER + " text"
                                                  + ");";
    // SELECT FROM org_data WHERE org_id=someUUID-00123-30ba-ac345;
    public static final String SELECT_ORG_BY_UUID = "SELECT FROM "
                                                     + OrganizationRepository.ORG_TABLE_NAME
                                                     + " WHERE " + OrganizationRepository.ORG_ID_COLUMN + "=";
    public static final String CREATE_ORG = "INSERT INTO " + OrganizationRepository.ORG_TABLE_NAME + "("
                                            + OrganizationRepository.ORG_ID_COLUMN + ", "
                                            + OrganizationRepository.ORG_NAME + ", "
                                            + OrganizationRepository.ORG_ADDRESS + ", "
                                            // Note that the UUID is stored strictly without ' quotes
                                            // because CQL's statements require that UUID types are not quoted, like strings
                                            // blame the interpreter....
                                            + OrganizationRepository.ORG_PHONE_NUMBER + ") VALUES (%s, '%s', '%s', '%s');";


    final CassandraSession session;

    @Inject
    public OrganizationRepository(final CassandraSession session) {
        this.session = session;
        this.session.executeCreateTable(OrganizationRepository.CREATE_ORG_TABLE);
    }



    public CompletionStage<Organization> lookupOrg(final UUID uuid) {
        return this.session.selectOne(OrganizationRepository.SELECT_ORG_BY_UUID + uuid.toString())
            .thenApplyAsync(row -> {
                if (!row.isPresent()) {
                    throw new IllegalStateException("Missing data for uuid: " + uuid);
                }
                return OrganizationRepository.getOrgFromRow(row.get());
            });
    }

    public Organization saveOrganization(final Organization org) {
        final String
            query =
            String
                .format(OrganizationRepository.CREATE_ORG, org.uuid.toString(), org.info.name, org.info.address, org.info.phoneNumber);
        this.session.executeWrite(query);
        return org;
    }

    public CompletionStage<POrderedSet<Organization>> getOrganizations() {
        return this.session.selectAll("SELECT * FROM organization." + OrganizationRepository.ORG_TABLE_NAME)
            .thenApply(rows -> {
                final ArrayList<Organization> orgs = new ArrayList<>();
                for (Row orgRow : rows) {
                    final Organization org = OrganizationRepository.getOrgFromRow(orgRow);
                    orgs.add(org);
                }
                // For some reason, the compiler does not like trying to use streams with a mapper and collector.
                // Complains something about generics which, honestly, the above function is
                // doing the same thing, just not parallelized.
                return OrderedPSet.from(orgs);
//                return rows.stream()
//                    .map(OrganizationRepository::getOrgFromRow)
//                    .collect(Collector.of(ArrayList::new, Collection::add, (left, right) -> {
//                        left.addAll(right);
//                        return left;
//                    }, OrderedPSet::from));
            });
    }

    private static Organization getOrgFromRow(Row orgRow) {
        final UUID orgId = orgRow.getUUID(0);
        final String name = orgRow.getString(1);
        final String address = orgRow.getString(2);
        final String phoneNumber = orgRow.getString(3);
        final OrganizationRegistration orgInfo = new OrganizationRegistration(name, address, phoneNumber);
        return new Organization(orgId, orgInfo);
    }

    public static Collector<Organization, List<Organization>, OrderedPSet<Organization>> toImmutableSet() {
        return Collector.of(ArrayList::new, Collection::add, (left, right) -> {
            left.addAll(right);
            return left;
        }, OrderedPSet::from);
    }

}
