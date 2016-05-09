package figat.pl.mobilesql;

import java.util.ArrayList;

public class Table {
    
    public String Name;

    // Temporary table data (may be released or not available always)
    public int EntriesCount;
    public String[] ColumnNames;
    public ArrayList<String[]> Data;

    /**
     * Clears table cached data gathered from the database
     */
    public void clearCache()
    {
        EntriesCount = -1;
        ColumnNames = null;
        Data = null;
    }
}
