package sample;

/**
 * Created by hassan on 12/23/16.
 */
public class MealPair {
    private String mealName;
    private Integer mealAmount;

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public Integer getMealAmount() {
        return mealAmount;
    }

    public void setMealAmount(Integer mealAmount) {
        this.mealAmount = mealAmount;
    }

    public MealPair(String mealName, Integer mealAmount) {
        this.mealName = mealName;
        this.mealAmount = mealAmount;
    }
    @Override
    public boolean equals (Object other){
        if(other == null) return false;
        if(other == this) return true;
        if(!(other instanceof MealPair))
            return false;
        if(this.mealName.equals(((MealPair) other).mealName))
            return true;
        else return false;
    }
}
