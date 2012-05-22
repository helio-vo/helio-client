package eu.heliovo.clientapi.config.des;

import java.util.ArrayList;
import java.util.List;

/**
 * The DES mission config
 * @author MarcoSoldati
 *
 */
public class DesMission {
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
    
}
