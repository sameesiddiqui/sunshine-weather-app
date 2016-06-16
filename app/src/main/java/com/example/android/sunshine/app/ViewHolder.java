package com.example.android.sunshine.app;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by glock on 6/16/16.
 */
public class ViewHolder {
    public final ImageView iconView;
    public final TextView descriptionView;
    public final TextView highView;
    public final TextView lowView;
    public final TextView dateView;

    public ViewHolder(View view) {
        iconView = (ImageView) view.findViewById(R.id.list_item_icon);
        descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
        dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
        highView= (TextView) view.findViewById(R.id.list_item_high_textview);
        lowView= (TextView) view.findViewById(R.id.list_item_low_textview);
    }
}
