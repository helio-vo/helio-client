package eu.heliovo.clientapi.query;


public enum QueryType {
    /**
     * Synchronous, full feature query
     */
    SYNC_QUERY,
    
    /**
     * Synchronous, time-only query
     */
    SYNC_TIME_QUERY,
    
    /**
     * Asynchronous, full-feature query
     */
    ASYNC_QUERY,
    
    /**
     * Asynchronous, time-only query
     */
    ASYNC_TIME_QUERY;
}
