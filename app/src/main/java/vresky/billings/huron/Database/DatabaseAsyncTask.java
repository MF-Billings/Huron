package vresky.billings.huron.Database;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Patrick on 20/31/2016
 * Database async task class.
 * Used by database interface to pull/push data to the web service.
 * Web service built by Patrick.
 */
// TODO move into DatabaseInterface class?
class DatabaseAsyncTask extends AsyncTask<String, Integer, String> implements Serializable {

    public Integer success;     // 0 for failure, 1 for success
    public String result;

    private final String TAG = "DatabaseAsyncTask";

    DatabaseAsyncTask() {
        super();
        success = 0;
        result = "";
    }

    @Override
    protected String doInBackground(String... params) {
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(params[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            int statusCode = urlConnection.getResponseCode();

            //Log.d(TAG, "Before 200");

            // HTTP_OK is 200
            if (statusCode ==  200) {

                //Log.d(TAG, "200");

                inputStream = new BufferedInputStream(urlConnection.getInputStream());

                try {
                    result = convertInputStreamToString(inputStream);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                success = 1; // Successful

            } else {
                success = 0; //"Failed to fetch data!";
            }

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        } finally {
            urlConnection.disconnect();
        }
        return result;     // failure
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));

        String line = "";
        String result = "";

        while((line = bufferedReader.readLine()) != null){
            result += line;
        }

        /* Close Stream */
        if(null!=inputStream){
            inputStream.close();
        }

        return result;
    }
}
