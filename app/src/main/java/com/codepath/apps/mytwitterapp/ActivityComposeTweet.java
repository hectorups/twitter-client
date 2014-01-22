package com.codepath.apps.mytwitterapp;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

public class ActivityComposeTweet extends Activity {

    private final int CHARACTER_LIMIT = 160;

    private EditText etTweetContents;
    private TextView tvCharacterCount;
    private Button btnClear;
    private Button btnSend;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);

        etTweetContents = (EditText) findViewById(R.id.etTweetContents);
        tvCharacterCount = (TextView) findViewById(R.id.tvCharacterCount);
        btnClear = (Button) findViewById(R.id.btnClear);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etTweetContents.setText("");
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = etTweetContents.getText().toString();
                if(text.trim() == "") return;

                btnSend.setEnabled(false);
                sendTweet(text);
            }
        });

        etTweetContents.addTextChangedListener(mTextEditorWatcher);

    }

    private void sendTweet(String text){
        MyTwitterApp.getRestClient().updateStatus(text, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(Throwable error, String details) {
                super.onFailure(error, details);
                Log.d("DEBUG", "Failed tweet!: " + details);
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.tweet_failed)
                        , Toast.LENGTH_SHORT).show();
                btnSend.setEnabled(false);
            }

            @Override
            public void onSuccess(int int0, String string0) {
                Log.d("DEBUG", "Successfully sent!");
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.tweet_posted),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after){
            tvCharacterCount.setText(getString(R.string.characters_left, 0));
        }

        public void onTextChanged(CharSequence s, int start, int before, int count){}

        public void afterTextChanged(Editable s)
        {
            tvCharacterCount.setText(getString(R.string.characters_left, CHARACTER_LIMIT - s.length()));
        }
    };




}
