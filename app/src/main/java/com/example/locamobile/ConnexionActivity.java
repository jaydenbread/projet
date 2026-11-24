package com.example.locamobile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.locamobile.reseau.ClientApi;
import com.example.locamobile.reseau.ConnexionRequete;
import com.example.locamobile.reseau.Profil;
import com.example.locamobile.reseau.Session;
import com.example.locamobile.reseau.TokenReponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnexionActivity extends AppCompatActivity {

    private EditText champCourriel, champMotDePasse;
    private TextView resultat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        champCourriel = findViewById(R.id.champCourriel);
        champMotDePasse = findViewById(R.id.champMotDePasse);
        Button boutonSeConnecter = findViewById(R.id.boutonSeConnecter);
        resultat = findViewById(R.id.resultat);

        boutonSeConnecter.setOnClickListener(v -> tenterConnexion());
    }

    private void tenterConnexion() {
        String courriel = champCourriel.getText().toString().trim();
        String motDePasse = champMotDePasse.getText().toString().trim();

        if (courriel.isEmpty()) {
            champCourriel.setError("Courriel obligatoire");
            return;
        }
        if (motDePasse.isEmpty()) {
            champMotDePasse.setError("Mot de passe obligatoire");
            return;
        }

        ClientApi.get().connecter(new ConnexionRequete(courriel, motDePasse)).enqueue(new Callback<TokenReponse>() {
            @Override
            public void onResponse(Call<TokenReponse> call, Response<TokenReponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TokenReponse tr = response.body();
                    Session.enregistrer(ConnexionActivity.this, tr.token, tr.nom);
                    chargerProfil(tr.token);
                } else {
                    try {
                        String erreurBrute = response.errorBody() != null ? response.errorBody().string() : "";
                        JsonObject json = new Gson().fromJson(erreurBrute, JsonObject.class);
                        String msg = json.get("message").getAsString();
                        resultat.setText("Erreur " + response.code() + " : " + msg);
                    } catch (Exception e) {
                        resultat.setText("Erreur " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<TokenReponse> call, Throwable t) {
                resultat.setText("Serveur injoignable : " + t.getMessage());
            }
        });
    }

    private void chargerProfil(String jeton) {
        ClientApi.get().monProfil("Bearer " + jeton).enqueue(new Callback<Profil>() {
            @Override
            public void onResponse(Call<Profil> call, Response<Profil> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Profil p = response.body();
                    resultat.setText("Code HTTP : " + response.code()
                            + "\nNom : " + p.nom
                            + "\nRôle : " + p.role
                            + "\nCourriel : " + p.courriel);
                } else {
                    resultat.setText("Erreur profil HTTP : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Profil> call, Throwable t) {
                resultat.setText("Serveur injoignable : " + t.getMessage());
            }
        });
    }
}