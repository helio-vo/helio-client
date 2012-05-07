package eu.heliovo.clientapi.processing.context.impl;

import java.util.Date;

import org.astrogrid.schema.agparameterdefinition.v1.ParameterValue;
import org.astrogrid.schema.agworkflow.v1.Input;
import org.astrogrid.schema.agworkflow.v1.Output;
import org.astrogrid.schema.agworkflow.v1.Tool;

import eu.heliovo.clientapi.processing.ProcessingResult;
import eu.heliovo.clientapi.processing.UrlProcessingResultObject;
import eu.heliovo.clientapi.processing.context.AbstractContextServiceImpl;
import eu.heliovo.clientapi.processing.context.GoesPlotterService;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.util.AssertUtil;
import eu.heliovo.shared.util.DateUtil;

/**
 * A ploting service for goes plots.
 * @author MarcoSoldati
 *
 */
public class GoesPlotterServiceImpl extends AbstractContextServiceImpl implements GoesPlotterService {
    /**
     * The start date
     */
    private Date startTime;
    
    /**
     * The end date
     */
    private Date endTime;
    
    /**
     * The plot type to use
     */
    private PlotType plotType = PlotType.BOTH;

    /**
     * ID of the goes plotter
     */
    public static final String SERVICE_VARIANT = "ivo://helio-vo.eu/cxs/goesplotter";
    
    /**
     * A plotting service for goes.
     */
    public GoesPlotterServiceImpl(HelioServiceName serviceName, String serviceVariant) {
        super(HelioServiceName.CXS, SERVICE_VARIANT);
        AssertUtil.assertArgumentEquals(HelioServiceName.CXS, serviceName, "serviceName");
        AssertUtil.assertArgumentEquals(serviceVariant, SERVICE_VARIANT, "serviceVariant");
    }
    
    /* (non-Javadoc)
     * @see eu.heliovo.clientapi.processing.context.impl.GoesPlotClient#goesPlot(java.lang.String, java.lang.String)
     */
    @Override
    public ProcessingResult<UrlProcessingResultObject> goesPlot(Date startTime, Date endTime, PlotType plotType) {
        this.startTime = startTime;
        this.endTime = endTime;
        if (plotType != null)
            this.plotType = plotType;
        return execute();
    }

    @Override
    protected Tool initTool() {
        AssertUtil.assertArgumentNotNull(startTime, "startTime");
        AssertUtil.assertArgumentNotNull(endTime, "endTime");
        
        Tool tool = new Tool();
        tool.setInterface("simple");
        tool.setName("ivo://helio-vo.eu/cxs/goesplotter");
        Input input = new Input();
        ParameterValue startTimeVal = createParameterValue("StartDate", DateUtil.toIsoDateString(startTime), false);
        input.getParameter().add(startTimeVal);
        ParameterValue endTimeVal = createParameterValue("EndDate", DateUtil.toIsoDateString(endTime), false);
        input.getParameter().add(endTimeVal);
        ParameterValue plottypeVal = createParameterValue("Type", getPlotType().getLabel(), false);
        input.getParameter().add(plottypeVal);
        
        tool.setInput(input);
        
        Output output = new Output();
        ParameterValue outParam = createParameterValue("goes_plot.png", "internalstorage:/", true);
        output.getParameter().add(outParam);
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
        return "goes_plot.png";
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
    public Date getEndTime() {
        return endTime;
    }

    @Override
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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
