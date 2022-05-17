package projects.THU.jukify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PrototypeActivity extends AppCompatActivity {
    private Context ctx;
    private final short IDLENGTH = 6;
    private String userID;
    private String token;
    private TextView tv;
    private JSONObject response;
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    private String partyid;
    final private Handler handlerCreateParty = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg){
            System.out.println("Hello from response!");
            if(msg.obj instanceof JSONObject){
                JSONObject response = (JSONObject)msg.obj;
                try {
                    partyid = response.getString("partyId");
                } catch(JSONException e){
                    e.printStackTrace();
                }

            };
        }
    };
    final private Handler handlerPlaySong = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg){
            System.out.println("Hello from response 2!");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prototype);
        tv = findViewById(R.id.text);

         prefs = this.getSharedPreferences("userpreference",Context.MODE_PRIVATE);
         editor = prefs.edit();



        Log.i("msg","Hello from terminal");
        System.out.println("Hello from terminal");
        // getID encoded in base64

         userID = IDgenerator.getBase62(5);


        editor.putString("userID",userID);
        editor.commit();


        Toast.makeText(this,"Your ID is: " + userID,Toast.LENGTH_SHORT).show();
        System.out.println(userID);

    }



    public void onClick1(View v) {
        SpotifyAuth();
    }

    public void onClick2(View v) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("masterId", "1337");
        map.put("authToken", token);
        HTTPClient client = HTTPClient.getInstance(PrototypeActivity.this, Request.Method.POST, "/createparty", map, handlerCreateParty);
        Thread t = new Thread(client);
        t.start();
    }

    public void onClick3(View v){
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("songId","1");
        map.put("partyId",partyid);
        HTTPClient client = HTTPClient.getInstance(PrototypeActivity.this, Request.Method.POST,"/playSong",map,handlerPlaySong);
        Thread t = new Thread(client);
        t.start();
    }

    private void SpotifyAuth() {
        Intent intent = new Intent(PrototypeActivity.this, SpotifyLoginActivity.class);
          startActivityForResult(intent, 2);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            token = data.getStringExtra("TOKEN");
            if (resultCode == RESULT_CANCELED) {
                tv.setText("Nothing selected");

                Log.i("TOKEN","TOKEN :" + token);
                Toast.makeText(this,token,Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


            prefs  = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
           System.out.println(prefs.getString("userID",userID));


        }

    }

