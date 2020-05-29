package com.naman.firebasequizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

public class ResultActivity extends AppCompatActivity {

    private com.facebook.ads.AdView adView;
    TextView tq,ca,wa,hu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        adView = new AdView(this, getResources().getString(R.string.banner), AdSize.BANNER_HEIGHT_90);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();


        tq = findViewById(R.id.tq);
        ca=findViewById(R.id.ca);
        wa=findViewById(R.id.wa);
        hu=findViewById(R.id.hu);
        Intent i=getIntent();
        String questions=i.getStringExtra("total");
        String correct=i.getStringExtra("correct");
        String hint=i.getStringExtra("hint");
        String wrong=i.getStringExtra("wrong");
        tq.setText(questions);
        ca.setText(correct);
        hu.setText(hint);
        wa.setText(wrong);
    }
}
