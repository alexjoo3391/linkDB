package view;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.Library;

public class AddLinkController {
	@FXML
	private Button closeBtn;
	@FXML
	private TextField AddLinkLink;
	@FXML
	private MenuButton AddLinkClass;
	@FXML
	private TextField AddLinkDetailClass;
	@FXML
	private TextField AddLinkDesc;
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
	
	public void AddLink() throws IOException {
		String link = AddLinkLink.getText();
		String dClass = AddLinkDetailClass.getText();
		String desc = AddLinkDesc.getText();
		
		if (lib.URLisValid(link) && Class != null && dClass != "" && desc != "") {
			id = lib.getLinkId();
			id++;
			boolean isCompleted = lib.insertLink(this.id, link, Class, dClass, desc);
			if(isCompleted) {
				lib.sendSignal(1);
				lib.alert("추가가 성공적으로 완료 되었습니다.", "");
				this.Close();
			} else {
				lib.alert("링크 추가에 실패하였습니다", "error");
			}
		} else {
			lib.alert("모든 항목을 입력하지 않았습니다.", "error");
		}
	}

	public void SelectedClass(ActionEvent e) {
		Class = ((MenuItem) e.getTarget()).getText();
	}

	public void Close() {
		pop = (Stage) closeBtn.getScene().getWindow();
		pop.close();
	}
}
