package confdb.converter.streams.ascii;

import java.util.Iterator;
import java.util.ArrayList;

import confdb.converter.table.ITable;
import confdb.converter.table.ITableRow;
import confdb.data.*;

/**
 * StreamsTable
 * ------------
 * @author Ulf Behrens
 * @author Philipp Schieferdecker
 *
 * streams data to be displayed in tables of various formats (ascii,
 * html, latex, etc.)
 */
public class StreamsTable implements ITable
{
    //
    // member data
    //
    
    /** the collection of summary table rows */
    private ArrayList<ITableRow> rows = new ArrayList<ITableRow>();
    
    /** column titles */
    private ArrayList<String> columnTitle = new ArrayList<String>();

    /** column widths */
    private ArrayList<Integer> columnWidth = new ArrayList<Integer>();
    

    //
    // construction
    //

    /** standard constructor */
    public StreamsTable( IConfiguration config )
    {
    	// this is a dummy!!!
    }

    
    //
    // member functions
    //
    
    /** number of columns */
    public int columnCount() { return columnTitle.size(); }
    
    /** get the i-th column title */
    public String columnTitle(int iColumn) { return columnTitle.get(iColumn); }

    /** get the width of i-th column */
    public int columnWidth(int iColumn) { return columnWidth.get(iColumn); }
    
    /** get the total table width */
    public int totalWidth()
    {
	int result = columnCount()*2+1;
	Iterator<Integer> itI = columnWidth.iterator();
	while (itI.hasNext()) result += itI.next();
	return result;
    }

    /** number of rows (triggers) in the table */
    public int rowCount() { return rows.size(); }

    /** get an iterator for rows in the table */
    public Iterator<ITableRow> rowIterator() { return rows.iterator(); }
    
}