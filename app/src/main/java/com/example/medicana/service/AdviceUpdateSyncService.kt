package com.example.medicana.service


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.impl.utils.futures.SettableFuture
import com.example.medicana.RES_SUCCESS
import com.example.medicana.SHARED_PREFS
import com.example.medicana.db.RoomService
import com.example.medicana.retrofit.RetrofitService
import com.google.common.util.concurrent.ListenableFuture
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("RestrictedApi")
class AdviceUpdateSyncService(private val ctx: Context, private val workParameters: WorkerParameters): ListenableWorker(ctx, workParameters) {

    lateinit var  future: SettableFuture<Result>

    override fun startWork(): ListenableFuture<Result> {
        future = SettableFuture.create()
        val doctors = RoomService.appDatabase.getAdviceDao().getAdviceDoctorsToSyncUpdate()
        syncAdvice(doctors)
        return future
    }

    private fun syncAdvice(doctors: List<Long>) {
        val patient_id = (ctx.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE))
                .getLong("patient_id", 0)
        for (doctor_id in doctors) {
            val result = RetrofitService.endpoint.seeAdvice(patient_id, doctor_id)
            result.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>?, t: Throwable?) {
                    future.set(Result.retry())
                }
                override fun onResponse(call: Call<String>?, response: Response<String>?) {
                    if (response?.isSuccessful!!) {
                        if (response?.body() == RES_SUCCESS) {
                            RoomService.appDatabase.getAdviceDao().updateSyncedSeenAdvice(doctor_id)
                            future.set(Result.success())
                        } else {
                            future.set(Result.retry())
                        }
                    } else {
                        future.set(Result.retry())
                    }
                }
            })
        }
    }


}
