/**
 * 
 */
package application;

import java.io.IOException;

import application.news.Article;
import application.news.Categories;
import application.news.User;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import serverConection.ConnectionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

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
	private MFXButton loginBtn;

	@FXML
	private MFXButton newBtn;

	@FXML
	private MFXComboBox<String> categories;

	@FXML
	private MFXTextField filterText;

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
		for (Categories c : Categories.values()) {
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
	void newArticle(ActionEvent event) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ArticleEdit.fxml"));
		Parent root1 = (Parent) fxmlLoader.load();

		ArticleEditController controller = fxmlLoader.<ArticleEditController>getController();
		controller.setUsr(usr);
		controller.setArticle(null);

		Stage stage = new Stage();
		stage.setScene(new Scene(root1));
		stage.show();
	}

	void refreshScreen() {
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

	private Parent generateVRow(Article article) {
		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("ArticleCard.fxml"));
			Parent root1 = loader.load();
			ArticleCardControler controller = loader.<ArticleCardControler>getController();
			controller.setData(article, usr);
			return root1;

			// articlesVBox.getChildren().add(root1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	@FXML
	void closeApplication(ActionEvent event) {
		Platform.exit();
		System.exit(0);
	}

	@FXML
	void filterByCategory(ActionEvent event) {
		filteredData.setPredicate(a -> compateCategory(a));
	}

	Boolean compateCategory(Parent a) {
		String selectedCategory = categories.getSelectionModel().getSelectedItem();
		if (selectedCategory.equals("All"))
			return true;
		String elementCategory = ((Label) a.lookup("#category")).getText();
		return selectedCategory.equals(elementCategory);
	}

	@FXML
	void onTextChange(KeyEvent event) {
		filteredData.setPredicate(a -> compateText(a));
	}

	Boolean compateText(Parent a) {
		String elementTitle = ((Label) a.lookup("#title")).getText();
		return elementTitle.toLowerCase().contains(filterText.getText().toLowerCase());
	}

}
