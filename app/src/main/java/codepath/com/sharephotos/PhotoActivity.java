package codepath.com.sharephotos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class PhotoActivity extends AppCompatActivity {
    public  static  final String CLIENT_ID="e05c462ebd86446ea48a5af73769b602";
    private ArrayList<InstagramPhoto>  photos;
    private InstagramPhotosAdapter aPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        //SEND OUT API REQUEST TO POPULAR PHOTOS
        photos = new ArrayList<>();
        //1 . Create the adapter linking it to the source
        aPhotos = new InstagramPhotosAdapter(this, photos);
        //2. FInd the listview from the layout
        ListView lvPhotos = (ListView) findViewById(R.id.lvPhotos);
        //3. Set the adapter binding to the Listview
        lvPhotos.setAdapter(aPhotos);
        //Fetch the popular photo
        fetchPopularPhotos();
    }

    //Trigger API request
    public  void fetchPopularPhotos(){
        String url = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;
        //create the network client
        AsyncHttpClient client = new AsyncHttpClient();
        //Trigger the get request
        client.get(url, null, new JsonHttpResponseHandler() {
                    //onSuccess (worked, 200)
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        //Expecting a JSON object
                      //Iterate each of the photo items and decode the item into a java object
                        JSONArray photosJson = null;
                        try {
                            photosJson = response.getJSONArray("data"); // Array of posts
                            //Iterate array of posts
                            for (int i=0; i < photosJson.length(); i++){
                                //get the json object at that position
                                JSONObject photoJson =  photosJson.getJSONObject(i);
                                //decode the attributes of the json into a data model
                                InstagramPhoto photo = new InstagramPhoto();
                                //Author Name:  {"data" => [x] => "user" => "username"}
                                photo.userName = photoJson.getJSONObject("user").getString("username");
                                //Caption: {"data" => [x] => "caption" => "text"}
                                photo.caption = photoJson.getJSONObject("caption").getString("text");
                                //Caption: {"data" => [x] => "images" => "standard_resolution" => "url"}
                                photo.imageUrl = photoJson.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                                //Height
                                photo.imageHeight = photoJson.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                                //Likes count
                                photo.likesCount = photoJson.getJSONObject("likes").getInt("count");
                                //Add decoded object to the photos
                                photos.add(photo);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                        //callback
                        aPhotos.notifyDataSetChanged();
                    }
                    //onFailure (fail)
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

                    }
                }
        );

    }
}
