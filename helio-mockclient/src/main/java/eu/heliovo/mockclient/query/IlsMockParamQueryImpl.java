package eu.heliovo.mockclient.query;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import eu.heliovo.clientapi.query.HelioParameter;
import eu.heliovo.shared.util.DateUtil;
import eu.heliovo.workflow.workflows.firstusecase.ListInstruments;
/**
 * Mock implementation of the param query. This implementation returns just a 
 * @author marco soldati at fhnw ch 
 */
public class IlsMockParamQueryImpl extends AbstractMockParamQueryImpl {
	
	
	private final HelioParameter<?>[] ilsMockParameters = new HelioParameter[] {
		new HelioParameter<Date>("STARTTIME", "the start date. Format: 2005-01-01T00:00:00", "xsd:dateTime"),
		new HelioParameter<Date>("ENDTIME", "the end date.  Format: 2005-01-11T00:00:00", "xsd:dateTime"),
		new HelioParameter<Integer>("delay", "Add an artifical delay in milliseconds to the service. For testing purposes only.", "xsd:int", new Integer(0)),
		new HelioParameter<Float>("exception-probability", "Add a probability for an exception to the service. no exception if <=0, always an exception if > 1.0. For testing purposes only.", "xsd:float", new Float(0))
	};
	
	public IlsMockParamQueryImpl() {
		super("./resource/ils.xml");
	}
	
	@Override
	public HelioParameter<?>[] getParameterDescription(Map<String, ? extends Object> context) {
		return ilsMockParameters;
	}
	
	@Override
	public String getResourceId() {
		return "ils";
	}
	
	@Override
	public String getName() {
		return "ils";
	}

	@Override
	public String getDescription() {
		return "Instrument Location Service";
	}
	
	@Override
	protected InputStream performQuery(Map<String, ? extends Object> parameters) throws Exception {
		ByteArrayOutputStream baos=new ByteArrayOutputStream();

		Date startTime = getAs(parameters, "STARTTIME", Date.class, new Date(1104534000328l)); // "2005-01-01T00:00:00
	    Date endTime = getAs(parameters, "ENDTIME", Date.class, new Date(1104620400921l)); // "2005-01-02T00:00:00    
	    ListInstruments.runWorkflow(new PrintWriter(baos), 
	    		Collections.singletonList(DateUtil.toIsoDateString(startTime)), 
	    		Collections.singletonList(DateUtil.toIsoDateString(endTime)));
	    
		return new ByteArrayInputStream(baos.toByteArray());
	}
}
