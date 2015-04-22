package biln.notreappeventful3;


import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Boris on 2015-03-10.
 */
class EventfulAPI {

    ArrayList<Event> eventsFound;                                          //peuplé par la recherche
    String apiKey = "b5JxXhsHhJTW2mzP";
    String url = "http://api.eventful.com/json/events/search?app_key="+apiKey;



    public EventfulAPI(){
        eventsFound = new ArrayList<Event>();
    }


    /**
     * Trouve les événements qui s'en viennent dans la ville donnée en paramètre
     *
     * @param city
     */


    public void getNextEvents(String city){
        String query = url+"&location="+city+"&sort_order=date"+"&page_size=100"; //+"c="
        //nécessaire de faire sort_order par popularity car sinon les pages d'Eventful sont folles
        getEvents(query);
    }

    public void getDesiredResults(String city, String dateStart, String dateStop, ArrayList<String> categories){

        List<NameValuePair> query = new ArrayList<NameValuePair>();
        //On ajoute le champs clé d'API
        query.add(new BasicNameValuePair("app_key", this.apiKey));

        //On ajoute le champ location
        query.add(new BasicNameValuePair("location", city));


        //On ajoute le champs time
        String time;
        if(dateStart == "" || dateStop == ""){
            time = "future";
        }
        else{
            time = dateStart + "-" + dateStop;
        }
        query.add(new BasicNameValuePair("t", time));

        //On ajoute le champs category
        String catList;
        if(categories.size() > 1) {
            catList = "";
            for (int i = 0; i<categories.size(); i++) {
                catList = catList + categories.get(i);
                if(i < categories.size() - 1)
                    catList = catList +  ",";
            }
            query.add(new BasicNameValuePair("category", catList));
        }else if(categories.size() == 1){
            catList = categories.get(0);
            query.add(new BasicNameValuePair("category", catList));
        }

        //On construit l'URL avec les champs
        String url = "http://api.eventful.com/json/events/search?" + URLEncodedUtils.format(query, HTTP.UTF_8);
        Log.d("ENCODAGE DE L'URL", url);

        //On fait l'appel de la page et la récolte
        getEvents(url);
        Log.d("EVENTFUL API, url : ", "" + url);
    }


    /**
     * Méthode utilitaire locale
     *
     * @param query
     */
    private void getEvents(String query){
        int page_count = 0;
        try{
            HttpEntity page = getHttp(query);
            JSONObject js = new JSONObject(EntityUtils.toString(page, HTTP.UTF_8));
            page_count = Integer.parseInt(js.getString("page_count"));
        } catch (ClientProtocolException e) {
            Log.d("HTTP ","Erreur: "+e.getMessage());
        } catch (IOException e) {
            Log.d("Web ","Erreur: "+e.getMessage());
        } catch (ParseException e) {
            Log.d("Parse ","Erreur: "+e.getMessage());
        } catch (JSONException e) {
            Log.d("JSON ","Erreur: "+e.getMessage());
        }
        //int i = 0;
        int i =  page_count - 1; //TODO ! Juste pour abréger ici on demande une seule page
        while(i < page_count) {
            try {
                HttpEntity page = getHttp(query+"&page_number="+Integer.toString(i+1));
                JSONObject js = new JSONObject(EntityUtils.toString(page, HTTP.UTF_8));
                JSONObject events = js.getJSONObject("events");
                JSONArray event = events.getJSONArray("event");
                Log.d("WEB", "Nombre d'événements: " + event.length());

                for (int j = 0; j < event.length(); j++) {
                    JSONObject item = event.getJSONObject(j);
                    //Si la valeur sous "stop_time" est null
                    if (item.isNull("stop_time")) {
                        eventsFound.add(new Event(item.getString("id"),
                                                    item.getString("title"),
                                                        item.getString("start_time"),
                                                            "2030-01-01",//TODO Important
                                                                item.getString("city_name"),
                                                                    item.getString("description")));
                    } else {
                        eventsFound.add(new Event(item.getString("id"),
                                                    item.getString("title"),
                                                        item.getString("start_time"),
                                                            item.getString("stop_time"),
                                                                item.getString("city_name"),
                                                                    item.getString("description")));
                    }
                }

            } catch (ClientProtocolException e) {
                Log.d("HTTP ", "Erreur: " + e.getMessage());
            } catch (IOException e) {
                Log.d("Web ", "Erreur: " + e.getMessage());
            } catch (ParseException e) {
                Log.d("Parse ", "Erreur: " + e.getMessage());
            } catch (JSONException e) {
                Log.d("JSON ", "Erreur: " + e.getMessage());
            }
            i++;
        }
    }




    public ArrayList<String> getEventDetails(String id)
    {
        List<NameValuePair> query = new ArrayList<NameValuePair>();

        query.add(new BasicNameValuePair("app_key", this.apiKey));
        query.add(new BasicNameValuePair("id", id));

        String url = "http://api.eventful.com/json/events/get?" + URLEncodedUtils.format(query, HTTP.UTF_8);




        Log.d("ENCODAGE DE L'URL", url);
        ArrayList<String> detailsList = new ArrayList<>();
        try
        {
            HttpEntity page = getHttp(url);
            JSONObject js = new JSONObject(EntityUtils.toString(page, HTTP.UTF_8));

            JSONObject cat = js.getJSONObject("categories");
            JSONArray category = cat.getJSONArray("category");

            Log.d("EventfulAPI", "Nombre de categories: " + category.length());

            JSONObject images = js.getJSONObject("images");
            JSONObject image = images.getJSONObject("image");
            JSONObject medium = image.getJSONObject("medium");
            String urlImage = medium.getString("url");


            String s = " ";
            if(!(category.length()== 0)){
                for(int i = 0; i<category.length(); i++){
                    JSONObject item = category.getJSONObject(i);
                    s = s + " ; " + item.getString("id");  // "name" music ; theatre
                }

            }

            detailsList.add(s);
            detailsList.add(urlImage);



            //À garder
            //detailslist.add(ev.getString("title"));
            //detailslist.add(ev.getString("address"));
            //detailslist.add(ev.getString("price"));
            //detailslist.add(ev.getString("links"));
            //detailslist.add(ev.getString("images"));



        }
        catch (ClientProtocolException e) {
            Log.d("HTTP ","Erreur: "+e.getMessage());
        } catch (IOException e) {
            Log.d("Web ","Erreur: "+e.getMessage());
        } catch (ParseException e) {
            Log.d("Parse ","Erreur: "+e.getMessage());
        } catch (JSONException e) {
            Log.d("JSON ","Erreur: "+e.getMessage());
        }

        return detailsList;
    }



    private HttpEntity getHttp(String myUrl) throws ClientProtocolException, IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet http = new HttpGet(myUrl);
        HttpResponse response = httpClient.execute(http);
        return response.getEntity();
    }

}


