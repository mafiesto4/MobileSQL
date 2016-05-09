package figat.pl.mobilesql;

import android.content.Context;

public class Controller {

    private static Controller instance = new Controller();
    public static Controller getInstance() {
        return instance;
    }

    private Model model;
    private IViewObject view;

    private Controller() {
    }

    public void linkView(Context context, IViewObject viewObj)
    {
        model = new Model(context);
        view = viewObj;

        view.onTablesModified();
    }

    /***
     * Gets model object
     * @return Model object
     */
    public Model getModel()
    {
        return model;
    }

    /**
     * Creates new sql database table
     * @param name New table name
     */
    public void createTable(String name) {

        try {
            // Validate table name
            Table table = model.findTable(name);
            if (table != null) {
                // Error
                view.onTableAlreadyExists();
                return;
            }

            // Create table
            table = model.createTable(name);

            // Refresh tables list
            view.onTablesModified();

            // Navigate to that table
            view.navigate(table);
        }
        catch(Exception ex)
        {
            // Error
            view.onException(ex, "Cannot create new table.");
        }
    }

    /**
     * Deletes existing sql database table
     * @param name New table name
     */
    public void deleteTable(String name) {

        try {
            // Find table by name
            Table table = model.findTable(name);
            if (table == null) {
                // Error
                view.onMissingTable();
                return;
            }

            // Remove table from the model database
            model.deleteTable(table);

            // Refresh tables list
            view.onTablesModified();

            // Navigate to tables list
            view.showTablesList();
        }
        catch(Exception ex)
        {
            // Error
            view.onException(ex, "Cannot delete table.");
        }
    }
}
