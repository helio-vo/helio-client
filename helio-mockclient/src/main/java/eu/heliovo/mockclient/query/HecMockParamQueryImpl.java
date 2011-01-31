package eu.heliovo.mockclient.query;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import eu.heliovo.clientapi.query.HelioParameter;
import eu.heliovo.shared.util.DateUtil;
import eu.heliovo.workflow.workflows.firstusecase.GetEvents;
/**
 * Mock implementation of the param query for the HEC service. 
 * @author marco soldati at fhnw ch 
 */
public class HecMockParamQueryImpl extends AbstractMockParamQueryImpl {
	/**
	 * Specify the hec parameters.
	 */
	private final HelioParameter<?>[] hecMockParameters = new HelioParameter[] {
		new HelioParameter<Date>("STARTTIME", "the start date. Format: java.util.Date or 2005-01-01T00:00:00", "xsd:dateTime"),
		new HelioParameter<Date>("ENDTIME", "the end date. Format: java.util.Date or 2005-01-01T00:00:00", "xsd:dateTime"),
		new HelioParameter<String>("EVENTLIST", "select the event list", "xsd:string", new String[] {"goeseventlist", "hessiflarelist"}),
		new HelioParameter<Integer>("delay", "Add an artifical delay in milliseconds to the service. For testing purposes only.", "xsd:integer", new Integer(0)),
		new HelioParameter<Float>("exception-probability", "Add a probability that the service throws an exception. " +
				"if <=0: never throw an exception, if >= 1.0: always throw an exception. " +
				"For testing purposes only.", "xsd:float", new Float(0))
	};
	
	/**
	 * Create a mock implementation of the DPAS.
	 */
	public HecMockParamQueryImpl() {
		super("./resource/hec_goes_xray.xml");		
	}
	
	@Override
	public String getResourceId() {
		return "hec";
	}
		
	@Override
	public String getName() {
		return "hec";
	}
	
	
	@Override
	public String getDescription() {
		return "Helio Event Catalogue";
	}
	
	@Override
	public HelioParameter<?>[] getParameterDescription(Map<String, ? extends Object> context) {	
		return hecMockParameters;
	}
		
	@Override
	protected InputStream performQuery(Map<String, ? extends Object> parameters) throws Exception {
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		
	    Date startTime = getAs(parameters, "STARTTIME", Date.class, new Date(1104534000328l)); // "2005-01-01T00:00:00
	    Date endTime = getAs(parameters, "ENDTIME", Date.class, new Date(1104620400921l)); // "2005-01-02T00:00:00    
		GetEvents.runWorkflow(new PrintWriter(baos), DateUtil.toIsoDateString(startTime), DateUtil.toIsoDateString(endTime));
		
		return new ByteArrayInputStream(baos.toByteArray());
	}
}
