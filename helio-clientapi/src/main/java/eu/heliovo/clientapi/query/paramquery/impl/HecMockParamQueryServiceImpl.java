package eu.heliovo.clientapi.query.paramquery.impl;

import eu.heliovo.clientapi.model.catalog.CatalogRegistry;
import eu.heliovo.clientapi.model.catalog.HecStaticCatalogRegistry;
import eu.heliovo.clientapi.model.field.HelioField;
import eu.heliovo.clientapi.query.longrunningquery.impl.LongRunningQueryServiceFactory;
/**
 * Mock implementation of the param query for the HEC service. 
 * @author marco soldati at fhnw ch 
 */
public class HecMockParamQueryServiceImpl extends AbstractParamQueryServiceImpl {

	/**
	 * Keep a reference to the HecCatalogRegistry
	 */
	private CatalogRegistry catalogRegistry = HecStaticCatalogRegistry.getInstance();
	
	/**
	 * Reference to the query service factory.
	 */
	private static LongRunningQueryServiceFactory queryServiceFactory = LongRunningQueryServiceFactory.getInstance();
	
	/**
	 * Create a mock implementation of the DPAS.
	 */
	public HecMockParamQueryServiceImpl() {
		super(queryServiceFactory.getHecService());	
	}

	@Override
	public String getName() {
		return queryService.getName();
	}
	
	@Override
	public String getDescription() {
		return "Helio Event Catalogue";
	}
	
	@Override
	public HelioField<?>[] getFieldDescriptions(String catalog) throws IllegalArgumentException {
		if (catalog == null) {
			return new HelioField[] {catalogRegistry.getCatalogField()};
		}
		
		HelioField<?>[] helioFields = catalogRegistry.getFields(catalog);
		return helioFields;
	}		
}
