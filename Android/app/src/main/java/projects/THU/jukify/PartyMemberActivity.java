package projects.THU.jukify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for PartyMember activity
 */
public class PartyMemberActivity extends AppCompatActivity {
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private String userID;
    private Intent intent;
    private String partyName;
    private String partyId;
    private TextView partyText;
    private boolean zoomOut =  false;
    /**
     * Stores data about playing queue
     */
    ArrayList<HashMap<String, String>> queueDataList;
    /**
     * Stores data about queue items
     */
    HashMap<String, String> queueItem;
    FloatingActionButton searchSong;
    BottomNavigationView navigationView;
    /**
     * Adapter for queue data
     */
    QueueCustomAdapter adapter;
    /**
     * Listview storing data about queue
     */
    ListView queueListView;
    /**
     * Image button for adding track
     */
    ImageButton addTrackButton;
    /**
     * Image button for refreshing queue
     */
    ImageButton refreshQueueButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_member);

        addTrackButton = findViewById(R.id.button_QaddSong);
        refreshQueueButton = findViewById(R.id.img_button_refresh);

        prefs = getSharedPreferences("userpreference", Context.MODE_PRIVATE);
        editor = prefs.edit();
        queueDataList = new ArrayList<>();
        queueListView = findViewById(R.id.tempQueueView);
        //Get Party Name and set it in the textView
        partyName = prefs.getString("partyName", "Default Party Name");
        partyText  = findViewById(R.id.masterPartyName);
        partyText.setText(partyName);
        //bottom nav view
        bottomNavigationView();

        //Retrieve queue and view Queue ListView
        viewQueueList();

        //Store partyid in preferences
        storePartyInPreferences();

        //Get Shared Preferences
        prefs = getSharedPreferences("userpreference", Context.MODE_PRIVATE);
        editor = prefs.edit();

        //Get partyId
        partyId = prefs.getString("partyId", "");

        //Generate qr of party name
        if(partyId != "")
            qrGenerator(partyId);

        //Show full screen qr code on click
        fullScreenImage();

        addTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PartyMemberActivity.this,AddSongActivity.class);
                startActivity(intent);
            }
        });

        refreshQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshQueue();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        refreshQueue();

    }
    /**
     * Set bottom navigation view buttons
     */
    private void bottomNavigationView(){
        //Bottom Navigation bar
        navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.getMenu().findItem(R.id.action_party).setEnabled(false);
        navigationView.setSelectedItemId(R.id.action_party);
        navigationView.setBackground(null);
        navigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.action_home:
                    Intent intent_home = new Intent(PartyMemberActivity.this, MainActivity.class);
                    startActivity(intent_home);
                    break;
                case R.id.action_party:
                    break;
                case R.id.action_settings:
                    Intent intent_settings = new Intent(PartyMemberActivity.this, SettingsActivity.class);
                    startActivity(intent_settings);
                    break;
                case R.id.action_share:
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "This is the party ID of our party: " + partyId);
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    startActivity(shareIntent);
                    break;
            }
            return false;
        });
    }

    /**
     * View Custom Queue List
     */
    private void viewQueueList() {
        queueDataList.clear();
        if (prefs.getString("jsonQueue","") != null) {
            try {
                JSONArray queueArr = new JSONArray(prefs.getString("jsonQueue",""));
                for (int i = 0; i < queueArr.length(); i++) {
                    HashMap<String, String> queueTracks = new HashMap<>();
                    JSONObject internItems = new JSONObject( (String) queueArr.get(i));
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
                    queueDataList.add(queueTracks);
                }
            } catch (JSONException e) {
            }
            adapter = new QueueCustomAdapter(PartyMemberActivity.this,R.layout.queue_list_item, queueDataList);
            queueListView.setAdapter(adapter);
        }
    }

    /**
     * Event handler for adding the next song in the queue
     * @param v View of the button
     */
    public void setNextSong(View v){
        View item = (View) v.getParent();
        int pos = queueListView.getPositionForView(item);
        queueItem = (HashMap<String, String>) adapter.getItem(pos);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("songId", queueItem.get("trackId"));
        map.put("partyId", partyId);
        //Toast.makeText(this, "PartyId: " + partyId + "--" + queueItem.get("trackId"), Toast.LENGTH_SHORT).show();
        HTTPClient client = HTTPClient.getInstance(PartyMemberActivity.this, Request.Method.POST, "/setNextSong", map, handlerNextSong);
        Thread t = new Thread(client);
        t.start();
    }

    /**
     * Handler for /setNextSong
     */
    final private Handler handlerNextSong = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg){
            if(msg.obj instanceof JSONObject){
                JSONObject response = (JSONObject)msg.obj;
                try {
                    refreshQueue();
                    /*
                    JSONArray queueRes = response.getJSONArray("queueList");
                    Log.i("Response",""+response);
                    queueDataList = new ArrayList<>();
                    for (int i = 0; i < queueRes.length(); i++) {
                        HashMap<String, String> queueTracksNew = new HashMap<>();
                        JSONObject internItems = new JSONObject((String) queueRes.get(i));
                        // Get Track id
                        queueTracksNew.put("trackId", internItems.getString("spotifyId"));
                        // Get Track name
                        queueTracksNew.put("trackName", internItems.getString("name"));
                        // Get Track album
                        queueTracksNew.put("trackAlbum", internItems.getString("album"));
                        // Get Track artist
                        queueTracksNew.put("trackArtist", internItems.getString("artist"));
                        // Get Track img
                        queueTracksNew.put("trackUrl", internItems.getString("imgUrl"));
                        queueDataList.add(queueTracksNew);

                    }
                    Log.i("Res",""+queueRes);
                    //Update adapter
                    adapter = new QueueCustomAdapter(PartyMemberActivity.this,R.layout.queue_list_item, queueDataList);
                    queueListView.setAdapter(adapter);
                   // adapter.notifyDataSetChanged();
                    */
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(PartyMemberActivity.this,"Song is added to the Queue!", Toast.LENGTH_SHORT).show();
            };
        }
    };

    /**
     * Handler for play song
     */
    final private Handler handlerPlaySong = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg){
            System.out.println("Song played!");
        }
    };

    /**
     * Generate qr code of the Party Name
     * @param PartyIdQr
     */
    public void qrGenerator(String PartyIdQr){
        QRCodeWriter writer = new QRCodeWriter();

        try {
            BitMatrix bitMatrix = writer.encode(PartyIdQr, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            ((ImageView) findViewById(R.id.qr_img)).setImageBitmap(bmp);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Show generated Qr code with a zoom in to full screen animation
     */
    public void fullScreenImage(){
        final ImagePopup imagePopup = new ImagePopup(this);
        ImageView qrImgButton = findViewById(R.id.qr_img);
        imagePopup.initiatePopup(qrImgButton.getDrawable()); // Load Image from Drawable
        qrImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /** Initiate Popup view **/
                imagePopup.viewPopup();
            }
        });
    }

    /**
     * Handler for refreshing queue
     */
    final private Handler handlerRefreshQueue = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg){
            System.out.println(msg.toString());
            queueDataList.clear();
            if(msg.obj instanceof JSONObject){
                JSONObject response = (JSONObject)msg.obj;
                try {
                    JSONArray queue = response.getJSONArray("queueList");
                    editor.putString("queueList", queue.toString());
                    editor.commit();
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
                        queueDataList.add(queueTracks);
                    }
                } catch(JSONException e){
                    e.printStackTrace();
                    System.out.println(response.toString());
                }
                adapter = new QueueCustomAdapter(PartyMemberActivity.this,R.layout.queue_list_item, queueDataList);
                queueListView.setAdapter(adapter);
            };
        }
    };

    /**
     * Function that refreshes queue
     */
    public void refreshQueue(){
        /* add listener in onCreate because it would reattach new listener every time this is called
        refreshQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Toast.makeText(PartyMemberActivity.this, "Refresh button clicked!", Toast.LENGTH_SHORT).show();
            }
        });
         */
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("partyId",prefs.getString("partyId",""));
        System.out.println(prefs.getString("partyId",""));
        HTTPClient client = HTTPClient.getInstance(PartyMemberActivity.this, Request.Method.POST,"/getQueueList",map,handlerRefreshQueue);
        Thread t = new Thread(client);
        t.start();
    }

    /**
     * Function that launches AddSongActivity
     * @param view Current view
     */
    public void launchAddSongActivity(View view){
        prefs = getSharedPreferences("userpreference", Context.MODE_PRIVATE);
        editor = prefs.edit();

        HashMap<String,String> map = new HashMap<String,String>();
        map.put("songId","1");
        map.put("partyId",prefs.getString("partyId",""));
        System.out.println(prefs.getString("partyId",""));
        HTTPClient client = HTTPClient.getInstance(PartyMemberActivity.this, Request.Method.POST,"/playSong",map,handlerPlaySong);
        Thread t = new Thread(client);
        t.start();
    }

    /**
     * Unused
     * @param view Current view
     */
    public void launchAddSongActivity2(View view) {
        Intent intent = new Intent(this, AddSongActivity.class);
        startActivity(intent);
    }

    /**
     * Stores current party in preferences and increments counter
     */
    private void storePartyInPreferences(){
        partyId = prefs.getString("partyId", "");
        int amount_of_parties= prefs.getInt("amountOfParties",-1);
        ++amount_of_parties;
        String party_id = "party_"+amount_of_parties;
        editor.putString(party_id,partyId);
        editor.putInt("amountOfParties",amount_of_parties);
        editor.commit();
    }
}