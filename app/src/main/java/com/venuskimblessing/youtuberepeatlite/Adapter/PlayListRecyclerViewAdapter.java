package com.venuskimblessing.youtuberepeatlite.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.venuskimblessing.youtuberepeatlite.Interface.PlayListItemTouchHelperCallback;
import com.venuskimblessing.youtuberepeatlite.PlayList.PlayListData;
import com.venuskimblessing.youtuberepeatlite.PlayList.PlayListDataManager;
import com.venuskimblessing.youtuberepeatlite.R;
import com.venuskimblessing.youtuberepeatlite.Utils.MediaUtils;

import java.util.ArrayList;

public class PlayListRecyclerViewAdapter extends RecyclerView.Adapter<PlayListRecyclerViewAdapter.ItemViewHolder> implements PlayListItemTouchHelperCallback.OnItemMoveListener {
    private static final String TAG = "PlayListRecycler";

    public interface OnStartDragListener {
        void onStartDrag(ItemViewHolder itemViewHolder);
    }

    private Context mContext = null;
    private PlayListDataManager mPlayListDataManager;
    private OnStartDragListener mStartDragListener;
    private View.OnClickListener mOnClickPlayListItemListener;
    private ArrayList<PlayListData> mList = null;

    public PlayListRecyclerViewAdapter(Context context) {
        this.mContext = context;
        mPlayListDataManager = new PlayListDataManager(mContext);
        mPlayListDataManager.initTable();
    }

    public void setOnStartDragListener(OnStartDragListener listener) {
        this.mStartDragListener = listener;
    }

    public void setOnClickPlayListItemListener(View.OnClickListener listener) {
        this.mOnClickPlayListItemListener = listener;
    }

    public void setData(ArrayList<PlayListData> list) {
        this.mList = list;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_playlist, null);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {
        final PlayListData data = mList.get(position);
        String thumbUrl = data.getImg_url();
        int duration = Integer.parseInt(data.getDuration());
        String title = data.getTitle();

        Glide.with(mContext).load(thumbUrl)
                .thumbnail(0.1f)
                .into(holder.thumbImageView);

        holder.durationTextView.setText(MediaUtils.getMillSecToHMS(duration));
        holder.titleTextView.setText(title);

        holder.moveButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                    mStartDragListener.onStartDrag(holder);
                }
                return false;
            }
        });

        holder.parentLay.setTag(data);
        holder.parentLay.setOnClickListener(mOnClickPlayListItemListener);
        holder.moreLay.setOnClickListener(mOnClickPlayListItemListener);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Log.d(TAG, "onItemMove...");
        PlayListData fromData = mList.get(fromPosition);
        PlayListData toData = mList.get(toPosition);
        Log.d(TAG, "fromPosition : " + fromPosition + "  toPosition : " + toPosition);

        int fromId = fromData.getId();
        int toId = toData.getId();
        Log.d(TAG, "fromId : " + fromId + "  toId : " + toId);

        mPlayListDataManager.updateWhere(toId, fromData.getImg_url(), fromData.getTitle(), fromData.getDuration(), fromData.getVideoId(), fromData.getStartTime(), fromData.getEndTime());
        mPlayListDataManager.updateWhere(fromId, toData.getImg_url(), toData.getTitle(), toData.getDuration(), toData.getVideoId(), toData.getStartTime(), toData.getEndTime());

        mList.clear();
        mList.addAll(mPlayListDataManager.loadPlayList());
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemSwipe(int position) {
        PlayListData data = mList.get(position);
        int id = data.getId();
        mPlayListDataManager.deleteWhere(id);
        mList.remove(position);
        notifyItemRemoved(position);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout parentLay, moreLay;
        private ImageView thumbImageView;
        private TextView durationTextView, titleTextView;
        private Button moveButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            parentLay = (RelativeLayout) itemView.findViewById(R.id.view_playlist_parent_lay);
            thumbImageView = (ImageView) itemView.findViewById(R.id.view_playlist_thumb_imageView);

            titleTextView = (TextView) itemView.findViewById(R.id.view_playlist_title_textView);
            durationTextView = (TextView) itemView.findViewById(R.id.view_playlist_duration_textView);

            moveButton = (Button) itemView.findViewById(R.id.view_playlist_move_button);
            moreLay = (RelativeLayout) itemView.findViewById(R.id.view_playlist_more_lay);
        }
    }
}
