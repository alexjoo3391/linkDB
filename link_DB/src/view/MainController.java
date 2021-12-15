package view;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;

import application.Main;
import util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainController implements Initializable {
	@FXML
	private Button AddLinkPop;
	@FXML
	private TableView<TableRowDataModel> LinkTable;
	@FXML
	private TableColumn<TableRowDataModel, Image> LinkTableThumbnail;
	@FXML
	private TableColumn<TableRowDataModel, Hyperlink> LinkTableLink;
	@FXML
	private TableColumn<TableRowDataModel, String> LinkTableClass;
	@FXML
	private TableColumn<TableRowDataModel, String> LinkTableDetailClass;
	@FXML
	private ImageView informImageVIew;
	@FXML
	private Hyperlink informHyperlink;
	@FXML
	private Label informClasslbl;
	@FXML
	private Label informDetailClasslbl;
	@FXML
	private Label informDescriptlbl;
	@FXML
	private Pane pane;
	@FXML
	private TextField LinkSearch;
	@FXML
	private MenuButton LinkSearchClassMenu;
	@FXML
	private MenuButton LinkSearchdClassMenu;
	@FXML
	private AnchorPane mainPane;

	private Stage pop;
	private String Class = null;
	private String dClass = null;
	private int currentLink = -1;
	private ArrayList<LinkInfo> currentLinkList = new ArrayList<LinkInfo>();
	private Thread signalThread;

	public Library lib = new Library();

	public void SelectedClass(ActionEvent e) {
		Class = ((MenuItem) e.getTarget()).getText();
	}

	public void SelectedDClass(ActionEvent e) {
		dClass = ((MenuItem) e.getTarget()).getText();
	}

	public void AddLinkPopUp() {

		Stage mainStage = (Stage) AddLinkPop.getScene().getWindow();

		pop = new Stage(StageStyle.DECORATED);
		pop.initModality(Modality.WINDOW_MODAL);
		pop.initOwner(mainStage);

		try {

			Parent root = FXMLLoader.load(getClass().getResource("/view/AddLinkLayout.fxml"));

			Scene scene = new Scene(root);

			scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
			pop.setScene(scene);
			pop.setTitle("링크 추가");
			pop.setResizable(false);

			pop.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void SignalFinder() {
		signalThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (lib.finder()) {
						try {
							currentLinkList = lib.getListLink();
							BuildData();
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		signalThread.start();
	}

	public void BuildData() throws IOException {
		ObservableList<TableRowDataModel> linkList = FXCollections.observableArrayList();
		LinkSearchdClassMenu.getItems().clear();

		for (int i = 0; i < currentLinkList.size(); i++) {
			TableRowDataModel trdm = new TableRowDataModel();
			trdm.setId(currentLinkList.get(i).getId());
			trdm.setLinkImage(currentLinkList.get(i).getImage());
			trdm.setLink(currentLinkList.get(i).getLink());
			trdm.setLinkClass(currentLinkList.get(i).getLinkClass());
			trdm.setDetailClass(currentLinkList.get(i).getDetailClass());
			trdm.setDescript(currentLinkList.get(i).getDescript());
			linkList.add(trdm);

			MenuItem mi = new MenuItem(currentLinkList.get(i).getDetailClass());
			LinkSearchdClassMenu.getItems().add(mi);
		}

		LinkTable.setItems(linkList);
	}

	public void linkInfo(Event e) {
		if (e.getTarget().toString().contains("Hyperlink")) {
			String str = e.getTarget().toString();
			openLink(str.substring(52, str.length() - 1));
		} else {
			if (LinkTable.getSelectionModel().selectedIndexProperty().getValue() != -1) {
				TableRowDataModel trdm = LinkTable.getItems()
						.get(LinkTable.getSelectionModel().selectedIndexProperty().getValue());

				currentLink = trdm.getId();
				lib.setCurrentLink(currentLink);
				informImageVIew.setImage(trdm.getLinkImage());
				informHyperlink.setText(trdm.getLink().getText());
				informClasslbl.setText(trdm.getLinkClass());
				informDetailClasslbl.setText(trdm.getDetailClass());
				informDescriptlbl.setText(trdm.getDescript());
			}
		}
	}

	public void openLink(String url) {
		Runtime rt = Runtime.getRuntime();
		try {
			rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteLink() throws SQLException, IOException {
		if (currentLink == -1) {
			lib.alert("선택된 링크가 없습니다.", "error");
		} else {
			lib.deleteLink(currentLink);
			for (int i = 0; i < currentLinkList.size(); i++) {
				if (currentLinkList.get(i).getId() == currentLink)
					currentLinkList.remove(i);
			}
			BuildData();
			File file = new File("src\\resources\\thumbnail.png");
			BufferedImage bi = ImageIO.read(file);
			Image image = SwingFXUtils.toFXImage(bi, null);
			informImageVIew.setImage(image);
			informHyperlink.setText("링크");
			informClasslbl.setText("분류");
			informDetailClasslbl.setText("세부분류");
			informDescriptlbl.setText("설명");
			currentLink = -1;

			lib.alert("링크가 성공적으로 삭제되었습니다.", "");
		}

	}

	public void updateLink() {
		if (currentLink == -1) {
			lib.alert("선택된 링크가 없습니다.", "error");
		} else {
			Stage mainStage = (Stage) AddLinkPop.getScene().getWindow();

			pop = new Stage(StageStyle.DECORATED);
			pop.initModality(Modality.WINDOW_MODAL);
			pop.initOwner(mainStage);

			try {

				Parent root = FXMLLoader.load(getClass().getResource("/view/UpdateLinkLayout.fxml"));

				Scene scene = new Scene(root);

				scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
				pop.setScene(scene);
				pop.setTitle("링크 수정");
				pop.setResizable(false);

				pop.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void searchLink() throws IOException {
		LinkTable.getItems().clear();
		String keyword = LinkSearch.getText();
		System.out.println("keyword : " + keyword + ", class : " + Class + ", detail : " + dClass);
		
		ObservableList<TableRowDataModel> linkList = FXCollections.observableArrayList();
		ArrayList<LinkInfo> searched = new ArrayList<LinkInfo>();

		for (int i = 0; i < currentLinkList.size(); i++) {
			if (currentLinkList.get(i).getDescript().contains(keyword)
					|| (Class != null ? currentLinkList.get(i).getLinkClass().contains(Class) : false)   
					|| (dClass != null ? currentLinkList.get(i).getDetailClass().contains(dClass) : false)) {
				searched.add(currentLinkList.get(i));
			}
		}

		for (int i = 0; i < searched.size(); i++) {
			TableRowDataModel trdm = new TableRowDataModel();
			trdm.setId(searched.get(i).getId());
			trdm.setLinkImage(searched.get(i).getImage());
			trdm.setLink(searched.get(i).getLink());
			trdm.setLinkClass(searched.get(i).getLinkClass());
			trdm.setDetailClass(searched.get(i).getDetailClass());
			trdm.setDescript(searched.get(i).getDescript());
			linkList.add(trdm);
		}

		LinkTable.setItems(linkList);
	}

	public void copyLink() {
		StringSelection stringSelection = new StringSelection(informHyperlink.getText());
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
	}

	@FXML
	public void exit() {
		Stage stage = (Stage) mainPane.getScene().getWindow();
		signalThread.interrupt();
		stage.close();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		this.SignalFinder();

		LinkTableThumbnail.setCellFactory(param -> {
			final ImageView imageview = new ImageView();
			imageview.setFitWidth(150);
			imageview.setFitHeight(100);
			imageview.setPreserveRatio(true);

			TableCell<TableRowDataModel, Image> cell = new TableCell<TableRowDataModel, Image>() {
				public void updateItem(Image image, boolean empty) {
					if (image != null) {
						imageview.setImage(image);
					}
				}
			};
			cell.setGraphic(imageview);
			return cell;
		});

		LinkTableThumbnail.setCellValueFactory(new PropertyValueFactory<TableRowDataModel, Image>("linkImage"));
		LinkTableLink.setCellFactory(param -> {
			final Hyperlink link = new Hyperlink();

			TableCell<TableRowDataModel, Hyperlink> cell = new TableCell<TableRowDataModel, Hyperlink>() {
				public void updateItem(Hyperlink item, boolean empty) {
					link.setGraphic(item);
					link.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							String str = event.getTarget().toString();
							openLink(str.substring(41, str.length() - 1));

						}
					});

				}
			};

			cell.setGraphic(link);
			return cell;
		});
		LinkTableLink.setCellValueFactory(new PropertyValueFactory<TableRowDataModel, Hyperlink>("link"));
		LinkTableClass.setCellValueFactory(new PropertyValueFactory<TableRowDataModel, String>("linkClass"));
		LinkTableDetailClass.setCellValueFactory(new PropertyValueFactory<TableRowDataModel, String>("detailClass"));

		ArrayList<LinkInfo> link = lib.getListLink();
		currentLinkList = link;

		try {
			BuildData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
