package com.codepath.apps.mytwitterapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.mytwitterapp.models.Tweet;
import com.codepath.apps.mytwitterapp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

public class ComposeTweetActivity extends ActionBarActivity {
    private static final String TAG = "ancitivitycomposetweet";
    public static final String CREATED_TWEET = "com.codepath.apps.mytwitterapp.activitycomposetweet.created_tweet";

    public static final String REPLY_TWEET = "reply_tweet";
    private Tweet replyTweet;

    private final int CHARACTER_LIMIT = 140;
    private final String TWEET = "tweet";

    private EditText etTweetContents;
    private TextView tvCharacterCount;
    private TextView tvComposeTweetTitle;
    private Button btnClear;
    private Button btnSend;
    private Button btnCancel;
    private ImageView ivThumb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        replyTweet = getIntent().getParcelableExtra(REPLY_TWEET);

        setContentView(R.layout.activity_compose_tweet);

        etTweetContents = (EditText) findViewById(R.id.etTweetContents);
        tvCharacterCount = (TextView) findViewById(R.id.tvCounter);
        tvComposeTweetTitle = (TextView) findViewById(R.id.tvComposeTweetTitle);
        btnClear = (Button) findViewById(R.id.btnClear);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        ivThumb = (ImageView) findViewById(R.id.ivThumb);

        setTitle(getResources().getString(R.string.compose_tweet));


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

        if(replyTweet != null){
            etTweetContents.setText("@" + replyTweet.getUser().getName() + " ");
            etTweetContents.setSelection(etTweetContents.getText().length());
        }

        tvCharacterCount.setText(getString(R.string.characters_left, CHARACTER_LIMIT));

        loadThumb();

    }

    public void loadThumb() {
        MyTwitterApp.getRestClient().getMyInfo( new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject json) {
                User u = User.fromJson(json);
                ImageLoader.getInstance().displayImage(u.getProfileImageUrl(), ivThumb);
                tvComposeTweetTitle.setText("@" + u.getScreenName());
            }

            @Override
            public void onFailure(Throwable e, JSONObject error) {
                Log.e(TAG, e.toString());
            }
        });
    }

    private void sendTweet(String text){
        setProgressBarIndeterminateVisibility(true);

        long inReplyStatusId = 0;
        if( replyTweet != null ){
            inReplyStatusId = replyTweet.getTweetId();
        }

        MyTwitterApp.getRestClient().updateStatus(text, inReplyStatusId, new JsonHttpResponseHandler() {
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
            public void onSuccess(JSONObject tweet) {
                Log.d("DEBUG", "Successfully sent!");
                setProgressBarIndeterminateVisibility(false);
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.tweet_posted),
                        Toast.LENGTH_SHORT).show();

                Intent i = new Intent();
                i.putExtra(CREATED_TWEET, Tweet.fromJson(tweet, false));
                setResult(RESULT_OK, i);
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