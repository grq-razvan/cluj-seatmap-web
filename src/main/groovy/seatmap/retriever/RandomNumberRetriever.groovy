package seatmap.retriever

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class RandomNumberRetriever {
    public int retrieveNewSeedNumber(String uri) {
        Document doc = Jsoup.connect(uri).get()
        return Integer.parseInt(doc.select("body").first().text())
    }
}
