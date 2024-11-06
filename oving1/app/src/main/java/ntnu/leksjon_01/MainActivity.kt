package ntnu.leksjon_01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(meny: Menu): Boolean {
        super.onCreateOptionsMenu(meny)
        meny.add("Emil")
        meny.add("Johnsen")
        Log.d("Øving" , "meny laget")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.title) {
            "Emil" -> Log.w("Leksjon", "Fornavn er trykket: Emil")
            "Johnsen" -> Log.e("Leksjon", "Etternavn er trykket: Johnsen")
        }
        return true
    }
}