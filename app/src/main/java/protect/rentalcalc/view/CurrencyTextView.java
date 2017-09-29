package protect.rentalcalc.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import java.util.Currency;
import java.util.Locale;

/**
 * A TextView that auto-populates the text field with the Locale's currency symbol.
 */
public class CurrencyTextView extends AppCompatTextView
{
    public CurrencyTextView(Context context)
    {
        super(context);
        setCurrency();
    }

    public CurrencyTextView(Context context, AttributeSet set)
    {
        super(context, set);
        setCurrency();
    }

    public CurrencyTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        setCurrency();
    }

    private void setCurrency()
    {
        Currency currency = Currency.getInstance(Locale.getDefault());
        String currencySymbol = currency.getSymbol();
        setText(currencySymbol);
    }
}
