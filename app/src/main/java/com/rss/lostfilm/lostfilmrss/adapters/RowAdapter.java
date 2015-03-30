package com.rss.lostfilm.lostfilmrss.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.rss.lostfilm.lostfilmrss.R;
import com.rss.lostfilm.lostfilmrss.RSSItems;
import com.rss.lostfilm.lostfilmrss.prefs;

import java.util.List;

/**
 * Created by Владимир on 12.03.2015.
 */
public class RowAdapter extends ArrayAdapter<RSSItems> implements View.OnClickListener {

    static float ALPHA1 = 1f;
    static float ALPHA07 = .7f;
    static int ALPHA1OLD = 255;
    static int ALPHA07OLD = 200;

    private final ImageLoader imgloader;
    private final LayoutInflater inflater;
    ViewHolder holder;

    public RowAdapter(Context context, List<RSSItems> objects) {
        super(context, 0, objects);
        imgloader=ImageLoader.getInstance();
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = inflater.inflate(R.layout.listrow, null, false);
            holder = getHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        RSSItems _rss = getItem(position);

        holder.txtTitle.setText(_rss.getTitle());
        holder.txtSerie.setText(_rss.getSerie());

        //detect format and links
        setLink(holder.img_avi, _rss.getLink("[AVI]"));
        setLink(holder.img_mp4, _rss.getLink("[MP4]"));
        setLink(holder.img_720p, _rss.getLink("[720p]"));
        setLink(holder.img_1080p, _rss.getLink("[1080p]"));

        //favorites
        holder.img_favs.setTag(_rss);
        // poster
        imgloader.displayImage(_rss.getImage(), holder.imageView);

        //favorites
        if(_rss.getIsFavorite())
            setAlpha(holder.img_favs, ALPHA1, ALPHA1OLD);
        else
            setAlpha(holder.img_favs, ALPHA07, ALPHA07OLD);

        return view;
    }

    void setLink(ImageView img, String link){
        img.setTag(link);
        float alpha1;
        float alpha1old;

        alpha1 = link!=null ? ALPHA1 : ALPHA07;
        alpha1old = link!=null ? ALPHA1OLD : ALPHA07OLD;

        setAlpha(img, alpha1, (int) alpha1old);
    }

    void setAlpha(ImageView img, float a1, int a2){
        if (Build.VERSION.SDK_INT > 14) {
            img.setAlpha(a1);
        } else
            img.getBackground().setAlpha(a2);
    }

    static class ViewHolder {
        TextView txtTitle,txtSerie;
        ImageView imageView,img_avi,img_mp4,img_720p,img_1080p,img_favs;
    }

    ViewHolder getHolder(View v){
        ViewHolder holder = new ViewHolder();
        holder.txtTitle = (TextView) v.findViewById(R.id.title);
        holder.txtSerie = (TextView) v.findViewById(R.id.series);
        holder.imageView = (ImageView) v.findViewById(R.id.img);
        holder.img_avi = (ImageView) v.findViewById(R.id.img_link_avi);
        holder.img_mp4 = (ImageView) v.findViewById(R.id.img_link_mp4);
        holder.img_720p = (ImageView) v.findViewById(R.id.img_link_720p);
        holder.img_1080p = (ImageView) v.findViewById(R.id.img_link_1080p);
        holder.img_favs = (ImageView) v.findViewById(R.id.img_favorites);

        holder.img_avi.setOnClickListener(followToLink);
        holder.img_mp4.setOnClickListener(followToLink);
        holder.img_720p.setOnClickListener(followToLink);
        holder.img_1080p.setOnClickListener(followToLink);
        holder.img_favs.setOnClickListener(this);

        return holder;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.img_favorites){
            // item
            RSSItems _rss = (RSSItems) v.getTag();
            // set favorite
            _rss.setIsFavorite(!_rss.getIsFavorite());
            if(_rss.getIsFavorite()){
                // alpha
                setAlpha((ImageView) v, ALPHA1, ALPHA1OLD);
                prefs.str_favorites+="|"+_rss.getTitle()+"|";
            }
            else{
                // alpha
                setAlpha((ImageView) v, ALPHA07, ALPHA07OLD);
                prefs.str_favorites=prefs.str_favorites.replaceAll("|"+_rss.getTitle()+"|","");
            }
            prefs.Save();
            notifyDataSetChanged();
        }
    }

    View.OnClickListener followToLink = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            String item = (String) v.getTag();
            if(item!=null) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(item));
                getContext().startActivity(intent);
            }
        }
    };
}
