package eu.heliovo.clientapi.query;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import eu.heliovo.clientapi.config.AnnotatedBean;
import eu.heliovo.clientapi.config.HelioConfigurationManager;
import eu.heliovo.clientapi.model.service.AbstractServiceImpl;
import eu.heliovo.clientapi.query.delegate.QueryDelegateFactory;
import eu.heliovo.clientapi.query.paramquery.serialize.QuerySerializer;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.AccessInterfaceType;
import eu.heliovo.registryclient.ServiceCapability;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Default implementation for all query services. Subclasses may provide more
 * specialized access methods for the services.
 * 
 * @author MarcoSoldati
 * 
 */
public class BaseQueryServiceImpl extends AbstractServiceImpl implements QueryService, AnnotatedBean {
    /**
     * The logger instance
     */
    static final Logger _LOGGER = Logger.getLogger(BaseQueryServiceImpl.class);

    /**
     * The default properties of this class. They will be returned by
     * getPropertyNames()
     */
    private static String[] DEFAULT_PROPERTY_NAMES = new String[] { "from", "startTime", "endTime", "where", "join",
            "maxRecords", "startIndex" };

    /**
     * the from clause
     */
    private List<String> from;

    /**
     * The start date
     */
    private List<String> startTime;

    /**
     * The end date
     */
    private List<String> endTime;

    /**
     * The where clause
     */
    //private String where;

    /**
     * The join clause
     */
    private String join;

    /**
     * Max number of records
     */
    private Integer maxRecords;

    /**
     * The start index
     */
    private Integer startIndex;

    /**
     * Type of the query. Defaults to sync_query.
     */
    private transient QueryType queryType = QueryType.SYNC_QUERY;

    /**
     * type of the query method to be called. defaults to full query.
     */
    private transient QueryMethodType queryMethodType = QueryMethodType.FULL_QUERY;

    /**
     * The manager to read the properties for a specific service.
     */
    private transient HelioConfigurationManager configurationManager;

    /**
     * Keep the records for this service call.
     */
    private transient final List<LogRecord> logRecords = new ArrayList<LogRecord>();

    /**
     * Factory that creates the query delegate depending on the query type.
     */
    private transient QueryDelegateFactory queryDelegateFactory;

    /**
     * Pointer to the factory bean to create new where clauses.
     */
    private transient WhereClauseFactoryBean whereClauseFactoryBean;
    
    /**
     * The query serializer bean
     */
    private transient QuerySerializer querySerializer; 
    
    /**
     * Store the BeanInfo for this service instance. assigned in the init method
     */
    private transient BeanInfo beanInfo = null;

    /**
     * hold the where-clauses
     */
    private List<WhereClause> whereClauses = new ArrayList<WhereClause>();

    /**
     * Cache where clauses that have been removed. 
     */
    private transient Map<String, WhereClause> whereClauseCache = new HashMap<String, WhereClause>();
    
    /**
     * Initialize this object.
     */
    @Override
    public void init() {
        String[] propertyNames = getPropertyNames();
        List<PropertyDescriptor> descriptors = new ArrayList<PropertyDescriptor>();
        for (int i = 0; i < propertyNames.length; i++) {
            String propName = propertyNames[i];
            PropertyDescriptor descriptor = configurationManager.getPropertyDescriptor(this, propName);
            if (descriptor == null) {
                _LOGGER.warn("Unable to find descriptor for property '"
                        + propName
                        + "'. Ignoring property in description. "
                        + "This may be a problem for clients that use bean introspection to detect available bean properties.");
            } else {
                descriptors.add(descriptor);
            }
        }
        this.beanInfo = new BeanInfo(descriptors.toArray(new PropertyDescriptor[descriptors.size()]));
    }

    @Override
    public HelioQueryResult query(String startTime, String endTime, String from, Integer maxrecords,
            Integer startindex, String join) throws JobExecutionException, IllegalArgumentException {
        HelioQueryResult result = query(Collections.singletonList(startTime), Collections.singletonList(endTime),
                Collections.singletonList(from), maxrecords, startindex, join);
        return result;
    }

    @Override
    public HelioQueryResult query(List<String> startTime, List<String> endTime, List<String> from, 
            Integer maxrecords, Integer startindex, String join) throws JobExecutionException, IllegalArgumentException {
        setQueryMethodType(QueryMethodType.FULL_QUERY);
        setStartTime(startTime);
        setEndTime(endTime);
        setFrom(from);
        setMaxRecords(maxrecords);
        setStartIndex(startindex);
        setJoin(join);
        return execute();
    }

    @Override
    public HelioQueryResult timeQuery(String startTime, String endTime, String from, Integer maxrecords,
            Integer startindex) throws JobExecutionException, IllegalArgumentException {
        HelioQueryResult result = timeQuery(Collections.singletonList(startTime), Collections.singletonList(endTime),
                Collections.singletonList(from), maxrecords, startindex);
        return result;
    }

    @Override
    public HelioQueryResult timeQuery(List<String> startTime, List<String> endTime, List<String> from,
            Integer maxrecords, Integer startindex) throws JobExecutionException, IllegalArgumentException {
        setQueryMethodType(QueryMethodType.TIME_QUERY);
        setStartTime(startTime);
        setEndTime(endTime);
        setFrom(from);
        setMaxRecords(maxrecords);
        setStartIndex(startindex);
        return execute();
    }

    @Override
    public HelioQueryResult execute() {
        final long jobStartTime = System.currentTimeMillis();
        validateInput();

        Set<AccessInterface> triedInterfaces = new HashSet<AccessInterface>();

        // get the delegate depending on the queryType and queryMethodType
        QueryDelegate queryDelegate = queryDelegateFactory.getDelegate(queryType, queryMethodType);

        while (true) {
            // get the end point
            final AccessInterface bestAccessInterface = getBestAccessInterface(queryDelegate);
            if (bestAccessInterface == null) {
                throw new JobExecutionException("No viable access interface found. " + Arrays.toString(accessInterfaces));
            }
            if (!triedInterfaces.add(bestAccessInterface)) {
                throw new JobExecutionException("All registered remote services are unavailable. Tried to access: "
                        + triedInterfaces.toString());
            }
            ;

            // log the call.
            String callId = bestAccessInterface.getUrl() + "::" + queryDelegate.getMethodName();
            logRecords.add(new LogRecord(Level.INFO, "Connecting to " + callId));

            StringBuilder message = new StringBuilder();
            message.append("Executing 'result=").append(queryDelegate.getMethodName()).append("(");
            message.append("startTime=").append(startTime);
            message.append(", ").append("endTime=").append(endTime);
            message.append(", ").append("from=").append(from);
            message.append(", ").append("where=").append(getWhere());
            message.append(", ").append("maxrecords=").append(maxRecords);
            message.append(", ").append("startIndex=").append(startIndex);
            message.append(", ").append("join=").append(join);
            // message.append(", ").append("saveTo=").append(saveTo);
            message.append(")'");

            _LOGGER.info(message.toString());
            logRecords.add(new LogRecord(Level.INFO, message.toString()));

            // do the call
            try {
                return queryDelegate.callWebService(this, bestAccessInterface);
            } catch (WebServiceException e) {
                // get port fails
                String msg = "Timeout occurred on " + bestAccessInterface.getUrl() + ". Trying to failover.";
                logRecords.add(new LogRecord(Level.INFO, msg));
                _LOGGER.info(msg);
                loadBalancer.updateAccessTime(bestAccessInterface, -1);
            } catch (JobExecutionException e) { // handle timeout
                if (e.getCause() instanceof TimeoutException) {
                    String msg = "Timeout occurred on " + bestAccessInterface.getUrl() + ". Trying to failover.";
                    logRecords.add(new LogRecord(Level.INFO, msg));
                    _LOGGER.info(msg);
                    loadBalancer.updateAccessTime(bestAccessInterface, -1);
                } else {
                    throw e;
                }
            } finally {
                long executionDuration = System.currentTimeMillis() - jobStartTime;
                loadBalancer.updateAccessTime(bestAccessInterface, executionDuration);
            }
        }
    }

    @Override
    public boolean supportsCapability(ServiceCapability capability) {
        return capability == ServiceCapability.SYNC_QUERY_SERVICE
                || capability == ServiceCapability.ASYNC_QUERY_SERVICE;
    }

    @Override
    public void setAccessInterfaces(AccessInterface... accessInterfaces) {
        for (AccessInterface accessInterface : accessInterfaces) {
            if (!hasSyncOrAsyncCapabilty(accessInterface)) {
                throw new IllegalArgumentException("AccessInterface.Capability must be "
                        + ServiceCapability.SYNC_QUERY_SERVICE + " or " + ServiceCapability.SYNC_QUERY_SERVICE
                        + ", but is " + accessInterface.getCapability());
            }
            if (!AccessInterfaceType.SOAP_SERVICE.equals(accessInterface.getInterfaceType())) {
                throw new IllegalArgumentException("AccessInterfaceType must be " + AccessInterfaceType.SOAP_SERVICE
                        + ", but is " + accessInterface.getInterfaceType());
            }
        }
        super.setAccessInterfaces(accessInterfaces);
    }

    /**
     * Check if the given access interface is either sync or async.
     * 
     * @param accessInterface
     *            the interface's capability to check
     * @return true if it has the capability.
     */
    private boolean hasSyncOrAsyncCapabilty(AccessInterface accessInterface) {
        return ServiceCapability.SYNC_QUERY_SERVICE.equals(accessInterface.getCapability())
                || ServiceCapability.ASYNC_QUERY_SERVICE.equals(accessInterface.getCapability());
    }
    
    /**
     * Update the where clauses, depending on the from property. 
     */
    private void updateWhereClauses() {
        // empty the where clauses
        whereClauses.clear();
        
        // and repopulate from cache or create new clause
        for (String catalogue : getFrom()) {
            WhereClause clause = whereClauseCache.get(catalogue);
            if (clause == null) {
                clause = whereClauseFactoryBean.createWhereClause(getServiceName(), catalogue);
                whereClauseCache.put(catalogue, clause);
            }
            whereClauses.add(clause);
        }
    }

    /**
     * Validate the input values. Subclasses my want to override this method for
     * improved validation.
     * 
     * @throws IllegalArgumentException
     *             if any input value is not legal.
     */
    protected void validateInput() throws IllegalArgumentException {
        AssertUtil.assertArgumentNotEmpty(getStartTime(), "startTime");
        AssertUtil.assertArgumentNotEmpty(getEndTime(), "endTime");
        AssertUtil.assertArgumentNotEmpty(getFrom(), "from");

        if (getStartTime().size() != getEndTime().size()) {
            throw new IllegalArgumentException("Argument 'startTime' and 'endTime' must have equal size: "
                    + getStartTime().size() + "!=" + getEndTime().size());
        }

        if (getStartTime().size() > 1 && getFrom().size() > 1 && getStartTime().size() != getFrom().size()) {
            throw new IllegalArgumentException(
                    "Either 'startTime/endTime' or 'from' must have size 1 or all must have equal size, but got "
                            + getStartTime().size() + "!=" + getFrom().size());
        }
    }

    /**
     * Get the name of all properties offered by this service. The corresponding
     * property descriptor will be looked up from the configurationManager. This
     * method can be overwritten for custom properties in subclasses.
     * 
     * @return the default property names.
     */
    protected String[] getPropertyNames() {
        return DEFAULT_PROPERTY_NAMES;
    }

    /**
     * @return the from
     */
    public List<String> getFrom() {
        return from;
    }

    /**
     * @param from
     *            the from to set
     */
    public void setFrom(List<String> from) {
        this.from = from;
        updateWhereClauses();
    }

    /**
     * @return the startDate
     */
    public List<String> getStartTime() {
        return startTime;
    }

    /**
     * @param startDate
     *            the startDate to set
     */
    public void setStartTime(List<String> startDate) {
        this.startTime = startDate;
    }

    /**
     * @return the endDate
     */
    public List<String> getEndTime() {
        return endTime;
    }

    /**
     * @param endDate
     *            the endDate to set
     */
    public void setEndTime(List<String> endDate) {
        this.endTime = endDate;
    }

    /**
     * @return the where
     */
    public String getWhere() {
        StringBuilder sb = new StringBuilder();
        for (WhereClause whereClause : whereClauses) {
            if (sb.length() > 0) {
                sb.append(";");  // separator between multiple where clauses
            }
            sb.append(querySerializer.getWhereClause(whereClause.getCatalogName(), whereClause.getQueryTerms()));
        }
        return sb.toString();
    }

    /**
     * @return the join
     */
    public String getJoin() {
        return join;
    }

    /**
     * @param join
     *            the join to set
     */
    public void setJoin(String join) {
        this.join = join;
    }

    /**
     * @return the maxRecords
     */
    public Integer getMaxRecords() {
        return maxRecords;
    }

    /**
     * @param maxRecords
     *            the maxRecords to set
     */
    public void setMaxRecords(Integer maxRecords) {
        this.maxRecords = maxRecords;
    }

    /**
     * @return the startIndex
     */
    public Integer getStartIndex() {
        return startIndex;
    }

    /**
     * @param startIndex
     *            the startIndex to set
     */
    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    /**
     * The log records assigned to this query call.
     * 
     * @return the log records.
     */
    public List<LogRecord> getLogRecords() {
        return logRecords;
    }

    @Override
    public java.beans.BeanInfo getBeanInfo() {
        return beanInfo;
    }

    /**
     * Get the query type, defaults to {@link QueryType#SYNC_QUERY}.
     * 
     * @return the queryType
     */
    public QueryType getQueryType() {
        return queryType;
    }

    /**
     * Set the query type, defaults to {@link QueryType#SYNC_QUERY}
     * 
     * @param queryType
     *            the queryType to set, must not be null
     */
    public void setQueryType(QueryType queryType) {

        this.queryType = queryType;
    }

    /**
     * Type of the query method to be called, defaults to FULL_QUERY.
     * 
     * @return the queryMethodType
     */
    public QueryMethodType getQueryMethodType() {
        return queryMethodType;
    }

    /**
     * Type of the query method to be called, defaults to FULL_QUERY.
     * 
     * @param queryMethodType
     *            the queryMethodType to set
     */
    public void setQueryMethodType(QueryMethodType queryMethodType) {
        this.queryMethodType = queryMethodType;
    }

    // /////// IOC
    /**
     * @return the configurationManager
     */
    public HelioConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    /**
     * @param configurationManager
     *            the configurationManager to set
     */
    @Required
    public void setConfigurationManager(HelioConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }

    /**
     * @return the queryDelegateFactory
     */
    public QueryDelegateFactory getQueryDelegateFactory() {
        return queryDelegateFactory;
    }

    /**
     * @param queryDelegateFactory
     *            the queryDelegateFactory to set
     */
    public void setQueryDelegateFactory(QueryDelegateFactory queryDelegateFactory) {
        this.queryDelegateFactory = queryDelegateFactory;
    }
    
    /**
     * @return the whereClauseFactoryBean
     */
    public WhereClauseFactoryBean getWhereClauseFactoryBean() {
        return whereClauseFactoryBean;
    }

    /**
     * @param whereClauseFactoryBean the whereClauseFactoryBean to set
     */
    public void setWhereClauseFactoryBean(WhereClauseFactoryBean whereClauseFactoryBean) {
        this.whereClauseFactoryBean = whereClauseFactoryBean;
    }
    
    /**
     * @return the querySerializer
     */
    public QuerySerializer getQuerySerializer() {
        return querySerializer;
    }

    /**
     * @param querySerializer the querySerializer to set
     */
    public void setQuerySerializer(QuerySerializer querySerializer) {
        this.querySerializer = querySerializer;
    }

    /**
     * Get the currently available where clauses. If a catalog does not support where clauses
     * the where clause will have an empty helioFieldDescriptor field.  
     * There will be one where clause per entry in the from property.
     * @return a list for where clauses, if the from property is set. An empty list otherwise.
     */
    @Override
    public List<WhereClause> getWhereClauses() {
        return whereClauses;
    }
    
    @Override
    public WhereClause getWhereClauseByCatalogName(String catalogName) {
        for (WhereClause clause : whereClauses) {
            if (catalogName.equals(clause.getCatalogName())) {
                return clause;
            }
        }
        return null;
    }

    /**
     * A custom Bean Info for this service.
     * 
     * @author MarcoSoldati
     * 
     */
    static class BeanInfo extends SimpleBeanInfo {
        private final PropertyDescriptor[] propertyDescriptors;

        public BeanInfo(PropertyDescriptor[] propertyDescriptors) {
            this.propertyDescriptors = propertyDescriptors;
        }

        @Override
        public PropertyDescriptor[] getPropertyDescriptors() {
            return propertyDescriptors;
        }
    }
}
