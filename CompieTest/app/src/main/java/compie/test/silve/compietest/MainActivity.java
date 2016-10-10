package compie.test.silve.compietest;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Movie;
import android.net.Uri;
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
    List<DataVideo> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        new AsyncFetch().execute();


    }

    private void initialize() {
        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                                            Movie movie = movieList.get(position);
                DataVideo data = dataList.get(position);
//                Toast.makeText(getApplicationContext(), data.getLink(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(data.getLink())));

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

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
                // Even you can make call to php file which returns json dataList
                url = new URL("http://www.razor-tech.co.il/hiring/youtube-api.json");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {

                // Setup HttpURLConnection class to send and receive dataList from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");

                // setDoOutput to true as we recieve dataList from json file
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

                    // Read dataList sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass dataList to onPostExecute method
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

        @Override
        protected void onPostExecute(String result) {
            //this method will be running on UI thread
            pdLoading.dismiss();
            try {

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jArray = jsonObject.getJSONArray("Playlists");

                for (int k = 0; k < jArray.length(); k++) {

                    JSONObject playlistObj = jArray.getJSONObject(k);
                    playlistsArray.add(playlistObj);
                    spinnerArray.add(playlistObj.getString("ListTitle"));

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            Toast.makeText(getApplicationContext(), ""+position, Toast.LENGTH_SHORT).show();
                            try {
                                dataList = new ArrayList<>();
                                JSONObject jsonObject1 = playlistsArray.get(position);
                                JSONArray pListItems = jsonObject1.getJSONArray("ListItems");


                                // Extract dataList from json and store into ArrayList as class objects
                                for (int i = 0; i < pListItems.length(); i++) {
                                    JSONObject json_data = pListItems.getJSONObject(i);
                                    DataVideo dataVideo = new DataVideo();

                                    dataVideo.setTitle(json_data.getString("Title"));
                                    dataVideo.setLink(json_data.getString("link"));
                                    dataVideo.setThumb(json_data.getString("thumb"));

                                    dataList.add(dataVideo);
                                    mAdapter = new MyAdapter(MainActivity.this, dataList);
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
                e.printStackTrace();
            }

        }

    }

}
