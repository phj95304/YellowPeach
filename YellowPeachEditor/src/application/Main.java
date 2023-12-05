package application;
	

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.prefs.Preferences;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import application.model.Chart;
import application.model.DraggableNode;
import application.model.SystemInfo;
import application.util.Connect;
import application.view.BoardLayoutController;
import application.view.RootLayoutController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

public class Main extends Application {
	private Stage primaryStage;
	private BorderPane rootLayout;
	private AnchorPane boardLayout;
	private Scene scene;
	
	private RootLayoutController rootLayoutController;
	private BoardLayoutController boardLayoutController;
	
	// UI ����Ʈ�� ��� ��ú��� ����Ʈ	(tab���̵� key�� ����ϰ� value�� ��Ʈ����Ʈ���� ������ ����)
	private HashMap<String, List<Chart>> dashBoardHashMap;
	
	// DashBoard �̸�
	private String dashBoardName = "";
	
	// tab������ Topic List
	public static HashMap<String, Vector<String>> tabTopicList;
	
	public static Connect connect;
	public static boolean isDbConnect;
	
	public static String beforeTab = "0";
	
	public static int col = 0;
	public static int row = 0;
	
	// tabid, SystemInfo
	public static HashMap<String, SystemInfo> tabSystemInfo;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Editor");
		this.primaryStage.getIcons().add(new Image("file:resources/images/mars.png"));
		
		// ��ú��� hashMap ����.
		dashBoardHashMap = new HashMap<String, List<Chart>>();
		tabSystemInfo = new HashMap<String, SystemInfo>();
		tabTopicList = new HashMap<String, Vector<String>>();
		
		Group root = new Group();
		
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		primaryStage.setWidth(primaryScreenBounds.getWidth());
		primaryStage.setHeight(primaryScreenBounds.getHeight());
		primaryStage.setX(primaryScreenBounds.getMinX());
		primaryStage.setY(primaryScreenBounds.getMinY());
		
		initRootLayout();
		showBoardLayout();
		
		primaryStage.show();
		
		boardLayoutController.setSize(primaryStage.getWidth(), primaryStage.getHeight());
		
		double dashBoardPaneWidth = primaryScreenBounds.getWidth()-boardLayoutController.getMenuRootPane().getPrefWidth();
		double dashBoardPaneHeight = primaryScreenBounds.getHeight()-117;
		
		rootLayoutController.setDashBoardPaneWidth(dashBoardPaneWidth);
		rootLayoutController.setDashBoardPaneHeight(dashBoardPaneHeight);
		
		rootLayoutController.setBoardController(boardLayoutController);
		//rootLayoutController.handleNew();
		
		scene.getAccelerators().put(new KeyCodeCombination(
			KeyCode.S, KeyCombination.CONTROL_ANY), new Runnable() {
				@Override public void run() {
					rootLayoutController.handleSave();
				}
			});
		
		connect = new Connect();
		isDbConnect = checkDBConnect();
	}
	
	public void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("fxml/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			
			// ��Ʈ�ѷ����� Main ���� ������ �ش�.
			rootLayoutController = loader.getController();
			rootLayoutController.setMain(this);
			
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showBoardLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("fxml/BoardLayout.fxml"));
			boardLayout = (AnchorPane) loader.load();
			
			rootLayout.setCenter(boardLayout);
			
			// ��Ʈ�ѷ����� Main ���� ������ �ش�.
			boardLayoutController = loader.getController();
			boardLayoutController.setMain(this);
						
			// BoardLayout�� ũ�� ����
			boardLayoutController.getBorderPane().setPrefWidth(primaryStage.getWidth()-18);
			boardLayoutController.getBorderPane().setPrefHeight(primaryStage.getHeight()-79);
			
			// menuPane�� ũ�� ����
			boardLayoutController.getMenuRootPane().setMinWidth(primaryStage.getWidth()*0.08);
		
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * UI ���� ȯ�漳���� ��ȯ�Ѵ�.
	 * �� ������ ���������� ���� ���̰�, ȯ�漳���� OS Ư�� ������Ʈ���κ��� �д´�.
	 * ���� preference�� ã�� ���ϸ� null�� ��ȯ�Ѵ�.
	 *
	 * @return
	 */
	public File getUiFilePath() {
	    Preferences prefs = Preferences.userNodeForPackage(Main.class);
	    String filePath = prefs.get("filePath", null);
	    String currentTab = boardLayoutController.getTabPane().getSelectionModel().getSelectedItem().getId();
	    //System.out.println(filePath);
	    System.out.println("c : "+currentTab);
	    System.out.println("b : "+beforeTab);
	    if (filePath != null) {
	    	if(!currentTab.equals(beforeTab))
	    		return null;
	        return new File(filePath);
	    } else {
	        return null;
	    }
	}
	/**
	 * ���� �ҷ��� ������ ��θ� �����Ѵ�. �� ��δ� OS Ư�� ������Ʈ���� ����ȴ�.
	 *
	 * @param file the file or null to remove the path
	 */
	public void setUiFilePath(File file) {
	    Preferences prefs = Preferences.userNodeForPackage(Main.class);
	    beforeTab = boardLayoutController.getTabPane().getSelectionModel().getSelectedItem().getId();
	    if (file != null) {
	        prefs.put("filePath", file.getPath());

	        // Tab Ÿ��Ʋ�� ������Ʈ�Ѵ�.
	        Tab tab = boardLayoutController.getTabPane().getSelectionModel().getSelectedItem();
	        int idx = file.getName().indexOf(".");
	        tab.setText(file.getName().substring(0, idx));
	    } else {
	        prefs.remove("filePath");
	    }    
	}
	
	/**
	 * ���� ��ú��带 ������ ���Ͽ� �����Ѵ�.
	 *
	 * @param file
	 */
	public boolean saveUiDataToFile(File file) {
	    try {
	    	System.out.println("save DashBoard");
			toJson(dashBoardHashMap, boardLayoutController.getTabId(), file);

	        // ���� ��θ� ������Ʈ���� �����Ѵ�.
	        setUiFilePath(file);
	    } catch (Exception e) { // ���ܸ� ��´�.
	        Alert alert = new Alert(AlertType.ERROR);
	        alert.setTitle("Error");
	        alert.setHeaderText("Could not save data");
	        alert.setContentText("Could not save data to file:\n" + file.getPath());
	        
	        alert.showAndWait();
	        return false;
	    }
	    return true;
	}

    public void setChartNumber() {
    	Tab tab = boardLayoutController.getTabPane().getSelectionModel().getSelectedItem();
		AnchorPane a = (AnchorPane)tab.getContent();
        ScrollPane s = (ScrollPane)a.getChildren().get(0);
        FlowPane fp = (FlowPane)s.getContent();
        
        ObservableList<Node> workingCollection;
        if(tabSystemInfo.get(tab.getId()).getVersion().equals("PC")) {
        	// tabFlowPane�� �ִ� �ڽ� ��� ����Ʈ
    		workingCollection = FXCollections.observableArrayList(
    				fp.getChildren()
             );        	
        }else {
        	FlowPane backFp = (FlowPane) fp.getChildren().get(0);
        	// tabFlowPane�� �ִ� �ڽ� ��� ����Ʈ
    		workingCollection = FXCollections.observableArrayList(
    				backFp.getChildren()
             );
        }
        int rowCount = 0;
        int colCount = 0;
        for (int i = 0; i < workingCollection.size(); i++) {
        	DraggableNode node = (DraggableNode)workingCollection.get(i);
        	node.getChart().setNumber(i);
        	
        	if(i == 0) {	// ù ���
        		node.getChart().setRow(0);
        		node.getChart().setCol(0);
        		if(node.getChart().getType().equals("value") ||
        			node.getChart().getType().equals("image") ||
        			node.getChart().getType().equals("text"))
        			colCount++;
        		else 
        			colCount += 2;
        	}else { // �ι�° ������~
        		// ū Ÿ���� �Ѿ ��
        		if(!(node.getChart().getType().equals("value") ||
        				node.getChart().getType().equals("image") ||
            			node.getChart().getType().equals("text"))
        				&& colCount >= 5) {
        			rowCount++;
        			colCount = 0;
        		}else if((node.getChart().getType().equals("value") ||
        				node.getChart().getType().equals("image") ||
            			node.getChart().getType().equals("text"))
        				&& colCount >= 6) {	// ���� Ÿ���� �Ѿ ��
        			rowCount++;
        			colCount = 0;
        		}
        		// ���� ����
        		node.getChart().setRow(rowCount);
        		node.getChart().setCol(colCount);
        		if(node.getChart().getType().equals("value") ||
            			node.getChart().getType().equals("image") ||
            			node.getChart().getType().equals("text")) {
            			colCount++;
        		}else { 
            			colCount += 2;
        		}
        		
        	}
		}
        
        
    }
	
	// json �����ͷ� ��ȯ
	private void toJson(HashMap<String, List<Chart>> dashBoardHashMap, String tabId, File file) {
		System.out.println("converting start");
		setChartNumber();
		
		JSONObject obj = new JSONObject();
		
		List<Chart> charts = dashBoardHashMap.get(tabId);
		System.out.println("charts size = "+charts.size());
		SystemInfo info = tabSystemInfo.get(tabId);
		
		// insert "dashboardName" : "bb",
		int idx = file.getName().indexOf(".");
		dashBoardName = file.getName().substring(0, idx);
		obj.put("dashboardName", dashBoardName);
		obj.put("version", info.getVersion());
		obj.put("systemName", info.getName());
		obj.put("isVirtual", info.isVirtual());
		
		// insert "content":[]
		JSONArray jsonArray = new JSONArray();
		Map map = new LinkedHashMap(charts.size());
		for (int i = 0; i < charts.size(); i++) {
			Map m = new LinkedHashMap(11);	// chart
			
			m.put("chart", charts.get(i).getType());
			m.put("chartName", charts.get(i).getChartName());
			
			JSONArray topicArray = new JSONArray();
			for (int j = 0; j < charts.get(i).getTopicVector().size(); j++)
				topicArray.add(charts.get(i).getTopicVector().get(j));
			m.put("topics", topicArray);
			
			JSONArray ctrTopicArray = new JSONArray();
			for (int j = 0; j < charts.get(i).getCtrTopicVector().size(); j++)
				ctrTopicArray.add(charts.get(i).getCtrTopicVector().get(j));
			m.put("ctrTopics", ctrTopicArray);
			System.out.println(ctrTopicArray);
			
			JSONArray topicNameArray = new JSONArray();
			for (int j = 0; j < charts.get(i).getTopicLabelVector().size(); j++)
				topicNameArray.add(charts.get(i).getTopicLabelVector().get(j));
			m.put("topicLabels", topicNameArray);
			
			m.put("row", charts.get(i).getRow());
			m.put("level", charts.get(i).getLevel());
			m.put("unit", charts.get(i).getUnit());
			
			map.put(charts.get(i).getNumber(), m);
			
		}
		jsonArray.add(map);
		obj.put("content", jsonArray);
		System.out.println(obj);
		
		try(FileWriter fileWrite = new FileWriter(file)){
			fileWrite.write(obj.toJSONString());
			fileWrite.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText(null);
		alert.setContentText("json���Ϸ� ����Ǿ����ϴ�.");
	}
	
	// DB���� Ȯ��
	public boolean checkDBConnect() {
		JSONParser parser = new JSONParser();
		String url, user, pass, camera;
		try {
			Object obj = parser.parse(new FileReader("dbData.json"));
			JSONObject jsonObject = (JSONObject) obj;
			
			url = (String)jsonObject.get("url");
			user = (String)jsonObject.get("user");
			pass = (String)jsonObject.get("pass");

			connect.getPool().set_url(url);
			connect.getPool().set_user(user);
			connect.getPool().set_password(pass);
			
			System.out.println(connect.getPool().get_url());
			
			try {
				if(connect.getPool().createConnection() != null) {
					System.out.println("connect");
					isDbConnect = true;
					if(rootLayoutController.getIdCount() != 0) {
						String tabId = getBoardLayoutController().getTabId();
						SystemInfo info = tabSystemInfo.get(tabId); 
						tabTopicList.put(tabId, connect.getTopicList(info.getName()));
					}
					return true;
				}
				
			} catch (SQLException e) {
				System.out.println("connecting false");
				isDbConnect = false;
				return false;
			}
			
		} catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
		return false;
	}
	
	
	// ��Ʈ ����Ʈ ��ȯ
	public HashMap<String, List<Chart>> getDashBoardHashMap(){
		return dashBoardHashMap;
	}
	
	public void setRootLayout(BorderPane rootLayout) {
		this.rootLayout = rootLayout;
	}
	public void setRootLayoutController(RootLayoutController rootLayoutController) {
		this.rootLayoutController = rootLayoutController;
	}
	public RootLayoutController getRootLayoutController() {
		return rootLayoutController;
	}
    public BoardLayoutController getBoardLayoutController() {
    	return boardLayoutController;
    }
	
	public Scene getScene() {
		return scene;
	}
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public static HashMap<String, SystemInfo> getTabSystemInfo() {
		return tabSystemInfo;
	}
	

	public static void main(String[] args) {
		launch(args);
	}
}
