package eu.heliovo.mockclient.query.paramquery;

import eu.heliovo.clientapi.model.field.HelioField;
import eu.heliovo.mockclient.model.catalog.HecCatalogRegistry;
import eu.heliovo.mockclient.query.longrunningquery.LongRunningQueryServiceFactory;
/**
 * Mock implementation of the param query for the HEC service. 
 * @author marco soldati at fhnw ch 
 */
public class HecMockParamQueryServiceImpl extends AbstractParamQueryServiceImpl {

	/**
	 * Keep a reference to the HecCatalogRegistry
	 */
	private HecCatalogRegistry catalogRegistry = HecCatalogRegistry.getInstance();
	
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
