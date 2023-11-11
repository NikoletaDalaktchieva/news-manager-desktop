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

	private static final String RED_BORDER = "-fx-border-color: red ; -fx-border-width: 1px ; -fx-border-radius: 3px;";
	private static final String BLACK_BORDER = "-fx-border-color: black ; -fx-border-width: 0px ; -fx-border-radius: 3px;";

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
		if (validateFields() && validateUser()) {
			this.closeWindow(event);
		} else {
			new Alert(AlertType.ERROR, "Login failed").showAndWait();
		}
	}

	private boolean validateFields() {
		if (userId.getText().isEmpty()) {
			userId.setStyle(RED_BORDER);
		} else {
			userId.setStyle(BLACK_BORDER);
		}

		if (password.getText().isEmpty()) {
			password.setStyle(RED_BORDER);
		} else {
			password.setStyle(BLACK_BORDER);
		}

		return !userId.getText().isEmpty() && !password.getText().isEmpty();
	}

	private boolean validateUser() {
		loggedUsr = loginModel.validateUser(userId.getText(), password.getText());
		return loggedUsr != null;
	}
}