package eu.heliovo.clientapi.processing;


/**
 * Factory to create a result object. This factory will be called after the result has been created.
 * @author MarcoSoldati
 *
 * @param <T> Exact type of the created ProcessingResultObject
 * @param <S> Type of the context object that is submitted to the method {@link #createResultObject(Object)}
 */
public interface ResultObjectFactory<T extends HelioProcessingServiceResultObject, S extends Object> {

    /**
     * Create the result object. This method will be called before the actual result is available.
     * @param context A context object with additional information which depends on the caller of this factory.
     * @return a new instance of the result object.
     */
    public T createResultObject(S context);

}