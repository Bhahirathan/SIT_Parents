package  com.sit.update;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
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
import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView mPeopleRV;
    private FirebaseRecyclerAdapter<News, MainActivity.NewsViewHolder> mPeopleRVAdapter;
    private LinearLayoutManager mLayoutManager;
    private static FirebaseDatabase Database;
    private SwipeRefreshLayout swipeRefreshLayout;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setBackground(getResources().getDrawable((R.drawable.gradient)));
                setSupportActionBar(toolbar);
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

                if (Database == null) {
                    Database = FirebaseDatabase.getInstance();
                    Database.setPersistenceEnabled(true);
                }
        FirebaseMessaging.getInstance().subscribeToTopic("notifications");
               refresh();
        }
        private void refresh()
        {
            DatabaseReference mDatabase = Database.getReference().child("News");
            mDatabase.keepSynced(true);
            mPeopleRV = findViewById(R.id.myRecycleView);
            final DatabaseReference personsRef = FirebaseDatabase.getInstance().getReference().child("News");
            final Query personsQuery = personsRef.orderByKey();
            mPeopleRV.hasFixedSize();
            mLayoutManager = new LinearLayoutManager(MainActivity.this);
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
            mPeopleRV.setLayoutManager(mLayoutManager);
            final FirebaseRecyclerOptions<News> personsOptions = new FirebaseRecyclerOptions.Builder<News>().setQuery(personsQuery, News.class).build();
            mPeopleRVAdapter = new FirebaseRecyclerAdapter<News, MainActivity.NewsViewHolder>(personsOptions) {
                @Override
                protected void onBindViewHolder(@NonNull final MainActivity.NewsViewHolder holder, int position, @NonNull final News model) {

                    holder.setTitle(model.getTitle());

                    holder.setDesc(model.getDesc());

                    if (model.getCreatedOn() != null) {

                        holder.setTime(model.getCreatedOn());
                    }
                    final String n = getRef(position).getKey();
                    if (model.getImage() != null)
                        holder.setImage(model.getImage(), n);
                    else
                        holder.disable();
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
                if(model.getImage()!=null)
                holder.setImage( model.getImage(), n);
                else
                    holder.disable();

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
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Link will be Updated Soon";
            String shareSub = "SIT Parent App";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
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
        NewsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        void setTitle(String title) {
            TextView post_title = mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }

        @SuppressLint("SetTextI18n")
        void setTime(Long time) {
            TextView post_time = mView.findViewById(R.id.post_time);

            String s = DateFormat.getDateTimeInstance().format(new Date(time));
            post_time.setText("Posted At: " + s);
        }

        void setDesc(String desc) {
            TextView post_desc = mView.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }
        void disable()
        {
         ImageView imageView=mView.findViewById(R.id.post_image);
            CardView cardView =mView.findViewById(R.id.rl);
            RelativeLayout relativeLayout=mView.findViewById(R.id.rl1);
            ViewGroup.LayoutParams layoutParams=cardView.getLayoutParams();
            layoutParams.height= ViewGroup.LayoutParams.WRAP_CONTENT;
            ViewGroup.LayoutParams layoutParam=relativeLayout.getLayoutParams();
            layoutParam.height= ViewGroup.LayoutParams.WRAP_CONTENT;
         imageView.setVisibility(View.GONE);
        }
        void setImage(String image, String id) {
            final ImageView post_image = mView.findViewById(R.id.post_image);
            String path= Objects.requireNonNull(getApplicationContext().getExternalCacheDir()).toString();
            path=path.replace("cache","");
            final File f=new File(path+"/Parent/",id+".jpeg");
            if(!f.exists()) {
                DownloadManager dm=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                Uri ur = Uri.parse(image);
                DownloadManager.Request request=new DownloadManager.Request(ur);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI|DownloadManager.Request.NETWORK_MOBILE)
                        .setDestinationInExternalPublicDir("Android/data/com.sit.update/Parent/",id+".jpeg")
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                ;
                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String action=intent.getAction();
                        if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)){
                            bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                            post_image.setImageBitmap(bitmap);
                        }
                    }
                };
                if (dm != null) {
                    dm.enqueue(request);
                }
                registerReceiver(broadcastReceiver,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
            bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
            post_image.setImageBitmap(bitmap);
                //Picasso.with(ctx).load(image).into(post_image);
            //}
        }
        }
    }