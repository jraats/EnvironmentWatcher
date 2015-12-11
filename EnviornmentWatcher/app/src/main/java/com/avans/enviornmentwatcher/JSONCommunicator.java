package com.avans.enviornmentwatcher;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Raoul-Laptop on 8-12-2015.
 */
public class JSONCommunicator {

    private static JSONCommunicator JSONCommunicator = null;
    private static Context context;
    private RequestQueue mRequestQueue;
    private String answer;

    //TODO: Get this to fucking work
    /*
    private String MyPREFERENCES = "myPrefs";
    private SharedPreferences sharedpreferences;*/
    private String url = "http://145.48.205.196:8080/api/";


    private JSONCommunicator(Context context) {
        this.context = context;
    }

    // returns the class and makes it if it isn't created yet
    public static synchronized JSONCommunicator getInstance(Context context) {
        if(null == JSONCommunicator) {
            JSONCommunicator = new JSONCommunicator(context);
        }
        return JSONCommunicator;
    }

    //this is so you don't need to pass context each time
    public static synchronized JSONCommunicator getInstance()
    {
        if (null == JSONCommunicator)
        {
            throw new IllegalStateException(JSONCommunicator.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return JSONCommunicator;
    }

    //send login data to server
    public String Login(String username, String password, final CommunicationInterface<String> listener)
    {
        answer = "";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("password", password);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url+"login", new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject json_data = null;
                answer = response.toString();
                listener.Login(answer.toString());
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR! "+error.toString());
                listener.Login("");
            }
        });
        System.out.println(url+"login");
        // Add the request to the RequestQueue.
        this.getRequestQueue().add(jsObjRequest);
        return answer;
    }


    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }
}


