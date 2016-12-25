package sample;

/**
 * Created by Hassan on 12/26/2016.
 */
public class ClosableOrder extends Order {


    public ClosableOrder(int assetID) {
        super(assetID);
    }
    private Float Price;

    public Float getPrice() {
        return Price;
    }

    public void setPrice(Float price) {
        Price = price;
    }

    @Override
    public boolean equals (Object other){

        if(other == null) return false;
        if(other == this) return true;
        if(!(other instanceof ClosableOrder))
            return false;
        if(Integer.valueOf(this.getID()).equals(Integer.valueOf(((ClosableOrder) other).getID()))) {
            return true;
        }
        else return false;
    }
}
