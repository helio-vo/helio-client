package eu.heliovo.clientapi.model.job;

import java.util.Comparator;



/**
 * Data object that holds a job element
 * @author marco soldati at fhnw ch
 *
 */
public interface Job {
	/**
	 * Comparator to sort jobs by get last modified date. 
	 * @author marco soldati at fhnw ch
	 *
	 */
	public class JobComparator implements Comparator<Job> {

		@Override
		public int compare(Job job1, Job job2) {
			return (int)(job1.getLastModified() - job2.getLastModified());
		}
	} 

	/**
	 * Get the id of the job.
	 * @return the id of the job.
	 */
	public ResultId getJobId();
	
	/**
	 * The last modified date
	 * @return
	 */
	public long getLastModified();

	/**
	 * The last modified date must be a unique ID per user.
	 * @param obj the job object
	 * @return
	 */
	@Override
	public boolean equals(Object obj);
}
