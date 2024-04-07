package au.edu.utas.kit305.tutorial05

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import au.edu.utas.kit305.tutorial05.databinding.ActivityMovieDetailsBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MovieDetails : AppCompatActivity() {
    private lateinit var ui : ActivityMovieDetailsBinding

    private lateinit var db : FirebaseFirestore
    private lateinit var moviesCollection : CollectionReference
    private lateinit var movieObject : Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(ui.root)

        //get movie object using id from intent
        val movieID = intent.getIntExtra(MOVIE_INDEX, -1)
        movieObject = items[movieID]
        //TODO: you'll need to set txtTitle, txtYear, txtDuration yourself
        ui.txtTitle.setText(movieObject.title)
        ui.txtDuration.setText(movieObject.duration.toString())
        ui.txtYear.setText(movieObject.year.toString())

        db = Firebase.firestore
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
            val newSwear = Swear(
                character = "Frodo",
                word = ui.txtSwearWord.text.toString().ifEmpty { "Gosh" },
                severity = ui.txtSwearSeverity.text.toString().ifEmpty { "0" }.toInt()
            )
            addSwear(newSwear)
        }
        ui.btnGandalfSwore.setOnClickListener {
            val newSwear = Swear(
                character = "Gandalf",
                word = ui.txtSwearWord.text.toString().ifEmpty { "Gosh" },
                severity = ui.txtSwearSeverity.text.toString().ifEmpty { "0" }.toInt()
            )
            addSwear(newSwear)
        }

        summariseSwears()
    }

    private fun addSwear(newSwear : Swear)
    {
        if (movieObject.swears == null) movieObject.swears = mutableListOf<Swear>()

        movieObject.swears!!.add(newSwear)

        moviesCollection.document(movieObject.id!!)
            .set(movieObject)
            .addOnSuccessListener {
                summariseSwears()
            }
    }

    @SuppressLint("SetTextI18n")
    private fun summariseSwears()
    {
        val swearCount = movieObject.swears?.size ?: 0
        val severityTotal = movieObject.swears?.fold(0) { acc, swear -> acc+swear.severity!! }?.toString() ?: "No Swears"
        ui.lblSwears.text = "Total Swears: $swearCount\nTotal Swear Severity level: $severityTotal"
    }
}