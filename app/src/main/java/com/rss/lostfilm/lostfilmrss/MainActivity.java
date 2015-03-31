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
        // broadcast receiver
        //
        _broadcast_receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(_check_internet_connect()) {
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

        IntentFilter inFilter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(_broadcast_receiver, inFilter);

        //SWIPE = push-to-refresh
        //
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        // делаем повеселее
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE);

        //ADAPTER
        _list = (ListView) findViewById(R.id.list);

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
                lst_rssitems=loadXMLviaHTTP("http://lostfilm.tv/rss.xml");

                //if error
                if(lst_rssitems==null){return null;}

                String[] strarr = new String[lst_rssitems.size()];//map_nameformat.size()];

                return lst_rssitems;

            }
            catch (Exception e)
            {
                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                e.printStackTrace(printWriter);
                String msg = writer.toString();
                return null;
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

        protected ArrayList<RSSItems> loadXMLviaHTTP(String url) throws ClientProtocolException, IOException {
            BufferedReader _in=null;
            //StringBuffer _buffer=new StringBuffer("");

            try{

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
                prefs.Load();

                //parse output string
                xml_parse_and_polish _xpolish = new xml_parse_and_polish();
                _xpolish._polish(getContext(), _http_out);

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

            }
            catch (Exception e)
            {return null;}

            bool_already_loaded=true;//xml successfully converted to list for sampleloader
            return lst_rssitems;
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

