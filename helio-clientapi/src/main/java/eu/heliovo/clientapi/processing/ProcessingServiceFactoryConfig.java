package eu.heliovo.clientapi.processing;

import eu.heliovo.clientapi.config.service.ServiceFactoryConfiguration;
import eu.heliovo.clientapi.config.service.ServiceVariantRegistry;
import eu.heliovo.clientapi.processing.context.impl.FlarePlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.GoesPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.SimpleParkerModelServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.AcePlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.StaPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.StbPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.UlyssesPlotterServiceImpl;
import eu.heliovo.clientapi.processing.context.impl.des.WindPlotterServiceImpl;
import eu.heliovo.clientapi.processing.hps.impl.CirBackwardPropagationModelImpl;
import eu.heliovo.clientapi.processing.hps.impl.CirPropagationModelImpl;
import eu.heliovo.clientapi.processing.hps.impl.CmeBackwardPropagationModelImpl;
import eu.heliovo.clientapi.processing.hps.impl.CmePropagationModelImpl;
import eu.heliovo.clientapi.processing.hps.impl.SepBackwardPropagationModelImpl;
import eu.heliovo.clientapi.processing.hps.impl.SepPropagationModelImpl;
import eu.heliovo.clientapi.processing.taverna.impl.TavernaWorkflow2283;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;

/**
 * Factory to get accessor objects for all processing services.
 * @author marco soldati at fhnw ch
 *
 */
public class ProcessingServiceFactoryConfig implements ServiceFactoryConfiguration {

	/**
	 * default constructor
	 */
	public ProcessingServiceFactoryConfig() {
    }
	
	@Override
	public void registerVariants(ServiceVariantRegistry serviceVariantRegistry) {
	    // register context plots
        serviceVariantRegistry.register(HelioServiceName.CXS, GoesPlotterServiceImpl.SERVICE_VARIANT, ServiceCapability.COMMON_EXECUTION_ARCHITECTURE_SERVICE, "goesPlotterService");
        serviceVariantRegistry.register(HelioServiceName.CXS, FlarePlotterServiceImpl.SERVICE_VARIANT, ServiceCapability.COMMON_EXECUTION_ARCHITECTURE_SERVICE, "flarePlotterService");
        serviceVariantRegistry.register(HelioServiceName.CXS, SimpleParkerModelServiceImpl.SERVICE_VARIANT, ServiceCapability.COMMON_EXECUTION_ARCHITECTURE_SERVICE, "simpleParkerModelService");
        
        // register des plots
        serviceVariantRegistry.register(HelioServiceName.DES, AcePlotterServiceImpl.SERVICE_VARIANT, ServiceCapability.ASYNC_QUERY_SERVICE, "acePlotterService");
        serviceVariantRegistry.register(HelioServiceName.DES, StaPlotterServiceImpl.SERVICE_VARIANT, ServiceCapability.ASYNC_QUERY_SERVICE, "staPlotterService");
        serviceVariantRegistry.register(HelioServiceName.DES, StbPlotterServiceImpl.SERVICE_VARIANT, ServiceCapability.ASYNC_QUERY_SERVICE, "stbPlotterService");
        serviceVariantRegistry.register(HelioServiceName.DES, UlyssesPlotterServiceImpl.SERVICE_VARIANT, ServiceCapability.ASYNC_QUERY_SERVICE, "ulyssesPlotterService");
        serviceVariantRegistry.register(HelioServiceName.DES, WindPlotterServiceImpl.SERVICE_VARIANT, ServiceCapability.ASYNC_QUERY_SERVICE, "windPlotterService");
        
        // register HPS plots
        serviceVariantRegistry.register(HelioServiceName.HPS, CmePropagationModelImpl.SERVICE_VARIANT, ServiceCapability.HELIO_PROCESSING_SERVICE, "cmePropagationModel");
        serviceVariantRegistry.register(HelioServiceName.HPS, CmeBackwardPropagationModelImpl.SERVICE_VARIANT, ServiceCapability.HELIO_PROCESSING_SERVICE, "cmeBackwardPropagationModel");
        serviceVariantRegistry.register(HelioServiceName.HPS, CirPropagationModelImpl.SERVICE_VARIANT, ServiceCapability.HELIO_PROCESSING_SERVICE, "cirPropagationModel");
        serviceVariantRegistry.register(HelioServiceName.HPS, CirBackwardPropagationModelImpl.SERVICE_VARIANT, ServiceCapability.HELIO_PROCESSING_SERVICE, "cirBackwardPropagationModel");
        serviceVariantRegistry.register(HelioServiceName.HPS, SepPropagationModelImpl.SERVICE_VARIANT, ServiceCapability.HELIO_PROCESSING_SERVICE, "sepPropagationModel");
        serviceVariantRegistry.register(HelioServiceName.HPS, SepBackwardPropagationModelImpl.SERVICE_VARIANT, ServiceCapability.HELIO_PROCESSING_SERVICE, "sepBackwardPropagationModel");
        
        serviceVariantRegistry.register(HelioServiceName.TAVERNA_SERVER, TavernaWorkflow2283.SERVICE_VARIANT, ServiceCapability.TAVERNA_SERVER, "tavernaWorkflow2283");
	}	
}
