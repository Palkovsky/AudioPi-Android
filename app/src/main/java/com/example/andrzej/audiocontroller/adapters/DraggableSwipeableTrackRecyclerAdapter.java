package com.example.andrzej.audiocontroller.adapters;


import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.models.Track;
import com.example.andrzej.audiocontroller.models.dbmodels.PlaylistDb;
import com.example.andrzej.audiocontroller.models.dbmodels.TrackDb;
import com.example.andrzej.audiocontroller.utils.Communicator;
import com.example.andrzej.audiocontroller.utils.Converter;
import com.example.andrzej.audiocontroller.utils.DatabaseUtils;
import com.example.andrzej.audiocontroller.utils.DrawableUtils;
import com.example.andrzej.audiocontroller.utils.Image;
import com.example.andrzej.audiocontroller.utils.ViewUtils;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder;
import com.squareup.picasso.Picasso;


import java.util.List;

public class DraggableSwipeableTrackRecyclerAdapter extends RecyclerView.Adapter<DraggableSwipeableTrackRecyclerAdapter.MyViewHolder>
        implements DraggableItemAdapter<DraggableSwipeableTrackRecyclerAdapter.MyViewHolder>,
        SwipeableItemAdapter<DraggableSwipeableTrackRecyclerAdapter.MyViewHolder> {

    // NOTE: Make accessible with short name
    private interface Draggable extends DraggableItemConstants {
    }

    private interface Swipeable extends SwipeableItemConstants {
    }

    private List<Track> mDataset;
    private EventListener mEventListener;
    private Context context;
    private Track lastRemovedItem;
    private int lastRemovedPosition = -1;

    public interface EventListener {
        void onItemRemoved(int position, Track track);

        void onItemMoved(int fromPosition, int toPosition);

        void onItemViewClicked(View v, int position);

        boolean onLongItemViewClicked(View v, int position);
    }

    public DraggableSwipeableTrackRecyclerAdapter(Context context, List<Track> mDataset) {
        this.context = context;
        this.mDataset = mDataset;
        /*
        mItemViewOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemViewClick(v);
            }
        };
        */

        // DraggableItemAdapter and SwipeableItemAdapter require stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);
    }

    private void onItemViewClick(View v, int position) {
        if (mEventListener != null)
            mEventListener.onItemViewClicked(v, position);
    }

    private boolean onLongItemViewClick(View v, int position) {
        if (mEventListener != null)
            return mEventListener.onLongItemViewClicked(v, position);
        return false;
    }

    public void undoLastRemoval() {
        if (lastRemovedPosition >= 0 && lastRemovedItem != null) {
            DatabaseUtils.handleInsertedPositions(lastRemovedItem.getPlaylist().getDbId(), lastRemovedPosition);
            TrackDb trackDb = Converter.standardToDb(lastRemovedItem);
            trackDb.position = lastRemovedPosition;
            trackDb.playlist = PlaylistDb.load(PlaylistDb.class, lastRemovedItem.getPlaylist().getDbId());
            trackDb.save();
            Track track = Converter.dbToStandard(trackDb);
            track.setPlaylist(lastRemovedItem.getPlaylist());
            mDataset.add(lastRemovedPosition, track);
            Communicator.getInstance().sendMessage(Communicator.LOCAL_PLAYLIST_ITEM_APPEND, lastRemovedPosition, track);
            notifyDataSetChanged();
            lastRemovedItem = null;
            lastRemovedPosition = -1;
        }
    }

    @Override
    public long getItemId(int position) {
        return mDataset.get(position).getDbId();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public DraggableSwipeableTrackRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.track_item_draggable, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DraggableSwipeableTrackRecyclerAdapter.MyViewHolder holder, final int position) {
        Track item = mDataset.get(position);

        // set listeners
        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemViewClick(v, position);
            }
        });

        holder.mContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return onLongItemViewClick(v, position);
            }
        });


        // set text
        holder.mTrackTitle.setText(item.getFormattedName());
        holder.mTrackPosition.setText(String.valueOf(TrackDb.load(TrackDb.class, item.getDbId()).position + 1));
        holder.coverIv.setImageBitmap(null);
        holder.coverIv.setImageDrawable(null);
        Image.clearDrawable(holder.coverIv);

        String coverUrl = item.getMetadata().getCoverUrl();
        if (coverUrl != null && !coverUrl.equals(""))
            Picasso.with(context).load(coverUrl).placeholder(R.drawable.ic_music_note_black_36dp).
                    error(R.drawable.ic_music_note_black_36dp).into(holder.coverIv);
        else
            Image.setBackgroundDrawable(context, holder.coverIv, R.drawable.ic_music_note_black_36dp);

        String artist, album;
        if (item.getMetadata().getArtist() == null)
            artist = context.getString(R.string.unknown);
        else
            artist = item.getMetadata().getArtist();
        if (item.getMetadata().getAlbum() == null)
            album = context.getString(R.string.unknown);
        else
            album = item.getMetadata().getAlbum();

        String formattedArtistAlbum =
                String.format(context.getString(R.string.artist_album_format), artist, album);


        if ((artist.equals(context.getString(R.string.unknown)) && album.equals(context.getString(R.string.unknown))) ||
                (artist.equals("null") && album.equals("null")))
            holder.albumArtistTv.setVisibility(View.GONE);
        else {
            holder.albumArtistTv.setVisibility(View.VISIBLE);
            holder.albumArtistTv.setText(formattedArtistAlbum);
        }

        // set background resource (target view ID: container)
        final int dragState = holder.getDragStateFlags();
        final int swipeState = holder.getSwipeStateFlags();

        if (((dragState & Draggable.STATE_FLAG_IS_UPDATED) != 0) ||
                ((swipeState & Swipeable.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((dragState & Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                DrawableUtils.clearState(holder.mContainer.getForeground());
            } else if ((dragState & Draggable.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else if ((swipeState & Swipeable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_swiping_active_state;
            } else if ((swipeState & Swipeable.STATE_FLAG_SWIPING) != 0) {
                bgResId = R.drawable.bg_item_swiping_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
        }

        // set swiping properties
        holder.setSwipeItemHorizontalSlideAmount(
                0);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public SwipeResultAction onSwipeItem(MyViewHolder holder, int position, int result) {

        switch (result) {
            // swipe right
            case Swipeable.RESULT_SWIPED_RIGHT:
                return new SwipeRemoveResultAction(this, position);
            case Swipeable.RESULT_SWIPED_LEFT:
                return new SwipeRemoveResultAction(this, position);
            case Swipeable.RESULT_CANCELED:
            default:
                return null;

        }
    }

    @Override
    public int onGetSwipeReactionType(MyViewHolder holder, int position, int x, int y) {
        if (onCheckCanStartDrag(holder, position, x, y)) {
            return Swipeable.REACTION_CAN_NOT_SWIPE_BOTH_H;
        } else {
            return Swipeable.REACTION_CAN_SWIPE_BOTH_H;
        }
    }

    @Override
    public void onSetSwipeBackground(MyViewHolder holder, int position, int type) {
        int bgRes = 0;
        switch (type) {
            case Swipeable.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_neutral;
                break;
            case Swipeable.DRAWABLE_SWIPE_LEFT_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_left;
                break;
            case Swipeable.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_right;
                break;
        }

        holder.itemView.setBackgroundResource(bgRes);
    }

    @Override
    public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
        // x, y --- relative from the itemView's top-left
        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
        return null; //STays this way
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        TrackDb firstTrack = TrackDb.load(TrackDb.class, mDataset.get(fromPosition).getDbId());
        firstTrack.position = toPosition;
        firstTrack.position = toPosition;
        firstTrack.save();

        final Track item = mDataset.remove(fromPosition);
        mDataset.add(toPosition, item);

        if (fromPosition > toPosition) {
            //Up
            for (int i = toPosition + 1; i <= fromPosition; i++) {
                TrackDb trackDb = TrackDb.load(TrackDb.class, mDataset.get(i).getDbId());
                trackDb.position++;
                trackDb.save();
            }

        } else {
            //Down
            for (int i = toPosition - 1; i >= fromPosition; i--) {
                TrackDb trackDb = TrackDb.load(TrackDb.class, mDataset.get(i).getDbId());
                trackDb.position--;
                trackDb.save();
            }
        }


        notifyItemMoved(fromPosition, toPosition);

        mEventListener.onItemMoved(fromPosition, toPosition);
        Integer[] positions = {fromPosition, toPosition};
        Communicator.getInstance().sendMessage(Communicator.LOCAL_PLAYLIST_POSITION_CHANGED, positions);
    }


    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }


    public static class MyViewHolder extends AbstractDraggableSwipeableItemViewHolder {
        public FrameLayout mContainer;
        public View mDragHandle;
        public ImageView coverIv;
        public TextView mTrackPosition;
        public TextView mTrackTitle;
        public TextView albumArtistTv;

        public MyViewHolder(View v) {
            super(v);
            mContainer = (FrameLayout) v.findViewById(R.id.container);
            mDragHandle = v.findViewById(R.id.drag_handle);
            coverIv = (ImageView) v.findViewById(R.id.coverIv);
            mTrackPosition = (TextView) v.findViewById(R.id.trackPositionTv);
            mTrackTitle = (TextView) v.findViewById(R.id.trackTitleTv);
            albumArtistTv = (TextView) v.findViewById(R.id.trackAlbumArtistTv);
        }

        @Override
        public View getSwipeableContainerView() {
            return mContainer;
        }
    }


    private static class SwipeRemoveResultAction extends SwipeResultActionRemoveItem {
        private DraggableSwipeableTrackRecyclerAdapter mAdapter;
        private final int mPosition;
        private Track removedItem;

        SwipeRemoveResultAction(DraggableSwipeableTrackRecyclerAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            TrackDb.delete(TrackDb.class, mAdapter.mDataset.get(mPosition).getDbId());
            DatabaseUtils.handleRemovedPositions(mAdapter.mDataset.get(mPosition).getPlaylist().getDbId(), mPosition);
            Communicator.getInstance().sendMessage(Communicator.LOCAL_PLAYLIST_ITEM_REMOVED, Integer.valueOf(mPosition));

            mAdapter.lastRemovedPosition = mPosition;
            mAdapter.lastRemovedItem = mAdapter.mDataset.get(mPosition);
            removedItem = mAdapter.lastRemovedItem;
            mAdapter.mDataset.remove(mPosition);
            mAdapter.notifyItemRemoved(mPosition);
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();

            if (mAdapter.mEventListener != null) {
                mAdapter.mEventListener.onItemRemoved(mPosition, removedItem);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }


}
