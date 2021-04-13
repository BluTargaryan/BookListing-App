package com.example.android.booklistingapp;

import android.text.TextUtils;
import android.util.Log;

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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    /**Tag for the kog messages*/
    public static final String LOG_TAG = Utils.class.getSimpleName();

    /**
     * Returns new URL object from the given string URL
     * */
     private static URL createUrl(String stringUrl) {
      URL url = null;
      try{
          url = new URL(stringUrl);
      }catch(MalformedURLException e) {
          Log.e(LOG_TAG, "Error with creating URL ", e);
      }
      return url;
     }

    /**
     * Make an HTTP request to the given URL and returns a String as the response.
     * */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return an {@link Book} object by parsing out information
     * about the first earthquake from the input earthquakeJSON string.
     */
      private static List<Book> extractfeaturefromJson(String bookJSON){
          //If JSON string is empty or null, then return early.
          if(TextUtils.isEmpty(bookJSON)){
              return null;
          }
// Create empty ArrayList to add books
          List<Book> books = new ArrayList<>();
          //Parse the JSON response string
          try{
              JSONObject baseJSONResponse = new JSONObject(bookJSON);
              JSONArray bookArray = baseJSONResponse.getJSONArray("items");

             //For each book in bookArray, create a {@link Book} object
              for(int  i=0; i <bookArray.length();i++){

                  //Get book at position i within list of books
                  JSONObject currentBook = bookArray.getJSONObject(i);

                  //For a book, extract JSONObject associated with
                  //key "volumeInfo"
                  JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");


                  //Extract value of "title"
                  String name = volumeInfo.getString("title");

                    if(name==null){
                        name="";
                    }
                  //Extract authorsArray
                  JSONArray authorsArray = volumeInfo.getJSONArray("authors");


                  //Extract first author
                  String author = authorsArray.get(0).toString();

                  if(author==null){
                      author="";
                  }
                  //Extract value of "previewLink"
                  String url = volumeInfo.getString("previewLink");

                  //Extract JSONObject of "imageLinks"
                  JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");

                  //Extract value of "thumbnail"
                  String imgurl = imageLinks.getString("thumbnail");

                  if(url==null){
                      url="";
                  }
                  /**
                   * Create a new {@link Book} object with gotten values
                   * */
                  Book booknew = new Book(name,author,url,imgurl);

                  //Add to list
                  books.add(booknew);
              }

          }catch (JSONException e){
            Log.e(LOG_TAG,"Problem parsing the book JSON results");
          }
          //Return the list of books
          return books;
      }

    /**
     * Query the Google Books dataset and return a {@link Book} object to represent a single book
     * */
    public static List<Book> fetchBookData(String requestUrl) {
        //Create URL object
        URL url = createUrl(requestUrl);

        //Perform HTTp request to the URL and receive a JSON response back
        String jsonResponse = null;

        try{
            jsonResponse = makeHttpRequest(url);
        }catch (IOException e) {
            Log.e(LOG_TAG,"Error closing input stream");
        }

        //Extract relevant fields from the JSON response and create a Book object
        List<Book> books = extractfeaturefromJson(jsonResponse);

        //Return the list of books
        return books;
    }
}
