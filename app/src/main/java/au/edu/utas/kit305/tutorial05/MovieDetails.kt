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
        movieObject = items[movieID]y
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

        //lecture example: added buttons for tracking swears in a movie
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

    //lecture example: made a function for adding a swear to the movie
    fun addSwear(swear: Swear)
    {
        //keep the "local" copy of the movie swears list up to date
        if (movieObject.swears == null) movieObject.swears = mutableListOf<Swear>()
        movieObject.swears!!.add(swear)

        //lecture example 1: store the array of swears on the movie document directly
        //we then commented this out to do lecture example 2
        val docRef = moviesCollection.document(movieObject.id!!)
        /*docRef.set(movieObject)
            .addOnSuccessListener {
                Log.d(FIREBASE_TAG, "Successfully updated movie ${movieObject.id}")
            }
        summariseSwears()
           */

        //lecture example 2: add swears to a sub-collection
        val swearsCollection = docRef.collection("swears")
        swearsCollection
            .add(swear)
            .addOnSuccessListener {
                summariseSwears()
            }

    }

    //lecture example: iterate over all swears to calculate a total
    fun summariseSwears()
    {
        //total count is easy, although note the orEmpty() call, because swears could be null
        val numberOfSwears = movieObject.swears.orEmpty().size

        //doing a sum -- using fold() higher order function
        /*val totalSeverityOfSwears  = movieObject.swears.orEmpty().fold(0) { acc, swear ->
            acc + swear.severity!!
        }*/

        //doing a sum -- using sumOf() higher order function (credit to Charlie)
        val totalSeverityOfSwears = movieObject.swears.orEmpty().sumOf { swear -> swear.severity!! }

        //also could have just done a for loop like
        /*var totalSeverityOfSwears = 0
        for (swear in movieObject.swears.orEmpty())
        {
            totalSeverityOfSwears += swear.severity!!
        }*/

        //display it
        ui.lblSwears.text = "Number of swears: $numberOfSwears\nTotal Severity: $totalSeverityOfSwears"
    }
}