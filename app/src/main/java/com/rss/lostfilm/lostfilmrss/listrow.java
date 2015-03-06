package com.rss.lostfilm.lostfilmrss;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import android.graphics.BitmapFactory;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;



/**
 * Created by Hrundel on 13.01.2015.
 */
    public class listrow extends ArrayAdapter<String>{
    private final Activity context;



    /////private final String[] txt_title;
    /////private final String[] txt_link;
    //private final Integer[] imageId;
    /////private final String[] url_image;
    //private ImageView imageView;

    //private Map<String,ArrayList<String>> map_nameformat = new HashMap<>();//name - format
    //private Map<String,ArrayList<String>> map_links = new HashMap<>();//name - links
    //ArrayList<String> lst_images = new ArrayList<String>();
    //private Map<String,String> map_images = new HashMap<>();
    private ArrayList<RSSItems> lst_rssitems= new ArrayList<>();

    String simg1="",simg2="",simg3="",simg4="";//images links

    //public listrow(Activity context, String[] title, String[] link, String[] imageurl,
    //               Map<String,ArrayList<String>> map_nf, Map<String,ArrayList<String>> map_l, Map<String,String> map_i,
    //               ArrayList<RSSItems> _rssitems)//Integer[] imageId) {
    //public listrow(Activity context, String[] title,
    //               Map<String,ArrayList<String>> map_nf, Map<String,ArrayList<String>> map_l, Map<String,String> map_i,
    //               ArrayList<RSSItems> _rssitems)//Integer[] imageId) {
    public listrow(Activity context, String[] title, ArrayList<RSSItems> _rssitems)
        {
        super(context, R.layout.listrow, title);
        this.context = context;
        //this.txt_title = title;
        //this.txt_link = link;
        //this.imageId = imageId;
        //this.url_image=imageurl;

        //this.map_nameformat = map_nf;
        //this.map_links = map_l;
        //this.map_images = map_i;

        this.lst_rssitems = _rssitems;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listrow, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.title);
        TextView txtSerie = (TextView) rowView.findViewById(R.id.series);
        //final TextView txtLink = (TextView) rowView.findViewById(R.id.link);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);

        final ImageView img_avi=(ImageView)rowView.findViewById(R.id.img_link_avi);
        ImageView img_mp4=(ImageView)rowView.findViewById(R.id.img_link_mp4);
        ImageView img_720p=(ImageView)rowView.findViewById(R.id.img_link_720p);
        ImageView img_1080p=(ImageView)rowView.findViewById(R.id.img_link_1080p);
        final ImageView img_favs=(ImageView)rowView.findViewById(R.id.img_favorites);
        //txtTitle.setText(txt_title[position]);

        //txtTitle.setMinLines(2);
        //txtLink.setText(txt_link[position]);
        //imageView.setImageResource(imageId[position]);


        //get INFO
        try {

            RSSItems _rss = lst_rssitems.get(position);
            if (_rss != null) {
                //name + serie
                txtTitle.setText(_rss.title);
                txtSerie.setText(_rss.serie);

                //detect format and links
                for (int j = 0; j < _rss.arr_format.size(); j++) {
                    if (_rss.arr_format.get(j).equals("[AVI]")) {
                        simg1 = _rss.arr_links.get(j);
                        img_avi.setAlpha((float) 1);
                        img_avi.setTag(simg1);
                    } else if (_rss.arr_format.get(j).equals("[MP4]")) {
                        simg2 = _rss.arr_links.get(j);
                        img_mp4.setAlpha((float) 1);
                        img_mp4.setTag(simg2);
                    } else if (_rss.arr_format.get(j).equals("[720p]")) {
                        simg3 = _rss.arr_links.get(j);
                        img_720p.setAlpha((float) 1);
                        img_720p.setTag(simg3);
                    } else if (_rss.arr_format.get(j).equals("[1080p]")) {
                        simg4 = _rss.arr_links.get(j);
                        img_1080p.setAlpha((float) 1);
                        img_1080p.setTag(simg4);
                    }
                }
                img_favs.setTag(_rss.title);//favorites

                //detect favorites in list


                //imageloader
                ImageLoader imgloader=ImageLoader.getInstance();
                imgloader.init(ImageLoaderConfiguration.createDefault(context));
                URI _uri=new URI(_rss.image);
                imgloader.displayImage(_rss.image, imageView);

                //favorites
                if(_rss.is_favorite)
                {
                    float f_alpha=img_favs.getAlpha();// ((ImageView) v).getAlpha();
                    //if(Math.abs(f_alpha - 0.4)<0.00001)//inactive
                    //{
                        img_favs.setAlpha((float) 1);
                    //}
                    //else
                    //{
                    //    img_favs.setAlpha((float) 0.4);
                    //}
                }
                else
                {
                    img_favs.setAlpha((float) 0.4);
                }


                /*
                //OLD IMAGE LOADER - custom
                //get images and links
                imageView.setImageResource(R.drawable.no_image);
                LoadImagesAsync _lia = new LoadImagesAsync();
                _lia.execute(_rss.image, imageView);
                //_lia.execute(url_image[position], imageView);
                */

            }//if
        }
        catch (Exception e)
        { }

        //CLICK LISTENERS
        //img_avi.setOnClickListener(this);
        //img_mp4.setOnClickListener(this);
        //img_720p.setOnClickListener(this);
        //img_1080p.setOnClickListener(this);

        img_avi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((ImageView) v).getAlpha()>0.5)//if image is active
                {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    Object item = ((ImageView) v).getTag();
                    if(item!=null) {
                        intent.setData(Uri.parse(item.toString()));
                        //startActivityForResult(intent);
                        Context _cont = getContext();
                        _cont.startActivity(intent);
                    }
                }
            }
        });
        img_mp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((ImageView) v).getAlpha()>0.5)//if image is active
                {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    Object item = ((ImageView) v).getTag();
                    if(item!=null) {
                        intent.setData(Uri.parse(item.toString()));
                        //startActivityForResult(intent);
                        Context _cont = getContext();
                        _cont.startActivity(intent);
                    }
                }
            }
        });
        img_720p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((ImageView) v).getAlpha()>0.5)//if image is active
                {
                    Intent intent2 = new Intent();
                    intent2.setAction(Intent.ACTION_VIEW);
                    Object item = ((ImageView) v).getTag();
                    if(item!=null) {
                        intent2.setData(Uri.parse(item.toString()));
                        //startActivityForResult(intent);
                        Context _cont = getContext();
                        _cont.startActivity(intent2);
                    }
                }
            }
        });
        img_1080p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((ImageView) v).getAlpha()>0.5)//if image is active
                {
                    Intent intent3 = new Intent();
                    intent3.setAction(Intent.ACTION_VIEW);
                    Object item = ((ImageView) v).getTag();
                    if(item!=null) {
                        intent3.setData(Uri.parse(item.toString()));
                        //startActivityForResult(intent);
                        Context _cont = getContext();
                        _cont.startActivity(intent3);
                    }
                }
            }
        });

        //FAVORITES

        img_favs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float f_alpha=((ImageView) v).getAlpha();

                boolean bool_status = false;
                String strname="";
                Object item = ((ImageView) v).getTag();
                if(item!=null) {
                    strname=(String)img_favs.getTag();
                }

                if(Math.abs(f_alpha - 0.4)<0.00001)//inactive
                {
                    img_favs.setAlpha((float) 1);
                    //save favorites
                    //strname=(String)img_favs.getTag();
                    if(strname.length()>0){ prefs.str_favorites+="|"+strname+"|"; bool_status=true; }
                }
                else
                {
                    img_favs.setAlpha((float) 0.4);
                    //remove favorites
                    if(strname.length()>0)
                    {
                        //strname=(String)img_favs.getTag();
                        String _s=prefs.str_favorites;
                        prefs.str_favorites=_s.replaceAll("|"+strname+"|","");
                        bool_status=false;
                    }
                }//if
                prefs.Save();
                _changeFavoritesState(strname, bool_status);
            }
        });

        /*
        try {
            URL newurl = new URL(url_image[position]);
            Bitmap mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
            imageView.setImageBitmap(mIcon_val);
        }
        catch(Exception e)
        {}
        finally {
            imageView.setImageResource(R.drawable.plus);
        }
        */



        //onclicklistener
        /*
        txtLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(),txtLink.getText().toString(),Toast.LENGTH_LONG).show();
                try
                {
                    Context _context=getContext();
                    Uri _uri = Uri.parse(txtLink.getText().toString());
                    _context.startActivity(new Intent(Intent.ACTION_VIEW, _uri));
                }
                catch (Exception e)
                {}
            }
        });
        */

        return rowView;
    }

    private void _changeFavoritesState(String strname, boolean bool_status)
    {
        for(int i=0;i<MainActivity.lst_rssitems.size();i++)
        {
            if(MainActivity.lst_rssitems.get(i).title.equals(strname))
            { MainActivity.lst_rssitems.get(i).is_favorite=bool_status; }
        }
    }

    /*
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.img_link_avi:
                if(simg1.length()>0)
                {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(simg1));
                    //startActivityForResult(intent);
                    Context _cont=getContext();
                    _cont.startActivity(intent);
                }
                break;
            case R.id.img_link_mp4:
                if(simg2.length()>0)
                {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(simg2));
                    //startActivityForResult(intent);
                    Context _cont=getContext();
                    _cont.startActivity(intent);
                }
                break;
            case R.id.img_link_720p:
                if(simg3.length()>0)
                {
                    Intent intent2 = new Intent();
                    intent2.setAction(Intent.ACTION_VIEW);
                    intent2.setData(Uri.parse(simg3));
                    //startActivityForResult(intent);
                    Context _cont=getContext();
                    _cont.startActivity(intent2);
                }
                break;
            case R.id.img_link_1080p:
                if(simg4.length()>0)
                {
                    Intent intent3 = new Intent();
                    intent3.setAction(Intent.ACTION_VIEW);
                    intent3.setData(Uri.parse(simg4));
                    //startActivityForResult(intent);
                    Context _cont=getContext();
                    _cont.startActivity(intent3);
                }
                break;
        }
    }
    */

    //load image async
    private class LoadImagesAsync extends AsyncTask<Object,Void,Void>
    {
        //Bitmap _pic;
        @Override
        protected void onPreExecute()
        {
            //super.onPreExecute();
        }

        //@Override
        protected void onPostExecute() {
            //super.onPostExecute(new Void());//0);
        }

        @Override
        protected Void doInBackground(Object... params) {
            try {
                String _param=(String)params[0];
                ImageView _iv=(ImageView)params[1];
                if(!_iv.getTag().equals(1)) //image not yet loaded
                {
                    if (_param.endsWith(".jpg") || _param.endsWith(".jpeg") || _param.endsWith(".png") || _param.endsWith(".gif")) {
                        URL newurl = new URL(_param);
                        Bitmap _pic = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                        //ImageView imgView = (ImageView)context.findViewById(R.id.img);
                        _iv.setImageBitmap(_pic);
                        _iv.setVisibility(View.VISIBLE);
                        _iv.setTag(1);
                    }
                }
            }
            catch(Exception e)
            {}
            //return 0;
            return null;
        }
    }

}