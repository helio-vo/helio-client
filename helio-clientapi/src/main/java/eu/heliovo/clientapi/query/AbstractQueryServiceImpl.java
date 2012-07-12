package eu.heliovo.clientapi.query;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import eu.heliovo.clientapi.config.HelioConfigurationManager;
import eu.heliovo.clientapi.model.service.AbstractServiceImpl;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Base class for all query services
 * @author MarcoSoldati
 *
 */
public abstract class AbstractQueryServiceImpl extends AbstractServiceImpl implements QueryService, AnnotatedBean {
    /**
     * The logger instance
     */
    static final Logger _LOGGER = Logger.getLogger(AbstractQueryServiceImpl.class);
    
    /**
     * The default properties of this class.
     * They will be returned by getPropertyNames()
     */
    private static String[] DEFAULT_PROPERTY_NAMES = 
            new String[] { "from", "startTime", "endTime", "where", "join", "maxRecords", "startIndex" };
    
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
    private String where;
    
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
     * A delegate to send queries to.
     */
    private transient QueryDelegate queryDelegate;

    /**
     * The manager to read the properties for a specific service.
     */
    private transient HelioConfigurationManager configurationManager;
    
    /**
     * Keep the records for this service call.
     */
    private transient final List<LogRecord> logRecords = new ArrayList<LogRecord>();
    
    /**
     * Store the BeanInfo for this service instance.
     * assigned in the init method
     */
    private BeanInfo beanInfo = null;
    
    /**
     * Validate the input values
     * @throws IllegalArgumentException if any input value is not legal.
     */
    protected void validateInput() throws IllegalArgumentException {
        AssertUtil.assertArgumentNotNull(getQueryDelegate(), "queryDelegate");
        AssertUtil.assertArgumentNotEmpty(getStartTime(), "startTime");
        AssertUtil.assertArgumentNotEmpty(getEndTime(), "endTime");
        AssertUtil.assertArgumentNotEmpty(getFrom(), "from");

        if (getStartTime().size() != getEndTime().size()) {
            throw new IllegalArgumentException("Argument 'startTime' and 'endTime' must have equal size: " + getStartTime().size() + "!=" + getEndTime().size());
        }
        
        if (getStartTime().size() > 1 && getFrom().size() > 1 && getStartTime().size() != getFrom().size()) {
            throw new IllegalArgumentException("Either 'startTime/endTime' or 'from' must have size 1 or all must have equal size, but got " + getStartTime().size() + "!=" + getFrom().size());
        }
    }
    
    /**
     * Initialize this object.
     */
    @Override
    public void init() {
        String[] propertyNames = getPropertyNames();
        List<PropertyDescriptor> descriptors = new ArrayList<PropertyDescriptor>();        
        for (int i = 0; i < propertyNames.length; i++) {
            String propName = propertyNames[i];
            PropertyDescriptor descriptor = configurationManager.getPropertyDescriptor(getServiceName(), getServiceVariant(), propName, getClass());
            if (descriptor == null) {
                _LOGGER.warn("Unable to find descriptor for property '" + propName + "'. Ignoring property in description. " +
                		"This may be a problem for clients that use bean introspection to detect available bean properties.");
            } else {
                descriptors.add(descriptor);
            }
        }
        this.beanInfo = new BeanInfo(descriptors.toArray(new PropertyDescriptor[descriptors.size()]));
    }
    
    /**
     * Get the name of all properties offered by this service. The corresponding property descriptor will be looked up
     * from the configurationManager. This method can be overwritten for custom properties in subclasses.
     * @return the default property names.
     */
    protected String[] getPropertyNames() {
        return DEFAULT_PROPERTY_NAMES;
    }
    
    @Override
    public HelioQueryResult execute() {
        final long jobStartTime = System.currentTimeMillis();
        validateInput();
        
        Set<AccessInterface> triedInterfaces = new HashSet<AccessInterface>();
        
        while (true) {
            // get the end point
            final AccessInterface bestAccessInterface = getBestAccessInterface();
            if (!triedInterfaces.add(bestAccessInterface)) {
                throw new JobExecutionException("All registered remote services are unavailable. Tried to access: " + triedInterfaces.toString());
            };
            
            // log the call.
            String callId = bestAccessInterface.getUrl() + "::" + queryDelegate.getMethodName();
            logRecords.add(new LogRecord(Level.INFO, "Connecting to " + callId));

            StringBuilder message = new StringBuilder();
            message.append("Executing 'result=").append(queryDelegate.getMethodName()).append("(");
            message.append("startTime=").append(startTime);
            message.append(", ").append("endTime=").append(endTime);
            message.append(", ").append("from=").append(from);
            message.append(", ").append("where=").append(where);
            message.append(", ").append("maxrecords=").append(maxRecords);
            message.append(", ").append("startIndex=").append(startIndex);
            message.append(", ").append("join=").append(join);
            //message.append(", ").append("saveTo=").append(saveTo);
            message.append(")'");
            
            _LOGGER.info(message.toString());            
            logRecords.add(new LogRecord(Level.INFO, message.toString()));

            // do the call
            try {
                return queryDelegate.callWebService(this, bestAccessInterface);
            } catch (WebServiceException e) {
                // get port fails
                String msg = "Timeout occurred. Trying to failover.";
                logRecords.add(new LogRecord(Level.INFO, msg));
                _LOGGER.info(msg);
                loadBalancer.updateAccessTime(bestAccessInterface, -1);
            } catch (JobExecutionException e) { // handle timeout
                if (e.getCause() instanceof TimeoutException) {
                    String msg = "Timeout occurred. Trying to failover.";
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
    
    /**
     * @return the queryDelegate
     */
    protected QueryDelegate getQueryDelegate() {
        return queryDelegate;
    }

    /**
     * @param queryDelegate the queryDelegate to set
     */
    protected void setQueryDelegate(QueryDelegate queryDelegate) {
        this.queryDelegate = queryDelegate;
    }

    /**
     * @return the from
     */
    public List<String> getFrom() {
        return from;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(List<String> from) {
        this.from = from;
    }

    /**
     * @return the startDate
     */
    public List<String> getStartTime() {
        return startTime;
    }

    /**
     * @param startDate the startDate to set
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
     * @param endDate the endDate to set
     */
    public void setEndTime(List<String> endDate) {
        this.endTime = endDate;
    }

    /**
     * @return the where
     */
    public String getWhere() {
        return where;
    }

    /**
     * @param where the where to set
     */
    public void setWhere(String where) {
        this.where = where;
    }

    /**
     * @return the join
     */
    public String getJoin() {
        return join;
    }

    /**
     * @param join the join to set
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
     * @param maxRecords the maxRecords to set
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
     * @param startIndex the startIndex to set
     */
    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }
    
    /**
     * The log records assigned to this query call.
     * @return the log records.
     */
    public List<LogRecord> getLogRecords() {
        return logRecords;
    }
    
    @Override
    public java.beans.BeanInfo getBeanInfo() {
        return beanInfo;
    }
    
    ///////// IOC
    /**
     * @return the configurationManager
     */
    public HelioConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    /**
     * @param configurationManager the configurationManager to set
     */
    @Required
    public void setConfigurationManager(
            HelioConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }

    /**
     * A custom Bean Info for this service.
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
