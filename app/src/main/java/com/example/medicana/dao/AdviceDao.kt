package com.example.medicana.dao

import androidx.room.*
import com.example.medicana.entity.Advice
import com.example.medicana.util.MESSAGE_SEEN
import com.example.medicana.util.MESSAGE_SENT

@Dao
interface AdviceDao {

    @Query("SELECT * FROM advice WHERE doctor_id = :doctor_id")
    fun getAdviceWithDoctor(doctor_id: Long?): List<Advice>

    @Query("SELECT * FROM advice WHERE is_sync = 0 AND message IS NOT NULL")
    fun getAdviceToSyncAdd(): List<Advice>

    @Query("SELECT doctor_id FROM advice WHERE is_sync = 0 AND reply IS NOT NULL")
    fun getAdviceDoctorsToSyncUpdate(): List<Long>

    @Update
    fun updateSyncedAdvice(advice_list: List<Advice>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAdvice(advice: Advice)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMyAdvice(advice: List<Advice>)

    @Query("DELETE FROM advice WHERE advice_id = :advice_id")
    fun deleteAdvice(advice_id: Long?)

    @Query("SELECT count(*) FROM advice WHERE doctor_id = :doctor_id AND reply IS NOT NULL AND state = 'sent'")
    fun checkUnreadFromDoctor(doctor_id: Long?): Int

    @Query("UPDATE advice SET state = 'seen', is_sync = 0 WHERE doctor_id = :doctor_id AND reply IS NOT NULL")
    fun updateSeenAdvice(doctor_id: Long?)

    @Query("UPDATE advice SET is_sync = 1 WHERE doctor_id = :doctor_id AND reply IS NOT NULL")
    fun updateSyncedSeenAdvice(doctor_id: Long?)

    @Query("DELETE FROM advice")
    fun deleteAll()

}
