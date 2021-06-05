package com.example.mandatorysnapchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mandatorysnapchat.R;
import com.example.mandatorysnapchat.model.Snap;

import java.util.List;

public class MyAdapter extends BaseAdapter {

    private List<Snap> snaps;
    private LayoutInflater layoutInflater;

    public MyAdapter(List<Snap> snaps, Context context) {
        this.snaps = snaps;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return snaps.size();
    }

    @Override
    public Object getItem(int i) {
        return snaps.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = layoutInflater.inflate(R.layout.my_snap_row_list, null);
        }
        TextView textView = view.findViewById(R.id.textViewSnapList);
        if(textView != null) {
            textView.setText(snaps.get(i).getTitle());
        }
        return textView;
    }
}
