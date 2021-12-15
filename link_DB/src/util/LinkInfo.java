package util;

import javafx.scene.image.Image;

public class LinkInfo {

	private int id;
	private Image image;
	private String link;
	private String linkClass;
	private String detailClass;
	private String descript;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
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

}
