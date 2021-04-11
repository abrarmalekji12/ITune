package com.tools.iTune

import androidx.room.*


@Entity(tableName = "iTune_database")
data  class ITuneSong(
    var name: String,
    var artistName:String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var image: ByteArray?=null ,
    var price: Double,
){

    @PrimaryKey(autoGenerate = true)
    var id: Int =0
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ITuneSong

        if (name != other.name) return false
        if (!image.contentEquals(other.image)) return false
        if (price != other.price) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + image.contentHashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + id
        return result
    }
}


@Dao
interface ITuneDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(song: ITuneSong)
    @Query("UPDATE iTune_database SET image = :image2 WHERE name = :name2")
    fun insert(name2:String,image2: ByteArray)

    @Query("DELETE FROM iTune_database")
    fun deleteAllNotes()

    @Query("SELECT * FROM iTune_database WHERE name LIKE '% ' || :word ||' %'  OR artistName LIKE '%' || :word ||'%'")
    fun searchFor(word: String): List<ITuneSong>
    @Query("SELECT image FROM iTune_database WHERE name LIKE :name LIMIT 1")
   fun getImage(name: String):ByteArray?
}