package application.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;

import com.jfoenix.controls.JFXButton;

import application.Main;
import application.model.Chart;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class BoardLayoutController {
	@FXML private AnchorPane rootPane;
	@FXML private BorderPane borderPane;
	
	@FXML private FlowPane menuRootPane;
	@FXML private ScrollPane menuScrollPane;
	@FXML private FlowPane menuFlowPane;
	@FXML private VBox vBox;
	
	@FXML private ImageView chartIV;
	@FXML private ImageView mediaIV;
	@FXML private ImageView widgetIV;

	@FXML private VBox firstSubVBox;
	@FXML private VBox firstSubMenuList;
	@FXML private FlowPane firstMenu;
	@FXML private ImageView barIV;
	//@FXML private ImageView histogramIV;
	@FXML private ImageView lineIV;
	@FXML private ImageView gaugeIV;
	@FXML private ImageView doughnutIV;
	@FXML private ImageView pieIV;

	@FXML private VBox secondSubVBox;
	@FXML private VBox secondSubMenuList;
	@FXML private FlowPane secondMenu;
	@FXML private ImageView valueIV;
	@FXML private ImageView imageIV;
	@FXML private ImageView textIV;
	
	@FXML private VBox thirdSubVBox;
	@FXML private VBox thirdSubMenuList;
	@FXML private FlowPane thirdMenu;
	@FXML private ImageView videoIV;
	@FXML private ImageView cameraIV;
	
	@FXML private HBox menuLabelBox;

	@FXML private TabPane tabPane;
	@FXML private BorderPane menuBorder1;
	@FXML private BorderPane menuBorder2;
	@FXML private BorderPane menuBorder3;
	@FXML private BorderPane menuBorder4;
	@FXML private BorderPane menuBorder5;
	@FXML private BorderPane menuBorder6;
	@FXML private BorderPane menuBorder7;
	@FXML private BorderPane menuBorder8;
	@FXML private BorderPane menuBorder9;
	@FXML private BorderPane menuBorder10;
	
	private Map<VBox, VBox> map = new HashMap<VBox, VBox>();
	private Vector<ImageView> imageVector;
	private Vector<BorderPane> borderPaneVector;
	private Vector<FlowPane> flowPaneVector;
	
	private Main main;
	private String typeId;

	public void setSize(double primaryWidth, double primaryHeight) {
		menuRootPane.setPrefSize(primaryWidth*0.15, primaryHeight*0.92);
		menuRootPane.setMaxSize(menuRootPane.getPrefWidth(), menuRootPane.getPrefHeight());
		menuRootPane.setMinSize(menuRootPane.getPrefWidth(), 600);
		
		menuScrollPane.setPrefSize(menuRootPane.getPrefWidth(), menuRootPane.getPrefHeight());
		menuScrollPane.setMaxSize(menuScrollPane.getPrefWidth(), menuScrollPane.getPrefHeight());
		menuScrollPane.setMinSize(menuScrollPane.getPrefWidth(), 600);
		menuScrollPane.setStyle("-fx-background-color:transparent;");
		
		menuFlowPane.setPrefWidth(menuScrollPane.getPrefWidth()-3);
		menuFlowPane.setStyle("-fx-background-color:white;");
		
		menuLabelBox.setPrefSize(menuFlowPane.getPrefWidth()-20, 33);
		menuLabelBox.setMinSize(menuLabelBox.getPrefWidth(), menuLabelBox.getPrefHeight());
		menuLabelBox.setMaxSize(menuLabelBox.getPrefWidth(), menuLabelBox.getPrefHeight());

		vBox.setPrefSize(menuFlowPane.getPrefWidth()-20, menuScrollPane.getPrefHeight()-43);
		vBox.setMaxSize(vBox.getPrefWidth(), vBox.getPrefHeight());
		vBox.setMinSize(vBox.getPrefWidth(), 600);
		
		vBox.setStyle("-fx-background-color:#22364e;");
		
		for (int i = 0; i < imageVector.size(); i++) {
			//imageVector.get(i).setFitWidth(vBox.getPrefWidth()*0.40);
			imageVector.get(i).setFitWidth(menuRootPane.getPrefWidth()*0.35);
			imageVector.get(i).setFitHeight(70);
		}
		for (int i = 0; i < borderPaneVector.size(); i++) {
			borderPaneVector.get(i).setPrefSize(menuRootPane.getPrefWidth()*0.35, 70);
			borderPaneVector.get(i).setMaxSize(menuRootPane.getPrefWidth()*0.35, 70);
			borderPaneVector.get(i).setMinSize(menuRootPane.getPrefWidth()*0.35, 70);
		}
	}
	
	@FXML
	private void initialize() {
		imageVector = new Vector<ImageView>();
		flowPaneVector = new Vector<FlowPane>();
		borderPaneVector = new Vector<BorderPane>();
		addMenusToMap();
		addImageViewEvent();
		addButtonEvent();
		
		// setComponentsSize();
		
		firstMenu.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				toolsSlider(firstSubVBox, firstSubMenuList);
				removeOtherMenus(firstSubVBox);
			}
		});
		secondMenu.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				toolsSlider(secondSubVBox, secondSubMenuList);
				removeOtherMenus(secondSubVBox);
			}
		});
		thirdMenu.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				toolsSlider(thirdSubVBox, thirdSubMenuList);
				removeOtherMenus(thirdSubVBox);
			}
		});
		
	}
	/**
	 * Add Menus to map
	 */
	public void addMenusToMap() {
		addMenusToMapImpl();
	}

	private void addMenusToMapImpl() {

		map.put(firstSubVBox, firstSubMenuList);
		map.put(secondSubVBox, secondSubMenuList);
		map.put(thirdSubVBox, thirdSubMenuList);

		/**
		 * Remove the components from VBox on load of stage
		 */
		for (Map.Entry<VBox, VBox> entry : map.entrySet()) {
			entry.getKey().getChildren().remove(entry.getValue());
		}
	}


	/**
	 * Menu slider
	 * 
	 * @param menu
	 * @param subMenu
	 */
	public void toolsSlider(VBox menu, VBox subMenu) {
		toolsSliderImpl(menu, subMenu);
	}

	private void toolsSliderImpl(VBox menu, VBox subMenu) {
		if (menu.getChildren().contains(subMenu)) {
			final FadeTransition transition = new FadeTransition(Duration.millis(500), menu);
			transition.setFromValue(0.5);
			transition.setToValue(1);
			transition.setInterpolator(Interpolator.EASE_IN);
			menu.getChildren().remove(subMenu);
			transition.play();
		} else {
			final FadeTransition transition = new FadeTransition(Duration.millis(500), menu);
			transition.setFromValue(0.5);
			transition.setToValue(1);
			transition.setInterpolator(Interpolator.EASE_IN);
			menu.getChildren().add(subMenu);
			transition.play();
		}
	}

	/**
	 * Remove other menus
	 * 
	 * @param menu
	 */
	public void removeOtherMenus(VBox menu) {
		//removeOtherMenusImpl(menu);
	}

	private void removeOtherMenusImpl(VBox menu) {
		for (Map.Entry<VBox, VBox> entry : map.entrySet()) {
			if (!entry.getKey().equals(menu))
				entry.getKey().getChildren().remove(entry.getValue());
		}
	}
	
	public void addImageViewEvent() {
		chartIV.setImage(new Image("file:resources/images/chart.png"));
		mediaIV.setImage(new Image("file:resources/images/media.png"));
		widgetIV.setImage(new Image("file:resources/images/widget.png"));
		
		barIV.setImage(new Image("file:resources/images/Bar.png"));
		//histogramIV.setImage(new Image("file:resources/images/Histogram.png"));
		lineIV.setImage(new Image("file:resources/images/Line.png"));
		gaugeIV.setImage(new Image("file:resources/images/Gauge.png"));
		doughnutIV.setImage(new Image("file:resources/images/Doughnut.png"));
		pieIV.setImage(new Image("file:resources/images/Pie.png"));
		
		valueIV.setImage(new Image("file:resources/images/Value.png"));
		imageIV.setImage(new Image("file:resources/images/peach.png"));
		textIV.setImage(new Image("file:resources/images/text.png"));
		
		videoIV.setImage(new Image("file:resources/images/streamming.png"));
		cameraIV.setImage(new Image("file:resources/images/Swiss.jpg"));
		
		// 이미지들을 벡터에 넣어준다
		imageVector.add(barIV);
		//imageVector.add(histogramIV);
		imageVector.add(lineIV);
		imageVector.add(gaugeIV);
		imageVector.add(doughnutIV);
		imageVector.add(pieIV);
		
		imageVector.add(valueIV);
		imageVector.add(imageIV);
		imageVector.add(textIV);
		
		imageVector.add(videoIV);
		imageVector.add(cameraIV);
		
		
		for (int i = 0; i < imageVector.size(); i++) {
			addDraggableNodeEvent(imageVector.get(i));
			//Tooltip.install(imageVector.get(i), new Tooltip(""+imageVector.get(i).getFitHeight()));
		}
		borderPaneVector.add(menuBorder1);
		borderPaneVector.add(menuBorder2);
		borderPaneVector.add(menuBorder3);
		borderPaneVector.add(menuBorder4);
		borderPaneVector.add(menuBorder5);
		borderPaneVector.add(menuBorder6);
		borderPaneVector.add(menuBorder7);
		borderPaneVector.add(menuBorder8);
		borderPaneVector.add(menuBorder9);
		borderPaneVector.add(menuBorder10);
	}
	
	
	private void addButtonEvent() {
		flowPaneVector.add(firstMenu);
		flowPaneVector.add(secondMenu);
		flowPaneVector.add(thirdMenu);
		
		for (int i = 0; i < flowPaneVector.size(); i++) {
			addMouseCursor(flowPaneVector.get(i));
		}
	}
	
	
	// 차트 노드 이벤트들
	public void addDraggableNodeEvent(ImageView imageView) {
		imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(getTabPane().getSelectionModel().getSelectedItem() == null) {
					// 경고 메시지를 보여준다.
		            Alert alert = new Alert(AlertType.CONFIRMATION);
		            alert.setTitle("Confirmation Dialog");
		            alert.setHeaderText("Tab does not exist.");
		            alert.setContentText("Do you want to create a new tab?");
		            Optional<ButtonType> resultCon = alert.showAndWait();
					if(resultCon.get() == ButtonType.OK) {
						boolean flag = main.getRootLayoutController().handleNew();
						if(flag == false)
							return;
					}else{
						return;
					}
				}
				setTypeId(imageView.getId());
				
				Chart chart = new Chart(typeId);
				Tab tab = getTabPane().getSelectionModel().getSelectedItem();
				AnchorPane a = (AnchorPane)tab.getContent();
	            ScrollPane s = (ScrollPane)a.getChildren().get(0);
	            FlowPane fp = (FlowPane)s.getContent();
	            
	            if(main.getTabSystemInfo().get(tab.getId()).getVersion().equals("PC")) {
		            // 차트를 hashMap에 저장한다
		            main.getDashBoardHashMap().get(tab.getId()).add(chart);
					main.getRootLayoutController().createNode(fp, tab, chart);		            	
	            }else {
	            	FlowPane backFp = (FlowPane)fp.getChildren().get(0);
	            	// 차트를 hashMap에 저장한다
		            main.getDashBoardHashMap().get(tab.getId()).add(chart);
					main.getRootLayoutController().createNode(backFp, tab, chart);
	            }
			}
		});
		imageView.setOnMouseEntered(new EventHandler() {
			@Override
			public void handle(Event event) {
				main.getScene().setCursor(Cursor.HAND);
			}
		});
		imageView.setOnMouseExited(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				main.getScene().setCursor(Cursor.DEFAULT);
			}
		});
	}
	// 커서 모양 바꾸는 이벤트리스너
	public void addMouseCursor(FlowPane fp) {
		fp.setOnMouseEntered(new EventHandler() {
			@Override
			public void handle(Event event) {
				main.getScene().setCursor(Cursor.HAND);
			}
		});
		fp.setOnMouseExited(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				main.getScene().setCursor(Cursor.DEFAULT);
			}
		});
	}

	public FlowPane getMenuRootPane() {
		return menuRootPane;
	}
	public BorderPane getBorderPane() {
		return borderPane;
	}
	public TabPane getTabPane() {
		return tabPane;
	}	
	
	// 선택된 탭의 id를 넘겨주는 메서드
	public String getTabId() {
		return tabPane.getSelectionModel().getSelectedItem().getId();
	}
	
	
	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public void setMain(Main main) {
		this.main = main;
	}
}
