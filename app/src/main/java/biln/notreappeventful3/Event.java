package biln.notreappeventful3;

/**
 * Created by Boris on 2015-03-16.
 */
class Event {

    public String idFromEventful;
    public String title;
    public String date_start;
    public String date_stop;
    public String location;
    public String description;

    public Event(String idFromEventful, String title,String date_start, String date_stop, String location, String description){
        this.idFromEventful = idFromEventful;
        this.title = title;
        this.date_start = date_start;
        this.date_stop = date_stop;
        this.location = location;
        this.description = description;
    }



}
