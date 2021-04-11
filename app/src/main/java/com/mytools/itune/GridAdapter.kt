package com.mytools.itune


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL


class GridAdapter(
    private val context: Context,
    private val buttons: List<Song>,
 ) : BaseAdapter(){
    private val iTuneDao = ITuneRoomDatabase.getInstance(context)?.iTuneDao()!!
    override fun getCount(): Int {
        return buttons.size
    }

    override fun getItem(index: Int): Any {
        return buttons[index]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(index: Int, view: View?, viewGroup: ViewGroup?): View {
        if(view==null){
          val layout = LayoutInflater.from(context).inflate(
                R.layout.button_ly,
                viewGroup,
                false
            ) as CardView
            val handler=Handler()
            layout.findViewById<TextView>(R.id.song_name).text=buttons[index].name
            layout.findViewById<TextView>(R.id.song_price).text="${buttons[index].price}$"
            layout.findViewById<TextView>(R.id.song_artist).text="by ${buttons[index].artistName}"
            if(buttons[index].bitmap!=null)
                layout.findViewById<ImageView>(R.id.song_icon).setImageBitmap(buttons[index].bitmap)
            else if(buttons[index].photoURL!=null)
                Thread{
                    try {
                        val stream: InputStream = URL(buttons[index].photoURL).openStream()
                        val option = BitmapFactory.Options()
                        option.inSampleSize = 2
                        val bitmap = BitmapFactory.decodeStream(stream, null, option)
                        if (bitmap != null) {
                            handler.post {
                                layout.findViewById<ImageView>(R.id.song_icon).setImageBitmap(bitmap)
                            }
                            buttons[index].bitmap=bitmap
                            val stream2 = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream2)
                            iTuneDao.insert(buttons[index].name, stream2.toByteArray())
                        }
                    } catch (e: Exception){
                        // TODO: Handling Any Network Exception.
                        MainActivity.isOffline=true
                    }
                }.start()
            buttons[index].layout = layout
            return  layout
            }
        return  view
    }
}
