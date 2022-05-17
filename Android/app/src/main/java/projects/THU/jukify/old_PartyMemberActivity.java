package projects.THU.jukify;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class old_PartyMemberActivity extends AppCompatActivity {

TextView partyNametxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.old_activity_party_member);

        partyNametxt = findViewById(R.id.masterPartyName);

        Intent intent = getIntent();
        String partyname = intent.getStringExtra("partyName");
        partyNametxt.setText(partyname);
    }

    /*
        Activity launcher area
     */
    public void launchAddSongActivity(View view) {
        Intent intent = new Intent(this, AddSongActivity.class);
        startActivity(intent);
    }



}