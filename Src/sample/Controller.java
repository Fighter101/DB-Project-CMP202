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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;


public class Controller {
        private static final Connector connector = new Connector("Restaurant", "127.0.0.1", "3306", "root", "AssAssin_108");
        private   @FXML VBox mealsVBox;
        private @FXML  Label priceLabel , mealsLabel;
        private  @FXML TextArea PriceTextArea;
        private  List <Meal>  meals = connector.getMeals();
        private List <MealPair> orderedMeals = new ArrayList<MealPair>();
        private ObservableList<String> options = FXCollections.observableArrayList("1","2","3","4","5");

        @FXML protected  void  refresh()
        {
            PriceTextArea.setText("0");
            mealsVBox.getChildren().clear();
            mealsVBox.getChildren().add(mealsLabel);
            for(Meal u : meals){
                CheckBox checkBox = new CheckBox(u.getName()+"\n\t"+u.getDescription()+"\t\t");
                checkBox.setWrapText(false);
                checkBox.setIndeterminate(false);
                checkBox.selectedProperty().addListener(new ChangeListener<Boolean>(){
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue){
                        HBox parent = (HBox) checkBox.getParent();
                        ComboBox amount= (ComboBox)parent.getChildren().get(1);
                        String text = checkBox.getText();
                        String mealName = text.substring(0 , text.indexOf('\n'));
                        if(newValue == true) {
                            amount.setDisable(false);
                        }
                        else {
                            amount.setDisable(true);
                            if(amount.getValue()!= null) {
                                orderedMeals.remove(new MealPair(mealName, (Integer) amount.getValue()));
                                PriceTextArea.setText(new Integer(new Integer(PriceTextArea.getText()) - (new Integer((Integer)amount.getValue()) * connector.getMealPrice(mealName))).toString());
                            }
                        }
                    }
                });
                checkBox.setPrefWidth(900);
                ComboBox comboBox = new ComboBox(options);
                comboBox.setEditable(true);
                comboBox.setDisable(true);
                comboBox.valueProperty().addListener(new ChangeListener<String> (){
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
                        HBox parent = (HBox) checkBox.getParent();
                        CheckBox chkbox = (CheckBox) parent.getChildren().get(0);
                        String text = chkbox.getText();
                        String mealName = text.substring(0 , text.indexOf('\n'));
                        orderedMeals.add(new MealPair(mealName , new Integer(newValue)));
                        PriceTextArea.setText(new Integer (new Integer(PriceTextArea.getText())+(new Integer(newValue)*connector.getMealPrice(mealName))).toString());
                    }
                });
                HBox hBox = new HBox();
                hBox.getChildren().add(checkBox);
                hBox.getChildren().add(comboBox);
                mealsVBox.getChildren().add(hBox);

            }
        }
        @FXML protected void handleRefreshButtonPressed (MouseEvent event){
            meals = connector.getMeals();
            refresh();
        }
        @FXML protected  void handleCreateOrderButtonPressed (MouseEvent  event){
            Order order = new Order(1);
            order.addOrder(connector);
            order.setMeals(orderedMeals);
            order.submitMeals(connector);

        }

}
