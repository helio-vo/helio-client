package eu.heliovo.clientapi.processing.context.impl;

import java.util.Date;
import java.util.List;
import java.util.logging.LogRecord;

import org.astrogrid.schema.agparameterdefinition.v1.ParameterValue;
import org.astrogrid.schema.agworkflow.v1.Input;
import org.astrogrid.schema.agworkflow.v1.Output;
import org.astrogrid.schema.agworkflow.v1.Tool;

import eu.heliovo.clientapi.processing.ProcessingResult;
import eu.heliovo.clientapi.processing.context.AbstractContextServiceImpl;
import eu.heliovo.clientapi.processing.context.GoesPlotterService;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.util.AssertUtil;
import eu.heliovo.shared.util.DateUtil;

public class GoesPlotterServiceImpl extends AbstractContextServiceImpl implements GoesPlotterService {
    /**
     * The start date
     */
    private Date startDate;
    
    /**
     * The end date
     */
    private Date endDate;
    
    public GoesPlotterServiceImpl(HelioServiceName serviceName, String description, AccessInterface[] accessInterfaces) {
        super(serviceName, description, accessInterfaces);
    }
    
    /* (non-Javadoc)
     * @see eu.heliovo.clientapi.processing.context.impl.GoesPlotClient#goesPlot(java.lang.String, java.lang.String)
     */
    @Override
    public ProcessingResult goesPlot(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        return execute();
    }

    @Override
    protected Tool initTool(List<LogRecord> logRecords) {
        AssertUtil.assertArgumentNotNull(startDate, "startDate");
        AssertUtil.assertArgumentNotNull(endDate, "endDate");
        
        Tool tool = new Tool();
        tool.setInterface("simple");
        tool.setName("ivo://helio-vo.eu/cxs/goesplotter");
        Input input = new Input();
        ParameterValue startTime = createParameterValue("START_DATE", DateUtil.toIsoDateString(startDate), false);
        input.getParameter().add(startTime);
        ParameterValue endTime = createParameterValue("END_DATE", DateUtil.toIsoDateString(endDate), false);
        input.getParameter().add(endTime);
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
        return "goes_plot.png";
    }
    
    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
