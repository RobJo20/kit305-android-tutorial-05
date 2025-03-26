package au.edu.utas.kit305.tutorial05

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.edu.utas.kit305.tutorial05.databinding.ActivityMainBinding
import au.edu.utas.kit305.tutorial05.databinding.MyListItemBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

const val FIREBASE_TAG = "FirebaseLogging"

val items = mutableListOf<Movie>()

class MainActivity : AppCompatActivity()
{
    private lateinit var ui : ActivityMainBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityMainBinding.inflate(layoutInflater)
        setContentView(ui.root)

        ui.lblMovieCount.text = "${items.size} Movies"
        ui.myList.adapter = MovieAdapter(movies = items)

        //vertical list
        ui.myList.layoutManager = LinearLayoutManager(this)


        //get db connection
        val db = Firebase.firestore
        Log.d("FIREBASE", "Firebase connected: ${db.app.name}")

        val lotr = Movie(
            title = "Lord of the Rings: Fellowship of the ring",
            year = 2001,
            duration = 9001F
        )

        val moviesCollection = db.collection("movies")
        /*moviesCollection
            .add(lotr)
            .addOnSuccessListener {
                Log.d(FIREBASE_TAG, "document created with id ${it.id}")
                lotr.id = it.id
            }
            .addOnFailureListener {
                Log.e(FIREBASE_TAG, "error writing document", it)
            }*/

        ui.lblMovieCount.text = "Loading..."
        moviesCollection
            .get()
            .addOnSuccessListener { result ->
                items.clear()
                Log.d(FIREBASE_TAG, "--- all movies ---")
                for (document in result)
                {
                    val movie = document.toObject<Movie>()
                    movie.id = document.id
                    Log.d(FIREBASE_TAG, movie.toString())

                    items.add(movie)
                }
                (ui.myList.adapter as MovieAdapter).notifyDataSetChanged()
            }
    }

    inner class MovieHolder(var ui: MyListItemBinding) : RecyclerView.ViewHolder(ui.root) {}

    inner class MovieAdapter(private val movies: MutableList<Movie>) : RecyclerView.Adapter<MovieHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainActivity.MovieHolder {
            val ui = MyListItemBinding.inflate(layoutInflater, parent, false)   //inflate a new row from the my_list_item.xml
            return MovieHolder(ui)                                                            //wrap it in a ViewHolder
        }

        override fun getItemCount(): Int {
            return movies.size
        }

        override fun onBindViewHolder(holder: MainActivity.MovieHolder, position: Int) {
            val movie = movies[position]   //get the data at the requested position
            holder.ui.txtName.text = movie.title
            holder.ui.txtYear.text = movie.year.toString()

            ui.lblMovieCount.text = "${getItemCount()} Movie(s)"
        }
    }
}

