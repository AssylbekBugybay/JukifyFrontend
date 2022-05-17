package projects.THU.jukify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for activity AddSong
 */
public class AddSongActivity extends AppCompatActivity {

    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    // UI stuff
    private EditText searchBar;
    private Button btnSearchSong;
    private Button btnAddSongToQueue;
    private ListView searchListView;

    /**
     * String that will be searched
     */
    private String searchWord;
    /**
     * Array list storing result of the search
     */
    private ArrayList<HashMap<String,String>> searchResultArray;
    /**
     * Holds the request spotifyId as string
     */
    private String requestedSongId = null;
    /**
     * Holds the request song name as string
     */
    private String requestedSongName = null;
    /**
     * Holds the request album name as string
     */
    private String requestedSongAlbum = null;
    /**
     * Holds the request artist name as string
     */
    private String requestedSongArtist = null;
    /**
     * Holds the request img url as string
     */
    private String requestedSongImgUrl = null;

    private View selectedSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song);
        prefs = getSharedPreferences("userpreference", Context.MODE_PRIVATE);
        editor = prefs.edit();
        // get views on the UI
        searchListView = findViewById(R.id.searchView);
        searchBar = findViewById(R.id.searchBarAddSong);
        btnSearchSong = findViewById(R.id.btnSearchSong);
        btnAddSongToQueue = findViewById(R.id.addSongToQueue);
        searchResultArray = new ArrayList<>(); // init array
        // add functionality to the buttons
        btnSearchSong.setOnClickListener(new View.OnClickListener() {
            /**
             * Whenever "search song" button is pressed
             * @param v View
             */
            @Override
            public void onClick(View v) {
                searchResultArray = new ArrayList<>();
                String searching = searchBar.getText().toString();
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("searchWord", searching);
                map.put("partyId", prefs.getString("partyId", ""));
                //System.out.println(prefs.getString("partyId", ""));
                HTTPClient client = HTTPClient.getInstance(AddSongActivity.this, Request.Method.POST, "/searchSong", map, handlerSearchSong);
                Thread t = new Thread(client);
                t.start();
            }
        });
        btnAddSongToQueue.setOnClickListener(new View.OnClickListener() {
            /**
             * Whenever button "add song to queue" is pressed
             * @param v View
             */
            @Override
            public void onClick(View v) {
                if(requestedSongId == null){
                    Toast.makeText(getApplicationContext(),"Choose a song first!",Toast.LENGTH_SHORT).show();
                    return;
                }
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("spotifyId", requestedSongId);
                map.put("name", requestedSongName);
                map.put("album", requestedSongAlbum);
                map.put("artist", requestedSongArtist);
                map.put("imgUrl", requestedSongImgUrl);

                map.put("partyId", prefs.getString("partyId", ""));

                //System.out.println(prefs.getString("partyId", ""));
                HTTPClient client = HTTPClient.getInstance(AddSongActivity.this, Request.Method.POST, "/addSong", map, handlerSearchSong);
                Thread t = new Thread(client);
                t.start();
            }
        });
    }
    @Override
    public void onResume(){
        super.onResume();
        requestedSongId = null; // reset in case user opens this again
        requestedSongName = null; // reset in case user opens this again
        requestedSongAlbum = null; // reset in case user opens this again
        requestedSongArtist = null; // reset in case user opens this again
        requestedSongImgUrl = null; // reset in case user opens this again
    }
    /**
     * Handler for searching the song
     */
    final private Handler handlerSearchSong = new Handler(Looper.getMainLooper()){
        /**
         * Handles message from backend
         * @param msg Message from backend
         */
        @Override
        public void handleMessage(Message msg){
            //System.out.println("Hello from response!");
            if(msg.obj instanceof JSONObject){
                JSONObject response = (JSONObject)msg.obj;
                try {
                    //System.out.println(response.toString());
                    JSONArray respSongs = response.getJSONArray("songs");
                    for(int i = 0 ; i < respSongs.length() ; ++i){
                        HashMap<String,String> songItem = new HashMap<>();
                        JSONObject internItems = new JSONObject(respSongs.getString(i));
                        songItem.put("spotifyId", internItems.getString("spotifyId"));
                        songItem.put("name", internItems.getString("name"));
                        songItem.put("album", internItems.getString("album"));
                        songItem.put("artist", internItems.getString("artist"));
                        songItem.put("url", internItems.getString("imgUrl"));
                        searchResultArray.add(songItem);
                    }
                    SongAdapter adapter = new SongAdapter(AddSongActivity.this,R.layout.songs_view, searchResultArray);
                    searchListView.setAdapter(adapter);
                    searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        /**
                         * Whenever item is clicked on searchSongListView
                         * @param parent Adapter
                         * @param view Type of view
                         * @param position Position of the item
                         * @param id ID
                         */
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            requestedSongId = searchResultArray.get(position).get("spotifyId");
                            requestedSongName = searchResultArray.get(position).get("name");
                            requestedSongAlbum = searchResultArray.get(position).get("album");
                            requestedSongArtist = searchResultArray.get(position).get("artist");
                            requestedSongImgUrl = searchResultArray.get(position).get("url");
                            if(selectedSong!=null){
                                selectedSong.setBackgroundColor(0x00000000);
                            }
                            selectedSong=view;
                            view.setBackgroundColor(0xFF0095FF);

                        }
                    });
                } catch(Exception e){
                    e.printStackTrace();
                }
            };
        }
    };

}