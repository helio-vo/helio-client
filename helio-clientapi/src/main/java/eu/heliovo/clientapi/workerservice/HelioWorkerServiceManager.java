package eu.heliovo.clientapi.workerservice;

/**
 * Manage the Helio result sets, i.e.&nbsp;provide access to long running queries
 * as well as to results of previous queries.
 */
public interface HelioWorkerServiceManager {
	  
	/**
	 * Gets a specific long-running job.
	 * @param id the id of the job to retrieve.
	 */
	public HelioWorkerService getHelioJob(String id);
	
	
	/**
	 * Get the list of active Helio jobs.
	 * Active jobs are either still running or have been finished, but the 
	 * result has not been collected.
	 * @return 
	 */
	public HelioWorkerService[] getHelioJobs();

}
