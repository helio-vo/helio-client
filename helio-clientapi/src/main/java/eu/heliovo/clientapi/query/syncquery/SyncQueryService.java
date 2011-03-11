package eu.heliovo.clientapi.query.syncquery;

import java.util.List;

import eu.heliovo.clientapi.model.service.HelioService;

import net.ivoa.xml.votable.v1.VOTABLE;

/**
 * Client wrapper for a synchronous query service.
 * @author MarcoSoldati
 *
 */
public interface SyncQueryService extends HelioService {
    /**
     * Execute a synchronous query to a helio query service.
     * @param starttime the start time
     * @param endtime the end time 
     * @param from the table to query.
     * @param instrument the instruments to query for, deprecated, may be removed soon, as it duplicates 'from'.
     * @param where where clause of the query.
     * @param maxrecords max number of records to display
     * @param startindex position of first record.
     * @param join ???
     * @return
     *     returns net.ivoa.xml.votable.v1.VOTABLE
     */
    public VOTABLE query(
        List<String> starttime,
        List<String> endtime,
        List<String> from,
        String where,
        String instrument,
        Integer maxrecords,
        Integer startindex,
        String join);

    /**
     * Execute a synchronous time query to a helio query service.
     * @param starttime the start time
     * @param endtime the end time 
     * @param from the table to query.
     * @param maxrecords max number of records to display
     * @param startindex position of first record.
     * @return
     *     returns net.ivoa.xml.votable.v1.VOTABLE
     */
    public VOTABLE timeQuery(
        List<String> starttime,
        List<String> endtime,
        List<String> from,
        Integer maxrecords,
        Integer startindex);
}
