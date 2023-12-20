import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {
    public static void main(String[] args) {

        String url = "https://pokeapi.co/api/v2/pokemon?limit=250&offset=0";

        HttpResponse<String> response = getPokemonByName(url);
        String respBody = response.body();

        parseJsonPokemons(respBody);

        //async call if needed.
        //getPokemonByNameAsync(url);

    }

    private static ArrayList<Pokemon> parseJsonPokemons(String json) {
        ArrayList<Pokemon> pokemonList = new ArrayList<Pokemon>();
        Matcher matcher = Pattern.compile(".*\\[(.*)\\].*").matcher(json);
        if(!matcher.matches()) {
            throw  new IllegalArgumentException("No match in "+json);

        }

        String[] pokemonArray = matcher.group(1).split("\\},\\{");
        for (String s : pokemonArray) {

            Pokemon pokemon = new Pokemon(findPokemonName(s), findPokemonUrl(s));
            System.out.println(pokemon.toString());
            pokemonList.add(pokemon);
        }


        return pokemonList;
    }


    private static String findPokemonName(String s) {
        String newS = s.substring(s.indexOf("\"name\":\""), s.indexOf("\","));
        String cleanString = newS.replace("\"name\":\"","");

        return cleanString;
    }

    //The idea of this method is to receive
    //E.g: //"name":"lugia","url":"https://pokeapi.co/api/v2/pokemon/249/"
    //Return: https://pokeapi.co/api/v2/pokemon/249/
    private static String findPokemonUrl(String s) {
        String newS = s.substring(s.indexOf("\"url\":\""));
        String cleanString = newS.replace("\"url\":\"","").replace("\"","").replace("}","");

        return cleanString;
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

}