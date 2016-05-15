package figat.pl.mobilesql;

import java.util.ArrayList;

/**
 * Contains result data of query to the database
 */
public class SqlQueryResult {

    /**
     * Amount of entries in the result
     */
    public int EntriesCount;

    /**
     * Names of the columns
     */
    public String[] ColumnNames;

    /**
     * Result data (row by row)
     */
    public ArrayList<String[]> Data;

    /**
     * Init
     */
    public SqlQueryResult() {
        EntriesCount = 0;
        ColumnNames = null;
        Data = new ArrayList<>();
    }
}
