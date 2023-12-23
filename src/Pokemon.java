import java.util.List;

public class Pokemon {
    private String name;
    private String url;
    private String image;

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
    public Pokemon(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public Pokemon(String name, String url, String image) {
        this.name = name;
        this.url = url;
        this.image = image;
    }

    @Override
    public String toString() {

        return "Hi! My name is "+name+" and more information can be found at: "+url+" and please refer to my image here "+image;
    }
}
