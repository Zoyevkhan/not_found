package com.tv9news.shorts.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.tv9news.R;
import com.tv9news.models.home.Articles;
import com.tv9news.shorts.interfaces.ShortsItemClick;
import com.tv9news.utils.helpers.Constants;
import com.tv9news.utils.helpers.Helper;

import java.util.List;

public class PagerAdapter extends androidx.viewpager.widget.PagerAdapter {

    private Context context;
    private ShortsItemClick itemClick;
    private String contentType;
    private List<Articles> lists;

    public PagerAdapter(Context context, ShortsItemClick itemClick, String contentType, List<Articles> lists) {
        this.context = context;
        this.itemClick = itemClick;
        this.contentType = contentType;
        this.lists =lists;
    }

    @Override
    public int getCount() {
        return lists.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final LayoutInflater mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (contentType.equals(Constants.ARTICLES_CONTENT_TYPE)){
            final View itemView = mLayoutInflater.inflate(R.layout.article_pager_row, container, false);
            ImageView mainImageView = itemView.findViewById(R.id.mainImageView);
            ImageView shareIcon = itemView.findViewById(R.id.shareIcon);
            TextView categoryTv = itemView.findViewById(R.id.categoryTv);
            TextView timeTv = itemView.findViewById(R.id.timeTv);
            TextView titleTv = itemView.findViewById(R.id.titleTv);
            TextView descriptionTv = itemView.findViewById(R.id.descriptionTv);
            TextView readMoreTv = itemView.findViewById(R.id.readMoreTv);
            Articles data = lists.get(position);
            if (data.getArticle_image() != null && !data.getArticle_image().equals("null")) {
                Helper.INSTANCE.loadImage(mainImageView, data.getArticle_image(), "");
            }
            categoryTv.setText(Helper.INSTANCE.fromHtml(data.getCategory()));
            if (data.getPosted_on() != null) {
                timeTv.setText(Helper.INSTANCE.setNiceTimeMilis(data.getPosted_on()));
            }
            titleTv.setText(Helper.INSTANCE.fromHtml(data.getArticle_title()));
            descriptionTv.setText(Helper.INSTANCE.fromHtml(data.getArticle_description()));
            readMoreTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClick.shortsClick(data, Constants.ARTICLES_CONTENT_TYPE, position);
                }
            });
            container.addView(itemView);


            return itemView;

        }else if (contentType.equals(Constants.VIDEOS_CONTENT_TYPE)){
            final View itemView = mLayoutInflater.inflate(R.layout.video_short_row, container, false);
        ShapeableImageView mainImageView = itemView.findViewById(R.id.mainImageView);
        TextView titleTv = itemView.findViewById(R.id.titleTv);
        ImageView shareImg = itemView.findViewById(R.id.shareImg);
        ImageView playImg = itemView.findViewById(R.id.playImg);
            container.addView(itemView);
            Articles data = lists.get(position);
            if (data.getArticle_image() != null && !data.getArticle_image().equals("null")) {
                Helper.INSTANCE.loadImage(mainImageView, data.getArticle_image(), "");
            }
            titleTv.setText(Helper.INSTANCE.fromHtml(data.getArticle_title()));

            playImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClick.shortsClick(data, Constants.VIDEOS_CONTENT_TYPE, position);
                }
            });
            return itemView;

        }else {
            final View itemView = mLayoutInflater.inflate(R.layout.podcast_short_row, container, false);
            container.addView(itemView);
            ShapeableImageView mainImageView = itemView.findViewById(R.id.mainImageView);
            TextView titleTv = itemView.findViewById(R.id.titleTv);
            RelativeLayout listenNowRL = itemView.findViewById(R.id.listenNowRL);

            Articles data = lists.get(position);

            if (data.getArticle_image() != null && !data.getArticle_image().equals("null")) {
                Helper.INSTANCE.loadImage(mainImageView, data.getArticle_image(), "");
            }
            titleTv.setText(Helper.INSTANCE.fromHtml(data.getArticle_title()));

            listenNowRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClick.shortsClick(data, Constants.PODCAST_CONTENT_TYPE, position);
                }
            });

            return itemView;

        }

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }



}
