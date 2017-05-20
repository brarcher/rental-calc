package protect.rentalcalc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

class ItemizationAdapter extends ArrayAdapter<Itemization>
{
    private static final String TAG = "RentalCalc";

    private OnValueChangedListener _listener;

    ItemizationAdapter(Context context, List<Itemization> items)
    {
        super(context, 0, items);
    }

    private static class ViewHolder
    {
        EditText field;
        TextWatcher fieldWatcher;
        TextView currency;
        EditText value;
        TextWatcher valueWatcher;
        ImageView deleteIcon;
    }

    interface OnValueChangedListener
    {
        void onValueChanged();
    }

    void setOnValueChangedListener(OnValueChangedListener listener)
    {
        _listener = listener;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent)
    {
        // Get the data item for this position
        final Itemization item = getItem(position);

        final ViewHolder holder;

        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.itemization_item_layout,
                    parent, false);

            holder = new ViewHolder();
            holder.field = (EditText) convertView.findViewById(R.id.field);
            holder.currency = (TextView) convertView.findViewById(R.id.currency);
            holder.value = (EditText) convertView.findViewById(R.id.value);
            holder.deleteIcon = (ImageView) convertView.findViewById(R.id.deleteIcon);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }

        // Remove the old watcher, if it exists, before continuing
        holder.field.removeTextChangedListener(holder.fieldWatcher);
        holder.fieldWatcher = new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                // noop
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(item != null)
                {
                    item.name = holder.field.getText().toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                // noop
            }
        };

        holder.field.setText(item != null ? item.name : "");
        holder.field.addTextChangedListener(holder.fieldWatcher);

        holder.currency.setText("$");

        // Remove the value watcher, if it exists, before continuing
        holder.value.removeTextChangedListener(holder.valueWatcher);
        holder.valueWatcher = new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                // noop
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String string = holder.value.getText().toString();
                Integer value = 0;

                if(item != null)
                {
                    if(string.isEmpty() == false)
                    {
                        try
                        {
                            value = Integer.parseInt(string);
                        }
                        catch(NumberFormatException e)
                        {
                            Log.w(TAG, "Failed to parse " + string, e);
                        }
                    }

                    item.value = value;
                    _listener.onValueChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                // noop
            }
        };

        if(item == null || item.value == 0)
        {
            holder.value.setText("");
        }
        else
        {
            holder.value.setText(String.format(Locale.US, "%d", item.value));
        }

        holder.value.addTextChangedListener(holder.valueWatcher);

        holder.deleteIcon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ItemizationAdapter.this.remove(ItemizationAdapter.this.getItem(position));
            }
        });

        return convertView;
    }
}