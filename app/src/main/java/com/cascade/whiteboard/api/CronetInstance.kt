package com.cascade.whiteboard.api

import android.content.Context
import org.chromium.net.CronetEngine
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CronetInstance {
    companion object {
        @Volatile
        private var INSTANCE: CronetInstance? = null

        fun setup(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: CronetInstance().also {
                    INSTANCE = it
                    it._engine = CronetEngine.Builder(context).build()
                }
            }

        fun setup(testData: ByteArray) = synchronized(this) {
            CronetInstance().also {
                INSTANCE = it
                it._testData = testData
            }
        }

        fun getInstance() =
            INSTANCE ?: throw IllegalStateException("CronetInstance is not initialized, call getInstance(context) first.")
    }

    private var _engine: CronetEngine? = null

    val engine: CronetEngine
        get() = _engine ?: throw IllegalStateException("CronetInstance is not initialized, call getInstance(context) first.")

    private var _testData: ByteArray? = null

    val testData: ByteArray?
        get() = _testData

    val executor: ExecutorService = Executors.newFixedThreadPool(4)
}
