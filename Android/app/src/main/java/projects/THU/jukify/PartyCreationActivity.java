package projects.THU.jukify;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for PartyCreationActivity
 */
public class PartyCreationActivity extends AppCompatActivity {
    /**
     * Authorization token
     */
    private String token = "notoken";
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private String userID;
    private EditText editPartyName;
    private String partyName;
    private String partyid;
    private Bundle data;
    private Context ctx;
    /**
     * Stores info whether party has been selected (cannot launch empty party)
     */
    private Boolean hasPlaylistBeenSelected = false;
    /**
     * Stores chosen playlist id
     */
    private String chosenPlaylistId = null;
    private TextView tv = null;
    /**
     * Stores featured playlists
     */
    ArrayList<HashMap<String, String>> featuredPlaylists;
    /**
     * Listview for playlists
     */
    ListView playlistView;
    private boolean firstcall = true;
    private View selectedPlaylist;
    ShareActionProvider shareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_creation);
        tv = findViewById(R.id.textView_nameOfList2);
        editPartyName = findViewById(R.id.createPartyPartyName);
        playlistView = findViewById(R.id.playlistView);
        featuredPlaylists = new ArrayList<>();
        prefs = getSharedPreferences("userpreference", Context.MODE_PRIVATE);
        editor = prefs.edit();
        userID = prefs.getString("userId","ID not found");
        Log.i("prefs",userID);
        SpotifyAuth();
        /* this is BAD it doesn't do spotifyAuth if you launch it later!!! token invalid!!!!
        if (firstcall) {
            firstcall = false;
            SpotifyAuth();
            // ??
            AlarmManager alarm = (AlarmManager) PartyCreationActivity.this.getSystemService(Context.ALARM_SERVICE);
            Intent authIntent = new Intent(PartyCreationActivity.this, SpotifyLoginActivity.class);
            authIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            authIntent.setAction("give me token please");
            authIntent.putExtra("newToken", token);
            PendingIntent validateToken = PendingIntent.getActivity(PartyCreationActivity.this, 33, authIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, AlarmManager.INTERVAL_HOUR, AlarmManager.INTERVAL_HOUR, validateToken);
        }
         */
    }

    /**
     * Launches spotify login activity from spotify SDK
     */
    private  void SpotifyAuth() {
        Intent intent = new Intent(PartyCreationActivity.this, SpotifyLoginActivity.class);
        startActivityForResult(intent, 2);
    }

    /**
     * Receives result from spotify login activity
     * @param requestCode Activity code
     * @param resultCode Activity result code
     * @param data Data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            token = data.getStringExtra("TOKEN");
            //Toast.makeText(this,token,Toast.LENGTH_SHORT).show();
            Log.i("Validation","New Token Arrived");
            getFeaturedPlaylist(token);
        }
        if (resultCode == RESULT_CANCELED) {
            Log.i("TOKEN","TOKEN :" + token);
            getFeaturedPlaylist(token);
        }
    }

    /**
     * Event handler for "Create Party" Button
     * @param view View of the button
     */
    public void onClickCreatePartyBtn(View view) {
        if(!hasPlaylistBeenSelected){
            Toast.makeText(editPartyName.getContext(),"Select a playlist first!", Toast.LENGTH_SHORT).show();
            return;
        }
        partyName = editPartyName.getText().toString();
        if(partyName.equals("") || partyName == null){
            partyName = "Default Party Name";
        }
        //Toast.makeText(editPartyName.getContext(),partyName, Toast.LENGTH_SHORT).show();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("userId", userID);
        map.put("authToken", token);
        map.put("playlistId", chosenPlaylistId);
        map.put("partyName", partyName);
        System.out.println(map.get("authToken"));
        HTTPClient client = HTTPClient.getInstance(PartyCreationActivity.this, Request.Method.POST, "/createParty", map, handlerCreateParty);
        Thread t = new Thread(client);
        t.start();
        // only start through response handler!!! otherwise it will move to partymasteractivity even when theres an error on server-side during party creation
        // startActivity(partyMasterActivity);
    }

    /**
     * Handler for /createParty
     */
    final private Handler handlerCreateParty = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg){
            if(msg.obj instanceof JSONObject){
                JSONObject response = (JSONObject)msg.obj;
                try {
                    prefs = getSharedPreferences("userpreference", Context.MODE_PRIVATE);
                    editor = prefs.edit();
                    partyid = response.getString("partyId");
                    editor.putString("partyId",partyid);
                    //JSONArray queue = response.getJSONArray("queueList");
                    //editor.putString("jsonQueue", queue.toString());
                    editor.putString("partyName", partyName);
                    editor.commit();
                    Toast.makeText(editPartyName.getContext(),"Your party id: "+ partyid, Toast.LENGTH_SHORT).show();
                    startPartyMemberActivity();

                } catch(JSONException e){
                    e.printStackTrace();
                    System.out.println(response.toString());
                }
            };
        }
    };


    /**
     * Starts party master activity (after handler receives confirmation that party is created)
     */
    private void startPartyMemberActivity(){
        Intent intent = new Intent(this, PartyMemberActivity.class);
        startActivity(intent);
    }

    /**
     * Call for /getPlaylists
     * @param token Spotify AUTH Token
     */
    public void getFeaturedPlaylist(String token) {
        Log.i("TOKEN","TOKEN :" + token);
        //Toast.makeText(this, "Try2: " + token, Toast.LENGTH_SHORT).show();
        HashMap<String,String> map = new HashMap<>();
        map.put("authToken",token);
        HTTPClient client = HTTPClient.getInstance(PartyCreationActivity.this, Request.Method.POST, "/getPlaylists", map, handlerGetFeaturedPlaylist);
        Thread t = new Thread(client);
        t.start();
    }

    /**
     * Handler for /getFeaturedPlaylist call
     */
    final private Handler handlerGetFeaturedPlaylist = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg){
            System.out.println("You received the playlist!");
            if(msg.obj instanceof JSONObject){
                // Featured Playlists json
                JSONObject response = (JSONObject)msg.obj;
                // Getting JSON Array node
                try {
                    JSONArray playlistsArr = response.getJSONArray("playlists");
                    Log.i("playlistsObj",playlistsArr.toString());
                    for (int i = 0; i < playlistsArr.length(); i++) {
                        HashMap<String, String> playlistItem = new HashMap<>();
                        //Log.i("ARRAY", ""+playlistsArr);
                        JSONObject internItems = playlistsArr.getJSONObject(i);
                        // Get Playlist id
                        playlistItem.put("listId", internItems.getString("playlistId"));
                        // Get Playlist name
                        playlistItem.put("name", internItems.getString("playlistName"));
                        // Get Playlist image
                        playlistItem.put("url", internItems.getString("imageUrl"));
                        // Get Playlist tracks total
                        playlistItem.put("total", internItems.getString("numberOfSongs"));
                        featuredPlaylists.add(playlistItem);
                    }
                } catch(JSONException e){
                    e.printStackTrace();
                }
                ListCustomAdapter adapter = new ListCustomAdapter(PartyCreationActivity.this,R.layout.playlist_view_item, featuredPlaylists);
                playlistView.setAdapter(adapter);
                playlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // when you click specific item on the listview
                        chosenPlaylistId = featuredPlaylists.get(position).get("listId");
                        tv.setText("Start with \"" + featuredPlaylists.get(position).get("name") + "\"!");
                        hasPlaylistBeenSelected = true;
                        if(selectedPlaylist!=null){
                            selectedPlaylist.setBackgroundColor(0x00000000);
                        }
                        selectedPlaylist=view;
                        view.setBackgroundColor(0xFF0095FF);
                    }
                });
            };
        }
    };
}