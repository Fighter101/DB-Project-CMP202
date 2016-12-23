package sample;

/**
 * Created by hassan on 12/9/16.
 */
public class Meal {

    public Meal(String name, String description, float price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    private String name;
    private String description;
    private float price;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }



}
