package com.rss.lostfilm.lostfilmrss;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

/**
 * Created by Hrundel on 15.01.2015.
 */
public class xml_parse extends DefaultHandler
{

    boolean bool_city;
    //String _out;
    ArrayList<String> lst_out=new ArrayList<String>();

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if(bool_city==true)
        {
            //MainActivity.
            String _s=new String(ch, start, length);
            lst_out.add(_s);
            bool_city=false;
        }
        //super.characters(ch, start, length);
    }

    public ArrayList getInfo()
    {
        return lst_out;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if(localName.equals("item"))
        {
            //String city="f-";
            //_xmldata.setCity(city);
            bool_city=true;
        }

        //super.startElement(uri, localName, qName, attributes);
    }

}
