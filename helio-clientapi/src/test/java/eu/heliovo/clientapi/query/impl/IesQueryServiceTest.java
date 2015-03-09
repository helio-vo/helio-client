package eu.heliovo.clientapi.query.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import net.ivoa.xml.votable.v1.VOTABLE;

import org.junit.Before;
import org.junit.Test;

import eu.heliovo.clientapi.query.local.LocalQueryServiceImpl;
import eu.heliovo.clientapi.utils.VOTableUtils;
import eu.heliovo.clientapi.workerservice.JobExecutionException;


/**
 * Test for {@link IesQueryServiceImpl}.
 * To mock the QueryService properties "mockito" is used. See: http://mockito.org/
 * @author junia schoch at fhnw ch
 *
 */
public class IesQueryServiceTest {
	private static final String SAMPLE_VOTABLE_HEC = "/eu/heliovo/clientapi/utils/resource/testdata_hec_votable.xml";
	private static final String SAMPLE_VOTABLE_ICS = "/eu/heliovo/clientapi/utils/resource/testdata_ics_votable.xml";
	private VOTABLE voTableHec;
	private VOTABLE voTableIcs;
	private IesQueryServiceImpl iesQueryService;
	
	@Before
	public void setup() {
		//Sample VoTables
		voTableHec = getTestVOTable(SAMPLE_VOTABLE_HEC);
		voTableIcs = getTestVOTable(SAMPLE_VOTABLE_ICS);
		
		//Mock QueryService properties
		DpasQueryServiceImpl mockDpas = mock(DpasQueryServiceImpl.class);
		LocalQueryServiceImpl mockHec = mock(LocalQueryServiceImpl.class);
		LocalQueryServiceImpl mockIcs = mock(LocalQueryServiceImpl.class);
		
		//Create new IesQueryServiceImpl
		iesQueryService = new IesQueryServiceImpl();
		iesQueryService.setDpasQueryService(mockDpas);
		iesQueryService.setHecQueryService(mockHec);
		iesQueryService.setIcsQueryService(mockIcs);
	}
	
	@Test
	public void test_setTimeRangesFromVoTable() {
		iesQueryService.setTimeRangesFromVoTable(voTableHec);
		assertEquals(3, iesQueryService.getStartTime().size());
		assertEquals(3, iesQueryService.getEndTime().size());
	}
	
	@Test
	public void test_getIcsInstruments() {
		List<String> instruments = iesQueryService.getIcsInstruments(voTableIcs);
		assertEquals(4, instruments.size());
	}
	
	private VOTABLE getTestVOTable(String path) {
		File file = getTestVOTableFile(path);
		URL url = getTestVoTableUrl(file);
		return VOTableUtils.getInstance().url2VoTable(url);
	}
	
	private File getTestVOTableFile(String path) {
        URL resultFile = getClass().getResource(path);
        assertNotNull("resource not found: " + path, resultFile);
        try {
			return new File(resultFile.toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
    }
	
	private URL getTestVoTableUrl(File voTableFile) {
		try {
            return voTableFile.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new JobExecutionException("Error creating URL from File: " + e.getMessage(), e);
        }
	}
}
