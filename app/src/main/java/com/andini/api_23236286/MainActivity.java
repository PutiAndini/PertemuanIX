package com.andini.api_23236286;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    public ListView listUsers;
    private ProgressBar progressBar;
    private UserAdapter adapter;
    public ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("List of USERS");
        }
        progressBar = findViewById(R.id.progressBar);
        listUsers = findViewById(R.id.lv_list);
        adapter = new UserAdapter(this);
        listUsers.setAdapter(adapter);
        users = new ArrayList<>();

        getListUsers();
        listUsers.setOnItemClickListener((adapterView, view, i, l) -> {
            Toast.makeText(MainActivity.this, users.get(i).getName(), Toast.LENGTH_SHORT).show();
            Log.d("Lihat", users.get(i).getName());
        });

    }

    private void getListUsers() {
        progressBar.setVisibility(View.VISIBLE);
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.github.com/users";
        client.addHeader("Authorization", "token gph_4XEPjSEaV4sGG1u1T0oxy48EkFucEw1Xqwdg");
        client.addHeader("User-Agent", "request");

        // Declare localUsers as final
        final ArrayList<User> localUsers = new ArrayList<>();

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressBar.setVisibility(View.INVISIBLE);
                ArrayList<User> listUser = new ArrayList<>();
                String result = new String(responseBody);
                Log.d(TAG, result);
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String name = jsonObject.getString("login");
                        String type = jsonObject.getString("type");
                        String photo = jsonObject.getString("avatar_url");

                        User user = new User();
                        user.setName(name);
                        user.setType(type);
                        user.setPhoto(photo);
                        listUser.add(user);
                    }
                    localUsers.clear(); // Clear the existing data
                    localUsers.addAll(listUser); // Add new data
                    adapter.setUser(localUsers);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressBar.setVisibility(View.INVISIBLE);
                String errorMassage;
                switch (statusCode) {
                    case 401:
                        errorMassage = statusCode + ": Bad Request";
                        break;
                    case 403:
                        errorMassage = statusCode + ": Forbidden";
                        break;
                    case 404:
                        errorMassage = statusCode + ": Not Found";
                        break;
                    default:
                        errorMassage = statusCode + ":" + error.getMessage();
                        break;
                }
                Toast.makeText(MainActivity.this, errorMassage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
