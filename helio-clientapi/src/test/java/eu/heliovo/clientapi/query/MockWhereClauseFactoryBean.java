package eu.heliovo.clientapi.query;

import java.util.ArrayList;
import java.util.List;

import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;
import eu.heliovo.clientapi.model.field.type.FieldType;
import eu.heliovo.registryclient.HelioServiceName;

public class MockWhereClauseFactoryBean implements WhereClauseFactoryBean {

    @Override
    public WhereClause createWhereClause(HelioServiceName helioServiceName, String listName) {
        List<HelioFieldDescriptor<?>> fieldDescriptors = new ArrayList<HelioFieldDescriptor<?>>();
        HelioFieldDescriptor<Integer> fieldDescriptor = new HelioFieldDescriptor<Integer>("testId", "testName", "testDescription", FieldType.INTEGER);
        HelioFieldDescriptor<Integer> fieldDescriptor2 = new HelioFieldDescriptor<Integer>("testId2", "testName2", "testDescription2", FieldType.INTEGER);
        
        
        fieldDescriptors.add(fieldDescriptor);
        fieldDescriptors.add(fieldDescriptor2);
        
        return new WhereClause("test", fieldDescriptors );
    }

}
