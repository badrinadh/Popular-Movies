package newapp.test.test.popularmovieapp1;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieInfoFragment extends Fragment {

    String movieId;
    View view;
    Loader loader;
    String moviesInfoJsonStr;
    ImageView img1,img2;
    TextView title,rating,release,votes,description;
    int width,oneThird;
    public MovieInfoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        oneThird=Math.round(width/3);
        movieId=getActivity().getIntent().getExtras().getString("id");
        view=inflater.inflate(R.layout.fragment_movie_info, container, false);
        img1=(ImageView) view.findViewById(R.id.imageView);
        img2=(ImageView) view.findViewById(R.id.poster);
        title=(TextView) view.findViewById(R.id.title);
        rating=(TextView) view.findViewById(R.id.ratting);
        release=(TextView) view.findViewById(R.id.release);
        votes=(TextView) view.findViewById(R.id.votes);
        description=(TextView) view.findViewById(R.id.description);

        Toast.makeText(getActivity(),"Id : "+width,Toast.LENGTH_LONG).show();
        loader=new Loader();
        new LoadMovieInfo().execute();
        return view;
    }

    private class LoadMovieInfo extends AsyncTask<Void, Void, String> {

        protected void onPreExecute(){
            loader.startLoader(getActivity(),"Loading","please Wait...!");
        }
        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            final String FORECAST_BASE_URL =
                    "https://api.themoviedb.org/3/movie/"+movieId+"?";
            final String APPID_PARAM = "api_key";
            final String OPEN_MOVIE_API_KEY="";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(APPID_PARAM, OPEN_MOVIE_API_KEY)
                    .build();
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
                    return moviesInfoJsonStr= buffer.toString();
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
                getMovieInfoJson(str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            loader.stopLoader();
        }
    }

    public void getMovieInfoJson(String str) throws JSONException {
        JSONObject object1=new JSONObject(str);
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w500"+object1.getString("backdrop_path"))
                .resize(width,600)
                .centerCrop()
                .placeholder(R.drawable.loading)
                .into(img1);
        title.setText(object1.getString("original_title"));
        rating.setText(object1.getString("vote_average")+"/10");
        votes.setText("Votes : "+object1.getString("vote_count"));
        release.setText("Released : "+object1.getString("release_date"));
        description.setText(object1.getString("overview"));
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w500" + object1.getString("poster_path"))
                .placeholder(R.drawable.loading)
                .into(img2);
    }
}