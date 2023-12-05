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
	// 메인 애플리케이션 참조
	private Main main;
	private int idCount = 0;
	
	public static String clicked;
	
	private BoardLayoutController boardLayoutController;
	
	private double dashBoardPaneWidth = 0;
	private double dashBoardPaneHeight = 0;
	
	Vector<String> topicList;
	
	@FXML
	private BorderPane rootPane;
	

	// BoardLayoutController를 전달받는 메서드
    public void setBoardController(BoardLayoutController boardLayoutController) {
    	this.boardLayoutController = boardLayoutController;
    }
	// 참조를 유지하기 위해 메인 애플레케이션을 호출한다
	public void setMain(Main main) {
		this.main = main;
	}
	
	/**
     * 비어 있는 대시보드 만든다.
     */
    @FXML
    public boolean handleNew() {
    	TabPane tabPane = boardLayoutController.getTabPane();
    	// 새로운 탭 생성
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
        	
        	//ScrollPane에 tabFlowPane 삽입
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
    		
    		//ScrollPane에 tabFlowPane 삽입
        	scrollPane.setContent(backFlowPane);
    	}
    	// 탭에 ScrollPane 삽입
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
				// TODO 탭을 닫으면 flowPaneTabVector에서 해당 FlowPane 삭제
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
    
    // TODO DraggableNode 생성
 	public void createNode(FlowPane dashBoardPane,Tab tab, Chart chart) {
 		if(dashBoardPaneWidth == 0 || dashBoardPaneHeight == 0) {
 			dashBoardPaneWidth = dashBoardPane.getWidth();
 			dashBoardPaneHeight = dashBoardPane.getHeight();
 		}
		// 큰 차트 생성
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
				// 수정 다이얼로그를 연다.
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
		
		// tab title에 변경된 표시해주기
		if(!tab.getText().contains("*"))
			tab.setText("* "+tab.getText());
 	}
 	// node의 이벤트 처리
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
				// 드래그 하고있는 노드
				DraggableNode draggingNode = (DraggableNode) main.getScene().lookup("#"+event.getDragboard().getString());
				System.out.println("drag type : "+draggingNode.getChart().getType());
				
				System.out.println("target type : "+node.getChart().getType());
				// 대시보드 flowPane에 있는 자식노드 리스트
				ObservableList<Node> workingCollection = FXCollections.observableArrayList(
	                    dashBoardPane.getChildren()
	             );
				// 자식노드 리스트에서 드래그하고있는 노드와 타켓노드의 index 파악
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
				
				// 자식노드 리스트에서 드래그하고있는노드를 타겟노드로 바꿔준다
				workingCollection.set(draggingNodeIndex, node);				
				// 자식노드 리스트에서 타겟 노드를 드래그하고있는노드로 바꿔준다
				workingCollection.set(nodeIndex, draggingNode);
				
				// 대시보드 flowPane의 자식리스트를 새로 set해준다
				dashBoardPane.getChildren().setAll(workingCollection);
				
				// tab title에 변경된 표시해주기
				Tab tab = boardLayoutController.getTabPane().getSelectionModel().getSelectedItem();
				if(!tab.getText().contains("*"))
					tab.setText("* "+tab.getText());
				
				event.consume();
			}
		});
 	}
 	// image를 바이너리 데이터로 변환
 	private void imageToBinary(DraggableNode node) {
 		FileChooser fileChooser = new FileChooser();

        // 확장자 필터를 설정한다.
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
        // File Dialog를 보여준다.
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
 	
 	
 	// 차트들에 값이 잘 설정되었는지 확인(검사)
 	private boolean inInputValid() {
 		String errorMessage = "";
    	
 		// 대시보드의 차트값을 제대로 안넣었을 때 검사하여 알림창만 띄우고 그냥 반환한다.
 		for (int j = 0; j < main.getDashBoardHashMap().get(main.getBoardLayoutController().getTabId()).size(); j++) {
 			Chart c = main.getDashBoardHashMap().get(main.getBoardLayoutController().getTabId()).get(j);
 			if(c.getChartName() == "null" || c.getChartName().length() == 0) {
 	    		errorMessage += "No valid Chart Name. {Chart Position : " + (j+1) + " Chart} \n";
 	    	}
 			// 차트 토픽 설정하지 않아도 검사 통과시키기 위해 주석처리함
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
    		 // 오류 메시지를 보여준다.
    		Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText("차트가 제대로 설정되지 않았습니다");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
    	}
    	
 	}
 	
 	/**
 	 * json파일을 열어 파싱하여 현재 선택된 tab에 그린다.
 	 */
 	@FXML
 	private void handleOpen() {
 		FileChooser fileChooser = new FileChooser();

        // 확장자 필터를 설정한다.
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        // Save File Dialog를 보여준다.
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
    			// 마지막 파일 주소를 open한 파일로 바꿔준다.
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
     * 현재 열려 있는 파일에 저장한다.
     * 만일 열려 있는 파일이 없으면 "save as" 다이얼로그를 보여준다.
     *
     */
    // 대시보드 저장
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
     * FileChooser를 열어서 사용자가 저장할 파일을 선택하게 한다.
     */
    @FXML
    private void handleSaveAs() {
        FileChooser fileChooser = new FileChooser();

        // 확장자 필터를 설정한다.
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        // Save File Dialog를 보여준다.
        File file = fileChooser.showSaveDialog(main.getPrimaryStage());

        if (file != null) {
            // 정확한 확장자를 가져야 한다.
            if (!file.getPath().endsWith(".json")) {
                file = new File(file.getPath() + ".json");
            }
            boolean okFlag = main.saveUiDataToFile(file);
            if(okFlag == false)
            	return;
            
            // 데이터베이스에 바로 넣기 확인       
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("");
            alert.setContentText("데이터베이스에 바로 업로드하시겠습니까?");
            Optional<ButtonType> result = alert.showAndWait();

            if(result.get() == ButtonType.OK) {
            	if(!main.isDbConnect) {
        			Alert alertErr = new Alert(AlertType.ERROR);
        			alertErr.setTitle("데이터베이스 연결 오류");
        			alertErr.setHeaderText("Please check Database setting");
        			alertErr.setContentText("데이터베이스가 연결되어 있지 않습니다.");

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
    				// 이미 있는 대시보드 파일 이면 확인하여 update
    				if(main.connect.isDashBoardExist(version, fileName)) {
    					Alert alertCon = new Alert(AlertType.CONFIRMATION);
    					alertCon.setTitle("Confirmation Dialog");
    					alertCon.setHeaderText("이미 있는 대시보드 파일 이름입니다.");
    					alertCon.setContentText("덮어씌우시겠습니까?");
    					
    					Optional<ButtonType> resultCon = alertCon.showAndWait();
    					if(resultCon.get() == ButtonType.OK) {
    						main.connect.updateDashboard(version, fileName, jsonObject, info.isVirtual(), info.getName());
    					}else{
    						Alert a = new Alert(AlertType.INFORMATION);
    			            a.setTitle("cancel");
    			            a.setContentText(fileName+"의 저장이 취소되었습니다.");
    						return;
    					}
    				}else {
    					// 새로운 대시보드 파일이면 insert
    					main.connect.insertDashboard(version, fileName, jsonObject, info.isVirtual(), info.getName());
    				}
    				Alert alertfin = new Alert(AlertType.INFORMATION);
        			alertfin.setTitle("Dadabase에 저장하였습니다.");
        			alertfin.setHeaderText("Dadabase에 저장하였습니다.");
        			alertfin.setContentText("Dadabase에 저장하였습니다.");

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
     *  JSON을 데이터베이스로 전송
     */
    @FXML
    private void handleSend() {
    	boolean sendClicked = showUploadDialog();
    }
    
    // send 다이얼로그를 연다
 	public boolean showUploadDialog() {
 		//main.checkDBConnect();
 		try {
 			// FXML 파일을 로드하고 나서 새로운 스테이지를 만든다.
 			FXMLLoader loader = new FXMLLoader();
 			loader.setLocation(Main.class.getResource("fxml/SendDialog.fxml"));
 			AnchorPane dialog = (AnchorPane) loader.load();
 			
 			// 다이얼로그 스테이지를 만든다.
 			Stage dialogStage = new Stage();
 			if(main.isDbConnect)
 				dialogStage.setTitle("Upload DashBoard");
 			else
 				dialogStage.setTitle("Upload DashBoard - Database not connected");
 			
 			dialogStage.initModality(Modality.WINDOW_MODAL);
 			dialogStage.initOwner(main.getPrimaryStage());
 			Scene scene = new Scene(dialog);
 			dialogStage.setScene(scene);
 			
 			// SendDialog의 컨트롤 설정
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
 			// FXML 파일을 로드하고 나서 새로운 스테이지를 만든다.
 			FXMLLoader loader = new FXMLLoader();
 			loader.setLocation(Main.class.getResource("fxml/LoadDialog.fxml"));
 			AnchorPane dialog = (AnchorPane) loader.load();
 			
 			// 다이얼로그 스테이지를 만든다.
 			Stage dialogStage = new Stage();
 			if(main.isDbConnect)
 				dialogStage.setTitle("Load Dialog");
 			else
 				dialogStage.setTitle("Load Dialog - Database not connected");
 			dialogStage.initModality(Modality.WINDOW_MODAL);
 			dialogStage.initOwner(main.getPrimaryStage());
 			Scene scene = new Scene(dialog);
 			dialogStage.setScene(scene);
 			
 			// LoadDialog의 컨트롤 설정
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
    
    // 차트 수정 다이얼로그 생성
 	public boolean showModifyDialog(DraggableNode node) {
 		//main.checkDBConnect();
 		if(node.getChart().getType().equals("value")) {
 			try {
	 			// FXML 파일을 로드하고 나서 새로운 스테이지를 만든다.
	 			FXMLLoader loader = new FXMLLoader();
	 			loader.setLocation(Main.class.getResource("fxml/ValueTypeDialog.fxml"));
	 			AnchorPane dialog = (AnchorPane) loader.load();
	 			
	 			// 다이얼로그 스테이지를 만든다.
	 			Stage dialogStage = new Stage();
	 			if(main.isDbConnect)
	 				dialogStage.setTitle("Modity Dialog");
	 			else
	 				dialogStage.setTitle("Modity Dialog - Database not connected");
	 			dialogStage.initModality(Modality.WINDOW_MODAL);
	 			dialogStage.initOwner(main.getPrimaryStage());
	 			Scene scene = new Scene(dialog);
	 			dialogStage.setScene(scene);
	 			
	 			// ModifyDialog의 컨트롤 설정
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
	 			// FXML 파일을 로드하고 나서 새로운 스테이지를 만든다.
	 			FXMLLoader loader = new FXMLLoader();
	 			loader.setLocation(Main.class.getResource("fxml/ModifyDialog.fxml"));
	 			AnchorPane dialog = (AnchorPane) loader.load();
	 			
	 			// 다이얼로그 스테이지를 만든다.
	 			Stage dialogStage = new Stage();
	 			if(main.isDbConnect)
	 				dialogStage.setTitle("Modity Dialog");
	 			else
	 				dialogStage.setTitle("Modity Dialog - Database not connected");
	 			dialogStage.initModality(Modality.WINDOW_MODAL);
	 			dialogStage.initOwner(main.getPrimaryStage());
	 			Scene scene = new Scene(dialog);
	 			dialogStage.setScene(scene);
	 			
	 			// ModifyDialog의 컨트롤 설정
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
 	
 	// streamming 수정 다이얼로그 생성
 	public boolean showStreammingDialog(DraggableNode node) {
 		//main.checkDBConnect();
 		try {
 			// FXML 파일을 로드하고 나서 새로운 스테이지를 만든다.
 			FXMLLoader loader = new FXMLLoader();
 			loader.setLocation(Main.class.getResource("fxml/StreammingDialog.fxml"));
 			AnchorPane dialog = (AnchorPane) loader.load();
 			
 			// 다이얼로그 스테이지를 만든다.
 			Stage dialogStage = new Stage();
 			
 			dialogStage.initModality(Modality.WINDOW_MODAL);
 			dialogStage.initOwner(main.getPrimaryStage());
 			Scene scene = new Scene(dialog);
 			dialogStage.setScene(scene);
 			
 			// ModifyDialog의 컨트롤 설정
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
 	 * PC인지 Mobile인지 선택하는 다이얼로그를 연다.
 	 */
	public boolean showSelectVersionDialog(String tabId) {
		//main.getVersion().put(tabId, "PC"); // 초기값 = PC
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
 	 * 데이터베이스 연결 설정창을 연다. 
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
     * 에뮬레이터 다이얼로그를 보여준다.
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
     * 애플리케이션을 닫는다.
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

