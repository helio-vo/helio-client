package eu.heliovo.clientapi.processing.context.impl;

import java.util.Date;

import org.astrogrid.schema.agparameterdefinition.v1.ParameterValue;
import org.astrogrid.schema.agworkflow.v1.Input;
import org.astrogrid.schema.agworkflow.v1.Output;
import org.astrogrid.schema.agworkflow.v1.Tool;

import eu.heliovo.clientapi.processing.ProcessingResult;
import eu.heliovo.clientapi.processing.context.AbstractContextServiceImpl;
import eu.heliovo.clientapi.processing.context.FlarePlotterService;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.shared.util.AssertUtil;
import eu.heliovo.shared.util.DateUtil;

public class FlarePlotterServiceImpl extends AbstractContextServiceImpl implements FlarePlotterService {
    /**
     * The  date
     */
    private Date date;
    
    public FlarePlotterServiceImpl(String name, String description, AccessInterface[] accessInterfaces) {
        super(name, description, accessInterfaces);
    }
    
    /* (non-Javadoc)
     * @see eu.heliovo.clientapi.processing.context.impl.GoesPlotClient#goesPlot(java.lang.String, java.lang.String)
     */
    @Override
    public ProcessingResult flarePlot(Date date) {
        this.date = date;
        return execute();
    }

    @Override
    protected Tool initTool() {
        AssertUtil.assertArgumentNotNull(date, "date");
        
        Tool tool = new Tool();
        tool.setInterface("simple");
        tool.setName("ivo://helio-vo.eu/cxs/flareplotter");
        Input input = new Input();
        ParameterValue dateParam = createParameterValue("date", DateUtil.toIsoDateString(date), false);
        input.getParameter().add(dateParam);
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
        return "flare_plot.png";
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public void setDate(Date date) {
        this.date = date;     
    }
}
