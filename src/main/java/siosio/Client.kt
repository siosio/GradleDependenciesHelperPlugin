package siosio

import org.apache.http.client.methods.*
import org.apache.http.impl.client.*

object Client {

    fun get(uri: String): String {
        val response = HttpClients.createDefault()
                .execute(HttpGet(uri))

        return when (response.statusLine.statusCode) {
            200 -> response.entity.content.reader().readText()
            else -> ""
        }
    }
}