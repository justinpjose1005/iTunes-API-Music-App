package com.example.lybrateassignment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {
    private final List<Track> mList;
    private final Context mContext;
    private final OnTrackListener mOnTrackListener;

    public TrackAdapter(Context context, List<Track> list, OnTrackListener onTrackListener) {
        this.mContext = context;
        this.mList = list;
        this.mOnTrackListener = onTrackListener;
        notifyDataSetChanged();
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_track, parent, false);
        return new ViewHolder(view, mOnTrackListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder viewHolder, int position) {
        Track track = mList.get(position);
        String trackInfo = "Track Name: " + track.getTrack_name();
        viewHolder.trackInfoText.setText(trackInfo);
        viewHolder.trackInfoText.setSelected(true);
        String artistInfo = "Artist Name: " + track.getArtist_name() + " | Artist ID: " + track.getArtist_id();
        viewHolder.artistInfoText.setText(artistInfo);
        viewHolder.artistInfoText.setSelected(true);
        Glide.with(mContext).load(track.getArtwork_url()).into(viewHolder.artworkImage);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView trackInfoText, artistInfoText;
        ImageView artworkImage;
        OnTrackListener onTrackListener;

        public ViewHolder(View view, OnTrackListener onTrackListener) {
            super(view);
            trackInfoText = view.findViewById(R.id.trackInfoText);
            artistInfoText = view.findViewById(R.id.artistInfoText);
            artworkImage = view.findViewById(R.id.artworkImage);
            this.onTrackListener = onTrackListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onTrackListener.onTrackClick(getAdapterPosition());
        }
    }

    public interface OnTrackListener {
        void onTrackClick(int position);
    }
}
