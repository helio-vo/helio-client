package eu.heliovo.mockclient.query.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;

import eu.heliovo.clientapi.model.field.HelioField;
import eu.heliovo.mockclient.query.paramquery.HecMockParamQueryServiceImpl;

/**
 * Test the {@link HecMockParamQueryServiceImpl}.
 * @author marco soldati at fhnw ch
 *
 */
public class HecMockParamQueryImplTest {

	
	@Test
	public void testHecQuery() throws Exception {
		HecMockParamQueryServiceImpl mockParamQueryImpl = new HecMockParamQueryServiceImpl();
		ConversionServiceFactoryBean conversionServiceFactory = new ConversionServiceFactoryBean();
		conversionServiceFactory.afterPropertiesSet();
		ConversionService conversionService = conversionServiceFactory.getObject();
		mockParamQueryImpl.setConversionService(conversionService);

		Assert.assertEquals("hec", mockParamQueryImpl.getName());
		
		HelioField<?>[] catalogField = mockParamQueryImpl.getFieldDescriptions(null);
		//System.out.println(Arrays.toString(params));
		
		assertNotNull(catalogField);
		assertEquals(1, catalogField.length);
		assertNotNull(catalogField[0].getValueDomain());
		assertEquals(2, catalogField[0].getValueDomain().length);
		
		HelioField<?>[] fields = mockParamQueryImpl.getFieldDescriptions((String) catalogField[0].getValueDomain()[0].getValue());
		assertNotNull(fields);
		assertEquals(5, fields);
		HelioField<?> field = fields[0];
		
		assertEquals("STARTTIME", field.getFieldName());
		assertEquals("the start date. Format: java.util.Date or 2005-01-01T00:00:00", field.getDescription());
		assertEquals("xsd:dateTime", field.getType());
		
		final Map<String, Object> map = new HashMap<String, Object>();
		
		final Calendar cal = Calendar.getInstance(Locale.ROOT);
		cal.set(2008, Calendar.JANUARY, 1, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		map.put("STARTTIME", cal.getTime());
		cal.add(Calendar.DAY_OF_MONTH, 3);
		map.put("ENDTIME", cal.getTime());

//		HelioQueryResult result = mockParamQueryImpl.query(map);
//		assertNotNull(result.getResultId());
//		InputStream voTable = result.asVOTable();
//		assertNotNull(voTable);
//		
//		BufferedReader br = new BufferedReader(new InputStreamReader(voTable, "UTF-8"));
//        final int CHARS_PER_PAGE = 5000; //counting spaces
//        StringBuilder builder = new StringBuilder(CHARS_PER_PAGE);
//        try {
//            for(String line=br.readLine(); line!=null; line=br.readLine()) {
//                builder.append(line);
//                builder.append('\n');
//            }
//        } catch (IOException ignore) { }
////        String text = builder.toString();
////        System.out.println(text);
//
//		for (LogRecord msg : result.getUserLogs()) {
//			assertTrue(msg.getMessage(), msg.getLevel().intValue() >= Level.WARNING.intValue());
//			if (msg.getThrown() != null) {
//				msg.getThrown().printStackTrace();
//			}
//		}
//		
//		assertEquals(Phase.COMPLETED, result.getPhase());
	}
}
