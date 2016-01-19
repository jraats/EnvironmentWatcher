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
import java.util.Iterator;
import java.util.Map;

public class JSONCommunicator {

    private static JSONCommunicator JSONCommunicator = null;
    private static Context context;
    private RequestQueue mRequestQueue;

    //TODO: Get this to fucking work
    /*
    private String MyPREFERENCES = "myPrefs";
    private SharedPreferences sharedpreferences;*/
    //private String url = "http://85.144.219.90:1337/api/";
    private String url = "http://www.senzingyou.nl:7862/api/";


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
                    " is not initialized, call getInstance(context) first");
        }
        return JSONCommunicator;
    }

    //send login data to server
    public void login(String username, String password, final CommunicationInterface<String> listener)
    {
        HashMap<String, String> params = new HashMap<>();
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


    //returns a object from the server
    public void getObject(final String table, final String item, final String optional,  final CommunicationInterface<HashMap<String, String>> listener)
    {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url+table+optional+"/"+item,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                    try{
                        JSONObject object = response.getJSONArray(table).getJSONObject(0);
                        HashMap<String, String> map = new HashMap<>();

                        Iterator<String> keysItr = object.keys();
                        while(keysItr.hasNext()) {
                            String key = keysItr.next();
                            String value = object.getString(key);

                            map.put(key, value);
                        }

                        listener.getResponse(map);

                    }catch (Exception error)
                    {
                        System.out.println("ERROR! "+error.toString());
                        listener.getResponse(new HashMap<String, String>());
                    }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR! "+error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                System.out.println(DataCommunicator.getInstance().getUser().getApiKey());
                headers.put("X-Access-Token", DataCommunicator.getInstance().getUser().getApiKey());
                return headers;
            }
        };
        System.out.println(url+table+"/"+item);
        // Add the request to the RequestQueue.
        this.getRequestQueue().add(jsObjRequest);
    }

    public void getMultibleData(final String table, final String item, final String optional,
                                final CommunicationInterface<ArrayList<HashMap<String, String>>> listener)
    {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url+table+optional+"/"+item,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject object) {
                        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
                        try {
                            JSONArray jsonArray = object.getJSONArray(table);
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                JSONObject jsonObject = jsonArray.optJSONObject(i);
                                HashMap<String, String> map = new HashMap<>();

                                Iterator<String> keysItr = jsonObject.keys();
                                while(keysItr.hasNext()) {
                                    String key = keysItr.next();
                                    String value = jsonObject.getString(key);

                                    map.put(key, value);
                                }
                                arrayList.add(map);
                            }
                        }catch (Exception e) {
                            System.out.println("Error!" + e.toString());
                        }

                        listener.getResponse(arrayList);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR! "+error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                System.out.println(DataCommunicator.getInstance().getUser().getApiKey());
                headers.put("X-Access-Token", DataCommunicator.getInstance().getUser().getApiKey());
                return headers;
            }
        };
        System.out.println(url+table+"/"+item);
        // Add the request to the RequestQueue.
        this.getRequestQueue().add(jsObjRequest);
    }

    //Retrieving all sensors from database
    public void getAllData(final String table, final CommunicationInterface<ArrayList<HashMap<String, String>>> listener)
    {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url+table, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object){
                ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
                try {
                    JSONArray jsonArray = object.getJSONArray(table);
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        HashMap<String, String> map = new HashMap<>();

                        Iterator<String> keysItr = jsonObject.keys();
                        while(keysItr.hasNext()) {
                            String key = keysItr.next();
                            String value = jsonObject.getString(key);

                            map.put(key, value);
                        }
                        arrayList.add(map);
                    }
                }catch (Exception e) {
                    System.out.println("Error!" + e.toString());
                }

                listener.getResponse(arrayList);
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
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-Access-Token", DataCommunicator.getInstance().getUser().getApiKey());
                return headers;
            }
        };
        // Add the request to the RequestQueue.
        this.getRequestQueue().add(jsObjRequest);
    }

    //Changes data in the database
    public void changeData(final String table, final HashMap<String, String> params, final CommunicationInterface<Integer> listener){
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT, url+table+"/"+DataCommunicator.getInstance().getUser().getUsername(),
                new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                listener.getResponse(0);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR! " + error.toString());
                listener.getResponse(1);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-Access-Token", DataCommunicator.getInstance().getUser().getApiKey());
                return headers;
            }
        };
        System.out.println(url+table);
        // Add the request to the RequestQueue.
        this.getRequestQueue().add(jsObjRequest);
    }

    //returns all sensor data from a date (only 1 dat for now)
    public void getSensorDataWithDate(final String d1,final String d2,final int id, final CommunicationInterface<ArrayList<HashMap<String, String>>> listener){
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url+"sensordata/getDataBytime/"+id+"/"+d1+"/"+d2, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object){
                ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
                try {
                    JSONArray jsonArray = object.getJSONArray("sensorData");
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        HashMap<String, String> map = new HashMap<>();

                        Iterator<String> keysItr = jsonObject.keys();
                        while(keysItr.hasNext()) {
                            String key = keysItr.next();
                            String value = jsonObject.getString(key);

                            map.put(key, value);
                        }
                        arrayList.add(map);
                    }
                }catch (Exception e) {
                    System.out.println("Error!" + e.toString());
                }

                listener.getResponse(arrayList);
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
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-Access-Token", DataCommunicator.getInstance().getUser().getApiKey());
                return headers;
            }
        };
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


