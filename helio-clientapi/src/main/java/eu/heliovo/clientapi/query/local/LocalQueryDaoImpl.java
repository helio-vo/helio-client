package eu.heliovo.clientapi.query.local;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.DefaultValueInfo;
import uk.ac.starlink.table.DescribedValue;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.Tables;
import uk.ac.starlink.table.ValueInfo;
import uk.ac.starlink.table.jdbc.SequentialResultSetStarTable;
import eu.heliovo.clientapi.config.CatalogueDescriptorDao;
import eu.heliovo.clientapi.model.catalog.HelioCatalogueDescriptor;
import eu.heliovo.clientapi.model.field.descriptor.HelioFieldDescriptor;

/**
 * Returns a StarTable from sql query.
 * @author junia schoch at fhnw ch
 *
 */
public class LocalQueryDaoImpl implements LocalQueryDao {

	private JdbcTemplate jdbcTemplate;
	private CatalogueDescriptorDao catalogueDescriptorDao;
	private TableNameFilter tableNameFilter;

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
		String sqlStatement = buildSQLStatement(select, from, where,
				startindex, maxrecord);
	System.out.println(sqlStatement);
		StarTable starTable = execute(sqlStatement);
		starTable = addQueryStringInfo(sqlStatement, starTable);
		starTable = enhanceColumnDescriptors(from, starTable);
		
		return starTable;
	}

	private String buildSQLStatement(String select, String from,
			String where, int startindex, int maxrecord) {
		StringBuilder sqlStatement = new StringBuilder();
		sqlStatement.append("SELECT ").append(select);
		sqlStatement.append(" FROM ").append(tableNameFilter.getTableName(from)).append(" as ").append(from);
		
		if(!where.isEmpty()) {
			sqlStatement.append(" WHERE ").append(where);
		}
		
		if(maxrecord > 0) {
			sqlStatement.append(" LIMIT " + maxrecord); 
		}
		if(startindex >= 0) {
			sqlStatement.append(" OFFSET " + startindex);
		}
		
		sqlStatement.append(";");
		return sqlStatement.toString();
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
	
	private StarTable addQueryStringInfo(String sqlStatement,
			StarTable starTable) {
		ValueInfo vinfo = new DefaultValueInfo("QUERY_STRING");
		starTable.setParameter(new DescribedValue(vinfo, sqlStatement));
		return starTable;
	}

	private StarTable enhanceColumnDescriptors(String from, StarTable starTable) {
		int columnCount = starTable.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			ColumnInfo column = starTable.getColumnInfo(i);
			HelioFieldDescriptor<?> descriptor = findFieldDescriptor(from, column.getName());
			if (descriptor != null)  {
				column.setUCD(descriptor.getType().getUcd());
				column.setUnitString(descriptor.getType().getUnit());
				column.setDescription(descriptor.getDescription());
				column.setUtype(descriptor.getType().getUtype());
			}
		}
		return starTable;
	}
	
	private HelioFieldDescriptor<?> findFieldDescriptor(String catalogue, String fieldName) {
		HelioCatalogueDescriptor catalogueDescriptor = findEventListDescriptor(catalogue);
		if (catalogueDescriptor == null) {
			return null;
		}
		
		List<HelioFieldDescriptor<?>> fieldDescriptors = catalogueDescriptor.getFieldDescriptors();
		for (HelioFieldDescriptor<?> fieldDescriptor : fieldDescriptors) {
			if (fieldName.equals(fieldDescriptor.getName())) {				
				return fieldDescriptor;
			}
		}
		return null;
	}


	private HelioCatalogueDescriptor findEventListDescriptor(String catalogue) {
		List<? extends HelioCatalogueDescriptor> catalogueDescriptors = catalogueDescriptorDao.getDomainValues();
		for (HelioCatalogueDescriptor catalogueDescriptor : catalogueDescriptors) {
			if (catalogue.equals(catalogueDescriptor.getValue())) {
				return catalogueDescriptor;
			}
		}
		return null;
	}

	public CatalogueDescriptorDao getCatalogueDescriptorDao() {
		return catalogueDescriptorDao;
	}

	public void setCatalogueDescriptorDao(
			CatalogueDescriptorDao catalogueListDescriptorDao) {
		this.catalogueDescriptorDao = catalogueListDescriptorDao;
	}
	
	public TableNameFilter getTableNameFilter() {
		return tableNameFilter;
	}

	public void setTableNameFilter(TableNameFilter tableNameFilter) {
		this.tableNameFilter = tableNameFilter;
	}
}