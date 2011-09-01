package eu.heliovo.clientapi.query.asyncquery.impl;

import eu.heliovo.clientapi.loadbalancing.LoadBalancer;
import eu.heliovo.clientapi.loadbalancing.impl.LoadBalancerFactory;
import eu.heliovo.clientapi.model.service.HelioService;
import eu.heliovo.registryclient.AccessInterface;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.shared.util.AssertUtil;

public class AbstractQueryServiceImpl implements HelioService {

    /**
     * The load balancer component to use.
     */
    protected LoadBalancer loadBalancer = LoadBalancerFactory.getInstance().getLoadBalancer();
    /**
     * The location of the target WSDL file
     */
    protected final AccessInterface[] accessInterfaces;
    /**
     * Name of the service
     */
    protected final HelioServiceName name;
    /**
     * Description of this service.
     */
    protected final String description;

    public AbstractQueryServiceImpl(HelioServiceName name, String description, AccessInterface[] accessInterfaces) {
        AssertUtil.assertArgumentNotNull(name, "name");
        AssertUtil.assertArgumentNotNull(accessInterfaces, "accessInterfaces");
        this.name = name;
        this.accessInterfaces = accessInterfaces;
        this.description = description;
    }

    @Override
    public HelioServiceName getName() {
    	return name;
    }

    @Override
    public String getDescription() {
    	return description;
    }

}