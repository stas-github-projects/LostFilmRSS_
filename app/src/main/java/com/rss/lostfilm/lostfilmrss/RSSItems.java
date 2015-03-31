package com.rss.lostfilm.lostfilmrss;

import java.util.HashMap;

/**
 * Created by Hrundel on 24.01.2015.
 */
public class RSSItems
{
    String title;
    String image;
    String serie;

    Boolean is_favorite=false;
    HashMap<String, String> formats = new HashMap<String, String>();

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getSerie() {
        return serie;
    }

    public Boolean getIsFavorite() {
        return is_favorite;
    }

    public void setIsFavorite(Boolean is_favorite) {
        this.is_favorite = is_favorite;
    }

    public String getLink(String type) {
        return formats.get(type);
    }
}
