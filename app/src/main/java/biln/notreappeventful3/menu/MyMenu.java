package biln.notreappeventful3.menu;

/**
 * Created by Boris on 2015-03-26.
 */

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.Toast;

import biln.notreappeventful3.R;
import biln.notreappeventful3.activities.FavoritesActivity;
import biln.notreappeventful3.activities.MainActivity;
import biln.notreappeventful3.activities.SearchActivity;

/**
 * Created by Boris on 2015-03-26.
 */
public class MyMenu extends ActionBarActivity {


    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();


        if (id == R.id.menuSuggestion) {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("Called from menu", 1);
            startActivity(i);
            //finish();
            return true;
        }
        else if (id == R.id.menuShowFavorites) {

            Intent i = new Intent(this, FavoritesActivity.class);
            startActivity(i);
            //finish();
            return true;
        }
        else if (id == R.id.menuAdvancedSearch) {
            Intent i = new Intent(this, SearchActivity.class);
            startActivity(i);
            //finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

