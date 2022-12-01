package websearch

import java.util.concurrent.ThreadLocalRandom

class WebCrawler(val startFrom: URL) {
  val limit = 30 // Maximum amount of downloads
  val downloaded: MutableMap<URL, WebPage> = mutableMapOf()

  // Webcrawler starts at a starting link, extract all links, downloads them,
  // and picks an unvisited link from the downloaded links and repeats
  fun run() {
    // The reason why I'm not using a mutable set is that problems arise
    // when using the method contains() on a mutable set
    val visited: MutableList<URL> = mutableListOf(startFrom)
    val toVisit: MutableList<URL> =
      startFrom.download().extractLinks().toMutableList()

    while (downloaded.size <= limit && toVisit.size > 0) {
      // Picks a random url from the list of unvisited urls
      val randomIndex = ThreadLocalRandom.current().nextInt(toVisit.size)
      val newurl = toVisit[randomIndex]
      // Downloads the first unvisited page
      if (!visited.contains(newurl)) {
        val newPage = newurl.download()
        downloaded[newurl] = newPage
        toVisit += newPage.extractLinks() // Adds all the links from the page
      }
      toVisit.removeAt(randomIndex)
      visited.add(newurl)
    }
  }

  // Returns the map of urls to their downloaded webpages
  fun dump() = downloaded
}

fun main() {
  val crawler =
    WebCrawler(startFrom = URL("https://bbc.co.uk"))
  crawler.run()
  val searchEngine = SearchEngine(crawler.dump())
  searchEngine.compileIndex()
  println(searchEngine.searchFor("news"))
}
