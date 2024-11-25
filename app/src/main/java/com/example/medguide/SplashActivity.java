package com.example.medguide;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Delay for 3 seconds (3000 milliseconds)
        new Handler().postDelayed(() -> {
            // Navigate to the MainActivity
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);

            // Add fade-in and fade-out animations
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            // Close the SplashActivity
            finish();
        }, 3000);
    }
}
