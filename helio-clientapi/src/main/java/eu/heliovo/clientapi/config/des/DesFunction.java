package eu.heliovo.clientapi.config.des;

import java.util.ArrayList;
import java.util.List;

/**
 * A AMDA function that can be called.
 * @author MarcoSoldati
 *
 */
public class DesFunction {
    private final String id;
    private final String name;
    private final String description;
    private final int numOfParams;
    
    private final List<DesParam> params = new ArrayList<DesParam>();
    private final List<DesFunctionArgument> args = new ArrayList<DesFunctionArgument>();
    
    public DesFunction(String id, String name, String description, int numOfParams) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.numOfParams = numOfParams;
    }
    
    /**
     * Add a parameter
     * @param param the param to add
     * @return the aded param
     */
    public DesParam addParam(DesParam param) {
        this.params.add(param);
        return param;
    }
    
    /**
     * Add a des function argument
     * @param functionArgument the argument
     * @return the added argument
     */
    public DesFunctionArgument addFunctionArgument(DesFunctionArgument functionArgument) {
        this.args.add(functionArgument);
        return functionArgument;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the numOfParams
     */
    public int getNumOfParams() {
        return numOfParams;
    }

    /**
     * @return the params
     */
    public List<DesParam> getParams() {
        return params;
    }

    /**
     * @return the args
     */
    public List<DesFunctionArgument> getArgs() {
        return args;
    }
    
    
}
