package com.example.android.weatherme.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.data.database.getDatabase
import retrofit2.HttpException

class RefreshDataWorker(context: Context, params: WorkerParameters)
    : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        Log.d(WORK_NAME, "doWork")
        val database = getDatabase(applicationContext)
        val repository = Repository(database)
        return try {
            repository.updateCurrents()
            Log.d(WORK_NAME, "doWork done")
            Result.success()
        } catch (e: HttpException) {
            Log.d(WORK_NAME, e.message())
            Result.retry()
        }
    }
}