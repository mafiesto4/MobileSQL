package figat.pl.mobilesql;

import java.util.ArrayList;

/**
 * Created by Wojtek on 2016-05-09.
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
