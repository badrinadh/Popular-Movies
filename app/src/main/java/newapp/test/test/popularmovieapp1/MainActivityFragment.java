package newapp.test.test.popularmovieapp1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    Loader loader;
    View view;
    GridView gridview;
    String moviesJsonStr=null;
    String orderBy;
    ArrayList<String> movieIdArray = new ArrayList<String>();
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.fragment_main, container, false);
        gridview = (GridView) view.findViewById(R.id.gridView);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent i= new Intent(getActivity(),MovieInfo.class);
                i.putExtra("id",movieIdArray.get(position));
                startActivity(i);
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        orderBy=prefs.getString("order_by_title","popular");
        loader=new Loader();

        new LoadImages().execute();

        return view;
    }

    private class LoadImages extends AsyncTask<Void, Void, String> {

        protected void onPreExecute(){
            loader.startLoader(getActivity(),"Loading","please Wait...!");
        }
        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            final String FORECAST_BASE_URL =
                    "http://api.themoviedb.org/3/discover/movie?";
            final String SORY_BY = "sort_by";
            final String APPID_PARAM = "api_key";
            final String OPEN_MOVIE_API_KEY="";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(SORY_BY,orderBy)
                    .appendQueryParameter(APPID_PARAM, OPEN_MOVIE_API_KEY)
                    .build();
            Log.e("url",builtUri.toString());
            try {
                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }else{
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        return null;
                    }
                    return moviesJsonStr= buffer.toString();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String str){
            try {
                ArrayList<String> imageArray=getImageUrl(str);
                gridview.setAdapter(new ImageAdapter(getActivity(),imageArray));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            loader.stopLoader();
        }
    }

    public ArrayList<String> getImageUrl(String str) throws JSONException {
        ArrayList<String> urlArray = new ArrayList<String>();
        JSONObject moviesJson = new JSONObject(str);
        JSONArray moviesArray = moviesJson.getJSONArray("results");
        for(int i = 0; i < moviesArray.length(); i++) {
            JSONObject moviesCast = moviesArray.getJSONObject(i);
            urlArray.add(moviesCast.getString("poster_path"));
            Log.e("test", moviesCast.getString("poster_path"));
            movieIdArray.add(moviesCast.getString("id"));
        }
        return urlArray;
    }
}
