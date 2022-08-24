package chathu.learn.music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DrawerLayout drawerLayout;
    NavigationView nav;
    Toolbar toolbar;

    ArrayList<AudioModel>songsList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        recyclerView=findViewById(R.id.songRv);
        sidebar();

        if(checkPermission()==false){
            requestPermission();
            return;
        }
        String[] projection={
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };
        String selection=MediaStore.Audio.Media.IS_MUSIC+" !=0";

        Cursor cursor=getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,selection,null,null);

        while(cursor.moveToNext()){
            AudioModel songData=new AudioModel(cursor.getString(1),
                    cursor.getString(0),cursor.getString(2));
            if(new File(songData.getPath()).exists()) {//check song still exists ,becase when sometimes deleted songs stay in database
                songsList.add(songData);
            }
        }

        if(songsList.size()==0){
            Toast.makeText(this, "No songs found!:(", Toast.LENGTH_SHORT).show();
        }else{
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new MusicListAdapter(songsList,getApplicationContext()));
        }

    }

    private void sidebar() {
        nav=findViewById(R.id.nav);
        drawerLayout=findViewById(R.id.drawerLayout);
        toolbar=findViewById(R.id.toolBar);

        setSupportActionBar(toolbar);//sets the toolbar as the action bar for this activity

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name,R.string.app_name);
        /**activity	Activity: The Activity hosting the drawer. Should have an ActionBar.
         drawerLayout	DrawerLayout: The DrawerLayout to link to the given Activity's ActionBar
         openDrawerContentDescRes	int: A String resource to describe the "open drawer" action for accessibility
         closeDrawerContentDescRes	int: A String resource to describe the "close drawer" action for accessibility
         *
         */
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_songs:

                        break;
                    case R.id.nav_fav:

                        break;
                    case R.id.nav_share:
                        String url = "App Link : https://play.google.com/store/apps/details?id=" + getPackageName();
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, url);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing App");
                        startActivity(Intent.createChooser(intent, getString(R.string.app_name)));
                        break;
                    case R.id.nav_rate:
                        String pkg = getPackageName();
                        String rateMUrl = "market://details?id=" + pkg;
                        String rateDUrl = "https://play.google.com/store/apps/details?id=" + pkg;

                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(rateMUrl)));
                        } catch (Exception e) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(rateDUrl)));
                        }
                        break;
                    case R.id.nav_pri:
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.policy))));
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Link Note Found", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }

    boolean checkPermission(){
        int result= ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if(result==PackageManager.PERMISSION_GRANTED){
            return true;
        }else{
            return false;
        }
    }

    void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(this, "Read permission is required! plese allow from settings.", Toast.LENGTH_SHORT).show();
        }else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(recyclerView!=null){
            recyclerView.setAdapter(new MusicListAdapter(songsList,getApplicationContext()));
        }
    }
}