package eu.heliovo.clientapi.config.des;

import java.util.ArrayList;
import java.util.List;

import eu.heliovo.clientapi.model.DomainValueDescriptor;

/**
 * The DES mission config
 * @author MarcoSoldati
 *
 */
public class DesMission implements DomainValueDescriptor<String> {
    private final String id;
    private final String name;
    private final List<DesDataset> datasets = new ArrayList<DesDataset>();
    
    /**
     * The des mission config
     * @param id
     * @param name
     */
    public DesMission(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    /**
     * Add a new dataset to the mission.
     * @param dataset the dataset to add
     * @return the added dataset.
     */
    public DesDataset addDataset(DesDataset dataset) {
        this.datasets.add(dataset);
        return dataset;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public List<DesDataset> getDatasets() {
        return datasets;
    }

    //******************** domain value descriptor ******************************
     
    @Override
    public String getValue() {
        return id;
    }

    @Override
    public String getLabel() {
        return name;
    }

    @Override
    public String getDescription() {
        return null;
    }

}
