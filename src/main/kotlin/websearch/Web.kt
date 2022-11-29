package websearch

import org.jsoup.Jsoup.connect
import org.jsoup.nodes.Document
import java.util.*

class URL(val url: String) {
  override fun toString(): String = url
  override fun equals(other: Any?): Boolean = url == other.toString()
  fun download(): WebPage = WebPage(connect(url).get())
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
