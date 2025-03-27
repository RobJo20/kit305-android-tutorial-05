package au.edu.utas.kit305.tutorial05

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import au.edu.utas.kit305.tutorial05.databinding.ActivityMovieDetailsBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MovieDetails : AppCompatActivity() {
    private lateinit var ui : ActivityMovieDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(ui.root)

        val movieID = intent.getIntExtra(MOVIE_INDEX, -1)
        val movieObject = items[movieID]
        ui.txtTitle.setText(movieObject.title)
        ui.txtYear.setText(movieObject.year.toString())
        ui.txtDuration.setText(movieObject.duration.toString())

        val db = Firebase.firestore
        val moviesCollection = db.collection("movies")

        ui.btnSave.setOnClickListener {
            movieObject.title = ui.txtTitle.text.toString()
            movieObject.year = ui.txtYear.text.toString().toInt()
            movieObject.duration = ui.txtDuration.text.toString().toFloat()

            moviesCollection.document(movieObject.id!!)
                .set(movieObject)
                .addOnSuccessListener {
                    Log.d(FIREBASE_TAG, "Successfully updated movie ${movieObject.id}")
                    finish()
                }
        }
    }
}