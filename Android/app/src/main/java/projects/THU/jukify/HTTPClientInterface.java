package projects.THU.jukify;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Interface for HTTP Client
 */
interface HTTPClientInterface {
    /**
     * Sets request
     * @param rt Request type (Request.Method.GET or Request.Method.POST)
     * @param p Path
     * @param b Hashmap of parameters to be passed as json
     * @param h Handler that handles response
     */
    public void setRequest(@Nullable Integer rt, @Nullable String p, @Nullable HashMap<String, String> b, @Nullable Handler h);
    /**
     * Returns response
     * @return response
     */
    public JSONObject getResponse();
    /**
     * Gets the instance of HTTPClient singleton with new parameters
     * @param c Context
     * @param rt Request type (Request.Method.GET or Request.Method.POST)
     * @param p Path
     * @param b Hashmap of parameters to be passed as json
     * @param h Handler that handles response
     * @return Instance of the HTTPClient
     */
    public static HTTPClient getInstance(Context c, int rt, String p, HashMap<String, String> b, Handler h){return null;};
    /**
     * Gets the instance of HTTPClient singleton
     * @return Instance of the HTTPClient
     */
    public static HTTPClient getInstance(){ return null; };
    /**
     * Adds to request queue for Volley
     * @param req Request
     * @param <T> Type of request
     */
    public <T> void addToRequestQueue(Request<T> req);
    /**
     * Sends HTTP Request
     */
    public void sendHTTP();
    }
