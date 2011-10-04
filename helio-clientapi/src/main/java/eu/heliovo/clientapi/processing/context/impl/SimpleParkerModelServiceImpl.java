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
     * The start date
     */
    private Date startDate;
    
    /**
     * The parker model plotter.
     * @param serviceName the name of the service. Must be {@link HelioServiceName#CXS}
     * @param description
     * @param accessInterfaces
     */
    public SimpleParkerModelServiceImpl(HelioServiceName serviceName, String description, AccessInterface[] accessInterfaces) {
        super(serviceName, description, accessInterfaces);
        AssertUtil.assertArgumentEquals(HelioServiceName.CXS, serviceName, "serviceName");
    }
    
    @Override
    public ProcessingResult parkerModel(Date startDate) {
        this.startDate = startDate;
        return execute();
    }

    @Override
    protected Tool initTool(List<LogRecord> logRecords) {
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
