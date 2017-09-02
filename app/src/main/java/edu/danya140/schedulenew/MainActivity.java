package edu.danya140.schedulenew;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import edu.danya140.schedulenew.Utils.Parser;

import java.io.IOException;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    protected static Document doc;
    protected TextView txt;
    Parser parser = new Parser();
    GetSchedule gts;
    String txtStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt = (TextView) findViewById(R.id.textView);
        gts = new GetSchedule();
        gts.execute();
    }


    public void setTxt(){
        txtStr = doc.toString();
        txt.setText(txtStr);
        parser.parse(doc);
        txtStr.indexOf("1");
    }

    //Internet thread
    class GetSchedule extends AsyncTask<Document,Document,Document> {

        @Override
        protected Document doInBackground(Document... params) {
            try {
                doc=getHtml("df","dfsg");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return doc;
        }

        private Map<String,String> getMidenCookies() throws IOException {

            //get cookies
            Connection.Response res = Jsoup.connect("https://cabs.itut.ru/cabinet/")
                    .referrer("http://www.google.com")
                    .method(Connection.Method.GET)
                    .timeout(0)
                    .execute();

            Map<String,String> midenCookies = res.cookies();

            return midenCookies;
        }

        private Document getHtml(String login,String pass) throws IOException{

            Map<String,String> cookies = getMidenCookies();

            //push login
            Jsoup.connect("https://cabs.itut.ru/cabinet/lib/updatesession.php?key=users&value=daniilhacker%40mail.ru")
                    .cookies(cookies)
                    .userAgent("Mozilla")
                    .method(Connection.Method.POST)
                    .execute();

            //push password
            Jsoup.connect("https://cabs.itut.ru/cabinet/lib/updatesession.php?key=parole&value=199617")
                    .cookies(cookies)
                    .userAgent("Mozilla")
                    .method(Connection.Method.POST)
                    .execute();

            //check for correct login and password
            Jsoup.connect("https://cabs.itut.ru/cabinet/lib/autentification.php")
                    .cookies(cookies)
                    .userAgent("Mozilla")
                    .method(Connection.Method.POST)
                    .execute();

            //log in to cabinet
            Jsoup.connect("https://cabs.itut.ru/cabinet/?login=yes")
                    .cookies(cookies)
                    .userAgent("Mozilla")
                    .method(Connection.Method.POST)
                    .get();

            //get schedule html
            return Jsoup.connect("https://cabs.itut.ru/cabinet/project/cabinet/forms/raspisanie.php?week=18")
                    .cookies(cookies)
                    .userAgent("Mozilla")
                    .method(Connection.Method.POST)
                    .get();
        }

        @Override
        protected void onPostExecute(Document document) {
            super.onPostExecute(document);
            setTxt();
        }
    }


    protected void makeLayout(){



    }
}

