package figat.pl.mobilesql;

import java.util.ArrayList;

/**
 * Contains result data of query to the database
 */
public class SqlQueryResult {

    public int EntriesCount;
    public String[] ColumnNames;
    public ArrayList<String[]> Data;

    public SqlQueryResult() {
        EntriesCount = 0;
        ColumnNames = null;
        Data = new ArrayList<>();
    }
}
