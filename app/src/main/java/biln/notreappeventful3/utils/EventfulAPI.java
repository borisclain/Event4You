package biln.notreappeventful3.utils;


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
import biln.notreappeventful3.event.Event;

/**
 * Created by Boris on 2015-03-10.
 */
public class EventfulAPI {

    boolean connectionSuccess = true;
    ArrayList<Event> eventsFound;                                          //peuplé par la recherche
    String apiKey = "b5JxXhsHhJTW2mzP";
    //String url = "http://api.eventful.com/json/events/search?app_key="+apiKey;

    public EventfulAPI(){
        eventsFound = new ArrayList<Event>();
    }

    /**
     * Trouve les événements qui s'en viennent dans la ville donnée en paramètre
     *
     * @param city
     */
    public void getNextEvents(String city){
        Log.d("Query ville ", ""+city);
        List<NameValuePair> query = new ArrayList<NameValuePair>();
        query.add(new BasicNameValuePair("app_key", this.apiKey));
        query.add(new BasicNameValuePair("location", city));
        query.add(new BasicNameValuePair("sort_order", "popularity"));
        query.add(new BasicNameValuePair("page_size", "25"));

        String url = "http://api.eventful.com/json/events/search?" + URLEncodedUtils.format(query, HTTP.UTF_8);
        Log.d("Query Encode ", ""+url);
        getEvents(url, 1);
    }

    public void getDesiredResults(String city, String dateStart, String dateStop, ArrayList<String> categories, int p){

        List<NameValuePair> query = new ArrayList<NameValuePair>();
        query.add(new BasicNameValuePair("app_key", this.apiKey));
        query.add(new BasicNameValuePair("l", city));


        //On ajoute le champs time
        String time;
        if(dateStart.matches("") || dateStop.matches("")){
        }
        else{
            time = dateStart + "-" + dateStop;
            query.add(new BasicNameValuePair("t", time));
        }

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

        String order = "date";
        query.add(new BasicNameValuePair("sort_order", order));
        String size = "100";
        query.add(new BasicNameValuePair("page_size", size));

        //On construit l'URL avec les champs
        String url = "http://api.eventful.com/json/events/search?" + URLEncodedUtils.format(query, HTTP.UTF_8);
        Log.d("ENCODAGE DE L'URL", url);
        //On fait l'appel de la page et la récolte
        getEvents(url, p);
    }



    /**
     * Méthode utilitaire locale
     *
     * @param url
     */
    private void getEvents(String url, int p){
        try {
            Log.d("url final", ""+url+"page_number="+p);
            HttpEntity page = getHttp(url+"page_number="+p);

            JSONObject js = new JSONObject(EntityUtils.toString(page, HTTP.UTF_8));
            JSONObject events = js.getJSONObject("events");
            JSONArray event = events.getJSONArray("event");
            Log.d("WEB", "Nombre d'événements: " + event.length());

            for (int j = 0; j < event.length(); j++) {

                JSONObject item = event.getJSONObject(j);

                String adresse = "";

                if (!(item.getString("venue_name")=="null"))
                    adresse = item.getString("venue_name");
                if (!(item.getString("venue_address")=="null"))
                    adresse = adresse + ", "+item.getString("venue_address");
                if (!(item.getString("city_name")=="null"))
                    adresse = adresse + ", "+item.getString("city_name");
                if (!(item.getString("postal_code")=="null"))
                    adresse = adresse + " "+item.getString("postal_code");
                if (!(item.getString("country_name")=="null"))
                    adresse = adresse + " "+item.getString("country_name");
                if (adresse == "")
                    adresse = item.getString("latitude")+","+item.getString("longitude");

                Log.d("EventfulAPI ", " adresse = " + adresse);


                //Si la valeur sous "stop_time" est null
                if (item.isNull("stop_time")) {
                    eventsFound.add(new Event(item.getString("id"),
                            item.getString("title"),
                            item.getString("start_time"),
                            "2030-01-01 00:00:00",
                            item.getString("city_name"),
                            adresse,
                            item.getString("description")));
                } else {
                    eventsFound.add(new Event(item.getString("id"),
                            item.getString("title"),
                            item.getString("start_time"),
                            item.getString("stop_time"),
                            item.getString("city_name"),
                            adresse,
                            item.getString("description")));
                }
            }

        } catch (ClientProtocolException e) {
            connectionSuccess = false;
            Log.d("HTTP ", "Erreur: " + e.getMessage());
        } catch (IOException e) {
            connectionSuccess = false;
            Log.d("Web ", "Erreur: " + e.getMessage());
        } catch (ParseException e) {
            Log.d("Parse ", "Erreur: " + e.getMessage());
        } catch (JSONException e) {
            Log.d("JSON ", "Erreur: " + e.getMessage());
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
                    if (i==0)
                        s = s + item.getString("id");
                    else
                        s = s + " ; " + item.getString("id");  // "name" music ; theatre
                }

            }
            else
                s = "Aucune catégorie définie";

            detailsList.add(s);
            detailsList.add(urlImage);



        }
        catch (ClientProtocolException e) {
            connectionSuccess = false;
            Log.d("HTTP ","Erreur: "+e.getMessage());
        } catch (IOException e) {
            connectionSuccess = false;
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


