import java.net.http.HttpResponse;
import java.util.ArrayList;


public class Main {
    public static void main(String[] args) {

        String url = "https://pokeapi.co/api/v2/pokemon?limit=10&offset=0";

        HttpResponse<String> response = PokemonBO.getPokemonData(url);
        String respBody = response.body();

        ArrayList<Pokemon> pokemonsList =  PokemonBO.parseJsonPokemons(respBody);

        HTMLGenerator.generateHTML(pokemonsList);

        //async call to api if needed.
        //getPokemonByNameAsync(url);

    }


}