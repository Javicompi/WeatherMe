package com.example.android.weatherme.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.android.weatherme.data.Repository
import retrofit2.HttpException

class RefreshDataWorker @WorkerInject constructor(
        @Assisted context: Context,
        @Assisted params: WorkerParameters,
        private val repository: Repository
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            repository.updateCurrents()
            Result.success()
        } catch (e: HttpException) {
            Log.e(WORK_NAME, e.message())
            Result.retry()
        }
    }
}