package com.example.medguide;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class shareFragment extends Fragment {



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_share, container, false);

        // Find the share button using the view
        Button shareButton = view.findViewById(R.id.share_button);

        // Set the onClickListener for the share button
        shareButton.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareMessage = "\uD83C\uDF1F Découvrez MedGuide : votre guide médical au quotidien ! \uD83C\uDF1F\n" +
                    "\n" +
                    "Avec MedGuide, accédez facilement à :\n" +
                    "✔️ Vos informations médicales personnelles organisées au même endroit.\n" +
                    "✔️ Des conseils pour mieux gérer votre santé et celle de vos proches.\n" +
                    "✔️ Une interface simple et intuitive adaptée à tous.\n" +
                    "\n" +
                    "\uD83D\uDCF2 Téléchargez dès maintenant MedGuide et prenez soin de votre santé en toute simplicité !\n" +
                    "\n" +
                    "Pour en savoir plus ou obtenir l’application, contactez-nous directement. \uD83D\uDCAC";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

            startActivity(Intent.createChooser(shareIntent, "Share MedGuide via"));
        });

        return view;
    }

}