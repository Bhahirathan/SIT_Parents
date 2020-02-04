package com.sit.update;



import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

public class NewsWebView extends AppCompatActivity implements ValueEventListener , View.OnClickListener, View.OnTouchListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mref, mTitle, mDesc, mImg, mUrl,mcOn;
    TextView txt;
    TextView txtdesc,txtdes;
    Toolbar toolbar;
    Bitmap res;
    private String AudioURL;
    private ImageView mIcon;
    private PhotoView photoView;

    private MoveableFloatingActionButton buttonPlayPause;


    private MediaPlayer mediaPlayer;
    private int mediaFileLengthInMilliseconds;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_web_view);

        mIcon = findViewById(R.id.imageView2);
        Bitmap bitmap = res;
        RoundedBitmapDrawable mDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        mDrawable.setCircular(true);
        mIcon.setImageDrawable(mDrawable);
        mIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(NewsWebView.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_custom_layout, null);
                photoView = mView.findViewById(R.id.imageView);
                photoView.setImageBitmap(res);
                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });


        initView();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackground(getResources().getDrawable((R.drawable.gradient)));
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle b = getIntent().getExtras();
        String id = b.getString("id");
        mDatabase = FirebaseDatabase.getInstance();
        mref = mDatabase.getReference().child("News").child(id);
        mTitle = mref.child("Title");
        mDesc = mref.child("desc");
        mImg = mref.child("image");
        mUrl = mref.child("url");
        mcOn = mref.child("createdOn");
        txt = findViewById(R.id.textView2);
        txtdesc = findViewById(R.id.textView3);
        txtdes = findViewById(R.id.textVie);
        txtdesc.setMovementMethod(new ScrollingMovementMethod());
        initView();


    }


    private void initView() {
        buttonPlayPause = findViewById(R.id.play);
        buttonPlayPause.setOnClickListener(this);


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }



    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.play){
            /** ImageButton onClick event handler. Method which start/pause mediaplayer playing */
            try {
                mediaPlayer.setDataSource(AudioURL); // setup song from https://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3 URL to mediaplayer data source
                mediaPlayer.prepare(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.
            } catch (Exception e) {
                e.printStackTrace();
            }

            mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL

            if(!mediaPlayer.isPlaying()){
                mediaPlayer.start();
                buttonPlayPause.setImageResource(R.drawable.pause);
            }else {
                mediaPlayer.pause();
                buttonPlayPause.setImageResource(R.drawable.play);
            }


        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                startActivity(new Intent(this,MainActivity.class));
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                startActivity(new Intent(this,MainActivity.class));
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        /** MediaPlayer onCompletion event handler. Method which calls then song playing is complete*/
        buttonPlayPause.setImageResource(R.drawable.play);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        /** Method which updates the SeekBar secondary progress by current song loading from URL position*/
    }
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        long time=0;
        String value="";
        if(dataSnapshot.getKey().equals("createdOn"))
        {
            if(dataSnapshot.getValue(Long.class)!=null)
            time = dataSnapshot.getValue(Long.class);

        }
        else {
            value = dataSnapshot.getValue(String.class);
        }

        if (dataSnapshot.getKey().equals("createdOn")) {
            String s="";
            if(dataSnapshot.getValue(Long.class)!=null) {
                 s = DateFormat.getDateTimeInstance().format(new Date(time));
            }
                txtdes.setText("Posted At: " + s);

        }
        if (dataSnapshot.getKey().equals("Title")) {
            toolbar.setTitle(value);
            txt.setText(value);
        }
        else if (dataSnapshot.getKey().equals("title")) {
            toolbar.setTitle(value);
            txt.setText(value);
        }else if (dataSnapshot.getKey().equals("image")) {
            new DownLoadImageTask(mIcon).execute(value);
        } else if (dataSnapshot.getKey().equals("desc")) {
            txtdesc.setText(value);
            txtdesc.setText(new SpannableString(txtdesc.getText()));
            TextJustification.justify(txtdesc);
        } else if (dataSnapshot.getKey().equals("url")) {
          if(value==null||value.equals("")) {
              buttonPlayPause.setVisibility(View.GONE);
          }
          else {
              AudioURL = value;
          }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onBackPressed() {

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
            startActivity(new Intent(this,MainActivity.class));

    }
    @Override
    protected void onStart() {
        super.onStart();
        mTitle.addValueEventListener(this);
        mImg.addValueEventListener(this);
        mDesc.addValueEventListener(this);
        mUrl.addValueEventListener(this);
        mcOn.addValueEventListener(this);
    }


    private class DownLoadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try {
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            } catch (Exception e) { // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
            res=result;
        }
    }
}





