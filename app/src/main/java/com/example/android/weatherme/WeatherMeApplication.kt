package com.example.android.weatherme

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.example.android.weatherme.data.worker.RefreshCurrentsWorker
import com.example.android.weatherme.utils.Constants
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class WeatherMeApplication : Application(), Configuration.Provider {

    val applicationScope = CoroutineScope(Dispatchers.Default)

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        delayedInit()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    private fun delayedInit() {
        applicationScope.launch {
            setUpRefreshCurrentsWorker()
        }
    }

    private fun setUpRefreshCurrentsWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            //.setRequiresDeviceIdle(true)
            .build()

        val repeatingRequest =
            PeriodicWorkRequestBuilder<RefreshCurrentsWorker>(
                Constants.PERIODIC_REQUEST_DELAY_MINS, TimeUnit.MINUTES
            ).setConstraints(constraints).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            RefreshCurrentsWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }
}