package websearch

import org.jsoup.Jsoup.connect
import org.jsoup.nodes.Document
import java.io.IOException
import java.util.*

class URL(val url: String) {
  override fun toString(): String = url
  override fun equals(other: Any?): Boolean = url == other.toString()
  fun download(): WebPage {
    return try {
      val page = WebPage(connect(url).get())
      println("Downloaded $url successfully")
      page
    } catch (e: IOException) {
      println("Unable to download $url")
      WebPage(Document("")) // returns an empty document
    }
  }
}

class WebPage(val doc: Document) {
  fun extractWords(): List<String> =
    doc.text().lowercase().split("\\s+".toRegex()).map { word ->
      word.replace("""^[,\.]|[,\.]$""".toRegex(), "")
    }

  fun extractLinks(): List<URL> =
    doc.getElementsByTag("a").map {
      it.attr("href")
    }.filter {
      it.startsWith("https://") || it.startsWith("http://")
    }.map { URL(it) }
}
