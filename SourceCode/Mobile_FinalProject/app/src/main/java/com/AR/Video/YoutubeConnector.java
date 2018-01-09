package com.AR.Video;

import android.content.Context;
import android.util.Log;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter Hoai on 9/12/2016.
 */
public class YoutubeConnector {
    private YouTube youtube;
    private YouTube.Search.List query;

    public static final String KEY = "AIzaSyDL2ZqeB3uGYmqv8jJgITwS4N9eFyBz4IA";

    public YoutubeConnector(Context context) {
        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest httpRequest) throws IOException {
            }
        }).setApplicationName(context.getString(R.string.app_name)).build();

        try {
            query = youtube.search().list("id,snippet");
            query.setKey(KEY);
            query.setType("video");
            query.setFields("items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url,snippet/publishedAt)");
            query.setMaxResults(20L);
        } catch (IOException e) {
            Log.d("YC", "Could not initialize: " + e.getMessage());
        }
    }

    public List<VideoItem> search(String keywords, int limit) {
        query.setQ(keywords);
        try {
            SearchListResponse response = query.execute();
            List<SearchResult> results = response.getItems();
            List<VideoItem> items = new ArrayList<VideoItem>();
            int i=0;

            for (SearchResult result : results) {
                if(i<limit) {
                    VideoItem item = new VideoItem();
                    item.setTitle(result.getSnippet().getTitle());
                    item.setDescription(result.getSnippet().getDescription());
                    item.setThumbnailURL(result.getSnippet().getThumbnails().getDefault().getUrl());
                    item.setId(result.getId().getVideoId());
                    items.add(item);
                    i= i+1;
                }
            }

            return items;
        } catch (IOException e) {
            Log.d("YC", "Could not search: " + e);
            return null;
        }
    }
}
