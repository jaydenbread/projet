package com.example.locamobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.locamobile.reseau.ClientApi;
import com.example.locamobile.reseau.logement;

import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListeActivity extends AppCompatActivity {

    private LinearLayout conteneurLogements;
    private TextView messageErreur;
    private EditText champVille;
    private Button boutonChercher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Garde R.layout.activity_main si ton fichier s'appelle encore activity_main.xml
        setContentView(R.layout.activity_main);

        conteneurLogements = findViewById(R.id.conteneurLogements);
        messageErreur = findViewById(R.id.messageErreur);
        champVille = findViewById(R.id.champVille);
        boutonChercher = findViewById(R.id.boutonChercher);
        Button boutonConnexion = findViewById(R.id.boutonConnexion);
        boutonConnexion.setOnClickListener(v ->
                startActivity(new Intent(ListeActivity.this, ConnexionActivity.class))
        );

        boutonChercher.setOnClickListener(v -> lancerRecherche());
    }

    @Override
    protected void onResume() {
        super.onResume();
        lancerRecherche();
    }

    private void lancerRecherche() {
        String ville = champVille.getText().toString().trim();
        Call<List<logement>> appel;

        if (ville.isEmpty()) {
            appel = ClientApi.get().listerLogements();
        } else {
            appel = ClientApi.get().chercherParVille(ville);
        }

        appel.enqueue(new Callback<List<logement>>() {
            @Override
            public void onResponse(Call<List<logement>> call, Response<List<logement>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<logement> liste = response.body();
                    if (liste.isEmpty() && !ville.isEmpty()) {
                        afficherErreur("Aucun logement à " + ville + ".");
                    } else {
                        afficher(liste);
                    }
                } else {
                    afficherErreur("Erreur HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<logement>> call, Throwable t) {
                afficherErreur("Serveur injoignable : " + t.getMessage());
            }
        });
    }

    private void afficher(List<logement> logements) {
        messageErreur.setVisibility(View.GONE);
        conteneurLogements.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (logement l : logements) {
            View carte = inflater.inflate(R.layout.item_logement, conteneurLogements, false);

            TextView carteTitre = carte.findViewById(R.id.carteTitre);
            TextView carteVille = carte.findViewById(R.id.carteVille);
            TextView cartePrix = carte.findViewById(R.id.cartePrix);

            carteTitre.setText(l.titre);
            carteVille.setText(l.ville);
            cartePrix.setText(String.format(Locale.CANADA_FRENCH, "%.2f $ / nuit", l.prixParNuit));

            carte.setOnClickListener(v -> {
                Intent intent = new Intent(ListeActivity.this, DetailActivity.class);
                intent.putExtra(DetailActivity.CLE_ID, l.id.intValue());
                startActivity(intent);
            });

            conteneurLogements.addView(carte);
        }
    }

    private void afficherErreur(String texte) {
        conteneurLogements.removeAllViews();
        messageErreur.setText(texte);
        messageErreur.setVisibility(View.VISIBLE);
    }
}