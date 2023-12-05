package application.view;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import application.Main;
import application.model.Chart;
import application.model.DraggableNode;
import application.model.SystemInfo;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RootLayoutController {
	//protected static final DataFormat DraggableNode = null;
	// ���� ���ø����̼� ����
	private Main main;
	private int idCount = 0;
	
	public static String clicked;
	
	private BoardLayoutController boardLayoutController;
	
	private double dashBoardPaneWidth = 0;
	private double dashBoardPaneHeight = 0;
	
	Vector<String> topicList;
	
	@FXML
	private BorderPane rootPane;
	

	// BoardLayoutController�� ���޹޴� �޼���
    public void setBoardController(BoardLayoutController boardLayoutController) {
    	this.boardLayoutController = boardLayoutController;
    }
	// ������ �����ϱ� ���� ���� ���÷����̼��� ȣ���Ѵ�
	public void setMain(Main main) {
		this.main = main;
	}
	
	/**
     * ��� �ִ� ��ú��� �����.
     */
    @FXML
    public boolean handleNew() {
    	TabPane tabPane = boardLayoutController.getTabPane();
    	// ���ο� �� ����
    	Tab tab = new Tab("* undefined tab"+(tabPane.getTabs().size()+1));
    	tab.setId(Integer.toString(idCount));
    	topicList = new Vector<String>();
    	if(!showSelectVersionDialog(tab.getId()))
    		return false;
    	
    	System.out.print("System : "+main.getTabSystemInfo().get(tab.getId()).getName()+", ");
    	System.out.print("isVirtual : "+main.getTabSystemInfo().get(tab.getId()).isVirtual()+", ");
    	System.out.println("Version : "+main.getTabSystemInfo().get(tab.getId()).getVersion());
    	
    	
    	if(main.isDbConnect) {
    		if(topicList.size() == 0) {
    			topicList = main.connect.getTopicList(main.getTabSystemInfo().get(tab.getId()).getName());
				main.tabTopicList.put(tab.getId(), topicList);
    		}
		}
    	else
			main.tabTopicList.put(tab.getId(), topicList);
    	
    	ScrollPane scrollPane = new ScrollPane();
    	AnchorPane tabAnchorPane = new AnchorPane();
    	FlowPane tabFlowPane = new FlowPane();
    	String version = main.getTabSystemInfo().get(tab.getId()).getVersion();
    	if(version.equals("PC")) {
    		tabFlowPane.setPrefSize(dashBoardPaneWidth-20, dashBoardPaneHeight);
    		tabFlowPane.setStyle("-fx-background-color:#EAEAEA;");
    		/*
        	tabFlowPane.setStyle(" -fx-background-color:#d8d8d8;-fx-background-image: url('file:img/pp.jpg');"
        			+ "-fx-background-size: "+dashBoardPaneWidth+" "+dashBoardPaneHeight+";");
        	*/
        	tabFlowPane.setId("tabFlowPane"+Integer.toString(idCount++));
        	addDragEvent(tabFlowPane);
        	
        	//ScrollPane�� tabFlowPane ����
        	scrollPane.setContent(tabFlowPane);
        	
    	}else if(version.equals("Mobile")){
    		FlowPane backFlowPane = new FlowPane();
    		backFlowPane.setPrefSize(dashBoardPaneWidth-20, dashBoardPaneHeight);
    		backFlowPane.setAlignment(Pos.TOP_CENTER);
    		backFlowPane.setStyle("-fx-background-color:#BDBDBD;");
    		
    		tabFlowPane.setAlignment(Pos.TOP_CENTER);
        	tabFlowPane.setPrefWidth(dashBoardPaneWidth/3+30);
        	tabFlowPane.setPrefHeight(dashBoardPaneHeight);
    		tabFlowPane.setStyle("-fx-background-color:#EAEAEA;");
        	
    		tabFlowPane.setId("tabFlowPane"+Integer.toString(idCount++));
    		addDragEvent(backFlowPane);
        	addDragEvent(tabFlowPane);
    		
    		backFlowPane.getChildren().add(tabFlowPane);
    		
    		//ScrollPane�� tabFlowPane ����
        	scrollPane.setContent(backFlowPane);
    	}
    	// �ǿ� ScrollPane ����
    	tabAnchorPane.getChildren().add(scrollPane);
    	
    	tab.setContent(tabAnchorPane);
    	tabPane.getTabs().add(tab);
    	tabPane.getSelectionModel().select(tab);
    	
    	List<Chart> list = new ArrayList<Chart>();
    	main.getDashBoardHashMap().put(tab.getId(), list);
    	
    	main.setUiFilePath(null);
    	
    	tab.setOnCloseRequest(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				// TODO ���� ������ flowPaneTabVector���� �ش� FlowPane ����
				//flowPaneTabVector.remove(tabFlowPane);
				
			}
		});
    	return true;
    }
    private void addDragEvent(FlowPane dashBoardPane) {
    	dashBoardPane.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				if(event.getGestureSource() != dashBoardPane) {
					event.acceptTransferModes(TransferMode.ANY);
				}
				event.consume();
			}
		});
    }
    
    // TODO DraggableNode ����
 	public void createNode(FlowPane dashBoardPane,Tab tab, Chart chart) {
 		if(dashBoardPaneWidth == 0 || dashBoardPaneHeight == 0) {
 			dashBoardPaneWidth = dashBoardPane.getWidth();
 			dashBoardPaneHeight = dashBoardPane.getHeight();
 		}
		// ū ��Ʈ ����
		DraggableNode node = new DraggableNode(boardLayoutController);
		node.setMain(main);
		node.setChart(chart);
		node.setId(Integer.toString(node.hashCode()));
		
		int level = 0;
		
		if(chart.getChartName().equals("null") == false)
			node.getTitle().setText(chart.getChartName());

		System.out.println("create "+chart.getType());

		if(chart.getType().equals("text")) {
			TextArea textArea = new TextArea();
			node.setTextArea(textArea);
			
			// text Area set size
			if(main.getTabSystemInfo().get(tab.getId()).getVersion().equals("PC")) {
				textArea.setPrefWidth((dashBoardPaneWidth/6)-25);
				textArea.setPrefHeight((dashBoardPaneHeight/4));
				textArea.setMaxSize(textArea.getPrefWidth(), textArea.getPrefHeight());
				textArea.setMinSize(textArea.getPrefWidth(), textArea.getPrefHeight());
				node.setPrefWidth((dashBoardPaneWidth/6)-25);
				node.setPrefHeight(dashBoardPaneHeight/4);	
				//node.getGridPane().setPrefWidth((dashBoardPaneWidth/6)-31);
			}else if(main.getTabSystemInfo().get(tab.getId()).getVersion().equals("Mobile")){
				textArea.setPrefWidth((dashBoardPaneWidth/3)-30);
				textArea.setPrefHeight(dashBoardPaneHeight/4);
				textArea.setMaxSize(textArea.getPrefWidth(), textArea.getPrefHeight());
				textArea.setMinSize(textArea.getPrefWidth(), textArea.getPrefHeight());
				node.setPrefWidth((dashBoardPaneWidth/3)-30);
				node.setPrefHeight(dashBoardPaneHeight/4);	
				//node.getGridPane().setPrefWidth((dashBoardPaneWidth/3)-31);
			}
			
			
			if(chart.getTopicVector().size() != 0)
				textArea.setText(chart.getTopicVector().get(0));
			
			textArea.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					if(chart.getTopicVector().size() == 0)
						chart.getTopicVector().add(newValue);
					else
						chart.getTopicVector().set(0, newValue);
				}
			});
			textArea.setOnDragDetected(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					Dragboard dragBoard = node.startDragAndDrop(TransferMode.MOVE);
					
					ClipboardContent content = new ClipboardContent();
					content.putString(node.getId());
					dragBoard.setContent(content);
					event.consume();
				}
			});
	 		
			level = 2;
			
			node.getRootPane().setCenter(textArea);
			chart.setLevel(level);
			
		}else {
			ImageView iv;
			// set ImageView, level
			if(chart.getType().equals("bar")) {
				iv = new ImageView(new Image("file:resources/images/Bar.png"));
				level = 1;
			} else if(chart.getType().equals("histogram")) {
				iv = new ImageView(new Image("file:resources/images/Histogram.png"));
				level = 1;
			}else if(chart.getType().equals("line")) {
				iv = new ImageView(new Image("file:resources/images/Line.png"));
				level = 1;
			}else if(chart.getType().equals("gauge")) {
				iv = new ImageView(new Image("file:resources/images/Gauge.png"));
				level = 1;
			}else if(chart.getType().equals("doughnut")) {
				iv = new ImageView(new Image("file:resources/images/Doughnut.png"));
				level = 1;
			}else if(chart.getType().equals("pie")) {
				iv = new ImageView(new Image("file:resources/images/Pie.png"));
				level = 1;
			}else if(chart.getType().equals("video")) {
				iv = new ImageView(new Image("file:resources/images/streamming.png"));
				level = 1;
			}else if(chart.getType().equals("camera")) {
				iv = new ImageView(new Image("file:resources/images/Swiss.jpg"));
				level = 1;
			}else if(chart.getType().equals("value")){
				iv = new ImageView(new Image("file:resources/images/Value.png"));
				level = 2;
			}else {		// image
				if(chart.getCtrTopicVector().size() != 0) {
					String path = chart.getCtrTopicVector().get(0);
					iv = new ImageView(new Image(path));
				}
				else
					iv = new ImageView(new Image("file:resources/images/peach.png"));
				level = 2;
			}
			node.setImageView(iv);
			node.getRootPane().setCenter(iv);

			chart.setLevel(level);
			
			// set size node
			if(main.getTabSystemInfo().get(tab.getId()).getVersion().equals("Mobile")) {
				node.getImageView().setFitWidth((dashBoardPaneWidth/3)-30);
				node.getImageView().setFitHeight(dashBoardPaneHeight/4);
				node.setPrefWidth((dashBoardPaneWidth/3)-30);
				node.setPrefHeight(dashBoardPaneHeight/4);
			}else if(main.getTabSystemInfo().get(tab.getId()).getVersion().equals("PC")){
				if(chart.getType().equals("value") || chart.getType().equals("image")) {
					node.getImageView().setFitWidth((dashBoardPaneWidth/6)-25);
					node.getImageView().setFitHeight(dashBoardPaneHeight/4);
					node.setPrefWidth((dashBoardPaneWidth/6)-25);
					node.setPrefHeight(dashBoardPaneHeight/4);	
				}else {
					node.getImageView().setFitWidth((dashBoardPaneWidth/3)-30);
					node.getImageView().setFitHeight(dashBoardPaneHeight/4);
					node.setPrefWidth((dashBoardPaneWidth/3)-30);
					node.setPrefHeight(dashBoardPaneHeight/4);
				}
			}
		}
		
		node.getRootPane().setStyle("-fx-effect : dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0)");
				
		// append dragEvent, mouseEvent
		addDragNodeEvent(node, dashBoardPane);
		dashBoardPane.getChildren().add(node);
		
		
		node.getImageView().setOnMouseClicked( new EventHandler <MouseEvent> () {
			@Override
			public void handle(MouseEvent event) {
				// ���� ���̾�α׸� ����.
				if(node.getChart().getType().equals("video") || node.getChart().getType().equals("camera")) {
					boolean applyClicked = showStreammingDialog(node);
					node.getTitle().setText(node.getChart().getChartName());
				} else if (node.getChart().getType().equals("image")){
					imageToBinary(node);
				} else {
					boolean applyClicked = showModifyDialog(node);
					node.getTitle().setText(node.getChart().getChartName());
				}
			}
		});
		
		node.getImageView().setOnMouseEntered(new EventHandler() {
			@Override
			public void handle(Event event) {
				main.getScene().setCursor(Cursor.HAND);
			}
		});
		node.getImageView().setOnMouseExited(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				main.getScene().setCursor(Cursor.DEFAULT);
			}
		});
		
		// tab title�� ����� ǥ�����ֱ�
		if(!tab.getText().contains("*"))
			tab.setText("* "+tab.getText());
 	}
 	// node�� �̺�Ʈ ó��
 	private void addDragNodeEvent(DraggableNode node, FlowPane dashBoardPane) {
 		
 		node.setOnDragDetected(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				
				Dragboard dragBoard = node.startDragAndDrop(TransferMode.MOVE);
				
				ClipboardContent content = new ClipboardContent();
				content.putString(node.getId());
				dragBoard.setContent(content);
				event.consume();
				
			}
		});
 		node.setOnDragEntered(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				//System.out.println("event node : "+node.getChart().getType());
				event.acceptTransferModes(TransferMode.ANY);
				// �巡�� �ϰ��ִ� ���
				DraggableNode draggingNode = (DraggableNode) main.getScene().lookup("#"+event.getDragboard().getString());
				System.out.println("drag type : "+draggingNode.getChart().getType());
				
				System.out.println("target type : "+node.getChart().getType());
				// ��ú��� flowPane�� �ִ� �ڽĳ�� ����Ʈ
				ObservableList<Node> workingCollection = FXCollections.observableArrayList(
	                    dashBoardPane.getChildren()
	             );
				// �ڽĳ�� ����Ʈ���� �巡���ϰ��ִ� ���� Ÿ�ϳ���� index �ľ�
				int nodeIndex = 0, draggingNodeIndex = 0;
				for (int j = 0; j < workingCollection.size(); j++) {
					DraggableNode d = (DraggableNode)workingCollection.get(j);
					if(d.getId() == node.getId())
						nodeIndex = j;
				}
				for (int j = 0; j < workingCollection.size(); j++) {
					DraggableNode d = (DraggableNode)workingCollection.get(j);
					if(d.getId() == draggingNode.getId())
						draggingNodeIndex = j;
				}
				
				// �ڽĳ�� ����Ʈ���� �巡���ϰ��ִ³�带 Ÿ�ٳ��� �ٲ��ش�
				workingCollection.set(draggingNodeIndex, node);				
				// �ڽĳ�� ����Ʈ���� Ÿ�� ��带 �巡���ϰ��ִ³��� �ٲ��ش�
				workingCollection.set(nodeIndex, draggingNode);
				
				// ��ú��� flowPane�� �ڽĸ���Ʈ�� ���� set���ش�
				dashBoardPane.getChildren().setAll(workingCollection);
				
				// tab title�� ����� ǥ�����ֱ�
				Tab tab = boardLayoutController.getTabPane().getSelectionModel().getSelectedItem();
				if(!tab.getText().contains("*"))
					tab.setText("* "+tab.getText());
				
				event.consume();
			}
		});
 	}
 	// image�� ���̳ʸ� �����ͷ� ��ȯ
 	private void imageToBinary(DraggableNode node) {
 		FileChooser fileChooser = new FileChooser();

        // Ȯ���� ���͸� �����Ѵ�.
        fileChooser.getExtensionFilters().addAll(
        	    new FileChooser.ExtensionFilter("All Images", "*.*"),
        	    new FileChooser.ExtensionFilter("JPG", "*.jpg"),
        	    new FileChooser.ExtensionFilter("GIF", "*.gif"),
        	    new FileChooser.ExtensionFilter("BMP", "*.bmp"),
        	    new FileChooser.ExtensionFilter("PNG", "*.png")
        	);

    	File file = fileChooser.showOpenDialog(null);
    	String imageFormat =null;
    	String encodedFile = null;
    	FileInputStream fis = null;
    	String filePath = null;
        // File Dialog�� �����ش�.
        try {
        	int idx = file.getName().indexOf(".");
    		imageFormat = file.getName().substring(idx+1);
			fis = new FileInputStream(file);
			if(fis == null)
				return;
			filePath = file.toURI().toURL().toString();
			byte[] bytes = new byte[(int)file.length()];
			fis.read(bytes);
	        encodedFile = Base64.getEncoder().encodeToString(bytes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        Image image = new Image(filePath);       
        
        node.getImageView().setImage(image);
        
        if(node.getChart().getTopicVector().size() == 0)
        	node.getChart().getTopicVector().add(encodedFile);
        else
        	node.getChart().getTopicVector().set(0, encodedFile);
        
        if(node.getChart().getTopicLabelVector().size() == 0)
        	 node.getChart().getTopicLabelVector().add(imageFormat);
        else
        	node.getChart().getTopicLabelVector().set(0, imageFormat);
        
        if(node.getChart().getCtrTopicVector().size() == 0)
        	node.getChart().getCtrTopicVector().add(file.getPath());
        else
        	node.getChart().getCtrTopicVector().set(0, file.getPath());
 	}
 	
 	
 	// ��Ʈ�鿡 ���� �� �����Ǿ����� Ȯ��(�˻�)
 	private boolean inInputValid() {
 		String errorMessage = "";
    	
 		// ��ú����� ��Ʈ���� ����� �ȳ־��� �� �˻��Ͽ� �˸�â�� ���� �׳� ��ȯ�Ѵ�.
 		for (int j = 0; j < main.getDashBoardHashMap().get(main.getBoardLayoutController().getTabId()).size(); j++) {
 			Chart c = main.getDashBoardHashMap().get(main.getBoardLayoutController().getTabId()).get(j);
 			if(c.getChartName() == "null" || c.getChartName().length() == 0) {
 	    		errorMessage += "No valid Chart Name. {Chart Position : " + (j+1) + " Chart} \n";
 	    	}
 			// ��Ʈ ���� �������� �ʾƵ� �˻� �����Ű�� ���� �ּ�ó����
 			/*
 	    	if(c.getFirstTopic() == "null" || c.getFirstTopic().length() == 0) {
 	    		errorMessage += "No valid Sensor. {Chart Number : " + j + " Chart} \n";
 	    	}
 	    	if(c.getFirstName() == "null" || c.getFirstName().length() == 0) {
 	    		errorMessage += "No valid Sensor Label. {Chart Number : " + j + " Chart} \n";
 	    	}
 	    	*/
		}
 		if(errorMessage.length() == 0) {
    		return true;
    	}else {
    		 // ���� �޽����� �����ش�.
    		Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText("��Ʈ�� ����� �������� �ʾҽ��ϴ�");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
    	}
    	
 	}
 	
 	/**
 	 * json������ ���� �Ľ��Ͽ� ���� ���õ� tab�� �׸���.
 	 */
 	@FXML
 	private void handleOpen() {
 		FileChooser fileChooser = new FileChooser();

        // Ȯ���� ���͸� �����Ѵ�.
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        // Save File Dialog�� �����ش�.
        File file = fileChooser.showOpenDialog(null);
            
        if (file != null) {
        	handleNew();
            Tab tab = boardLayoutController.getTabPane().getSelectionModel().getSelectedItem();
            
			AnchorPane a = (AnchorPane)tab.getContent();
            ScrollPane s = (ScrollPane)a.getChildren().get(0);
            FlowPane fp = (FlowPane)s.getContent();
            
            List<Chart> list = new ArrayList<Chart>();
        	main.getDashBoardHashMap().put(tab.getId(), list);
        	
        	// json parser
            JSONParser parser = new JSONParser();
            try {
            	Object obj = parser.parse(new FileReader(file.getAbsolutePath()));
    			JSONObject jsonObject = (JSONObject) obj;
    			tab.setText((String)jsonObject.get("dashboardName"));
    			
    			JSONArray content = (JSONArray) jsonObject.get("content");
    			JSONObject contentObject = (JSONObject)content.get(0);
    			
    			Chart chart;
    			ImageView imageView;
    			
    			int i = 0;
    			while(contentObject.get(Integer.toString(i)) != null) {
    				Map m = (Map)contentObject.get(Integer.toString(i));
    				String chartType = (String)m.get("chart"); 
    				
    				if(chartType.equals("bar")) {
    					imageView = new ImageView(new Image("file:resources/images/Bar.png"));
    					chart = new Chart("bar");
    				} else if(chartType.equals("histogram")) {
    					imageView = new ImageView(new Image("file:resources/images/Histogram.png"));
    					chart = new Chart("histogram");
    				}else if(chartType.equals("line")) {
    					imageView = new ImageView(new Image("file:resources/images/Line.png"));
    					chart = new Chart("line");
    				}else if(chartType.equals("gauge")) {
    					imageView = new ImageView(new Image("file:resources/images/Gauge.png"));
    					chart = new Chart("gauge");
    				}else if(chartType.equals("doughnut")) {
    					imageView = new ImageView(new Image("file:resources/images/Doughnut.png"));
    					chart = new Chart("doughnut");
    				}else if(chartType.equals("pie")) {
    					imageView = new ImageView(new Image("file:resources/images/Pie.png"));
    					chart = new Chart("pie");
    				}else if(chartType.equals("image")) {
    					imageView = new ImageView(new Image("file:resources/images/image.png"));
    					chart = new Chart("image");
    				}else if(chartType.equals("text")) {
    					imageView = new ImageView(new Image("file:resources/images/Doughnut.png"));
    					chart = new Chart("text");
    				}else if(chartType.equals("video")) {
    					imageView = new ImageView(new Image("file:resources/images/streamming.png"));
    					chart = new Chart("video");
    				}else if(chartType.equals("camera")) {
    					imageView = new ImageView(new Image("file:resources/images/image.png"));
    					chart = new Chart("camera");
    				}else {	// value
    					imageView = new ImageView(new Image("file:resources/images/Value.png"));
    					chart = new Chart("value");
    				}
    				
    				chart.setChartName((String)m.get("chartName"));
    				
    				JSONArray topicArray = (JSONArray)m.get("topics");
    				Vector<String> topicVector = new Vector<String>(); 
    				for (int j = 0; j < topicArray.size(); j++) {
						topicVector.add((String)topicArray.get(j));
					}
    				chart.setTopicVector(topicVector);
    				
    				JSONArray ctrTopicArray = (JSONArray)m.get("ctrTopics");
    				Vector<String> ctrTopicVector = new Vector<String>(); 
    				for (int j = 0; j < ctrTopicArray.size(); j++) {
    					ctrTopicVector.add((String)ctrTopicArray.get(j));
					}
    				chart.setCtrTopicVector(ctrTopicVector);
    				
    				JSONArray topicNameArray = (JSONArray)m.get("topicLabels");
    				Vector<String> topicNameVector = new Vector<String>(); 
    				for (int j = 0; j < topicNameArray.size(); j++) {
    					topicNameVector.add((String)topicNameArray.get(j));
					}
    				chart.setTopicLabelVector(topicNameVector);
    				
					chart.setNumber(i);
					chart.setUnit((String)m.get("unit"));
					
					main.getDashBoardHashMap().get(tab.getId()).add(chart);
					
					if(main.getTabSystemInfo().get(tab.getId()).getVersion().equals("PC"))
						createNode(fp, tab, chart);
					else if(main.getTabSystemInfo().get(tab.getId()).getVersion().equals("Mobile")){
						FlowPane backFp = (FlowPane)fp.getChildren().get(0);
						createNode(backFp, tab, chart);
					}
    				i++;
    				
    			}
    			// ������ ���� �ּҸ� open�� ���Ϸ� �ٲ��ش�.
    			main.setUiFilePath(file);
    			
            } catch (FileNotFoundException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		} catch (ParseException e) {
    			e.printStackTrace();
    		}
        }
 	}
 	
    /**
     * ���� ���� �ִ� ���Ͽ� �����Ѵ�.
     * ���� ���� �ִ� ������ ������ "save as" ���̾�α׸� �����ش�.
     *
     */
    // ��ú��� ����
 	@FXML
 	public void handleSave() {
 		boolean flag = inInputValid();
 		if(flag == false)
 			return;
 		
 		File dashBoardFile = main.getUiFilePath();
 		if (dashBoardFile != null) {
 			main.saveUiDataToFile(dashBoardFile);
 		} else {
 			handleSaveAs();
 		}
 		
 	}
 	
    /**
     * FileChooser�� ��� ����ڰ� ������ ������ �����ϰ� �Ѵ�.
     */
    @FXML
    private void handleSaveAs() {
        FileChooser fileChooser = new FileChooser();

        // Ȯ���� ���͸� �����Ѵ�.
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        // Save File Dialog�� �����ش�.
        File file = fileChooser.showSaveDialog(main.getPrimaryStage());

        if (file != null) {
            // ��Ȯ�� Ȯ���ڸ� ������ �Ѵ�.
            if (!file.getPath().endsWith(".json")) {
                file = new File(file.getPath() + ".json");
            }
            boolean okFlag = main.saveUiDataToFile(file);
            if(okFlag == false)
            	return;
            
            // �����ͺ��̽��� �ٷ� �ֱ� Ȯ��       
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("");
            alert.setContentText("�����ͺ��̽��� �ٷ� ���ε��Ͻðڽ��ϱ�?");
            Optional<ButtonType> result = alert.showAndWait();

            if(result.get() == ButtonType.OK) {
            	if(!main.isDbConnect) {
        			Alert alertErr = new Alert(AlertType.ERROR);
        			alertErr.setTitle("�����ͺ��̽� ���� ����");
        			alertErr.setHeaderText("Please check Database setting");
        			alertErr.setContentText("�����ͺ��̽��� ����Ǿ� ���� �ʽ��ϴ�.");

        			alertErr.showAndWait();
                    return;
        		}
            	JSONParser parser = new JSONParser();
            	Object obj;
    			try {
    				obj = parser.parse(new FileReader(file.getAbsolutePath()));
    				JSONObject jsonObject = (JSONObject) obj;
    				
    				Tab tab = boardLayoutController.getTabPane().getSelectionModel().getSelectedItem();
    	 			SystemInfo info = main.getTabSystemInfo().get(tab.getId());
    	 			String version = info.getVersion();
    				System.out.println(version + ", "+(String)jsonObject.get("dashboardName"));
    				
    				String fileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
    				// �̹� �ִ� ��ú��� ���� �̸� Ȯ���Ͽ� update
    				if(main.connect.isDashBoardExist(version, fileName)) {
    					Alert alertCon = new Alert(AlertType.CONFIRMATION);
    					alertCon.setTitle("Confirmation Dialog");
    					alertCon.setHeaderText("�̹� �ִ� ��ú��� ���� �̸��Դϴ�.");
    					alertCon.setContentText("�����ðڽ��ϱ�?");
    					
    					Optional<ButtonType> resultCon = alertCon.showAndWait();
    					if(resultCon.get() == ButtonType.OK) {
    						main.connect.updateDashboard(version, fileName, jsonObject, info.isVirtual(), info.getName());
    					}else{
    						Alert a = new Alert(AlertType.INFORMATION);
    			            a.setTitle("cancel");
    			            a.setContentText(fileName+"�� ������ ��ҵǾ����ϴ�.");
    						return;
    					}
    				}else {
    					// ���ο� ��ú��� �����̸� insert
    					main.connect.insertDashboard(version, fileName, jsonObject, info.isVirtual(), info.getName());
    				}
    				Alert alertfin = new Alert(AlertType.INFORMATION);
        			alertfin.setTitle("Dadabase�� �����Ͽ����ϴ�.");
        			alertfin.setHeaderText("Dadabase�� �����Ͽ����ϴ�.");
        			alertfin.setContentText("Dadabase�� �����Ͽ����ϴ�.");

        			alertfin.showAndWait();
    			} catch (FileNotFoundException e) {
    				e.printStackTrace();
    			} catch (IOException e) {
    				e.printStackTrace();
    			} catch (ParseException e) {
    				e.printStackTrace();
    			}
            }
        }
    }
    
    /**
     *  JSON�� �����ͺ��̽��� ����
     */
    @FXML
    private void handleSend() {
    	boolean sendClicked = showUploadDialog();
    }
    
    // send ���̾�α׸� ����
 	public boolean showUploadDialog() {
 		//main.checkDBConnect();
 		try {
 			// FXML ������ �ε��ϰ� ���� ���ο� ���������� �����.
 			FXMLLoader loader = new FXMLLoader();
 			loader.setLocation(Main.class.getResource("fxml/SendDialog.fxml"));
 			AnchorPane dialog = (AnchorPane) loader.load();
 			
 			// ���̾�α� ���������� �����.
 			Stage dialogStage = new Stage();
 			if(main.isDbConnect)
 				dialogStage.setTitle("Upload DashBoard");
 			else
 				dialogStage.setTitle("Upload DashBoard - Database not connected");
 			
 			dialogStage.initModality(Modality.WINDOW_MODAL);
 			dialogStage.initOwner(main.getPrimaryStage());
 			Scene scene = new Scene(dialog);
 			dialogStage.setScene(scene);
 			
 			// SendDialog�� ��Ʈ�� ����
 			SendDialogController controller = loader.getController();
 			controller.setDialogStage(dialogStage);
 			
 			//Tab tab = boardLayoutController.getTabPane().getSelectionModel().getSelectedItem();
 			//controller.setVersion(main.getVersion().get(tab.getId()));
 			
 			dialogStage.showAndWait();
 			
 			return controller.isOkClicked();
 		} catch (IOException e) {
 			e.printStackTrace();
 			return false;
 		}
 	}
 	
 	@FXML
 	private void handleLoad() {
 		boolean loadClicked = showLoadDialog();
 	}
 	private boolean showLoadDialog() {
 		//main.checkDBConnect();
 		try {
 			// FXML ������ �ε��ϰ� ���� ���ο� ���������� �����.
 			FXMLLoader loader = new FXMLLoader();
 			loader.setLocation(Main.class.getResource("fxml/LoadDialog.fxml"));
 			AnchorPane dialog = (AnchorPane) loader.load();
 			
 			// ���̾�α� ���������� �����.
 			Stage dialogStage = new Stage();
 			if(main.isDbConnect)
 				dialogStage.setTitle("Load Dialog");
 			else
 				dialogStage.setTitle("Load Dialog - Database not connected");
 			dialogStage.initModality(Modality.WINDOW_MODAL);
 			dialogStage.initOwner(main.getPrimaryStage());
 			Scene scene = new Scene(dialog);
 			dialogStage.setScene(scene);
 			
 			// LoadDialog�� ��Ʈ�� ����
 			LoadDialogController controller = loader.getController();
 			controller.setDialogStage(dialogStage);
 			controller.setMain(main);
 			
 			dialogStage.showAndWait();
 			
 			return controller.isApplyClicked();
 		} catch (IOException e) {
 			e.printStackTrace();
 			return false;
 		}
 	}
    
    // ��Ʈ ���� ���̾�α� ����
 	public boolean showModifyDialog(DraggableNode node) {
 		//main.checkDBConnect();
 		if(node.getChart().getType().equals("value")) {
 			try {
	 			// FXML ������ �ε��ϰ� ���� ���ο� ���������� �����.
	 			FXMLLoader loader = new FXMLLoader();
	 			loader.setLocation(Main.class.getResource("fxml/ValueTypeDialog.fxml"));
	 			AnchorPane dialog = (AnchorPane) loader.load();
	 			
	 			// ���̾�α� ���������� �����.
	 			Stage dialogStage = new Stage();
	 			if(main.isDbConnect)
	 				dialogStage.setTitle("Modity Dialog");
	 			else
	 				dialogStage.setTitle("Modity Dialog - Database not connected");
	 			dialogStage.initModality(Modality.WINDOW_MODAL);
	 			dialogStage.initOwner(main.getPrimaryStage());
	 			Scene scene = new Scene(dialog);
	 			dialogStage.setScene(scene);
	 			
	 			// ModifyDialog�� ��Ʈ�� ����
	 			ValueTypeDialogController controller = loader.getController();
	 			controller.setDraggableNode(node);
	 			controller.setDialogStage(dialogStage);
	 			controller.setMain(main);
	 			controller.setChart(node.getChart());
	 			
	 			dialogStage.showAndWait();
	 			
	 			return controller.isApplyClicked();
 			} catch (IOException e) {
	 			e.printStackTrace();
	 			return false;
	 		}
 		} else {
	 		try {
	 			// FXML ������ �ε��ϰ� ���� ���ο� ���������� �����.
	 			FXMLLoader loader = new FXMLLoader();
	 			loader.setLocation(Main.class.getResource("fxml/ModifyDialog.fxml"));
	 			AnchorPane dialog = (AnchorPane) loader.load();
	 			
	 			// ���̾�α� ���������� �����.
	 			Stage dialogStage = new Stage();
	 			if(main.isDbConnect)
	 				dialogStage.setTitle("Modity Dialog");
	 			else
	 				dialogStage.setTitle("Modity Dialog - Database not connected");
	 			dialogStage.initModality(Modality.WINDOW_MODAL);
	 			dialogStage.initOwner(main.getPrimaryStage());
	 			Scene scene = new Scene(dialog);
	 			dialogStage.setScene(scene);
	 			
	 			// ModifyDialog�� ��Ʈ�� ����
	 			ModifyDialogController controller = loader.getController();
	 			controller.setDraggableNode(node);
	 			controller.setDialogStage(dialogStage);
	 			controller.setMain(main);
	 			controller.setChart(node.getChart());
	 			
	 			dialogStage.showAndWait();
	 			
	 			return controller.isApplyClicked();
	 		} catch (IOException e) {
	 			e.printStackTrace();
	 			return false;
	 		}
 		}
 	}
 	
 	// streamming ���� ���̾�α� ����
 	public boolean showStreammingDialog(DraggableNode node) {
 		//main.checkDBConnect();
 		try {
 			// FXML ������ �ε��ϰ� ���� ���ο� ���������� �����.
 			FXMLLoader loader = new FXMLLoader();
 			loader.setLocation(Main.class.getResource("fxml/StreammingDialog.fxml"));
 			AnchorPane dialog = (AnchorPane) loader.load();
 			
 			// ���̾�α� ���������� �����.
 			Stage dialogStage = new Stage();
 			
 			dialogStage.initModality(Modality.WINDOW_MODAL);
 			dialogStage.initOwner(main.getPrimaryStage());
 			Scene scene = new Scene(dialog);
 			dialogStage.setScene(scene);
 			
 			// ModifyDialog�� ��Ʈ�� ����
 			StreammingDialogController controller = loader.getController();
 			//controller.setDraggableNode(node);
 			controller.setDialogStage(dialogStage);
 			controller.setMain(main);
 			controller.setChart(node.getChart());
 			
 			dialogStage.showAndWait();
 			
 			return controller.isApplyClicked();
 		} catch (IOException e) {
 			e.printStackTrace();
 			return false;
 		}
 	}

 	/**
 	 * PC���� Mobile���� �����ϴ� ���̾�α׸� ����.
 	 */
	public boolean showSelectVersionDialog(String tabId) {
		//main.getVersion().put(tabId, "PC"); // �ʱⰪ = PC
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("fxml/SelectVersionDialog.fxml"));
			AnchorPane dialog = (AnchorPane) loader.load();
			
			Stage dialogStage = new Stage();
			if(main.isDbConnect)
 				dialogStage.setTitle("Create New File");
 			else
 				dialogStage.setTitle("Create New File - Database not connected");

			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(main.getPrimaryStage());
			Scene scene = new Scene(dialog);
			dialogStage.setScene(scene);
			
			SelectVersionDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMain(main);
			controller.setTabId(tabId);
			controller.setTopicList(topicList);
			
			dialogStage.showAndWait();
			
			if(main.getTabSystemInfo().get(tabId) == null) {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

 	
 	/**
 	 * �����ͺ��̽� ���� ����â�� ����. 
 	 */
    @FXML
    private void handleDatabase() {
    	try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("fxml/DatabaseOptionDialog.fxml"));
			AnchorPane dialog = (AnchorPane) loader.load();
			
			Stage dialogStage = new Stage();
			
			if(main.isDbConnect)
 				dialogStage.setTitle("Connection");
 			else
 				dialogStage.setTitle("Connection - Database not connected");
			
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(main.getPrimaryStage());
			Scene scene = new Scene(dialog);
			dialogStage.setScene(scene);
			
			DatabaseOptionDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMain(main);
			
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * ���ķ����� ���̾�α׸� �����ش�.
     */
    @FXML
    private void handleEmulator() {
    	try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("fxml/EmulatorDialog.fxml"));
			AnchorPane dialog = (AnchorPane) loader.load();
			
			Stage dialogStage = new Stage();
			if(main.isDbConnect)
 				dialogStage.setTitle("Emulator Setting");
 			else
 				dialogStage.setTitle("Emulator Setting - Database not connected");
			
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(main.getPrimaryStage());
			Scene scene = new Scene(dialog);
			dialogStage.setScene(scene);
			
			EmulatorController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMain(main);
			
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * ���ø����̼��� �ݴ´�.
     */
    @FXML
    private void handleExit() {
        System.exit(0);
    }
    
	public BorderPane getRootPane() {
		return rootPane;
	}

	public void setRootPane(BorderPane rootPane) {
		this.rootPane = rootPane;
	}

	public double getDashBoardPaneWidth() {
		return dashBoardPaneWidth;
	}

	public void setDashBoardPaneWidth(double dashBoardPaneWidth) {
		this.dashBoardPaneWidth = dashBoardPaneWidth;
	}

	public double getDashBoardPaneHeight() {
		return dashBoardPaneHeight;
	}

	public void setDashBoardPaneHeight(double dashBoardPaneHeight) {
		this.dashBoardPaneHeight = dashBoardPaneHeight;
	}
	public int getIdCount() {
		return idCount;
	}
	public void setIdCount(int idCount) {
		this.idCount = idCount;
	}
    
    
 	
}

