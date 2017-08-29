package edu.danya140.schedulenew.Utils;

import org.jsoup.nodes.Document;

/**
 * Created by Данил on 29.08.2017.
 */
public class Parser {

    Document docum;
    String str;
    Subject[][] subjects = new Subject[6][6];

    public Subject[][] parse(Document doc){
        str = doc.toString();
        str = str.substring(str.indexOf("<table"),str.lastIndexOf("<script"));

        //parseWeek();
        parseSubjects();

        return null;
    }

    private int parseWeek(){
        String string;
        string = str.substring(str.indexOf("№"),str.indexOf("<strong"));
        string = string.substring(1,string.length()-1);
        return Integer.parseInt(string);
    }

    private void parseSubjects(){

        String string = str.substring(str.lastIndexOf("<table"),str.lastIndexOf("</table> "));

        docum = null;

    }

}
