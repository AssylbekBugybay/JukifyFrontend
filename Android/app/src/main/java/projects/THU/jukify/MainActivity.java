package projects.THU.jukify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Class for MainActivity
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Editor for Preference-based storage
     */
    private SharedPreferences.Editor editor;
    /**
     * Preference-based storage
     */
    private SharedPreferences prefs;
    /**
     * User ID String
     */
    private String userId;
    /**
     * QR Code scanner
     */
    QrScanner qrClass;
    /**
     * QR Code button
     */
    FloatingActionButton qrButton;
    /**
     * Navigation view
     */
    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.cooLayout);
        setSupportActionBar(toolbar);

        //Bottom Navigation bar
        qrButton = findViewById(R.id.button_useQRCodeMain);
        navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.getMenu().findItem(R.id.action_home).setEnabled(false);
        navigationView.setSelectedItemId(R.id.action_home);
        navigationView.setBackground(null);
        navigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.action_home:
                    Intent intent_home = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent_home);
                    break;
                case R.id.action_party:
                    Intent intent_party = new Intent(MainActivity.this, PartyCreationActivity.class);
                    startActivity(intent_party);
                    break;
                case R.id.action_settings:
                    Intent intent_settings = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent_settings);
                    break;
                case R.id.action_share:
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "This is the link for our Jukify app!");
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    startActivity(shareIntent);
                    break;
            }

            return false;
        });
        qrClass = new QrScanner(qrButton, this);
        prefs = this.getSharedPreferences("userpreference", Context.MODE_PRIVATE);
        editor = prefs.edit();
        createUserId();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        prefs  = getSharedPreferences("userpreference", Context.MODE_PRIVATE);
        //Toast.makeText(this, "Here is the userId: " + userId, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.putString("userId",userId);
        editor.commit();
    }

    ////////////////////////////////////////
    //      Activity launcher area       //
    //////////////////////////////////////

    /**
     * Launches PlaylistsActivity
     * @param view Current view
     */
    public void launchPaylistsActivity(View view) {
        Intent intent = new Intent(this, PartyHistoryActivity.class);
        startActivity(intent);
    }

    /**
     * Launches PartyCreation Activity
     * @param view Current view
     */
    public void launchPartyCreationActivity(View view) {
        Intent intent = new Intent(this, PartyCreationActivity.class);
        this.startActivity(intent);
    }
    /**
     * Launches JoinParty Activity
     * @param view Current view
     */
    public void launchJoinPartyActivity(View view) {
        Intent intent = new Intent(this, PartyJoinActivity.class);
        startActivity(intent);
    }
    /**
     * Launches Settings Activity
     * @param view Current view
     */
    public void launchSettingsActivity(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Creates user ID and stores it in preferences
     */
    public void createUserId() {
        userId = prefs.getString("userId", userId);
        if (userId == null) {
            System.out.println("UserId was null.");
            userId = IDgenerator.getBase62(5);
            editor.putString("userId", userId);
            editor.commit();
        }
        else {
            System.out.println("UserId was NOT null.");
        }
    }

    /**
     * Gets result of QR code scan
     * @param requestCode Activity request code
     * @param resultCode Activity result code
     * @param data data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.e("Scan*******", "Cancelled scan!");
            } else {
                Toast.makeText(this, "Scan Done!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, PartyJoinActivity.class);
                intent.putExtra("partyIdPassed", result.getContents());
                startActivity(intent);
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}


