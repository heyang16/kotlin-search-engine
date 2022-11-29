package websearch

class SearchEngine(val corpus: Map<URL, WebPage>) {
  var index: Map<String, List<SearchResult>> = emptyMap()

  // process the downloaded webpages, compute
  // the index, and store the results in the index map
  fun compileIndex() {
    // Creates a list of pairs of (word, url)
    val pairs: MutableList<Pair<String, URL>> = mutableListOf()
    for (e in corpus.entries) {
      for (w in e.value.extractWords()) {
        pairs += Pair(w, e.key) // Makes a pair of (word, url)
      }
    }
    // Groups the urls that share correspond to the same word, then converts
    // urls into SearchResults
    index =
      pairs.groupBy({ it.first }, { it.second }).mapValues { rank(it.value) }
  }

  // Given a list of URLs, return of SearchResults where each SearchResult
  // includes a distinct url and the number of times it appears in the list
  // sorts the URLs in descending order of number of references
  fun rank(l: List<URL>): List<SearchResult> =
    l.groupingBy { it }.eachCount().map { e -> SearchResult(e.key, e.value) }
      .sortedBy { r -> r.numRefs }.reversed()

  fun searchFor(query: String): SearchResultsSummary =
    SearchResultsSummary(query, index[query])
}

class SearchResult(val url: URL, val numRefs: Int)

class SearchResultsSummary(
  val query: String,
  val results: List<SearchResult>?
) {
  override fun toString(): String {
    return if (results == null) "No results for $query found."
    else {
      val s =
        results.joinToString { r -> "  ${r.url} - ${r.numRefs} references" }
      return "Found ${results.size} results for $query:\n" + s
    }
  }
}

