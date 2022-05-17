package projects.THU.jukify;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Class for PartyJoin activity
 */
public class PartyJoinActivity extends AppCompatActivity {

    private FloatingActionButton qrButton;
    private QrScanner qrClass;
    private BottomNavigationView navigationView;
    private EditText partyId;
    private Button join;
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private String userId;
    private String partyidSaved;
    /**
     * Stores passed partyId from the QR Code
     */
    private String partyIdPassed;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_party);
        prefs = getSharedPreferences("userpreference", Context.MODE_PRIVATE);
        editor = prefs.edit();
        userId = prefs.getString("userId","ID not found");
        partyId = findViewById(R.id.joinPartyCodeInput);
        join = findViewById(R.id.button_sendCode);

        //get PartyId from intent & set edit text box with the data
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            partyIdPassed = extras.getString("partyIdPassed"); // retrieve the data using keyName
            partyId.setText(partyIdPassed);
        }
        //Bottom Navigation bar
        qrButton = findViewById(R.id.button_useQRCodeJoinParty);
        navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.setBackground(null);
        navigationView.setSelectedItemId(R.id.placeholder);
        navigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.action_home:
                    Intent intent_home = new Intent(PartyJoinActivity.this, MainActivity.class);
                    startActivity(intent_home);
                    break;
                case R.id.action_party:
                    Intent intent_party = new Intent(PartyJoinActivity.this, PartyCreationActivity.class);
                    startActivity(intent_party);
                    break;
                case R.id.action_settings:
                    Intent intent_settings = new Intent(PartyJoinActivity.this, SettingsActivity.class);
                    startActivity(intent_settings);
                    break;
                case R.id.action_share:
                    break;
            }

            return false;
        });
        qrClass = new QrScanner(qrButton, this);

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("userId", userId);
                map.put("partyId", partyId.getText().toString());
                System.out.println(map.get("authToken"));
                HTTPClient client = HTTPClient.getInstance(PartyJoinActivity.this, Request.Method.POST, "/join", map, handlerJoinParty);
                Thread t = new Thread(client);
                t.start();
            }
        });
    }

    /**
     * Handler for join request
     */
    final private Handler handlerJoinParty = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg){
            if(msg.obj instanceof JSONObject){
                JSONObject response = (JSONObject)msg.obj;
                try {
                    // swap to next activity here
                    String respPartyId = response.getString("partyId");
                    String respPartyName = response.getString("partyName");
                    String respUserId = response.getString("userId");
                    String[] respDuration = {response.getString("currentMs"),response.getString("durationMs")};
                    String respSong = response.getString("song");
                    JSONArray respQueueList = response.getJSONArray("queueList");
                    Intent intent = new Intent(PartyJoinActivity.this, PartyMemberActivity.class);
                    intent.putExtra("partyName", respPartyName);
                    intent.putExtra("partyId", respPartyId);
                    intent.putExtra("currentMS",respDuration[0]);
                    intent.putExtra("totalMS",respDuration[1]);
                    editor.putString("partyId",respPartyId);
                    editor.commit();
                    System.out.println(respPartyId);
                    System.out.println(respPartyName);
                    System.out.println(respUserId);
                    System.out.println(respDuration[0]);
                    System.out.println(respDuration[1]);
                    System.out.println(respSong);
                    startActivity(intent);
                } catch(JSONException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Invalid partyId",Toast.LENGTH_SHORT).show();
                }

            };
        }
    };

    /**
     * Gets result of the QR code activity
     * @param requestCode Activity Request Code
     * @param resultCode Activity Result Code
     * @param data Data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.e("Scan*******", "Cancelled scan!");
                partyId.setText("");

            } else {
                Toast.makeText(this, "Scan Done!", Toast.LENGTH_LONG).show();
                partyId.setText(result.getContents());

                //Intent intent = new Intent(this, old_PartyMemberActivity.class);
                //intent.putExtra("partyName", result.getContents());
                //startActivity(intent);
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}