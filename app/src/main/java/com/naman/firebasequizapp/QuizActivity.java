package com.naman.firebasequizapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

import static java.lang.String.valueOf;

public class QuizActivity extends AppCompatActivity {

    private com.facebook.ads.AdView adView;

    private final String TAG = "reward ad:";

    CountDownTimer timer;
    CardView c1,c2,c3,c4,hintc,next;
    TextView q,t,o1,o2,o3,o4,ca,hin,nt;
    MediaPlayer c,w,to;
    int correct=0;
    int wrong=0;
    int total=0;
    int check=0;
    int hint=5;
    int hu=0;

    ArrayList keys = new ArrayList<String>();
    ArrayList<QuestionsModel> questionArrayList=new ArrayList<>();

    DatabaseReference root= FirebaseDatabase.getInstance().getReference().child("Questions");


    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        AudienceNetworkAds.initialize(this);

        adView = new AdView(this, getResources().getString(R.string.banner), AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        //inter ad
        interstitialAd = new InterstitialAd(this, getResources().getString(R.string.inter));
        // Set listeners for the Interstitial Ad
        //noinspection deprecation
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                interstitialAd.loadAd();
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());

            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                // Show the ad
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");

            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }

        });

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd();

        // Request an ad
        adView.loadAd();

        //sound
        c=MediaPlayer.create(this,R.raw.correct);
        w=MediaPlayer.create(this,R.raw.wrong);
        to=MediaPlayer.create(this,R.raw.to);
        //sound
        c1=findViewById(R.id.c1);
        nt=findViewById(R.id.nt);
        ca=findViewById(R.id.canswer);
        hin=findViewById(R.id.hint);
        c2=findViewById(R.id.c2);
        hintc=findViewById(R.id.hintc);
        c3=findViewById(R.id.c3);
        next=findViewById(R.id.next);
        c4=findViewById(R.id.c4);
        q=findViewById(R.id.question);
        t=findViewById(R.id.timer);
        o1=findViewById(R.id.option1);
        o2=findViewById(R.id.option2);
        o3=findViewById(R.id.option3);
        o4=findViewById(R.id.option4);


        getquestions();


    }

    private void getquestions() {

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                QuestionsModel qsnapshot = dataSnapshot.getValue(QuestionsModel.class);
                assert qsnapshot != null;
                questionArrayList.add(new QuestionsModel(qsnapshot.getQuestion(),qsnapshot.getOption1(),qsnapshot.getOption2(),qsnapshot.getOption3(),qsnapshot.getOption4(),qsnapshot.getAnswer()));
                keys.add(dataSnapshot.getKey());
                Collections.shuffle(questionArrayList);
                updatequestion();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int index = keys.indexOf(dataSnapshot.getKey());
                questionArrayList.remove(index);
                Collections.shuffle(questionArrayList);
                updatequestion();
                keys.remove(index);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    public void updatequestion()
    {
        if(total==0)
        {
//            Collections.shuffle(list);
        }
        next.setVisibility(View.INVISIBLE);
        if(total==19)
        {
            nt.setText("SUBMIT");
        }
        if(total>=20)
        {
            timer.cancel();
            Intent result=new Intent(getApplicationContext(), ResultActivity.class);
            result.putExtra("total", valueOf(total));
            result.putExtra("correct", valueOf(correct));
            result.putExtra("wrong", valueOf(wrong));
            result.putExtra("hint", valueOf(hu));
            startActivity(result);
            finish();
            //result activity
        }
        else
        {
            q.setText(valueOf(questionArrayList.get(total).question));
            o1.setText(valueOf(questionArrayList.get(total).option1));
            o2.setText(valueOf(questionArrayList.get(total).option2));
            o3.setText(valueOf(questionArrayList.get(total).option3));
            o4.setText(questionArrayList.get(total).option4);
            if(total==0) {
                timer(600);
            }//hint
            hintc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hint <= 0) {
                        if (interstitialAd.isAdLoaded()) {
                            interstitialAd.show();
                            //noinspection deprecation
                            interstitialAd.setAdListener(new AbstractAdListener() {
                                @Override
                                public void onInterstitialDismissed(Ad ad) {
                                    hint = hint + 5;
                                    hin.setText(valueOf(hint));
                                    Toast.makeText(getApplicationContext(), "5 Hints Rewarded", Toast.LENGTH_LONG).show();
                                    interstitialAd.loadAd();
                                    super.onInterstitialDismissed(ad);
                                }
                            });
                        } else {
                            Toast.makeText(QuizActivity.this.getApplicationContext(), "ad is not loaded please try again for reward", Toast.LENGTH_LONG).show();
                        }


                    }
                    if (hint > 0) {
                        c.start();
                        hint--;
                        hu++;
                        if (o1.getText().toString().equals(valueOf(questionArrayList.get(total).answer))) {

                            c1.setCardBackgroundColor(Color.GREEN);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    c1.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                                    hin.setText(valueOf(hint));
                                    if (hint <= 0) {
                                        hin.setText("+5 NOW");
                                    }
                                }
                            }, 1000);
                        }
                        if (o2.getText().toString().equals(valueOf(questionArrayList.get(total).answer))) {

                            c2.setCardBackgroundColor(Color.GREEN);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    c2.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                                    hin.setText(valueOf(hint));
                                    if (hint <= 0) {
                                        hin.setText("+5 NOW");
                                    }
                                }
                            }, 1000);

                        }
                        if (o3.getText().toString().equals(valueOf(questionArrayList.get(total).answer))) {


                            c3.setCardBackgroundColor(Color.GREEN);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    c3.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                                    hin.setText(valueOf(hint));
                                    if (hint <= 0) {
                                        hin.setText("+5 NOW");
                                    }
                                }
                            }, 1000);

                        }
                        if (o4.getText().toString().equals(valueOf(questionArrayList.get(total).answer))) {

                            c4.setCardBackgroundColor(Color.GREEN);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    c4.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                                    hin.setText(valueOf(hint));
                                    if (hint <= 0) {

                                        hin.setText("+5 NOW");
                                    }
                                }
                            }, 1000);

                        }
                    }
                }
            });
            //hint
            o1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (check == 0) {
                        c.start();
                        check++;
                        if (o1.getText().toString().equals(valueOf(questionArrayList.get(total).answer))) {
                            c1.setCardBackgroundColor(Color.GREEN);
                            correct++;
                            ca.setText(valueOf(correct));
                        } else {
                            w.start();
                            //answer is wrong find correct and make it green
                            wrong++;
                            c1.setCardBackgroundColor(Color.RED);
                            if (o2.getText().toString().equals(valueOf(questionArrayList.get(total).answer))) {
                                c2.setCardBackgroundColor(Color.GREEN);
                            } else if (o3.getText().toString().equals(valueOf(questionArrayList.get(total).answer))) {
                                c3.setCardBackgroundColor(Color.GREEN);
                            } else if (o4.getText().toString().equals(valueOf(questionArrayList.get(total).answer))) {
                                c4.setCardBackgroundColor(Color.GREEN);
                            }
                        }
                    } else {

                        Toast.makeText(QuizActivity.this.getApplicationContext(), "Can't select more then one option", Toast.LENGTH_SHORT).show();
                    }
                    next.setVisibility(View.VISIBLE);
                }
            });
            o2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (check == 0) {
                        check++;
                        if (o2.getText().toString().equals(valueOf(questionArrayList.get(total).answer))) {
                            c.start();
                            c2.setCardBackgroundColor(Color.GREEN);
                            correct++;
                            ca.setText(valueOf(correct));
                        } else {
                            w.start();
                            //answer is wrong find correct and make it green
                            wrong++;
                            c2.setCardBackgroundColor(Color.RED);
                            if (o1.getText().toString().equals(valueOf(questionArrayList.get(total).answer))) {
                                c1.setCardBackgroundColor(Color.GREEN);
                            } else if (o3.getText().toString().equals(valueOf(questionArrayList.get(total).answer))) {
                                c3.setCardBackgroundColor(Color.GREEN);
                            } else if (o4.getText().toString().equals(valueOf(questionArrayList.get(total).answer))) {
                                c4.setCardBackgroundColor(Color.GREEN);
                            }
                        }
                    } else {

                        Toast.makeText(QuizActivity.this.getApplicationContext(), "Can't select more then one option", Toast.LENGTH_SHORT).show();
                    }
                    next.setVisibility(View.VISIBLE);
                }
            });
            o3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (check == 0) {
                        check++;
                        if (o3.getText().toString().equals(valueOf(questionArrayList.get(total).answer))) {
                            c.start();
                            c3.setCardBackgroundColor(Color.GREEN);
                            correct++;
                            ca.setText(valueOf(correct));
                        } else {
                            w.start();
                            //answer is wrong find correct and make it green
                            wrong++;
                            c3.setCardBackgroundColor(Color.RED);
                            if (o2.getText().toString().equals(valueOf(questionArrayList.get(total).answer))) {
                                c2.setCardBackgroundColor(Color.GREEN);
                            } else if (o1.getText().toString().equals(valueOf(questionArrayList.get(total).answer))) {
                                c1.setCardBackgroundColor(Color.GREEN);
                            } else if (o4.getText().toString().equals(valueOf(questionArrayList.get(total).answer))) {
                                c4.setCardBackgroundColor(Color.GREEN);
                            }
                        }
                    } else {

                        Toast.makeText(QuizActivity.this.getApplicationContext(), "Can't select more then one option", Toast.LENGTH_SHORT).show();
                    }
                    next.setVisibility(View.VISIBLE);

                }
            });
            o4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (check == 0) {
                        check++;
                        if (o4.getText().toString().equals(valueOf(questionArrayList.get(total).answer))) {
                            c.start();
                            c4.setCardBackgroundColor(Color.GREEN);
                            correct++;
                            ca.setText(valueOf(correct));
                        } else {
                            w.start();
                            //answer is wrong find correct and make it green
                            wrong++;
                            c4.setCardBackgroundColor(Color.RED);
                            if (o2.getText().toString().equals(valueOf(questionArrayList.get(total).answer))) {
                                c2.setCardBackgroundColor(Color.GREEN);
                            } else if (o3.getText().toString().equals(valueOf(questionArrayList.get(total).answer))) {
                                c3.setCardBackgroundColor(Color.GREEN);
                            } else if (o1.getText().toString().equals(valueOf(questionArrayList.get(total).answer))) {
                                c1.setCardBackgroundColor(Color.GREEN);
                            }
                        }
                    } else {

                        Toast.makeText(QuizActivity.this.getApplicationContext(), "Can't select more then one option", Toast.LENGTH_SHORT).show();
                    }
                    next.setVisibility(View.VISIBLE);

                }
            });
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    check = 0;
                    c1.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                    c2.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                    c3.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                    c4.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                    total++;
                    QuizActivity.this.updatequestion();
                }
            });
        }
    }


    public void timer(int seconds)
    {
        t=findViewById(R.id.timer);
        timer=new CountDownTimer(seconds*1000+1000,1000)
        {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds=(int) (millisUntilFinished/1000);
                int minutes=seconds/60;
                seconds=seconds%60;
                t.setText(String.format("%02d",minutes)+":"+String.format("%02d",seconds));
            }
            @Override
            public void onFinish() {
                if(total<=20) {
                    to.start();
                    total = total - 1;
                    t.setText("TIME OUT");
                    Intent myIntent = new Intent(getApplicationContext(), ResultActivity.class);
                    myIntent.putExtra("total", valueOf(total));
                    myIntent.putExtra("correct", valueOf(correct));
                    myIntent.putExtra("wrong", valueOf(wrong));
                    myIntent.putExtra("hint", valueOf(hu));
                    startActivity(myIntent);
                    finish();
                }
            }
        }.start();
    }

}
