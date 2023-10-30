package application;

import java.util.Optional;

import application.news.User;

import serverConection.ConnectionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginController {
//TODO Add all attribute and methods as needed 
	@FXML
	private AnchorPane loginWindow;

	@FXML
	private PasswordField password;

	@FXML
	private TextField userId;

	private LoginModel loginModel = new LoginModel();

	private User loggedUsr = null;

	public LoginController() {

		// Uncomment next sentence to use data from server instead dummy data
		// loginModel.setDummyData(false);
	}

	User getLoggedUsr() {
		return loggedUsr;

	}

	void setConnectionManager(ConnectionManager connection) {
		this.loginModel.setConnectionManager(connection);
	}

	@FXML
	void closeWindow(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();

	}

	@FXML
	void login(ActionEvent event) {
		if (validateFields()) {
			this.closeWindow(event);
		} else {
			Alert alert = new Alert(AlertType.ERROR, "Login failed");
			alert.showAndWait();
		}
	}

	private boolean validateFields() {
		if (userId.getText().isEmpty()) {
			userId.setStyle("-fx-border-color: red ; -fx-border-width: 1px ; -fx-border-radius: 3px;");
		}

		if (password.getText().isEmpty()) {
			password.setStyle("-fx-border-color: red ; -fx-border-width: 1px ; -fx-border-radius: 3px;");
		}

		return !userId.getText().isEmpty() && !password.getText().isEmpty();
	}

	private boolean validateUser() {
		return true;
	}
}