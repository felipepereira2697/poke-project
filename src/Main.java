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

        HttpResponse<String> response = getPokemonData(url);
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
            //Building pokemon instance
            String pokemonURL = findPokemonUrl(s);
            String pokemonName = findPokemonName(s);
            String pokemonImage = findPokemonImage(pokemonURL);
            Pokemon pokemon = new Pokemon(pokemonName, pokemonURL, pokemonImage);

            pokemonList.add(pokemon);
        }

        pokemonList.forEach(System.out::println);
        return pokemonList;
    }

    private static String findPokemonImage(String pokemonURL) {

        String pokeUrl = pokemonURL.substring(pokemonURL.indexOf("/pokemon/"), pokemonURL.lastIndexOf("/"));
        String pokeId = pokeUrl.replace("/pokemon/","");
        System.out.println("pokeId "+pokeId);

        HttpResponse<String> response = getPokemonData(pokemonURL);
        String respBody = response.body();
        String sprites = respBody.substring(respBody.indexOf("\"sprites\":"), respBody.indexOf("\"front_female\""));
        String pokemonImg = sprites.substring(sprites.indexOf("\"front_default\"")).replaceAll("\"","").replace(",","").replace("front_default:","");

        return pokemonImg;
    }


    private static void generateHTML(ArrayList<Pokemon> pokemonsList) {
        File file = new File("index.html");


        String htmlBody = "<!DOCTYPE html><html><head><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"><style>.card{margin:40px;box-shadow:0 4px 8px 0 rgba(0,0,0,.2);transition:.3s;width:40%}.card:hover{box-shadow:0 8px 16px 0 rgba(0,0,0,.2)}.container{padding:2px 16px}</style></head><body><h2>Pokemon list</h2>";

        for (Pokemon item : pokemonsList) {

            String pokemon = "<div class=\"card\"><img src=\""+item.getImage()+"\" alt=\""+item.getName()+"\" style=\"width:100%\"><div class=\"container\"><h4><b>"+item.getName().toUpperCase()+"</b></h4><p><a href=\""+item.getUrl()+"\" Click here to have more information</a></p></div></div>";
            htmlBody = htmlBody.concat(pokemon);
        }
        htmlBody.concat("</body></html>");

        try {
            PrintWriter pw = new PrintWriter(file);
            pw.write(htmlBody);
            pw.close();
        }catch (FileNotFoundException ex ) {
            System.out.println(ex.getMessage());
        }

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

    private static HttpResponse<String> getPokemonData(String url) {

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