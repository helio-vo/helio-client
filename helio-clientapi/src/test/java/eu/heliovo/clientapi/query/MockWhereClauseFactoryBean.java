package eu.heliovo.clientapi.query;

import java.util.ArrayList;
import java.util.List;

import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;
import eu.heliovo.registryclient.HelioServiceName;

public class MockWhereClauseFactoryBean implements WhereClauseFactoryBean {

    @Override
    public WhereClause createWhereClause(HelioServiceName helioServiceName, String listName) {
        List<HelioFieldDescriptor<?>> fieldDescriptors = new ArrayList<HelioFieldDescriptor<?>>();
        return new WhereClause("test", fieldDescriptors );
    }

}
