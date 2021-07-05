package com.example.medicana.dao

import androidx.room.*
import com.example.medicana.entity.Treatment

@Dao
interface TreatmentDao {

    @Query("SELECT * FROM treatment WHERE :date <= finish_date")
    fun getMyCurrentTreatments(date: String?): List<Treatment>

    @Query("SELECT * FROM treatment WHERE :date > finish_date")
    fun getMyOldTreatments(date: String?): List<Treatment>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMyTreatments(treatments: List<Treatment?>?)

    @Query("DELETE FROM treatment")
    fun deleteAll()

}