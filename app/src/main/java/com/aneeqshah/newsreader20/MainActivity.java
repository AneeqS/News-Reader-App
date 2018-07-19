package com.aneeqshah.newsreader20;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DownloadTask task = new DownloadTask();
    ListView listView;
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> urls = new ArrayList<>();
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.list);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, names);
        listView.setAdapter(arrayAdapter);

        try {
            task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public class DownloadTask extends AsyncTask<String, Void, String> {


        //String... strings. think of it as an array.
        @Override
        protected String doInBackground(String... strings) {

            String result = "";

            URL url;
            //Like a browser
            HttpURLConnection httpURLConnection = null;

            try {

                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();

                //Hold input data
                InputStream in = httpURLConnection.getInputStream();

                //Read input stream data. We do this one character at a time.
                InputStreamReader reader = new InputStreamReader(in);


                int data = reader.read();

                //When data is finished reading all the data from the reader, it will have a value on -1
                while (data != -1) {

                    //The current character being downloaded from the url
                    char curr = (char) data;

                    result += curr;

                    //Make date move to the next character.
                    data = reader.read();

                }

                JSONArray jsonArray = new JSONArray(result);
                int max = 20;

                if(jsonArray.length() < 20){
                    max = jsonArray.length();
                }

                for(int i = 0; i < max; i++){
                    String id = jsonArray.getString(i);

                    url = new URL(" https://hacker-news.firebaseio.com/v0/item/" + id + ".json?print=pretty");
                    httpURLConnection = (HttpURLConnection) url.openConnection();

                    //Hold input data
                   in = httpURLConnection.getInputStream();

                    //Read input stream data. We do this one character at a time.
                    reader = new InputStreamReader(in);


                    data = reader.read();

                    String info = "";

                    while(data != -1){

                        char curr = (char) data;
                        info += curr;
                        data = reader.read();
                    }

                    JSONObject jsonObject = new JSONObject(info);

                    if(!jsonObject.isNull("title") && !jsonObject.isNull("url")) {
                        String title = jsonObject.getString("title");
                        String link = jsonObject.getString("url");

                        names.add(title);
                        urls.add(link);
                    }
                }

                return result;

            } catch (Exception e) {

                e.printStackTrace();
                return "Failed";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            arrayAdapter.notifyDataSetChanged();

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Intent intent = new Intent(getApplicationContext(), webViewActivity.class);
                    intent.putExtra("link", urls.get(i));
                    startActivity(intent);
                }
            });
        }
    }
}
