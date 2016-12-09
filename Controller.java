package sample;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.List;


public class Controller {
        private static final Connector connector = new Connector("Restaurant", "127.0.0.1", "3306", "root", "AssAssin_108");
        private   @FXML VBox mealsVBox;
        private  List <Meal>  meals = connector.getMeals();
        @FXML protected  void  refresh()
        {
            for(Meal u : meals){
                CheckBox checkBox = new CheckBox(u.getName()+"\n\t"+u.getDescription());
                checkBox.setWrapText(false);
                checkBox.setIndeterminate(false);
                mealsVBox.getChildren().add(checkBox);
            }
        }
        @FXML protected void handleRefreshButtonPressed (MouseEvent event){
            meals = connector.getMeals();
            refresh();
        }

}
