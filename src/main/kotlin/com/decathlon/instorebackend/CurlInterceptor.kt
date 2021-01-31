package com.decathlon.instorebackend

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import java.io.IOException
import java.nio.charset.Charset

class CurlInterceptor(private val logger: HttpLoggingInterceptor.Logger = HttpLoggingInterceptor.Logger.DEFAULT) : Interceptor {

    companion object {
        private val UTF8 = Charset.forName("UTF-8")
    }

    private var curlOptions: String? = null

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        var compressed = false

        var curlCmd = "curl"
        if (curlOptions != null) {
            curlCmd += " " + curlOptions!!
        }
        curlCmd += " -X " + request.method

        val headers = request.headers
        for (i in 0 until headers.size) {
            val name = headers.name(i)
            var value = headers.value(i)

            val start = 0
            val end = value.length - 1
            if (value.isNotEmpty() && value[start] == '"' && value[end] == '"') {
                value = "\\\"" + value.substring(1, end) + "\\\""
            }

            if ("Accept-Encoding".equals(name, ignoreCase = true) && "gzip".equals(value, ignoreCase = true)) {
                compressed = true
            }
            curlCmd += " -H \"$name: $value\""
        }

        request.body?.let {
            curlCmd += " -H \"Content-Type: application/json\""
            val buffer = Buffer().apply { it.writeTo(this) }
            val charset = it.contentType()?.charset(UTF8) ?: UTF8
            // try to keep to a single line and use a subshell to preserve any line breaks
            curlCmd += " --data '" + buffer.readString(charset).replace("\n", "\\n") + "'"
        }

        curlCmd += (if (compressed) " --compressed " else " ") + "\"${request.url}\""

        logger.log("╭------------------------------------------------")
        logger.log(curlCmd)
        logger.log("╰------------------------------------------------")

        return chain.proceed(request)
    }
}