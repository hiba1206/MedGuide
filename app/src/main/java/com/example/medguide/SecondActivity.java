package com.example.medguide;

import com.example.medguide.ui.login.LoginFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;



import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class SecondActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    // GoogleSignInOptions gso;
    //GoogleSignInClient gsc;
    //TextView name,email;
    //Button signOutBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_menu);

              /* ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        signOutBtn = findViewById(R.id.signout);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if(acct !=null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            name.setText(personName);
            email.setText(personEmail);
        }

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        }); */

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_logged_in);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.main_second);
        navigationView = findViewById(R.id.nav_view_logged_in);

        // Set up ActionBarDrawerToggle
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        toolbar.setNavigationIcon(R.drawable.hamburger);
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Load the default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_logged_in, new HomeFragment())
                    .commit();
        }

        // Handle Navigation Item Selection
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_logged_in, new HomeFragment()).commit();
            }else if (item.getItemId() == R.id.nav_historique){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_logged_in, new HistoriqueFragment()).commit();
            }else if (item.getItemId() == R.id.nav_doctors){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_logged_in, new doctorsFragment()).commit();
            }else if (item.getItemId() == R.id.nav_medicaments) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_logged_in, new medicamentsFragment()).commit();
            }else if (item.getItemId() == R.id.nav_pharmacie) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_logged_in, new pharmacieFragment()).commit();
            }else if (item.getItemId() == R.id.nav_pharmacie_garde) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_logged_in, new gardeFragment()).commit();
            }else if (item.getItemId() == R.id.nav_share) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_logged_in, new shareFragment()).commit();
            }else if (item.getItemId() == R.id.nav_logout) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_logged_in, new LoginFragment()).commit();
            }else {
                Toast.makeText(this, "Unknown menu item", Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }
    /*    void signOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                startActivity(new Intent(SecondActivity.this,MainActivity.class));
            }
        });
    } */
}
