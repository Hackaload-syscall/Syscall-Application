package com.example.youngseok.syscall;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ColorSpinnerAdapter extends BaseAdapter {
    Context context;
    List<Color> colorList;
    LayoutInflater inflater;

    public ColorSpinnerAdapter(Context applicationContext, List<Color> colorList) {
        this.context = applicationContext;
        this.colorList = colorList;
        this.inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return colorList.size();
    }

    @Override
    public String getItem(int i) { return colorList.get(i).getName(); }

    @Override
    public long getItemId(int i) {
        return colorList.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.spinner_color_items, null);
        ImageView icon = view.findViewById(R.id.imageView_color);
        TextView names = view.findViewById(R.id.textView_color);
        icon.setImageResource(colorList.get(i).getColor());
        names.setText(colorList.get(i).getName());

        return view;
    }
}
