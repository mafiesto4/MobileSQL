package figat.pl.mobilesql;

import java.util.ArrayList;

public class Table {
    
    public String Name;
    public SqlQueryResult Cache;

    /**
     * Clears table cached data gathered from the database
     */
    public void clearCache() {
        Cache = null;
    }
}
