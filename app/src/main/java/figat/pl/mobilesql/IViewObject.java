package figat.pl.mobilesql;

public interface IViewObject {

    void Navigate(Table table);

    void OnTableAlreadyExists();

    void OnTablesModified();

    void OnException(Exception ex, String info);
}
