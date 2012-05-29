package eu.heliovo.clientapi.config.des;

import static eu.heliovo.clientapi.config.des.DesFunctionArgument.DesFunctionOperator.EQ;
import static eu.heliovo.clientapi.config.des.DesFunctionArgument.DesFunctionOperator.GT;
import static eu.heliovo.clientapi.config.des.DesFunctionArgument.DesFunctionOperator.LT;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.heliovo.shared.util.AssertUtil;

/**
 * The configuration of the DES service. Main class of this package.
 * 
 * @author MarcoSoldati
 * 
 */
public class DesConfiguration {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd");

    private final List<DesFunction> functions = new ArrayList<DesFunction>();
    private final List<DesMission> missions = new ArrayList<DesMission>();
    private final List<DesParam> params = new ArrayList<DesParam>();

    public void init() {
        DesParam v = new DesParam("V", "V", "velocity_magnitude", "km/sec");
        params.add(v);
        DesParam n = new DesParam("N", "N", "ion_density", "cm^-3");
        params.add(n);
        DesParam b = new DesParam("B", "B", "magnetic_field_magnitude", "nT");
        params.add(b);
        DesParam bx = new DesParam("BX", "BX", "magnetic_field_x_component", "nT");
        params.add(bx);
        DesParam by = new DesParam("BY", "BY", "magnetic_field_y_component", "nT");
        params.add(by);
        DesParam bz = new DesParam("BZ", "BZ", "magnetic_field_z_component", "nT");
        params.add(bz);

        // ACE
        DesMission mission = new DesMission("ACE", "ACE");
        missions.add(mission);
        DesDataset dataset = new DesDataset("ace:swe:all", "plasma", "thermal plasma", "SWEPAM", 64, parse("1998-02-04"), parse("2010-04-30"));
        dataset.addParam(v);
        dataset.addParam(n);
        mission.addDataset(dataset);
        
        dataset= new DesDataset("ace:imf:all", "mag", "magnetic field", "MAG", 16, parse("1997-09-02"), parse("2011-08-06"));
        dataset.addParam(b);
        dataset.addParam(bx);
        dataset.addParam(by);
        dataset.addParam(bz);
        mission.addDataset(dataset);

        // WIND
        mission = new DesMission("WIND", "WIND");
        missions.add(mission);
        dataset= new DesDataset("wnd:swe:kp", "plasma", "thermal plasma", "SWE", 90, parse("1994-11-17"), parse("2011-08-10"));
        dataset.addParam(v);
        dataset.addParam(n);
        mission.addDataset(dataset);
        
        dataset= new DesDataset("wnd:mfi:kp", "mag", "magnetic field", "MFI", 60, parse("1994-11-16"), parse("2011-07-29"));
        dataset.addParam(b);
        dataset.addParam(bx);
        dataset.addParam(by);
        dataset.addParam(bz);
        mission.addDataset(dataset);
        
        
        // ULYSSES
        mission = new DesMission("ULYSSES", "ULYSSES");
        missions.add(mission);
        dataset= new DesDataset("ulys:bai:mom", "plasma", "thermal plasma", "SWOOPS", 240, parse("1990-11-18"), parse("2009-06-30"));
        dataset.addParam(v);
        mission.addDataset(dataset);

        dataset= new DesDataset("b:ulys:mag", "mag", "magnetic field", "FGM/VHM", 1, parse("1990-10-25"), parse("2011-06-27"));
        dataset.addParam(b);
        dataset.addParam(bx);
        dataset.addParam(by);
        dataset.addParam(bz);
        mission.addDataset(dataset);
          
                  
        // STA
        mission = new DesMission("STA", "STEREO-A");
        missions.add(mission);
        dataset= new DesDataset("sta:l2:pla", "plasma", "thermal plasma", "PLASTIC", 60, parse("2007-03-01"), parse("2011-05-01"));
        dataset.addParam(v);
        mission.addDataset(dataset);

        dataset= new DesDataset("sta:mag:mag", "mag", "magnetic field", "MAG", 1, parse("2006-11-07"), parse("2011-06-29"));
        dataset.addParam(b);
        dataset.addParam(bx);
        dataset.addParam(by);
        dataset.addParam(bz);
        mission.addDataset(dataset);
        
        
        // STB
        mission = new DesMission("STB", "STEREO-B");
        missions.add(mission);
        dataset= new DesDataset("stb:l2:pla", "plasma", "thermal plasma", "PLASTIC", 60, parse("2007-04-01"), parse("2011-03-01"));
        dataset.addParam(v);
        mission.addDataset(dataset);
        
        dataset= new DesDataset("stb:mag:mag", "mag", "magnetic field", "MAG", 1, parse("2006-11-07"), parse("2011-06-29"));
        dataset.addParam(b);
        dataset.addParam(bx);
        dataset.addParam(by);
        dataset.addParam(bz);
        mission.addDataset(dataset);
        
        
        // functions
        // DERIV
        DesFunction function = new DesFunction("DERIV", "Parameter Derivative", "PARAM(t+dt) - PARAM(t) OP DELTAF; dt = AVERAGETIME * 2", 1);
        functions.add(function);
        function.addParam(v);
        function.addParam(n);
        function.addParam(b);
        function.addParam(bx);
        function.addParam(by);
        function.addParam(bz);
        function.addFunctionArgument(new DesFunctionArgument("DELTAF", LT, GT));
        DesFunctionArgument arg = function.addFunctionArgument(new DesFunctionArgument("AVERAGETIME", EQ));
        arg.setCompound(new DesFunctionArgumentCompound("DELTAT", "*2"));

        // SIGN
        function = new DesFunction("SIGN", "Parameter Sign Change", "PARAM(t+dt) / PARAM(t) lt 0; dt = AVERAGETIME * 3", 1); 
        functions.add(function);
        function.addParam(bx);
        function.addParam(by);
        function.addParam(bz);
        arg =function.addFunctionArgument(new DesFunctionArgument("AVERAGETIME", EQ));
        arg.setCompound(new DesFunctionArgumentCompound("DELTAT", "*3"));

        // VAR
        function = new DesFunction("VAR", "Parameter Variance in Sliding Window", "var_(param,width) - Wrapper to IDL variance() function. Finds Variance = 1/(N-1) * SUM[X - Xmean]2 in \"window\" time interval", 1);
        functions.add(function);
        function.addParam(v);
        function.addParam(n);
        function.addParam(bx);
        function.addParam(by);
        function.addParam(bz);
        
        function.addFunctionArgument(new DesFunctionArgument("DELTAF", LT, GT));
        function.addFunctionArgument(new DesFunctionArgument("TIMEWINDOW", EQ));
        function.addFunctionArgument(new DesFunctionArgument("AVERAGETIME", EQ));

        // VALUE
        function = new DesFunction("VALUE", "Parameter Value", "Value of parameters", 1);
        functions.add(function);
        function.addParam(v);
        function.addParam(n);
        function.addParam(b);
        function.addParam(bx);
        function.addParam(by);
        function.addParam(bz);
        
        function.addFunctionArgument(new DesFunctionArgument("DELTAF", LT, GT));
        function.addFunctionArgument(new DesFunctionArgument("AVERAGETIME", EQ));
    }

    private Date parse(String date) {
        try {
            return DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse date " + date + ": " + e.getMessage(), e);
        }
    }

    /**
     * @return the functions
     */
    public List<DesFunction> getFunctions() {
        return functions;
    }

    /**
     * @return the missions
     */
    public List<DesMission> getMissions() {
        return missions;
    }

    /**
     * @return the params
     */
    public List<DesParam> getParams() {
        return params;
    }
    
    public List<DesParam> getParamsByMissionAndFunction(String missionId, String functionId) {
        DesMission mission = getMissionById(missionId);
        DesFunction function = getFunctionById(functionId);
        AssertUtil.assertArgumentNotNull(mission, "missionId");
        AssertUtil.assertArgumentNotNull(function, "functionId");
        
        // create the union of all params in a data set.
        Set<DesParam> datasetParams = new HashSet<DesParam>();
        for (DesDataset dataset : mission.getDatasets()) {
            datasetParams.addAll(dataset.getParams());
        }
        
        // clone the function params (and preserve their sort order)
        List<DesParam> ret = new ArrayList<DesParam>(function.getParams());
        // create intersection of datasetParams and function params.
        ret.retainAll(datasetParams);
        return ret;
    }

    /**
     * Get a mission by id.
     * @param missionId the id of the mission. Must not be null.
     * @return the mission or null if not found
     */
    public DesMission getMissionById(String missionId) {
        for (DesMission mission : this.missions) {
            if (missionId.equals(mission.getId())) {
                return mission;
            }
        }
        return null;
    }
    
    /**
     * Get a function by id
     * @param functionId the function id. must not be null.
     * @return the function.
     */
    public DesFunction getFunctionById(String functionId) {
        for (DesFunction function : this.functions) {
            if (functionId.equals(function.getId())) {
                return function;
            }
        }
        return null;
    }
    
}
