package com.example.jsonparsing;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    String cityName,urlString;
    TextView textView2,textView3,textView4,textView5;
    @SuppressLint("SetTextI18n")
    public void getWeather(View view)
    {
        EditText editText=findViewById(R.id.cityName);

        if (editText.getText()==null){
            Toast.makeText(this, "Please enter a valid city name", Toast.LENGTH_SHORT).show();
            textView2 = findViewById(R.id.textView2);
            textView2.setText("");
            textView3 = findViewById(R.id.textView3);
            textView4 = findViewById(R.id.textView4);
            textView5 = findViewById(R.id.textView5);
            textView3.setText("");
            textView4.setText("");
            textView5.setText("");
        }
        else {
            cityName=editText.getText().toString();
            cityName = cityName.substring(0, 1).toUpperCase() + cityName.substring(1).toLowerCase();
            Log.i("city", cityName);
            urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=a84691906531edd1a1e6e8953c0b4f85";
            textView2 = findViewById(R.id.textView2);
            textView2.setText("Please Wait...");
            textView3 = findViewById(R.id.textView3);
            textView4 = findViewById(R.id.textView4);
            textView5 = findViewById(R.id.textView5);
            textView3.setText("");
            textView4.setText("");
            textView5.setText("");

            DownloadTask task = new DownloadTask(textView2, textView3, textView4, textView5);
            task.execute(urlString);
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public class DownloadTask extends AsyncTask<String, Void, String> {
        TextView text1,text2,text3,text4;
        DownloadTask(TextView text1,TextView text2,TextView text3,TextView text4)
        {
            this.text1=text1;
            this.text2=text2;
            this.text3=text3;
            this.text4=text4;
        }


        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject j=new JSONObject(s);
                String weatherData=j.getString("weather");
                Log.i("weather content", weatherData);
                JSONArray jsonArray = new JSONArray(weatherData);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    text3.setText(jsonObject.getString("main")+" : "+jsonObject.getString("description"));
                }
                JSONObject js=new JSONObject(s);
                String mainData=js.getString("main");
                //Log.i("weather content", weatherData);
                JSONObject json=new JSONObject(mainData);
                text1.setText("Temperature : "+String.format("%.2f",(Double.parseDouble(json.getString("temp"))-273))+"°C");
                text2.setText("Feels Like : "+String.format("%.2f",(Double.parseDouble(json.getString("feels_like"))-273))+"°C");
                text4.setText("Humidity : "+json.getString("humidity")+"%");
                }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.i("JSON", s);
            }
        }
    }
}