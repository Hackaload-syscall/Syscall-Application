package com.example.youngseok.syscall;

/**
 * Created by leesangwook on 2017. 11. 5..
 */

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public abstract class AccessServerDB extends AsyncTask<String, Void, String> {

    protected void onPreExecute() {

        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        String serverURL = params[0];

        try {
            //Connection
            URL url = new URL(serverURL);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();

            //Send
            if(!getPostParameters(params).equals("")) {

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(getPostParameters(params).getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
            }

            //Receieve
            InputStream inputStream;
            int responseStatusCode = httpURLConnection.getResponseCode();

            if (responseStatusCode == HttpURLConnection.HTTP_OK) {

                inputStream = httpURLConnection.getInputStream();

            } else {

                inputStream = httpURLConnection.getErrorStream();
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while((line = bufferedReader.readLine()) != null) {

                stringBuilder.append(line);
            }

            bufferedReader.close();
            Log.e("JSON", stringBuilder.toString());

            return stringBuilder.toString();

        } catch (Exception e) {

            e.printStackTrace();

            return new String("Error: " + e.getMessage());
        }
    }

    protected void onPostExecute(String result) {

        super.onPostExecute(result);
    }

    // Start at params[1]
    protected abstract String getPostParameters(String... params);
}