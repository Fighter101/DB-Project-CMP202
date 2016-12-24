package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;


public class Controller {
        private final Integer assetID = 1;
        private static final Connector connector = new Connector("Restaurant", "127.0.0.1", "3306", "root", "AssAssin_108");
        private   @FXML VBox mealsVBox;
        private @FXML  Label priceLabel , mealslabel;
        private  @FXML TextField PriceTextField;
        private  List <Meal>  meals = connector.getMeals();
        private List <MealPair> orderedMeals = new ArrayList<MealPair>();
        private ObservableList<String> amountOptions = FXCollections.observableArrayList("1","2","3","4","5");
        private ObservableList<String> employeeNames = FXCollections.observableArrayList();
        private ObservableList<String> licenceNumbers = FXCollections.observableArrayList();
        private List<DelievryOrder> delievryOrders;
        private @FXML  VBox delievryOrdersVBox;
        @FXML protected  void  refresh()
        {
            PriceTextField.setText("0");
            mealsVBox.getChildren().clear();
            mealsVBox.getChildren().add(mealslabel);
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
                                orderedMeals.remove(new MealPair(mealName, Integer.valueOf(((String) amount.getValue()))));
                                PriceTextField.setText(new Integer(new Integer(PriceTextField.getText()) - (new Integer((String) amount.getValue()) * connector.getMealPrice(mealName))).toString());
                                amount.setValue(null);
                            }
                        }
                    }
                });
                checkBox.setPrefWidth(900);
                ComboBox comboBox = new ComboBox(amountOptions);
                comboBox.setEditable(true);
                comboBox.setDisable(true);
                comboBox.valueProperty().addListener(new ChangeListener<String> (){
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
                        HBox parent = (HBox) checkBox.getParent();
                        CheckBox chkbox = (CheckBox) parent.getChildren().get(0);
                        String text = chkbox.getText();
                        String mealName = text.substring(0 , text.indexOf('\n'));
                        if(oldValue!=null && newValue!=null) {
                            orderedMeals.remove(new MealPair(mealName, Integer.valueOf(oldValue)));
                            PriceTextField.setText(new Integer(new Integer(PriceTextField.getText()) - (new Integer((oldValue)) * connector.getMealPrice(mealName))).toString());

                        }
                        if(newValue!=null) {
                            orderedMeals.add(new MealPair(mealName, new Integer(newValue)));
                            PriceTextField.setText(new Integer(new Integer(PriceTextField.getText()) + (new Integer(newValue) * connector.getMealPrice(mealName))).toString());
                        }
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
            Order order = new Order(assetID);
            order.addOrder(connector);
            order.setStatus(Order.Status.Ordered);
            order.setMeals(orderedMeals);
            order.submitMeals(connector);
        }
        @FXML protected  void handleDelievryRefreshButtonPressed (MouseEvent event){

            List<Employee> employees = connector.getEmployees(assetID);
            for (Employee employee : employees){
                employeeNames.add(employee.getName());
            }
            List<String> vechiles = connector.getVechiles();
            licenceNumbers.addAll(vechiles);
            delievryOrders = connector.getdelievryorders(assetID);
            for (DelievryOrder delievryOrder : delievryOrders){
                HBox deliveryOrderHbo = new HBox();
                deliveryOrderHbo.setSpacing(10);
                Label deliveryOrderInfo = new Label("Client Name : "+delievryOrder.getClientName()+"\nCLient Address : "+delievryOrder.getAddress());
                Label employee = new Label("Employee Name");
                Label vechile = new Label("Vechile License No");
                ComboBox employeeBox = new ComboBox(employeeNames);
                ComboBox vechilesBox = new ComboBox(licenceNumbers);
                deliveryOrderHbo.getChildren().add(deliveryOrderInfo);
                deliveryOrderHbo.getChildren().add (employee);
                deliveryOrderHbo.getChildren().add(employeeBox);
                deliveryOrderHbo.getChildren().add(vechile);
                deliveryOrderHbo.getChildren().add(vechilesBox);
                Button deliever = new Button("Deliver Order");
                deliever.setOnMousePressed(MouseEvent -> {
                    if(employeeBox.getValue()!=null && vechilesBox.getValue()!=null){
                        connector.deliverOrder(delievryOrder , employees.get(employeeBox.getSelectionModel().getSelectedIndex()) , (String)vechilesBox.getValue());
                    }
                });
                deliveryOrderHbo.getChildren().add(deliever);
                delievryOrdersVBox.getChildren().add(deliveryOrderHbo);
            }

        }
}
