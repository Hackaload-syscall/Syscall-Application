package com.example.youngseok.syscall;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class BrandSpinnerAdapter extends BaseAdapter {
    Context context;
    List<Brand> brandList;
    LayoutInflater inflater;

    public BrandSpinnerAdapter(Context applicationContext, List<Brand> brandList) {
        this.context = applicationContext;
        this.brandList = brandList;
        this.inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return brandList.size();
    }

    @Override
    public String getItem(int i) { return brandList.get(i).getName(); }

    @Override
    public long getItemId(int i) {
        return brandList.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.spinner_brand_items, null);
        ImageView icon = view.findViewById(R.id.imageView_brand);
        TextView names = view.findViewById(R.id.textView_brand);
        icon.setImageResource(brandList.get(i).getLogo());
        names.setText(brandList.get(i).getName());

        return view;
    }
}
