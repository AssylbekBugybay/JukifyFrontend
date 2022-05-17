package projects.THU.jukify;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;


public class SpotifyTokenBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      Log.i("You have new token via Alarm Manager", "New Token BRUV " + intent.getStringExtra("newToken"));
    }
}



