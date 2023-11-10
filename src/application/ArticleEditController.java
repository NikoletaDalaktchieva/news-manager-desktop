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
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import java.io.IOException;

import javax.json.JsonObject;

import application.news.Article;
import application.news.Category;
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
import javafx.scene.web.WebView;
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

	@FXML
	private WebView webView;

	@FXML
	private MFXButton textTypeBtn;

	@FXML
	private MFXButton sendAndBack;

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

	private boolean bodySwitch = false;

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
					this.image.setImage(image);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * This method is used to set the connection manager which is needed to save a
	 * news
	 * 
	 * @param connection connection manager
	 */
	void setConnectionMannager(ConnectionManager connection) {
		this.connection = connection;
	}

	/**
	 * 
	 * @param usr the usr to set
	 */
	void setUsr(User usr) {
		this.usr = usr;
		if (usr == null) {
			sendAndBack.setVisible(false);
			return;
		}

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
		for (Category c : Category.values()) {
			if (c == Category.ALL)
				continue;
			categories.getItems().add(c.toString());
		}

		this.editingArticle = (article != null) ? new ArticleEditModel(article) : new ArticleEditModel(usr);
		if (article == null)
			return;
		title.setText(article.getTitle());
		subtitle.setText(article.getSubtitle());

		// TODO Niki Много лош подход, така ли го искаме?
		categories.getSelectionModel().selectItem(article.getCategory());
		abstractBody.setText(article.getAbstractText());
		webViewChangeText(abstractBody.getText());
		abstractBodyBtn.setText("Change to Body");
		image.setImage(article.getImageData());
	}

	@FXML
	void changeAbstractBody(ActionEvent event) {
		if (bodySwitch) {
			bodySwitch = false;
			editingArticle.setBodyText(abstractBody.getText());
			abstractBody.setText(editingArticle.getAbstractText());
			webViewChangeText(editingArticle.getAbstractText());
			abstractBodyBtn.setText("Change to Body");
			abstractBody.setPromptText("Abstract");
			return;
		}

		bodySwitch = true;
		editingArticle.setAbstractText(abstractBody.getText());
		abstractBody.setText(editingArticle.getBodyText());
		webViewChangeText(editingArticle.getBodyText());
		abstractBodyBtn.setText("Change to Abstract");
		abstractBody.setPromptText("Body");
	}

	@FXML
	void sendAndBack(ActionEvent event) {
		if (send()) {
			back(event);
		}
	}

	/**
	 * Send and article to server, Title and category must be defined and category
	 * must be different to ALL
	 * 
	 * @return true if only if article was been correctly send
	 */
	private boolean send() {
		if (!commitChanges()) {
			return false;
		}

		// TODO Niki fix if(!editingArticle.isModified()) return true;
		// TODO prepare and send using connection.saveArticle( ...)
		try {
			connection.saveArticle(getArticle());
		} catch (ServerCommunicationError e) {
			new Alert(AlertType.INFORMATION, "Conntection problem!").showAndWait();
			return false;
		}

		return true;
	}

	private boolean commitChanges() {
		String titleText = title.getText();
		Category category = Category.getByName(categories.getSelectionModel().getSelectedItem());
		if (titleText == null || category == null || titleText.equals("") || category == Category.ALL) {
			Alert alert = new Alert(AlertType.ERROR,
					"Article did not pass validation! Title and categoy are mandatory!", ButtonType.OK);
			alert.showAndWait();
			return false;
		}
		editingArticle.setTitle(titleText);
		editingArticle.setSubtitle(subtitle.getText());
		editingArticle.setCategory(category);
		if (bodySwitch) {
			editingArticle.setBodyText(abstractBody.getText());
		} else {
			editingArticle.setAbstractText(abstractBody.getText());
		}

		editingArticle.commit();
		return true;
	}

	@FXML
	void back(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@FXML
	void saveToFile(ActionEvent event) {
		write();
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
			new Alert(AlertType.INFORMATION, "File saved successfully!").show();
		} catch (IOException e) {
			new Alert(AlertType.ERROR, "File saving problem!").show();
		}
	}

	@FXML
	void changeTextType(ActionEvent event) {
		if (textTypeBtn.getText().equals("Text")) {
			textTypeBtn.setText("HTML");
			webView.setVisible(true);
			abstractBody.setVisible(false);
		} else {
			textTypeBtn.setText("Text");
			webView.setVisible(false);
			abstractBody.setVisible(true);
		}
	}

	void webViewChangeText(String text) {
		webView.getEngine().loadContent(text);
	}

}
