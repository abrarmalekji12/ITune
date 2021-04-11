package com.tools.iTune

import android.graphics.Bitmap
import androidx.cardview.widget.CardView

class Song(
val name:String,val artistName:String,val photoURL:String?,var bitmap:Bitmap?,val price : Double,
val id:Int
){
     var layout: CardView?=null
 }