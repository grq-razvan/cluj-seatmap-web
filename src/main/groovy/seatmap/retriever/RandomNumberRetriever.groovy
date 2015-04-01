package seatmap.retriever

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Service

@Service
class RandomNumberRetriever {
    public int retrieveNewSeedNumber(String uri) {
        Document doc = Jsoup.connect(uri).get()
        return Integer.parseInt(doc.select("body").first().text())
    }
}
