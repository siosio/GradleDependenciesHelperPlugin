package siosio

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.TreeSet
import java.util.regex.Pattern

public class MavenFinder {

  companion object {

    /** mavenのアクセス結果からライブラリ名と最新バージョンを抽出する正規表現  */
    private val PATTERN = Pattern.compile("\\{\"id\":\"((?:[^:]+:){2})[^\"]+\"")

    /** バージョン番号を抽出する正規表現 */
    private val VERSION_PATTERN = Pattern.compile("\\{\"id\":\"([^\"]+)\"")

    private val FIND_URL = "http://search.maven.org/solrsearch/select?q=a:\"%s\"&core=gav&rows=30&wt=json"

    /** バージョ番号を取得するURL */
    private val FIND_VERSION_URL =
        "http://search.maven.org/solrsearch/select?" +
            "q=g:\"%s\"+AND+a:\"%s\"&rows=20&core=gav&wt=json"

    private fun getConnection(spec: String): HttpURLConnection {
      val url = URL(spec)
      return url.openConnection() as HttpURLConnection
    }

    private fun getResponse(connection: HttpURLConnection): InputStream {
      if (connection.getResponseCode() != 200) {
        throw IOException("response is abnormal end.")
      }
      return connection.getInputStream()
    }
  }

  fun find(searchParam:SearchParam):Set<String> {
    return if (searchParam.isFindVersion()) {
      search(
          FIND_VERSION_URL.format(searchParam.group, searchParam.name), VERSION_PATTERN)
    } else {
      search(FIND_URL.format(searchParam.text), PATTERN)
    }
  }

  private fun search(url: String, pattern: Pattern): Set<String> {
    val result = TreeSet<String>()
    val connection = getConnection(url)
    val stream = getResponse(connection)
    stream.use {
      val reader = BufferedReader(it.bufferedReader())
      val text = reader.readText()
      val matcher = pattern.matcher(text)
      while (matcher.find()) {
        result.add(matcher.group(1))
      }
    }
    return result.descendingSet()
  }
}

