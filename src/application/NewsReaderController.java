/**
 * 
 */
package application;

import java.io.File;
import java.io.IOException;

import application.news.Article;
import application.news.Category;
import application.news.User;
import application.utils.JsonArticle;
import application.utils.exceptions.ErrorMalFormedArticle;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import serverConection.ConnectionManager;

/**
 * @author ÁngelLucas
 *
 */
public class NewsReaderController {

	@FXML
	private ListView<Parent> articlesListView;

	private FilteredList<Parent> filteredData;

	@FXML
	private AnchorPane loginWindow;

	@FXML
	private ImageView loginImage;

	@FXML
	private ImageView newBtn;
	@FXML
	private ImageView uploadBtn;

	@FXML
	private MFXComboBox<String> categories;

	@FXML
	private MFXTextField filterText;

	private NewsReaderModel newsReaderModel = new NewsReaderModel();
	private User usr;

	private String textSearch;
	private String selectedCategory;

	public NewsReaderController() {
		// Uncomment next sentence to use data from server instead dummy data
		newsReaderModel.setDummyData(false);
		// Get text Label

	}

	private void getData() {
		for (Category c : Category.values()) {
			categories.getItems().add(c.toString());
		}
		categories.getSelectionModel().selectFirst();

		refreshScreen();

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

	@FXML
	void onLoginClick(MouseEvent event) throws IOException {
		if (usr == null) {
			openLoginPage();
		} else {
			usr = null;
			refreshScreen();
		}
	}

	void openLoginPage() throws IOException {
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
		stage.showAndWait();
	}

	@FXML
	void newArticle(MouseEvent event) throws IOException {
		openEditScreen(null);
	}

	void openEditScreen(Article article) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ArticleEdit.fxml"));
		Parent root1 = (Parent) fxmlLoader.load();

		ArticleEditController controller = fxmlLoader.<ArticleEditController>getController();
		controller.setUsr(usr);
		controller.setArticle(article);
		controller.setConnectionMannager(newsReaderModel.getConnectionManager());

		Stage stage = new Stage();
		stage.setScene(new Scene(root1));
		stage.setOnHidden(e -> refreshScreen());
		stage.showAndWait();
	}

	void refreshScreen() {

		refreshLoginBtn();
		newBtn.setVisible(usr != null);
		newsReaderModel.retrieveData();
		ObservableList<Article> articles = newsReaderModel.getArticles();

		ObservableList<Parent> articleCards = FXCollections.observableArrayList();

		for (Article article : articles) {
			articleCards.add(generateVRow(article));
		}
		filteredData = new FilteredList<>(articleCards, article -> true);
		articlesListView.setItems(filteredData);

	}

	private void refreshLoginBtn() {
		if (usr == null) {
			loginImage.setImage(new Image(getClass().getResourceAsStream("/images/login.png")));
		} else {
			loginImage.setImage(new Image(getClass().getResourceAsStream("/images/logout.png")));
		}

	}

	private Parent generateVRow(Article article) {
		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("ArticleCard.fxml"));
			Parent root1 = loader.load();
			ArticleCardControler controller = loader.<ArticleCardControler>getController();
			controller.setData(article, usr, newsReaderModel.getConnectionManager(), this);
			return root1;

			// articlesVBox.getChildren().add(root1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	@FXML
	void closeApplication(MouseEvent event) {
		Platform.exit();
		System.exit(0);
	}

	@FXML
	void filterByCategory(ActionEvent event) {
		filteredData.setPredicate(a -> {
			selectedCategory = categories.getSelectionModel().getSelectedItem();
			return filterPredicate(a);
		});
	}

	@FXML
	void onTextChange(KeyEvent event) {
		filteredData.setPredicate(a -> {
			textSearch = filterText.getText();
			return filterPredicate(a);
		});
	}

	private boolean filterPredicate(Parent a) {
		var title = ((Label) a.lookup("#title")).getText();
		var category = ((Label) a.lookup("#category")).getText();

		return equalToSelectedCategoryPredicate(category) && containsTextSearchPredicate(title);

	}

	private boolean equalToSelectedCategoryPredicate(String elementCategory) {
		return selectedCategory.equals("All") ? true : elementCategory.equals(selectedCategory);
	}

	private boolean containsTextSearchPredicate(String text) {
		return notEmpty(textSearch) ? text.toLowerCase().contains(textSearch.toLowerCase()) : true;
	}

	private boolean notEmpty(String string) {
		return string != null && !string.isEmpty() && !string.trim().equals("");
	}

	@FXML
	void uploadArticle(MouseEvent event) throws IOException {
		try {
			File selectedFile = new FileChooser()
					.showOpenDialog((Stage) ((Node) event.getSource()).getScene().getWindow());
			Article article = JsonArticle.jsonToArticle(JsonArticle.readFile(selectedFile.getCanonicalPath()));
			openEditScreen(article);
		} catch (ErrorMalFormedArticle e) {
			new Alert(AlertType.ERROR, "Fail problem!").showAndWait();
			e.printStackTrace();
		}

	}
}
