package eu.heliovo.clientapi.query.asyncquery.impl;


/**
 * DPAS needs an increase timeout for the first call.
 * @author MarcoSoldati
 * TODO: Rather use a spring property for setting the timeout.
 */
public class DpasAsyncQueryServiceImpl extends AsyncQueryServiceImpl {
    
    /**
     * Create the DPAS query support.
     */
    public DpasAsyncQueryServiceImpl() {
    }
    
    /**
     * Increase call timeout for DPAS
     */
    @Override
    protected long getCallTimout() {
        return 60000;
    }
}