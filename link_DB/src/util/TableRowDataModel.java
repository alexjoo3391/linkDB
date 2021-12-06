package util;

import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;

public class TableRowDataModel {

	private int id;
	private Image linkImage;
	private Hyperlink link;
	private String linkClass;
	private String detailClass;
	private String descript;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public Image getLinkImage() {
		return linkImage;
	}

	public void setLinkImage(Image linkImage) {
		this.linkImage = linkImage;
	}

	public Hyperlink getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = new Hyperlink(link);
	}

	public String getLinkClass() {
		return linkClass;
	}

	public void setLinkClass(String linkClass) {
		this.linkClass = linkClass;
	}

	public String getDetailClass() {
		return detailClass;
	}

	public void setDetailClass(String detailClass) {
		this.detailClass = detailClass;
	}

	public String getDescript() {
		return descript;
	}
	
	public void setDescript(String descript) {
		this.descript = descript;
	}
	
	@Override
	public String toString() {
		return "TableRowDataModel [linkImage=" + linkImage + ", link=" + link + ", linkClass=" + linkClass + ", detailClass=" + detailClass + "]";
	}
}
