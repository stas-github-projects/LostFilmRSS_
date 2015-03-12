package com.rss.lostfilm.lostfilmrss;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Created by Stas on 02.03.2015.
 */
public class prefs extends Application {


    //prefs
    public static boolean bool_show_only_favorites = true;
    public static String str_favorites = "";
    private static prefs _instance;
    //private SharedPreferences sPref;

    @Override
    public void onCreate() {
        super.onCreate();
        _instance=this;
        initImageLoader(this);
    }

    public static prefs getInstance() {
        return _instance;
    }


    public static void Reset()
    {
        _instance._options_Reset();
    }
    public static void Load()//prefs o)
    {
        //o._options_LoadFavorites();
        _instance._options_LoadFavorites();
        //this._options_LoadFavorites();
    }
    public static void Save()//prefs o)
    {
        //o._options_SaveFavorites();
        _instance._options_SaveFavorites();
        //this._options_SaveFavorites();
    }

    public void _options_Reset()
    {
        SharedPreferences sPref = getSharedPreferences("lostopts", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.clear();
        ed.commit();
    }

    public void _options_SaveFavorites()
    {
        SharedPreferences sPref = getSharedPreferences("lostopts", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean("_show_only_favorites", bool_show_only_favorites);
        ed.putString("_favorites", str_favorites);
        ed.commit();
    }

    public void _options_LoadFavorites()
    {
        try {
            SharedPreferences sPref = getSharedPreferences("lostopts", MODE_PRIVATE);
            bool_show_only_favorites = sPref.getBoolean("_show_only_favorites", false);
            str_favorites = sPref.getString("_favorites", "");
        }
        catch (Exception e)
        {
            StackTraceElement[] h= e.getStackTrace();
        }
    }

    public static ImageLoader initImageLoader(Context c) {
        if (ImageLoader.getInstance().isInited())
            return ImageLoader.getInstance();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(c)
                .defaultDisplayImageOptions(getImageLoaderOptions())
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileCount(100)
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // 2 Mb
                .build();

        ImageLoader.getInstance().init(config);
        return ImageLoader.getInstance();
    }

    public static DisplayImageOptions getImageLoaderOptions() {
        return getImageLoaderOptionsBuilder().build();
    }

    public static DisplayImageOptions.Builder getImageLoaderOptionsBuilder() {
        return new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .resetViewBeforeLoading(false)
                .displayer(new FadeInBitmapDisplayer(400, true, true, false));
    }

}
