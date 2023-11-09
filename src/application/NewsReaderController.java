/**
 * 
 */
package application;

import java.io.IOException;
import application.news.Article;
import application.news.Categories;
import application.news.User;
import application.utils.JsonArticle;
import application.utils.exceptions.ErrorMalFormedArticle;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;
import serverConection.ConnectionManager;
import javafx.collections.ObservableList;

/**
 * @author ÁngelLucas
 *
 */
public class NewsReaderController {

	@FXML
	private ListView<Article> articleList;

	@FXML
	private VBox articlesVBox;

	@FXML
	private AnchorPane loginWindow;

	private NewsReaderModel newsReaderModel = new NewsReaderModel();
	private User usr;

	

	// TODO add attributes and methods as needed

	public NewsReaderController() {
		// TODO
		// Uncomment next sentence to use data from server instead dummy data
		newsReaderModel.setDummyData(false);

		// Get text Label

	}

	private void getData() {
		// TODO retrieve data and update UI
		// The method newsReaderModel.retrieveData() can be used to retrieve data
		newsReaderModel.retrieveData();
		ObservableList<Article> articles = newsReaderModel.getArticles();
		this.articleList.setItems(articles);
	
	}

	/**
	 * @return the usr
	 */
	User getUsr() {
		return usr;
	}

	void setConnectionManager(ConnectionManager connection) {
		this.newsReaderModel.setDummyData(false); // System is connected so dummy data are not needed
		this.newsReaderModel.setConnectionManager(connection);
		this.getData();
	}

	/**
	 * @param usr the usr to set
	 */
	void setUsr(User usr) {

		this.usr = usr;
		// Reload articles
		this.getData();
		// TODO Update UI
	}

	void refreshScreen() {
		if (usr == null)
			return;

	}

	@FXML
	void openLoginPage(ActionEvent event) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
		Parent root1 = (Parent) fxmlLoader.load();

		LoginController controller = fxmlLoader.<LoginController>getController();
		controller.sendData(this.newsReaderModel.getConnectionManager(), this.usr);

		Stage stage = new Stage();
		stage.setOnHidden(ev -> {
			setUsr(controller.getLoggedUsr());
			refreshScreen();
		});
		stage.setScene(new Scene(root1));
		stage.show();
	}

	@FXML
	void openArticle(ActionEvent event) throws IOException {
		Article article = articleList.getSelectionModel().getSelectedItem();

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ArticleDetails.fxml"));
		Parent root1 = (Parent) fxmlLoader.load();

		ArticleDetailsController controller = fxmlLoader.<ArticleDetailsController>getController();
		controller.setUsr(usr);
		controller.setArticle(article);

		Stage stage = new Stage();
		stage.setScene(new Scene(root1));
		stage.show();

	}

	// TODO Niki pretty similar functions, should be optimized
	@FXML
	void editArticle(ActionEvent event) throws IOException {
		openEditScreen(articleList.getSelectionModel().getSelectedItem());
	}

	@FXML
	void newArticle(ActionEvent event) throws IOException {
		openEditScreen(null);
	}

	void openEditScreen(Article article) throws IOException {

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ArticleEdit.fxml"));
		Parent root1 = (Parent) fxmlLoader.load();

		ArticleEditController controller = fxmlLoader.<ArticleEditController>getController();
		controller.setUsr(usr);
		controller.setArticle(article);

		Stage stage = new Stage();
		stage.setScene(new Scene(root1));
		stage.show();
	}
}
