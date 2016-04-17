package figat.pl.mobilesql;

import java.util.ArrayList;
import java.util.Date;

public class Table {
    
    public String Name;

    // Temporary table data (may be released or not available always)
    public int EntriesCount;
    public String[] ColumnNames;
    public ArrayList<String[]> Data;
}
