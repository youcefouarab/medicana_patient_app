package com.example.medicana.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.medicana.R
import com.example.medicana.db.RoomService
import com.example.medicana.entity.Advice
import com.example.medicana.entity.Appointment
import com.example.medicana.entity.Doctor
import com.example.medicana.retrofit.RetrofitService
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormatSymbols
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Suppress("DEPRECATION")
fun isOnline(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
    } else {
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            return true
        }
    }
    return false
}

fun navController(activity: Activity): NavController {
    val navHostFragment = (activity as AppCompatActivity)
            .supportFragmentManager
            .findFragmentById(R.id.nav_host) as NavHostFragment
    return navHostFragment.navController
}

fun checkFailure(context: Context) {
    if (isOnline(context)) {
        Toast.makeText(
            context,
            R.string.error,
            Toast.LENGTH_SHORT
        ).show()
    } else {
        Toast.makeText(
            context,
            R.string.internet_error,
            Toast.LENGTH_SHORT
        ).show()
    }
}

fun displayDate(date: String): String {
    val day = date.slice(IntRange(8, 9))
    val month = DateFormatSymbols().getMonths().get(date.slice(IntRange(5, 6)).toInt() - 1)
    val year = date.slice(IntRange(0, 3))
    val lang = Locale.getDefault().getLanguage()
    return if (LocalDate.now().year.toString() == year) {
        if (lang == "en") {
            "$month $day"
        } else {
            "$day $month"
        }
    } else {
        if (lang == "en") {
            "$month $day, $year"
        } else {
            "$day $month $year"
        }
    }

}

fun displayDateFromUnix(date_time: Long): String {
    val lang = Locale.getDefault().getLanguage()
    val year = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.ofInstant(
            Instant.ofEpochSecond(date_time),
            TimeZone.getDefault().toZoneId()
        ).format(DateTimeFormatter.ofPattern("yyyy"))
    } else {
        //TODO
        ""
    }
    val pattern = if (LocalDate.now().year.toString() == year) {
        if (lang == "en") {
            "MMM d"
        } else {
            "d MMM"
        }
    } else {
        if (lang == "en") {
            "MMM d, yyyy"
        } else {
            "d MMM yyyy"
        }
    }
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.ofInstant(
            Instant.ofEpochSecond(date_time),
            TimeZone.getDefault().toZoneId()
        ).format(DateTimeFormatter.ofPattern(pattern))
    } else {
        //TODO
        ""
    }

}

fun displayTimeFromUnix(date_time: Long): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.ofInstant(
            Instant.ofEpochSecond(date_time),
            TimeZone.getDefault().toZoneId()
        ).format(DateTimeFormatter.ofPattern("HH:mm"))
    } else {
        //TODO
        ""
    }
}


fun unixTimestamp(): Long {
    return System.currentTimeMillis() / 1000L
}


fun reloadRoomDatabase(patient_id: Long, ctx: Context) {
    val call1 = RetrofitService.endpoint.getMyAppointments(patient_id)
    call1.enqueue(object : Callback<List<Appointment>> {
        override fun onResponse(
            call: Call<List<Appointment>>?,
            response: Response<List<Appointment>>?
        ) {
            if (response?.isSuccessful!!) {
                RoomService.appDatabase.getAppointmentDao().addMyAppointments(response.body()!!)
            } else {
                checkFailure(ctx)
            }
        }

        override fun onFailure(call: Call<List<Appointment>>?, t: Throwable?) {
            Log.e("Retrofit error", t.toString())
            checkFailure(ctx)
        }
    })

    val call2 = RetrofitService.endpoint.getMyDoctors(patient_id)
    call2.enqueue(object : Callback<List<Doctor>> {
        override fun onResponse(
            call: Call<List<Doctor>>?,
            response: Response<List<Doctor>>?
        ) {
            if (response?.isSuccessful!!) {
                RoomService.appDatabase.getDoctorDao().addMyDoctors(response.body()!!)

            } else {
                checkFailure(ctx)
            }
        }

        override fun onFailure(call: Call<List<Doctor>>?, t: Throwable?) {
            Log.e("Retrofit error", t.toString())
            checkFailure(ctx)
        }
    })

    val call3 = RetrofitService.endpoint.getAllAdvice(patient_id)
    call3.enqueue(object : Callback<List<Advice>> {
        override fun onResponse(
            call: Call<List<Advice>>?,
            response: Response<List<Advice>>?
        ) {
            if (response?.isSuccessful!!) {
                RoomService.appDatabase.getAdviceDao().addMyAdvice(response.body()!!)
            } else {
                checkFailure(ctx)
            }
        }

        override fun onFailure(call: Call<List<Advice>>?, t: Throwable?) {
            Log.e("Retrofit error", t.toString())
            checkFailure(ctx)
        }
    })


}

fun getQrCodeBitmap(qrCodeContent: String): Bitmap {
    val hints = hashMapOf<EncodeHintType, Int>().also { it[EncodeHintType.MARGIN] = 1 }
    val size = 512
    val bits = QRCodeWriter().encode(qrCodeContent, BarcodeFormat.QR_CODE, size, size, hints)
    return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
        for (x in 0 until size) {
            for (y in 0 until size) {
                it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
            }
        }
    }
}