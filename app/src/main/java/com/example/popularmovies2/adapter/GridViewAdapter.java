package com.example.popularmovies2.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.popularmovies2.ImageAdapter;
import com.example.popularmovies2.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GridViewAdapter extends BaseAdapter {
    private Context mContext;
    LayoutInflater inflater;
    List<ImageAdapter> modelList;
    ArrayList<ImageAdapter> arrayList;

    public GridViewAdapter(Context c, List<ImageAdapter> modelData) {
        this.mContext = c;
        this.modelList = modelData;
        inflater = LayoutInflater.from( mContext );
        this.arrayList = new ArrayList<ImageAdapter>(  );
        this.arrayList.addAll( modelList );
    }

    public class ViewHolder
    {
        ImageView mPackImg;
    }

    @Override
    public int getCount() {
        return modelList==null?0:modelList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        if(view == null)
        {
            holder = new ViewHolder();
            view = inflater.inflate( R.layout.img_poster,  null);
            holder.mPackImg = view.findViewById(R.id.img_poster);
            view.setTag( holder );
        }
        else
        {
            holder = (ViewHolder)view.getTag();
        }
        try {
            Picasso.get().load(modelList.get(position).getImg()).into(holder.mPackImg);
        }
        catch (Exception e){
        }
        return view;

    }

}
