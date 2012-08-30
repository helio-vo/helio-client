package eu.heliovo.clientapi.query;

import eu.heliovo.registryclient.HelioServiceName;

public interface WhereClauseFactoryBean {

    /**
     * Create a new where clause for a given service and catalog name.
     * @param helioServiceName the service name.
     * @param listName the list name.
     * @return the created where clause or an empty where clause if the catalog does not support where clauses.
     */
    public WhereClause createWhereClause(HelioServiceName helioServiceName, String listName);

}