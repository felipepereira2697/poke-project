public class Pokemon {
    private String name;
    private String url;

    public Pokemon(String name, String url) {
        this.name = name;
        this.url = url;
    }

    @Override
    public String toString() {

        return "Hi! My name is "+name+" and more information can be found at: "+url;
    }
}
