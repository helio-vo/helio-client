package eu.heliovo.clientapi.processing.taverna.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import uk.org.taverna.ns._2010.xml.server.soap.NoDirectoryEntryException;
import uk.org.taverna.ns._2010.xml.server.soap.UnknownRunException;
import eu.heliovo.clientapi.processing.ProcessingResultObject;
import eu.heliovo.clientapi.processing.taverna.AbstractTavernaServiceImpl;
import eu.heliovo.clientapi.processing.taverna.impl.TavernaWorkflow2283.TavernaWorkflow2283ResultObject;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.props.HelioFileUtil;
import eu.heliovo.tavernaserver.Run;

/**
 * Client implementation of Taverna Workflow
 * http://www.myexperiment.org/workflows/2283.html
 * @author MarcoSoldati
 *
 */
public class TavernaWorkflow2283 extends AbstractTavernaServiceImpl<TavernaWorkflow2283ResultObject> {
    
    /**
     * the workflow id.
     */
    private static final String WORKFLOW_ID = "2283";

    /**
     * Name of the variant
     */
    public static final String SERVICE_VARIANT = HelioServiceName.TAVERNA_SERVER.getServiceId() + "/" + WORKFLOW_ID;

    /**
     * Create the Taverna workflow instance
     * @param myExperimentInterface the myExperiment interface.
     * @param tavernaInterfaces the Taverna Server instances to use.
     */
    public TavernaWorkflow2283(AccessInterface myExperimentInterface, AccessInterface[] tavernaInterfaces) {
        super(HelioServiceName.TAVERNA_SERVER, null, myExperimentInterface, tavernaInterfaces);
    }


    @Override
    protected String getWorkflowId() {
        return WORKFLOW_ID;
    }

    @Override
    public void setInputs(Map<String, Object> inputs) {
    }
    
    
    @Override
    public TavernaWorkflow2283ResultObject createResultObject(Run run) {
        TavernaWorkflow2283ResultObject resultObject = new TavernaWorkflow2283ResultObject(run);
        return resultObject;
    }
    
    
    /**
     * Provide access to the results.
     * @author MarcoSoldati
     *
     */
    public static class TavernaWorkflow2283ResultObject implements ProcessingResultObject {
        /**
         * The list of outputs for this workflow
         */
        private static final List<String> OUTPUT_NAMES = Collections.singletonList("VOTable");

        /**
         * The ZIP file that contains the result of this workflow run.
         */
        private final File zipFile;
        
        /**
         * Create the result object with a pointer to the run
         * @param run the run.
         */
        public TavernaWorkflow2283ResultObject(Run run) {
            // get the result of the run
            byte[] zip;
            try {
                zip = run.getOutputZip();
            } catch (NoDirectoryEntryException e) {
                throw new RuntimeException("Internal Error: unable to retrieve results: " + e.getMessage(), e);
            } catch (UnknownRunException e) {
                throw new RuntimeException("Internal Error: unable to retrieve results: " + e.getMessage(), e);
            }
            
            File tavernaTmp = HelioFileUtil.getHelioTempDir("taverna");
            zipFile = new File(tavernaTmp, run.getId());
            
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(zipFile);
                fos.write(zip);
                fos.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Internal Error: unable to create target ZIP " + zipFile.getAbsolutePath() + ". Cause: " + e.getMessage(), e);
            } catch (IOException e) {
                throw new RuntimeException("Internal Error: unable to create target ZIP: " + e.getMessage(), e);
            }
        }

        @Override
        public List<String> getOutputNames() throws JobExecutionException {
            return OUTPUT_NAMES;
        }

        @Override
        public Object getOutput(String outputName) throws JobExecutionException {
            if (OUTPUT_NAMES.get(0).equals(outputName)) {
                return getVOTable();
            }
            return null;
        }
        
        /**
         * Get the URL pointing to the rmeote VOTable.
         * @return the VOTable as string.
         */
        public InputStream getVOTable() {
            ZipFile zip;
            try {
                zip = new ZipFile(zipFile);
            } catch (ZipException e) {
                throw new RuntimeException("Internal Error: unable to read previously stored ZIP-file: " + e.getMessage(), e);
            } catch (IOException e) {
                throw new RuntimeException("Internal Error: unable to read previously stored ZIP-file: " + e.getMessage(), e);
            }
            ZipEntry votable = zip.getEntry("VOTable");
            try {
                return zip.getInputStream(votable);
            } catch (IOException e) {
                throw new RuntimeException("Internal Error: unable to read entry from previously stored ZIP-file: " + e.getMessage(), e);
            }
        }
    }
}
