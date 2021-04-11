package com.mytools.itune

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import java.net.InetAddress
import java.net.UnknownHostException


class MainActivity : AppCompatActivity() {
    private var lastSearched: String? = null

    lateinit var connectionReceiver: BroadcastReceiver
    lateinit var resultView: TextView
    lateinit var searchingView: TextView
    private lateinit var iTuneViewModel: ITuneViewModel
    var gridView: GridView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        iTuneViewModel = ViewModelProviders.of(this).get(ITuneViewModel::class.java)

        resultView = findViewById(R.id.result_view)
        searchingView = findViewById(R.id.searching_view)
        gridView = findViewById(R.id.songs)
        val searchBox = findViewById<EditText>(R.id.search)

        searchBox.imeOptions = EditorInfo.IME_ACTION_DONE
        searchBox.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                val searching = searchBox.text.toString()
                if (searching.isEmpty() || (!isOffline && searching == lastSearched))
                    return@setOnEditorActionListener false
                lastSearched = searching
                searchFor(searching)
            }
            return@setOnEditorActionListener false
        }
        val handler = Handler(mainLooper)
        connectionReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                if (ACTION_CONNECTIVITY_CHANGE == p1!!.action && isOffline && lastSearched != null && lastSearched!!.isNotEmpty())

                    Thread {
                        if (isInternetAvailable())
                            handler.post {
                                showToast("Network available.",false)
                                isOffline = false
                                searchFor(lastSearched!!)
                            }

                    }.start()

            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            findViewById<RelativeLayout>(R.id.root_view).isForceDarkAllowed = false
        }
        //        searchFor("Across the Great Divide")
    }

    private fun isInternetAvailable(): Boolean {
        try {
            val address = InetAddress.getByName("www.google.com")
            return !address.equals("")
        } catch (e: UnknownHostException) {
            //Internet not available
        }
        return false
    }

    private fun showToast(message: String, long: Boolean) {
        Toast.makeText(this, message, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
    }


    private fun searchFor(search: String) {
        val spanString = SpannableString(search)
        spanString.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            search.length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        val spannableStringBuilder = SpannableStringBuilder()
        spannableStringBuilder.append("Searching for ")
        spannableStringBuilder.append(spanString)

        findViewById<ProgressBar>(R.id.loading).visibility = View.VISIBLE
        findViewById<View>(R.id.load_back).visibility = View.VISIBLE
        resultView .visibility = View.GONE
       searchingView.visibility = View.VISIBLE

        searchingView.text = spannableStringBuilder
        val handler = Handler(mainLooper)
        Thread {
            val searchResult = iTuneViewModel.getAllSongs(search)
            val t = searchResult.songs
            isOffline = !searchResult.online
            if (isOffline) {
                val filter = IntentFilter()
                filter.addAction(ACTION_CONNECTIVITY_CHANGE)
                try {
                    registerReceiver(connectionReceiver, filter)
                } catch (e: IllegalArgumentException) {
                    //Already unregistered
                }
            }
            handler.post {
                findViewById<ProgressBar>(R.id.loading).visibility = View.INVISIBLE
                findViewById<View>(R.id.load_back).visibility = View.INVISIBLE
                resultView.visibility = View.VISIBLE
               searchingView.visibility = View.GONE
                if(isOffline)
                    showToast("You are Offline, Please Turn on mobile data.",true)
                if (t.isEmpty()) {
                    if (searchResult.online)
                        resultView.text =
                            "Sorry, No match found for $search"
                    else
                      resultView.text =
                            getString(R.string.offline_no_result)
                } else {
                    val span = SpannableString(search)
                    span.setSpan(
                        StyleSpan(Typeface.BOLD),
                        0,
                        search.length,
                        SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                    val spannableBuilder = SpannableStringBuilder()
                    if (!searchResult.online) {
                        spannableBuilder.append("You are Offline, Results for ")
                    } else {
                        spannableBuilder.append("Results for ")
                    }
                    spannableBuilder.append(span)
                    resultView.text = spannableBuilder
                    gridView?.adapter = GridAdapter(this@MainActivity, t)
                }
            }
        }.start()
    }
    companion object {
        var isOffline = false
        const val ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE"
    }
}