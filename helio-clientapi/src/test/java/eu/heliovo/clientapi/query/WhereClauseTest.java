package eu.heliovo.clientapi.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import eu.heliovo.clientapi.config.catalog.dao.HecCatalogueDescriptorDao;
import eu.heliovo.clientapi.model.field.type.FieldTypeFactory;
import eu.heliovo.clientapi.query.WhereClause;
import eu.heliovo.clientapi.query.WhereClauseFactoryBean;
import eu.heliovo.clientapi.utils.STILUtils;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.props.HelioFileUtil;

public class WhereClauseTest {

    @Test public void createWhereClause_noConfig() {
        WhereClauseFactoryBean whereClauseFactoryBean = newWhereClauseFactoryBean();
        HelioServiceName testService = HelioServiceName.register("TEST", "test");
        WhereClause clause = whereClauseFactoryBean.createWhereClause(testService, "test");
        assertNotNull(clause);
        assertNotNull(clause.getFieldDescriptors());
        assertEquals(0, clause.getFieldDescriptors().size());
    }
    
    @Test public void createWhereClause_hecConfig_existingCatalog() {
        WhereClauseFactoryBean whereClauseFactoryBean = newWhereClauseFactoryBean();
        WhereClause clause = whereClauseFactoryBean.createWhereClause(HelioServiceName.HEC, "goes_sxr_flare");
        assertNotNull(clause);
        assertNotNull(clause.getFieldDescriptors());
        assertEquals(10, clause.getFieldDescriptors().size());
    }
    
    
    
    /**
     * Create and configure the where clause factory bean.
     * @return
     */
    private WhereClauseFactoryBean newWhereClauseFactoryBean() {
        FieldTypeFactory fieldTypeFactory = new FieldTypeFactory();
        fieldTypeFactory.init();
        HelioFileUtil fileUtil = new HelioFileUtil("test");
        STILUtils stilUtils = new STILUtils();
        stilUtils.setHelioFileUtil(fileUtil);
        
        HecCatalogueDescriptorDao hecCatalogueDescriptorDao = new HecCatalogueDescriptorDao();
        hecCatalogueDescriptorDao.setHelioFileUtil(fileUtil);
        hecCatalogueDescriptorDao.setStilUtils(stilUtils);
        hecCatalogueDescriptorDao.setFieldTypeFactory(fieldTypeFactory);
        hecCatalogueDescriptorDao.init();
        
        WhereClauseFactoryBean whereClauseFactoryBean = new WhereClauseFactoryBean();
        whereClauseFactoryBean.setHecCatalogueDescriptorDao(hecCatalogueDescriptorDao);
        return whereClauseFactoryBean;
    }
    
    
    
}
