package com.sit.update;



import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.NavUtils;
import androidx.core.view.GravityCompat;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

public class NewsWebView extends AppCompatActivity implements ValueEventListener , View.OnClickListener, View.OnTouchListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {

    private DatabaseReference mTitle;
    private DatabaseReference mDesc;
    private DatabaseReference mImg;
    private DatabaseReference mUrl;
    private DatabaseReference mcOn;
    TextView txt;
    TextView txtdesc,txtdes;
    Toolbar toolbar;
    private String AudioURL;
    private ImageView mIcon;
    private PhotoView photoView;
    private Bitmap bitmap;
    private MoveableFloatingActionButton buttonPlayPause;
    private String id;
    private MediaPlayer mediaPlayer;
    private CardView cardView;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_web_view);
        mIcon = findViewById(R.id.imageView2);
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(NewsWebView.this);
        @SuppressLint("InflateParams") View mView = getLayoutInflater().inflate(R.layout.dialog_custom_layout, null);
        mBuilder.setView(mView);
        photoView = mView.findViewById(R.id.imageView);
        cardView=mView.findViewById(R.id.cd);
        final AlertDialog mDialog = mBuilder.create();
        mIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.show();
            }
        });
        initView();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackground(getResources().getDrawable((R.drawable.gradient)));
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(this.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            id = b.getString("id");
        }
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mref = mDatabase.getReference().child("News").child(id);
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


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        buttonPlayPause = findViewById(R.id.play);
        buttonPlayPause.setForegroundGravity(GravityCompat.END);
        buttonPlayPause.setOnClickListener(this);


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }



    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.play){
            try {
                mediaPlayer.setDataSource(AudioURL);
                mediaPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }

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
        if (item.getItemId() == android.R.id.home) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            startActivity(new Intent(this, MainActivity.class));
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        startActivity(new Intent(this, MainActivity.class));
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        buttonPlayPause.setImageResource(R.drawable.play);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
    }
    @SuppressLint("SetTextI18n")
    @SuppressWarnings("ControlFlowStatementWithoutBraces")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        long time=0;
        String value="";
        if(Objects.equals(dataSnapshot.getKey(), "createdOn"))
        {
            if(dataSnapshot.getValue(Long.class)!=null)
                //noinspection ControlFlowStatementWithoutBraces
            {
                //noinspection ConstantConditions
                time = dataSnapshot.getValue(Long.class);
            }
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
        switch (dataSnapshot.getKey()) {
            case "Title":
                toolbar.setTitle(value);
                txt.setText(value);
                break;
            case "image":
                if (value != null) {
                    String path = Objects.requireNonNull(getApplicationContext().getExternalCacheDir()).toString();
                    path = path.replace("cache", "");
                    final File f = new File(path + "/Parent/", id + ".jpeg");
                    if (!f.exists()) {
                        Toast.makeText(getApplicationContext(), f.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        Uri ur = Uri.parse(value);
                        DownloadManager.Request request = new DownloadManager.Request(ur);
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                                .setDestinationInExternalPublicDir("/Parent/", id + ".jpeg")
                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                        ;
                        bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                String action = intent.getAction();
                                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                                    mIcon.setImageBitmap(bitmap);
                                    photoView.setImageDrawable(mIcon.getDrawable());
                                    ViewGroup.LayoutParams p = photoView.getLayoutParams();
                                    cardView.setLayoutParams(p);
                                }
                            }
                        };
                        if (dm != null) {
                            dm.enqueue(request);
                        }
                        registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    }
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                    mIcon.setImageBitmap(bitmap);
                    photoView.setImageDrawable(mIcon.getDrawable());
                } else {
                    mIcon.setVisibility(View.GONE);
                }
                //new DownLoadImageTask(mIcon).execute(value);
                break;
            case "desc":
                txtdesc.setText(value);
                txtdesc.setText(new SpannableString(txtdesc.getText()));
                TextJustification.justify(txtdesc);
                break;
            case "url":
                if (value == null || value.equals("")) {
                    buttonPlayPause.setVisibility(View.GONE);
                } else {
                    AudioURL = value;
                }
                break;
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

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
}





