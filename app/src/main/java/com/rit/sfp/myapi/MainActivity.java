package com.rit.sfp.myapi;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText emailText;
    TextView responseView;
    ProgressBar progressBar;
    static final String API_KEY = "86d6fcdde6315a64";
    static final String API_URL = "https://api.fullcontact.com/v2/person.json?";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        responseView = (TextView) findViewById(R.id.responseView);
        emailText = (EditText) findViewById(R.id.emailText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Button queryButton = (Button) findViewById(R.id.queryButton);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RetrieveFeedTask().execute();
            }
        });
    }
    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {
       private Exception exeption;
        String email;

        protected void onPreExecute(){
            //Before going to internet
            progressBar.setVisibility(View.VISIBLE);
            responseView.setText("");
            email=emailText.getText().toString();
        }
        protected String doInBackground(Void... urls){
            try{
                URL url = new URL(API_URL+"email="+email+"&apiKey="+API_KEY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try{
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while((line=br.readLine()) != null){
                        sb.append(line).append("\n");
                    }
                    br.close();
                    return sb.toString();
                }finally{
                    urlConnection.disconnect();
                }
            }catch(Exception e){
                Log.e("ERROR",e.getMessage(),e);
                return null;
            }
        }
        protected void onPostExecute(String response){
            if(response == null){
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO",response);
            responseView.setText(response);
            try{
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                String requestId = object.getString("requestId");
                int status = object.getInt("status");
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }
}
