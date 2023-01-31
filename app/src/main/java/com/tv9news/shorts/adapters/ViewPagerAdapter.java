package com.tv9news.shorts.adapters;

import static android.view.View.GONE;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.material.imageview.ShapeableImageView;
import com.tv9news.R;
import com.tv9news.models.home.Articles;
import com.tv9news.shorts.shortutils.ExoplayerItem;
import com.tv9news.shorts.shortutils.MyData;

import java.util.List;
import java.util.Map;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder> {
    List<Articles> articlesList;
    Context context;

    Boolean isVideoPlayinge = true;

    public ViewPagerAdapter(List<Articles> articlesList, Context context) {
        this.articlesList = articlesList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_video_short, parent, false);
        return new ViewPagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerViewHolder holder, int position) {
        holder.setVideoPath(articlesList.get(position).getMedia_url());
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewPagerViewHolder holder) {
        Log.d("TAGA", "attached" + holder.getAdapterPosition());
        if (!MyData.hashMap.isEmpty()) {
            ExoplayerItem item = MyData.hashMap.get(holder.getAdapterPosition());
            ExoPlayer exoPlayer = item.getExoPlayer();
            exoPlayer.setMediaSource(item.getMediaSource());
            exoPlayer.prepare();
            holder.videoView.setPlayer(item.getExoPlayer());
            exoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState == Player.STATE_BUFFERING) {
                        holder.progressBar.setVisibility(View.VISIBLE);
                    } else if (playbackState == Player.STATE_READY) {
                        holder.progressBar.setVisibility(GONE);
                    } else if (playbackState == Player.STATE_ENDED) {
                        exoPlayer.seekTo(0);
                        exoPlayer.play();
                    }
                }

                @Override
                public void onPlayerErrorChanged(@Nullable PlaybackException error) {
                    Player.Listener.super.onPlayerErrorChanged(error);
                    Log.d("TAG", error.getLocalizedMessage());
                }

            });
            item.getExoPlayer().setPlayWhenReady(true);
            item.getExoPlayer().play();
            isVideoPlayinge = true;

        }
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewPagerViewHolder holder) {
        Log.d("TAGA", "detatched" + holder.getAdapterPosition());
        ExoplayerItem item = MyData.hashMap.get(holder.getAdapterPosition());
        holder.playVideoButton.setVisibility(GONE);
        item.getExoPlayer().setPlayWhenReady(false);
        for (Map.Entry<Integer,ExoplayerItem> entry:MyData.hashMap.entrySet()){
            if(entry.getValue().getExoPlayer().isPlaying()){
                entry.getValue().getExoPlayer().setPlayWhenReady(false);
                entry.getValue().getExoPlayer().pause();
                isVideoPlayinge = true;

            }
        }
        super.onViewDetachedFromWindow(holder);
    }


    @Override
    public int getItemCount() {
        return articlesList.size();
    }

    class ViewPagerViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        StyledPlayerView videoView;
        ExoPlayer exoPlayer= new ExoPlayer.Builder(context).build();
        MediaSource mediaSource;
        RelativeLayout playerControlContainer;
        ImageView playVideoButton;
        ShapeableImageView muteImg;

        TextView titleTv;

        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(context);

        public ViewPagerViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar_viewpager);
            videoView = itemView.findViewById(R.id.video_view);
            playerControlContainer = itemView.findViewById(R.id.playerControlContainer);
            playVideoButton = itemView.findViewById(R.id.playVideoButton);
            muteImg = itemView.findViewById(R.id.muteImg);
            titleTv = itemView.findViewById(R.id.titleTv);

            muteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    float currentVolume = exoPlayer.getVolume();
                    if (currentVolume == 0f) {
                        exoPlayer.setVolume(1f);
                        muteImg.setImageDrawable(context.getDrawable(R.drawable.ic_audio_icon));
                    } else {
                        exoPlayer.setVolume(0f);
                        muteImg.setImageDrawable(context.getDrawable(R.drawable.ic_volume_mute));

                    }
                }
            });

            playerControlContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isVideoPlayinge){
                        isVideoPlayinge = false;
                        exoPlayer.setPlayWhenReady(false);
                        playVideoButton.setVisibility(View.VISIBLE);
                    }else {
                        isVideoPlayinge = true;
                        exoPlayer.setPlayWhenReady(true);
                        playVideoButton.setVisibility(GONE);
                    }
                }
            });
        }

        void setVideoPath(String url) {
            if (MyData.hashMap.isEmpty()) {
                mediaSource = new ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(url)));
                MyData.hashMap.put(getAdapterPosition(), new ExoplayerItem(exoPlayer, getAdapterPosition(), mediaSource));
            } else if (MyData.hashMap.get(getAdapterPosition()) == null) {
                mediaSource = new ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(url)));
                MyData.hashMap.put(getAdapterPosition(), new ExoplayerItem(exoPlayer, getAdapterPosition(), mediaSource));
            }
        }


    }
}
