package com.rss.lostfilm.lostfilmrss;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by Stas on 30.03.2015.
 */
public class xml_parse_and_polish
{


    public void _polish(Context _context, String _http_out) {

        //xml parse
        XMLSimpleParser _xmlparser = new XMLSimpleParser();
        boolean bool_parse_status = _xmlparser.Parse(_http_out);
        //get info
        if (bool_parse_status) {
            ArrayList<String> arrlist_date = _xmlparser.GetTextInsideBlock("lastBuildDate", true);
            ArrayList<String> arrlist_links = _xmlparser.GetTextInsideBlock("link");
            ArrayList<String> arrlist_title = _xmlparser.GetTextInsideBlock("title");
            ArrayList<String> arrlist_rawimage = _xmlparser.GetTextInsideBlock("description");

            //
            //read prefs
            //
            MainActivity.bool_something_new = false;
            SharedPreferences _prefs = _context.getSharedPreferences("last_build_date", Context.MODE_PRIVATE);
            if (_prefs != null)//read
            {
                String _builddate = _prefs.getString("last_build_date", "");
                if (!_builddate.equals(arrlist_date.get(0)))//if not equal -> something new appers
                {
                    MainActivity.bool_something_new = true;
                }
            } else//create
            {
                SharedPreferences.Editor _editor = _prefs.edit();
                _editor.putString("last_build_date", arrlist_date.get(0));
                _editor.commit();
                MainActivity.bool_something_new = true;
            }

            if (arrlist_links.size() != 0) {
                int i = 0, j = 0, j1 = 0, j2 = 0, istart = 0, iend = 0;
                String stemptitle = "";

                //detect video format in title
                String strformat = "", strname = "", strserie = "", strprevname = "";
                boolean b_is_favorite = false;

                for (i = 1; i < arrlist_title.size(); i++) {
                    stemptitle = arrlist_title.get(i);
                    String[] arr_str = new String[3];//();
                    j = stemptitle.lastIndexOf(". (");
                    j1 = stemptitle.lastIndexOf("[", j);
                    j2 = stemptitle.lastIndexOf(")");
                    if (j > -1 && j1 > -1 && j2 > -1) {
                        strformat = stemptitle.substring(j1, j);
                        strname = stemptitle.substring(0, j1).trim();
                        strserie = stemptitle.substring(j + 2, j2 + 1);
                        b_is_favorite = _checkFavoritesForSerial(strname);//check for serial in favorites

                        if (b_is_favorite == false && prefs.bool_show_only_favorites == true)//skip non-favorites series
                        {
                            //TO DO
                        } else if ((prefs.bool_show_only_favorites == false) || (prefs.bool_show_only_favorites == true && b_is_favorite == true)) {
                            //RSS ITEMS CLASS ITEMS
                            if (strprevname.equals(strname)) {
                                RSSItems _rss = MainActivity.lst_rssitems.get(MainActivity.lst_rssitems.size() - 1);
                                _rss.formats.put(strformat, arrlist_links.get(i));
                            } else {
                                RSSItems _rss = new RSSItems();
                                _rss.title = strname;
                                _rss.is_favorite = b_is_favorite;
                                _rss.formats.put(strformat, arrlist_links.get(i));
                                _rss.serie = GetSerieFromCode(arrlist_rawimage.get(i));
                                _rss.image = GetImageFromCode(arrlist_rawimage.get(i));
                                MainActivity.lst_rssitems.add(_rss);
                                strprevname = strname;
                            }
                        }

                    } else {
                        strname = stemptitle.substring(0, j).trim();
                        b_is_favorite = _checkFavoritesForSerial(strname);//check for serial

                        if (b_is_favorite == false && prefs.bool_show_only_favorites == true)//skip non-favorites series
                        {
                            //TO DO
                        } else if ((prefs.bool_show_only_favorites == false) || (prefs.bool_show_only_favorites == true && b_is_favorite == true)) {

                            //RSS ITEMS CLASS ITEMS
                            if (strprevname.equals(strname)) {
                                RSSItems _rss = MainActivity.lst_rssitems.get(MainActivity.lst_rssitems.size() - 1);
                                _rss.formats.put("[AVI]", arrlist_links.get(i));
                            } else {
                                RSSItems _rss = new RSSItems();
                                _rss.title = strname;
                                _rss.is_favorite = b_is_favorite;
                                _rss.formats.put("[AVI]", arrlist_links.get(i));
                                _rss.serie = GetSerieFromCode(arrlist_rawimage.get(i));
                                _rss.image = GetImageFromCode(arrlist_rawimage.get(i));
                                MainActivity.lst_rssitems.add(_rss);
                                strprevname = strname;
                            }
                        }

                    }
                }


            }//if
        }
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
            String stemp2 = "", strimage = "";
            int i=0,istart=0,iend=0;

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
                        //delete text artifacts
                        strimage = stemp2.substring(istart + 1, iend);
                        strimage = strimage.replaceAll("&#58;", ":");
                        strimage = strimage.replaceAll("&#46;", ".");
                        return strimage;//output to list
                    }
                }
            }
            return "";
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

}
