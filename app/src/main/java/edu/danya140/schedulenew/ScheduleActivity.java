package edu.danya140.schedulenew;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    public void onUpdateClick(MenuItem item){
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        relativeLayout.removeAllViews();

        doc = null;
        parser = new Parser();

        gts = new GetSchedule();
        gts.execute();

    }

    public void onNextWeek(MenuItem item){
        Toast ts = Toast.makeText(getApplicationContext(),"На следующей неделе увидишь", Toast.LENGTH_SHORT);
        ts.show();
    }

    public void onAbbrev(MenuItem item){

        deleteFile("subject.inf");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    openFileInput("subject.inf")));

            doc = Jsoup.parse(readFromFile());
            mainParse();

        } catch (FileNotFoundException e) {
            e.printStackTrace();

            gts = new GetSchedule();
            gts.execute();
        }


    }

    public void mainParse(){
        subjects = parser.parse(doc);
        days = parser.parseDays(doc);
        makeLayout();

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
            Jsoup.connect("https://cabs.itut.ru/cabinet/lib/updatesession.php?key=users&value="+readAuth(true))
                    .cookies(cookies)
                    .userAgent("Mozilla")
                    .method(Connection.Method.POST)
                    .execute();

            //push password
            Jsoup.connect("https://cabs.itut.ru/cabinet/lib/updatesession.php?key=parole&value="+readAuth(false))
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
            return Jsoup.connect("https://cabs.itut.ru/cabinet/project/cabinet/forms/raspisanie.php")
                    .cookies(cookies)
                    .userAgent("Mozilla")
                    .method(Connection.Method.GET)
                    .get();
        }

        @Override
        protected void onPostExecute(Document document) {
            super.onPostExecute(document);
            mainParse();
            saveToFile();
        }
    }

    protected void makeLayout(){
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        Toolbar toolbar = new Toolbar(this);
        /*
        TODO: Find current view by id and add layout from here

         */

        RelativeLayout mainView =(RelativeLayout) findViewById(R.id.mainLayout);

        TextView week = new TextView(this);
        week.setText("Неделя №"+parser.parseWeek(doc));
        week.setTextSize(20f);
        week.setPadding(0,0,0,5);
        layout.addView(week);

        layout.addView(createDaysLayout());

        //layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //layout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP );

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(layout);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mainView.addView(scrollView);

    }

    private LinearLayout createDaysLayout(){

        LinearLayout allDaysLayout = new LinearLayout(this);
        allDaysLayout.setOrientation(LinearLayout.VERTICAL);
        allDaysLayout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP );



        TableRow.LayoutParams inRowTextParams = new TableRow.LayoutParams();
        inRowTextParams.span = 3;

        TableRow.LayoutParams rowParams = new TableRow.LayoutParams();
        rowParams.gravity = Gravity.CENTER_HORIZONTAL;
        rowParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;

        for(int d = 0; d<subjects.length; d++){
            if(subjects[d][0] == null) continue;

            TableLayout day = new TableLayout(this);
            day.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            TextView dayName = new TextView(this);
            dayName.setText(days[d]);
            dayName.setTextSize(18f);
            dayName.setPadding(0,10,0,5);
            dayName.setLayoutParams(rowParams);

            day.addView(dayName);
            for (int i = 0; i < subjects[d].length; i++) {
                if(subjects[d][i] == null) continue;
                TableRow row = new TableRow(this);
                row.setLayoutParams(rowParams);

                TextView room = new TextView(this);
                room.setText(subjects[d][i].getClassroom());
                room.setPadding(5,0,5,0);

                TextView time = new TextView(this);
                time.setText(subjects[d][i].getTime());
                time.setPadding(5,0,5,0);

                TextView type = new TextView(this);
                type.setText(subjects[d][i].getType());
                type.setPadding(5,0,5,0);


                row.addView(room);
                row.addView(time);
                row.addView(type);

                day.addView(row);

                row = new TableRow(this);
                row.setLayoutParams(rowParams);

                TextView name = new TextView(this);
                name.setText(subjects[d][i].getName());
                name.setLayoutParams(inRowTextParams);

                row.addView(name);
                day.addView(row);

                row = new TableRow(this);
                row.setLayoutParams(rowParams);

                TextView teacher = new TextView(this);
                teacher.setText(subjects[d][i].getTeacher());
                teacher.setLayoutParams(inRowTextParams);
                teacher.setPadding(0,0,0,15);

                row.addView(teacher);
                day.addView(row);

            }

            allDaysLayout.addView(day);

        }

        return allDaysLayout;
    }

    private void saveToFile(){
        try {
            FileOutputStream writer = openFileOutput("subject.inf", Context.MODE_PRIVATE);
            writer.write(doc.toString().getBytes());

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFromFile() {

        String str = null;
        StringBuilder strBuilder = new StringBuilder();
        try {
            InputStream inputStream = openFileInput("subject.inf");
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(isr);

            String line;

            while ((line = reader.readLine()) != null) {
                strBuilder.append(line);
            }
            isr.close();

        }catch (IOException e) {
            e.printStackTrace();
        }

        str=strBuilder.toString();
        return str;
    }

    protected  String readAuth(boolean isLogin){

        String login = null;
        String pass = null;

        try {
            InputStream inputStream = openFileInput("auth.inf");
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(isr);
            login = reader.readLine();
            pass = reader.readLine();


        }catch (IOException e){
            e.printStackTrace();
        }

        if(isLogin){
            return login;
        } else {
            return pass;
        }

    }


}
