package eu.heliovo.clientapi.query.local;

import eu.heliovo.registryclient.HelioServiceName;

/**
 * Test for {@link LocalQueryServiceImpl} with HEC
 * @author junia schoch at fhnw ch
 *
 */
public class LocalHecQueryServiceTest extends AbstractLocalQueryServiceTest{

	@Override
	public HelioServiceName getHelioServiceName() {
		return HelioServiceName.HEC;
	}
	
}
