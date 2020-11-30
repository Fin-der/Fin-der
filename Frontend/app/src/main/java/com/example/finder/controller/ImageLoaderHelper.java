package com.example.finder.controller;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.finder.R;

public class ImageLoaderHelper {
    public static void loadProfilePic(Context context, ImageView view, String pic, int width, int height) {
        if (!pic.equals("null")) {
            Glide.with(context)
                 .load(Uri.parse(pic))
                 .override(width, height)
                 .apply(RequestOptions.circleCropTransform())
                 .into(view);
        } else {
            Glide.with(context)
                    .load(R.drawable.ic_profile_image)
                    .override(width, height)
                    .apply(RequestOptions.circleCropTransform())
                    .into(view);
        }
    }
}
