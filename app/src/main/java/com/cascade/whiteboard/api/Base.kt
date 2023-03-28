package com.cascade.whiteboard.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.gson.Gson
import org.chromium.net.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URLEncoder
import java.nio.ByteBuffer
import java.nio.channels.Channels

open class Base {
    companion object {
        const val CACHE_DISABLED = false
    }

    protected val gson = Gson()

    abstract class BaseAPIException(msg: String = ""): Throwable(msg)
    class JSONException(msg: String = ""): BaseAPIException(msg)
    class APIConnectionException(msg: String = ""): BaseAPIException(msg)
    class APIWriteByteBufferException(msg: String = ""): BaseAPIException(msg)

    abstract class Request: UrlRequest.Callback() {
        private val bytesReceived = ByteArrayOutputStream()
        private val receiveChannel = Channels.newChannel(bytesReceived)

        override fun onRedirectReceived(
            request: UrlRequest,
            info: UrlResponseInfo,
            newLocationUrl: String
        ) {
            request.followRedirect()
        }

        override fun onResponseStarted(request: UrlRequest, info: UrlResponseInfo) {
            request.read(ByteBuffer.allocateDirect(64 * 1024))
        }

        override fun onReadCompleted(
            request: UrlRequest,
            info: UrlResponseInfo,
            byteBuffer: ByteBuffer
        ) {
            byteBuffer.flip()
            try {
                receiveChannel.write(byteBuffer)
            } catch (e: IOException) {
                throw APIWriteByteBufferException()
            }

            byteBuffer.clear()

            request.read(byteBuffer)
        }

        override fun onSucceeded(request: UrlRequest, info: UrlResponseInfo) {
            val bodyBytes = bytesReceived.toByteArray()
            if (info.httpStatusCode == 200) {
                onSucceeded(bodyBytes)
            } else {
                onFailed(info.httpStatusCode, bodyBytes)
            }
        }

        override fun onFailed(
            request: UrlRequest?,
            info: UrlResponseInfo?,
            error: CronetException?
        ) {
            onFailed(null, null)
        }

        abstract fun onSucceeded(body: ByteArray)

        abstract fun onFailed(status: Int?, body: ByteArray?)
    }

    private class StringRequest(
        val successListener: SuccessListener<String>,
        val errorListener: ErrorListener
    ): Request() {
        override fun onSucceeded(body: ByteArray) {
            successListener.onResponse(body.decodeToString())
        }

        override fun onFailed(status: Int?, body: ByteArray?) {
            errorListener.onError(status, body?.decodeToString())
        }
    }

    private class ImageRequest(
        val successListener: SuccessListener<Bitmap>,
        val errorListener: ErrorListener
    ): Request() {
        override fun onSucceeded(body: ByteArray) {
            successListener.onResponse(BitmapFactory.decodeByteArray(body, 0, body.size))
        }

        override fun onFailed(status: Int?, body: ByteArray?) {
            errorListener.onError(status, body?.decodeToString())
        }
    }

    private abstract class UploadData: UploadDataProvider() {
        abstract val data: ByteArray

        override fun getLength(): Long {
            return data.size.toLong()
        }

        override fun read(uploadDataSink: UploadDataSink?, byteBuffer: ByteBuffer?) {
            byteBuffer?.put(data)
            uploadDataSink?.onReadSucceeded(false)
        }

        override fun rewind(uploadDataSink: UploadDataSink?) {
            uploadDataSink?.onRewindSucceeded()
        }
    }

    private class UploadFormData(
        private val formData: MutableMap<String, Any?>
    ): UploadData() {
        override val data: ByteArray
            get() {
                val entries = mutableListOf<String>()
                formData.forEach { (param, value) ->
                    value?.let { entries.add("$param=$it") }
                }
                return entries.joinToString("&").encodeToByteArray()
            }
    }

    protected fun interface SuccessListener<T> {
        fun onResponse(response: T)
    }

    protected fun interface ErrorListener {
        fun onError(status: Int?, response: String?)
    }

    protected abstract class RequestImpl(
        private val url: String,
        private val callback: Request
    ) {
        protected open fun getHeaders(): MutableMap<String, String> = mutableMapOf()

        protected open fun getParams(): MutableMap<String, Any?> = mutableMapOf()

        protected open fun getUrl(): String {
            return if (method == "GET" && getParams().isNotEmpty()) {
                val queries = mutableListOf<String>()
                getParams().forEach { (param, value) ->
                    value?.let { queries.add("$param=${encodeQueryParameter(it)}") }
                }
                val queryString = queries.joinToString("&")
                "$url?$queryString"
            } else {
                url
            }
        }

        private fun encodeQueryParameter(parameter: Any): String {
            return URLEncoder.encode(parameter.toString(), "utf-8")
        }

        protected open val method: String = "GET"

        protected open val cacheDisabled: Boolean = CACHE_DISABLED

        protected open fun getUploadData(uploadParams: MutableMap<String, Any?>): UploadDataProvider? = null

        private fun build(): UrlRequest? {
            if (CronetInstance.getInstance().testData != null) return null

            val cronetInstance = CronetInstance.getInstance()
            val requestBuilder = cronetInstance.engine
                .newUrlRequestBuilder(
                    getUrl(),
                    callback,
                    cronetInstance.executor
                )
                .setHttpMethod(method)

            getHeaders().forEach { (header, value) ->
                requestBuilder.addHeader(header, value)
            }

            if (getParams().isNotEmpty() && method == "POST")
                getUploadData(getParams())?.let {
                    requestBuilder.setUploadDataProvider(it, cronetInstance.executor)
                }

            return requestBuilder.build()
        }

        fun execute() {
            CronetInstance.getInstance().testData?.let {
                callback.onSucceeded(it)
            } ?: run {
                build()?.start()
            }
        }
    }

    protected open class FormPost(
        url: String,
        successListener: SuccessListener<String>,
        errorListener: ErrorListener,
        private val params: MutableMap<String, Any?> = mutableMapOf()
    ): RequestImpl(url, StringRequest(successListener, errorListener)) {
        override val method: String = "POST"

        override fun getUploadData(uploadParams: MutableMap<String, Any?>): UploadDataProvider = UploadFormData(uploadParams)

        override fun getHeaders(): MutableMap<String, String> {
            return mutableMapOf("Content-Type" to "application/x-www-form-urlencoded; charset=utf-8")
        }

        override fun getParams(): MutableMap<String, Any?> {
            return params
        }
    }

    protected open class Post(
        url: String,
        successListener: SuccessListener<String>,
        errorListener: ErrorListener,
        private val body: MutableMap<String, Any?> = mutableMapOf()
    ): RequestImpl(url, StringRequest(successListener, errorListener)) {

        private val gson = Gson()

        override val method: String = "POST"

        override fun getUploadData(uploadParams: MutableMap<String, Any?>): UploadDataProvider =
            UploadDataProviders.create(gson.toJson(uploadParams).toByteArray())

        override fun getHeaders(): MutableMap<String, String> {
            return mutableMapOf("Content-Type" to "application/json; charset=utf-8")
        }

        override fun getParams(): MutableMap<String, Any?> {
            return body
        }
    }

    protected open class Get(
        url: String,
        successListener: SuccessListener<String>,
        errorListener: ErrorListener,
        private val params: MutableMap<String, Any?> = mutableMapOf(),
        private val headers: MutableMap<String, String> = mutableMapOf()
    ): RequestImpl(url, StringRequest(successListener, errorListener)) {
        override fun getParams(): MutableMap<String, Any?> {
            return params
        }

        override fun getHeaders(): MutableMap<String, String> {
            headers["Content-Type"] = "application/json; charset=utf-8"
            return headers
        }
    }

    protected class ImageLoad(
        url: String,
        successListener: SuccessListener<Bitmap>,
        errorListener: ErrorListener
    ): RequestImpl(url, ImageRequest(successListener, errorListener)) {
        override val cacheDisabled: Boolean = true
    }
}
