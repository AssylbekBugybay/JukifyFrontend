package projects.THU.jukify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Class for Settings activity
 */
public class SettingsActivity extends AppCompatActivity {

    QrScanner qrClass;
    FloatingActionButton qrButton;
    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Bottom Navigation bar
        qrButton = findViewById(R.id.button_useQRCodeSettings);
        navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.getMenu().findItem(R.id.action_settings).setEnabled(false);
        navigationView.setSelectedItemId(R.id.action_settings);
        navigationView.setBackground(null);
        navigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.action_home:
                    Intent intent_home = new Intent(SettingsActivity.this, MainActivity.class);
                    startActivity(intent_home);
                    break;
                case R.id.action_party:
                    Intent intent_party = new Intent(SettingsActivity.this, PartyCreationActivity.class);
                    startActivity(intent_party);
                    break;
                case R.id.action_settings:
                    Intent intent_settings = new Intent(SettingsActivity.this, SettingsActivity.class);
                    startActivity(intent_settings);
                    break;
                case R.id.action_share:
                    break;
            }

            return false;
        });
        qrClass = new QrScanner(qrButton, this);
    }

    //Get the results of the qr scan
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.e("Scan*******", "Cancelled scan!");

            } else {
                Toast.makeText(this, "Scan Done!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SettingsActivity.this, PartyJoinActivity.class);
                intent.putExtra("partyIdPassed", result.getContents());
                startActivity(intent);
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}