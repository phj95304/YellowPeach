package application.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import application.Main;
import application.model.SystemInfo;
import application.view.ModifyDialogController.TopicView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class SelectVersionDialogController {
	@FXML private JFXTreeTableView<SystemView> listTable;
	@FXML private RadioButton pcBt;
	@FXML private RadioButton mobileBt;
	@FXML private Button applyBt;
	@FXML private Button cancelBt;
	@FXML private Button getSystemBt;
	
	private HashMap<String, Boolean> systemMap;
	private String tabId;
	private Main main;
	private Stage dialogStage;
	
	private String name;
	private boolean isVirtual;
	private String version;
	private Vector<String> topicList;
	private ObservableList<SystemView> systems;
	
	@FXML
	private void initialize() {
		listTable.setPlaceholder(new Label("System file not found."));
		final ToggleGroup group = new ToggleGroup();
		pcBt.setToggleGroup(group);
		pcBt.setSelected(true);
		version = "PC";
		mobileBt.setToggleGroup(group);
		setTreeTable();
	}
	
	// TreeTableView 설정
	public void setTreeTable() {
		JFXTreeTableColumn<SystemView, String> session1 = new JFXTreeTableColumn<>("System File");
    	//session1.setPrefWidth(150);
		session1.prefWidthProperty().bind(listTable.widthProperty().divide(2));
    	session1.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<SystemView, String>, ObservableValue<String>>(){
    		@Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<SystemView, String> param) {
    			if(session1.validateValue(param)) {
            		return param.getValue().getValue().session1;
            	}else {
            		return session1.getComputedValue(param);
            	}
    		}
    	});
    	
    	JFXTreeTableColumn<SystemView, Boolean> session2 = new JFXTreeTableColumn<>("Virtual");
    	session2.setPrefWidth(150);
    	session2.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<SystemView, Boolean>, ObservableValue<Boolean>>(){
    		@Override
            public ObservableValue<Boolean> call(TreeTableColumn.CellDataFeatures<SystemView, Boolean> param) {
    			if(session2.validateValue(param)) {
            		return param.getValue().getValue().session2;
            	}else {
            		return session2.getComputedValue(param);
            	}
    		}
    	});
    	
    	systems = FXCollections.observableArrayList();
    	if(main.isDbConnect) {
    		systemMap = main.connect.getIotSystemList();
    		
    		Iterator<String> iterator = systemMap.keySet().iterator();
    		while (iterator.hasNext()) {
    			String key = (String) iterator.next();
				systems.add(new SystemView(key, systemMap.get(key)));
			}
    	}
    	
    	final TreeItem<SystemView> root = new RecursiveTreeItem<SystemView>(systems, RecursiveTreeObject::getChildren);
    	listTable.getColumns().setAll(session1, session2);
    	listTable.setRoot(root);
    	listTable.setShowRoot(false);
    	
    	// un grup,grup 설정
    	JFXButton groupButton = new JFXButton("Group");
        groupButton.setOnAction((action) -> new Thread(() -> listTable.group(session1)).start());

        JFXButton unGroupButton = new JFXButton("unGroup");
        unGroupButton.setOnAction((action) -> listTable.unGroup(session1));
        
        // table에서 선택된 system 가져오기
        listTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<SystemView>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<SystemView>> observable, TreeItem<SystemView> old_val, TreeItem<SystemView> new_val) {
            	TreeItem<SystemView> selectedItem = new_val;
            	
            	name = selectedItem.getValue().session1.getValue();
            	isVirtual = selectedItem.getValue().session2.getValue();
            	
            	if(name != null && version != null) {
        			applyBt.setDisable(false);
        		}
            }

        });
	}
	
	class SystemView extends RecursiveTreeObject<SystemView> {
        StringProperty session1;
        BooleanProperty session2;

        public SystemView(String session1, Boolean session2) {
        	this.session1 = new SimpleStringProperty(session1);
        	this.session2 = new SimpleBooleanProperty(session2);
        }
    }
	
	
	@FXML
	private void handleGetSystemFile() {
		FileChooser fileChooser = new FileChooser();

        // 확장자 필터를 설정한다.
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(null);
        
        if (file != null) {
        	JSONParser parser = new JSONParser();
            try {
            	Object obj = parser.parse(new FileReader(file.getAbsolutePath()));
    			JSONObject jsonObject = (JSONObject) obj;
    			String name = (String)jsonObject.get("systemName");
    			SystemView getSystem = new SystemView(name, true);
    			systems.add(getSystem);
    			JSONArray jsonArray = (JSONArray)jsonObject.get("content");
    			int i = 0;
				Map m = (Map)jsonArray.get(0);
				
				while(m.get(Integer.toString(i)) != null) {
					JSONObject conObj = (JSONObject)m.get(Integer.toString(i));
					String s = (String)conObj.get("topic");
					topicList.add(s);
					i++;
					//System.out.println(s);
				}
            } catch (FileNotFoundException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		} catch (ParseException e) {
    			e.printStackTrace();
    		}
        }
	}
	
	@FXML
	private void handleApply() {
		SystemInfo info = new SystemInfo(name, isVirtual, version);
		main.getTabSystemInfo().put(tabId, info);
		
		dialogStage.close();
	}
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}
	@FXML
	private void handlePcButton() {
		version = "PC";
		if(name != null && version != null) {
			applyBt.setDisable(false);
		}
	}
	@FXML
	private void handleMobileButton() {
		version = "Mobile";
		if(name != null && version != null) {
			applyBt.setDisable(false);
		}
	}
	
	
	public String getTabId() {
		return tabId;
	}
	public void setTabId(String tabId) {
		this.tabId = tabId;
	}
	public void setMain(Main main) {
		this.main = main;
	}
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public Vector<String> getTopicList() {
		return topicList;
	}

	public void setTopicList(Vector<String> topicList) {
		this.topicList = topicList;
	}

}
