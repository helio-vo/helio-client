package eu.heliovo.clientapi.config.des;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A data set covered by a DES mission
 * @author MarcoSoldati
 *
 */
public class DesDataset {
    private final String id;
    private final String type;
    private final String measurementType;
    private final String instrument;
    private final int cadence;
    private final Date startTime;
    private final Date endTime;
    private final List<DesParam> params = new ArrayList<DesParam>();
    
    
    public DesDataset(String id, String type, String measurementType, String instrument, int cadence, Date startTime, Date endTime) {
        this.id = id;
        this.type = type;
        this.measurementType = measurementType;
        this.instrument = instrument;
        this.cadence = cadence;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    /**
     * Add a des param.
     * @param param the param to add.
     * @return the added param.
     */
    public DesParam addParam(DesParam param) {
        this.params.add(param);
        return param;
    }
    
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }
    /**
     * @return the measurementType
     */
    public String getMeasurementType() {
        return measurementType;
    }
    /**
     * @return the instrument
     */
    public String getInstrument() {
        return instrument;
    }
    /**
     * @return the cadence
     */
    public int getCadence() {
        return cadence;
    }
    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }
    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }
    
    public List<DesParam> getParams() {
        return params;
    }
}
