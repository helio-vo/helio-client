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
import eu.heliovo.clientapi.query.asyncquery.impl.AsyncQueryServiceFactory;
import eu.heliovo.clientapi.query.syncquery.impl.SyncQueryServiceFactory;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;
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
	private static final ServiceCapability DEFAULT_CAPABILITY = ServiceCapability.SYNC_QUERY_SERVICE;
	
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
	    //System.out.println("queryService called");
		AssertUtil.assertArgumentHasText(serviceName, "serviceName");
		AssertUtil.assertArgumentNotEmpty(startTime, "startTime");
		AssertUtil.assertArgumentNotEmpty(endTime, "endTime");

		// set the default values. The may be overwritten further down in the script.
		int maxrecords = 1000;
		int startindex = 0;
		// timeout to wait for a response
		int timeout = 300;
		ServiceCapability serviceCapability = DEFAULT_CAPABILITY;
		
		//if (serviceName.equalsIgnoreCase("DPAS")) {
		// normalize all lists
		int sizeOfTime = startTime.size();
		int sizeOfFrom = from.size();
		//System.out.println("normalizing " + startTime + " - " + endTime + " - " + from + " - " + sizeOfTime + " - " + sizeOfFrom);
		startTime = normalizeList(sizeOfFrom, startTime);
		endTime = normalizeList(sizeOfFrom, endTime);
		from = normalizeList(sizeOfTime, from);
		//System.out.println("normalizing " + startTime + " - " + endTime + " - " + from + " - " + sizeOfTime + " - " + sizeOfFrom);
		//}
		
		HelioServiceName helioServiceName = HelioServiceName.valueOf(serviceName.toUpperCase());
		
		HelioQueryService service;
		if (serviceCapability == ServiceCapability.ASYNC_QUERY_SERVICE) {
			service = AsyncQueryServiceFactory.getInstance().getAsyncQueryService(helioServiceName);
		} else if (serviceCapability == ServiceCapability.SYNC_QUERY_SERVICE) {
			service = SyncQueryServiceFactory.getInstance().getSyncQueryService(helioServiceName);
		} else {
			throw new RuntimeException("Internal Error: Unknown service type " + DEFAULT_CAPABILITY);
		}
		
		HelioQueryResult result = service.query(startTime, endTime, from, where, maxrecords, startindex, null);
		
		VOTABLE voTable = result.asVOTable(timeout, TimeUnit.SECONDS);
		ResultVT resvt = new ResultVT(voTable, result.getUserLogs());

		return resvt;
	}

	 
	/**
	 * Name of the query service
	 */
	private static final QName SERVICE_NAME = new QName("http://helio-vo.eu/xml/QueryService/v0.1", "HelioQueryServiceService");
  
	
	/**
	 * @deprecated use {@link #queryService(String, List, List, List, String)} instead.
	 */
	@Deprecated public static ResultVT queryService(List<String> minDate,List<String> maxDate,List<String> from,String portAddress,String where)
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

	
	// TODO: need to check if functionality will hold for propagation model
	// usecase
	private static List<String> normalizeList(int max, List<String> list) {
		List<String> result = new ArrayList<String>();

		for (int i = 0; i < max; i++)
			result.addAll(list);

		return result;
	}
}
