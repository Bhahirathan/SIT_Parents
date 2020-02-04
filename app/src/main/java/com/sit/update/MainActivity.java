package  com.sit.update;




import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView tex,tev;
    ArrayList<Uri> arrayListapkFilepath; // define global
    private RecyclerView mPeopleRV;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<News, MainActivity.NewsViewHolder> mPeopleRVAdapter;
    private LinearLayoutManager mLayoutManager;
    private static FirebaseDatabase Database;
    private NotificationManager notificationManager;
    private List<String> items;
    private Menu menu;
    public static final String CHANNEL="1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackground(getResources().getDrawable((R.drawable.gradient)));
        setSupportActionBar(toolbar);

        arrayListapkFilepath = new ArrayList<Uri>();


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(Database==null) {
            Database= FirebaseDatabase.getInstance();
            Database.setPersistenceEnabled(true);
        }


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "1";
        String channel2 = "2";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId,
                    "Channel 1",NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("This is BNT");
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(true);
            notificationManager.createNotificationChannel(notificationChannel);

            NotificationChannel notificationChannel2 = new NotificationChannel(channel2,
                    "Channel 2",NotificationManager.IMPORTANCE_MIN);

            notificationChannel.setDescription("This is bTV");
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(true);
            notificationManager.createNotificationChannel(notificationChannel2);

        }

        // Get Firebase database reference
        FirebaseMessaging.getInstance().subscribeToTopic("notifications");

        //FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
        //FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
        // Init user list










        mDatabase = Database.getReference().child("News");
        mDatabase.keepSynced(true);
        mPeopleRV = findViewById(R.id.myRecycleView);
        final DatabaseReference personsRef = FirebaseDatabase.getInstance().getReference().child("News");
        final Query personsQuery = personsRef.orderByKey();
        mPeopleRV.hasFixedSize();
        mLayoutManager = new LinearLayoutManager(MainActivity.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mPeopleRV.setLayoutManager(mLayoutManager);


        final FirebaseRecyclerOptions personsOptions = new FirebaseRecyclerOptions.Builder<News>().setQuery(personsQuery, News.class).build();

        mPeopleRVAdapter = new FirebaseRecyclerAdapter<News, MainActivity.NewsViewHolder>(personsOptions) {
            @Override
            protected void onBindViewHolder(final MainActivity.NewsViewHolder holder, int position, final News model) {
                holder.setTitle(model.getTitle());

                holder.setDesc(model.getDesc());

                if(model.getCreatedOn()!=null) {

                    holder.setTime(model.getCreatedOn());
                }
                holder.setImage(getBaseContext(), model.getImage());
                final String n=getRef(position).getKey();
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), NewsWebView.class);
                        intent.putExtra("id", n);
                        startActivity(intent);
                    }
                });
            }
            @Override
            public MainActivity.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.news_row, parent, false);
                return new MainActivity.NewsViewHolder(view);
            }
        };
        mPeopleRV.setAdapter(mPeopleRVAdapter);
    }
    private void fireSearch(String searchText) {

        //convert string entered in SearchView to lowercase

        final DatabaseReference personsRe = FirebaseDatabase.getInstance().getReference().child("News");
        final Query personsQuer;
        if(searchText.equals(""))
        {
            personsQuer = personsRe.orderByKey();
        }
        else {
            personsQuer = personsRe.orderByChild("Title").startAt(searchText)
                    .endAt(searchText + "\uf8ff");
        }

        mPeopleRV.hasFixedSize();
        mLayoutManager = new LinearLayoutManager(MainActivity.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mPeopleRV.setLayoutManager(mLayoutManager);

        final FirebaseRecyclerOptions personsOption = new FirebaseRecyclerOptions.Builder<News>().setQuery(personsQuer, News.class).build();

        mPeopleRVAdapter = new FirebaseRecyclerAdapter<News, MainActivity.NewsViewHolder>(personsOption) {
            @Override
            protected void onBindViewHolder(final MainActivity.NewsViewHolder holder, final int position, final News model) {
                holder.setTitle(model.getTitle());
                holder.setDesc(model.getDesc());

                if(model.getCreatedOn()!=null) {

                    holder.setTime(model.getCreatedOn());
                }

                holder.setImage(getBaseContext(), model.getImage());
                final String n=getRef(position).getKey();
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), NewsWebView.class);
                        intent.putExtra("id",n );
                        startActivity(intent);
                    }
                });
            }
            @Override
            public MainActivity.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

        this.menu = menu;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();

            search.setQueryHint("Search By Title...");

            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

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

        }

        return true;

    }

    // History


    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
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
        mPeopleRVAdapter.stopListening();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public NewsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title) {
            TextView post_title = mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }
        public void setTime(Long time) {
            TextView post_time = mView.findViewById(R.id.post_time);

            String s = DateFormat.getDateTimeInstance().format(new Date(time));
            post_time.setText("Posted At: "+s);
        }

        public void setDesc(String desc) {
            TextView post_desc = mView.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }

        public void setImage(Context ctx, String image) {
            ImageView post_image =  mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);
        }
    }
}
