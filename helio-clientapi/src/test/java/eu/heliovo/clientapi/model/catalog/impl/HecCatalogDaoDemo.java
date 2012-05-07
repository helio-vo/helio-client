package eu.heliovo.clientapi.model.catalog.impl;

import org.springframework.context.support.GenericXmlApplicationContext;

import eu.heliovo.clientapi.model.catalog.HelioCatalogDao;
import eu.heliovo.clientapi.model.field.DomainValueDescriptor;
import eu.heliovo.clientapi.model.field.HelioField;

public class HecCatalogDaoDemo {

    public static void main(String[] args) {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext("classpath:spring/clientapi-main.xml");
        HelioCatalogDao hecDao =  (HelioCatalogDao) context.getBean("hecDao");
        
        for (DomainValueDescriptor<String> c : hecDao.getCatalogField().getValueDomain()) {
            System.out.println(c.getValue());
            for (HelioField<?> hf : hecDao.getFields(c.getValue()))
                System.out.println("    " + hf.getId());
        }
    }
}
