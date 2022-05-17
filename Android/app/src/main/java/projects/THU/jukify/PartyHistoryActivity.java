package projects.THU.jukify;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for PartyHistory activity
 */
public class PartyHistoryActivity extends AppCompatActivity {
    /**
     * Stores info about historical playlists
     */
    private ArrayList<String> historyIds;
    EditText editTextPartyID;
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    /**
     * Adapter for historical playlists
     */
    PartyHistoryItemAdapter partyHistoryAdapter;
    /**
     * Listview for partyhistory
     */
    ListView historyView;
    Button testme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        //Get Shared Preferences
        prefs = getSharedPreferences("userpreference", Context.MODE_PRIVATE);
        editor = prefs.edit();
        historyView = findViewById(R.id.historyView);
        //editTextPartyID = findViewById(R.id.PartyID);
        //This 2 lines make sure the listview doesn't go at the top of the screen
        historyView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        getPartyIdFromPreferences();
    }

    /**
     * Fetches attended partyIDs from Preference storage
     */
    public void getPartyIdFromPreferences(){
        int amount = prefs.getInt("amountOfParties",-1);
        historyIds = new ArrayList<>();
        String key;
        if(amount < 0 )
            return;
        for(int i = 0 ; i <= amount ; ++i){
            key = "party_"+i;
            historyIds.add(prefs.getString(key,""));
        }
        System.out.println(historyIds.toString());
        PartyHistoryItemAdapter adapter = new PartyHistoryItemAdapter(PartyHistoryActivity.this,R.layout.history_item,historyIds);
        historyView.setAdapter(adapter);
        historyView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PartyHistoryActivity.this, ViewHistoryActivity.class);
                intent.putExtra("partyId",historyIds.get(position));
                startActivity(intent);
            }
        });
    }


    // junkyard
    //Place for call to backend
// !!!!! NOT TESTED
/*
    public void getPartyHistory(View v) {

        prefs = getSharedPreferences("userpreference", Context.MODE_PRIVATE);
        editor = prefs.edit();
        //editTextPartyID = findViewById(R.id.PartyID);

        String partyId = editTextPartyID.getText().toString();

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("partyId", partyId);
        System.out.println(prefs.getString("partyId", ""));
        HTTPClient client = HTTPClient.getInstance(PartyHistoryActivity.this, Request.Method.GET, "/getPartyHistory", map, handlerGetHistory);
        Thread t = new Thread(client);
        t.start();
    }

//Handler for the Songs. Not Implemented
    final private Handler handlerGetHistory = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            System.out.println("Hello from response!");
            if (msg.obj instanceof JSONObject) {
                JSONObject response = (JSONObject) msg.obj;


                try {
                    JSONArray songs = response.getJSONArray("songs");

                    Log.i("Songs", songs.toString());
                    for (int i = 0; i < songs.length(); ++i) {


                        JSONObject internItems = songs.getJSONObject(i);

                        songList.add(internItems.getString("songs"));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    testme = findViewById(R.id.button1337);
        testme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
 */
}




