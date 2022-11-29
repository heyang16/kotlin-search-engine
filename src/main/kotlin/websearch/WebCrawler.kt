package websearch

import org.jsoup.HttpStatusException
import java.io.IOException
import java.util.concurrent.ThreadLocalRandom

class WebCrawler(val startFrom: URL) {
  val limit = 40
  val downloaded: MutableMap<URL, WebPage> = mutableMapOf()

  fun run() {
    val outmap: MutableMap<URL, WebPage> = mutableMapOf()
    val visited: MutableList<URL> = mutableListOf()
    val notVisited: MutableList<URL> =
      try {
        startFrom.download().extractLinks().toMutableList()
      } catch (e: IOException) {
        println("Cannot download $startFrom")
        when (e) {
          is HttpStatusException -> println("Error: Http Status Exception")
        }
        return
      }
    while (outmap.size <= limit && notVisited.size > 0) {
      // Picks a random url from the list of unvisited urls
      val newurl = notVisited[
        ThreadLocalRandom.current().nextInt(notVisited.size)
      ]
      // Downloads the first unvisited page
      try {
        if (!visited.contains(newurl)) {
          val newPage = newurl.download()
          outmap[newurl] = newPage
          notVisited += newPage.extractLinks() // Adds all the links from the page
        }
      } catch (e: IOException) {
        println("$newurl cannot be downloaded")
      }
      visited += newurl
      notVisited.removeAt(0)
    }
    downloaded.putAll(outmap)
  }

  // Returns the map of urls to their downloaded webpages
  fun dump() = downloaded
}

fun main() {
  val crawler =
    WebCrawler(startFrom = URL("https://www.linkedin.com/school/imperial-college-london/"))
  crawler.run()
  val searchEngine = SearchEngine(crawler.dump())
  searchEngine.compileIndex()
  println(searchEngine.searchFor("malaysia"))
}
