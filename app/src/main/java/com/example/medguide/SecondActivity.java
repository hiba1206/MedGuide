package com.example.medguide;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.medguide.ui.login.LoginFragment;
import com.google.android.material.navigation.NavigationView;
import java.util.Objects;

public class SecondActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_menu);

        // Récupérer le username transmis par LoginFragment
        String username = getIntent().getStringExtra("username");

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_logged_in);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.main_second);
        navigationView = findViewById(R.id.nav_view_logged_in);

        // Récupérer l'en-tête de la NavigationView
        View headerView = navigationView.getHeaderView(0);

        // Trouver le TextView dans l'en-tête
        TextView tvUsername = headerView.findViewById(R.id.tv_username);

        // Mettre à jour le texte avec le username récupéré
        if (username != null) {
            tvUsername.setText(username);
        }

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
            if (item.getItemId() == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_logged_in, new HomeFragment())
                        .commit();
            } else if (item.getItemId() == R.id.profil) {
                ProfilFragment profilFragment = new ProfilFragment();

                // Créer un Bundle pour passer le username au fragment
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                profilFragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_logged_in, profilFragment)
                        .commit();
            } else if (item.getItemId() == R.id.nav_historique) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_logged_in, new HistoriqueFragment())
                        .commit();
            } else if (item.getItemId() == R.id.nav_doctors) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_logged_in, new doctorsFragment())
                        .commit();
            } else if (item.getItemId() == R.id.nav_medicaments) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_logged_in, new medicamentsFragment())
                        .commit();
            } else if (item.getItemId() == R.id.nav_pharmacie) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_logged_in, new pharmacieFragment())
                        .commit();
            } else if (item.getItemId() == R.id.nav_pharmacie_garde) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_logged_in, new gardeFragment())
                        .commit();
            } else if (item.getItemId() == R.id.nav_share) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_logged_in, new shareFragment())
                        .commit();
            } else if (item.getItemId() == R.id.nav_logout) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_logged_in, new LoginFragment())
                        .commit();
            } else {
                Toast.makeText(this, "Unknown menu item", Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        String usrname = getIntent().getStringExtra("username");

        HomeFragment homeFragment = new HomeFragment();

        Bundle bundle = new Bundle();
        bundle.putString("username", usrname);

        homeFragment.setArguments(bundle);

// Load the fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_logged_in, homeFragment)
                .commit();

    }

    // Fonction pour gérer le clic sur l'image
    public void onProfileImageClick(View view) {
        // Remplacer le fragment actuel par ProfilFragment
        ProfilFragment profilFragment = new ProfilFragment();

        // Récupérer le username et le passer au fragment
        String username = getIntent().getStringExtra("username");
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        profilFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_logged_in, profilFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}

