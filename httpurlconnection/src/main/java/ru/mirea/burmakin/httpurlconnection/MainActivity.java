package ru.mirea.burmakin.httpurlconnection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView DownloadTextView;
    TextView IPTextView;
    TextView CountryTextView;
    TextView ContinentTextView;
    TextView CityTextView;
    String url = "https://ipwho.is/109.252.177.152";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownloadTextView = (TextView) findViewById(R.id.textView);
        IPTextView = (TextView) findViewById(R.id.textView_Ip);
        CountryTextView = (TextView) findViewById(R.id.textView_Country);
        ContinentTextView = (TextView) findViewById(R.id.textView_Continent);
        CityTextView = (TextView) findViewById(R.id.textView_City);
    }



    public void onClick(View view) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = null;
        if (connectivityManager != null) {
            networkinfo = connectivityManager.getActiveNetworkInfo();
        }
        if (networkinfo != null && networkinfo.isConnected()) {
            new DownloadPageTask().execute(url); // запускаем в новом потоке
        } else {
            Toast.makeText(this, "Нет интернета", Toast.LENGTH_SHORT).show();
        }
    }



    private class DownloadPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DownloadTextView.setText("Загружаем...");
        }
        @Override
        protected String doInBackground(String... urls) {
            try {
                DownloadTextView.setText("Загрузка выполнена");
                return downloadIpInfo(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                DownloadTextView.setText("Ошибка");
                return "error";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            //resultTextView.setText(result);
            Log.d(MainActivity.class.getSimpleName(), result);
            try {
                JSONObject responseJson = new JSONObject(result);
                String ip = responseJson.getString("ip");
                String Country = responseJson.getString("country");
                String City = responseJson.getString("city");
                String Continent = responseJson.getString("continent");
                Log.d(MainActivity.class.getSimpleName(), ip);
                IPTextView.setText("IP: " + ip);
                CountryTextView.setText("Country: " + Country);
                CityTextView.setText("City: " + City);
                ContinentTextView.setText("Continent: " + Continent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }

        public void execute(URL url) {
        }
    }
    private String downloadIpInfo(String address) throws IOException {
        InputStream inputStream = null;
        String data = "";
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setReadTimeout(100000);
            connection.setConnectTimeout(100000);
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 200 OK
                inputStream = connection.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int read = 0;
                while ((read = inputStream.read()) != -1) {
                    bos.write(read);
                }
                byte[] result = bos.toByteArray();
                bos.close();
                data = new String(result);
            } else {
                data = connection.getResponseMessage() + " . Error Code : " + responseCode;
            }
            connection.disconnect();
            //return data;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return data;
    }
}

