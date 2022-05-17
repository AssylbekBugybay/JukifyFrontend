package projects.THU.jukify;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.lang.IllegalArgumentException;

//Test client for accessing SpotifyAPI and getting featured playlist.

public class SpotifyAPITestClient implements Runnable, HTTPClientInterface{


    private static SpotifyAPITestClient instance = null;
    public static JSONObject response = new JSONObject();
    private Context ctx;
    private RequestQueue requestQueue = null;
    private int requestType;
    final private String backendAddress = "http://10.0.2.2:5000";
    //final private String backendAddress = "https://api.spotify.com/v1/browse";

    private String path;
    private HashMap<String, String> body;
    private Handler handler;

    private SpotifyAPITestClient(){
    }
    private SpotifyAPITestClient(Context c, int rt, String p, HashMap<String, String> b, Handler h) {
        this.ctx = c;
        this.requestType = rt;
        this.path = this.backendAddress + p;
        this.body = b;
        this.handler = h;
        this.requestQueue = getRequestQueue();
    }

    public void setRequest(@Nullable Integer rt, @Nullable String p, @Nullable HashMap<String, String> b, @Nullable Handler h){
        if(rt != null)
            this.requestType = rt.intValue();
        if(p != null)
            this.path = this.backendAddress + p;
        if(b != null)
            this.body = b;
        if(h != null)
            this.handler = h;
    }

    public JSONObject getResponse(){
        return response;
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) { requestQueue = Volley.newRequestQueue(ctx.getApplicationContext()); } // getApplicationContext() is key, it keeps you from leaking the Activity or BroadcastReceiver if someone passes one in.
        return requestQueue;
    }

    public static synchronized SpotifyAPITestClient getInstance(Context c, int rt, String p, HashMap<String, String> b, Handler h) {
        if (instance == null)
            instance = new SpotifyAPITestClient(c, rt, p, b, h);
        else
            instance.setRequest(new Integer(rt),p,b,h);
        return instance;
    }

    public static synchronized SpotifyAPITestClient getInstance(){
        if(instance == null)
            instance = new SpotifyAPITestClient();
        return instance;
    }
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }


    public void run(){
        System.out.println("Hello from runnable!");
        sendHTTP();
    }
    public void sendHTTP() {
        // basic check for proper method
        if(this.requestType != Request.Method.GET && this.requestType != Request.Method.POST){
            throw new IllegalArgumentException("Argument out of range!");
        }
        this.response = null; // reset response, indicating that something went wrong
        // variables
        JSONObject jsonobj = new JSONObject();

        // fill request parameters with hashmap values
        this.body.forEach((k, v) -> {
            try {
                jsonobj.put(k, v);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(ctx, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        // create request
        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(requestType, this.path, jsonobj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject r) {
                        response = r;
                        Message msg = new Message();
                        msg.obj = r;
                        handler.sendMessage(msg);
                        return;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        Toast.makeText(ctx, e.toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {

                SharedPreferences prefs =ctx.getApplicationContext().getSharedPreferences("userpreference", Context.MODE_PRIVATE);
                Log.i("getHeaders",prefs.getString("token","no token"));
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");

                /* Authorization does not work with current implemntation.
                   It needs to periodically fetch new token from https://developer.spotify.com/console
                   for making valid HTTP requests

                 */

                String auth = "Bearer " + prefs.getString("token","no token");
                headers.put("Authorization","Bearer BQDA9eKrU-1Zz2z9fLb5LZI68YHaoUdXeUSDfXN_Y5Q7qR3e34rosE88OuAskO46NSEWrz1wKNe4sIoiTm7OvwID61vqHS4PamGHqLXam_8X4Givp2JZEpsozLbAR5AiqHE__POB2RA8-M_Q6t7X7z5Y3GdxvoMlo93gt8Zt5qLpHpW8NrEPgNpDSS71uOfxa6NcJDnX_AfkJPDXdGef4W2oXthRXdFkIE_DD3PGbZHhq7Gc");
                return headers;
            }
        };
        // add request to queue
        addToRequestQueue(jsonObjRequest);
    }


}
