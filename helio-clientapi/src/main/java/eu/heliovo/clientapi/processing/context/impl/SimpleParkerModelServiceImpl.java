package eu.heliovo.clientapi.processing.context.impl;

import java.util.Date;

import org.astrogrid.schema.agparameterdefinition.v1.ParameterValue;
import org.astrogrid.schema.agworkflow.v1.Input;
import org.astrogrid.schema.agworkflow.v1.Output;
import org.astrogrid.schema.agworkflow.v1.Tool;

import eu.heliovo.clientapi.processing.ProcessingResult;
import eu.heliovo.clientapi.processing.context.AbstractContextServiceImpl;
import eu.heliovo.clientapi.processing.context.SimpleParkerModelService;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.shared.util.AssertUtil;
import eu.heliovo.shared.util.DateUtil;

public class SimpleParkerModelServiceImpl extends AbstractContextServiceImpl implements SimpleParkerModelService {
    /**
     * The start date
     */
    private Date startDate;
    
    public SimpleParkerModelServiceImpl(String name, String description, AccessInterface[] accessInterfaces) {
        super(name, description, accessInterfaces);
    }
    
    @Override
    public ProcessingResult parkerModel(Date startDate) {
        this.startDate = startDate;
        return execute();
    }

    @Override
    protected Tool initTool() {
        AssertUtil.assertArgumentNotNull(startDate, "startDate");
        
        Tool tool = new Tool();
        tool.setInterface("simple");
        tool.setName("ivo://helio-vo.eu/cxs/parkermodel");
        Input input = new Input();
        ParameterValue startTime = createParameterValue("startDate", DateUtil.toIsoDateString(startDate), false);
        input.getParameter().add(startTime);
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
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}
