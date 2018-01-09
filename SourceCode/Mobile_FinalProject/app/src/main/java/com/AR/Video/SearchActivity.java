package com.AR.Video;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchActivity extends Activity {

    private EditText searchInput;
    private EditText searchLimit;
    private ListView videosFound;
    private ImageButton btnsearch;
    private Handler handler;

    private List<VideoItem> searchResults;

    String search_input;
    String video_result_limit_string ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchInput = (EditText) findViewById(R.id.search_input);
        searchLimit = (EditText) findViewById(R.id.search_limit);
        btnsearch = (ImageButton) findViewById(R.id.btn_search);
        videosFound = (ListView) findViewById(R.id.videos_found);

        handler = new Handler();

        Toast.makeText(SearchActivity.this, "Press Done when finish input!", Toast.LENGTH_SHORT).show();

        // Get the input string
        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE){
                    search_input = v.getText().toString() + ", trailer";
                    return false;
                }
                return true;
            }
        });

        // Set search limit up to 20 videos
        searchLimit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE){
                    if(!v.getText().toString().equals("") ) {
                        if (Integer.valueOf(v.getText().toString()) > 20 || Integer.valueOf(v.getText().toString()) < 1) {
                            Toast.makeText(SearchActivity.this, "The limit is only from 1 -> 20", Toast.LENGTH_SHORT).show();
                            v.setText("");
                        } else
                            video_result_limit_string = v.getText().toString();
                        return false;
                    }
                }
                return true;
            }
        });

        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (search_input == null)
                    Toast.makeText(SearchActivity.this, "Please enter trailer name!", Toast.LENGTH_SHORT).show();
                else if (video_result_limit_string == null)
                    Toast.makeText(SearchActivity.this, "Please enter the limit!", Toast.LENGTH_SHORT).show();
                else {
                    searchOnYoutube(search_input, Integer.valueOf(video_result_limit_string));
                }
            }
        });

        addClickListener();
        addLongClickListener();
    }

    private void searchOnYoutube(final String keywords, final int limit) {
        new Thread(){
            @Override
            public void run() {
                YoutubeConnector yc = new YoutubeConnector(SearchActivity.this);
                searchResults = yc.search(keywords, limit);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateVideosFound();
                    }
                });
            }
        }.start();
    }

    private void updateVideosFound() {
        ArrayAdapter<VideoItem> adapter = new ArrayAdapter<VideoItem>(getApplicationContext(), R.layout.video_item, searchResults) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.video_item, parent, false);
                }

                ImageView thumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);
                TextView title = (TextView) convertView.findViewById(R.id.video_title);
                TextView description = (TextView) convertView.findViewById(R.id.video_description);
                TextView url = (TextView) convertView.findViewById(R.id.video_URL);
                TextView puDate = (TextView) convertView.findViewById(R.id.video_puDate);

                VideoItem searchResult = searchResults.get(position);

                Picasso.with(getApplicationContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
                url.setText("Link: https://www.youtube.com/watch?v=" + searchResult.getId().toString());
                title.setText("Title: " + searchResult.getTitle());
                description.setText("Description: " + searchResult.getDescription());
                //puDate.setText("Published Date: " + searchResult.);

                return convertView;
            }
        };
        videosFound.setAdapter(adapter);
    }

    private void addClickListener() {
        videosFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplication(), PlayerActivity.class);

                /////Video link///////
                String videolink = "https://www.youtube.com/watch?v=" + searchResults.get(position).getId();
                //////////////////////////////

                intent.putExtra("VIDEO_LINK", videolink);
                intent.putExtra("VIDEO_ID", searchResults.get(position).getId());
                startActivity(intent);
            }
        });
    }

    private void addLongClickListener(){
        videosFound.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                String link_share = "https://www.youtube.com/watch?v=" + searchResults.get(position).getId();
                share.putExtra(Intent.EXTRA_SUBJECT, "Video title");
                share.putExtra(Intent.EXTRA_TEXT, link_share);
                startActivity(Intent.createChooser(share, "Share link!"));
                return true;
            }
        });
    }
}
