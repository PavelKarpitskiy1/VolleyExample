package com.sibext.volleyexample;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

public class MainActivity extends Activity {
    private int direction = 1;
    private ViewGroup resultView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultView = (ViewGroup) findViewById(R.id.result);
        VolleyHelper.init(this);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setClickable(false);
                RequestQueue queue = VolleyHelper.getRequestQueue();
                JSONObject object = new JSONObject();
                try {
                    object.put("direction", direction);
                    JsonObjectRequest volleyRequest = new JsonObjectRequest(Method.POST,
                            "http://volley.sibext.com/",
                            object,
                            createMyReqSuccessListener(),
                            createMyReqErrorListener());

                    queue.add(volleyRequest);
                    TextView textView = new TextView(MainActivity.this);
                    textView.setText("-> " + direction + "");
                    resultView.addView(textView);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private Response.Listener<JSONObject> createMyReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String result = response.getString("result");
                    if (!result.equals("goal")) {
                        if (direction == 1) {
                            direction = 0;
                        } else {
                            direction = 1;
                        }
                    }
                    TextView textView = ((TextView) resultView.getChildAt(resultView.getChildCount() - 1));
                    textView.setText(textView.getText() + " (" + result + ")");
                    button.setClickable(true);
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "Parse error", Toast.LENGTH_LONG).show();
                }
            }
        };
    }


    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error = " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
    }

}
