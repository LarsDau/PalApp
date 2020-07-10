package com.example.palapp;

import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AddContactFragment extends Fragment {

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String user;
    private String password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_contact, container, false);
    }

    public void addContactClickedFragment(View view){
        TextView contactName = getActivity().findViewById(R.id.contactNameFragment);
        String name = contactName.getText().toString();
        System.out.println(name);

        final HashMap<String, String> params = new HashMap<>();
        params.put("Username", user);
        params.put("Password", password);
        params.put("Friend", name);

        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        JsonObjectRequest postRequest = new JsonObjectRequest(PalaverLinks.addContact,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("MsgType").equals("1")){
                                Toast toast = Toast.makeText(getActivity().getApplicationContext(), params.get("Friend") + " is now your friend", Toast.LENGTH_SHORT);
                                toast.show();
                            }else{
                                if(response.getString("Info").equals("Freund bereits auf der Liste")){
                                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Contact is already your friend", Toast.LENGTH_SHORT);
                                    toast.show();
                                }else{
                                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Contact does not exist", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_SHORT);
                        toast.show();
                        System.out.println("Error: " + error);
                    }
                }
        );
        queue.add(postRequest);
    }
}
