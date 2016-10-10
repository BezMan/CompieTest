package compie.test.silve.compietest;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;

public class MainActivity extends AppCompatActivity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private Spinner spinner;
    List<String> spinnerArray = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ArrayList<JSONObject> playlistsArray = new ArrayList<>();
    List<DataVideo> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new AsyncFetch().execute();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray);

    }


    private class AsyncFetch extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your json file resides
                // Even you can make call to php file which returns json data
                url = new URL("http://www.razor-tech.co.il/hiring/youtube-api.json");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {

                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");

                // setDoOutput to true as we recieve data from json file
                conn.setDoOutput(true);

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return e1.toString();
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }


        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread

            pdLoading.dismiss();
//            data = new ArrayList<>();

            pdLoading.dismiss();
            try {

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jArray = jsonObject.getJSONArray("Playlists");

                for (int k = 0; k < jArray.length(); k++) {

                    JSONObject playlistObj = jArray.getJSONObject(k);
                    playlistsArray.add(playlistObj);
                    spinnerArray.add(playlistObj.getString("ListTitle"));

//                    JSONArray pListItems = playlistObj.getJSONArray("ListItems");
//
//                    // Extract data from json and store into ArrayList as class objects
//                    for (int i = 0; i < pListItems.length(); i++) {
//                        JSONObject json_data = pListItems.getJSONObject(i);
//                        DataVideo dataVideo = new DataVideo();
//
//                        dataVideo.setTitle(json_data.getString("Title"));
//                        dataVideo.setLink(json_data.getString("link"));
//                        dataVideo.setThumb(json_data.getString("thumb"));
//
//                        data.add(dataVideo);
//                    }

                    // Setup and Handover data to recyclerview
                    recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                    spinner = (Spinner) findViewById(R.id.spinner);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            Toast.makeText(getApplicationContext(), ""+position, Toast.LENGTH_SHORT).show();


                            try {
                                data = new ArrayList<>();
                                JSONObject jsonObject1 = playlistsArray.get(position);
                                JSONArray pListItems = jsonObject1.getJSONArray("ListItems");


                                // Extract data from json and store into ArrayList as class objects
                                for (int i = 0; i < pListItems.length(); i++) {
                                    JSONObject json_data = pListItems.getJSONObject(i);
                                    DataVideo dataVideo = new DataVideo();

                                    dataVideo.setTitle(json_data.getString("Title"));
                                    dataVideo.setLink(json_data.getString("link"));
                                    dataVideo.setThumb(json_data.getString("thumb"));

                                    data.add(dataVideo);
                                    mAdapter = new MyAdapter(MainActivity.this, data);
                                    recyclerView.setAdapter(mAdapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });


                }
            } catch (JSONException e) {
                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            }

        }

    }

}
