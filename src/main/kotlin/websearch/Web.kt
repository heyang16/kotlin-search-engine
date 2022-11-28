package websearch

import org.jsoup.nodes.Document
import java.util.*

class URL(val url: String) {
  override fun toString(): String = url
}

class WebPage(val doc: Document) {

  fun extractWords(): List<String> =
    doc.text().lowercase().split("\\s+".toRegex()).map { word ->
      word.replace("""^[,\.]|[,\.]$""".toRegex(), "")
    }
}
