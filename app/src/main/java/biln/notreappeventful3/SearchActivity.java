package biln.notreappeventful3;


import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

//version originale de Nassym: extends FragmentActivity

public class SearchActivity extends MyMenu implements View.OnClickListener, SelecteurDateDebut.ListenerDateDebut, SelecteurDateFin.ListenerDateFin {


    //déclaration des EditText
    EditText edit_text_date_debut ;

    EditText edit_text_date_fin ;
    EditText edit_text_lieu ;

    //declaration des TextView
    TextView rechercher_date ;
    TextView rechercher_lieu ;
    TextView rechercher_categories ;

    ImageButton bouton_concert ;
    ImageButton bouton_musee ;
    ImageButton bouton_loisirs ;
    ImageButton bouton_nightlife;
    ImageButton bouton_sport ;
    ImageButton bouton_gastronomie ;
    ImageButton bouton_sciences ;
    ImageButton bouton_films ;

    Date debut ;
    Date fin ;
    ArrayList<String> categories;

    DBHelper dbh;
    SQLiteDatabase db;
    //format de l'Edit text choisi
    DateFormat format_chaine = new SimpleDateFormat("dd MMM yyyy");
    //format du query pour l'API web
    DateFormat queryFormat = new SimpleDateFormat("yyyyMMdd00");

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText("Rechercher:");
        categories = new ArrayList();

        //récupération des elements du layout dans des variables java
        edit_text_date_debut = (EditText) findViewById(R.id.editText_date_debut);
        edit_text_date_fin = (EditText) findViewById(R.id.editText_date_fin);
        edit_text_lieu = (EditText) findViewById(R.id.editText_lieu);

        rechercher_date = (TextView) findViewById(R.id.rechercher_date);
        rechercher_lieu = (TextView) findViewById(R.id.rechercher_lieu);
        rechercher_categories = (TextView) findViewById(R.id.rechercher_categories);

        bouton_concert = (ImageButton)findViewById(R.id.bouton_concert);
        bouton_musee = (ImageButton)findViewById(R.id.bouton_musee) ;
        bouton_films = (ImageButton)findViewById(R.id.bouton_film);
        bouton_nightlife= (ImageButton)findViewById(R.id.bouton_nightlife);
        bouton_sport = (ImageButton)findViewById(R.id.bouton_sport) ;
        bouton_gastronomie = (ImageButton)findViewById(R.id.bouton_gastronomie);
        bouton_sciences = (ImageButton)findViewById(R.id.bouton_sciences) ;
        bouton_loisirs = (ImageButton) findViewById(R.id.bouton_loisirs) ;

        bouton_concert.setOnClickListener(this);
        bouton_sport.setOnClickListener(this);
        bouton_musee.setOnClickListener(this);
        bouton_films.setOnClickListener(this);
        bouton_nightlife.setOnClickListener(this);
        bouton_gastronomie.setOnClickListener(this);
        bouton_loisirs.setOnClickListener(this);
        bouton_sciences.setOnClickListener(this);
        edit_text_date_debut.setOnClickListener(this);
        edit_text_date_fin.setOnClickListener(this);

        //rendre EditTextDebut inchangeable par utilisateur - seulement par bouton créer à cet effet
        edit_text_date_debut.setKeyListener(null);

        //rendre EditTextFin inchangeable par utilisateur - seulement par bouton créer à cet effet
        edit_text_date_fin.setKeyListener(null);

        //Log.d("Creati", "Tout declare") ;


        //SELECTION DATE

        // affichage de la date courante dans le EditText, par défaut
        Calendar calendrier = Calendar.getInstance();

        //mise de la date dans champs editText_debut comme date courante
        try {
            edit_text_date_debut.setHint(R.string.date_debut);
            edit_text_date_fin.setHint(R.string.date_fin);

        } catch (Exception e) {
            Log.d("FORMATTAGE EDIT TEXT", "Erreur dans la définition du format de l'EditText. Message d'erreur : "+ e.getMessage()) ;
        }
    }


    public void affichage_datepicker_debut(View v) {
        //creation d'une boite de dialogue pour la date de début avec le selecteur de date (fragment)


        SelecteurDateDebut mon_fragment =  SelecteurDateDebut.nouvelleInstanceSelecteurDateDebut(this) ;

        //affichage du fragment de date d'ID unique FRAGMENT_SELECTEUR_DATE
        mon_fragment.show(getFragmentManager(), "FRAGMENT_SELECTEUR_DATE");
        Log.d("FRAGMENT", "creation du fragment de début");

    }

    @Override
    public void onDateSet(int ID_SELECTEUR, Date date) {
        // Cette méthode est appellée depuis le Fragment Date Picker, avec la date que l'utilisateur a choisi

        SimpleDateFormat format_chaine = new SimpleDateFormat("dd MMM yyyy");
        // récupération de date mise sous une chaine de caractères
        String date_to_string = format_chaine.format(date);

        if (ID_SELECTEUR ==1) {

            edit_text_date_debut.setText(date_to_string);
            Log.d("SELECTEUR DEBUT", "champs début : " + edit_text_date_debut.getText().toString());
        }
        else{
            edit_text_date_fin.setText(date_to_string);
            Log.d("SELECTEUR FIN", "champs fin : " + edit_text_date_fin.getText().toString());
        }

    }

    public void affichage_datepicker_fin(View v) {
        //creation d'une boite de dialogue pour la date de fin avec le selecteur de date

        SelecteurDateFin mon_fragment_fin =  SelecteurDateFin.nouvelleInstanceSelecteurDateFin(this) ;

        //affichage du fragment de date d'ID unique FRAGMENT_SELECTEUR_DATE
        mon_fragment_fin.show(getFragmentManager(), "FRAGMENT_SELECTEUR_DATE");
        Log.d("FRAGMENT", "creation du fragment de fin");
    }

//selection catégories

    //initialisation des compteurs pour chaque bouton (comptent le nombre de fois que l'on a cliqué le bouton, pour pouvoir selectionner/deselectionner
    int compteur_concert=0, compteur_musee=0, compteur_films=0, compteur_loisirs=0,
            compteur_sport=0, compteur_sciences=0, compteur_gastronomie=0, compteur_nightlife=0;

    // apelée quznd l'usager presse le bouton de la catégorie "musique et concert"
    public void bouton_concert(View view) {

        // ce qu'on fait (cf avec query ce que représente choix de cette catégorie)
        Log.d("SELECTION CATEGORIES", "Valeur de compteur_concert = "+compteur_concert);
        if (compteur_concert%2 ==0) {
            Log.d("Bouton concert", " compteur_concert="+compteur_concert);
            bouton_concert.setImageResource(R.drawable.bouton_concert_appuye);
            categories.add("music");
        }

        else {
            bouton_concert.setImageResource(R.drawable.bouton_concert_non_appuye);
            categories.remove("music");
        }

        compteur_concert++ ;
    }

    // apelée quznd l'usager presse le bouton de la catégorie "films et cinema"
    public void bouton_films(View view) {
        // ce qu'on fait (cf avec query ce que représente choix de cette catégorie)
        Log.d("SELECTION CATEGORIES", "Valeur de compteur_films = "+compteur_films);
        if (compteur_films%2 ==0 ) {
            bouton_films.setImageResource(R.drawable.bouton_film_appuye);
            categories.add("movies_film");
        }

        else {
            bouton_films.setImageResource(R.drawable.bouton_film_non_appuye);
            categories.remove("movies_film");
        }

        compteur_films++ ;


    }

    // apelée quznd l'usager presse le bouton de la catégorie "musee"
    public void bouton_musee(View view) {
        // ce qu'on fait (cf avec query ce que représente choix de cette catégorie)
        Log.d("SELECTION CATEGORIES", "Valeur de compteur_musee = "+compteur_musee);

        if (compteur_musee%2 ==0 ) {
            bouton_musee.setImageResource(R.drawable.bouton_musee_appuye);
            categories.add("art");
        }

        else{
            bouton_musee.setImageResource(R.drawable.bouton_musee_non_appuye);
            categories.remove("art");
        }

        compteur_musee++ ;
    }


    // apelée quand l'usager presse le bouton de la catégorie "loisirs"
    public void bouton_loisirs(View view) {
        // ce qu'on fait (cf avec query ce que représente choix de cette catégorie)

        Log.d("SELECTION CATEGORIES", "Valeur de compteur_loisirs = "+compteur_loisirs);

        if (compteur_loisirs%2 ==0 ) {
            bouton_loisirs.setImageResource(R.drawable.bouton_loisirs_appuye);
            categories.add("outdoors_recreation");
        }

        else {
            bouton_loisirs.setImageResource(R.drawable.bouton_loisirs_non_appuye);
            categories.remove("outdoors_recreation");
        }

        compteur_loisirs++ ;

    }

    // apelée quznd l'usager presse le bouton de la catégorie "sciences"
    public void bouton_sciences(View view) {
        // ce qu'on fait (cf avec query ce que représente choix de cette catégorie)

        Log.d("SELECTION CATEGORIES", "Valeur de compteur_sciences = "+compteur_sciences);

        if (compteur_sciences%2 ==0 ) {
            bouton_sciences.setImageResource(R.drawable.bouton_sciences_appuye);
            categories.add("science");
        }

        else {
            bouton_sciences.setImageResource(R.drawable.bouton_sciences_non_appuye);
            categories.remove("science");
        }

        compteur_sciences++ ;


    }

    // apelée quznd l'usager presse le bouton de la catégorie "sport"
    public void bouton_sport(View view) {
        // ce qu'on fait (cf avec query ce que représente choix de cette catégorie)

        Log.d("SELECTION CATEGORIES", "Valeur de compteur_sport = "+compteur_sport);
        if (compteur_sport%2 ==0 ) {
            bouton_sport.setImageResource(R.drawable.bouton_sport_appuye);
            categories.add("sports");
        }

        else {
            bouton_sport.setImageResource(R.drawable.bouton_sport_non_appuye);
            categories.remove("sports");
        }

        compteur_sport++ ;


    }

    // apelée quznd l'usager presse le bouton de la catégorie "gastronomie"
    public void bouton_gastronomie(View view) {
        // ce qu'on fait (cf avec query ce que représente choix de cette catégorie)
        Log.d("SELECTION CATEGORIES", "Valeur de compteur_gastronomie = "+compteur_gastronomie);
        if (compteur_gastronomie %2 ==0 ) {
            bouton_gastronomie.setImageResource(R.drawable.bouton_gastronomie_appuye);
            categories.add("food");
        }

        else {
            bouton_gastronomie.setImageResource(R.drawable.bouton_gastronomie_non_appuye);
            categories.remove("food");
        }

        compteur_gastronomie++ ;
    }

    // apelée quznd l'usager presse le bouton de la catégorie "vie nocturne"
    public void bouton_nightlife(View view) {
        // ce qu'on fait (cf avec query ce que représente choix de cette catégorie)
        Log.d("SELECTION CATEGORIES", "Valeur de compteur_nightlife = "+compteur_nightlife);

        if (compteur_nightlife%2 ==0 ) {
            bouton_nightlife.setImageResource(R.drawable.bouton_fete_appuye);
            categories.add("festivals_parades");
        }

        else {
            bouton_nightlife.setImageResource(R.drawable.bouton_fete_non_appuye);
            categories.remove("festivals_parades");
        }

        compteur_nightlife++ ;
    }


    // apelée quand l'usager presse le bouton "valider le filtrage"
    public void validation_filtrage(View view) {

        Boolean debutHasBeenEntered = true;
        Boolean finHasBeenEntered = true;

        String dateS = "";
        String dateT = "";

        try {
            debut = format_chaine.parse(edit_text_date_debut.getText().toString()) ;
            Log.d("Date debut:", " "+debut);
        } catch (ParseException e) {
            debutHasBeenEntered = false;
            e.printStackTrace();
        }


        try {
            fin = format_chaine.parse(edit_text_date_fin.getText().toString()) ;
            Log.d("Date fin:",  " "+fin);
        } catch (ParseException e) {
            finHasBeenEntered = false;
            e.printStackTrace();
        }

        //Si l'usager a bien entré les deux dates, on les traite dans la requête
        if (debutHasBeenEntered && finHasBeenEntered) {
            if (fin.compareTo(debut) < 0) {
                Toast.makeText(getApplicationContext(), R.string.message_toast_erreurs_dates, Toast.LENGTH_LONG).show();
            }
            else{
                //cas non pathologique
                dateS = queryFormat.format(debut);
                dateT = queryFormat.format(fin);
                Log.d("DATES:", " "+dateS+" "+dateT);
            }
        }
        //Si l'usager n'a pas entré les deux dates nécessaires, on n'utilisera aucune date dans le query
        else {
            Log.d("DATES:", "Au moins une date n'a pas été entrée. Recherche temporelle pour le futur");
        }

        Log.d("DATES: ", " " + dateS + " "+dateT);
        for(String cat : categories){
            Log.d("CATEGORIES: ", " "+cat);
        }


        dbh = new DBHelper(this);
        db = dbh.getWritableDatabase();
        dbh.cleanLastAdvancedSearch(db);


        Intent intent = new Intent(this, SearchResults.class);
        Bundle b = new Bundle();
        b.putInt("Called from Search Activity", 1);
        b.putString("dateS", dateS); // dateS = yyyyMMdd00 ou dateS = ""
        b.putString("dateT", dateT); // dateT = yyyyMMdd00 ou dateT = ""
        b.putStringArrayList("categories", categories);
        intent.putExtras(b);
        startActivity(intent);


    }




    @Override
    public void onClick(View view) {


        if (view.getId() == R.id.editText_date_debut) {
            this.affichage_datepicker_debut(view);

        }

        if (view.getId() == R.id.editText_date_fin) {
            this.affichage_datepicker_fin(view);

        }

        if (view.getId() == R.id.bouton_concert) {
            this.bouton_concert(view);

        }

        if (view.getId() == R.id.bouton_musee) {
            this.bouton_musee(view);

        }

        if (view.getId() == R.id.bouton_loisirs) {
            this.bouton_loisirs(view);

        }

        if (view.getId() == R.id.bouton_nightlife) {
            this.bouton_nightlife(view);

        }

        if (view.getId() == R.id.bouton_sport) {
            this.bouton_sport(view);

        }


        if (view.getId() == R.id.bouton_gastronomie) {
            this.bouton_gastronomie(view);

        }


        if (view.getId() == R.id.bouton_sciences) {
            this.bouton_sciences(view);

        }

        if (view.getId() == R.id.bouton_film) {
            this.bouton_films(view);

        }


        if (view.getId() == R.id.validation_filtrage) {
            this.validation_filtrage(view);

        }


    }




}

