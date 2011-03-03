package eu.heliovo.clientapi.workerservice;

import java.util.Date;


/**
 * Object that provides control over asynchronous, potentially long running calls to Helio. 
 * The design of this interface is inspired by the IVOA UWS (Universal Worker Service, 
 * <a href="http://www.ivoa.net/Documents/UWS/">http://www.ivoa.net/Documents/UWS/</a>).
 * Classes that implement this interface should be called asynchronously.
 * @author marco soldati at fhnw ch
 */
public interface HelioWorkerService {
	/**
	 * Enumeration of the possible phases of a worker service. Borrowed from UWS.
	 *
	 */
	public enum Phase {
		/**
		 * The job is accepted by the service but not yet committed for execution by the client. In this state,
		 * the job quote can be read and evaluated. This is the state into which a job enters when it is first created.
		 */
		PENDING     ("The job is accepted by the service but not yet committed for execution by the client. In this state," +
				     "the job quote can be read and evaluated. This is the state into which a job enters when it is first created."),
	    /**
	     * The job is committed for execution by the client but the service has not yet assigned it to a processor.
	     * No Results are produced in this phase.
	     */
		QUEUED      ("The job is committed for execution by the client but the service has not yet assigned it to a processor. " +
				     "No Results are produced in this phase."),
		/**
		 * The job has been assigned to a processor. Results may be produced at any time during	this phase.		     
		 */
		EXECUTING   ("The job has been assigned to a processor. Results may be produced at any time during	this phase."),
		/**
		 * The execution of the job is over. The Results may be collected.
		 */
		COMPLETED   ("The execution of the job is over. The Results may be collected."),
		/**
		 * The job failed to complete. No further work will be done nor Results produced. Results may be unavailable or
		 * available but invalid; either way the Results should not be trusted.
		 */
		ERROR       ("The job failed to complete. No further work will be done nor Results produced. Results may be unavailable or " +
				     "available but invalid; either way the Results should not be trusted."),
		/**
		 * The job has been manually aborted by the user, or the system has aborted the job due to lack of or overuse " +
		 * of resources.
		 */
		ABORTED     ("The job has been manually aborted by the user, or the system has aborted the job due to lack of or overuse " +
				     "of resources."),
		/**
		 * The job is in an unknown state.
		 */
		UNKNOWN     ("The job is in an unknown state."),
		/**
		 * The job is HELD pending execution and will not automatically be executed (cf, pending)
		 */
		HELD        ("The job is HELD pending execution and will not automatically be executed (cf, pending)"),
		/**
		 * The job has been suspended by the system during execution. This might be because of temporary lack of resource. " +
		 * The UWS will automatically resume the job into the EXECUTING phase without any intervention when resource becomes " +
	     *available.
		 */
		SUSPENDED   ("The job has been suspended by the system during execution. This might be because of temporary lack of resource. " +
				     "The UWS will automatically resume the job into the EXECUTING phase without any intervention when resource becomes " +
				     "available."),
		;
		
		/**
		 * The description for the phase value.
		 */
		private final String description;

		/**
		 * 
		 * @param description
		 */
		Phase(String description) {
			this.description = description;
			
		}
		
		public String getDescription() {
			return description;
		}
	}
	
	/**
	 * Return the current status of a job.
	 * @return the status of a job.
	 */
	public Phase getPhase(); 
	
	/**
	 * Estimated time to execute a job in milliseconds. 0 means unlimited execution duration. Must be >=0.
	 * @return the time estimation.
	 */
	public int getExecutionDuration();
	
	/**
	 * Time when a Job gets destroyed and all results are cleared.
	 * @return the destruction time as date.
	 */
	public Date getDestructionTime();
	
	
}
