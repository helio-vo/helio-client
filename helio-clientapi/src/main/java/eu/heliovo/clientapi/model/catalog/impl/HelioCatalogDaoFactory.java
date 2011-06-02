package eu.heliovo.clientapi.model.catalog.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import eu.heliovo.clientapi.model.catalog.HelioCatalogDao;
import eu.heliovo.registryclient.HelioServiceName;
import eu.heliovo.registryclient.ServiceResolutionException;
import eu.heliovo.shared.util.AssertUtil;

public class HelioCatalogDaoFactory {
    /**
     * Number of concurrent threads to initialize the registries.
     */
    private static final int NUM_OF_THREADS = 5;
    
    /**
     * The map of catalog DAOs.
     */
    private final Map<String, HelioCatalogDao> helioCatalogDaoMap = new HashMap<String, HelioCatalogDao>();
    
    
    /**
     * The registry instance
     */
    private static HelioCatalogDaoFactory instance;

    /**
     * Get the singleton instance of the catalog dao factory
     * @return the catalog dao factory.
     */
    public static synchronized HelioCatalogDaoFactory getInstance() {
        if (instance == null) {
            instance = new HelioCatalogDaoFactory();
        }
        return instance;
    }
    
    /**
     * Hide the constructor
     */
    private HelioCatalogDaoFactory() {
        init();
    }

    /**
     * Populate the dao registry in a set of background threads.
     */
    @SuppressWarnings("unchecked")
    private void init() {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_OF_THREADS);
        
        Collection<? extends Callable<HelioCatalogDao>> daoCallables = new ArrayList<Callable<HelioCatalogDao>>();
        registerCallable((Collection<Callable<HelioCatalogDao>>) daoCallables, HecDao.class);
        registerCallable((Collection<Callable<HelioCatalogDao>>) daoCallables, DpasDao.class);
        
        List<Future<HelioCatalogDao>> futures;
        try {
            futures = executor.invokeAll(daoCallables);
        } catch (InterruptedException e) {
            throw new ServiceResolutionException("Got interrupted while initializing the HelioCatalogDao " + e.getMessage(), e);
        }
        
        for (Future<HelioCatalogDao> currentFuture : futures) {
            HelioCatalogDao helioCatalogDao;
            try {
                helioCatalogDao = currentFuture.get();
            } catch (InterruptedException e) {
                throw new ServiceResolutionException("Got interrupted while initializing the HelioCatalogDao " + e.getMessage(), e);
            } catch (ExecutionException e) {
                throw new ServiceResolutionException("Exception while initializing the HelioCatalogDao: " + e.getMessage(), e);
            }
            helioCatalogDaoMap.put(helioCatalogDao.getServiceName(), helioCatalogDao);
        }
    }

    /**
     * Register a callable.
     * @param daoCallables the callable.
     * @param daoClass the class of the registry.
     */
    private void registerCallable(final Collection<Callable<HelioCatalogDao>> daoCallables, final Class<? extends HelioCatalogDao> daoClass) {
        daoCallables.add(new Callable<HelioCatalogDao>() {
            @Override
            public HelioCatalogDao call() throws Exception {
                HelioCatalogDao dao = daoClass.newInstance();
                return dao;
            }
        });
    }
    
    /**
     * Get the catalog dao by service name. Possible service names are 
     * {@link HelioServiceName#HEC HelioServiceName#HEC.getName()}, {@link HelioServiceName#DPAS HelioServiceName.DPAS.getName()}, ....
     * @param serviceName the service name. must not be null
     * @return the catalog dao
     */
    public HelioCatalogDao getHelioCatalogDao(String serviceName) {
        AssertUtil.assertArgumentHasText(serviceName, "serviceName");
        return helioCatalogDaoMap.get(serviceName);
    }
    
}
