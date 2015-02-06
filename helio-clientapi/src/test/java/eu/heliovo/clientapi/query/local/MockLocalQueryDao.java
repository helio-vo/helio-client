package eu.heliovo.clientapi.query.local;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import eu.heliovo.clientapi.HelioClientException;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.StarTableFactory;
import uk.ac.starlink.util.DataSource;

/**
 * Mock class for {@link LocalQueryDao}
 * @author junia schoch at fhnw ch
 *
 */
public class MockLocalQueryDao implements LocalQueryDao {
	private String select;
	private String from;
	private String where;
	
	@Override
	public StarTable query(String select, String from, String where, int startindex,
			int maxrecords) {
		this.select = select;
		this.from = from;
		this.where = where;
		
		return getStarTable();
	}
	
	public String getSelect() {
		return select;
	}

	public String getFrom() {
		return from;
	}

	public String getWhere() {
		return where;
	}
	
	/**
	 * Get Sample StarTable.
	 */
	private static StarTable getStarTable()  {
		DataSource datsrc = new DataSource() {	
			@Override
			protected InputStream getRawInputStream() throws IOException {
				InputStream is = new ByteArrayInputStream("a, b, c\n1, 2, 3\n4, 5, 6\n".getBytes());
				return is;
			}
		};
		StarTable starTable;
		try {
			starTable = new StarTableFactory().makeStarTable(datsrc, "CSV");
		} catch (IOException e) {
			throw new HelioClientException("could not create StarTable", e);
		}
		return starTable;
	}

}
