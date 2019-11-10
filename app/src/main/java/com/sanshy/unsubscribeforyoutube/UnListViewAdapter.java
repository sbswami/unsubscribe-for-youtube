package com.sanshy.unsubscribeforyoutube;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class UnListViewAdapter extends ArrayAdapter<String> {

    Activity mContext;
    ArrayList<com.sanshy.unsubscribeforyoutube.SingleUnsubscribeList> unList = new ArrayList<>();
    ArrayList<String> CName = new ArrayList<>();

    public UnListViewAdapter.MyAdapterListener onClickListener;
    public interface MyAdapterListener {
        void unsubscriberListener(View v, int position);
    }

    public UnListViewAdapter(Activity mContext, ArrayList<String> CName, ArrayList<com.sanshy.unsubscribeforyoutube.SingleUnsubscribeList> unList, MyAdapterListener onClickListener){
        super(mContext,R.layout.single_unsubscribe,CName);
        this.mContext = mContext;
        this.CName = CName;
        this.unList = unList;
        this.onClickListener = onClickListener;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = mContext.getLayoutInflater();
        View myListView = inflater.inflate(R.layout.single_unsubscribe,null,true);

try{
    com.sanshy.unsubscribeforyoutube.SingleUnsubscribeList singleList = unList.get(position);
    ImageView ChannelPic;
    TextView ChannelTitle,ChannelViewCount;
    Button unsubscribeButton;

    ChannelPic = myListView.findViewById(R.id.channel_image);
    ChannelTitle = myListView.findViewById(R.id.channel_name);
    ChannelViewCount = myListView.findViewById(R.id.view_count);
    unsubscribeButton = myListView.findViewById(R.id.unsubscribe_button);

    unsubscribeButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onClickListener.unsubscriberListener(v,position);
        }
    });
    ChannelTitle.setText(singleList.getChannelName());
    ChannelViewCount.setText("Videos: "+singleList.getVideosCount());

    try{
        Glide.with(mContext)
                .load(singleList.getPhotoURL())
                .into(ChannelPic);
    }catch (Exception ex){
        Toast.makeText(mContext, "Channel Photo Not Available.", Toast.LENGTH_SHORT).show();
    }

}catch (Exception ex){
    Log.d("UnListViewAdapter",ex.toString());
}


        return myListView;
    }
}
