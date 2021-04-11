package com.tools.iTune


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import com.facebook.shimmer.ShimmerFrameLayout
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
        if(buttons[index].layout==null){
          val layout = LayoutInflater.from(context).inflate(
                R.layout.button_ly,
                viewGroup,
                false
            ) as CardView

            val handler=Handler(Looper.getMainLooper())
            val songIcon=  layout.findViewById<ImageView>(R.id.song_icon)
            val shimmerView = layout.findViewById<ShimmerFrameLayout>(R.id.shimmer_view)
            layout.findViewById<TextView>(R.id.song_name).text=buttons[index].name
            layout.findViewById<TextView>(R.id.song_price).text="${buttons[index].price}$"
            layout.findViewById<TextView>(R.id.song_artist).text="by ${buttons[index].artistName}"

            if(buttons[index].bitmap!=null) {
                shimmerView.stopShimmerAnimation()
                 songIcon.setImageBitmap(buttons[index].bitmap)
            }
                else if(buttons[index].photoURL!=null) {
             shimmerView .startShimmerAnimation()
                Thread {
                    try {
                        val stream: InputStream = URL(buttons[index].photoURL).openStream()
                        val option = BitmapFactory.Options()
                        option.inSampleSize = 2
                        val bitmap = BitmapFactory.decodeStream(stream, null, option)

                        if (bitmap != null) {
                            buttons[index].bitmap = bitmap
                            handler.post {
                                shimmerView.stopShimmerAnimation()
                                songIcon.setImageBitmap(bitmap)
                            }
                            val stream2 = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream2)
                            iTuneDao.insert(buttons[index].name, stream2.toByteArray())
                        }
                    } catch (e: Exception) {
                        // TODO: Handling Any Network Exception.
                        MainActivity.isOffline = true
                    }
                }.start()
            }
            buttons[index].layout = layout
            return  layout
            }
        else  {

            val shimmerView = buttons[index].layout!!.findViewById<ShimmerFrameLayout>(R.id.shimmer_view)
            if(buttons[index].bitmap!=null )
                shimmerView.stopShimmerAnimation()
        else if(!shimmerView.isAnimationStarted)
                shimmerView.startShimmerAnimation()

        }
        //return view
        return buttons[index].layout!!
    }
}
