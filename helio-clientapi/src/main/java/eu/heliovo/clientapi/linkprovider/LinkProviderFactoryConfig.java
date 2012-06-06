package eu.heliovo.clientapi.linkprovider;

import eu.heliovo.clientapi.linkprovider.impl.CdawStereoLinkProvider;
import eu.heliovo.clientapi.linkprovider.impl.RadioMonitorLinkProvider;
import eu.heliovo.clientapi.linkprovider.impl.SolarMonitorLinkProvider;
import eu.heliovo.clientapi.linkprovider.impl.SpaceWeatherLinkProvider;
import eu.heliovo.clientapi.linkprovider.impl.TheSunTodayLinkProvider;
import eu.heliovo.clientapi.model.service.ServiceFactoryConfiguration;
import eu.heliovo.clientapi.model.service.ServiceVariantRegistry;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;

/**
 * Factory to get access to the link providers.
 * @author MarcoSoldati
 */
public class LinkProviderFactoryConfig implements ServiceFactoryConfiguration {

    /**
     * The default constructor.
     */
    public LinkProviderFactoryConfig() {
    }
    
    @Override
    public void registerVariants(ServiceVariantRegistry serviceVariantRegistry) {
        serviceVariantRegistry.register(HelioServiceName.LPS, SolarMonitorLinkProvider.SERVICE_VARIANT, ServiceCapability.LINK_PROVIDER_SERVICE, "solarMonitorLinkProvider");
        serviceVariantRegistry.register(HelioServiceName.LPS, SpaceWeatherLinkProvider.SERVICE_VARIANT, ServiceCapability.LINK_PROVIDER_SERVICE, "spaceWeatherLinkProvider");
        serviceVariantRegistry.register(HelioServiceName.LPS, TheSunTodayLinkProvider.SERVICE_VARIANT, ServiceCapability.LINK_PROVIDER_SERVICE, "theSunTodayLinkProvider");
        serviceVariantRegistry.register(HelioServiceName.LPS, CdawStereoLinkProvider.SERVICE_VARIANT, ServiceCapability.LINK_PROVIDER_SERVICE, "cdawStereoLinkProvider");
        serviceVariantRegistry.register(HelioServiceName.LPS, RadioMonitorLinkProvider.SERVICE_VARIANT, ServiceCapability.LINK_PROVIDER_SERVICE, "radioMonitorLinkProvider");
    }
}
