package sample;

import java.time.LocalDate;

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
    public Order(float tax, int assetID, int recordID) {
        this.tax = tax;
        this.assetID = assetID;
        this.recordID = recordID;
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
    public void addOrder (Connector connector)
    {
        //INSERT VALUES (Status , Tax , AssetID , RecordID) VALUES (' ' , ' ' , ' ' , ' ')
        if(connector.exists("Records" , "WHERE AssetID = " + String.valueOf(assetID) + "AND Date = " + LocalDate.now().toString())){
            connector.insert("Records" , new Pair("Date" , LocalDate.now().toString()) , new Pair("AssetID" , String.valueOf(assetID)));
        }
        connector.insert("Orders" , new Pair("Status" , status.toString()), new Pair("Tax" , String.valueOf(tax)), new Pair ("AssetID" , String .valueOf(assetID)) , new Pair("RecordID" , String.valueOf(recordID)));
        //TODO don't forget to update the records
        //connector.update("Records" , "Where AssetID = " + String.valueOf(assetID) +"AND Date = '" + LocalDate.now().toString()+"'" ,  );
    }
}
