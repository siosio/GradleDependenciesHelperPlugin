package siosio

import com.intellij.util.net.*
import org.apache.http.client.config.*
import org.apache.http.client.methods.*
import org.apache.http.impl.client.*
import java.util.concurrent.*

object Client {

    fun get(uri: String): String {
        val clientBuilder = RequestConfig.custom()
        IdeHttpClientHelpers.ApacheHttpClient4.setProxyForUrlIfEnabled(clientBuilder, uri)

        val response: CloseableHttpResponse = try {
            HttpClients.custom()
                    .setDefaultRequestConfig(clientBuilder.build())
                    .setConnectionTimeToLive(1, TimeUnit.SECONDS)
                    .build()
                    .execute(HttpGet(uri))
        } catch (e: Exception) {
            return ""
        }

        return when (response.statusLine.statusCode) {
            200 -> response.entity.content.reader().readText()
            else -> ""
        }
    }
}