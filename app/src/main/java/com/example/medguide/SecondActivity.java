package com.example.medguide;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
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

        // Handle Navigation Item Selection
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_logged_in, new HomeFragment())
                        .commit();
            } else if (item.getItemId() == R.id.profil) {
                ProfilFragment profilFragment = new ProfilFragment();
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                profilFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_logged_in, profilFragment)
                        .addToBackStack(null)
                        .commit();
            } else if (item.getItemId() == R.id.nav_logout) {
                // Afficher un message de confirmation avant la déconnexion
                new AlertDialog.Builder(SecondActivity.this)
                        .setMessage("Êtes-vous sûr de vouloir vous déconnecter ?")
                        .setCancelable(false) // L'utilisateur ne peut pas annuler la boîte de dialogue en cliquant en dehors
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Code de déconnexion
                                SharedPreferences prefs = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("isLoggedIn", false); // Update login state
                                editor.apply();

                                // Déconnexion de Google Sign-In ou Firebase
                                GoogleSignIn.getClient(SecondActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();
                                FirebaseAuth.getInstance().signOut();

                                // Rediriger l'utilisateur vers l'écran de connexion
                                Intent intent = new Intent(SecondActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();  // Terminer l'activité actuelle pour éviter de revenir en arrière
                            }
                        })
                        .setNegativeButton("Non", null)  // Si "Non" est cliqué, fermer la boîte de dialogue sans faire d'action
                        .show();
            } else {
                Toast.makeText(this, "Élément du menu inconnu", Toast.LENGTH_SHORT).show();
            }

            // Fermer le drawer après un choix
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Charger le fragment HomeFragment par défaut
        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        homeFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_logged_in, homeFragment)
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
