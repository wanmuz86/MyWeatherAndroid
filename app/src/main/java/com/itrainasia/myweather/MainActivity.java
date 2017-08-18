package com.itrainasia.myweather;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RequestQueue queue = Volley.newRequestQueue(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        final CustomAdapter adapter =  new CustomAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String url ="http://api.openweathermap.org/data/2.5/forecast?q=Kuala%20Lumpur,my&appid=8131be7e3e6b2014b3af931e011bd730";

// Request a string response from the provided URL.
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the first 500 characters of the response string.
                        try {
                            JSONArray objectArray = response.getJSONArray("list");
                            for (int i = 0; i< objectArray.length(); i++){
                                adapter.addWeather(objectArray.getJSONObject(i));
                            }

                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("debug","That didn't work!");
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    public static class CustomViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView weatherTextView;
        public TextView dateTextView;
        public TextView tempTextView;

        public CustomViewHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.custom_row, parent, false));

            imageView = itemView.findViewById(R.id.icon);
            weatherTextView = itemView.findViewById(R.id.textView1);
            dateTextView = itemView.findViewById(R.id.textView2);
            tempTextView = itemView.findViewById(R.id.textView3);

        }
    }

    public static class CustomAdapter extends RecyclerView.Adapter<CustomViewHolder>{

        List<JSONObject> myArray = new ArrayList<>();
        Context context;

        public CustomAdapter (Context context) {
            this.context = context;

        }


        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CustomViewHolder(LayoutInflater.from(parent.getContext()),parent);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {
            try {
                holder.dateTextView.setText(myArray.get(position).getString("dt_txt"));
                Double temperature = Double.parseDouble(myArray.get(position).getJSONObject("main").getString("temp")) -273;
                holder.tempTextView.setText(String.format("%.2f C",temperature));
                holder.weatherTextView.setText(myArray.get(position).getJSONArray("weather").getJSONObject(0).getString("main"));
                String iconID=  myArray.get(position).getJSONArray("weather").getJSONObject(0).getString("icon");
                String url = "https://openweathermap.org/img/w/" +iconID+".png";
                Picasso.with(context).load(url).into(holder.imageView);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return myArray.size();
        }

        public void addWeather(JSONObject weather){
            myArray.add(weather);
        }
    }
}
