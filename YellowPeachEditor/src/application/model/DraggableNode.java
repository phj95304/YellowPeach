package application.model;

import java.io.IOException;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import application.Main;
import application.view.BoardLayoutController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

public class DraggableNode extends BorderPane{
	@FXML private BorderPane rootPane;
	@FXML private GridPane gridPane;
	@FXML private JFXTextField title;
	@FXML private Label sensor;
	@FXML private Label close;
	@FXML private ImageView imageView;
	
	private TextArea textArea;
	
	private Main main;
	private BoardLayoutController boardLayoutController;
	
	private final DraggableNode self;
	
	private Chart chart;
	
	public DraggableNode(BoardLayoutController boardLayoutController) {
		// fxml파일 연동
		FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/DraggableNode.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		this.boardLayoutController = boardLayoutController;
		
		self = this;
		
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}
	
	@FXML
	private void initialize() {
		buildEvent();
		//imageViewEvent();
	}
	
	private void buildEvent() {
		close.setOnMouseClicked( new EventHandler <MouseEvent> () {
			@Override
			public void handle(MouseEvent event) {
				Tab tab = boardLayoutController.getTabPane().getSelectionModel().getSelectedItem();
				main.getDashBoardHashMap().get(tab.getId()).remove(chart);
				FlowPane tabFlowPane = (FlowPane)main.getScene().lookup("#tabFlowPane"+tab.getId());
				
				// tabFlowPane에 있는 자식 노드 리스트
				ObservableList<Node> workingCollection = FXCollections.observableArrayList(
						tabFlowPane.getChildren()
	             );
				
				int nodeIndex = 0;
				for (int i = 0; i < workingCollection.size(); i++) {
					DraggableNode deleteNode = (DraggableNode)workingCollection.get(i);
					if(deleteNode.getId() == self.getId()) {
						nodeIndex = i;
						break;
					}
				}
				System.out.println("delete "+chart.getType()+" : "+nodeIndex);
				
				tabFlowPane.getChildren().remove(self);
				if(!tab.getText().contains("*"))
	    			tab.setText("* "+tab.getText());
			}
		});
		close.setOnMouseEntered(new EventHandler() {
			@Override
			public void handle(Event event) {
				main.getScene().setCursor(Cursor.HAND);
			}
		});
		close.setOnMouseExited(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				main.getScene().setCursor(Cursor.DEFAULT);
			}
		});
		
		title.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				chart.setChartName(newValue);
			}
		});
		title.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(title.getText().equals("Chart Title"))
					title.setText("");
			}
		});
	}
	
	public BorderPane getRootPane() {
		return rootPane;
	}

	public void setRootPane(BorderPane rootPane) {
		this.rootPane = rootPane;
	}

	public ImageView getImageView() {
		return imageView;
	}

	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
		this.imageView.setImage(imageView.getImage());
	}


	public TextArea getTextArea() {
		return textArea;
	}

	public void setTextArea(TextArea textArea) {
		this.textArea = textArea;
	}

	public void setMain(Main main) {
        this.main = main;
    }

	public Chart getChart() {
		return chart;
	}

	public void setChart(Chart chart) {
		this.chart = chart;
	}

	

	public JFXTextField getTitle() {
		return title;
	}

	public void setTitle(JFXTextField title) {
		this.title = title;
	}

	public Label getSensor() {
		return sensor;
	}

	public void setSensor(Label sensor) {
		this.sensor = sensor;
	}

	public GridPane getGridPane() {
		return gridPane;
	}

	public void setGridPane(GridPane gridPane) {
		this.gridPane = gridPane;
	}
	
	
}
