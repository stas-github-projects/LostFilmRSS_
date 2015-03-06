package com.rss.lostfilm.lostfilmrss;

//import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Hrundel on 28.01.2015.
 */
public class listview_fragments extends Fragment implements LoaderManager.LoaderCallbacks<Object>
{
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View viewHierarchy = inflater.inflate(R.layout.listview_fragment, container, false);

        //LoaderManager _loader=getLoaderManager();
        //_loader.initLoader(0,null,listview_fragments.this);

        return viewHierarchy;
    }

    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {

    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {
        //LoaderManager _loader=getLoaderManager();
        //_loader.restartLoader(0,null,listview_fragments.this);
    }
}
