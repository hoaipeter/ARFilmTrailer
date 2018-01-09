package com.AR.Video;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class TopActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);

        ImageView imageView = (ImageView) findViewById(R.id.imageView6); //top budget
        ImageView imageView2 = (ImageView) findViewById(R.id.imageView7); // top upcoming
        ImageView imageView3 = (ImageView) findViewById(R.id.imageView8); // star war

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopActivity.this,SecondActivity.class);
                intent.putExtra("link","http://www.fandango.com/rss/top10boxoffice.rss");
                startActivity(intent);
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopActivity.this,SecondActivity.class);
                intent.putExtra("link","http://www.fandango.com/rss/comingsoonmovies.rss");
                startActivity(intent);
            }
        });
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopActivity.this,SecondActivity.class);
                intent.putExtra("link","http://www.fandango.com/rss/starwars.rss");
                startActivity(intent);
            }
        });

    }
}
