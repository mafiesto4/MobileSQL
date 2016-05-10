package figat.pl.mobilesql;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Custom text view for the SQL table view
 */
public class SqlTableText extends TextView {

    public int rowIndex;
    public int columnIndex;

    public SqlTableText(Context context) {
        super(context);
    }
}
