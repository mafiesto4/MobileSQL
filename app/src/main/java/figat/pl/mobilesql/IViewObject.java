package figat.pl.mobilesql;

/**
 * Interface for View object
 */
public interface IViewObject {

    /**
     * Display list with tables for the user
     */
    void showTablesList();

    /**
     * Navigate to target table
     * @param table Target table
     */
    void navigate(Table table);

    /**
     * Show 'on table already exists' dialog message
     */
    void onTableAlreadyExists();

    /**
     * Show 'on missing table' dialog message
     */
    void onMissingTable();

    /**
     * Update view when table gets modified
     */
    void onTablesModified();

    /**
     * Show error dialog with exception and additional info
     * @param ex Exception
     * @param info Additional information
     */
    void onException(Exception ex, String info);
}
