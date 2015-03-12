package com.rss.lostfilm.lostfilmrss;

//import android.app.Fragment;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
//import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;

//import android.content.CursorLoader;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
        import android.support.v4.widget.SwipeRefreshLayout;

        import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.rss.lostfilm.lostfilmrss.adapters.RowAdapter;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

        import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
        import java.util.ArrayList;
        import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
        import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.R.layout.simple_list_item_1;

//import javax.xml.parsers.SAXParser;
//import javax.xml.parsers.SAXParserFactory;


public class MainActivity extends FragmentActivity implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<ArrayList<RSSItems>> {

    SwipeRefreshLayout mSwipeRefreshLayout;
    ListView _list;
    ArrayList<RSSItems> all_rssitems=new ArrayList<>();
    public static boolean bool_something_new=false;
    public static boolean bool_already_loaded=false;
    BroadcastReceiver _broadcast_receiver;
    static String _http_out;
    public static ArrayList<RSSItems> lst_rssitems=new ArrayList<>();
    public static boolean bool_update_and_xml_readed=false;
    public static long long_update=0;
    public static String s_last_xml="";
    public static final long HOUR = 3600*1000;
    public prefs _global_prefs = (prefs)getApplication();
    //Globals g = (Globals)getApplication();

    /*
    void saveText() {
    sPref = getPreferences(MODE_PRIVATE);
    Editor ed = sPref.edit();
    ed.putString(SAVED_TEXT, etText.getText().toString());
    ed.commit();
    Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show();
  }

  void loadText() {
    sPref = getPreferences(MODE_PRIVATE);
    String savedText = sPref.getString(SAVED_TEXT, "");
    etText.setText(savedText);
    Toast.makeText(this, "Text loaded", Toast.LENGTH_SHORT).show();
  }
    */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(_broadcast_receiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        StrictMode.ThreadPolicy _policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(_policy);

        //
        //reset preferences
        //
        //prefs.Reset();


        //String[] strarr = new String[all_rssitems.size()];//map_nameformat.size()];
        //ArrayAdapter<ArrayList<RSSItems>> _adapter = new listrow(MainActivity.this, strarr, all_rssitems);//imageId);
        //listrow adapter = listrow()


        //
        // broadcast receiver
        //
        _broadcast_receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(_check_internet_connect()) {
                    //loadData();

                    /*
                    NotificationCompat.Builder _notify_builder=new NotificationCompat.Builder(MainActivity.this).setContentTitle("Something heppend!!!111")
                            .setContentText("RSS have been updated");

                    Intent _intent = new Intent(MainActivity.this, MainActivity.class);

                    TaskStackBuilder _taskbuilder=TaskStackBuilder.create(MainActivity.this);
                    _taskbuilder.addParentStack(MainActivity.this);
                    _taskbuilder.addNextIntent(_intent);

                    //PendingIntent _pending=_taskbuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    PendingIntent _pending=PendingIntent.getActivity(MainActivity.this,0,_intent, 0);

                    _notify_builder.setContentIntent(_pending);
                    NotificationManager _notify_manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    _notify_manager.notify(1, _notify_builder.build());
                    */

                    /*
                    String title = "LostFilm RSS";
                    String subject = "Что-то новенькое!";
                    String body = "Нажмите, чтобы вернуться в приложение";
                    Intent _intent=new Intent(MainActivity.this, MainActivity.class);
                    _intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    NotificationManager NM=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notify=new Notification(android.R.drawable.
                            stat_notify_more,title,System.currentTimeMillis());
                    PendingIntent pending=PendingIntent.getActivity(
                            getApplicationContext(),0, new Intent(),0);
                    notify.setLatestEventInfo(getApplicationContext(),subject,body,pending);
                    NM.notify(0, notify);
                    */

                    // WORKING NOTIFICATION

                    if(bool_something_new==true) //if something new
                    {

                        Intent notificationIntent = new Intent(context, MainActivity.class);
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        PendingIntent _intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.lostfilm_logo)
                                .setContentTitle(context.getString(R.string.app_name))
                                .setContentIntent(_intent)
                                .setPriority(5) //private static final PRIORITY_HIGH = 5;
                                .setContentText("Появились новые серии!")
                                .setAutoCancel(true);
                        //.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
                        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(0, mBuilder.build());
                        bool_something_new = false;
                    }//bool

                }
            }
        };

        //IntentFilter inFilter = new IntentFilter("com.rss.lostfilm.lostfilmrss");
        IntentFilter inFilter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(_broadcast_receiver, inFilter);

        //SWIPE = push-to-refresh
        //
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        // делаем повеселее
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE);

        //ADAPTER
        //
        _list = (ListView) findViewById(R.id.list);
        //list.setAdapter(_adapter);
        //list.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, adapter);

        SwipeRefreshLayout layout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        layout.setOnRefreshListener(this);

        //fix scroll to up
        _list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {  }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                int topRowVerticalPosition =
                        (_list == null || _list.getChildCount() == 0) ?
                                0 : _list.getChildAt(0).getTop();
                mSwipeRefreshLayout.setEnabled(i == 0 && topRowVerticalPosition >= 0);
            }
        });


        if(mSwipeRefreshLayout!=null) {
            try {
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        loadData();
                    }
                });
            } catch (Exception e) {
            }
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void loadData() {
        mSwipeRefreshLayout.setRefreshing(true);

        //check internet connection
        if(_check_internet_connect()==false)
        {
            Toast t = Toast.makeText(getBaseContext(), "Нет доступа в интернет", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.CENTER,0,0);
            //sey image
            LinearLayout toast_layout=(LinearLayout)t.getView();
            ImageView toast_image=new ImageView(getBaseContext());
            toast_image.setImageResource(R.drawable.signal84);
            toast_layout.addView(toast_image, 0);
            t.show();
        }
        else {
            Toast t = Toast.makeText(getBaseContext(), "Загрузка...", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.CENTER,0,0);
            //sey image
            LinearLayout toast_layout=(LinearLayout)t.getView();
            ImageView toast_image=new ImageView(getBaseContext());
            toast_image.setImageResource(R.drawable.signal84);
            toast_layout.addView(toast_image, 0);
            t.show();
            //load data

            Loader loader = getSupportLoaderManager().getLoader(1);
            if (loader == null) {
                loader = getSupportLoaderManager().initLoader(1, null, this);
            } else {
                loader = getSupportLoaderManager().restartLoader(1, null, this);
            }
            loader.forceLoad();
        }

        mSwipeRefreshLayout.setRefreshing(false);

        //
        //send popup message
        //

        /*
        if(bool_something_new==true)
        {
            NotificationCompat.Builder _notify_builder=new NotificationCompat.Builder(this).setContentTitle("Something heppend!!!111")
                    .setContentText("RSS have been updated");

            Intent _intent=new Intent(this, MainActivity.class);

            TaskStackBuilder _taskbuilder=TaskStackBuilder.create(this);
            _taskbuilder.addParentStack(MainActivity.this);
            _taskbuilder.addNextIntent(_intent);

            PendingIntent _pending=_taskbuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            _notify_builder.setContentIntent(_pending);
            NotificationManager _notify_manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            _notify_manager.notify(0, _notify_builder.build());

            bool_something_new=false;
        }
        */



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem _mitem= menu.findItem(R.id._show_favs_menuitem);
        _mitem.setChecked(prefs.bool_show_only_favorites);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}
        if(id == R.id._show_favs_menuitem)
        {
            if(item.isChecked())
            { item.setChecked(false); prefs.bool_show_only_favorites = false; }
            else
            { item.setChecked(true); prefs.bool_show_only_favorites = true; }
            prefs.Save();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean _check_internet_connect()
    {
        Context context=getBaseContext();
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //wi-fi
        //boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        boolean isConnected;
        try {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork.isConnectedOrConnecting();
        }
        catch (Exception e)
        {isConnected=false;}
        return isConnected;
    }

    @Override
    public void onRefresh() {
        try {

            bool_already_loaded=false;
            long_update=0;
            lst_rssitems.clear();
            loadData();

        }
        catch (Exception e)
        {int i=0;}
    }


    @Override
    public Loader<ArrayList<RSSItems>> onCreateLoader(int id, Bundle args) {
        SampleLoader loader = new SampleLoader(this);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<RSSItems>> loader, ArrayList<RSSItems> data) {
        try {
            if(mSwipeRefreshLayout!=null) {
                mSwipeRefreshLayout.setRefreshing(false);
                //ArrayAdapter<ArrayList<RSSItems>> adapter;
                //adapter = new ArrayAdapter<ArrayList<RSSItems>>(this,
                //        simple_list_item_1, data);

//                String[] strarr = new String[data.size()];//map_nameformat.size()];
                RowAdapter adapter = new RowAdapter(MainActivity.this, data);

                _list.setAdapter(adapter);
            }
        }
        catch (Exception e)
        {}
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<RSSItems>> loader)
    {
        ListView lst=(ListView)findViewById(R.id.list);
        if(lst!=null)
        {lst.setAdapter(null);}
    }


    private static class SampleLoader extends AsyncTaskLoader<ArrayList<RSSItems>> {
        //ArrayList<RSSItems> lst_rssitems=new ArrayList<>();

        public SampleLoader(Context context) {
            super(context);
        }

        @Override
        public ArrayList<RSSItems> loadInBackground() {
            try {
                //load url
                //LoadXmlAndParse("http://lostfilm.tv/rss.xml");

                //clear list
                //lst_rssitems.clear();

                lst_rssitems=loadXMLviaHTTP("http://lostfilm.tv/rss.xml");

                //if error
                if(lst_rssitems==null){return null;}

                String[] strarr = new String[lst_rssitems.size()];//map_nameformat.size()];

                return lst_rssitems;
                //listrow adapter = new
                //       listrow(MainActivity.this, _title, _link, _imageurl,
                //        map_nameformat, map_links, map_images, lst_rssitems);//imageId);
                //listrow adapter = new
                //        listrow(MainActivity.this, strarr , map_nameformat, map_links, map_images, lst_rssitems);//imageId);


                //LoaderManager _loader=getSupportLoaderManager();
                //_loader.restartLoader(0,null,this);

            }
            catch (Exception e)
            {
                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                e.printStackTrace(printWriter);
                String msg = writer.toString();
                return null;
                //String msg = e.getMessage();
                //Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
            }
        }

        private void AsyncConnect(String _url)
        {
            String sret="";

            final AsyncHttpClient _async_client=new SyncHttpClient();
            _async_client.get(_url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String _charset=DetectEncoding(responseBody);
                    try {
                        _http_out=new String(responseBody, _charset);
                    } catch (UnsupportedEncodingException e) {
                        _http_out="";
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    _http_out="";
                }
            });

            //return sret;
        }

        private boolean _checkFavoritesForSerial(String _name)
        {
            if(prefs.bool_show_only_favorites)
            {
                if(prefs.str_favorites.contains("|" + _name + "|"))
                {
                    return true;//found
                }
            }
            else //if(prefs.bool_show_only_favorites)
            {
                if(prefs.str_favorites.contains("|" + _name + "|"))
                {
                    return true;//found
                }
            }
            return false;//nothing
        }

        protected ArrayList<RSSItems> loadXMLviaHTTP(String url) throws ClientProtocolException, IOException {
            BufferedReader _in=null;
            //StringBuffer _buffer=new StringBuffer("");

            try{
                /*
                //OLD
                HttpClient _httpclient=new DefaultHttpClient();
                URI _website=new URI(url);
                HttpGet _request=new HttpGet();
                _request.setURI(_website);
                HttpResponse _response=_httpclient.execute(_request);
                byte[] byte_content = EntityUtils.toByteArray(_response.getEntity());
                String _charset=DetectEncoding(byte_content);
                String _buffer2=new String(byte_content, _charset);
                */

                //TIMER
                long _now=System.currentTimeMillis();//current time
                if(_now<(long_update+(3*HOUR))){return lst_rssitems;}//if current time less than last update time + 3 hr -> exit


                //new connect
                AsyncConnect(url);

                //if already loaded -> return prev
                if(bool_already_loaded==true){return lst_rssitems;}

                //if error -> exit
                if(_http_out.length()==0){return null;}


                //load favorites
                //_options_LoadFavorites();
                //prefs.Load(new prefs());
                prefs.Load();
                //parse string


                //xml parse
                XMLSimpleParser _xmlparser=new XMLSimpleParser();
                boolean bool_parse_status=_xmlparser.Parse(_http_out);
                //get info
                if(bool_parse_status) {
                    ArrayList<String> arrlist_date = _xmlparser.GetTextInsideBlock("lastBuildDate", true);
                    ArrayList<String> arrlist_links = _xmlparser.GetTextInsideBlock("link");
                    ArrayList<String> arrlist_title = _xmlparser.GetTextInsideBlock("title");
                    ArrayList<String> arrlist_rawimage = _xmlparser.GetTextInsideBlock("description");


                    //
                    //read prefs
                    //

                    bool_something_new=false;
                    SharedPreferences _prefs=getContext().getSharedPreferences("last_build_date",Context.MODE_PRIVATE);
                    if(_prefs!=null)//read
                    {
                        String _builddate = _prefs.getString("last_build_date", "");
                        if(!_builddate.equals(arrlist_date.get(0)))//if not equal -> something new appers
                        { bool_something_new = true; }
                    }
                    else//create
                    {
                        SharedPreferences.Editor _editor=_prefs.edit();
                        _editor.putString("last_build_date", arrlist_date.get(0));
                        _editor.commit();
                        bool_something_new=true;
                    }
                    //bool_something_new=true;


                    if (arrlist_links.size() != 0) {
                        int i = 0, j = 0, j1 = 0, j2 = 0, istart = 0, iend = 0;
                        String stemptitle="";

                        //detect video format in title
                        String strformat="", strname="", strserie="", strprevname="";
                        boolean b_is_favorite = false;

                        for(i=1; i < arrlist_title.size(); i++)
                        {
                            stemptitle=arrlist_title.get(i);
                            String[] arr_str=new String[3];//();
                            j=stemptitle.lastIndexOf(". (");
                            j1=stemptitle.lastIndexOf("[",j);
                            j2=stemptitle.lastIndexOf(")");
                            if(j>-1 && j1>-1 && j2>-1)
                            {
                                strformat=stemptitle.substring(j1,j);
                                strname=stemptitle.substring(0,j1).trim();
                                strserie=stemptitle.substring(j+2,j2+1);
                                b_is_favorite=_checkFavoritesForSerial(strname);//check for serial in favorites

                                if(b_is_favorite==false && prefs.bool_show_only_favorites==true)//skip non-favorites series
                                {
                                 //TO DO
                                }
                                else if ((prefs.bool_show_only_favorites==false) || (prefs.bool_show_only_favorites==true && b_is_favorite==true)){
                                    //RSS ITEMS CLASS ITEMS
                                    if (strprevname.equals(strname)) {
                                        RSSItems _rss = lst_rssitems.get(lst_rssitems.size() - 1);
                                        _rss.formats.put(strformat, arrlist_links.get(i));
//                                        _rss.arr_format.add(strformat);
//                                        _rss.arr_links.add(arrlist_links.get(i));
                                        //lst_rssitems.add(_rss);
                                        //strprevname=strname;
                                    } else {
                                        RSSItems _rss = new RSSItems();
                                        _rss.title = strname;
                                        _rss.is_favorite = b_is_favorite;
                                        //_rss.serie=strserie;
                                        _rss.formats.put(strformat, arrlist_links.get(i));
//                                        _rss.arr_format.add(strformat);
//                                        _rss.arr_links.add(arrlist_links.get(i));
                                        _rss.serie = GetSerieFromCode(arrlist_rawimage.get(i));
                                        _rss.image = GetImageFromCode(arrlist_rawimage.get(i));
                                        lst_rssitems.add(_rss);
                                        strprevname = strname;
                                    }
                                }

                            }
                            else
                            {
                                strname=stemptitle.substring(0,j).trim();
                                b_is_favorite=_checkFavoritesForSerial(strname);//check for serial

                                if(b_is_favorite==false && prefs.bool_show_only_favorites==true)//skip non-favorites series
                                {
                                    //TO DO
                                }
                                else if ((prefs.bool_show_only_favorites==false) || (prefs.bool_show_only_favorites==true && b_is_favorite==true)) {

                                    //RSS ITEMS CLASS ITEMS
                                    if (strprevname.equals(strname)) {
                                        RSSItems _rss = lst_rssitems.get(lst_rssitems.size() - 1);
                                        _rss.formats.put("[AVI]", arrlist_links.get(i));
//                                        _rss.arr_format.add("[AVI]");
//                                        _rss.arr_links.add(arrlist_links.get(i));
                                        //lst_rssitems.add(_rss);
                                        //strprevname=strname;
                                    } else {
                                        RSSItems _rss = new RSSItems();
                                        _rss.title = strname;
                                        _rss.is_favorite = b_is_favorite;
                                        //_rss.serie=strserie;
                                        _rss.formats.put("[AVI]", arrlist_links.get(i));
//                                        _rss.arr_format.add("[AVI]");
//                                        _rss.arr_links.add(arrlist_links.get(i));
                                        _rss.serie = GetSerieFromCode(arrlist_rawimage.get(i));
                                        _rss.image = GetImageFromCode(arrlist_rawimage.get(i));
                                        lst_rssitems.add(_rss);
                                        strprevname = strname;
                                    }
                                }

                            }
                            //lst_links.add(arr_str);
                        }


                    }//if

                    //save to db
                    if(long_update==0)//create new
                    {
                        long_update=System.currentTimeMillis();//update 'update_time'
                        sql_class _sql=new sql_class(this.getContext());
                        _sql.addUpdate(new _sql_xml_full(long_update, _http_out));
                    }
                    else//update
                    {
                        long_update = System.currentTimeMillis();//update 'update_time'
                        sql_class _sql = new sql_class(this.getContext());
                        _sql.updateLastUpdateAndXML(new _sql_xml_full(long_update, _http_out));
                    }

                }//if get info

            }
            catch (Exception e)
            {return null;}

            bool_already_loaded=true;//xml successfully converted to list for sampleloader
            return lst_rssitems;
        }

        private String GetSerieFromCode(String stemp)
        {
            String st="",sret="";
            int istart=0,iend=0;

            istart=stemp.indexOf("<strong>");
            if(istart>-1) {
                iend=stemp.indexOf("</strong>", istart);
                if(iend>-1) {
                    sret=stemp.substring(istart+8,iend);
                }
            }
            return sret;
        }

        private String GetImageFromCode(String stemp)
        {
            //ArrayList<String> arrlist_googimage=new ArrayList<String>();
            String stemp2 = "", strimage = "";
            int i=0,istart=0,iend=0;

            //stemp = arrlist_rawimage.get(i);
            istart = stemp.indexOf("<img");//start tag
            if (istart > -1)
            {
                iend = stemp.indexOf(">", istart + 4);//end tag
                if (iend > -1)
                {
                    stemp2 = stemp.substring(istart, iend);
                    istart = stemp2.indexOf('"');
                    iend = stemp2.indexOf('"', istart + 1);
                    if (istart > -1 && iend > -1)
                    {
                        strimage = stemp2.substring(istart + 1, iend);
                        strimage = strimage.replaceAll("&#58;", ":");
                        strimage = strimage.replaceAll("&#46;", ".");
                        return strimage;//output to list
                    }
                }
            }
            return "";
        }

        private String DetectEncoding(byte[] _in)
        {
            String enc="cp1251";
            if(_in.length<4){return "cp1251";}
            byte[] bom=new byte[4];
            if ((bom[0] == 0xef && bom[1] == 0xbb && bom[2] == 0xbf) || // utf-8
                    (bom[0] == 0xff && bom[1] == 0xfe) || // ucs-2le, ucs-4le, and ucs-16le
                    (bom[0] == 0xfe && bom[1] == 0xff) || // utf-16 and ucs-2
                    (bom[0] == 0 && bom[1] == 0 && bom[2] == 0xfe && bom[3] == 0xff)) // ucs-4
            {
                enc = "UTF-8";
                return enc;
            }
            else
            {
                enc = "cp1251";
            }
            return enc;
        }

    }//sampleloader
}

