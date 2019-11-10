package com.gabizou.cameraiq.demo.impl;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Row;
import com.gabizou.cameraiq.demo.api.Organization;
import com.gabizou.cameraiq.demo.api.OrganizationId;
import com.gabizou.cameraiq.demo.api.OrganizationRegistration;
import com.gabizou.cameraiq.demo.util.DemoFunctional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.pcollections.POrderedSet;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

@Singleton
public class OrganizationRepository {

    private static final String ORG_TABLE_NAME = "org_data";
    private static final String ORG_ID_COLUMN = "org_id";
    private static final String ORG_NAME = "org_name";
    private static final String ORG_PHONE_NUMBER = "org_phone_number";
    private static final String ORG_ADDRESS = "org_address";
    private static final String CREATE_ORG_TABLE = "CREATE TABLE " + OrganizationRepository.ORG_TABLE_NAME + "("
                                                  + OrganizationRepository.ORG_ID_COLUMN + " uuid PRIMARY KEY, "
                                                  + OrganizationRepository.ORG_NAME + " text, "
                                                  + OrganizationRepository.ORG_ADDRESS + " text, "
                                                  + OrganizationRepository.ORG_PHONE_NUMBER + " text"
                                                  + ");";
    // SELECT FROM org_data WHERE org_id=someUUID-00123-30ba-ac345;
    private static final String SELECT_ORG_BY_UUID = "SELECT FROM "
                                                     + OrganizationRepository.ORG_TABLE_NAME
                                                     + " WHERE " + OrganizationRepository.ORG_ID_COLUMN + "=";
    private static final String CREATE_ORG = "INSERT INTO " + OrganizationRepository.ORG_TABLE_NAME + "("
                                            + OrganizationRepository.ORG_ID_COLUMN + ", "
                                            + OrganizationRepository.ORG_NAME + ", "
                                            + OrganizationRepository.ORG_ADDRESS + ", "
                                            // Note that the UUID is stored strictly without ' quotes
                                            // because CQL's statements require that UUID types are not quoted, like strings
                                            // blame the interpreter....
                                            + OrganizationRepository.ORG_PHONE_NUMBER + ") VALUES (?, '?', '?', '?');";

    private static final Logger LOGGER =
        LogManager.getLogger(OrganizationRepository.class);

    private final CassandraSession session;

    @Inject
    public OrganizationRepository(final CassandraSession session) {
        this.session = session;
        OrganizationRepository.LOGGER.debug("Executing create table query");
        this.session.executeCreateTable(OrganizationRepository.CREATE_ORG_TABLE);
    }



    CompletionStage<Organization> lookupOrg(final UUID uuid) {
        return this.session.selectOne(OrganizationRepository.SELECT_ORG_BY_UUID + uuid.toString())
            .thenApplyAsync(row -> row.map(OrganizationRepository::getOrgFromRow)
                .orElseThrow(() -> new IllegalStateException("Missing " +
                    "org-data for uuid: " + uuid)));
    }

    CompletionStage<Organization> saveOrganization(final Organization org) {
        return this.session.prepare(OrganizationRepository.CREATE_ORG)
            .thenApply(statement -> {
                    final BoundStatement boundStatement = statement.bind()
                        .setUUID(OrganizationRepository.ORG_ID_COLUMN, org.orgId.uuid)
                        .setString(OrganizationRepository.ORG_NAME, org.info.name)
                        .setString(OrganizationRepository.ORG_ADDRESS, org.info.address)
                        .setString(OrganizationRepository.ORG_PHONE_NUMBER, org.info.phoneNumber);
                    OrganizationRepository.LOGGER.debug("Prepared bound statement: " + boundStatement);
                    return boundStatement;
                }
            ).thenApply(this.session::executeWrite)
            .thenApply(done -> {
                OrganizationRepository.LOGGER.debug("Completed save of " +
                    "organization: " + org);
                return org;
            });
    }

    CompletionStage<POrderedSet<Organization>> getOrganizations() {
        return this.session.selectAll("SELECT * FROM organization." + OrganizationRepository.ORG_TABLE_NAME)
            .thenApply(rows -> rows.stream()
                .map(OrganizationRepository::getOrgFromRow)
                .collect(DemoFunctional.toImmutableSet()));
    }

    private static Organization getOrgFromRow(Row orgRow) {
        OrganizationRepository.LOGGER.debug("Converting found row into Organization:" + orgRow);
        final UUID orgId = orgRow.getUUID(OrganizationRepository.ORG_ID_COLUMN);
        final String name = orgRow.getString(OrganizationRepository.ORG_NAME);
        final String address = orgRow.getString(OrganizationRepository.ORG_ADDRESS);
        final String phoneNumber = orgRow.getString(OrganizationRepository.ORG_PHONE_NUMBER);
        final OrganizationRegistration orgInfo = new OrganizationRegistration(name, address, phoneNumber);
        final Organization organization = new Organization(new OrganizationId(orgId), orgInfo);
        OrganizationRepository.LOGGER.debug("Converted Organization: " + organization);
        return organization;
    }

}
