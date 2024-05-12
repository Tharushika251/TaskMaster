@file:Suppress("DEPRECATION")

package com.example.notesapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.PopupMenu
//import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notesapp.Adapter.NotesAdapter
import com.example.notesapp.Database.NotesDatabase
import com.example.notesapp.Models.Note
import com.example.notesapp.Models.NoteViewModel
import com.example.notesapp.databinding.ActivityMainBinding
import androidx.appcompat.widget.SearchView

class MainActivity : AppCompatActivity(), NotesAdapter.NotesClickListener , PopupMenu.OnMenuItemClickListener{

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: NotesDatabase
    lateinit var viewModel: NoteViewModel
    lateinit var adapter: NotesAdapter
    lateinit var selectedNote: Note

    private val updateNote = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
        if(result.resultCode==Activity.RESULT_OK){
            val note = result.data?.getSerializableExtra("note") as? Note
            if(note != null){
                viewModel.updateNote(note)
            }
        }
    }

    //sets up the activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize ui components--------
        initUI()

        //sets up view model
        viewModel = ViewModelProvider(this,
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(NoteViewModel::class.java)

        //sets up live data observers
        viewModel.allnotes.observe(this) { list ->

            list?.let {

                adapter.updateList(list)

            }
        }

        //initialize the database
        database = NotesDatabase.getDatabase(this)


    }


    private fun initUI() {

        binding.recyclerView.setHasFixedSize(true)                                                          //fix the recycler view size
        //binding.recyclerView.layoutManager  = StaggeredGridLayoutManager(2,LinearLayout.VERTICAL) //2 columns and a vertical orientation
        binding.recyclerView.layoutManager  = StaggeredGridLayoutManager(1,LinearLayout.VERTICAL) //2 columns and a vertical orientation
        adapter = NotesAdapter(this,this)                                                    //populating data into the RecyclerView
        binding.recyclerView.adapter = adapter                                                              //allows display data

        val getContent  = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result-> //handle the result
            if(result.resultCode == Activity.RESULT_OK){                                                       //result ok means launch activity successful
                val note = result.data?.getSerializableExtra("note") as? Note
                if(note!=null){
                    viewModel.insertNote(note)                                                                 //retrieves note object from the result data bundle using the key "note"
                }
            }
        }


        binding.FABadd.setOnClickListener{                        //add a floating button
            val intent = Intent(this,AddNote::class.java)
            getContent.launch(intent)
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {   //anonymous object implementing
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false                                            //if the query in the search view not changed
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText!=null){
                    adapter.filterList(newText)                         //if the query in the search view changed, call filterList()
                }
                return true
            }

        })
    }

    override fun onItemClicked(note: Note) {
        val intent = Intent(this,AddNote::class.java)
        intent.putExtra("current_note",note)                  //adds extra data to the intent
        updateNote.launch(intent)
    }

    override fun onLongItemClicked(note: Note, cardView: CardView) {
        selectedNote = note
        popUpDisplay(cardView)                                      //helps to display delete
    }

    private fun popUpDisplay(cardView: CardView) {
        val popup = PopupMenu(this,cardView)
        popup.setOnMenuItemClickListener(this)
        popup.inflate(R.menu.pop_up_menu)
        popup.show()

    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if(item?.itemId==R.id.delete_note){
            viewModel.deleteNote(selectedNote)
            return true
        }
        return false
    }

}