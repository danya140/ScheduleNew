package edu.danya140.schedulenew;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

import edu.danya140.schedulenew.Utils.Parser;
import edu.danya140.schedulenew.Utils.Subject;

public class ScheduleActivity extends AppCompatActivity {

    protected static Document doc;
    protected TextView txt;
    Parser parser = new Parser();
    GetSchedule gts;
    String txtStr;
    Subject[][] subjects;
    String[] days;

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        txt = (TextView) findViewById(R.id.textView);
        gts = new GetSchedule();
        gts.execute();

    }

    public void setTxt(){
        //txtStr = doc.toString();
        //txt.setText(txtStr);
        subjects = parser.parse(doc);
        days = parser.parseDays(doc);
        makeLayout();
        //txtStr.indexOf("1");
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
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        Toolbar toolbar = new Toolbar(this);
        /*
        TODO: Find current view by id and add layout from here

         */

        TextView week = new TextView(this);
        week.setText("Неделя№"+parser.parseWeek(doc));
        layout.addView(week);

        layout.addView(createDaysLayout());

        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        HorizontalScrollView scrollView  = new HorizontalScrollView(this);
        scrollView.addView(layout);

        setContentView(scrollView);

    }

    private LinearLayout createDaysLayout(){

        LinearLayout allDaysLayout = new LinearLayout(this);

        for(int d = 0; d<subjects.length; d++){
            TableLayout day = new TableLayout(this);

            for (int i = 0; i < subjects[d].length; i++) {
                if(subjects[d][i] == null) continue;
                TableRow row = new TableRow(this);

                TextView room = new TextView(this);
                room.setText(subjects[d][i].getClassroom());
                TextView time = new TextView(this);
                time.setText(subjects[d][i].getTime());
                TextView type = new TextView(this);
                type.setText(subjects[d][i].getType());

                row.addView(room);
                row.addView(time);
                row.addView(type);

                day.addView(row);

            }

            allDaysLayout.addView(day);

        }

        return allDaysLayout;
    }

}
