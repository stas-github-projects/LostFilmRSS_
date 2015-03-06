package com.rss.lostfilm.lostfilmrss;

/**
 * Created by Hrundel on 22.02.2015.
 */
public class _sql_xml_full
{
        private long last_update;
        private String full_xml;

        public _sql_xml_full(){}

        public _sql_xml_full(long _last_update, String _full_xml) {
            super();
            this.last_update = _last_update;
            this.full_xml = _full_xml;
        }

        public long getLastUpdate(){return this.last_update;}
        public void setLastUpdate(long value){this.last_update = value;}
        public String getXmlFull(){return this.full_xml;}
        public void setXmlFull(String value){this.full_xml = value;}

}
