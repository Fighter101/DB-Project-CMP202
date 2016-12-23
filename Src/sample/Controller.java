package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;


public class Controller {
        private static final Connector connector = new Connector("Restaurant", "127.0.0.1", "3306", "root", "AssAssin_108");
        private   @FXML VBox mealsVBox;
        private @FXML  Label priceLabel , mealsLabel;
        private  @FXML TextArea PriceTextArea;
        private  List <Meal>  meals = connector.getMeals();
        private List <MealPair> orderedMeals = new ArrayList<MealPair>();
        private ObservableList<Integer> options = FXCollections.observableArrayList(1,2,3,4,5);
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
                ComboBox comboBox = new ComboBox(options);
                comboBox.setEditable(true);

                mealsVBox.getChildren().add(checkBox);
                mealsVBox.getChildren().add(comboBox);

            }
        }
        @FXML protected void handleRefreshButtonPressed (MouseEvent event){
            meals = connector.getMeals();
            refresh();
        }
        @FXML protected  void handleCreateOrderButtonPressed (MouseEvent  event){
            Order order = new Order(1);
            order.addOrder(connector);

        }

}
