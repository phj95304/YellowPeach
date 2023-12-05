package application.view;

import java.awt.event.MouseAdapter;
import java.util.HashMap;
import java.util.Vector;
import java.util.function.Predicate;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.jfoenix.controls.RecursiveTreeItem;

import application.Main;
import application.model.Chart;
import application.model.DraggableNode;
import application.util.Connect;
import application.view.ModifyDialogController.SelectedTopicView;
import application.view.ModifyDialogController.TopicView;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;


public class ModifyDialogController {
	@FXML private JFXTextField chartType;
	@FXML private JFXTextField chartName;
	
	@FXML private JFXTextField firstSensor;
	@FXML private JFXTextField firstSensorLabel;
	@FXML private JFXTextField secondSensor;
	@FXML private JFXTextField secondSensorLabel;
	@FXML private JFXTextField searchTextField;
	
	@FXML private Label unitLabel;
	@FXML private JFXComboBox<String> unitCombo;
	
	@FXML private JFXTreeTableView<TopicView> topicTable;
	@FXML private JFXTreeTableView<SelectedTopicView> selectedTopicTable;
	
	@FXML private JFXButton apply;
	@FXML private JFXButton cancle;
	
	private Stage dialogStage;
	private Chart chart;
	private boolean applyClicked = false;
	private int seletNum;	// ������ TextField�� �����ϱ� ���� ���� 
	
	private DraggableNode node;
	
	private Main main;
	
	private ObservableList<SelectedTopicView> selectedTopics;
	
	private TabPane tabPane = null;
    private Tab tab = null;
	
	@FXML
	private void initialize() {
		unitCombo.getItems().addAll("��","\u00b0C","��","��/��");
		unitCombo.valueProperty().addListener(new ChangeListener<String>() {
	        @Override public void changed(ObservableValue ov, String oldV, String newV) {
	            chart.setUnit(newV);
	            if(!tab.getText().contains("*"))
	    			tab.setText("* "+tab.getText());
	            
	          }    
	     });
		setSelectedTopicTableView();
	}
	public ModifyDialogController() {
		searchTextField = firstSensor;
	}
	
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	public void setMain(Main main) {
		this.main = main;
		tabPane = main.getBoardLayoutController().getTabPane();
		tab = tabPane.getSelectionModel().getSelectedItem();
		setTreeTable();
	}
	public void setDraggableNode(DraggableNode node) {
		this.node = node;
	}
	
	
	// ���̾�α׿��� ����� ��Ʈ�� �����Ѵ�.
	public void setChart(Chart chart) {
		this.chart = chart;
		
		chartType.setText(chart.getType());
		if(!chart.getType().equals("value")) {
			unitLabel.setDisable(true);
			unitCombo.setDisable(true);
		}else {		// value�� �� 
			if(!chart.getUnit().equals("null")) 
				unitCombo.setPromptText(chart.getUnit());
		}
		
		if(!chart.getChartName().equals("null"))
			chartName.setText(chart.getChartName());
		
		Vector<String> topicV = new Vector<String>();
		Vector<String> topicLabelV = new Vector<String>();
		if(chart.getTopicVector().size() != 0) {
			for (int i = 0; i < chart.getTopicVector().size(); i++) {
				String topic = chart.getTopicVector().get(i);
				topicV.add(topic);
			}
		}
		if(chart.getTopicLabelVector().size() != 0) {
			for (int j = 0; j < chart.getTopicLabelVector().size(); j++) {
				String topicLabel = chart.getTopicLabelVector().get(j);
				topicLabelV.add(topicLabel);
			}
		}
		/*
		if(topicV.size() > topicLabelV.size()) {
			for (int i = topicLabelV.size(); i < topicV.size(); i++) {
				topicLabelV.add(i, "null");
			}
		}*/
		for (int j = 0; j < topicV.size(); j++) {
			selectedTopics.add(new SelectedTopicView(topicV.get(j), topicLabelV.get(j)));	
		}
		
			
		chartName.requestFocus();
	}
	
	
	
	
    /**
     * ����ڰ� cancel�� Ŭ���ϸ� ȣ��ȴ�.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
	
    /**
     * ����ڰ� apply�� Ŭ���ϸ� true��, �� �ܿ��� false�� ��ȯ�Ѵ�.
     *
     * @return
     */
    public boolean isApplyClicked() {
        return applyClicked;
    }
	
    // ����ڰ� apply��ư�� ������ ȣ��.
    @FXML
    private void handleApply() {
    	if(inInputValid()) {    			
    		chart.setChartName(chartName.getText());

    		Vector<String> topicVector = new Vector<String>();
			Vector<String> topicLabel = new Vector<String>();
			Vector<String> ctrTopicVector = new Vector<String>();
			for (int i = 0; i < selectedTopics.size(); i++) {
				String topic = selectedTopics.get(i).topicName.getValue();
				String label = selectedTopics.get(i).label.getValue();
				if(label == null || label.equals("Write a Label"))
					label = "null";
				topicVector.add(topic);
				topicLabel.add(label);
				ctrTopicVector.add("null");
			}
			
			chart.setTopicVector(topicVector);
			chart.setTopicLabelVector(topicLabel);
			chart.setCtrTopicVector(ctrTopicVector);
			
    		if(!tab.getText().contains("*"))
    			tab.setText("* "+tab.getText());
    		applyClicked = true;
    		dialogStage.close();
    	}
    }
    
    // ������� �Է°˻�
    @FXML
    private boolean inInputValid() {
    	String errorMessage = "";
    	
    	if(chartName.getText() == null || chartName.getText().length() == 0 || chartName.getText().equals("null")) {
    		errorMessage += "No valid Chart Name\n";
    	}
 
    	BoardLayoutController blc = main.getBoardLayoutController();
    	Tab tab = blc.getTabPane().getSelectionModel().getSelectedItem(); 
    	// ��Ʈ �̸��� �ߺ��̸� �������� �ʴ´�.
		for (int j = 0; j < main.getDashBoardHashMap().get(tab.getId()).size(); j++) {
			if(chartName.getText().equals(chart.getChartName()) == false && chartName.getText().equals(main.getDashBoardHashMap().get(tab.getId()).get(j).getChartName())) {
				errorMessage += "��Ʈ �̸��� �ߺ��˴ϴ�.";
			}
		}
    	
    	if(errorMessage.length() == 0) {
    		return true;
    	}else {
    		 // ���� �޽����� �����ش�.
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
    	}
    	
    }
    
    @FXML
    public void clickFirstTextFiled() {
    	seletNum = 0;
    }
    @FXML
    public void clickSecondTextFiled() {
    	seletNum = 1;
    }
    
    // Topic�� ���� TreeTableView ���� 
    public void setTreeTable() {
    	JFXTreeTableColumn<TopicView, String> session1 = new JFXTreeTableColumn<>("Topic");
    	session1.setPrefWidth(780);
    	//session1.prefWidthProperty().bind(topicTable.widthProperty().divide(1));
    	session1.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TopicView, String>, ObservableValue<String>>(){
    		@Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<TopicView, String> param) {
    			if(session1.validateValue(param)) {
            		return param.getValue().getValue().session1;
            	}else {
            		return session1.getComputedValue(param);
            	}
    		}
    	});
    	
    	String[] colors = {"#D9E5FF","#E4F7BA"};
    	// ���� ����Ʈ ����
    	ObservableList<TopicView> topics = FXCollections.observableArrayList();
    	//if(main.isDbConnect) {
		Vector<String> topicList = main.tabTopicList.get(tab.getId());
		for (int i = 0; i < topicList.size(); i++) {
    		topics.add(new TopicView(topicList.get(i)));
		}
    	//}
    	
    	final TreeItem<TopicView> root = new RecursiveTreeItem<TopicView>(topics, RecursiveTreeObject::getChildren);
    	topicTable.getColumns().setAll(session1);
    	topicTable.setRoot(root);
    	topicTable.setShowRoot(false);
    	
    	// �˻� â ����
    	searchTextField.textProperty().addListener(new ChangeListener<String>() {
    		@Override
    		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
    			topicTable.setPredicate(new Predicate<TreeItem<TopicView>>() {
					@Override
					public boolean test(TreeItem<TopicView> topic) {
						Boolean flag =  topic.getValue().session1.getValue().contains(newValue);
						return flag;
					}
				});
    		}
		} );
    	// table���� ���õ� topic�� selectTable�� ��������
		topicTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if(event.getClickCount() == 2) {
					TreeItem<TopicView> selectedItem = topicTable.getSelectionModel().getSelectedItem();
					TopicView tv = selectedItem.getValue();
					selectedTopics.add(new SelectedTopicView(tv.session1.getValue(), "Write a Label"));
				}
			};
		});
        
    }
    
    private void setSelectedTopicTableView() {
		selectedTopicTable.setEditable(true);
		JFXTreeTableColumn<SelectedTopicView, String> topicName = new JFXTreeTableColumn<>("TopicName");
		topicName.setPrefWidth(390);
		topicName.setEditable(false);
		topicName.setCellValueFactory(
				new Callback<TreeTableColumn.CellDataFeatures<SelectedTopicView, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(
							TreeTableColumn.CellDataFeatures<SelectedTopicView, String> param) {
						if (topicName.validateValue(param)) {
							return param.getValue().getValue().topicName;
						} else {
							return topicName.getComputedValue(param);
						}
					}
				});
		/*
		// topicName column�� ������ �� �ְ� �Ѵ�.
		topicName.setCellFactory(new Callback<TreeTableColumn<SelectedTopicView,String>, TreeTableCell<SelectedTopicView,String>>() {
			@Override
			public TreeTableCell<SelectedTopicView, String> call(TreeTableColumn<SelectedTopicView, String> param) {
				return new TextFieldTreeTableCell<>();
			}
		});
		topicName.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		topicName.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<SelectedTopicView,String>>() {
			@Override
			public void handle(CellEditEvent<SelectedTopicView, String> event) {
				TreeItem<SelectedTopicView> current = selectedTopicTable.getTreeItem(event.getTreeTablePosition().getRow());
				current.getValue().setTopicName(event.getNewValue());
			}
		});
		*/
		JFXTreeTableColumn<SelectedTopicView, String> label = new JFXTreeTableColumn<>("Label");
		label.setPrefWidth(390);
		label.setEditable(true);
		label.setCellValueFactory(
				new Callback<TreeTableColumn.CellDataFeatures<SelectedTopicView, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(
							TreeTableColumn.CellDataFeatures<SelectedTopicView, String> param) {
						if (label.validateValue(param)) {
							return param.getValue().getValue().label;
						} else {
							return label.getComputedValue(param);
						}
					}
				});
		// Label column�� ������ �� �ְ� �Ѵ�.
		label.setCellFactory(new Callback<TreeTableColumn<SelectedTopicView,String>, TreeTableCell<SelectedTopicView,String>>() {
			@Override
			public TreeTableCell<SelectedTopicView, String> call(TreeTableColumn<SelectedTopicView, String> param) {
				return new TextFieldTreeTableCell<>();
			}
		});
		label.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
		label.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<SelectedTopicView,String>>() {
			@Override
			public void handle(CellEditEvent<SelectedTopicView, String> event) {
				TreeItem<SelectedTopicView> current = selectedTopicTable.getTreeItem(event.getTreeTablePosition().getRow());
				current.getValue().setLabel(event.getNewValue());
			}
		});
		
		selectedTopics = FXCollections.observableArrayList();

		final TreeItem<SelectedTopicView> root = new RecursiveTreeItem<SelectedTopicView>(selectedTopics, RecursiveTreeObject::getChildren);
		selectedTopicTable.getColumns().setAll(topicName, label);
		selectedTopicTable.setRoot(root);
		selectedTopicTable.setShowRoot(false);

		// un grup,grup ����
		JFXButton groupButton = new JFXButton("Group");
		groupButton.setOnAction((action) -> new Thread(() -> selectedTopicTable.group(label)).start());

		JFXButton unGroupButton = new JFXButton("unGroup");
		unGroupButton.setOnAction((action) -> selectedTopicTable.unGroup(label));
		
		/*
		topicName.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if(event.getClickCount() == 2) {
					TreeItem<SelectedTopicView> selectedItem = selectedTopicTable.getSelectionModel().getSelectedItem();
					selectedTopics.remove(selectedItem.getValue());
				}
			};
		});
		label.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if(event.getClickCount() == 2) {
					TreeItem<SelectedTopicView> selectedItem = selectedTopicTable.getSelectionModel().getSelectedItem();
					selectedTopics.remove(selectedItem.getValue());
				}
			};
		});
		*/
		
		// table���� ����Ŭ�� �̺�Ʈ
		selectedTopicTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if(event.getClickCount() == 2) {
					TreeItem<SelectedTopicView> selectedItem = selectedTopicTable.getSelectionModel().getSelectedItem();
					selectedTopics.remove(selectedItem.getValue());
				}
			};
		});
		
		
	}
    
    
    class TopicView extends RecursiveTreeObject<TopicView> {
        StringProperty session1;

        public TopicView(String session1) {
        	this.session1 = new SimpleStringProperty(session1);
        }
    }
    
    // ������ �丯 ����Ʈ ��
 	class SelectedTopicView extends RecursiveTreeObject<SelectedTopicView> {
 		StringProperty topicName;
 		StringProperty label;

 		public SelectedTopicView(String topicName, String label) {
 			this.topicName = new SimpleStringProperty(topicName);
 			this.label = new SimpleStringProperty(label);
 		}

 		public StringProperty getTopicName() {
 			return topicName;
 		}
 		public void setTopicName(String topicName) {
 			this.topicName = new SimpleStringProperty(topicName);
 		}
 		public StringProperty getLabel() {
 			return label;
 		}
 		public void setLabel(String label) {
 			this.label = new SimpleStringProperty(label);
 		}
 	}
}
