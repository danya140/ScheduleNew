package edu.danya140.schedulenew.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Данил on 29.08.2017.
 */
public class Parser {

    Document docum;
    String str;
    Subject[][] subjects = new Subject[6][6];

    public Subject[][] parse(Document doc){
        docum = doc;
        str = doc.toString();
        str = str.substring(str.indexOf("<table"),str.lastIndexOf("<script"));
        //parseWeek();
        //parseDays();
        parseSubjects();
        return subjects;
    }

    public String[] parseDays(Document doc){
        docum = doc;
        Element el = docum.select("table").get(1);
        String[] str = el.getElementsByTag("b").text().split(" ");
        return str;
    }

    public int parseWeek(Document doc){
        docum = doc;
        String string;
        string = str.substring(str.indexOf("№"),str.indexOf("<strong"));
        string = string.substring(1,string.length()-1);
        return Integer.parseInt(string);
    }

    private void parseSubjects(){

        String string = str.substring(str.lastIndexOf("<table"),str.lastIndexOf("</table> "));

        docum = Jsoup.parse(string);

        Element table = docum.select("table").get(0);
        Elements tr = table.getElementsByTag("tr");

        int i=-1,j=0;

        int rowspan=0;

        for(Element element : tr){
            if(isStart(element)){
                rowspan = getRowspan(element);
                j=0;

                i++;
                subjects[i][j] = parseStart(element);
                j++;
            } else {
                if(rowspan==0)continue;
                subjects[i][j] = parseSubject(element);
                j++;
                rowspan--;
            }
        }
        docum = null;
    }

    private Subject parseStart(Element el){
        Subject subj = new Subject();

        subj.setTime(el.getElementsByTag("td").get(1).text());
        subj.setName(el.getElementsByTag("td").get(2).text());
        subj.setType(el.getElementsByTag("td").get(3).text());
        subj.setTeacher(el.getElementsByTag("td").get(4).text());
        subj.setClassroom(el.getElementsByTag("td").get(5).text());

        return subj;
    }

    private Subject parseSubject(Element el){
        Subject subj = new Subject();

        subj.setTime(el.getElementsByTag("td").get(0).text());
        subj.setName(el.getElementsByTag("td").get(1).text());
        subj.setType(el.getElementsByTag("td").get(2).text());
        subj.setTeacher(el.getElementsByTag("td").get(3).text());
        subj.setClassroom(el.getElementsByTag("td").get(4).text());

        return subj;
    }

    private int getRowspan(Element el){
        String rowspanString;
        String str;

        str = el.getElementsByTag("td").get(0).toString();
        rowspanString = str.substring(str.indexOf("\"")+1,str.lastIndexOf("\""));

        return Integer.parseInt(rowspanString);
    }

    private boolean isStart(Element el){
        if(el.toString().contains("rowspan")){
            return true;
        } else {
            return false;
        }
    }

}
