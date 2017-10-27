package com.example.youngseok.syscall;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ClassificationSpinnerAdapter extends BaseAdapter {
    Context context;
    List<Classification> classificationList;
    LayoutInflater inflater;

    public ClassificationSpinnerAdapter(Context applicationContext, List<Classification> classificationList) {
        this.context = applicationContext;
        this.classificationList = classificationList;
        this.inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return classificationList.size();
    }

    @Override
    public String getItem(int i) {
        return classificationList.get(i).getClassification();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.spinner_classification_items, null);
        TextView names = view.findViewById(R.id.textView_classification);
        names.setText(classificationList.get(i).getClassification());

        return view;
    }
}
