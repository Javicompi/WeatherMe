package com.example.android.weatherme

import android.app.Application
import android.util.Log
import androidx.work.*
import com.example.android.weatherme.data.worker.RefreshDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class WeatherMeApplication : Application() {

    val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        delayedInit()
    }

    private fun delayedInit() {
        Log.d("Application", "delayedInt")
        applicationScope.launch {
            setUpdatingWork()
        }
    }

    private fun setUpdatingWork() {

        Log.d("Application", "setUpdatingWork")
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .setRequiresDeviceIdle(true)
            .build()

        val repeatingRequest =
            PeriodicWorkRequestBuilder<RefreshDataWorker>(60, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            RefreshDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }
}