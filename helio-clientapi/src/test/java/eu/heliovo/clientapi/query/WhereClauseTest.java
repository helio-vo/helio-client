package eu.heliovo.clientapi.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.GenericXmlApplicationContext;

import eu.heliovo.registryclient.HelioServiceName;

public class WhereClauseTest {
    private WhereClauseFactoryBeanImpl whereClauseFactoryBean;

    @Before
    public void setUp() {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext("classpath:spring/clientapi-main-test.xml");
        this.whereClauseFactoryBean = (WhereClauseFactoryBeanImpl) context.getBean("whereClauseFactoryBean");
    }
    

    @Test public void createWhereClause_noConfig() {
        WhereClauseFactoryBeanImpl whereClauseFactoryBean = newWhereClauseFactoryBean();
        HelioServiceName testService = HelioServiceName.register("TEST", "test");
        WhereClause clause = whereClauseFactoryBean.createWhereClause(testService, "test");
        assertNotNull(clause);
        assertNotNull(clause.getFieldDescriptors());
        assertEquals(0, clause.getFieldDescriptors().size());
    }
    
    @Test public void createWhereClause_hecConfig_existingCatalog() {
        WhereClauseFactoryBeanImpl whereClauseFactoryBean = newWhereClauseFactoryBean();
        WhereClause clause = whereClauseFactoryBean.createWhereClause(HelioServiceName.HEC, "goes_sxr_flare");
        assertNotNull(clause);
        assertNotNull(clause.getFieldDescriptors());
        assertEquals(10, clause.getFieldDescriptors().size());
    }
    
    /**
     * Create and configure the where clause factory bean.
     * @return
     */
    private WhereClauseFactoryBeanImpl newWhereClauseFactoryBean() {
        return whereClauseFactoryBean;
    }
    
    
    
}
