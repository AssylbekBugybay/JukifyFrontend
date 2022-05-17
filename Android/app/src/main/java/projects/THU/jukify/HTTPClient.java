package projects.THU.jukify;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
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

/**
 * Class responsible for HTTP Volley connection
 */
public class HTTPClient implements Runnable, HTTPClientInterface{
    /**
     * Singleton static instance
     */
    private static HTTPClient instance = null;

    /**
     * JSON response from HTTP Request
     */
    public static JSONObject response = new JSONObject();

    /**
     * Context for toast
     */
    private Context ctx;

    /**
     * Request queue used by volley
     */
    private RequestQueue requestQueue = null;

    /**
     * Type of the request
     */
    private int requestType;

    /**
     * Address of the backend
     */
    //final private String backendAddress = "http://10.0.2.2:5000";
    final private String backendAddress = "http://87.106.171.240:5025";

    /**
     * Path of the request
     */
    private String path;

    /**
     * Hashmap containing data for body
     */
    private HashMap<String, String> body;

    /**
     * Handler that handles the response
     */
    private Handler handler;

    /**
     * Private default constructor
     */
    private HTTPClient(){
    }

    /**
     * Private constructor with parameters
     * @param c Context of the activity
     * @param rt Request type (Request.Method.GET or Request.Method.POST)
     * @param p Path
     * @param b Hashmap of parameters to be passed as json
     * @param h Handler that handles response
     */
    private HTTPClient(Context c, int rt, String p, HashMap<String, String> b, Handler h) {
        this.ctx = c;
        this.requestType = rt;
        this.path = this.backendAddress + p;
        this.body = b;
        this.handler = h;
        this.requestQueue = getRequestQueue();
    }

    /**
     * Sets request
     * @param rt Request type (Request.Method.GET or Request.Method.POST)
     * @param p Path
     * @param b Hashmap of parameters to be passed as json
     * @param h Handler that handles response
     */
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

    /**
     * Returns response
     * @return response
     */
    public JSONObject getResponse(){
        return response;
    }

    /**
     * Gets request queue
     * @return Request queue
     */
    private RequestQueue getRequestQueue() {
        if (requestQueue == null) { requestQueue = Volley.newRequestQueue(ctx.getApplicationContext()); } // getApplicationContext() is key, it keeps you from leaking the Activity or BroadcastReceiver if someone passes one in.
        return requestQueue;
    }

    /**
     * Gets the instance of HTTPClient singleton with new parameters
     * @param c Context
     * @param rt Request type (Request.Method.GET or Request.Method.POST)
     * @param p Path
     * @param b Hashmap of parameters to be passed as json
     * @param h Handler that handles response
     * @return Instance of the HTTPClient
     */
    public static synchronized HTTPClient getInstance(Context c, int rt, String p, HashMap<String, String> b, Handler h) {
        if (instance == null)
            instance = new HTTPClient(c, rt, p, b, h);
        else
            instance.setRequest(new Integer(rt),p,b,h);
        return instance;
    }

    /**
     * Gets the instance of HTTPClient singleton
     * @return Instance of the HTTPClient
     */
    public static synchronized HTTPClient getInstance(){
        if(instance == null)
            instance = new HTTPClient();
        return instance;
    }

    /**
     * Adds to request queue for Volley
     * @param req Request
     * @param <T> Type of request
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    /**
     * Runs the HTTP Request as thread (required by implementing Runnable)
     */
    public void run(){
        sendHTTP();
    }

    /**
     * Sends HTTP Request
     */
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
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        // add request to queue
        addToRequestQueue(jsonObjRequest);
    }
}
