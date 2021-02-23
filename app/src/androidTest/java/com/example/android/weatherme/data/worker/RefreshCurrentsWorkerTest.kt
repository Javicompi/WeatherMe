package com.example.android.weatherme.data.worker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RefreshCurrentsWorkerTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testDataWorker() {
        val worker = TestListenableWorkerBuilder<RefreshCurrentsWorker>(context).build()
        val result = worker.startWork().get()
        assertThat(result, `is`(ListenableWorker.Result.success()))
    }
}