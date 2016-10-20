package com.rit.sfp.myapi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
    //static final String API_KEY = "86d6fcdde6315a64";
    //static final String API_URL = "https://api.fullcontact.com/v2/person.json?";
    static final String API_KEY = "4e306afa29420c8b8c732fbe01e52e84";
    static final String API_URL ="http://api.openweathermap.org/data/2.5/weather?";

     String myFeedResult = null;
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

        private Exception exception;
        // Declare a string for 'searchString' here to be used in the doInBackground()
        String searchString;

// you might want to do some validation on the searchString here

        protected void onPreExecute() {
// what to do before the call to the Internet

            progressBar.setVisibility(View.VISIBLE);
            responseView.setText("");
            searchString = emailText.getText().toString();
        }

        protected String doInBackground(Void... urls) {
// Execute the task in the background

            try {
                URL url = new URL(API_URL + "q=" + searchString + "&APPID=" + API_KEY);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
// we have the response, turn off progress bar
            if (response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            responseView.setText(response);

// Do something with the feed
            if (!response.equals("THERE WAS AN ERROR")) {
                try {
                    Log.i("INFO", response);
                    JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                    JSONObject coord = object.getJSONObject("coord");
                    String longitude = coord.getString("lon");
                    String latitude = coord.getString("lat");
                    Log.i("RESPONSE",object.getJSONObject("coord").getString("lon"));
                    responseView.setText(responseView.getText() + "\n" + "Longitude: " + longitude
                    +"\n" + "Latitude: " +latitude);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}