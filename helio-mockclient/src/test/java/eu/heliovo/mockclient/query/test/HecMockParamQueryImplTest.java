package eu.heliovo.mockclient.query.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;

import eu.heliovo.clientapi.query.HelioParameter;
import eu.heliovo.clientapi.result.HelioQueryResult;
import eu.heliovo.clientapi.result.HelioWorkerService.Phase;
import eu.heliovo.mockclient.query.HecMockParamQueryImpl;

/**
 * Test the {@link HecMockParamQueryImpl}.
 * @author marco soldati at fhnw ch
 *
 */
public class HecMockParamQueryImplTest {

	
	@Test
	public void testHecQuery() throws Exception {
		HecMockParamQueryImpl mockParamQueryImpl = new HecMockParamQueryImpl();
		ConversionServiceFactoryBean conversionServiceFactory = new ConversionServiceFactoryBean();
		conversionServiceFactory.afterPropertiesSet();
		ConversionService conversionService = conversionServiceFactory.getObject();
		mockParamQueryImpl.setConversionService(conversionService);

		Assert.assertEquals("hec", mockParamQueryImpl.getName());
		
		HelioParameter<?>[] params = mockParamQueryImpl.getParameterDescription(new HashMap<String, Object>());
		//System.out.println(Arrays.toString(params));
		Assert.assertEquals(5, params.length);
		
		HelioParameter<?> param = params[0];
		assertEquals("STARTTIME", param.getParamName());
		assertEquals("the start date. Format: java.util.Date or 2005-01-01T00:00:00", param.getDescription());
		assertEquals("xsd:dateTime", param.getXsdType());
		
		final Map<String, Object> map = new HashMap<String, Object>();
		
		final Calendar cal = Calendar.getInstance(Locale.ROOT);
		cal.set(2008, Calendar.JANUARY, 1, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		map.put("STARTTIME", cal.getTime());
		cal.add(Calendar.DAY_OF_MONTH, 3);
		map.put("ENDTIME", cal.getTime());

		HelioQueryResult result = mockParamQueryImpl.query(map);
		assertNotNull(result.getResultId());
		InputStream voTable = result.asVOTable();
		assertNotNull(voTable);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(voTable, "UTF-8"));
        final int CHARS_PER_PAGE = 5000; //counting spaces
        StringBuilder builder = new StringBuilder(CHARS_PER_PAGE);
        try {
            for(String line=br.readLine(); line!=null; line=br.readLine()) {
                builder.append(line);
                builder.append('\n');
            }
        } catch (IOException ignore) { }
//        String text = builder.toString();
//        System.out.println(text);

		for (LogRecord msg : result.getUserLogs()) {
			assertTrue(msg.getMessage(), msg.getLevel().intValue() >= Level.WARNING.intValue());
			if (msg.getThrown() != null) {
				msg.getThrown().printStackTrace();
			}
		}
		
		assertEquals(Phase.COMPLETED, result.getPhase());		
	}
}
