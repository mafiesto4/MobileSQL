package figat.pl.mobilesql;

import android.content.Context;

/**
 * Controller object (singleton)
 */
public class Controller {

    private static Controller instance = new Controller();

    /***
     * Gets singleton instance of the Controller object
     * @return Instance
     */
    public static Controller getInstance() {
        return instance;
    }

    private Model model;
    private IViewObject view;

    /**
     * Link View component to the Controller
     * @param context Application context
     * @param viewObj View object
     */
    public void linkView(Context context, IViewObject viewObj) {
        model = new Model(context);
        view = viewObj;
        view.onTablesModified();
    }

    /***
     * Gets model object
     * @return Model object
     */
    public Model getModel() {
        return model;
    }

    /**
     * Creates new SQL database table
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
        } catch (Exception ex) {
            // Error
            view.onException(ex, "Cannot create new table.");
        }
    }

    /**
     * Deletes existing SQL database table
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
        } catch (Exception ex) {
            // Error
            view.onException(ex, "Cannot delete table.");
        }
    }

    /***
     * Add new column to the table
     * @param tableName    Table name
     * @param columnName   Column name
     * @param columnType   Column type
     * @param defaultValue Default value
     */
    public void addColumn(String tableName, String columnName, String columnType, String defaultValue) {

        try {
            // Find table by name
            Table table = model.findTable(tableName);
            if (table == null) {
                // Error
                view.onMissingTable();
                return;
            }

            // Add column
            model.addColumn(table, columnName, columnType, defaultValue);

            // Refresh table
            view.navigate(table);
        } catch (Exception ex) {
            // Error
            view.onException(ex, "Cannot add column.");
        }
    }

    /***
     * Remove column from the table
     * @param tableName  Table name
     * @param columnName Column name
     */
    public void removeColumn(String tableName, String columnName) {

        try {
            // Find table by name
            Table table = model.findTable(tableName);
            if (table == null) {
                // Error
                view.onMissingTable();
                return;
            }

            // Remove column
            model.removeColumn(table, columnName);

            // Refresh table
            view.navigate(table);
        } catch (Exception ex) {
            // Error
            view.onException(ex, "Cannot remove column.");
        }
    }

    /**
     * Add row with default value to the table
     * @param tableName Table name
     */
    public void addRow(String tableName) {
        try {
            // Find table by name
            Table table = model.findTable(tableName);
            if (table == null) {
                // Error
                view.onMissingTable();
                return;
            }

            // Add row
            model.addRow(table);

            // Refresh table
            view.navigate(table);
        } catch (Exception ex) {
            // Error
            view.onException(ex, "Cannot add row.");
        }
    }

    /**
     * Remove row from the table
     * @param tableName Table name
     * @param index     Zero-based row index
     */
    public void deleteRow(String tableName, int index) {
        try {
            // Find table by name
            Table table = model.findTable(tableName);
            if (table == null) {
                // Error
                view.onMissingTable();
                return;
            }

            // Get table data
            model.getTableData(table);

            // Create row values string
            String row = "";
            for (int column = 0; column < table.cache.ColumnNames.length; column++) {
                if (column != 0)
                    row += " AND ";
                row += table.cache.ColumnNames[column] + " = \'" + table.cache.Data.get(index)[column] + "\'";
            }

            // Remove row
            model.rawQuery("DELETE FROM " + table.name + " WHERE " + row);

            // Refresh table
            view.navigate(table);

        } catch (Exception ex) {
            // Error
            view.onException(ex, "Cannot delete row.");
        }
    }

    /**
     * Edit single cell value
     * @param tableName   Name of the table to edit
     * @param rowIndex    Zero-based row index
     * @param columnIndex Zero-based column index
     * @param newValue    New value to assign
     */
    public void editCell(String tableName, int rowIndex, int columnIndex, String newValue) {
        try {
            // Find table by name
            Table table = model.findTable(tableName);
            if (table == null) {
                // Error
                view.onMissingTable();
                return;
            }

            // Get table data
            model.getTableData(table);

            // Create row values string
            String row = "";
            for (int column = 0; column < table.cache.ColumnNames.length; column++) {
                if (column != 0)
                    row += " AND ";
                row += table.cache.ColumnNames[column] + " = \'" + table.cache.Data.get(rowIndex)[column] + "\'";
            }

            // Remove row
            model.rawQuery("UPDATE " + table.name + " SET " + table.cache.ColumnNames[columnIndex] + " = \'" + newValue + "\' WHERE " + row);

            // Refresh table
            view.navigate(table);

        } catch (Exception ex) {
            // Error
            view.onException(ex, "Cannot edit cell.");
        }
    }
}
