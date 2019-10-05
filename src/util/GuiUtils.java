package util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class GuiUtils {
	public static void showAlert(String msg){
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.show();
	}
    public static String getCurrentTimeString(){
        Date date=new Date();
        DateFormat format=new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        return format.format(date);
    }
}
