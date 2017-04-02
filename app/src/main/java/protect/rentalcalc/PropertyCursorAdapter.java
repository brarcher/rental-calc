package protect.rentalcalc;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.Locale;

class PropertyCursorAdapter extends CursorAdapter
{
    PropertyCursorAdapter(Context context, Cursor cursor)
    {
        super(context, cursor, 0);
    }

    private static class ViewHolder
    {
        TextView nicknameField;
        TextView shortAddressField;
        TextView priceField;
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.property_cursor_layout, parent, false);

        ViewHolder holder = new ViewHolder();
        holder.nicknameField = (TextView) view.findViewById(R.id.nickname);
        holder.shortAddressField = (TextView) view.findViewById(R.id.shortAddress);
        holder.priceField = (TextView) view.findViewById(R.id.price);
        view.setTag(holder);

        return view;
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        ViewHolder holder = (ViewHolder)view.getTag();

        // Extract properties from cursor
        Property property = Property.toProperty(cursor);

        // Populate fields with extracted properties
        holder.nicknameField.setText(property.nickname);
        if(property.addressCity.isEmpty() == false && property.addressState.isEmpty() == false)
        {
            holder.shortAddressField.setText(String.format("%s, %s", property.addressCity, property.addressState));
        }
        else if(property.addressState.isEmpty() == false)
        {
            holder.shortAddressField.setText(property.addressState);
        }
        else if(property.addressCity.isEmpty() == false)
        {
            holder.shortAddressField.setText(property.addressCity);
        }
        else
        {
            holder.shortAddressField.setText("");
        }

        holder.priceField.setText(String.format(Locale.US, "%dK", property.purchasePrice/1000));
    }
}
