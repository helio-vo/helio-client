package eu.heliovo.clientapi.processing.context.impl;

import java.util.Date;
import java.util.List;
import java.util.logging.LogRecord;

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
    private Date startDate;

    /**
     * Velocity of the parker spiral
     */
    private int velocity = 400;

    /**
     * ?
     */
    private int outer = 0;
    
    /**
     * ?
     */
    private int inner = 4;
    /**
     * The parker model plotter.
     * @param accessInterfaces
     */
    public SimpleParkerModelServiceImpl(AccessInterface[] accessInterfaces) {
        super(HelioServiceName.CXS, null, accessInterfaces);
    }
    
    @Override
    public ProcessingResult<UrlProcessingResultObject> parkerModel(Date startDate) {
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
        ParameterValue startDateVal = createParameterValue("StartDate", DateUtil.toIsoDateString(startDate), false);
        input.getParameter().add(startDateVal);
        ParameterValue velocityVal = createParameterValue("Velocity", "" + this.velocity, false);
        input.getParameter().add(velocityVal);
        ParameterValue innerVal = createParameterValue("Inner", "" + this.inner, false);
        input.getParameter().add(innerVal);
        ParameterValue outerVal = createParameterValue("Outer", "" + this.outer, false);        
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
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
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
    public int getOuter() {
        return outer;
    }

    @Override
    public void setOuter(int outer) {
        this.outer = outer;
    }

    @Override
    public int getInner() {
        return inner;
    }

    @Override
    public void setInner(int inner) {
        this.inner = inner;
    }
    
    
}
