package eu.heliovo.clientapi.query.paramquery.impl;

import eu.heliovo.clientapi.model.field.HelioField;
import eu.heliovo.clientapi.model.service.dao.HecCatalogueDescriptorDao;
import eu.heliovo.registryclient.HelioServiceName;
/**
 * Default implementation of the param query for the HEC service. 
 * @author marco soldati at fhnw ch 
 */
public class HecParamQueryServiceImpl extends AbstractParamQueryServiceImpl {

	/**
	 * Keep a reference to the hecDao
	 */
	private HecCatalogueDescriptorDao hecCatalogueDescriptorDao;
	
	/**
	 * Create a mock implementation of the DPAS.
	 */
	public HecParamQueryServiceImpl() {
		super(null);	
	}

	@Override
	public HelioServiceName getServiceName() {
		return queryService.getServiceName();
	}
	
	@Override
	public HelioField<?>[] getFieldDescriptions(String catalog) throws IllegalArgumentException {
		if (catalog == null) {
			return hecCatalogueDescriptorDao.getDomainValues().toArray(new HelioField[0]);
		}
		
		HelioField<?>[] helioFields = null; //77hecCatalogueDescriptorDao.getFields(catalog);
		return helioFields;
	}	

	@Override
	public String getServiceVariant() {
	    return null;
	}
}
