package eu.heliovo.clientapi.processing;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import eu.heliovo.clientapi.workerservice.JobExecutionException;

/**
 * Processes that just return a simple URL.
 * @author MarcoSoldati
 *
 */
public class UrlProcessingResultObject implements ProcessingResultObject {
    /**
     * List holding the allowed output names
     */
    private static List<String> OUTPUT_NAMES = Collections.singletonList("url");
    
    /**
     * The stored url
     */
    private final URL url;
    
    /**
     * Create a new URL Object
     * @param url the stored url
     */
    public UrlProcessingResultObject(URL url) {
        this.url = url;
    }

    @Override
    public List<String> getOutputNames() throws JobExecutionException {
        return OUTPUT_NAMES;
    }

    @Override
    public Object getOutput(String outputName) throws JobExecutionException {
        if (OUTPUT_NAMES.get(0).equals(outputName)) {
            return url;
        }
        throw new IllegalArgumentException("Unknown output name: " + outputName);
    }
    
    /**
     * Get the URL.
     * @return the URL
     */
    public URL getUrl() {
        return url;
    }
}
