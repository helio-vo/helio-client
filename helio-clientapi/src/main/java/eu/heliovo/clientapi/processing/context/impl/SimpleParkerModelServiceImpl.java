package eu.heliovo.clientapi.processing.context.impl;

import java.util.Date;

import org.astrogrid.schema.agparameterdefinition.v1.ParameterValue;
import org.astrogrid.schema.agworkflow.v1.Input;
import org.astrogrid.schema.agworkflow.v1.Output;
import org.astrogrid.schema.agworkflow.v1.Tool;

import eu.heliovo.clientapi.processing.ProcessingResult;
import eu.heliovo.clientapi.processing.UrlProcessingResultObject;
import eu.heliovo.clientapi.processing.context.AbstractContextServiceImpl;
import eu.heliovo.clientapi.processing.context.SimpleParkerModelService;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.util.AssertUtil;
import eu.heliovo.shared.util.DateUtil;

/**
 * Proxy to the Parker model service impl
 * @author MarcoSoldati
 *
 */
public class SimpleParkerModelServiceImpl extends AbstractContextServiceImpl implements SimpleParkerModelService {
    
    /**
     * ID of the parker model
     */
    public static final String SERVICE_VARIANT = "ivo://helio-vo.eu/cxs/parkermodel";
    
    /**
     * The start date
     */
    private Date startTime;

    /**
     * Velocity of the parker spiral
     */
    private int velocity = 400;

    /**
     * Plot type
     */
    private PlotType plotType = PlotType.INNER;
    
    /**
     * The parker model plotter.
     * @param accessInterfaces
     */
    public SimpleParkerModelServiceImpl(AccessInterface[] accessInterfaces) {
        super(HelioServiceName.CXS, null, accessInterfaces);
    }
    
    @Override
    public ProcessingResult<UrlProcessingResultObject> parkerModel(Date startTime, Integer velocity, PlotType plotType) {
        this.startTime = startTime;
        if (velocity != null) {
            this.velocity = velocity;
        }
        if (plotType != null) {
            this.plotType = plotType;
        }
        return execute();
    }

    @Override
    protected Tool initTool() {
        AssertUtil.assertArgumentNotNull(startTime, "startTime");
        
        Tool tool = new Tool();
        tool.setInterface("simple");
        tool.setName("ivo://helio-vo.eu/cxs/parkermodel");
        Input input = new Input();
        ParameterValue startDateVal = createParameterValue("StartDate", DateUtil.toIsoDateString(startTime), false);
        input.getParameter().add(startDateVal);
        ParameterValue velocityVal = createParameterValue("Velocity", "" + this.velocity, false);
        input.getParameter().add(velocityVal);
        ParameterValue innerVal = createParameterValue("Inner", this.plotType == PlotType.INNER ? "1" : "0", false);
        input.getParameter().add(innerVal);
        ParameterValue outerVal = createParameterValue("Outer", this.plotType == PlotType.OUTER ? "1" : "0", false);
        input.getParameter().add(outerVal);
        tool.setInput(input);
        
        Output output = new Output();
        tool.setOutput(output);
        return tool;
    }

    @Override
    protected String getLogOutFileName() {
        return "cea-output.log";
    }

    @Override
    protected String getLogErrFileName() {
        return "cea-error.log";
    }

    @Override
    protected String getResultFileName() {
        return "parkermodel.png";
    }
    
    @Override
    public Date getStartTime() {
        return startTime;
    }

    @Override
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Override
    public int getVelocity() {
        return velocity;
    }

    @Override
    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    @Override
    public void setPlotType(PlotType plotType) {
        this.plotType = plotType;
    }

    @Override
    public PlotType getPlotType() {
        return plotType;
    }
}
