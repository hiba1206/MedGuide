package com.example.medguide;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.medguide.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private EditText symptomsInput;
    private ImageButton submitButton;
    private String symptoms = "";
    private TextView responseTextView;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        symptomsInput = rootView.findViewById(R.id.symptomsInput);
        responseTextView = rootView.findViewById(R.id.responseTextView);
        submitButton = rootView.findViewById(R.id.sendButton);

        // Find the FrameLayout and Close Button
        FrameLayout chatgptResponseContainer = rootView.findViewById(R.id.chatgptresponse_container);
        ImageButton closeButton = rootView.findViewById(R.id.closeButton);

        // Set up a click listener for the close button
        closeButton.setOnClickListener(v -> {
            chatgptResponseContainer.setVisibility(View.GONE);
            symptomsInput.setEnabled(true); // Re-enable EditText
        });


        submitButton.setOnClickListener(view -> {
            // Disable the button to prevent multiple clicks
            submitButton.setEnabled(false);

            symptoms = symptomsInput.getText().toString().trim();

            if (symptoms.isEmpty()) {
                // Show a warning message if the symptoms field is empty
                Toast.makeText(getContext(), "Please enter symptoms.", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Symptoms entered: " + symptoms);

                if (getArguments() != null) {
                    String username = getArguments().getString("username");
                    Log.d(TAG, "Username passed to fragment: " + username);
                    if (username != null) {
                        fetchData(username, symptoms);
                    } else {
                        Log.e(TAG, "Username is null");
                        Toast.makeText(getContext(), "Username is missing.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Arguments are null");
                    Toast.makeText(getContext(), "No arguments provided.", Toast.LENGTH_SHORT).show();
                }
            }

            // Re-enable the button after processing
            submitButton.setEnabled(true);
        });

        return rootView;
    }

    private void fetchData(String username, String symptoms) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                User user = userSnapshot.getValue(User.class);

                                if (user != null) {
                                    Log.d(TAG, "User data retrieved: " + user.toString());


                                    String dateNaissance = user.getDateNaissance() != null ? user.getDateNaissance() : "N/A";
                                    String sexe = user.getSexe() != null ? user.getSexe() : "N/A";
                                    double taille = user.getTaille() != -1 ? user.getTaille() : -1;
                                    double poids = user.getPoids() != -1 ? user.getPoids() : -1;
                                    boolean handicape = user.isHandicape();
                                    boolean diabetique = user.isDiabetique();
                                    boolean allergies = user.isAllergies();
                                    String detailsAllergies = user.getDetailsAllergies() != null ? user.getDetailsAllergies() : "N/A";
                                    String groupeSanguin = user.getGroupeSanguin() != null ? user.getGroupeSanguin() : "N/A";

                                    String chatGptPrompt = generateChatGptPrompt(dateNaissance, sexe,
                                            taille, poids, handicape, diabetique, allergies,
                                            detailsAllergies, groupeSanguin, symptoms);

                                    callOpenAiApi(chatGptPrompt);
                                    Log.d(TAG, "ChatGPT prompt: " + chatGptPrompt);
                                } else {
                                    Log.e(TAG, "User object is null");
                                    Toast.makeText(getContext(), "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Log.d(TAG, "No user found with this username.");
                            Toast.makeText(getContext(), "Utilisateur non trouvé", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "Error reading data", databaseError.toException());
                        Toast.makeText(getContext(), "Error reading user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void callOpenAiApi(String prompt) {
        // Create a custom OkHttpClient with timeouts
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)  // Set connection timeout
                .readTimeout(20, TimeUnit.SECONDS)     // Set read timeout
                .writeTimeout(40, TimeUnit.SECONDS)    // Set write timeout
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("model", "gpt-4");
            jsonObject.put("messages", new JSONArray().put(new JSONObject()
                    .put("role", "user")
                    .put("content", prompt)
            ));

            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .header("Authorization", "Bearer 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")
                    .post(body)
                    .build();

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(TAG, "API call failed: " + e.getMessage());
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "API call failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.d(TAG, "API response: " + responseBody);

                        String result = extractApiResponse(responseBody);

                        getActivity().runOnUiThread(() -> {
                            // Set the response text
                            responseTextView.setText(result);

                            // Make the container (FrameLayout) visible
                            FrameLayout chatgptResponseContainer = rootView.findViewById(R.id.chatgptresponse_container);
                            chatgptResponseContainer.setVisibility(View.VISIBLE);
                            symptomsInput.setEnabled(false); // Disable EditText

                            // Optional: Add animations for a smoother appearance
                            chatgptResponseContainer.animate().alpha(1.0f).setDuration(300).start();
                        });

                    } else {
                        Log.e(TAG, "API response failed: " + response.message());
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "API response failed", Toast.LENGTH_SHORT).show();
                        });
                    }
                }

            });
        } catch (Exception e) {
            Log.e(TAG, "Error preparing API request: " + e.getMessage());
        }
    }

    private String extractApiResponse(String responseBody) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            JSONObject choice = choices.getJSONObject(0);
            String result = choice.getJSONObject("message").getString("content");
            Log.d(TAG, "Extracted result: " + result);
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error extracting response: " + e.getMessage());
            return "Error extracting response.";
        }
    }

    private String generateChatGptPrompt(String dateNaissance,
                                         String sexe, double taille, double poids, boolean handicape, boolean diabetique,
                                         boolean allergies, String detailsAllergies, String groupeSanguin, String symptoms) {
        return "Informations personnelles :\n" +
                "Date de naissance : " + dateNaissance + "\n" +
                "Sexe : " + sexe + "\n" +
                "Taille : " + taille + " cm\n" +
                "Poids : " + poids + " kg\n" +
                "Handicapé : " + (handicape ? "Oui" : "Non") + "\n" +
                "Diabétique : " + (diabetique ? "Oui" : "Non") + "\n" +
                "Allergies : " + (allergies ? "Oui" : "Non") + "\n" +
                (allergies ? "Détails de l'allergie : " + detailsAllergies + "\n" : "") +
                "Groupe sanguin : " + groupeSanguin + "\n" +
                "Symptômes : " + symptoms + "\n" +
                "Tâche :\n" +
                "1. Suggérer des maladies possibles.\n" +
                "2. Recommander des médicaments (si applicable).\n" +
                "3. Suggérer la spécialité du médecin à consulter.";

    }
}