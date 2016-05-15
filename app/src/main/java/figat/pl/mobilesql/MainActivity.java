package figat.pl.mobilesql;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IViewObject {

    private GoogleApiClient client;
    private final Context context = this;
    private String lastTableName;

    // Switching views
    private View viewTablesList;
    private View viewTableView;
    private View viewQueryView;
    private View currentView;
    private View previousView;
    private int mShortAnimationDuration;

    // Tables List
    private ListView tablesListView;
    private ArrayList<String> tablesList;
    private ArrayAdapter<String> tablesListAdapter;

    // Table View
    private TableLayout tableViewTable;

    // Query View
    private TableLayout queryViewTable;
    private EditText queryViewInput;

    /*
    TODO: remove column
    TODO: primary keys
    TODO: update entry
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        // View changing
        viewTablesList = findViewById(R.id.viewTablesList);
        viewTableView = findViewById(R.id.viewTableView);
        viewQueryView = findViewById(R.id.viewQueryView);
        viewTableView.setVisibility(View.GONE);
        viewQueryView.setVisibility(View.GONE);
        currentView = viewTablesList;
        previousView = null;
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        // Tables List
        tablesListView = (ListView) findViewById(R.id.viewTablesListTables);
        tablesList = new ArrayList<>();
        tablesListAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, tablesList);
        tablesListView.setAdapter(tablesListAdapter);
        tablesListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id)
            {
                String item = tablesListAdapter.getItem(position);
                navigate(item);
            }
        });
        (findViewById(R.id.viewTablesListAdd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateTableDialog();
            }
        });
        Toolbar tableListToolbar = (Toolbar)findViewById(R.id.viewTablesListToolbar);
        setSupportActionBar(tableListToolbar);
        tableListToolbar.setNavigationIcon(R.drawable.ic_action_database);
        tableListToolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context).setTitle("Info").setMessage("Brought to you by Wojciech Figat\nwww.figat.pl").show();
            }
        });
        getMenuInflater().inflate(R.menu.menu_main, tableListToolbar.getMenu());

        // Table View
        tableViewTable = (TableLayout)findViewById(R.id.viewTableViewTable);
        (findViewById(R.id.viewTableViewAdd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddEntry();
            }
        });
        Toolbar tableViewToolbar = (Toolbar)findViewById(R.id.viewTableViewToolbar);
        setSupportActionBar(tableViewToolbar);
        tableViewToolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        tableViewToolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showTablesList();
            }
        });
        tableViewToolbar.inflateMenu(R.menu.menu_table);
        getMenuInflater().inflate(R.menu.menu_table, tableViewToolbar.getMenu());

        // Query View
        queryViewTable = (TableLayout)findViewById(R.id.viewQueryViewTable);
        queryViewInput = (EditText) findViewById(R.id.viewQueryViewInput);
        (findViewById(R.id.viewQueryViewSearch)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performQuery();
            }
        });

        // Register view
        Controller.getInstance().linkView(context, this);
    }

    private void showCreateTableDialog() {

        // Setup a dialog window
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.new_table, null);
        alertDialogBuilder.setView(promptView);
        final EditText input = (EditText) promptView.findViewById(R.id.userInput);
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Controller.getInstance().createTable(input.getText().toString());
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertD = alertDialogBuilder.create();

        // Show it
        alertD.show();
    }

    private void showAddEntry()
    {
        Controller.getInstance().addRow(lastTableName);
    }

    private void performQuery() {
        try {

            // Try to get data
            String sql = queryViewInput.getText().toString();
            SqlQueryResult result = Controller.getInstance().getModel().performQuery(sql);

            // Show result
            updateTable(result, queryViewTable, false);

            // Update current table (it can be changed during this query)
            Table table = Controller.getInstance().getModel().findTable(lastTableName);
            if(table != null) {
                Controller.getInstance().getModel().getTableData(table);
                updateTable(table.cache, tableViewTable, true);
                table.clearCache();
            }
        }
        catch(Exception ex)
        {
            // Error
            onException(ex, "Cannot query SQL.");
        }
    }

    private void editCell(SqlTableText cell)
    {
        // Setup a dialog window
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.edit_row, null);
        alertDialogBuilder.setView(promptView);
        final EditText input = (EditText) promptView.findViewById(R.id.userInput);
        input.setText(cell.getText());
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                new AlertDialog.Builder(context).setTitle("Edit").setMessage("new value: " + input.getText()).show();
                //Controller.getInstance().createTable(input.getText().toString());
                //Controller.getInstance().editCell(lastTableName, cell.rowIndex, cell.columnIndex, input.getText());
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertD = alertDialogBuilder.create();

        // Show it
        alertD.show();
    }

    private void updateTable(SqlQueryResult sqlResult, TableLayout table, boolean allowEdit) {
        // Clear previous data
        table.removeAllViews();

        // Add header row
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(Color.parseColor("#c0c0c0"));
        headerRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        for (String t : sqlResult.ColumnNames) {
            TextView tv = new TextView(this);
            //tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            //tv.setBackgroundResource(R.drawable.cell_shape);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(16);
            tv.setText(t);
            tv.setPadding(3, 3, 3, 3);

            // TODO: finish removing columns
            /*if(allowEdit) {
                tv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View var1) {
                        final String columnName = ((TextView) var1).getText().toString();
                        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        Controller.getInstance().removeColumn(lastTableName, columnName);
                                        break;
                                }
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Delete column \'" + columnName + "\'?\nThis action cannot be undone.")
                                .setPositiveButton("OK", dialogClickListener)
                                .setNegativeButton("Cancel", dialogClickListener).show();
                        return true;
                    }
                });
            }*/
            headerRow.addView(tv);
        }
        table.addView(headerRow);

        // Fill table (row by row)
        for (int i = 0; i < sqlResult.Data.size(); i++) {

            TableRow row = new TableRow(this);
            //row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            String[] rowData = sqlResult.Data.get(i);

            // Add all columns
            for (int j = 0; j < rowData.length; j++) {

                SqlTableText tv = new SqlTableText(this);
                tv.rowIndex = i;
                tv.columnIndex = j;
                //tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                //tv.setGravity(Gravity.CENTER);
                //tv.setTextSize(18);
                tv.setPadding(3, 3, 3, 3);
                tv.setText(rowData[j]);

                if (allowEdit) {
                    tv.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View var1) {
                            final SqlTableText cell = (SqlTableText) var1;
                            final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            editCell(cell);
                                            break;
                                        case DialogInterface.BUTTON_NEUTRAL:
                                            Controller.getInstance().deleteRow(lastTableName, cell.rowIndex);
                                            break;
                                    }
                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Edit row")
                                    .setPositiveButton("Edit", dialogClickListener)
                                    .setNeutralButton("Delete", dialogClickListener)
                                    .setNegativeButton("Cancel", dialogClickListener).show();
                            return true;
                        }
                    });
                }

                row.addView(tv);
            }

            table.addView(row);
        }
    }

    private void deleteTable()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Controller.getInstance().deleteTable(lastTableName);
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure to delete table \'" + lastTableName + "\'?\nThis action cannot be undone.")
                .setPositiveButton("OK", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Toolbar tableViewToolbar = (Toolbar) findViewById(R.id.viewTableViewToolbar);

        if (tableViewToolbar.getMenu() == menu)
            getMenuInflater().inflate(R.menu.menu_table, menu);
        else
            getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    private void newColumn()
    {
        // Setup a dialog window
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.new_column, null);
        alertDialogBuilder.setView(promptView);
        final EditText input = (EditText) promptView.findViewById(R.id.newColumnName);

        class ColumnType
        {
            public String typeName;
            public String defaultValue;

            ColumnType(String t, String d)
            {
                typeName = t;
                defaultValue = d;
            }

            @Override
            public String toString()
            {
                return typeName;
            }
        };
        ColumnType[] columns = new ColumnType[]
                {
                        new ColumnType("INT", "o"),
                        new ColumnType("DATE", "\'2000-00-00\'"),
                        new ColumnType("VARCHAR", "\'?\'"),
                };

        final Spinner newColumnType = (Spinner)promptView.findViewById(R.id.newColumnType);
        ArrayAdapter<ColumnType> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, columns);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newColumnType.setAdapter(spinnerArrayAdapter);

        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                String columnName = input.getText().toString();
                ColumnType selectedType = (ColumnType)newColumnType.getSelectedItem();
                Controller.getInstance().addColumn(lastTableName, columnName, selectedType.typeName, selectedType.defaultValue);

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertD = alertDialogBuilder.create();

        // Show it
        alertD.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Table List
        if (id == R.id.action_exit) {
            finishAffinity();
            return true;
        }
        if(id == R.id.action_deleteTable) {
            deleteTable();
            return true;
        }
        if(id == R.id.action_newEntry) {
            showAddEntry();
            return true;
        }
        if(id== R.id.action_newColumn) {
            newColumn();
            return true;
        }
        if(id == R.id.action_querySql) {
            changeView(viewQueryView);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            // Check if is viewing a table
            if(currentView == viewTableView) {
                showTablesList();
                return true;
            }
            else if(currentView == viewQueryView)
            {
                changeView(viewTableView);
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    public void navigate(String table)
    {
        navigate(Controller.getInstance().getModel().findTable(table));
    }

    @Override
    public void showTablesList() {
        lastTableName = "";
        changeView(viewTablesList);
    }

    @Override
    public void navigate(Table table) {
        // Check if show default page
        if (table == null) {
            showTablesList();
            return;
        }

        // Navigate to table view
        lastTableName = table.name;
        changeView(viewTableView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.viewTableViewToolbar);
        toolbar.setTitle(table.name);

        try {

            // Get all table data
            Controller.getInstance().getModel().getTableData(table);

           // Update table
            updateTable(table.cache, tableViewTable, true);

            // Clear data
            table.clearCache();

        } catch (Exception ex) {
            onException(ex, "Cannot show table " + table.name);
        }
    }

    @Override
    public void onTableAlreadyExists()
    {
        new AlertDialog.Builder(context).setTitle("Error").setMessage("Table with that name already exists!").show();
    }

    @Override
    public void onMissingTable()
    {
        new AlertDialog.Builder(context).setTitle("Error").setMessage("Missing table!").show();
    }

    @Override
    public void onTablesModified() {
        tablesList.clear();
        Model model = Controller.getInstance().getModel();
        for (int i = 0; i < model.getTablesCount(); i++)
            tablesList.add(model.getTable(i).name);
        tablesListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onException(Exception ex, String info)
    {
        new AlertDialog.Builder(context).setTitle("Error").setMessage(info + "\n" + ex.getMessage()).show();
    }

    private void changeView(View targetView) {

        // Check if view won't change
        if(targetView == currentView)
            return;

        // Set new current view
        previousView = currentView;
        currentView = targetView;

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        currentView.setAlpha(0f);
        currentView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        currentView.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        previousView.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        previousView.setVisibility(View.GONE);
                    }
                });
    }
}
