package eu.heliovo.mockclient.workerservice;

import eu.heliovo.clientapi.workerservice.HelioWorkerService;
import eu.heliovo.clientapi.workerservice.HelioWorkerServiceManager;


/**
 * The default worker service manager.
 * This component should be instanciated once per user. 
 * It manages the currently active and the past jobs of a user.
 * @author marco soldati at fhnw ch
 *
 */
public class DefaultWorkerServiceManager implements HelioWorkerServiceManager {

	
	
	
	@Override
	public HelioWorkerService getHelioJob(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HelioWorkerService[] getHelioJobs() {
		// TODO Auto-generated method stub
		return null;
	}

}
