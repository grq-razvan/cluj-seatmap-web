import seatmap.retriever.RandomNumberRetriever;

public class Application {
    public static final String RANDOM_INT_URL = "https://www.random.org/integers/?num=1&min=1&max=100000&col=5&base=10&format=html&rnd=new&format=plain";

    public static void main(String... args) {
        System.out.println("new random number retrieved: "
                + new RandomNumberRetriever().retrieveNewSeedNumber(RANDOM_INT_URL));
    }
}