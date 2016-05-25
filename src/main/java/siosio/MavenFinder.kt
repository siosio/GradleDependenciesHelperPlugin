package siosio

import java.io.*
import java.net.*
import java.util.*
import kotlin.text.*

class MavenFinder {

  companion object {

    /** mavenのアクセス結果からライブラリ名と最新バージョンを抽出する正規表現  */
    private val PATTERN = Regex("\\{\"id\":\"((?:[^:]+:){2})[^\"]+\"")

    /** バージョン番号を抽出する正規表現 */
    private val VERSION_PATTERN = Regex("\\{\"id\":\"([^\"]+)\"")

    private const val FIND_URL = "http://search.maven.org/solrsearch/select?q=a:\"%s\"&core=gav&rows=30&wt=json"

    /** バージョ番号を取得するURL */
    private const val FIND_VERSION_URL =
        "http://search.maven.org/solrsearch/select?q=g:\"%s\"+AND+a:\"%s\"&rows=20&core=gav&wt=json"

    fun MatchResult.group(index: Int) = this.groups.get(index)!!.value

    private fun getConnection(spec: String): HttpURLConnection {
      val url = URL(spec)
      return url.openConnection() as HttpURLConnection
    }

    private fun getResponse(connection: HttpURLConnection): InputStream {
      if (connection.responseCode != 200) {
        throw IOException("response is abnormal end.")
      }
      return connection.inputStream
    }
  }

  fun contains(searchParam: SearchParam): Boolean {
    return !search(FIND_VERSION_URL.format(searchParam.group, searchParam.name), VERSION_PATTERN).isEmpty()
  }

  fun find(searchParam: SearchParam): Set<String> {
    return if (searchParam.isFindVersion()) {
      search(
          FIND_VERSION_URL.format(searchParam.group, searchParam.name), VERSION_PATTERN)
    } else {
      search(FIND_URL.format(searchParam.text), PATTERN)
    }
  }

  private fun search(url: String, pattern: Regex): Set<String> {
    val result = LinkedHashSet<String>()
    val connection = getConnection(url)
    val stream = getResponse(connection)
    stream.use {
      val reader = BufferedReader(it.bufferedReader())
      val text = reader.readText()
      pattern.findAll(text).forEach {
        result.add(it.group(1))
      }
    }
    return result
  }
}

