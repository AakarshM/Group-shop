package aakarsh.familyshop;

import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public FirebaseAuth Auth = FirebaseAuth.getInstance();
    public FirebaseAuth.AuthStateListener AuthListener;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    DatabaseReference leaderListRef = db.getRoot();


    public static String uid;
public static    String disp, email;
    TextView text, emailView;
    ListView lview;
    ArrayList<String> groups = new ArrayList<>();

    TextView mainText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mainText = (TextView) findViewById(R.id.MainField);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Main Screen");
        AuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    uid = user.getUid();
                    disp = user.getDisplayName();
                    email = user.getEmail();
                    System.out.println (uid + disp + email);
                    changeDrawerStuff(disp, email);


                } else {
                    Log.i("Problem:", "couldn't login.");
                }
            }
        };FirebaseAuth.getInstance().addAuthStateListener(AuthListener);


        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Fragment fragment = null;
        Class fragmentClass = null;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }




        View header = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
        navigationView.addHeaderView(header);
        text = (TextView) header.findViewById(R.id.DisplayField);
         emailView = (TextView) header.findViewById(R.id.EmailField);



    }


    public void changeDrawerStuff(String d, String e){
        text.setText(d);
        System.out.println(d);
        emailView.setText(e);

    }

    public void getGroupsInvolvedFromDB(){
        System.out.println(leaderListRef.toString());



    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {

            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {   //SIGN OUT TAPPER
            System.out.println("You selected the small tab on the upper-right hand side (SIGN OUT).");
            sgnOutNow();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sgnOutNow(){
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent FrontActivity = new Intent(getApplicationContext(), RegisterLoginActivity.class);
        startActivity(FrontActivity);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;
        if (id == R.id.nav_camera) {
            // Handle the MyGroupsAction
            fragmentClass = MyGroupsFragment.class;
            Auth.removeAuthStateListener(AuthListener);

        } else if (id == R.id.nav_gallery) {
            fragmentClass = AdminSideFragment.class;
          //  AuthListener = null;

            Auth.removeAuthStateListener(AuthListener);
        } else if (id == R.id.nav_people) {
            //Find people
            System.out.println("Clicked on find people");
            fragmentClass = FindPeopleFragment.class;
            Auth.removeAuthStateListener(AuthListener);
          //  AuthListener = null;

        } else if (id == R.id.nav_manage) {
            //Settings
            System.out.println("Clicked on Settings");
            fragmentClass = SettingsFragment.class;
            //AuthListener = null;
            Auth.removeAuthStateListener(AuthListener);

        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mainText.setVisibility(View.INVISIBLE);
     FragmentManager fragmentManager = getSupportFragmentManager();
       fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
