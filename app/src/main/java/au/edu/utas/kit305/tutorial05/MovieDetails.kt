package au.edu.utas.kit305.tutorial05

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import au.edu.utas.kit305.tutorial05.databinding.ActivityMovieDetailsBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.firestore

class MovieDetails : AppCompatActivity() {
    private lateinit var ui : ActivityMovieDetailsBinding
    private lateinit var movieObject : Movie
    private lateinit var moviesCollection : CollectionReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(ui.root)

        //get movie object using id from intent
        val movieID = intent.getIntExtra(MOVIE_INDEX, -1)
        movieObject = items[movieID]
        //TODO: you'll need to set txtTitle, txtYear, txtDuration yourself

        ui.txtTitle.setText(movieObject.title)
        ui.txtDuration.setText(""+movieObject.duration)
        ui.txtYear.setText(movieObject.year.toString())

        val db = Firebase.firestore
        moviesCollection = db.collection("movies")

        ui.btnSave.setOnClickListener {
            //get the user input
            movieObject.title = ui.txtTitle.text.toString()
            movieObject.year = ui.txtYear.text.toString().toInt() //good code would check this is really an int
            movieObject.duration = ui.txtDuration.text.toString().toFloat() //good code would check this is really a float

            //update the database
            moviesCollection.document(movieObject.id!!)
                .set(movieObject)
                .addOnSuccessListener {
                    Log.d(FIREBASE_TAG, "Successfully updated movie ${movieObject.id}")
                    //return to the list
                    finish()
                }
        }

        ui.btnFrodoSwore.setOnClickListener {
            val newSwearObject = Swear(
                character = "Frodo",
                word = ui.txtSwearWord.text.toString(),
                severity = ui.txtSwearSeverity.text.toString().toInt()
            )
            addSwear(newSwearObject)
        }
        ui.btnGandalfSwore.setOnClickListener {
            val newSwearObject = Swear(
                character = "Gandalf",
                word = ui.txtSwearWord.text.toString(),
                severity = ui.txtSwearSeverity.text.toString().toInt()
            )
            addSwear(newSwearObject)
        }
        summariseSwears()
    }

    fun addSwear(swear: Swear)
    {
        if (movieObject.swears == null) movieObject.swears = mutableListOf<Swear>()
        movieObject.swears!!.add(swear)

        val docRef = moviesCollection.document(movieObject.id!!)
        docRef.set(movieObject)
            .addOnSuccessListener {
                Log.d(FIREBASE_TAG, "Successfully updated movie ${movieObject.id}")
            }

        summariseSwears()
    }
    fun summariseSwears()
    {
        val numberOfSwears = movieObject.swears.orEmpty().size
        //doing a sum
        /*val totalSeverityOfSwears  = movieObject.swears.orEmpty().fold(0) { acc, swear ->
            acc + swear.severity!!
        }*/
        val totalSeverityOfSwears = movieObject.swears.orEmpty().sumOf { swear -> swear.severity!! }
        //could do a for loop
        ui.lblSwears.text = "Number of swears: $numberOfSwears\nTotal Severity: $totalSeverityOfSwears"
    }
}