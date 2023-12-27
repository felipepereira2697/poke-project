import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class HTMLGenerator {

    static void generateHTML(List<Pokemon> pokemonsList) {
        File file = new File("index.html");


        String htmlHead = "<!DOCTYPE html><html><head><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"><style>.main-container{display:flex;flex-direction:row;flex-wrap:wrap;}.card{max-width: 200px; padding: 16px; margin:40px;box-shadow:0 4px 8px 0 rgba(0,0,0,.2);transition:.3s;width:40%}.card:hover{box-shadow:0 8px 16px 0 rgba(0,0,0,.2)}.container{padding:2px 16px}</style></head><body><h2>Pokemon list</h2>" +
                "<div class=\"main-container\">";
        String body = "";

        for (Pokemon item : pokemonsList) {
            String pokemon = "<div class=\"card\"><img src=\""+item.image()+"\" alt=\""+item.name()+"\" style=\"width:100%\"><div class=\"container\"><h4><b>"+item.name().toUpperCase()+"</b></h4><p><a href=\""+item.url()+"\"> Click here to have more information</a></p></div></div>";
            if(!body.contains(item.url())) {
                body = body.concat(pokemon);
                System.out.println("Pokemon Item "+body);
            }

        }

        try {
            PrintWriter pw = new PrintWriter(file);
            pw.write(htmlHead.concat(body).concat("</div></body></html>"));
            pw.close();

        }catch (FileNotFoundException ex ) {
            System.out.println(ex.getMessage());
        }

    }
}
