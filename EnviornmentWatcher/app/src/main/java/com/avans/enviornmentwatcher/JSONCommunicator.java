package com.avans.enviornmentwatcher;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.ErrorListener;

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
    private String url = "http://145.48.205.34:8080/api/";


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
    public void login(String username, String password, final CommunicationInterface<String> listener)
    {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("password", password);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url+"login", new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String answer = response.getString("token");
                    System.out.println(response.toString());
                    listener.getResponse(answer);
                }catch (JSONException error)
                {
                    System.out.println("ERROR! "+error.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR! "+error.toString());
                listener.getResponse("");
            }
        });
        System.out.println(url+"login");
        // Add the request to the RequestQueue.
        this.getRequestQueue().add(jsObjRequest);
    }


    //check if the user already has a product reserved
    public void getProductIDUser(final User user, final CommunicationInterface<String> listener)
    {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url+"user/"+user.getUsername(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                    try{
                        System.out.println(response.toString());
                        JSONObject object = response.getJSONArray("user").getJSONObject(0);
                        System.out.println(object.toString());

                        if(object.getInt("productId") != -1)
                        {
                            listener.getResponse("0");
                        }
                    }catch (Exception error)
                    {

                    }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR! "+error.toString());
                listener.getResponse("3");
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                System.out.println(user.getApiKey());
                headers.put("X-Access-Token", user.getApiKey());
                return headers;
            }
        };
        System.out.println(url+"user/"+user.getUsername());
        // Add the request to the RequestQueue.
        this.getRequestQueue().add(jsObjRequest);
    }

    //Retrieving all sensors from database
    public void getAllProducts(final User user, final CommunicationInterface<ArrayList<Product>> listener)
    {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url+"product", new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response){
                JSONArray jsonArray = null;
                ArrayList<Product> products = new ArrayList<Product>();
                try {
                    jsonArray = response.getJSONArray("products");
                }catch (Exception e) {
                    System.out.println("Error!" + e.toString());
                }


                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject object = response.getJSONArray("products").getJSONObject(i);
                        Product product = new Product();
                        product.setId(object.getInt("id"));
                        product.setRoom(object.getString("roomName"));
                        product.setLocation(object.getString("location"));
                        products.add(product);

                    } catch (JSONException error) {
                        System.out.println("ERROR! " + error.toString());
                    }
                }
                listener.getResponse(products);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR! " + error.toString());
                //listener.getResponse(new Product());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                System.out.println(user.getApiKey());
                headers.put("X-Access-Token", user.getApiKey());
                return headers;
            }
        };
        System.out.println(url+"product");
        // Add the request to the RequestQueue.
        this.getRequestQueue().add(jsObjRequest);
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


