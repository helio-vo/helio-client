package eu.heliovo.clientapi.query;

import java.util.ArrayList;
import java.util.List;

import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;
import eu.heliovo.clientapi.model.field.type.FieldType;
import eu.heliovo.registryclient.HelioServiceName;

public class MockWhereClauseFactoryBean implements WhereClauseFactoryBean {
	private List<HelioFieldDescriptor<?>> fieldDescriptors = new ArrayList<HelioFieldDescriptor<?>>();
	
	public MockWhereClauseFactoryBean() {
		 HelioFieldDescriptor<Integer> fieldDescriptor = new HelioFieldDescriptor<Integer>("testId", "testName", "testDescription", FieldType.INTEGER);
		 fieldDescriptors.add(fieldDescriptor);
	}
	
	public MockWhereClauseFactoryBean(List<HelioFieldDescriptor<?>> fieldDescriptors) {
		 this.fieldDescriptors = fieldDescriptors;
	}

    @Override
    public WhereClause createWhereClause(HelioServiceName helioServiceName, String listName) {
        return new WhereClause("test", fieldDescriptors );
    }
}
