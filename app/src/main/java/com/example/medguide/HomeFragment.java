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
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Handler;
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
    private TextView responseTextView, loadingAnimation,question;
    private FrameLayout chatgptResponseContainer;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        symptomsInput = rootView.findViewById(R.id.symptomsInput);
        responseTextView = rootView.findViewById(R.id.responseTextView);
        submitButton = rootView.findViewById(R.id.sendButton);
        chatgptResponseContainer = rootView.findViewById(R.id.chatgptresponse_container);
        loadingAnimation = rootView.findViewById(R.id.loadingAnimation);
        question = rootView.findViewById(R.id.questionText);
        ImageButton closeButton = rootView.findViewById(R.id.closeButton);

        closeButton.setOnClickListener(v -> {
            chatgptResponseContainer.setVisibility(View.GONE);
            symptomsInput.setEnabled(true); // Re-enable EditText
        });

        submitButton.setOnClickListener(view -> {
            submitButton.setEnabled(false);
            symptoms = symptomsInput.getText().toString().trim();

            if (symptoms.isEmpty()) {
                Toast.makeText(getContext(), "Please enter symptoms.", Toast.LENGTH_SHORT).show();
                submitButton.setEnabled(true);
                return;
            }

            if (getArguments() != null) {
                String username = getArguments().getString("username");
                if (username != null) {
                    fetchData(username, symptoms);
                } else {
                    Toast.makeText(getContext(), "Username is missing.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "No arguments provided.", Toast.LENGTH_SHORT).show();
            }
            submitButton.setEnabled(true);
        });

        return rootView;
    }

    private Handler handler;
    private Runnable loadingRunnable;

    private void animateLoadingDots(TextView loadingTextView) {
        Log.d(TAG, "Starting loading animation...");
        handler = new Handler();
        final String[] dotsArray = { "", ".", "..", "..." };
        final int[] index = { 0 };

        loadingRunnable = new Runnable() {
            @Override
            public void run() {
                loadingTextView.setText(dotsArray[index[0]]);
                Log.d(TAG, "Loading animation: " + dotsArray[index[0]]);
                index[0] = (index[0] + 1) % dotsArray.length; // Cycle through the dots
                handler.postDelayed(this, 500); // Update every 500ms
            }
        };

        handler.post(loadingRunnable);
    }


    private void stopAnimation(TextView loadingTextView) {
        if (handler != null && loadingRunnable != null) {
            handler.removeCallbacks(loadingRunnable); // Stop the animation
        }
        loadingTextView.setVisibility(View.GONE); // Hide the loading view after stopping
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
                .connectTimeout(30, TimeUnit.SECONDS)  // Set connection timeout
                .readTimeout(30, TimeUnit.SECONDS)     // Set read timeout
                .writeTimeout(30, TimeUnit.SECONDS)    // Set write timeout
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

            loadingAnimation = rootView.findViewById(R.id.loadingAnimation);
            loadingAnimation.setVisibility(View.VISIBLE);
            animateLoadingDots(loadingAnimation);

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(TAG, "API call failed: " + e.getMessage());
                    getActivity().runOnUiThread(() -> {
                        stopAnimation(loadingAnimation);
                        Toast.makeText(getContext(), "API call failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        getActivity().runOnUiThread(() -> {
                            stopAnimation(loadingAnimation);
                            chatgptResponseContainer.setVisibility(View.VISIBLE);
                            responseTextView.setText(""); // Clear any existing content

                            // Use extractApiResponse to process the response
                            String[] extractedContent = extractApiResponse(responseBody);

                            // Display the extracted content one by one with a delay
                            Handler handler = new Handler();
                            question.setVisibility(View.GONE);
                            for (int i = 0; i < extractedContent.length; i++) {
                                String content = extractedContent[i];
                                handler.postDelayed(() -> {
                                    responseTextView.append(content + "\n\n");
                                }, i * 500); // Delay of 0.5 second per line or paragraph
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(() -> {
                            stopAnimation(loadingAnimation);
                            Toast.makeText(getContext(), "API response failed", Toast.LENGTH_SHORT).show();
                        });
                    }
                }




            });
        } catch (Exception e) {
            Log.e(TAG, "Error preparing API request: " + e.getMessage());
        }
    }

    private String[] extractApiResponse(String responseBody) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            JSONObject choice = choices.getJSONObject(0);
            String content = choice.getJSONObject("message").getString("content");
            Log.d(TAG, "Extracted content: " + content);

            // Split the content into lines or items
            return content.split("\\n\\s*\\d+\\.\\s*");
        } catch (Exception e) {
            Log.e(TAG, "Error extracting response: " + e.getMessage());
            return new String[] {"Error extracting response."};
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
                "Suggérer des maladies possibles.\n" +
                "Recommander des médicaments (si applicable).\n" +
                "Suggérer la spécialité du médecin à consulter.";

    }
}