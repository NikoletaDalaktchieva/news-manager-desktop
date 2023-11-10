/**
 * 
 */
package application;

import application.news.Article;
import application.news.User;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * @author ÁngelLucas
 *
 */
public class ArticleDetailsController {
	// TODO add attributes and methods as needed

	@FXML
	private Label category;

	@FXML
	private Label subtitle;

	@FXML
	private Label title;

	@FXML
	private Label userId;

	@FXML
	private MFXButton abstractBodyBtn;

	@FXML
	private ImageView image;

	@FXML
	private WebView webView;

	/**
	 * Registered user
	 */
	private User usr;

	/**
	 * Article to be shown
	 */
	private Article article;

	/**
	 * @param usr the usr to set
	 */
	void setUsr(User usr) {
		this.usr = usr;
		if (usr == null) {
			return; // Not logged user
		}
		// TODO Update UI information
		userId.setText(" for " + usr.getLogin());
	}

	/**
	 * @param article the article to set
	 */
	void setArticle(Article article) {
		this.article = article;
		// TODO complete this method
		title.setText(article.getTitle());
		subtitle.setText(article.getSubtitle());
		category.setText(article.getCategory());
		changeAbstractBody(article.getAbstractText());
		abstractBodyBtn.setText("Change to Body");
		image.setImage(article.getImageData());

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
			changeAbstractBody(article.getAbstractText());
			abstractBodyBtn.setText("Change to Body");
		} else {
			changeAbstractBody(article.getBodyText());
			abstractBodyBtn.setText("Change to Abstract");
		}
	}

	void changeAbstractBody(String text) {
		webView.getEngine().loadContent(text);
	}
}
