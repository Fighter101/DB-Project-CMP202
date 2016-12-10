package sample;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.List;


public class Controller {
        private static final Connector connector = new Connector("Restaurant", "127.0.0.1", "3306", "root", "AssAssin_108");
        private   @FXML VBox mealsVBox;
        private @FXML  Label priceLabel , mealsLabel;
        private  List <Meal>  meals = connector.getMeals();
        @FXML protected  void  refresh()
        {
            mealsVBox.getChildren().clear();
            mealsVBox.getChildren().add(mealsLabel);
            for(Meal u : meals){
                CheckBox checkBox = new CheckBox(u.getName()+"\n\t"+u.getDescription());
                checkBox.setWrapText(false);
                checkBox.setIndeterminate(false);
                checkBox.selectedProperty().addListener(new ChangeListener<Boolean>(){
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue){
                        if(newValue == true)
                            priceLabel.setText("S" + meals.indexOf(u));
                        else priceLabel.setText("U"+meals.indexOf(u));
                    }
                });
                mealsVBox.getChildren().add(checkBox);

            }
        }
        @FXML protected void handleRefreshButtonPressed (MouseEvent event){
            meals = connector.getMeals();
            refresh();
        }

}
