/**
 * 
 */
package application;

import java.io.FileWriter;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import java.io.IOException;

import javax.json.JsonObject;

import application.news.Article;
import application.news.Categories;
import application.news.User;
import application.utils.JsonArticle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import serverConection.ConnectionManager;
import serverConection.exceptions.ServerCommunicationError;

/**
 * @author ÁngelLucas
 *
 */
public class ArticleEditController {

	@FXML
	private TextArea abstractBody;

	@FXML
	private MFXButton abstractBodyBtn;

	@FXML
	private MFXComboBox<String> categories;

	@FXML
	private ImageView image;

	@FXML
	private MFXTextField subtitle;

	@FXML
	private MFXTextField title;

	@FXML
	private Label userId;

	/**
	 * Connection used to send article to server after editing process
	 */
	private ConnectionManager connection;

	/**
	 * Instance that represent an article when it is editing
	 */
	private ArticleEditModel editingArticle;
	/**
	 * User whose is editing the article
	 */
	private User usr;
	// TODO add attributes and methods as needed

	@FXML
	void onImageClicked(MouseEvent event) {
		if (event.getClickCount() >= 2) {
			Scene parentScene = ((Node) event.getSource()).getScene();
			FXMLLoader loader = null;
			try {
				loader = new FXMLLoader(getClass().getResource(AppScenes.IMAGE_PICKER.getFxmlFile()));
				Pane root = loader.load();
				Scene scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				Window parentStage = parentScene.getWindow();
				Stage stage = new Stage();
				stage.initOwner(parentStage);
				stage.setScene(scene);
				stage.initStyle(StageStyle.UNDECORATED);
				stage.initModality(Modality.WINDOW_MODAL);
				stage.showAndWait();
				ImagePickerController controller = loader.<ImagePickerController>getController();
				Image image = controller.getImage();
				if (image != null) {
					editingArticle.setImage(image);
					// TODO Update image on UI
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Send and article to server, Title and category must be defined and category
	 * must be different to ALL
	 * 
	 * @return true if only if article was been correctly send
	 */
	private boolean send() {
		String titleText = title.getText();
		Categories category = Categories.valueOf(categories.getSelectionModel().getSelectedItem().toUpperCase());
		if (titleText == null || category == null || titleText.equals("") || category == Categories.ALL) {
			Alert alert = new Alert(AlertType.ERROR, "Imposible send the article!! Title and categoy are mandatory",
					ButtonType.OK);
			alert.showAndWait();
			return false;
		}

		// TODO Niki fix if(!editingArticle.isModified()) return true;
		// TODO prepare and send using connection.saveArticle( ...)
		try {
			connection.saveArticle(editingArticle.getEditedOriginal());
		} catch (ServerCommunicationError e) {
			// TODO Niki conntection error
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * This method is used to set the connection manager which is needed to save a
	 * news
	 * 
	 * @param connection connection manager
	 */
	void setConnectionMannager(ConnectionManager connection) {
		this.connection = connection;
		// TODO enable send and back button

	}

	/**
	 * 
	 * @param usr the usr to set
	 */
	void setUsr(User usr) {
		this.usr = usr;
		// TODO Update UI and controls
		if (usr == null)
			return;
		userId.setText(" for " + usr.getLogin());

	}

	/**
	 * Get the article without changes since last commit
	 * 
	 * @return article without changes since last commit
	 */
	Article getArticle() {
		Article result = null;
		if (this.editingArticle != null) {
			result = this.editingArticle.getArticleOriginal();
		}
		return result;
	}

	/**
	 * PRE: User must be set
	 * 
	 * @param article the article to set
	 */
	void setArticle(Article article) {

		// TODO Niki not working
		// category.getEditor().setFont(Font.font("Book Antiqua", FontWeight.NORMAL,
		// 14));
		for (Categories c : Categories.values()) {
			if (c == Categories.ALL)
				continue;
			categories.getItems().add(c.toString());
		}

		this.editingArticle = (article != null) ? new ArticleEditModel(article) : new ArticleEditModel(usr);
		// TODO update UI
		if (article == null)
			return;
		title.setText(article.getTitle());
		subtitle.setText(article.getSubtitle());

		// TODO Niki Много лош подход, така ли го искаме?
		categories.getSelectionModel().selectItem(article.getCategory());
		abstractBody.setText(article.getAbstractText());
		abstractBodyBtn.setText("Change to Body");
		image.setImage(article.getImageData());
	}

	/**
	 * Save an article to a file in a json format Article must have a title
	 */
	private void write() {
		// TODO Consolidate all changes
		this.editingArticle.commit();
		// Removes special characters not allowed for filenames
		String name = this.getArticle().getTitle().replaceAll("\\||/|\\\\|:|\\?", "");
		String fileName = "saveNews//" + name + ".news";
		JsonObject data = JsonArticle.articleToJson(this.getArticle());
		try (FileWriter file = new FileWriter(fileName)) {
			file.write(data.toString());
			file.flush();
			
			//TODO NiKi Successful save message
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void back(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@FXML
	void changeAbstractBody(ActionEvent event) {
		// TODO Niki better codde
		if (abstractBodyBtn.getText() == "Change to Abstract") {
			abstractBody.setText(editingArticle.getAbstractText());
			abstractBodyBtn.setText("Change to Body");
			abstractBody.setPromptText("Body");
		} else {
			abstractBody.setText(editingArticle.getBodyText());
			abstractBodyBtn.setText("Change to Abstract");
			abstractBody.setPromptText("Abstract");
		}
	}

	@FXML
	void sendAndBack(ActionEvent event) {
		if (send()) {
			back(event);
		}
	}

	@FXML
	void saveToFile(ActionEvent event) {
		write();
	}

}
