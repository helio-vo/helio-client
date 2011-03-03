package eu.heliovo.clientapi.model.user.data;

import java.util.SortedSet;

import eu.heliovo.clientapi.model.job.Job;

/**
 * Contains a list of jobs. 
 * @author marco soldati at fhnw ch
 *
 */
public interface JobList {
	/**
	 * Get the currently active or recently terminated jobs.
	 * @return the current jobs sorted by last modified date.
	 */
	public SortedSet<Job> getCurrentJobs();
	
	
	/**
	 * Return the jobs that already terminated.
	 * @return the current jobs sorted by last modified date.
	 */
	public SortedSet<Job> getOldJobs();
}
