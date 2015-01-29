package eu.heliovo.clientapi.query.local;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.Tables;
import uk.ac.starlink.table.jdbc.SequentialResultSetStarTable;

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
	 * Execute postgresql query.
	 * @param select: select statement that contains the field names
	 * @param from: table name
	 * @param where: where statement
	 * @param startindex: named 'OFFSET' in postgresql
	 * @param maxrecords: named 'LIMIT' in postgresql
	 */
    @Override
	public StarTable query(String select, String from, String where, int startindex, int maxrecord) {
		StringBuilder sqlStatement = new StringBuilder();
		sqlStatement.append("SELECT " + select);
		sqlStatement.append(" FROM " + from);
		sqlStatement.append(" WHERE " + where);
		
		if(maxrecord > 0) {
			sqlStatement.append(" LIMIT " + maxrecord); 
		}
		if(startindex >= 0) {
			sqlStatement.append(" OFFSET " + startindex);
		}
		
		sqlStatement.append(";");
	
		System.out.println(sqlStatement.toString());
		StarTable starTable = execute(sqlStatement.toString());
		
		return starTable;
	}
	
	private StarTable execute(String sqlStatement) {
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