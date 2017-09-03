package edu.danya140.schedulenew;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class LoginActivity extends AppCompatActivity {

    private EditText mLoginView;
    private EditText mPassView;
    private TextView mErrorTxt;
    private Button mAuthButton;

    private String mLoginString;
    private String mPassString;

    public Document doc = null;

    String str = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginView = (EditText) findViewById(R.id.loginInput);
        mPassView = (EditText) findViewById(R.id.pasInput);
        mErrorTxt = (TextView) findViewById(R.id.errorTxt);
        mAuthButton = (Button) findViewById(R.id.loginButton);
        mAuthButton.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mLoginString = mLoginView.getText().toString();
                mPassString = mPassView.getText().toString();


                try {
                    str = new GetSchedule().execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                if(Integer.parseInt(str)==1){
                    //saveAuthInf(mLoginString,mPassString);
                    Intent mainmenu = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(mainmenu);
                } else {
                    mErrorTxt.setText("Не правильный логин или пароль");
                    mErrorTxt.setTextColor(Color.RED);
                    mErrorTxt.setTextSize(24f);
                }

            }
        });
    }

    class GetSchedule extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String str = getHtml(mLoginString,mPassString);
                if(str.length()<2){
                    return "1";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "0";
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

        private String getHtml(String login,String pass) throws IOException{

            Map<String,String> cookies = getMidenCookies();

            //push login
            Jsoup.connect("https://cabs.itut.ru/cabinet/lib/updatesession.php?key=users&value=daniilhacker@mail.ru")//+login)
                    .cookies(cookies)
                    .userAgent("Mozilla")
                    .method(Connection.Method.POST)
                    .execute();

            //push password
            Jsoup.connect("https://cabs.itut.ru/cabinet/lib/updatesession.php?key=parole&value=199617")//+pass)
                    .cookies(cookies)
                    .userAgent("Mozilla")
                    .method(Connection.Method.POST)
                    .execute();

            //check for correct login and password
            return Jsoup.connect("https://cabs.itut.ru/cabinet/lib/autentification.php")
                    .cookies(cookies)
                    .userAgent("Mozilla")
                    .method(Connection.Method.POST)
                    .execute().parse().text();
        }

        @Override
        protected void onPostExecute(String document) {
            super.onPostExecute(document);

        }
    }

    protected void saveAuthInf(String mLoginString, String mPassString){

        try {
            FileOutputStream outputStream = openFileOutput("auth.inf", Context.MODE_PRIVATE);
            outputStream.write(mLoginString.getBytes());
            outputStream.write("\n".getBytes());
            outputStream.write(mPassString.getBytes());
            outputStream.close();
        } catch (IOException e){
        }
    }
}
