package figat.pl.mobilesql;

/**
 * Contains basic information about single table in program database
 */
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
