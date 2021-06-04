package com.example.lybrateassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TrackAdapter.OnTrackListener {
    private EditText mSearchEditText;
    private TextView mSearchButton;
    private ProgressBar mProgressbar;
    private RecyclerView mTrackRecyclerview;
    private List<Track> mTrackList;
    private ImageView mArtworkImage;
    private TextView mTrackNameText, mArtistNameText, mArtistIdText;
    private TextView mPlayButton, mStopButton, mCloseButton;
    private RelativeLayout mDialogBox;
    private LinearLayout mMask;
    private Track mTrack;
    private List<String> mIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize_views();
        initialize_onClicks();
        hideKeyboard();
        mTrackList = new ArrayList<>();
    }

    private void initialize_views() {
        mSearchEditText = findViewById(R.id.searchEditText);
        mSearchButton = findViewById(R.id.searchButton);
        mProgressbar = findViewById(R.id.progressbar);
        mTrackRecyclerview = findViewById(R.id.trackRecyclerview);
        mArtworkImage = findViewById(R.id.artworkImage);
        mTrackNameText = findViewById(R.id.trackNameText);
        mArtistNameText = findViewById(R.id.artistNameText);
        mArtistIdText = findViewById(R.id.artistIdText);
        mPlayButton = findViewById(R.id.playButton);
        mStopButton = findViewById(R.id.stopButton);
        mCloseButton = findViewById(R.id.closeButton);
        mDialogBox = findViewById(R.id.dialogBox);
        mMask = findViewById(R.id.mask);
    }

    private void initialize_onClicks() {
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mSearchEditText.getText().toString();
                if (text.isEmpty()) {
                    // when nothing to search
                    Toast.makeText(MainActivity.this, "Please enter a track to search.", Toast.LENGTH_SHORT).show();
                    mSearchEditText.requestFocus();
                } else {
                    // when there is a value to search
                    hideKeyboard();
                    text = text.replace(" ", "+");
                    String url = "https://itunes.apple.com/search?term=" + text + "&entity=song&attribute=songTerm&limit=50";
                    fetch_json(url);
                }
            }
        });
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio();
            }
        });
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAudio();
            }
        });
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogBox.setVisibility(View.GONE);
                mMask.setVisibility(View.GONE);
                stopAudio();
            }
        });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void stopAudio() {
        Intent intent = new Intent(MainActivity.this, TrackService.class);
        stopService(intent);
        Toast.makeText(getApplicationContext(), "Audio preview has been stopped.", Toast.LENGTH_SHORT).show();
    }

    private void playAudio() {
        Intent intent = new Intent(MainActivity.this, TrackService.class);
        intent.putExtra("url", mTrack.getPreview_url());
        startService(intent);
    }

    private void fetch_json(String url) {
        mProgressbar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgressbar.setVisibility(View.GONE);
                try {
                    mIds = new ArrayList<>();
                    mTrackList.clear();
                    JSONObject obj = new JSONObject(response);
                    JSONArray trackArray = obj.getJSONArray("results");
                    for (int i = 0; i < trackArray.length(); i++) {
                        JSONObject trackObject = trackArray.getJSONObject(i);
                        String id = trackObject.getString("artistId");
                        if (!mIds.contains(id)) {
                            // duplicate id's not considered
                            mIds.add(id);
                            String name = trackObject.getString("artistName");
                            String song = trackObject.getString("trackName");
                            String preview_url = trackObject.getString("previewUrl");
                            String artwork_url = trackObject.getString("artworkUrl100");
                            Track track = new Track(name, id, song, preview_url, artwork_url);
                            mTrackList.add(track);
                        }
                    }
                    initialize_recycler_view();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void initialize_recycler_view() {
        mProgressbar.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mTrackRecyclerview.setLayoutManager(linearLayoutManager);
        TrackAdapter mTrackAdapter = new TrackAdapter(this, mTrackList, this);
        mTrackRecyclerview.setAdapter(mTrackAdapter);
    }

    @Override
    public void onTrackClick(int position) {
        // when a track if clicked
        mDialogBox.setVisibility(View.VISIBLE);
        mMask.setVisibility(View.VISIBLE);
        mTrack = mTrackList.get(position);
        mTrackNameText.setText(mTrack.getTrack_name());
        mArtistNameText.setText(mTrack.getArtist_name());
        mArtistIdText.setText(mTrack.getArtist_id());
        Glide.with(this).load(mTrack.getArtwork_url()).into(mArtworkImage);
    }
}