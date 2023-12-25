import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PokemonBO {
    static List<Pokemon> parseJsonPokemons(String json) {
        List<Pokemon> pokemonList = new ArrayList<Pokemon>();
        Matcher matcher = Pattern.compile(".*\\[(.*)\\].*").matcher(json);
        if(!matcher.matches()) {
            throw  new IllegalArgumentException("No match in "+json);
        }

        String[] pokemonArray = matcher.group(1).split("\\},\\{");

        for (String item : pokemonArray) {
            //Building pokemon instance
            String pokemonURL = findPokemonUrl(item);
            String pokemonName = findPokemonName(item);
            String pokemonImage = findPokemonImage(pokemonURL);
            Pokemon pokemon = new Pokemon(pokemonName, pokemonURL, pokemonImage);

            pokemonList.add(pokemon);
        }

        pokemonList.forEach(System.out::println);
        return pokemonList;
    }

    private static String findPokemonImage(String pokemonURL) {

        String pokeUrl = pokemonURL.substring(pokemonURL.indexOf("/pokemon/"), pokemonURL.lastIndexOf("/"));

        HttpResponse<String> response = getPokemonData(pokemonURL);
        String respBody = null;
        if (response != null) {
            respBody = response.body();
        }
        String sprites = respBody.substring(respBody.indexOf("\"sprites\":"), respBody.indexOf("\"front_female\""));
        String pokemonImg = sprites.substring(sprites.indexOf("\"front_default\"")).replaceAll("\"","").replace(",","").replace("front_default:","");

        return pokemonImg;
    }

    private static String findPokemonName(String s) {
        String sanitizedString = s.substring(s.indexOf("\"name\":\""), s.indexOf("\","));
        String pokemonName = sanitizedString.replace("\"name\":\"","");

        return pokemonName;
    }

    //The idea of this method is to receive
    //E.g: //"name":"lugia","url":"https://pokeapi.co/api/v2/pokemon/249/"
    //Return: https://pokeapi.co/api/v2/pokemon/249/
    private static String findPokemonUrl(String s) {
        String sanitizeUrl = s.substring(s.indexOf("\"url\":\""));
        String pokemonUrl = sanitizeUrl.replace("\"url\":\"","").replace("\"","").replace("}","");

        return pokemonUrl;
    }

    private static void getPokemonByNameAsync(String url) {
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

    static HttpResponse<String> getPokemonData(String url) {

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
}
