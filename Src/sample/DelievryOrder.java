package sample;

/**
 * Created by hassan on 12/24/16.
 */
public class DelievryOrder extends Order {

    public DelievryOrder(int assetID) {
        super(assetID);
    }
    private  String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Integer phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    private  Integer phoneNumber;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    private  String clientName;
}
