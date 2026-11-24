package com.example.locamobile;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.locamobile.reseau.ClientApi;
import com.example.locamobile.reseau.logement;

import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    public static final String CLE_ID = "id";

    private TextView etat;
    private LinearLayout blocDetail;
    private TextView detailTitre, detailVille, detailPrix, detailVoyageurs, detailProprietaire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        etat = findViewById(R.id.etat);
        blocDetail = findViewById(R.id.blocDetail);
        detailTitre = findViewById(R.id.detailTitre);
        detailVille = findViewById(R.id.detailVille);
        detailPrix = findViewById(R.id.detailPrix);
        detailVoyageurs = findViewById(R.id.detailVoyageurs);
        detailProprietaire = findViewById(R.id.detailProprietaire);

        int id = getIntent().getIntExtra(CLE_ID, -1);
        chargerDetail(id);
    }

    private void chargerDetail(int id) {
        ClientApi.get().trouverLogement(id).enqueue(new Callback<logement>() {
            @Override
            public void onResponse(Call<logement> call, Response<logement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    logement l = response.body();
                    etat.setVisibility(View.GONE);
                    blocDetail.setVisibility(View.VISIBLE);

                    detailTitre.setText(l.titre);
                    detailVille.setText(l.ville);
                    detailPrix.setText(String.format(Locale.CANADA_FRENCH, "%.2f $ / nuit", l.prixParNuit));
                    detailVoyageurs.setText("Capacité : " + l.nbVoyageurs + " voyageur(s)");
                    detailProprietaire.setText("Propriétaire : " + l.proprietaireNom);
                } else if (response.code() == 404) {
                    afficherEtat("Ce logement n'existe plus (404).");
                } else {
                    afficherEtat("Erreur HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<logement> call, Throwable t) {
                afficherEtat("Serveur injoignable : " + t.getMessage());
            }
        });
    }

    private void afficherEtat(String message) {
        blocDetail.setVisibility(View.GONE);
        etat.setText(message);
        etat.setVisibility(View.VISIBLE);
    }
}