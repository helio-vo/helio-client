package eu.heliovo.clientapi.query.delegate;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import eu.heliovo.clientapi.query.QueryDelegate;
import eu.heliovo.clientapi.query.QueryMethodType;
import eu.heliovo.clientapi.query.QueryType;

/**
 * Factory to return the query delegate depending on the current query type.
 * 
 * @author MarcoSoldati
 * 
 */
public class QueryDelegateFactoryImpl implements ApplicationContextAware, QueryDelegateFactory {
    
    private ApplicationContext applicactionContext;

    @Override
    public QueryDelegate getDelegate(QueryType queryType, QueryMethodType queryMethodType) {
        String delegateName = null;
        switch (queryType) {
        case ASYNC_QUERY:
            switch (queryMethodType) {
            case FULL_QUERY:
                delegateName = "asyncQueryDelegate";
                break;
            case TIME_QUERY:
                delegateName = "asyncTimeQueryDelegate";
                break;
            }
            break;
        case SYNC_QUERY:
            switch (queryMethodType) {
            case FULL_QUERY:
                delegateName = "syncQueryDelegate";
                break;
            case TIME_QUERY:
                delegateName = "syncTimeQueryDelegate";
                break;
            }
            break;
        }
        QueryDelegate delegate = (QueryDelegate) applicactionContext.getBean(delegateName);
        return delegate;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicactionContext = applicationContext;
    }
}
