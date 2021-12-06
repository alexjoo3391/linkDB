package util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;

public class Library {
	private TreeMap<Integer, Image> currentImages = new TreeMap<Integer, Image>();
	private TreeMap<Integer, Image> currentSearchedImages = new TreeMap<Integer, Image>();
	private JDBCUtil db = new JDBCUtil();
	private Connection con = db.getConnection();
	private Preferences systemPref = Preferences.userNodeForPackage(getClass());
	
	public boolean setCurrentLink(int id) {
		systemPref.putInt("currentLink", id);
		return true;
	}
	
	public int getCurrentLink() {
		return systemPref.getInt("currentLink", -1);
	}
	
	public boolean URLisValid(String url) {
		try {
			new URL(url).toURI();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public void alert(String msg, String header) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("¾Ë¸²");
		alert.setHeaderText(header);
		alert.setContentText(msg);

		alert.show();
	}
	
	public boolean insertLink(int id, String link, String linkClass, String dlinkClass, String desc) {
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO `link`(`id`, `link`, `class`, `detailClass`, `descript`) VALUES ("+ id +", '" + link + "', '" + linkClass + "', '" + dlinkClass + "', '" + desc + "')";
		
		try {
			pstmt = this.con.prepareStatement(sql);
			pstmt.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
		
	public boolean updateLink(int id, String link, String linkClass, String dlinkClass, String desc) {
		PreparedStatement pstmt = null;
		String sql = "UPDATE `link` SET `link`='" + link + "', `class`='" + linkClass + "', `detailClass`='" + dlinkClass + "', `descript`='" + desc + "' WHERE `id`=" + id;
		try {
			pstmt = this.con.prepareStatement(sql);
			pstmt.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public int getLinkId() {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT MAX(id) as id FROM `link`";
		
		try {
			pstmt = this.con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				String id = rs.getString("id");
				if(id == null) {
					return -1;
				} else {
					return Integer.parseInt(id);
				}
			} else {
				return -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public ArrayList<ArrayList<String>> getDB() {
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM `link`";
		
		try {
			pstmt = this.con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				ArrayList<String> links = new ArrayList<String>();
				
				String id = rs.getString("id");
				String link = rs.getString("link");
				String linkClass = rs.getString("class");
				String detailClass = rs.getString("detailClass");
				String descript = rs.getString("descript");
				
				if(id == null || link == null || linkClass == null || detailClass == null || descript == null) {
					return null;
				} else {
					links.add(id);
					links.add(link);
					links.add(linkClass);
					links.add(detailClass);
					links.add(descript);
					list.add(links);
				}
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public ArrayList<ArrayList<String>> getSearched() {
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM `searchedLink`";
		
		try {
			pstmt = this.con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				ArrayList<String> links = new ArrayList<String>();
				
				String id = rs.getString("id");
				String link = rs.getString("link");
				String linkClass = rs.getString("class");
				String detailClass = rs.getString("detailClass");
				String descript = rs.getString("descript");
				
				if(id == null || link == null || linkClass == null || detailClass == null || descript == null) {
					return null;
				} else {
					links.add(id);
					links.add(link);
					links.add(linkClass);
					links.add(detailClass);
					links.add(descript);
					list.add(links);
				}
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public ArrayList<Integer> getSearchedListId() {
		ArrayList<Integer> data = new ArrayList<Integer>();
		ArrayList<ArrayList<String>> linkList = this.getSearched();

		for (ArrayList<String> link : linkList) {
			data.add(Integer.parseInt(link.get(0)));
		}
		
		return data;
	}
	
	public ArrayList<Image> getSearchedListImage() throws IOException {
		
		ArrayList<Image> data = new ArrayList<Image>();
		ArrayList<ArrayList<String>> linkList = this.getSearched();
		ArrayList<Integer> idIdx = this.getSearchedListId();
		int idx = this.currentSearchedImages.size();
		
		while(this.currentSearchedImages.size() < idIdx.size()) {
			int temp = idIdx.get(idx);
			this.currentSearchedImages.put(temp, null);
			idx++;
		}
		
		
		idx = 0;
		
		for (ArrayList<String> link : linkList) {
			org.jsoup.Connection conn = Jsoup.connect(link.get(1)).userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
			boolean hasImage = false;

			if(this.currentSearchedImages.get(idIdx.get(idx)) == null) {
				try { 
					Response resp = conn.execute();
					if(resp.statusCode() == 200) {
						Document doc = conn.get();
						Elements metas = doc.getElementsByTag("meta");

						for (Element meta : metas) {
							if (meta.attr("property").equals("og:image")) {
								String src = meta.attr("content");
								URL url = new URL(src);
								URLConnection urlc = url.openConnection();
								urlc.setRequestProperty("User-Agent", "Mozilla 5.0 (Windows; U; " + "Windows NT 5.1; en-US; rv:1.8.0.11) ");
								BufferedImage bi = ImageIO.read(urlc.getInputStream());
								if(bi != null) {
									Image image = SwingFXUtils.toFXImage(bi, null);
									this.currentSearchedImages.replace(idIdx.get(idx), image);
									hasImage = true;
								}
								break;
							}
						}
					}
				} catch (IOException e) {
				}

				if (!hasImage) {
					URL url = new URL("https://user-images.githubusercontent.com/77566626/135647420-38390382-d030-4d3f-8df9-65e52e11850e.png");
					URLConnection urlc = url.openConnection();
					urlc.setRequestProperty("User-Agent", "Mozilla 5.0 (Windows; U; " + "Windows NT 5.1; en-US; rv:1.8.0.11) ");
					BufferedImage bi = ImageIO.read(urlc.getInputStream());
					Image image = SwingFXUtils.toFXImage(bi, null);
					this.currentImages.replace(idIdx.get(idx), image);
				}
			}
			idx++;
		}
		
		Object[] mapkey = this.currentSearchedImages.keySet().toArray();
		Arrays.sort(mapkey);

		this.currentSearchedImages.forEach((key, value) -> {
			data.add(value);
		});
		return data;
	}
	
	public ArrayList<String> getSearchedListLink() {
		ArrayList<String> data = new ArrayList<String>();
		ArrayList<ArrayList<String>> linkList = this.getSearched();

		for (ArrayList<String> link : linkList) {
			data.add(link.get(1));
		}
		
		return data;
	}
	
	public ArrayList<String> getSearchedListClass() {
		ArrayList<String> data = new ArrayList<String>();
		ArrayList<ArrayList<String>> linkList = this.getSearched();

		for (ArrayList<String> link : linkList) {
			data.add(link.get(2));
		}
		
		return data;
	}
	
	public ArrayList<String> getSearchedListDetailClass() {
		ArrayList<String> data = new ArrayList<String>();
		ArrayList<ArrayList<String>> linkList = this.getSearched();

		for (ArrayList<String> link : linkList) {
			data.add(link.get(3));
		}
		
		return data;
	}
	
	public ArrayList<String> getSearchedListDescript() {
		ArrayList<String> data = new ArrayList<String>();
		ArrayList<ArrayList<String>> linkList = this.getSearched();

		for (ArrayList<String> link : linkList) {
			data.add(link.get(4));
		}
		
		return data;
	}
	
	public ArrayList<Integer> getListId() {
		ArrayList<Integer> data = new ArrayList<Integer>();
		ArrayList<ArrayList<String>> linkList = this.getDB();

		for (ArrayList<String> link : linkList) {
			data.add(Integer.parseInt(link.get(0)));
		}
		
		return data;
	}
	
	public ArrayList<Image> getListImage() throws IOException {
		
		ArrayList<Image> data = new ArrayList<Image>();
		ArrayList<ArrayList<String>> linkList = this.getDB();
		ArrayList<Integer> idIdx = this.getListId();
		int idx = this.currentImages.size();
		
		while(this.currentImages.size() < idIdx.size()) {
			int temp = idIdx.get(idx);
			this.currentImages.put(temp, null);
			idx++;
		}
		
		
		idx = 0;
		
		for (ArrayList<String> link : linkList) {
			org.jsoup.Connection conn = Jsoup.connect(link.get(1)).userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
			boolean hasImage = false;
			
			if(this.currentImages.get(idIdx.get(idx)) == null) {
				try { 
					Response resp = conn.execute();
					if(resp.statusCode() == 200) {
						Document doc = conn.get();
						Elements metas = doc.getElementsByTag("meta");

						for (Element meta : metas) {
							if (meta.attr("property").equals("og:image")) {
								String src = meta.attr("content");
								URL url = new URL(src);
								URLConnection urlc = url.openConnection();
								urlc.setRequestProperty("User-Agent", "Mozilla 5.0 (Windows; U; " + "Windows NT 5.1; en-US; rv:1.8.0.11) ");
								BufferedImage bi = ImageIO.read(urlc.getInputStream());
								if(bi != null) {
									Image image = SwingFXUtils.toFXImage(bi, null);
									this.currentImages.replace(idIdx.get(idx), image);
									hasImage = true;
								}
								break;
							}
						}
					}
				} catch (IOException e) {
				}

				if (!hasImage) {
					URL url = new URL("https://user-images.githubusercontent.com/77566626/135647420-38390382-d030-4d3f-8df9-65e52e11850e.png");
					URLConnection urlc = url.openConnection();
					urlc.setRequestProperty("User-Agent", "Mozilla 5.0 (Windows; U; " + "Windows NT 5.1; en-US; rv:1.8.0.11) ");
					BufferedImage bi = ImageIO.read(urlc.getInputStream());
					Image image = SwingFXUtils.toFXImage(bi, null);
					this.currentImages.replace(idIdx.get(idx), image);
				}
			}
			idx++;
		}
		
		Object[] mapkey = this.currentImages.keySet().toArray();
		Arrays.sort(mapkey);

		this.currentImages.forEach((key, value) -> {
			data.add(value);
		});
		return data;
	}
	
	public ArrayList<String> getListLink() {
		ArrayList<String> data = new ArrayList<String>();
		ArrayList<ArrayList<String>> linkList = this.getDB();

		for (ArrayList<String> link : linkList) {
			data.add(link.get(1));
		}
		
		return data;
	}
	
	public ArrayList<String> getListClass() {
		ArrayList<String> data = new ArrayList<String>();
		ArrayList<ArrayList<String>> linkList = this.getDB();

		for (ArrayList<String> link : linkList) {
			data.add(link.get(2));
		}
		
		return data;
	}
	
	public ArrayList<String> getListDetailClass() {
		ArrayList<String> data = new ArrayList<String>();
		ArrayList<ArrayList<String>> linkList = this.getDB();

		for (ArrayList<String> link : linkList) {
			data.add(link.get(3));
		}
		
		return data;
	}
	
	public ArrayList<String> getListDescript() {
		ArrayList<String> data = new ArrayList<String>();
		ArrayList<ArrayList<String>> linkList = this.getDB();

		for (ArrayList<String> link : linkList) {
			data.add(link.get(4));
		}
		
		return data;
	}
	
	public boolean deleteLink(int id){
		
		PreparedStatement pstmt = null;
		String sql = "DELETE FROM `link` WHERE `id`='" + id + "'";
		try {
			pstmt = this.con.prepareStatement(sql);
			pstmt.execute();
			this.currentImages.remove(id);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@SuppressWarnings("resource")
	public boolean finder() {
		int act = systemPref.getInt("signal", 0);
		if(act == 1) {
			systemPref.putInt("signal", 0);
			return true;
		} else if(act == 2) {
			currentImages.replace(this.getCurrentLink(), null);
			systemPref.putInt("signal", 0);
			return true;
		}
		return false;
	}

	public boolean sendSignal(int option) {
		systemPref.putInt("signal", option);
		return true;
	}
	
	public TableRowDataModel getCurrentLinkById(int id) {
		TableRowDataModel trdm = new TableRowDataModel();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM `link` WHERE `id`=" + id;
		try {
			pstmt = this.con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				trdm.setId(id);
				trdm.setLink(rs.getString("link"));
				trdm.setLinkClass(rs.getString("class"));
				trdm.setDetailClass(rs.getString("detailClass"));
				trdm.setDescript(rs.getString("descript"));
				trdm.setLinkImage(null);
				
				return trdm;
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	public void searchLink(String searchString, String searchClass, String searchDClass) {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM `link`";
		
		try {
			pstmt = this.con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				
				int id = rs.getInt("id");
				String link = rs.getString("link");
				String linkClass = rs.getString("class");
				String detailClass = rs.getString("detailClass");
				String descript = rs.getString("descript");
				
				
				if(searchString.equals("")) {
					if(searchClass != null) {if(searchDClass != null) {
							if(linkClass.contains(searchClass) && detailClass.contains(searchDClass)) {
								this.insertSearchedLink(id, link, linkClass, detailClass, descript);
							}
						} else {
							if(linkClass.contains(searchClass)) {
								this.insertSearchedLink(id, link, linkClass, detailClass, descript);
							}
						}
					} else {
						if(searchDClass != null) {
							if(detailClass.contains(searchDClass)) {
								this.insertSearchedLink(id, link, linkClass, detailClass, descript);
							}
						}
					}
				} else {
					if(searchClass != null) {
						if(searchDClass != null) {
							if(link.contains(searchString) && linkClass.contains(searchClass) && detailClass.contains(searchDClass)) {
								this.insertSearchedLink(id, link, linkClass, detailClass, descript);
							}
						} else {
							if(link.contains(searchString) && linkClass.contains(searchClass)) {
								this.insertSearchedLink(id, link, linkClass, detailClass, descript);
							}
						}
					} else {
						if(searchDClass != null) {
							if(link.contains(searchString) && detailClass.contains(searchDClass)) {
								this.insertSearchedLink(id, link, linkClass, detailClass, descript);
							}
						} else {
							if(link.contains(searchString)) {
								this.insertSearchedLink(id, link, linkClass, detailClass, descript);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean insertSearchedLink(int id, String link, String linkClass, String dlinkClass, String desc) {
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO `searchedLink`(`id`, `link`, `class`, `detailClass`, `descript`) VALUES ("+ id +", '" + link + "', '" + linkClass + "', '" + dlinkClass + "', '" + desc + "')";
		
		try {
			pstmt = this.con.prepareStatement(sql);
			pstmt.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean resetSearchedLink() {
		PreparedStatement pstmt = null;
		String sql = "DELETE FROM `searchedLink`";
		
		try {
			pstmt = this.con.prepareStatement(sql);
			pstmt.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
} 

