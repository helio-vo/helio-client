package eu.heliovo.clientapi.frontend;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import net.ivoa.xml.votable.v1.VOTABLE;
import eu.helio_vo.xml.queryservice.v0.HelioQueryServiceService;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.HelioQueryService;
import eu.heliovo.clientapi.query.longrunningquery.impl.LongRunningQueryServiceFactory;
import eu.heliovo.clientapi.query.syncquery.impl.SyncQueryServiceFactory;
import eu.heliovo.clientapi.registry.HelioServiceDescriptor;
import eu.heliovo.clientapi.registry.HelioServiceRegistry;
import eu.heliovo.clientapi.registry.HelioServiceType;
import eu.heliovo.clientapi.registry.impl.StaticHelioRegistryImpl;
import eu.heliovo.shared.util.AssertUtil;

/**
 * Facade for the HELIO frontend.
 * 
 * @author DavidGuevara, SimonFelix, MarcoSoldati
 * 
 */
public class SimpleInterface {
	/**
	 * which service type shall we use.
	 */
	private static final HelioServiceType SERVICE_TYPE = HelioServiceType.LONGRUNNING_QUERY_SERVICE;
	
	/**
	 * The registry used to lookup the service
	 */
	private static HelioServiceRegistry registry = StaticHelioRegistryImpl.getInstance();

	/**
	 * Execute a query on the specified server.
	 * @param serviceName the name of the service to query
	 * @param startTime the range of start times
	 * @param endTime the range of end times.
	 * @param from the name of the table to use
	 * @param where the where clause to use.
	 * @return
	 */
	public static ResultVT queryService(String serviceName, List<String> startTime, List<String> endTime, List<String> from, String where) {
		AssertUtil.assertArgumentHasText(serviceName, "serviceName");
		AssertUtil.assertArgumentNotEmpty(startTime, "startTime");
		AssertUtil.assertArgumentNotEmpty(endTime, "endTime");
		int maxrecords = 0;
		int startindex = 0;

		// timeout to wait for a response
		int timeout = 15;
		
		int numberOfDatePairs = startTime.size();
		int numberOfFromSingles = from.size();

		if (serviceName.equalsIgnoreCase("DPAS")) {
			startTime = normalizeList(numberOfFromSingles, startTime);
			endTime = normalizeList(numberOfFromSingles, endTime);
			from = normalizeList(numberOfDatePairs, from);
			timeout = 30;
		}
		
		HelioQueryService service;
		if (SERVICE_TYPE == HelioServiceType.LONGRUNNING_QUERY_SERVICE) {
			HelioServiceDescriptor descriptor = registry.getServiceDescriptor(serviceName.toUpperCase(), SERVICE_TYPE);
			service = LongRunningQueryServiceFactory.getInstance().getLongRunningQueryService(descriptor);
		} else 		if (SERVICE_TYPE == HelioServiceType.LONGRUNNING_QUERY_SERVICE) {
			HelioServiceDescriptor descriptor = registry.getServiceDescriptor(serviceName.toUpperCase(), SERVICE_TYPE);
			service = SyncQueryServiceFactory.getInstance().getSyncQueryService(descriptor);
		} else {
			throw new RuntimeException("Internal Error: Unknown service type " + SERVICE_TYPE);
		}
		
		HelioQueryResult result = service.query(startTime, endTime, from, where, maxrecords, startindex, null);

		VOTABLE voTable = result.asVOTable(timeout, TimeUnit.SECONDS);
		ResultVT resvt = new ResultVT(voTable);

		return resvt;
	}

	 
	/**
	 * Name of the query service
	 */
	private static final QName SERVICE_NAME = new QName("http://helio-vo.eu/xml/QueryService/v0.1", "HelioQueryServiceService");
  
	
	  public static ResultVT queryService(List<String> minDate,List<String> maxDate,List<String> from,String portAddress,String where)
	  {
	    int maxrecords=0;
	    int startindex=0;
	    
	    if(minDate.size()==0)
	      return null;
	    
	    if(maxDate.size()==0)
	      return null;
	    
	    URL portURL;
		try {
			portURL = new URL(portAddress);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Illegal port address found: " + portAddress);
		}
	    
	    HelioQueryServiceService service=new HelioQueryServiceService(portURL, SERVICE_NAME);
	    eu.helio_vo.xml.queryservice.v0.HelioQueryService port = service.getHelioQueryServicePort();
	    int numberOfDatePairs=minDate.size();
	    int numberOfFromSingles=from.size();
	    
	    if(portAddress.equals(PortDirectory.DPAS))
	    {
	      minDate = normalizeList(numberOfFromSingles,minDate);
	      maxDate = normalizeList(numberOfFromSingles,maxDate);
	      from = normalizeList(numberOfDatePairs,from);
	    }
	    
	    BindingProvider bp = (BindingProvider) port;
	    bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, portAddress);
	    VOTABLE result = port.query(minDate,maxDate, from,where,null, maxrecords, startindex, null);
	    
	    
	    ResultVT resvt= new ResultVT(result);
	    
	    return resvt;
	  }

	
	public static String test(List<String> minDate, List<String> maxDate, List<String> from, String portAddress, String where) {
		return "test: " + portAddress + " " + where;
	}

	// TODO: need to check if functionality will hold for propagation model
	// usecase
	private static List<String> normalizeList(int max, List<String> list) {
		List<String> result = new ArrayList<String>();

		for (int i = 0; i < max; i++)
			result.addAll(list);

		return result;
	}
}
