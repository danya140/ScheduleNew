package edu.danya140.schedulenew;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.FileOutputStream;
import java.io.IOException;


public class LoginActivity extends AppCompatActivity {

    private EditText mLoginView;
    private EditText mPassView;
    private Button mAuthButton;

    private String mLoginString;
    private String mPassString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginView = (EditText) findViewById(R.id.loginInput);
        mPassView = (EditText) findViewById(R.id.pasInput);
        mAuthButton = (Button) findViewById(R.id.loginButton);
        mAuthButton.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mLoginString = mLoginView.getText().toString();
                mPassString = mPassView.getText().toString();

                saveAuthInf(mLoginString,mPassString);

                Intent mainmenu = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(mainmenu);
            }
        });
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
