import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {
    public static void main(String[] args) {

        String url = "https://pokeapi.co/api/v2/pokemon?limit=250&offset=0";

        HttpResponse<String> response = getPokemonByName(url);
        String respBody = response.body();
        System.out.println("respBody --> "+respBody);
        parseJsonPokemons(respBody);
        //getPokemonByNameAsync(url);

    }

    private static String[] parseJsonPokemons(String json) {

        Matcher matcher = Pattern.compile(".*\\[(.*)\\].*").matcher(json);
        if(!matcher.matches()) {
            throw  new IllegalArgumentException("No match in "+json);

        }

        String[] pokemonArray = matcher.group(1).split("\\},\\{");
        pokemonArray[0] = pokemonArray[0].substring(1);
        System.out.println("Pokemon array at 0 --> "+pokemonArray[0]);
        return pokemonArray;
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