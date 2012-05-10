package eu.heliovo.clientapi.processing.hps.impl;

import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import eu.heliovo.clientapi.processing.ProcessingResult;
import eu.heliovo.clientapi.processing.hps.AbstractHelioProcessingServiceImpl;
import eu.heliovo.clientapi.processing.hps.CirPropagationModel;
import eu.heliovo.clientapi.processing.hps.CirPropagationModel.CirProcessingResultObject;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.hps.server.ApplicationParameter;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.util.DateUtil;
import eu.heliovo.shared.util.FileUtil;

/**
 * Implementation of the CME propagation model.
 * @author MarcoSoldati
 *
 */
public class CirPropagationModelImpl extends AbstractHelioProcessingServiceImpl<CirProcessingResultObject> implements CirPropagationModel {

    /**
     * the application id.
     */
    private static final String APPLICATION_ID = "pm_cir_fw";

    /**
     * Name of the variant
     */
    public static final String SERVICE_VARIANT = HelioServiceName.HPS.getServiceId() + "/" + APPLICATION_ID;

    /**
     * Create the propagation model.
     */
    public CirPropagationModelImpl() {
    }
    
    /**
     * Start time
     */
    private Date startTime;
    
    /**
     * Position of cme on sun
     */
    private float longitude;
    
    /**
     * Speed
     */
    private float speed;
    
    /**
     * The speed error
     */
    private float speedError;
    
    @Override
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Override
    public Date getStartTime() {
        return startTime;
    }

    @Override
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    @Override
    public float getLongitude() {
        return longitude;
    }

    @Override
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public float getSpeed() {
        return speed;
    }
    
    @Override
    public void setSpeedError(float speedError) {
        this.speedError = speedError;
    }

    @Override
    public float getSpeedError() {
        return speedError;
    }
    
    @Override
    public ProcessingResult<CirProcessingResultObject> execute(Date startTime, Float longitude, Float speed, Float speedError) throws JobExecutionException {
        this.setStartTime(startTime);
        this.setLongitude(longitude);
        this.setSpeed(speed);
        this.setSpeedError(speedError);
        return execute();
    }

    /**
     * Output names 
     */
    private static final List<String> OUTPUT_NAMES = Arrays.asList("votable_url", "outerplot_url", "innerplot_url");

    @Override
    protected CirProcessingResultObject createResultObject(final AccessInterface accessInterface, final String executionId) {
        // FIXME: hardcoded
        final String baseUrl = "http://cagnode58.cs.tcd.ie/output_dir/" + executionId + "/";
        
        return new CirProcessingResultObject() {
            
            @Override
            public URL getVoTableUrl() {
                return FileUtil.asURL(baseUrl + "cir_pm.votable");
            }
            
            @Override
            public URL getOuterPlotUrl() {
                return FileUtil.asURL(baseUrl + "cir_pm_outer.png");
            }
            
            @Override
            public URL getInnerPlotUrl() {
                return FileUtil.asURL(baseUrl + "cir_pm_inner.png");
            }
            
            @Override
            public List<String> getOutputNames() throws JobExecutionException {
                return OUTPUT_NAMES;
            }

            @Override
            public Object getOutput(String outputName) throws JobExecutionException {
                int output = OUTPUT_NAMES.indexOf(outputName);
                switch (output) {
                case 0:
                    return getVoTableUrl();
                case 1:
                    return getOuterPlotUrl();
                case 2:
                    return getInnerPlotUrl();
                default:
                    throw new IllegalArgumentException("Unknown output name: " + outputName);
                }
            }
        };
    }

    @Override
    protected String getApplicationId() {
        return APPLICATION_ID;
    }

    @Override
    protected void setParameter(ApplicationParameter param) {
        String name = param.getName();
        if (name.contains("time")) {
            checkType("startTime", "String", "String");
            param.setValue(DateUtil.toIsoDateString(getStartTime()));
        } else if (name.contains("point")) {
            checkType("longitude", "Float", "Float");
            param.setValue(""+getLongitude());
        } else if (name.contains("starting speed")) {
            checkType("speed", "Float", "Float");
            param.setValue(""+getSpeed());
        } else if (name.contains("error speed")) {
            checkType("speedError", "Float", "Float");
            param.setValue(""+getSpeedError());
        } else {
            throw new JobExecutionException("Internal Error: Unknown parameter found: " + param.getName());
        }
    }

}
