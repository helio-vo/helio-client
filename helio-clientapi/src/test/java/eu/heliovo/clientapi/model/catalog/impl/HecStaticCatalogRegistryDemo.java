package eu.heliovo.clientapi.model.catalog.impl;

import eu.heliovo.clientapi.model.catalog.HelioCatalogDao;
import eu.heliovo.clientapi.model.field.DomainValueDescriptor;
import eu.heliovo.clientapi.model.field.HelioField;

public class HecStaticCatalogRegistryDemo {

    public static void main(String[] args) {
        HelioCatalogDao hecDao = HelioCatalogDaoFactory.getInstance().getHelioCatalogDao("hec");
        
        for (DomainValueDescriptor<String> c : hecDao.getCatalogField().getValueDomain()) {
            System.out.println(c.getValue());
            for (HelioField<?> hf : hecDao.getFields(c.getValue()))
                System.out.println("    " + hf.getId());
        }
    }
}
