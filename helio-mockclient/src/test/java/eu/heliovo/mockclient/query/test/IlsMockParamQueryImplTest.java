package eu.heliovo.mockclient.query.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.Test;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;

import eu.heliovo.clientapi.query.HelioParameter;
import eu.heliovo.clientapi.result.HelioQueryResult;
import eu.heliovo.clientapi.result.HelioWorkerService.Phase;
import eu.heliovo.mockclient.query.IlsMockParamQueryImpl;

/**
 * Test the {@link IlsMockParamQueryImpl}.
 * @author marco soldati at fhnw ch
 *
 */
public class IlsMockParamQueryImplTest {

	
	@Test
	public void testIlsQuery() {
		IlsMockParamQueryImpl mockParamQueryImpl = new IlsMockParamQueryImpl();
		ConversionServiceFactoryBean conversionServiceFactory = new ConversionServiceFactoryBean();
		conversionServiceFactory.afterPropertiesSet();
		ConversionService conversionService = conversionServiceFactory.getObject();
		mockParamQueryImpl.setConversionService(conversionService);
		
		HelioParameter<?>[] params = mockParamQueryImpl.getParameterDescription(new HashMap<String, Object>());		
		//System.out.println(Arrays.toString(params));
		assertEquals(4, params.length);
		
		HelioParameter<?> param = params[0];
		assertEquals("STARTTIME", param.getParamName());
		assertEquals("the start date. Format: 2005-01-01T00:00:00", param.getDescription());
		assertEquals("xsd:dateTime", param.getXsdType());
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("STARTTIME", new Date());
		map.put("ENDTIME", new Date());		
		HelioQueryResult result = mockParamQueryImpl.query(map);

		assertNotNull(result.getResultId());
		InputStream voTable = result.asVOTable();
		assertNotNull(voTable);

		for (LogRecord msg : result.getUserLogs()) {
			assertTrue(msg.getMessage(), msg.getLevel().intValue() < Level.WARNING.intValue());
			if (msg.getThrown() != null) {
				msg.getThrown().printStackTrace();
			}
		}
		
		assertEquals(Phase.COMPLETED, result.getPhase());		
	}
}
