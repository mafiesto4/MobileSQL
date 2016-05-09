package figat.pl.mobilesql;

public interface IViewObject {

    void showTablesList();

    void navigate(Table table);

    void onTableAlreadyExists();

    void onMissingTable();

    void onTablesModified();

    void onException(Exception ex, String info);
}
