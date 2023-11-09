package application;

import java.io.IOException;

import application.news.Article;
import application.news.User;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ArticleCardControler {

	@FXML
	private MFXButton deleteBtn;
	
	@FXML
	private MFXButton editBtn;

	@FXML
	private MFXButton openBtn;
	
	@FXML
	private ImageView image;
	
	@FXML
	private Label subtitle;

	@FXML
	private Label category;
	
	@FXML
	private Label title;

	private Article article;

	private User usr;

	public void setData(Article article, User usr) {
		this.usr = usr;
		this.article = article;
		title.setText(article.getTitle());
		category.setText(article.getCategory());
		subtitle.setText(article.getSubtitle());
		image.setImage(article.getImageData());
		
		editBtn.setVisible(usr != null);
		deleteBtn.setVisible(usr != null);
	}

	@FXML
	void openArticle(ActionEvent event) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ArticleDetails.fxml"));
		Parent root1 = (Parent) fxmlLoader.load();

		ArticleDetailsController controller = fxmlLoader.<ArticleDetailsController>getController();
		controller.setUsr(usr);
		controller.setArticle(article);

		Stage stage = new Stage();
		stage.setScene(new Scene(root1));
		stage.show();

	}

	@FXML
	void editArticle(ActionEvent event) throws IOException {
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
