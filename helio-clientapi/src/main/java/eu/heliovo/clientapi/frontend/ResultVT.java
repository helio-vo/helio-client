package eu.heliovo.clientapi.frontend;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import net.ivoa.xml.votable.v1.FIELD;
import net.ivoa.xml.votable.v1.INFO;
import net.ivoa.xml.votable.v1.RESOURCE;
import net.ivoa.xml.votable.v1.TABLE;
import net.ivoa.xml.votable.v1.TD;
import net.ivoa.xml.votable.v1.TR;
import net.ivoa.xml.votable.v1.VOTABLE;

public class ResultVT {

	/**
	 * Store a reference to the wrapped VOTable.
	 */
	private VOTABLE res;
	
	
	private List<String> headers;
	
	public int startTime;
	public int endTime;
	
	/**
	 * Shortcut to the list of tables.
	 */
	public List<TableVT> tables;
	
	/**
	 * Query info string.
	 */
	public String queryInfo;


	private final LogRecord[] logRecords;

	/**
	 * Method added temporarily to edit directly the rows to remove when a
	 * selected result download is activated.
	 */
	public VOTABLE getVOTABLE() {
		return this.res;
	}

	/**
	 * Get the total number of columns in all tables.
	 * @return the overall column number.
	 */
	public int getTotalSize() {
		int count = 0;
		for (TableVT table : tables) {
			count += table.getData().size();
		}

		return count;
	}

	
	/**
	 * Inner class to store one table of a set of votables, 
	 *
	 */
	public class TableVT {
		private String tableName;
		private List<String> headers;
		private List<List<String>> data;

		public TableVT() {
			tableName = "";
			headers = new ArrayList<String>();
			data = new ArrayList<List<String>>();

		}

		private void setName(String name) {
			this.tableName = name;
		}

		private void setHeaders(List<String> headersVT) {
			this.headers = headersVT;
		}

		private void setData(List<List<String>> dataVT) {
			this.data = dataVT;
		}

		public String getName() {
			return tableName;
		}

		public List<String> getHeaders() {
			return headers;
		}

		public List<List<String>> getData() {
			return data;
		}
	}
	
	/**
	 * Create a Result VT that wraps a given {@link VOTABLE}
	 * @param res the VOTable to wrap
	 */
	public ResultVT(VOTABLE res) {
		this(res, null);
	}

	/**
	 * Create an ResultVT that wraps a given {@link VOTABLE}.
	 * @param voTable the votable to wrao.
	 * @param logRecords the log records for user feedback
	 * 
	 */
	public ResultVT(VOTABLE voTable, LogRecord[] logRecords) {
		this.res = voTable;
		this.logRecords = logRecords;
		this.tables = new LinkedList<TableVT>();
		parse(res);
	}
	
	/**
	 * Get the list of tables stored within this VOTable.
	 * @return the list of tables
	 */
	public List<TableVT> getTables() {
		return tables;
	}
	
	/**
	 * Get the array of log records that are associated with the VOTable.
	 * @return the log records.
	 */
	public LogRecord[] getLogRecords() {
		return logRecords;
	}
	
	/**
	 * Transform the VOTable into a string.
	 * @return the string representation of this VOTable.
	 */
	public String getStringTable() {
		try {
			StringWriter retBuf = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(res.getClass());
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FRAGMENT, true);
			m.marshal(res, retBuf);
			String retVal = retBuf.toString();

			return retVal;
		} catch (JAXBException ex) {
			Logger.getLogger(ResultVT.class.getName()).log(Level.SEVERE, null, ex);
			return "";
		}
	}

	/**
	 * Parse the wrapped VOTable and initialize structures in this class.
	 * @param res the wrapped VOTable
	 */
	private void parse(VOTABLE res) {
		headers = new ArrayList<String>();

		// loop over all tables
		for (RESOURCE resource : res.getRESOURCE()) {
			// System.out.println("resource " +resource.getDESCRIPTION());
			queryInfo = "";
			for (Object o : resource.getINFOOrCOOSYSOrPARAM()) {
				INFO info = (INFO) o;
				queryInfo += info.getName() + " " + info.getValue();
			}

			for (TABLE table : resource.getTABLE()) {
				// System.out.println("TABLE " +table.getName());
				TableVT tableVT = new TableVT();
				tableVT.setName(table.getName());
				LinkedList<String> headersVT = new LinkedList<String>();
				for (Object o : table.getFIELDOrPARAMOrGROUP()) {
					FIELD f = (FIELD) o;
					headersVT.add(f.getName());

					if (!headers.contains(f.getName())) {
						headers.add(f.getName());
					}

				}
				tableVT.setHeaders(headersVT);
				ArrayList<List<String>> dataVT = new ArrayList<List<String>>();
				for (TR tr : table.getDATA().getTABLEDATA().getTR()) {
					ArrayList<String> rowVT = new ArrayList<String>();
					for (TD td : tr.getTD()) {
						rowVT.add(td.getValue());
					}
					dataVT.add(rowVT);
				}
				tableVT.setData(dataVT);
				tables.add(tableVT);
			}

		}

	}
}
