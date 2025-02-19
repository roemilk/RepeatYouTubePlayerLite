package com.venuskimblessing.tuberepeatfree.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.venuskimblessing.tuberepeatfree.Json.SearchList;
import com.venuskimblessing.tuberepeatfree.R;
import com.venuskimblessing.tuberepeatfree.Utils.FormatUtils;

import java.util.ArrayList;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.ItemViewHolder> {
    public static final String TAG = "SearchRecyclerViewAdapter";

    private Context mContext = null;
    private View.OnClickListener onClickListener = null;
    private SearchList mSearchList = null;
    private String mNextPageToken = null;
    private ArrayList<SearchList.ItemsItem> mItemList = null;

    private OnClickRecyclerViewItemListener mListener = null;

    public interface OnClickRecyclerViewItemListener {
        public void onClickItem(SearchList.ItemsItem itemsItem);
    }

    public SearchRecyclerViewAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(SearchList searchList){
        this.mSearchList = searchList;
        this.mNextPageToken = mSearchList.getNextPageToken();
        setList();
    }

    public void setList(){
        mItemList = mSearchList.getItems();
    }

    public void setOnClickRecyclerViewItemListener(OnClickRecyclerViewItemListener listener){
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_search_cardview, null);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        final SearchList.ItemsItem item = mItemList.get(position);
        String title = item.getTitle();
        String thumbUrl = item.getThumbnails_url();
        String videoId = item.getVideoId();
        String duration = item.getDuration();
        String channelTitle = item.getChannelTitle();
        String viewCount = item.getViewCount();

        ImageView thumbNailImageView = holder.image;

        if(mListener != null){
            thumbNailImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClickItem(item);
                }
            });
        }

        Glide.with(mContext).load(thumbUrl)
                .thumbnail(0.1f)
                .into(thumbNailImageView);

        holder.title.setText(title);
        holder.title.setSelected(true);
        holder.time.setText(duration);
        holder.channelTitle.setText(channelTitle);
        holder.channelTitle.setSelected(true);
        holder.viewCount.setText(FormatUtils.parseNumberFormat(viewCount) + " view");
    }

    @Override
    public int getItemCount() {
        return this.mItemList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        private ImageView image;
        private TextView title, time, channelTitle, viewCount;

        public ItemViewHolder(View itemView) {
            super(itemView);
            image = (ImageView)itemView.findViewById(R.id.search_cardview_imageView);
            title = (TextView)itemView.findViewById(R.id.search_cardview_textView);
            time = (TextView)itemView.findViewById(R.id.search_cardview_time_textView);
            channelTitle = (TextView)itemView.findViewById(R.id.search_cardview_channelTitle_textView);
            viewCount = (TextView)itemView.findViewById(R.id.search_cardview_viewCount_textView);
        }
    }
}
