package application.view;

import java.util.Vector;
import java.util.function.Predicate;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import application.Main;
import application.model.Chart;
import application.model.DraggableNode;
import application.model.SystemInfo;
import application.view.ModifyDialogController.SelectedTopicView;
import application.view.ModifyDialogController.TopicView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ValueTypeDialogController {
	@FXML private JFXTextField chartType;
	@FXML private JFXTextField chartName;
	
	@FXML private JFXTextField searchTextField;
	
	@FXML private Label unitLabel;
	@FXML private JFXComboBox<String> unitCombo;
	
	@FXML private JFXTreeTableView<TopicView> topicTable;
	@FXML private JFXTreeTableView<SelectedTopicView> selectedTopicTable;
	@FXML private JFXTreeTableView<TopicView> ctrTopicTable;
	@FXML private JFXTreeTableView<TopicView> selectedCtrTopicTable;
	
	@FXML private Button addSubBt;
	@FXML private Button addPubBt;
	
	@FXML private JFXButton apply;
	@FXML private JFXButton cancle;
	
	@FXML private ImageView arr1;
	@FXML private ImageView arr2;
	
	private Stage dialogStage;
	private Chart chart;
	private boolean applyClicked = false;
	private int seletNum;	// ������ TextField�� �����ϱ� ���� ���� 
	
	private DraggableNode node;
	
	private Main main;
	
	private ObservableList<SelectedTopicView> selectedTopics;
	private ObservableList<TopicView> ctrTopics;
	private ObservableList<TopicView> selectedCtrTopics;
	
	private TabPane tabPane = null;
    private Tab tab = null;
	
	@FXML
	private void initialize() {
		arr1.setImage(new Image("file:resources/images/arrow2.png"));
		arr2.setImage(new Image("file:resources/images/arrow2.png"));
		
		unitCombo.getItems().addAll("��","\u00b0C","��","��/��");
		unitCombo.valueProperty().addListener(new ChangeListener<String>() {
	        @Override public void changed(ObservableValue ov, String oldV, String newV) {
	            chart.setUnit(newV);
	            if(!tab.getText().contains("*"))
	    			tab.setText("* "+tab.getText());
	            
	          }    
	     });
		
		
	}
	
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	public void setMain(Main main) {
		this.main = main;
		tabPane = main.getBoardLayoutController().getTabPane();
		tab = tabPane.getSelectionModel().getSelectedItem();
		
		setTreeTable();
		setCtrTreeTable();
		setSelectedTopicTableView();
		setSelectedCtrTable();
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
		if(chart.getTopicVector().size() !=0) {
			for (int i = 0; i < chart.getTopicVector().size(); i++) {
				String topic = chart.getTopicVector().get(i);
				String topicLabel = chart.getTopicLabelVector().get(i);
				selectedTopics.add(new SelectedTopicView(topic, topicLabel));
			}
		}
		if(chart.getCtrTopicVector().size() != 0) {
			for (int i = 0; i < chart.getCtrTopicVector().size(); i++) {
				String ct = chart.getCtrTopicVector().get(i);
				ctrTopics.add(new TopicView(ct));
			}
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
			}
			for (int i = 0; i < ctrTopics.size(); i++) {
				String ct = ctrTopics.get(i).session1.getValue();
				if(ct == null)
					ct = "null";
				ctrTopicVector.add(ct);
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
    
    @FXML
    private void handleAddPub() {
    	TreeItem<TopicView> selectedItem = topicTable.getSelectionModel().getSelectedItem();
		TopicView tv = selectedItem.getValue();
		ctrTopics.add(new TopicView(tv.session1.getValue()));
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
    
    // subTopic�� ���� TreeTableView ���� 
    private void setTreeTable() {
    	JFXTreeTableColumn<TopicView, String> session1 = new JFXTreeTableColumn<>("Topic");
    	session1.setPrefWidth(380);
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
    
    //pubTopic�� ���� TreeTableView ���� 
    private void setCtrTreeTable() {
    	JFXTreeTableColumn<TopicView, String> session1 = new JFXTreeTableColumn<>("Topic");
    	session1.setPrefWidth(380);
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
    	SystemInfo info = main.tabSystemInfo.get(tab.getId());
    	
    	ctrTopics = FXCollections.observableArrayList();
		Vector<String> topicList = main.connect.getCtrTopicList(info.getName());
		for (int i = 0; i < topicList.size(); i++) {
			ctrTopics.add(new TopicView(topicList.get(i)));
		}
    	//}
    	
    	final TreeItem<TopicView> root = new RecursiveTreeItem<TopicView>(ctrTopics, RecursiveTreeObject::getChildren);
    	ctrTopicTable.getColumns().setAll(session1);
    	ctrTopicTable.setRoot(root);
    	ctrTopicTable.setShowRoot(false);
    	
    	// �˻� â ����
    	searchTextField.textProperty().addListener(new ChangeListener<String>() {
    		@Override
    		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
    			ctrTopicTable.setPredicate(new Predicate<TreeItem<TopicView>>() {
					@Override
					public boolean test(TreeItem<TopicView> topic) {
						Boolean flag =  topic.getValue().session1.getValue().contains(newValue);
						return flag;
					}
				});
    		}
		} );
    	
    	ctrTopicTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if(event.getClickCount() == 2) {
					TreeItem<TopicView> selectedItem = ctrTopicTable.getSelectionModel().getSelectedItem();
					TopicView tv = selectedItem.getValue();
					selectedCtrTopics.add(new TopicView(tv.session1.getValue()));
				}
			};
		});
    }
    
    private void setSelectedTopicTableView() {
		selectedTopicTable.setEditable(true);
		JFXTreeTableColumn<SelectedTopicView, String> topicName = new JFXTreeTableColumn<>("Publish Topic");
		topicName.setPrefWidth(280);
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

		JFXTreeTableColumn<SelectedTopicView, String> label = new JFXTreeTableColumn<>("Label");
		label.setPrefWidth(100);
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
		
		// table���� ����Ŭ�� �̺�Ʈ (����)
		selectedTopicTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if(event.getClickCount() == 2) {
					TreeItem<SelectedTopicView> selectedItem = selectedTopicTable.getSelectionModel().getSelectedItem();
					selectedTopics.remove(selectedItem.getValue());
					addSubBt.setDisable(false);
				}
			};
		});
	}
    
    private void setSelectedCtrTable() {
    	JFXTreeTableColumn<TopicView, String> session1 = new JFXTreeTableColumn<>("Subscribe Topic");
		session1.setPrefWidth(380);
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
		
		selectedCtrTopics = FXCollections.observableArrayList();
		
		// ���� ����Ʈ ����
		final TreeItem<TopicView> root = new RecursiveTreeItem<TopicView>(selectedCtrTopics, RecursiveTreeObject::getChildren);
		selectedCtrTopicTable.getColumns().setAll(session1);
		selectedCtrTopicTable.setRoot(root);
		selectedCtrTopicTable.setShowRoot(false);
		
		// table���� ���õ� topic�� ����
		selectedCtrTopicTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if(event.getClickCount() == 2) {
					TreeItem<TopicView> selectedItem = selectedCtrTopicTable.getSelectionModel().getSelectedItem();
					TopicView tv = selectedItem.getValue();
					selectedCtrTopics.remove(tv);
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
