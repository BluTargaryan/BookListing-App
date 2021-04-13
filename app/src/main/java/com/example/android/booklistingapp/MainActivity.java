package com.example.android.booklistingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Value for log tag
    public static final String LOG_TAG = MainActivity.class.getName();




 private BookAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Find a reference to the edittext and button in the layout
        final EditText  text = (EditText) findViewById(R.id.search_main);
        Button button = (Button) findViewById(R.id.search_button);







        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                //Value for user search input
                String searchstring = text.getText().toString();
                //URL
                final String USGS_REQUEST_URL =
                        "https://www.googleapis.com/books/v1/volumes?q="+searchstring+"&key=AIzaSyBj-DvovphccjcXKS89r3Z_TA9gSMumulc";

                Log.e(LOG_TAG,searchstring);
                // Start the AsyncTask to fetch the earthquake data
                BookAsyncTask task = new BookAsyncTask();
                task.execute(USGS_REQUEST_URL);


            }
        });

        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.listview);
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);
        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected earthquake.
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                Book currentEarthquake = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });





    }



    private class BookAsyncTask extends AsyncTask<String,Void, List<Book>>{
        @Override
        protected List<Book> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            // Perform the HTTP request for earthquake data and process the response.
            //instead of hardcoding a value for url fetch the first value in the varargs
            List<Book> result = Utils.fetchBookData(urls[0]);
            return result;
        }

        @Override
        protected void onPostExecute(List<Book> data) {
            // Clear the adapter of previous earthquake data
            mAdapter.clear();

            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            }
        }
    }

}
