package eu.heliovo.clientapi.query.local;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.Tables;
import uk.ac.starlink.table.jdbc.SequentialResultSetStarTable;
import eu.heliovo.clientapi.query.WhereClause;
import eu.heliovo.clientapi.workerservice.JobExecutionException;

/**
 * Returns a StarTable from sql query.
 * @author junia schoch at fhnw ch
 *
 */
public class LocalHecQueryDaoImpl implements LocalHecQueryDao {

	private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    /**
     * Returns a StarTable from a sql query.
     * Params must not be null.
     * @param startTime DateTime String example: '2003-08-03 08:00:00'
     * @param endTime DateTime String example: '2003-08-03 08:00:00'
     * @param from name of a table in the database
     * @param startindex
	 * @param maxrecords
     * @return StarTable
     */
	@Override
	public StarTable query(String startTime, String endTime, String from, 
			int startindex, int maxrecords) {
		StringBuilder sqlStatement = new StringBuilder();
		sqlStatement.append("SELECT id, time_start, time_peak, time_end, nar, x_cart, y_cart, radial_arcsec, duration, count_sec_peak, total_count, energy_kev, flare_number");
		sqlStatement.append(" FROM " + from);
		sqlStatement.append(" WHERE time_start <= '" + startTime + "' AND time_end <= '" + endTime + "';" );
	
		StarTable starTable = query(sqlStatement.toString());
		
		return starTable;
	}
	
	/**
	 * Returns a StarTable from sql query.
	 * @param from table name in db, must not be null
	 * @param whereClause where statement, must not be null
	 * @param startindex
	 * @param maxrecords
	 */
	@Override
	public StarTable query(String from, String whereClause, int startindex, int maxrecord) {
		StringBuilder sqlStatement = new StringBuilder();
		sqlStatement.append("SELECT id, time_start, time_peak, time_end, nar, x_cart, y_cart, radial_arcsec, duration, count_sec_peak, total_count, energy_kev, flare_number");
		sqlStatement.append(" FROM " + from);
		sqlStatement.append(" " + whereClause + ";");
		
		StarTable starTable = query(sqlStatement.toString());
		return starTable;
	}
	
	private StarTable query(String sqlStatement) {
		StarTable starTable = this.jdbcTemplate.query(
				sqlStatement.toString(),
				new ResultSetExtractor<StarTable>() {

					@Override
					public StarTable extractData(ResultSet rs) throws SQLException,
							DataAccessException {
						SequentialResultSetStarTable starTable = new SequentialResultSetStarTable( rs );
						StarTable randomTable;
						try {
							randomTable = Tables.randomTable(starTable);
						} catch (IOException e) {
							throw new RuntimeException("could not make StarTable form result set", e);
						}
						return randomTable;
					}
					
				}		
			);
		return starTable;
	}
}