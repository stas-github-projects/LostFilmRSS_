package com.rss.lostfilm.lostfilmrss;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hrundel on 17.01.2015.
 */
public class XMLSimpleParser
{

    long _stopwatch_parse = 0;//=new Stopwatch();
    long _stopwatch_gettext = 0;//=new Stopwatch();

    //String xml_data;
    int xml_data_length = 0;
    boolean bool_data_status;//data.length>0 (y/n)
    ArrayList<String> xml_list_blocks = new ArrayList<String>();

    public XMLSimpleParser() {

    }

    public ArrayList<String> GetTextInsideBlock(String _block)
    {
        return GetTextInsideBlock(_block, false);
    }
    public ArrayList<String> GetTextInsideBlock(String _block, boolean exit_on_first_found) {
        int i = 0, j = 0, ipos = 0, itemplen = 0, iblocklength = _block.length(), icount = xml_list_blocks.size(), _blockendlength = 0;
        String stemp = "";
        boolean bool_found_tag = false;
        long ltemptime = 0;

        ArrayList<String> sret = new ArrayList<String>();

        String str_delim = "\".,;':\\|[]{}<>?~`!@#$%^&*()-=_+ \n";

        if (iblocklength == 0) {
            return sret;
        }


        ltemptime = System.nanoTime();//TIMER


        String _blockend = "</" + _block + ">";
        _blockendlength = _blockend.length();
        _block = "<" + _block;
        iblocklength++;

        //search
        for (i = 0; i < icount; i++) {
            stemp = xml_list_blocks.get(i);
            itemplen = stemp.length();
            if (itemplen == iblocklength) {
                bool_found_tag = false;
            } else if (itemplen > iblocklength) {
                if (stemp.substring(0, iblocklength).equals(_block)) {
                    char ch = stemp.charAt(iblocklength);//next char
                    int isdelimeter = str_delim.indexOf(ch);
                    if (isdelimeter != -1)//if next char is delim
                    {
                        bool_found_tag = true;
                    }
                }
            }
            //if TAG found
            if (bool_found_tag == true && (iblocklength + 1) == itemplen) {
                //if(icount>(i+1))//output text
                for (j = i + 1; j < icount; j++) {
                    stemp = xml_list_blocks.get(j);//.Substring(0,_blockendlength);
                    if (stemp.length() == _blockendlength)//if equal length
                    {
                        if (stemp.equals(_blockend))//if equal first chars
                        {
                            {
                                break;
                            }
                        }
                    } else {
                        stemp=xml_list_blocks.get(i + 1);
                        //replaces
                        if(stemp.contains("\'"))
                        {int i1 = 0;}
                        stemp=stemp.replaceAll("&amp;","&");
                        stemp=stemp.replaceAll("\'","'");
                        stemp=stemp.replaceAll("\\'","'");

                        sret.add(stemp);
                    }
                }

                //clear
                bool_found_tag = false;
                //EXIT on first found item
                if (exit_on_first_found == true) {
                    break;
                }
                //break;
            }
        }

        _stopwatch_gettext = System.nanoTime() - ltemptime;//TIMER
        return sret;
    }

    public String GetBlockArgument() {
        String sret = "";

        return sret;
    }

    public boolean Parse(String input_data) {
        int i = 0, ipos = 0, ilen = 0, istart = 0, xml_data_length = input_data.length();
        String stemp = "";
        char ch = '\0', chprev = '\0', chprev2 = '\0', chnext = '\0', chnext2 = '\0';
        boolean bool_comment = false, bool_text = false, bool_cdata = false;
        boolean bool_valid_xml_header = true, bool_can_add = false, bool_can_make_key = false;
        long ltemptime = 0;

        if (xml_data_length == 0) {
            return false;
        }//nothing to parse

        ltemptime = System.nanoTime();//TIMER

        //List<string> lst_blocks=new List<string>();
        try {


            for (i = 0; i < xml_data_length; i++)
            {
                ch = input_data.charAt(i);

                if (ch == '"')//TEXT // argument's VALUE
                {
                    if (bool_text == false) {
                        bool_text = true;
                    } else {
                        bool_text = false;
                    }
                }

                if (bool_text == false)
                {
                    if (ch == '<' && bool_comment == false && bool_cdata == false)//START TAG
                    {
                        if (bool_can_add == true) {
                            xml_list_blocks.add(input_data.substring(istart, i));
                            bool_can_add = false;
                        }
                        istart = i;
                        if (xml_data_length >= (i + 1)) {
                            chnext = input_data.charAt(i + 1);
                        }
                        //if(bool_comment==false && bool_cdata==false)
                        {
                            if (chnext == '?') {
                                bool_valid_xml_header = true;
                                i++;
                            } else if (chnext == '!')//COMMENT or CDATA
                            {
                                if (xml_data_length >= (i + 1)) {
                                    chnext2 = input_data.charAt(i + 2);
                                }
                                if (chnext2 == '-') {
                                    bool_comment = true;
                                    i += 2;
                                }//COMMENT
                                else if (chnext2 == '[') {
                                    bool_cdata = true;
                                    i += 2;
                                }//CDATA
                            }
                        }
                    }
                    else if (ch == '>')//CLOSE TAG
                    {
                        if (i >= 2) {
                            chprev = input_data.charAt(i - 1);
                            chprev2 = input_data.charAt(i - 2);
                        }
                        if (bool_comment == true && bool_cdata == false && chprev == '-' && chprev2 == '-') {
                            bool_comment = false;
                            istart = i + 1;
                            i += 2;
                        } else if (bool_comment == false && bool_cdata == true && chprev == ']' && chprev2 == ']') {
                            bool_cdata = false; //istart=i+1;
                            xml_list_blocks.add(input_data.substring(istart, i + 1));
                            istart = i + 1;
                            bool_can_add = false;
                            i += 2;
                        } else if (bool_comment == false && bool_cdata == false) {
                            String hh=input_data.substring(istart, i + 1);
                            xml_list_blocks.add(input_data.substring(istart, i + 1));
                            istart = i + 1;
                            bool_can_add = false;
                        }
                    }
                    else
                    {
                        if (bool_comment == false && bool_cdata == false)//IF NOT COMMENT
                        {
                            if (ch != '\r' && ch != '\n') {
                                bool_can_add = true;
                            }
                        }
                    }//if(ch=='<')

                }//bool_comment // bool_cdata
            }

            _stopwatch_parse = System.nanoTime() - ltemptime;//TIMER
            return true;
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }

        return false;
    }

    public long getTime_Parse() {
        return _stopwatch_parse;
    }

    public long getTime_GetText() {
        return _stopwatch_gettext;
    }

    private int inBlock(int ipos) {

        return ipos;
    }
}

