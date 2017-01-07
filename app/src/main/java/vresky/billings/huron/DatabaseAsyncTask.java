package vresky.billings.huron;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DatabaseAsyncTask extends AsyncTask<String, Integer, String> {

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


                //parseResult(response);

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
/*
    @Override
    protected void onPostExecute(Integer success) {
        if(success == 1){
            Log.d("ON POST EXEC", result);
        }else{
            Log.e(TAG, "failed ");
        }
    }
*/
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
