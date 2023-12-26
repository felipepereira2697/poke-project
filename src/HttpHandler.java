import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpHandler {

    static HttpResponse<String> getData(String url) {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        //This requires to be inside of a try/catch block or it add the throws exception on method
        //signature
        try {
            System.out.println("###SYNC CALL###");
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response;
        } catch (IOException | InterruptedException ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }

    private static void getAsyncData(String url) {
        System.out.println("###ASYNC CALL###");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                .join();
    }
}
