package com.example.projectmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    AppBarConfiguration appBarConfiguration;
    DrawerLayout drawerLayout;
    NavigationView navView;
    private static Context mContext;
    private ViewGroup container;
    private LayoutInflater inflater;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mFirebaseDatabaseReference;
    private GoogleSignInClient mSignInClient;
    private String mUsername;
    NavController navController;
    public static final String ANONYMOUS = "anonymous";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mUsername = ANONYMOUS;
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mSignInClient = GoogleSignIn.getClient(this, gso);

        drawerLayout = findViewById(R.id.drawerLayout);
        navView = findViewById(R.id.nav_view);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph())
                .setOpenableLayout(drawerLayout).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        navView.setNavigationItemSelectedListener(item -> {
            Bundle pageIndex = new Bundle();
            if (mFirebaseAuth.getCurrentUser() != null){
                switch(item.getItemId()){
                    case R.id.nav_loginPage:
                        navController.navigate(R.id.loginPage);
                        break;
                    case R.id.nav_activityList:
                        pageIndex.putInt("index", R.layout.item_activities);
                        navController.navigate(R.id.itemsList, pageIndex);
                        break;
                    case R.id.nav_notifications:
                        pageIndex.putInt("index", R.layout.item_notifications_message);
                        navController.navigate(R.id.itemsList, pageIndex);
                        break;
                    case R.id.nav_task_list:
                        pageIndex.putInt("index", R.layout.item_tasks);
                        navController.navigate(R.id.itemsList, pageIndex);
                        break;
                }
            } else {
                Toast.makeText(mContext,"you have not yet logged in.", Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.loginPage);
            }

            return true;
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                if (mFirebaseUser != null){
                    mFirebaseAuth.signOut();
                    mSignInClient.signOut();
                }
                mUsername = ANONYMOUS;
                navController.navigate(R.id.loginPage);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {

    }
}