package com.example.popularmovies2.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.popularmovies2.model.ModelTrailer;
import com.example.popularmovies2.R;

import java.util.ArrayList;
import java.util.List;

public class ListViewTrailerAdapter extends BaseAdapter{

    private Context mContext;
    LayoutInflater inflater;
    List<ModelTrailer> modelList;
    ArrayList<ModelTrailer> arrayList;

    public ListViewTrailerAdapter(Context ctx, List<ModelTrailer> modelData) {
        this.mContext = ctx;
        this.modelList = modelData;
        inflater = LayoutInflater.from( mContext );
        this.arrayList = new ArrayList<ModelTrailer>(  );
        this.arrayList.addAll( modelList );
    }

    public class ViewHolder
    {
        TextView tv;
        ImageView iv;
    }

    @Override
    public int getCount() {
        return modelList.size();
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
    public View getView(final int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        if(view == null)
        {
            holder = new ViewHolder();
            view = inflater.inflate( R.layout.model_trailer,  null);
            holder.tv = view.findViewById(R.id.sticker_tvName);
            holder.iv = view.findViewById(R.id.imageView);
            view.setTag( holder );

        }
        else
        {
            holder = (ViewHolder)view.getTag();
        }
        try {
            holder.tv.setText(modelList.get(position).getName());
        }
        catch (Exception e){

        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + modelList.get(position).getKey()));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + modelList.get(position).getKey()));
                webIntent.putExtra("finish_on_ended", true);
                appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    mContext.startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    mContext.startActivity(webIntent);
                }
            }
        });
        return view;
    }
}