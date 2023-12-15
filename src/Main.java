import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class Main {
    public static void main(String[] args) {

        String url = "https://pokeapi.co/api/v2/pokemon?limit=250&offset=0";

        HttpResponse<String> response = getPokemonByName(url);
        String respBody = response.body();
        System.out.println("respBody --> "+respBody);
        //getPokemonByNameAsync(url);

    }

    private static void getPokemonByNameAsync(String url) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        System.out.println("###ASYNC CALL###");
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                .join();
    }

    private static HttpResponse<String> getPokemonByName(String url) {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        //This requires to be inside of a try/catch block or it add the throws exception on method
        //signature
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("###SYNC CALL###");
            System.out.println(response.statusCode());
            //System.out.println(response.body());
            return response;
        } catch (IOException | InterruptedException ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }

    class Pokemon {
        private String Name;
        private String Id;
        private Double Weight;
        private String imageURL;
    }
}