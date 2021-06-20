package com.example.padstartscherm;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to make a request to the online API
 *
 * @author Sagi
 */
public class Request {

    /**
     * Method to make a request to the API
     *
     * @param data     is the ArrayList of the passed in data
     * @param URL      is the POST URL of the API
     * @param activity The current activity.
     * @param userId   This is a variable that you can pass to the next activity
     * @param melding  notify the user when the request is susccesfull.
     * @return
     */
    public static void makeRequest(final Map<String, String> data, String URL, final Activity activity, final String userId, final String melding) {

        final int SUCCESVOL_REQUEST = 200;

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject object = new JSONObject();

                // Try to convert returned data to a JSON object
                try {
                    object = new JSONObject(response);
                } catch (JSONException e) {
                    Toast.makeText(activity.getApplication(), "Er is een fout opgetreden", Toast.LENGTH_LONG).show();
                }

                // Try to see if request was done successfully
                try {
                    // Check if request was successful
                    if (Integer.valueOf(object.get("code").toString()) == SUCCESVOL_REQUEST) {
                        // Check if MainActivity triggered this request
                        if (activity instanceof HomeActivity) {
                            // Create an intent to start the next activity
                            Intent intent = new Intent(activity, RequestActivity.class);
                            // Put what data in the intent to make it accessible for the next activity
                            intent.putExtra("license_plate", userId.toUpperCase());
                            // Reset current activity's text fields as we don't need them anymore
                            ((HomeActivity) activity).usr.setText("");
                            ((HomeActivity) activity).pas.setText("");

                            activity.startActivity(intent); // Start next activity
                        } // Check if Main2Activity triggered this request
                        else if (activity instanceof RequestActivity) {
                            // Reset current activity's text fields to prepare them for the next report
                            ((RequestActivity) activity).aantalContainers.setText("0");
                            ((RequestActivity) activity).aantalKlikos.setText("0");
                            ((RequestActivity) activity).aantalzakken.setText("0");
                        }

                        // Let the user know the request was done successfully
                        Toast.makeText(activity.getApplication(), melding, Toast.LENGTH_LONG).show();
                    } else {
                        // Let the user know the reason why the request has failed (The API gives a detailed reason)
                        Toast.makeText(activity.getApplication(), object.get("message").toString(), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Let the user know the request couldn't be done successfully
                Toast.makeText(activity.getApplication(), "FOUT: Het verzoek is niet succesvol gegaan", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                // Go through each given data from the activities to put them
                // together in the request's params
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    params.put(entry.getKey(), entry.getValue());
                }

                // Also put API key inside the params as the API expects you to give a valid API key
                params.put("api_key", "f2f49a-890394-e8634d-b265a8-85bc65");

                return params;
            }
        };

        // Put the request in a queue to let it get launched later on
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(stringRequest);
    }
}

