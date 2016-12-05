package com.example.mael.yogappli;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnPref = (Button) findViewById(R.id.btnPref);
        btnPref.setOnClickListener(observateurclic);

        Button btnFile = (Button) findViewById(R.id.btnFile);
        btnFile.setOnClickListener(observateurclic);

        Button btnSqlite = (Button) findViewById(R.id.btnSqlite);
        btnSqlite.setOnClickListener(observateurclic);

        Button btnVoirPref = (Button) findViewById(R.id.btnVoirPref);
        btnVoirPref.setOnClickListener(observateurclic);

        Button btnVoirSqlite = (Button) findViewById(R.id.btnVoirSqlite);
        btnVoirSqlite.setOnClickListener(observateurclic);

        Button btnVoirFile = (Button) findViewById(R.id.btnVoirFille);
        btnVoirFile.setOnClickListener(observateurclic);

        Button btnImport = (Button) findViewById(R.id.btnImport);
        btnImport.setOnClickListener(observateurclic);





    }

    public View.OnClickListener observateurclic = new View.OnClickListener() {
        public void onClick(View v) {
            String posture = String.valueOf(((EditText) findViewById(R.id.txtposture)).getText());
            int nbResp = Integer.parseInt(((EditText) findViewById(R.id.txtnbrepresentation)).getText().toString());


            switch (v.getId()) {
                case R.id.btnFile:
                    enchainement exo = new enchainement(posture, nbResp);
                    Gson gson = new Gson();
                    try {
                        FileOutputStream fic;
                        fic = getApplicationContext().openFileOutput("monFichier.txt", Context.MODE_APPEND);
                        String str = gson.toJson(exo) + "\n";
                        fic.write(str.getBytes());
                        fic.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(),
                            "Enregisterment effectué ", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.btnPref:
                  /*  exo = new enchainement(posture, nbResp);
                    SharedPreferences mesPrefs;
                    mesPrefs = getApplicationContext().getSharedPreferences("mesVarGlobales", 0);
                    SharedPreferences.Editor monEditeurDePreferences = mesPrefs.edit();
                    int nb = mesPrefs.getInt("nbEnchainements", 0);
                    nb++;
                    monEditeurDePreferences.putInt("nbEnchainements", nb);
                    gson = new Gson();
                    monEditeurDePreferences.putString(String.valueOf(nb), gson.toJson(exo));
                    monEditeurDePreferences.apply();
                    Toast.makeText(getApplicationContext(),
                            "Enregisterment effectué ",
                            Toast.LENGTH_LONG).show();
                    break;
                    */
                    ArrayList<enchainement> list;
                    SharedPreferences mesPrefs;
                    mesPrefs = getApplicationContext().getSharedPreferences("maSeanceDeYoga", 0);
                    SharedPreferences.Editor monEditeur = mesPrefs.edit();
                    gson = new Gson();
                    String str=mesPrefs.getString("liste", "");
                    if(str.equals("")){
                        list = new ArrayList<>();
                    }
                    else {
                        list = gson.fromJson(str, new TypeToken<ArrayList<enchainement>>(){}.getType());
                    }
                    enchainement.setTousLesEnchainements(list);
                    enchainement.ajouteUnenchainement(posture, nbResp);
                    monEditeur.putString("liste", gson.toJson(enchainement.tousLesEnchainements));
                    monEditeur.apply();
                case R.id.btnSqlite:
                    BaseSqlite maBD = new BaseSqlite(getApplicationContext());
                    SQLiteDatabase db = maBD.getWritableDatabase();
                    String insertEnchainement = "INSERT INTO enchainement(posture,nbResp) VALUES('" + posture + "', " + nbResp + ");";
                    db.execSQL(insertEnchainement);
                    break;
                case R.id.btnVoirFille:
                    Intent i;
                    i = new Intent(getApplicationContext(),results.class);
                    i.putExtra("choixMethode", "File");
                    startActivity(i);
                    break;
                case R.id.btnVoirPref:
                    Intent y;
                    y = new Intent(getApplicationContext(),results.class);
                    y.putExtra("choixMethode","Prefs");
                    startActivity(y);
                    break;
                case R.id.btnVoirSqlite:
                    Intent x;
                    x = new Intent(getApplicationContext(),results.class);
                    x.putExtra("choixMethode","SQLite");
                    startActivity(x);
                    break;
                case R.id.btnImport:
                    Spinner spin = (Spinner) findViewById(R.id.spinnerPostures);
                    Importation tacheImport = new Importation();
                    tacheImport.execute("http://10.0.3.2/yogappli/import.php");
                    try {
                        ArrayList<enchainement> listeImportee = tacheImport.get();
                        if(listeImportee!=null){
                            EnchainementAdapter adapter;
                            adapter=new EnchainementAdapter(getApplicationContext(), listeImportee);
                            spin.setAdapter(adapter);
                        }
                        else{
                            Log.i("Parseur","Problème lors de la lecture du fichier");
                        }
                    } catch (InterruptedException e) {
                        Log.i("Parseur", "Interruption lecture fichier"+e.getMessage());
                    } catch (ExecutionException e) {
                        Log.i("Parseur", "Erreur execution" + e.getMessage());
                    }
                    break;




//traitements
            }

        }
    };
}






