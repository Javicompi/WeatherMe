package com.example.android.weatherme.data.worker

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.data.database.WeatherDatabase
import retrofit2.HttpException

class RefreshDataWorker(context: Context, params: WorkerParameters)
    : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val database = WeatherDatabase.getDatabase(applicationContext)
        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val repository = Repository(database, preferences)
        return try {
            repository.updateCurrents()
            Result.success()
        } catch (e: HttpException) {
            Log.e(WORK_NAME, e.message())
            Result.retry()
        }
    }
}