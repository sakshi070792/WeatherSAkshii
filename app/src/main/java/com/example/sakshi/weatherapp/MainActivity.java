package com.example.sakshi.weatherapp;

import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.app.PendingIntent.getActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etname;
    String city;
    Double lats, lons;
    List<Address> addressList = null;
    static TextView tvname, tvtemp;
    TextView tvshowtemp,tvhumid,weathericon;
    Typeface weatherFont;
    Address address;
    ProgressDialog progressDialog;
    public static String setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = "&#xf00d;";
            } else {
                icon = "&#xf02e;";
            }
        } else {
            switch(id) {
                case 2 : icon = "&#xf01e;";
                    break;
                case 3 : icon = "&#xf01c;";
                    break;
                case 7 : icon = "&#xf014;";
                    break;
                case 8 : icon = "&#xf013;";
                    break;
                case 6 : icon = "&#xf01b;";
                    break;
                case 5 : icon = "&#xf019;";
                    break;
            }
        }
        return icon;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvname = (TextView) findViewById(R.id.name);
        tvtemp = (TextView) findViewById(R.id.temp);
        etname = (EditText) findViewById(R.id.cityname);
        tvshowtemp = (TextView) findViewById(R.id.showtemp);
        tvhumid= (TextView) findViewById(R.id.humid);
        weathericon= (TextView) findViewById(R.id.weather_icon);
        weatherFont = Typeface.createFromAsset(this.getAssets(), "fonts/weather.ttf");

        weathericon.setTypeface(weatherFont);

        tvshowtemp.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        city = etname.getText().toString();
       /* if (city != null) {
            //Geocoder class is used to used to get Latitude and Longitude from location name & vice versa
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(city, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // To get address from list<Address>
            if (addressList != null && addressList.size() > 0) {
                 address = addressList.get(0);
                lats = address.getLatitude();
                lons = address.getLongitude();
            } else {
                lats = 98.7;
                lons = 78.8;
            }




        }*/
        String uriPlace = Uri.encode(city);
        

        new DownloadTask().execute("http://api.openweathermap.org/data/2.5/weather?q="+uriPlace+"&&appid=3878b99de50af29d1368ae667ab1adb8");




}


    private class DownloadTask extends AsyncTask<String,Void,String>  {

        @Override

        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog=new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("PLease Wait..");
            progressDialog.setCancelable(false);
            progressDialog.show();



        }
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlconnection=null;

            try {
                url=new URL(urls[0]);
                urlconnection= (HttpURLConnection) url.openConnection();
                InputStream in=urlconnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while(data!=-1){
                    char current=(char)data;
                    result+=current;
                    data=reader.read();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(progressDialog.isShowing())
                progressDialog.dismiss();
            try {
                JSONObject jsonObject=new JSONObject(result);
                JSONObject weatherdata=new JSONObject(jsonObject.getString("main"));
                Double temperature=Double.parseDouble(weatherdata.getString("temp"));
                int humidi=Integer.parseInt(weatherdata.getString("humidity"));
                int temps=(int)(temperature-273.15);
                JSONObject details=jsonObject.getJSONArray("weather").getJSONObject(0);
                String icon=setWeatherIcon(details.getInt("id"),jsonObject.getJSONObject("sys").getLong("sunrise")*1000,jsonObject.getJSONObject("sys").getLong("sunset")*1000);
                weathericon.setText(Html.fromHtml(icon));


                String name=jsonObject.getString("name");
                MainActivity.tvtemp.setText("Temperature: "+String.valueOf(temps)+"C");
                MainActivity.tvname.setText(name);
                tvhumid.setText("Humidity: "+humidi+"%");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}



