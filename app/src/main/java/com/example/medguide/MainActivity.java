package com.example.medguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.medguide.ui.login.LoginFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity {



    private DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");



        drawerLayout = findViewById(R.id.main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toggle.setDrawerIndicatorEnabled(false);
        toolbar.setNavigationIcon(R.drawable.hamburger);
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .commit();
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_doctors){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new doctorsFragment()).commit();
            }else if (item.getItemId() == R.id.nav_medicaments) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new medicamentsFragment()).commit();
            } else if (item.getItemId() == R.id.nav_pharmacie) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new pharmacieFragment()).commit();
            } else if (item.getItemId() == R.id.nav_pharmacie_garde) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new gardeFragment()).commit();
            } else if (item.getItemId() == R.id.nav_share) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new shareFragment()).commit();
            } else if (item.getItemId() == R.id.nav_login) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment()).commit();
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
}