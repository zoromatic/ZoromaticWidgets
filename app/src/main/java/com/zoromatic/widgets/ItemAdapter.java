package com.zoromatic.widgets;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemAdapter extends BaseAdapter {
    Context context;
    List<RowItem> rowItems;

    public ItemAdapter(Context context, List<RowItem> items) {
        this.context = context;
        this.rowItems = items;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        //TextView txtTitle;
        TextView txtDesc;
        ImageView imageArrow;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.batteryinforow, parent, false);
            holder = new ViewHolder();
            holder.txtDesc = (TextView) convertView.findViewById(R.id.batterylabel);
            //holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.batteryicon);
            holder.imageArrow = (ImageView) convertView.findViewById(R.id.batteryarrow);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RowItem rowItem = (RowItem) getItem(position);

        holder.txtDesc.setText(rowItem.getDesc());
        //holder.txtTitle.setText(rowItem.getTitle());
        holder.imageView.setImageResource(rowItem.getImageId());

        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, outValue, true);
        int color = outValue.resourceId;
        int colorAccent = context.getResources().getColor(color);

        holder.imageView.setColorFilter(colorAccent);

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        String theme = Preferences.getMainTheme(context);
        if (theme.compareToIgnoreCase("dark") == 0) {
            holder.imageArrow.setImageResource(R.drawable.arrow_white);

            //holder.imageView.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
            //holder.imageArrow.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_ATOP);
        } else {
            holder.imageArrow.setImageResource(R.drawable.arrow_black);

            //holder.imageView.setColorFilter(Color.BLACK, android.graphics.PorterDuff.Mode.SRC_ATOP);
            //holder.imageArrow.setColorFilter(Color.BLACK, android.graphics.PorterDuff.Mode.SRC_ATOP);
        }

        //holder.imageView.setBackgroundColor(Color.rgb(0x0D, 0x52, 0x92));
        //}
        holder.imageArrow.setVisibility(rowItem.isArrow() ? View.VISIBLE : View.INVISIBLE);
        holder.imageArrow.setColorFilter(colorAccent);

        return convertView;
    }

    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }
}