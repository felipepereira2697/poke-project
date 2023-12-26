import java.net.http.HttpResponse;
import java.util.List;


public class Main {
    public static void main(String[] args) {

        String url = "https://pokeapi.co/api/v2/pokemon?limit=30&offset=0";
        HttpResponse<String> response = HttpHandler.getData(url);

        String respBody = "";
        if (response != null) {
            respBody = response.body();
        }

        List<Pokemon> pokemonsList =  PokemonBO.parseJsonPokemons(respBody);
        HTMLGenerator.generateHTML(pokemonsList);

        //async call to api if needed.
        //getPokemonByNameAsync(url);
    }


}