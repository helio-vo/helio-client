package eu.heliovo.clientapi.query.paramquery.impl;

import java.util.List;

import org.springframework.core.convert.ConversionService;

import eu.heliovo.clientapi.model.field.HelioField;
import eu.heliovo.clientapi.query.HelioQueryResult;
import eu.heliovo.clientapi.query.paramquery.ParamQueryService;
import eu.heliovo.clientapi.query.paramquery.ParamQueryTerm;
import eu.heliovo.clientapi.workerservice.JobExecutionException;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceCapability;

/**
 * Wrapper class around a {@link ParamQueryService} that accepts {@link ParamQueryTerm} and converts them to the required types.
 * @author marco soldati at fhnw ch
 *
 */
public class ConvertingParamQueryServiceImpl implements ParamQueryService {
	
	/**
	 * Hold the conversion service for data type conversion.
	 */
	protected ConversionService conversionService;
	
	/**
	 * The wrapped service
	 */
	private final ParamQueryService paramQueryService;
	
	
	public ConvertingParamQueryServiceImpl(ParamQueryService paramQueryService) {
		this.paramQueryService = paramQueryService;
	}
	
    @Override
    public boolean supportsCapability(ServiceCapability capability) {
        return capability == ServiceCapability.UNDEFINED;
    }

	
	@Override
	public HelioServiceName getServiceName() {
		return paramQueryService.getServiceName();
	}

	@Override
	public HelioQueryResult query(List<ParamQueryTerm<?>> terms) throws IllegalArgumentException, JobExecutionException {
		return paramQueryService.query(terms);
	}

	@Override
	public HelioField<?>[] getFieldDescriptions(String catalog) throws IllegalArgumentException {
		return paramQueryService.getFieldDescriptions(catalog);
	}
	
	/**
	 * Get the conversion service
	 * @return the conversion service
	 */
	public ConversionService getConversionService() {
		return conversionService;
	}

	/**
	 * Set the conversion service.
	 * @param conversionService
	 */
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	public String getServiceVariant() {
	    return null;
	}
}
