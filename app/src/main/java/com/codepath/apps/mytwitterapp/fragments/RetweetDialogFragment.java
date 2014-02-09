package com.codepath.apps.mytwitterapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.codepath.apps.mytwitterapp.ComposeTweetActivity;
import com.codepath.apps.mytwitterapp.R;
import com.codepath.apps.mytwitterapp.models.Tweet;

/**
 * Created by hectormonserrate on 08/02/14.
 */
public class RetweetDialogFragment extends DialogFragment {
    protected String TAG = "retweetdialogfragment";
    private final static String EXTRA_TWEET = "tweet";

    public static final int RETWEET_CODE = 20;

    private Tweet tweet;

    private FrameLayout flRetweetCancel;
    private FrameLayout flRetweetQuote;
    private FrameLayout flRetweetDo;

    public RetweetDialogFragment() {}

    public static RetweetDialogFragment newInstance(Tweet tweet) {
        RetweetDialogFragment frag = new RetweetDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_TWEET, tweet);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tweet = getArguments().getParcelable(EXTRA_TWEET);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_retweet, parent, false);
        getDialog().setTitle(getResources().getString(R.string.retweet_dialog_title));

        flRetweetCancel = (FrameLayout) view.findViewById(R.id.flRetweetCancel);
        flRetweetQuote = (FrameLayout) view.findViewById(R.id.flRetweetQuote);
        flRetweetDo = (FrameLayout) view.findViewById(R.id.flRetweetDo);

        flRetweetCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        flRetweetQuote.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(getActivity(), ComposeTweetActivity.class);
                i.putExtra(ComposeTweetActivity.REPLY_TWEET, tweet);
                i.putExtra(ComposeTweetActivity.QUOTE, true);
                startActivityForResult(i, TweetsListFragments.REQUEST_CODE);
            }
        });

        flRetweetDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                getTargetFragment().onActivityResult(RETWEET_CODE, Activity.RESULT_OK, i);
                dismiss();
            }
        });



        return view;

    }


}
