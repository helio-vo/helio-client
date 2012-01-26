
package eu.heliovo.clientapi.processing.taverna;

import java.util.List;

import eu.heliovo.clientapi.workerservice.JobExecutionException;

/**
 * Thrown if a problem occurs during the execution of a taverna workflow.
 * The exception contains a context object to provide additional information about the problem.
 * @author MarcoSoldati
 *
 */
public class TavernaWorkflowException extends JobExecutionException {
    /**
     * the serial id
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * List of context messages from the outputs
     */
    private final List<String> context;
    
    public TavernaWorkflowException(String message, List<String> context) {
        super(message);
        this.context = context;
    }

    public TavernaWorkflowException(String message, Throwable cause, List<String> context) {
        super(message, cause);
        this.context = context;
    }

    public TavernaWorkflowException(Throwable cause, List<String> context) {
        super(cause);
        this.context = context;
    }
    
    /**
     * Get the context
     * @return
     */
    public List<String> getContext() {
        return context;
    }
}
