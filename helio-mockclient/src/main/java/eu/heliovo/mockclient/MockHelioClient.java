package eu.heliovo.mockclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;

import eu.heliovo.clientapi.HelioClient;
import eu.heliovo.clientapi.model.service.HelioService;

public class MockHelioClient implements HelioClient {
	/**
	 * Reference to the logger.
	 */
	private static final Logger _LOGGER = Logger.getLogger(MockHelioClient.class);
	
	/**
	 * The Map of registered query services.
	 */
	private final Map<String, HelioService> SERVICES = new HashMap<String, HelioService>();

	public MockHelioClient() {
		initialize();
	}
	
	/**
	 * Initialize the mock param query impl with some static data.
	 */
	private void initialize() {
		GenericApplicationContext ctx = new GenericApplicationContext();
		XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);
		xmlReader.loadBeanDefinitions(new ClassPathResource("helio-mockclient.xml"));
//		PropertiesBeanDefinitionReader propReader = new PropertiesBeanDefinitionReader(ctx);
//		propReader.loadBeanDefinitions(new ClassPathResource("otherBeans.properties"));
		ctx.refresh();
		
		// get all beans that implement HelioService
		String[] servicBeanIds = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(ctx, HelioService.class);
		
		for (String serviceBeanId : servicBeanIds) {			
			HelioService service = ctx.getBean(serviceBeanId, HelioService.class);
			
			if (service != null) {
				SERVICES.put(service.getName(), service);
			} else {
				_LOGGER.warn("Unable to load service with id " + serviceBeanId);
			}
			
		}
	}

	@Override
	public HelioService[] getServices() {
		return SERVICES.values().toArray(new HelioService[SERVICES.values().size()]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] getServices(Class<T> clazz) throws IllegalArgumentException {
		if (clazz == null) {
			throw new IllegalArgumentException("Argument 'clazz' must not be null.");
		}		
		List<T> services = new ArrayList<T>();
		for (HelioService helioService : SERVICES.values()) {
			if (clazz.isAssignableFrom(helioService.getClass())) {
				services.add((T) helioService);
			}
		}
		return (T[]) services.toArray(new Object[services.size()]);
	}

	@Override
	public HelioService getService(String serviceName) throws IllegalArgumentException {	
		if (serviceName == null) {
			throw new IllegalArgumentException("Argument 'serviceName' must not be null.");
		}		

		return SERVICES.get(serviceName);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getService(String serviceName, Class<T> clazz) throws IllegalArgumentException {
		if (clazz == null) {
			throw new IllegalArgumentException("Argument 'clazz' must not be null.");
		}		

		HelioService service = getService(serviceName);
		if (service == null) {
			return null;
		} else if (clazz.isAssignableFrom(service.getClass())) {
			return (T)service;
		} 
		return null;
	}
}