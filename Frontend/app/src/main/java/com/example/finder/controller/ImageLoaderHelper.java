package com.example.finder.controller;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.finder.R;

/**
 * Helper class to handle loading of Google Profile Picture
 *
 */
public class ImageLoaderHelper {
    /**
     * Loads profile picture into "view"
     *
     * @param context Parent of view, Context the picture will be loaded into
     * @param view The view to load the picture in
     * @param pic The URI of the picture (null if unavailable)
     * @param width Width of the profile picture to be seen on screen
     * @param height Height of the profile picture to be seen on screen
     */
    public static void loadProfilePic(Context context, ImageView view, String pic, int width, int height) {
        /*
         * If there is no given URI for their profile picture, will load the default grey profile
         * picture as a stand-in
         */
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
