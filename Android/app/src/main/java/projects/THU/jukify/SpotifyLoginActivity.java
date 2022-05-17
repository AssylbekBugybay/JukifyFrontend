package projects.THU.jukify;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

/**
 * Activity whose sole purpose is running Spotify's SDK Login
 */
public class SpotifyLoginActivity extends AppCompatActivity {
    private Intent pendingIntent;
    private String newToken;
    /**
     * Unique request code
     */
    private static final int REQUEST_CODE = 1337;
    /**
     * Client ID from developers.spotify (authorization of the app)
     */
    private static final String CLIENT_ID = "4150c92f3630402c9691cd083dca3925";
    /**
     * Random URI
     */
    private static final String REDIRECT_URI = "kek://asd";
    /**
     * Scope of access to Spotify API
     */
    private static final String[] ACCESS_SCOPES = {
            "user-read-playback-state",
            "user-read-currently-playing",
            "playlist-read-private",
            "playlist-modify-public",
            "playlist-read-collaborative",
            "user-modify-playback-state",
            "user-library-read",
            "user-read-playback-position",
            "user-read-recently-played",
            "user-top-read",
            "app-remote-control",
            "streaming",
            "user-follow-read"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(CLIENT_ID,
                AuthorizationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(ACCESS_SCOPES);
        AuthorizationRequest request = builder.build();
        AuthorizationClient.openLoginActivity(this,REQUEST_CODE,request);
        pendingIntent = getIntent();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Intent result =new Intent();
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    System.out.println("TOKEN: " + response.getAccessToken());
                    result.putExtra("TOKEN",response.getAccessToken());
                    setResult(Activity.RESULT_OK, result);
                    pendingIntent.putExtra("newToken",response.getAccessToken());
                    finish();//finishing activity
                    break;
                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    result.putExtra("TOKEN","ERR");
                    setResult(Activity.RESULT_CANCELED, result);
                    finish();//finishing activity
                    break;
                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
                    setResult(Activity.RESULT_CANCELED, result);
                    finish();//finishing activity
            }
        }
    }
}
