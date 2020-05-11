package  com.sit.update;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView mPeopleRV;
    private FirebaseRecyclerAdapter<News, MainActivity.NewsViewHolder> mPeopleRVAdapter;
    private LinearLayoutManager mLayoutManager;
    private DatabaseReference mDatabase;
    private Context mcontext;
    private SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar p;
    @SuppressLint("ResourceAsColor")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setBackground(getResources().getDrawable(R.color.tool));
                setSupportActionBar(toolbar);
                setProgressBarIndeterminateVisibility(true);
        p=findViewById(R.id.pbar);
                Toast toast=Toast.makeText(getApplicationContext(),"Loading, Please wait...",Toast.LENGTH_LONG);
                toast.show();
        AlertDialog.Builder alert=new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("No Internet Connection !!!");
        alert.setMessage("Please Connect to the Internet!!!");
        alert.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog=alert.create();
        if(!isNConnected())
        alertDialog.show();
        mcontext=getApplicationContext();
        swipeRefreshLayout=findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startActivity(new Intent(MainActivity.this,MainActivity.class));
                swipeRefreshLayout.setRefreshing(false);
            }
        });
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.addDrawerListener(toggle);
                toggle.syncState();

                NavigationView navigationView = findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(this);

                if (mDatabase == null ) {
                    try {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        database.setPersistenceEnabled(true);
                        mDatabase = database.getReference();
                    } catch (Exception e) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        mDatabase = database.getReference();
                    }
                }
        FirebaseMessaging.getInstance().subscribeToTopic("notifications");
               refresh();
          //     p.setVisibility(View.GONE);
        }
        private void refresh()
        {
            mDatabase.keepSynced(true);
            mDatabase =mDatabase.child("News");
            mPeopleRV = findViewById(R.id.myRecycleView);
            final DatabaseReference personsRef = FirebaseDatabase.getInstance().getReference().child("News");
            final Query personsQuery = personsRef.orderByKey();
            mPeopleRV.hasFixedSize();
            mLayoutManager = new LinearLayoutManager(MainActivity.this);
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
            mPeopleRV.setLayoutManager(mLayoutManager);
            final FirebaseRecyclerOptions<News> personsOptions = new FirebaseRecyclerOptions.Builder<News>().setQuery(personsQuery, News.class).build();
            mPeopleRVAdapter = new FirebaseRecyclerAdapter< News, MainActivity.NewsViewHolder>(personsOptions) {
                @SuppressLint("GetInstance")
                @Override
                public void onDataChanged()
                {
                    p.setVisibility(View.GONE);
                }
                @Override
                protected void onBindViewHolder(@NonNull final MainActivity.NewsViewHolder holder, int position, @NonNull final News model) {

                    holder.setTitle(model.getTitle());

                    holder.setDesc(model.getDesc());

                    if (model.getCreatedOn() != null) {

                        holder.setTime(model.getCreatedOn());
                    }
                    final String n = getRef(position).getKey();
                    holder.mView.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            String shareBody = "http://sitparents.com/"+n;
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                            startActivity(Intent.createChooser(sharingIntent, "Share using"));
                        }
                    });
                    if (model.getImage() != null)
                        holder.setImage(mcontext,model.getImage(), n);
                    else
                    {
                        holder.disable();
                    holder.mView.findViewById(R.id.cr).setVisibility(View.GONE);
                }
                    holder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), NewsWebView.class);
                            intent.putExtra("id", n);
                            startActivity(intent);
                        }
                    });
                }

                @NonNull
                @Override
                public MainActivity.NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.news_row, parent, false);
                    return new MainActivity.NewsViewHolder(view);
                }
            };
            mPeopleRV.setAdapter(mPeopleRVAdapter);
        }
        @Override
        protected void onResume()
        {
            super.onResume();
        }
        protected void onPause()
        {
            super.onPause();
        }
    private boolean isNConnected()
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return Objects.requireNonNull(connectivityManager).getActiveNetworkInfo()!=null && Objects.requireNonNull(connectivityManager.getActiveNetworkInfo()).isConnected();
    }
    private void fireSearch(String searchText) {

        final DatabaseReference personsRe = FirebaseDatabase.getInstance().getReference().child("News");
        final Query personsQuer;
        if (searchText.equals("")) {
            personsQuer = personsRe.orderByKey();
        } else {
            personsQuer = personsRe.orderByChild("Title").startAt(searchText)
                    .endAt(searchText + "\uf8ff");
        }
        mPeopleRV.hasFixedSize();
        mLayoutManager = new LinearLayoutManager(MainActivity.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mPeopleRV.setLayoutManager(mLayoutManager);

        final FirebaseRecyclerOptions<News> personsOption = new FirebaseRecyclerOptions.Builder<News>().setQuery(personsQuer, News.class).build();
        mPeopleRVAdapter = new FirebaseRecyclerAdapter<News, MainActivity.NewsViewHolder>(personsOption) {
            @Override
            protected void onBindViewHolder(@NonNull final MainActivity.NewsViewHolder holder, final int position, @NonNull final News model) {
                holder.setTitle(model.getTitle());
                holder.setDesc(model.getDesc());

                if (model.getCreatedOn() != null) {

                    holder.setTime(model.getCreatedOn());
                }
                final String n = getRef(position).getKey();
                holder.mView.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody = "http://sitparents.com/"+n;
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Share using"));
                    }
                });
                if(model.getImage()!=null)
                holder.setImage(mcontext, model.getImage(), n);
                else {
                    holder.disable();
                    holder.mView.findViewById(R.id.cr).setVisibility(View.GONE);
                }
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), NewsWebView.class);
                        intent.putExtra("id", n);
                        startActivity(intent);
                    }
                });
            }
            @NonNull
            @Override
            public MainActivity.NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.news_row, parent, false);
                return new MainActivity.NewsViewHolder(view);
            }
        };
        mPeopleRV.setAdapter(mPeopleRVAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();

        search.setQueryHint("Search By Title...");

        if (manager != null) {
            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        }
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String query) {
                fireSearch(query);
                mPeopleRVAdapter.startListening();
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String s) {
                fireSearch(s);
                return false;
            }
        });

        return true;
    }

    // History
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this).setTitle("Exit")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton("No",null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                        }
                    }).create().show();
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.nav_courses)
        {
            Intent intent=new Intent(MainActivity.this,intro.class);
            intent.putExtra("back","true");
            startActivity(intent);
        }
     /*   else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Link will be Updated Soon";
            String shareSub = "SIT Parent App";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
        }*/
        else if(id==R.id.about)
        {
            startActivity(new Intent(MainActivity.this,about_us.class));
        }
        else if(id==R.id.abo)
        {
            startActivity(new Intent(MainActivity.this,About.class));
        }
        else
        {
            startActivity(new Intent(MainActivity.this,MainActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPeopleRVAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        Bitmap bitmap ;
        ImageView p,r,sh;
        TextView post_time,post_title,post_desc;
        NewsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            r=mView.findViewById(R.id.RI);
            sh=mView.findViewById(R.id.share);
        }

        void setTitle(String title) {
            post_title = mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }

        @SuppressLint("SetTextI18n")
        void setTime(Long time) {
             post_time = mView.findViewById(R.id.post_time);
             Date d=new Date(time);
             Date now =new Date();
             long sec= TimeUnit.MILLISECONDS.toSeconds(now.getTime()-d.getTime());
            long mins= TimeUnit.MILLISECONDS.toMinutes(now.getTime()-d.getTime());
            long hrs= TimeUnit.MILLISECONDS.toHours(now.getTime()-d.getTime());
            long days= TimeUnit.MILLISECONDS.toDays(now.getTime()-d.getTime());
            //String s = DateFormat.getDateTimeInstance().format(new Date(time));
            String s="\u2022 ";
            if(sec<60)
                s+= sec +" seconds ago";
            else if(mins<60)
                s+= mins +" minutes ago";
            else if(hrs<24)
                s+= hrs +" hours ago";
            else if(days==1)
                s+=days+ " day ago";
            else
                s+= days +" days ago";
            post_time.setText(s);
        }

        void setDesc(String desc) {
            post_desc = mView.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }
        void disable()
        {
         ImageView imageView=mView.findViewById(R.id.post_image);
            CardView cardView =mView.findViewById(R.id.rl);
            RelativeLayout relativeLayout=mView.findViewById(R.id.rl1);
            ViewGroup.LayoutParams layoutParams=cardView.getLayoutParams();
            cardView.setPadding(cardView.getPaddingLeft(),cardView.getPaddingTop(),cardView.getPaddingRight(),20);
            layoutParams.height= ViewGroup.LayoutParams.WRAP_CONTENT;
            ViewGroup.LayoutParams layoutParam=relativeLayout.getLayoutParams();
            layoutParam.height= ViewGroup.LayoutParams.WRAP_CONTENT;
            imageView.setVisibility(View.GONE);
            RelativeLayout rl=mView.findViewById(R.id.rela);
            Resources r=getApplicationContext().getResources();
            RelativeLayout.LayoutParams relaL=(RelativeLayout.LayoutParams)rl.getLayoutParams();
            relaL.addRule(RelativeLayout.BELOW,R.id.post_desc);
            rl.setPadding(rl.getPaddingLeft(),rl.getPaddingTop(),rl.getPaddingRight(),(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,r.getDisplayMetrics()));
            //relaL.setMargins(relaL.leftMargin,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5,r.getDisplayMetrics()),relaL.rightMargin,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,15,r.getDisplayMetrics()));
            rl.setLayoutParams(relaL);
         mView.findViewById(R.id.image).setVisibility(View.GONE);
        }
        @SuppressLint("ResourceAsColor")
        void setImage(Context context,String image, String id) {
            final ImageView post_image = mView.findViewById(R.id.post_image);
            String path= Objects.requireNonNull(getApplicationContext().getExternalCacheDir()).toString();
            path=path.replace("cache","");
            final File f=new File(path+"/files/",id+".jpeg");
            if(!f.exists()) {
                DownloadManager dm=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                Uri ur = Uri.parse(image);
                DownloadManager.Request request=new DownloadManager.Request(ur);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI|DownloadManager.Request.NETWORK_MOBILE)
                        .setDestinationInExternalFilesDir(mcontext,"/",id+".jpeg")
                        //.setDestinationInExternalPublicDir("Android/data/com.sit.update/Parent/",id+".jpeg")
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                ;
                //bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String action=intent.getAction();
                        if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)&&f.exists()){
                            bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                            post_image.setImageBitmap(bitmap);
                            p=mView.findViewById(R.id.image);
                            if(bitmap!=null)
                            if(bitmap.getHeight()<bitmap.getWidth())
                            {
                                post_image.setImageBitmap(bitmap);
                                p.setImageBitmap(bitmap);
                                mView.findViewById(R.id.cr).setVisibility(View.GONE);
                            }
                            else {
                                disable();
                                r.setImageBitmap(bitmap);
                                Resources r = getApplicationContext().getResources();
                                CardView cardView = mView.findViewById(R.id.rl);
                                ViewGroup.LayoutParams layoutPara = cardView.getLayoutParams();
                                layoutPara.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, r.getDisplayMetrics());
                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                layoutParams.addRule(RelativeLayout.BELOW, R.id.post_title);
                                layoutParams.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, r.getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, r.getDisplayMetrics()), 0);
                                post_desc.setLayoutParams(layoutParams);
                                post_desc.setMaxLines(2);
                                RelativeLayout.LayoutParams layoutParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                layoutParam.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, r.getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, r.getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, r.getDisplayMetrics()), 0);
                                post_title.setMaxLines(2);
                                post_title.setLayoutParams(layoutParam);
                                RelativeLayout Rlayout = mView.findViewById(R.id.rela);
                                Rlayout.setGravity(Gravity.BOTTOM);
                                Rlayout.setPadding(Rlayout.getPaddingLeft(), Rlayout.getPaddingTop(), Rlayout.getPaddingRight(), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics()));
                                RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) Rlayout.getLayoutParams();
                                //layout.addRule(RelativeLayout.BELOW,R.id.RI);
                                layout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                //layout.setMargins(layout.leftMargin,layout.topMargin,layout.rightMargin,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,15,r.getDisplayMetrics()));
                                Rlayout.setLayoutParams(layout);
                            }
                            }
                    }
                };

                if (dm != null) {
                    dm.enqueue(request);
                }
                registerReceiver(broadcastReceiver,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
            else {
                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                p = mView.findViewById(R.id.image);
                if (bitmap.getHeight() < bitmap.getWidth()) {
                    post_image.setImageBitmap(bitmap);
                    p.setImageBitmap(bitmap);
                    mView.findViewById(R.id.cr).setVisibility(View.GONE);
                } else {
                    disable();
                    r.setImageBitmap(bitmap);
                    Resources r = getApplicationContext().getResources();
                    CardView cardView = mView.findViewById(R.id.rl);
                    ViewGroup.LayoutParams layoutPara = cardView.getLayoutParams();
                    layoutPara.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, r.getDisplayMetrics());
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.addRule(RelativeLayout.BELOW, R.id.post_title);
                    layoutParams.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, r.getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, r.getDisplayMetrics()), 0);
                    post_desc.setLayoutParams(layoutParams);
                    post_desc.setMaxLines(2);
                    RelativeLayout.LayoutParams layoutParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParam.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, r.getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, r.getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, r.getDisplayMetrics()), 0);
                    post_title.setMaxLines(2);
                    post_title.setLayoutParams(layoutParam);
                    RelativeLayout Rlayout = mView.findViewById(R.id.rela);
                    Rlayout.setGravity(Gravity.BOTTOM);
                    Rlayout.setPadding(Rlayout.getPaddingLeft(), Rlayout.getPaddingTop(), Rlayout.getPaddingRight(), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics()));
                    RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) Rlayout.getLayoutParams();
                    //layout.addRule(RelativeLayout.BELOW,R.id.RI);
                    layout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    //layout.setMargins(layout.leftMargin,layout.topMargin,layout.rightMargin,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,15,r.getDisplayMetrics()));
                    Rlayout.setLayoutParams(layout);
                }
            }
        }
        }
    }