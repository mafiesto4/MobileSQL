package figat.pl.mobilesql;

/**
 * Contains basic information about single table in program database
 */
public class Table {

    /**
     * Table name
     */
    public String name;

    /**
     * Cached table data
     */
    public SqlQueryResult cache;

    /**
     * Clears table cached data gathered from the database
     */
    public void clearCache() {
        cache = null;
    }
}
