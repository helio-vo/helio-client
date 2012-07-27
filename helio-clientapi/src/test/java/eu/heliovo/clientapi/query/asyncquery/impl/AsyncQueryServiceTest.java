package eu.heliovo.clientapi.query.asyncquery.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import eu.helio_vo.xml.longqueryservice.v0.StatusValue;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.asyncquery.impl.MockQueryService.MockPort;
import eu.heliovo.clientapi.workerservice.HelioWorkerServiceHandler.Phase;
import eu.heliovo.clientapi.workerservice.JobExecutionException;

public class AsyncQueryServiceTest {
	
	@Test public void testLongQuery() {
		StatusValue[] statusSequence = new StatusValue[] {StatusValue.PENDING, StatusValue.PENDING, StatusValue.PENDING, StatusValue.COMPLETED};
		URL resultFile = getDefaultVoTable();
		
		MockPort port = new MockPort("testid", resultFile, statusSequence, 0, 0, 0);
		MockQueryService service = new MockQueryService(port);
		assertNotNull(service.getServiceName());
		
		HelioQueryResult result = service.query(Arrays.asList("2003-02-01T00:00:00", "2003-02-02T00:00:00"), Arrays.asList("2003-02-10T00:00:00", "2003-02-12T00:00:00"), Arrays.asList("instrument"), null, 100, 0, null);
		
		assertEquals(Phase.PENDING, result.getPhase());
		assertEquals(Phase.PENDING, result.getPhase());
		assertEquals(Phase.PENDING, result.getPhase());
		assertEquals(Phase.COMPLETED, result.getPhase());
		
		assertNotNull(result.asURL());
		assertNotNull(result.asVOTable());
		
		// test invalid calls
		try {
			service.timeQuery(Arrays.asList("2003-02-01T00:00:00", "2003-02-02T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("instrument"), 100, 0);
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			// we're fine
		}
		try {
			service.timeQuery(Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00", "2003-02-12T00:00:00"), Arrays.asList("instrument"), 100, 0);
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			// we're fine
		}
		try {
			service.timeQuery(Arrays.asList("2003-02-01T00:00:00", "2003-02-02T00:00:00"), Arrays.asList("2003-02-10T00:00:00", "2003-02-12T00:00:00"), Arrays.asList("instrument", "instrument2", "instrument3"), 100, 0);
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			// we're fine
		}
		
		// test variations of time query
        result = service.query("2003-02-01T00:00:00", "2003-02-10T00:00:00", "instrument", null, 100, 0, null);
        assertEquals(Phase.COMPLETED, result.getPhase());
        
		assertNotNull(result.toString());
	}
	
	@Test public void testLongTimeQuery() {
		StatusValue[] statusSequence = new StatusValue[] {StatusValue.PENDING, StatusValue.PENDING, StatusValue.PENDING, StatusValue.COMPLETED};
		URL resultFile = getDefaultVoTable();
		
		MockPort port = new MockPort("testid", resultFile, statusSequence, 0, 0, 0);
		MockQueryService service = new MockQueryService(port);
		assertNotNull(service.getServiceName());
		
		HelioQueryResult result = service.timeQuery(Arrays.asList("2003-02-01T00:00:00", "2003-02-02T00:00:00"), Arrays.asList("2003-02-10T00:00:00", "2003-02-12T00:00:00"), Arrays.asList("instrument"), 100, 0);
		
		assertEquals(Phase.PENDING, result.getPhase());
		assertEquals(Phase.PENDING, result.getPhase());
		assertEquals(Phase.PENDING, result.getPhase());
		assertEquals(Phase.COMPLETED, result.getPhase());
		
		assertNotNull(result.asURL());
		assertNotNull(result.asVOTable());
		
		// test invalid calls
		try {
			service.timeQuery(Arrays.asList("2003-02-01T00:00:00", "2003-02-02T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("instrument"), 100, 0);
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			// we're fine
		}
		try {
			service.timeQuery(Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00", "2003-02-12T00:00:00"), Arrays.asList("instrument"), 100, 0);
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			// we're fine
		}
		try {
			service.timeQuery(Arrays.asList("2003-02-01T00:00:00", "2003-02-02T00:00:00"), Arrays.asList("2003-02-10T00:00:00", "2003-02-12T00:00:00"), Arrays.asList("instrument", "instrument2", "instrument3"), 100, 0);
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			// we're fine
		}
		
		assertNotNull(result.toString());
		
	    result = service.timeQuery("2003-02-01T00:00:00", "2003-02-10T00:00:00", "instrument", 100, 0);

	}
	
	/**
	 * Test a time query that gives an error.
	 */
	@Test public void testErrorTimeQuery() {
		StatusValue[] statusSequence = new StatusValue[] {StatusValue.PENDING, StatusValue.ERROR};
		URL resultFile = getDefaultVoTable();
		MockPort port = new MockPort("testid", resultFile, statusSequence, 0, 0, 0);
		MockQueryService service = new MockQueryService(port);
		HelioQueryResult result = service.timeQuery(Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("instrument1", "instrument2"), 100, 0);
		assertEquals(Phase.PENDING, result.getPhase());
		assertEquals(Phase.ERROR, result.getPhase());
		
		try {
			result.asURL();
			fail("JobExecutionException expected");
		} catch (JobExecutionException e) {
			// we're fine
		}
		try {
			result.asVOTable();
			fail("JobExecutionException expected");
		} catch (JobExecutionException e) {
			// we're fine
		}
		assertNotNull(result.toString());
	}

	/**
	 * Test a time query that produces a timeout on the server side.
	 */
	@Test public void testServiceTimeoutQuery() {
		StatusValue[] statusSequence = new StatusValue[] {StatusValue.PENDING, StatusValue.TIMEOUT};
		URL resultFile = getDefaultVoTable();
		MockPort port = new MockPort("testid", resultFile, statusSequence, 0, 0, 0);
		MockQueryService service = new MockQueryService(port);
		HelioQueryResult result = service.timeQuery(Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("instrument1", "instrument2"), 100, 0);
		assertEquals(Phase.PENDING, result.getPhase());
		assertEquals(Phase.ABORTED, result.getPhase());
		
		try {
			result.asURL();
			fail("JobExecutionException expected");
		} catch (JobExecutionException e) {
			// we're fine
		}
		try {
			result.asVOTable();
			fail("JobExecutionException expected");
		} catch (JobExecutionException e) {
			// we're fine
		}
		assertNotNull(result.toString());
	}

	/**
	 * Test a time query that produces a timeout on the client side.
	 */
	@Test public void testClientTimeoutQuery() {
		StatusValue[] statusSequence = new StatusValue[10];
		Arrays.fill(statusSequence, StatusValue.PENDING);
		statusSequence[statusSequence.length - 1] = StatusValue.COMPLETED;
		
		URL resultFile = getDefaultVoTable();
		MockPort port = new MockPort("testid", resultFile, statusSequence, 200, 200, 200);
		MockQueryService service = new MockQueryService(port);
		HelioQueryResult result = service.timeQuery(Arrays.asList("2003-02-01T00:00:00"), Arrays.asList("2003-02-10T00:00:00"), Arrays.asList("instrument1", "instrument2"), 100, 0);
		// poll one time
		assertEquals(Phase.PENDING, result.getPhase());
		
		try {
			// poll three times
			result.asURL(700, TimeUnit.MILLISECONDS);
			fail("JobExecutionException expected");
		} catch (JobExecutionException e) {
			// we're fine
		}
		assertNotNull(result.toString());
		try {
			// poll another three times
			result.asVOTable(700, TimeUnit.MILLISECONDS);
			fail("JobExecutionException expected");
		} catch (JobExecutionException e) {
			// we're fine
		}
		assertNotNull(result.toString());
		
		try {
			// poll once
			result.asURL(0, TimeUnit.MILLISECONDS);
			fail("JobExecutionException expected");
		} catch (JobExecutionException e) {
			// we're fine
		}
		assertNotNull(result.toString());
		try {
			// poll once again
			result.asVOTable(0, TimeUnit.MILLISECONDS);
			fail("JobExecutionException expected");
		} catch (JobExecutionException e) {
			// we're fine
		}
		assertNotNull(result.toString());
		
		assertEquals(0, result.getExecutionDuration());
		assertNull(result.getDestructionTime());
		
		// poll once more
		assertEquals(Phase.COMPLETED, result.getPhase());

		assertTrue(result.getExecutionDuration() >= 2000);
		assertNotNull(result.getDestructionTime());
	}
	

	private URL getDefaultVoTable() {
		String resource = "/eu/heliovo/clientapi/query/resource/hec_goes_xray.xml";
		URL resultFile = getClass().getResource(resource);
		assertNotNull("resource not found: " + resource, resultFile);
		return resultFile;
	}
}
