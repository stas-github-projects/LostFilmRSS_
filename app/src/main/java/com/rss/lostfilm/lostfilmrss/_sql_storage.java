package com.rss.lostfilm.lostfilmrss;

/**
 * Created by Hrundel on 22.02.2015.
 */
public class _sql_storage
{
        private int id;
        private String title;
        private String author;

        public _sql_storage(){}

        public _sql_storage(String title, String author) {
            super();
            this.title = title;
            this.author = author;
        }

        public int getId(){return this.id;}
        public void setId(int value){this.id = value;}
        public String getTitle(){return this.title;}
        public void setTitle(String value){this.title = value;}
        public String getAuthor(){return this.author;}
        public void setAuthor(String value){this.author=value;}
}
