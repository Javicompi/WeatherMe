package com.example.android.weatherme.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.android.weatherme.data.Repository
import com.example.android.weatherme.data.database.getDatabase

class RefreshDataWorker(context: Context, params: WorkerParameters)
    : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        Log.d(WORK_NAME, "doWork")
        val database = getDatabase(applicationContext)
    }
}