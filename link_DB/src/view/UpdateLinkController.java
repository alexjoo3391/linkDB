package view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.Library;
import util.TableRowDataModel;

public class UpdateLinkController implements Initializable{
	@FXML
	private Button closeBtn;
	@FXML
	private TextField UpdateLinkLink;
	@FXML
	private MenuButton UpdateLinkClass;
	@FXML
	private TextField UpdateLinkDetailClass;
	@FXML
	private TextField UpdateLinkDesc;
	@FXML
	private MenuItem classAction1;
	@FXML
	private MenuItem classAction2;
	@FXML
	private MenuItem classAction3;

	private Stage pop;
	
	private String Class = null;
	private int id = 0;
	
	public Library lib = new Library();
	
	public void UpdateLink() throws IOException {
		String link = UpdateLinkLink.getText();
		String dClass = UpdateLinkDetailClass.getText();
		String desc = UpdateLinkDesc.getText();
		
		if (link != "" && Class != null && dClass != "" && desc != "") {
			id = lib.getCurrentLink();
			boolean isCompleted = lib.updateLink(id, link, Class, dClass, desc);
			if(isCompleted) {
				lib.sendSignal(2);
				lib.alert("수정이 성공적으로 완료 되었습니다.", "");
				this.Close();
			} else {
				lib.alert("링크 수정에 실패하였습니다", "error");
			}
		} else {
			lib.alert("모든 항목을 입력하지 않았습니다.", "error");
		}
	}

	public void SelectedClass(ActionEvent e) {
		Class = ((MenuItem) e.getTarget()).getText();
		UpdateLinkClass.setText(Class);
	}

	public void Close() {
		pop = (Stage) closeBtn.getScene().getWindow();
		pop.close();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		new TextField();
		
		id = lib.getCurrentLink();
		TableRowDataModel trdm = lib.getCurrentLinkById(id);
		UpdateLinkLink.setText(trdm.getLink().getText());
		Class = trdm.getLinkClass();
		UpdateLinkDetailClass.setText(trdm.getDetailClass());
		UpdateLinkDesc.setText(trdm.getDescript());
	}
}
