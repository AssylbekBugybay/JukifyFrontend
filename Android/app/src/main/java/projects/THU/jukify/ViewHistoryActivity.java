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
 * Activity to view history of the party
 */
public class ViewHistoryActivity extends AppCompatActivity {
    /**
     * Stores party history
     */
    ArrayList<HashMap<String, String>> historyDataList = new ArrayList<>();
    /**
     * Listview for party history
     */
    ListView historyListView;
    String partyId;
    /**
     * Adapter for party history
     */
    ViewHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewhistory);
        historyListView = findViewById(R.id.historyListView);
        Intent intent = getIntent();
        partyId = intent.getStringExtra("partyId");
        getData();

    }

    /**
     * Gets party history from Database on backend
     */
    public void getData(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("partyId", partyId);
        HTTPClient client = HTTPClient.getInstance(ViewHistoryActivity.this, Request.Method.POST, "/getPartyHistory", map, handlerGetPartyHistory);
        Thread t = new Thread(client);
        t.start();
    }

    /**
     * Handler for getting party history from Database on backend
     */
    final private Handler handlerGetPartyHistory = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg){
            if(msg.obj instanceof JSONObject){
                JSONObject response = (JSONObject)msg.obj;
                try {
                    JSONArray queue = response.getJSONArray("songs");

                    for (int i = 0; i < queue.length(); i++) {
                        HashMap<String, String> queueTracks = new HashMap<>();
                        JSONObject internItems = new JSONObject( (String) queue.get(i));
                        // Get Track id
                        queueTracks.put("trackId", internItems.getString("spotifyId"));
                        // Get Track name
                        queueTracks.put("trackName", internItems.getString("name"));
                        // Get Track album
                        queueTracks.put("trackAlbum", internItems.getString("album"));
                        // Get Track artist
                        queueTracks.put("trackArtist", internItems.getString("artist"));
                        // Get Track img
                        queueTracks.put("trackUrl", internItems.getString("imgUrl"));
                        //System.out.println(queueTracks);
                        historyDataList.add(queueTracks);
                    }
                } catch(JSONException e){
                    e.printStackTrace();
                    System.out.println(response.toString());
                }
                //System.out.println(historyDataList.toString());
                adapter = new ViewHistoryAdapter(ViewHistoryActivity.this,R.layout.historyview_item, historyDataList);
                historyListView.setAdapter(adapter);
            };
        }
    };
}