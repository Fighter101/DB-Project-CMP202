package sample;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hassan on 12/9/16.
 */
public class Order {
    enum Status
    {
        Ordered,
        Cooked,
        Served
    }
    private float tax;
    private int assetID;
    private int recordID;
    private Status status;
    private int ID;
    private List<MealPair> Meals;
    public Order(int assetID) {
        this.assetID = assetID;
        Meals = new ArrayList<MealPair>();
    }

    public List<MealPair> getMeals() {
        return Meals;
    }

    public float getTax() {
        return tax;
    }

    public void setTax(float tax) {
        this.tax = tax;
    }

    public int getAssetID() {
        return assetID;
    }

    public void setAssetID(int assetID) {
        this.assetID = assetID;
    }

    public int getRecordID() {
        return recordID;
    }

    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void addOrder (Connector connector)
    {
        connector.addOrder(assetID , this);
    }
    public void addMeal (String mealName , Integer mealAmount){
        Meals.add(new MealPair(mealName , mealAmount));
    }

    public void setMeals(List<MealPair> meals) {
        Meals = meals;
    }
    public void submitMeals (Connector connector){
        connector.addMealsToOrder(this);
    }
}
