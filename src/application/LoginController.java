package application;

import application.news.User;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import serverConection.ConnectionManager;

public class LoginController {
	@FXML
	private AnchorPane loginWindow;

	@FXML
	private MFXPasswordField password;

	@FXML
	private MFXTextField userId;

	private LoginModel loginModel = new LoginModel();

	private User loggedUsr = null;

	public LoginController() {
		// Uncomment next sentence to use data from server instead dummy data
		this.loginModel.setDummyData(false);
	}

	User getLoggedUsr() {
		return loggedUsr;
	}

	void sendData(ConnectionManager connection, User usr) {
		this.loggedUsr = usr;
		this.loginModel.setConnectionManager(connection);
	}

	@FXML
	void closeWindow(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@FXML
	void login(ActionEvent event) {
		// TODO Niki Remove
		userId.setText("DEV_TEAM_01");
		password.setText("123501@3");
		if (validateFields() && validateUser()) {
			this.closeWindow(event);
		} else {
			new Alert(AlertType.ERROR, "Login failed").showAndWait();
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
		loggedUsr = loginModel.validateUser(userId.getText(), password.getText());
		return loggedUsr != null;
	}
}