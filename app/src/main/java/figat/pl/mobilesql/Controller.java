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

        view.OnTablesModified();
    }

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
                view.OnTableAlreadyExists();
                return;
            }

            // Create table
            table = model.createTable(name);

            // Refresh tables list
            view.OnTablesModified();

            // Navigate to that table
            view.Navigate(table);
        }
        catch(Exception ex)
        {
            // Error
            view.OnException(ex, "Cannot create new table.");
        }
    }
}
