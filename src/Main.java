import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
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

        ArrayList<Pokemon> pokemonsList =  parseJsonPokemons(respBody);

        generateHTML(pokemonsList);

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

            pokemonList.add(pokemon);
        }

        pokemonList.forEach(System.out::println);
        return pokemonList;
    }


    private static void generateHTML(ArrayList<Pokemon> pokemonsList) {
        File file = new File("index.html");

        String head = "<html><body><h1 style=\"color: yellow;\">Pokemon List</h1>";
       for (Pokemon item : pokemonsList) {
           String pokemon = "<div>"+item.getName()+" - "+item.getUrl()+" </div>";
           head = head.concat(pokemon);
       }
       head.concat("</body></html>");

        try {
            PrintWriter pw = new PrintWriter(file);
            pw.write(head);
            pw.close();
        }catch (FileNotFoundException ex ) {
            System.out.println(ex.getMessage());
        }


//        System.out.println(head);
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