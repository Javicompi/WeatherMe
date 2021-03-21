package com.example.android.weatherme.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.android.weatherme.data.Repository
import retrofit2.HttpException


class RefreshCurrentsWorker @WorkerInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    val repository: Repository
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        Log.d(WORK_NAME, "do Work")
        return try {
            repository.updateData(forced = false)
            Log.d(WORK_NAME, "success")
            Result.success()
        } catch (e: HttpException) {
            Log.e(WORK_NAME, e.message())
            Log.d(WORK_NAME, "error")
            Result.retry()
        }
    }
}