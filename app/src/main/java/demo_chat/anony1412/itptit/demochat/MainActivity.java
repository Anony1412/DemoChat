package demo_chat.anony1412.itptit.demochat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    // FireBase
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;

    // AndroidLayout
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private SelectionsPageAdapter mSelectionsPageAdapter;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        Setup();
    }

    private void Setup() {
        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("DemoChat");

        mViewPager = findViewById(R.id.tabsPager);
        mSelectionsPageAdapter = new SelectionsPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSelectionsPageAdapter);

        mTabLayout = findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {

            sendToStart();

        } else {

            mUserRef.child("online").setValue(true);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        mUserRef.child("online").setValue(false);
    }

    private void sendToStart() {
        Intent start_intent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(start_intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        int id = item.getItemId();
        switch (id) {
            case R.id.btn_main_settting: {
                Intent setting_Intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(setting_Intent);
                break;
            }
            case R.id.btn_main_all_users : {
                Intent user_Intent = new Intent(MainActivity.this, UsersActivity.class);
                startActivity(user_Intent);
                break;
            }
            case R.id.btn_main_log_out: {
                FirebaseAuth.getInstance().signOut();
                sendToStart();
                break;
            }
        }

        return true;
    }
}
