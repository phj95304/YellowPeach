package application.view;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Vector;
import java.util.prefs.Preferences;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import application.Main;
import application.model.VirtualNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class EmulatorController {
	@FXML private TextField nameTextField;
	@FXML private TextField typeTextField;
	@FXML private TextField topicTextField;
	@FXML private TextField minTextField;
	@FXML private TextField maxTextField;
	@FXML private TextArea memoTextArea;
	@FXML private VBox vbox;
	@FXML private Button applyButton;
	@FXML private Button openButton;
	@FXML private Button saveButton;
	@FXML private Button cancelButton;
	@FXML private FlowPane sensorField;
	
	private Stage dialogStage;
	private Main main;
	private File beforeFile;
	private Vector<VirtualNode> emulatorVector;

	public EmulatorController() {
		emulatorVector = new Vector<VirtualNode>();
	}
	public void setMain(Main main) {
		this.main = main;
	}
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
    /**
     * ����ڰ� Cancel�� Ŭ���ϸ� ȣ��ȴ�.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
    
    /**
     * ����ڰ� Apply�� Ŭ���ϸ� ȣ��ȴ�.
     */
    @FXML
    private void handleApply() {
    	if(inInputValid()) {
    		//nameTextField, typeTextField, topicTextField, minTextField, maxTextField, memoTextArea, sensorField, vbox
    		VirtualNode newVNode = new VirtualNode(this);
    		sensorField.getChildren().add(newVNode);
    		emulatorVector.add(newVNode);
    		resetTextField();
    	}
    }
    
    /**
     * ����ڰ� Save�� Ŭ���ϸ� ȣ��ȴ�.
     */
    @FXML
    private void handleSave() {
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
            
            // �ý��� ���� ���� �Լ� ȣ��
            saveEmDataToFile(file);
            
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
    				
    				String fileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
					if(fileName.contains(" "))
						fileName = fileName.replaceAll(" ", "");
    				// �̹� �ִ� ���ķ����� ���� �̸� Ȯ���Ͽ� update
    				if(main.connect.isIoTSystemExist(fileName)) {
    					Alert alertCon = new Alert(AlertType.CONFIRMATION);
    					alertCon.setTitle("Confirmation Dialog");
    					alertCon.setHeaderText("�̹� �ִ� ���� �̸��Դϴ�.");
    					alertCon.setContentText("�����ðڽ��ϱ�?");
    					
    					Optional<ButtonType> resultCon = alertCon.showAndWait();
    					if(resultCon.get() == ButtonType.OK) {
    						main.connect.updateEmulatorSystem(fileName, jsonObject);
    						main.connect.updateIoTSystemList(fileName, true);
    						main.connect.dropEmulatorTopicTable(fileName);
    						// ���ο� ���ķ����� �������̺� ����
            				Vector<String> topics = getSystemTopic(jsonObject);
            				main.connect.createEmulatorTable(fileName);
            				main.connect.insertTopicToEmulatorTable(fileName, topics);
    					}else{
    						System.out.println("���");
        					Alert a = new Alert(AlertType.INFORMATION);
    			            a.setTitle("cancel");
    			            a.setContentText(fileName+"�� ������ ��ҵǾ����ϴ�.");
    			            a.showAndWait();
    						return;
    					}
    				}else {
    					// ���ο� ���ķ����� �����̸� insert
    					main.connect.insertEmulatorSystem(fileName, jsonObject);
    					// IoT�ý��� ����Ʈ�� �߰�
        				main.connect.insertIoTSystemList(fileName, true);
        				// ���ο� ���ķ����� �������̺� ����
        				Vector<String> topics = getSystemTopic(jsonObject);
        				main.connect.createEmulatorTable(fileName);
        				main.connect.insertTopicToEmulatorTable(fileName, topics);
    				}
        			Alert alertfin = new Alert(AlertType.INFORMATION);
        			alertfin.setTitle("Dadabase�� �����Ͽ����ϴ�.");
        			alertfin.setHeaderText("Dadabase�� �����Ͽ����ϴ�.");
        			alertfin.setContentText("Dadabase�� �����Ͽ����ϴ�.");

        			alertfin.showAndWait();
        	        
        	        // ������ �ʱ�ȭ
        	        resetTextField();
        	        emulatorVector.removeAllElements();
        	        sensorField.getChildren().clear();
    			} catch (FileNotFoundException e) {
    				e.printStackTrace();
    			} catch (IOException e) {
    				e.printStackTrace();
    			} catch (ParseException e) {
    				e.printStackTrace();
    			}
            }
            Alert a = new Alert(AlertType.INFORMATION);
            a.setTitle("Information");
            a.setContentText("��������ҿ� ������ �Ϸ�Ǿ����ϴ�.");
            resetTextField();
            emulatorVector.removeAllElements();
            sensorField.getChildren().clear();
        }
    }
    
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
        	// json parser
            JSONParser parser = new JSONParser();
            try {
            	Object obj = parser.parse(new FileReader(file.getAbsolutePath()));
    			JSONObject jsonObject = (JSONObject) obj;
    			JSONArray jsonArray = (JSONArray) jsonObject.get("content");
				JSONObject one = (JSONObject)jsonArray.get(0);
				System.out.println(one);
				Set keys = one.keySet();
				Iterator it = keys.iterator(); 
				while(it.hasNext()){
					JSONObject o = (JSONObject)one.get(it.next());
					setPage(o.get("name").toString(), o.get("type").toString(), o.get("topic").toString(),
							o.get("min").toString(), o.get("max").toString(), o.get("memo").toString());
	    	        handleApply();
	    	    	resetTextField();
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
    
    public void saveEmDataToFile(File file) {
	    try {
	    	System.out.println("save DashBoard");

	    	// json���� ��ȯ�ϴ� �Լ� ȣ��
			toEmulatorJson(emulatorVector, file);
	        	        
	    } catch (Exception e) { // ���ܸ� ��´�.
	        Alert alert = new Alert(AlertType.ERROR);
	        alert.setTitle("Error");
	        alert.setHeaderText("Could not save data");
	        alert.setContentText("Could not save data to file:\n" + file.getPath());

	        alert.showAndWait();
	    }
	}
    
    // Emulator�ý����� json���Ϸ� ��ȯ
    private void toEmulatorJson(Vector<VirtualNode> emulatorList, File file) {
    	JSONObject obj = new JSONObject();
    	
    	// insert "emulatorSystemName":"system1",
    	int idx = file.getName().indexOf(".");
    	String systemName = file.getName().substring(0, idx);
    	obj.put("systemName", systemName);
    	obj.put("version", "virtual");
    	obj.put("size", emulatorList.size());
    	
    	// insert "content":[]
    	JSONArray jsonArray = new JSONArray();
    	Map map = new LinkedHashMap(emulatorList.size());
    	for (int i = 0; i < emulatorList.size(); i++) {
    		JSONObject conObj = new JSONObject();
    		
    		conObj.put("topic", emulatorList.get(i).getTopic());
    		conObj.put("name", emulatorList.get(i).getName());
    		conObj.put("type", emulatorList.get(i).getType());
    		conObj.put("min", emulatorList.get(i).getMin());
    		conObj.put("max", emulatorList.get(i).getMax());
    		conObj.put("memo", emulatorList.get(i).getMemo());
    		
    		map.put(i, conObj);
		}
    	jsonArray.add(map);
    	obj.put("content", jsonArray);
    	System.out.println(obj);

    	//String fileName = "json/".concat(dashBoardName).concat(".json");
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
    
    // ������ �ý����� ���� ����
    private Vector<String> getSystemTopic(JSONObject obj) {
    	Vector<String> topics = new Vector<String>();
    	JSONArray jsonArray = (JSONArray) obj.get("content");
		JSONObject one = (JSONObject)jsonArray.get(0);
		System.out.println(one);
		Set keys = one.keySet();
		Iterator it = keys.iterator(); 
		while(it.hasNext()){
			JSONObject o = (JSONObject)one.get(it.next());
			String topic = o.get("topic").toString(); 
			topics.add(topic);
		}
    	return topics;
    }
    
    // ������� �Է°˻�
    @FXML
    private boolean inInputValid() {
    	String errorMessage = "";
    	
    	if(nameTextField.getText() == null || nameTextField.getText().length() == 0)
    		errorMessage += "No Valid Sensor Name\n";
    	if(topicTextField.getText() == null || topicTextField.getText().length() == 0)
    		errorMessage += "No Valid Topic\n";
    	if(minTextField.getText() == null || minTextField.getText().length() == 0)
    		errorMessage += "No Valid Min\n";
    	if(maxTextField.getText() == null || maxTextField.getText().length() == 0)
    		errorMessage += "No Valid Max\n";
    	
    	ObservableList<Node> workingCollection = FXCollections.observableArrayList(
    			sensorField.getChildren()
         );
    	
    	for(int i = 0; i < workingCollection.size(); i++) {
    		VirtualNode vn = (VirtualNode)workingCollection.get(i);
    		if(vn.getTopic().equals(topicTextField.getText()))
    			errorMessage += "The Topic cannot be a duplicate";
    		i++;
    	}
    	
    	if(!(isStringDouble(minTextField.getText()) == true && isStringDouble(maxTextField.getText()) == true))
    		errorMessage += "Please enter a correct value (Min or Max)\n";
    	else {
    		if(Double.parseDouble(minTextField.getText()) > Double.parseDouble(maxTextField.getText())) 
        		errorMessage += "Min value bigger than Max value\n";
    	}
    		
    	if(errorMessage.length() == 0)
    		return true;
    	else {
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
    
    public void resetTextField() {
        nameTextField.setText("");
        typeTextField.setText("");
        topicTextField.setText("");
        minTextField.setText("");
        maxTextField.setText("");
        memoTextArea.setText("");
    }
    public void setPage(String name, String type, String topic, String min, String max, String memo) {
    	nameTextField.setText(name);
        typeTextField.setText(type);
        topicTextField.setText(topic);
        minTextField.setText(min);
        maxTextField.setText(max);
        memoTextArea.setText(memo);
    }
    public boolean isStringDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public boolean isStringInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // getter, setter
	public TextField getNameTextField() {
		return nameTextField;
	}
	public void setNameTextField(TextField nameTextField) {
		this.nameTextField = nameTextField;
	}
	public TextField getTypeTextField() {
		return typeTextField;
	}
	public void setTypeTextField(TextField typeTextField) {
		this.typeTextField = typeTextField;
	}
	public TextField getTopicTextField() {
		return topicTextField;
	}
	public void setTopicTextField(TextField topicTextField) {
		this.topicTextField = topicTextField;
	}
	public TextField getMinTextField() {
		return minTextField;
	}
	public void setMinTextField(TextField minTextField) {
		this.minTextField = minTextField;
	}
	public TextField getMaxTextField() {
		return maxTextField;
	}
	public void setMaxTextField(TextField maxTextField) {
		this.maxTextField = maxTextField;
	}
	public TextArea getMemoTextArea() {
		return memoTextArea;
	}
	public void setMemoTextArea(TextArea memoTextArea) {
		this.memoTextArea = memoTextArea;
	}
	public FlowPane getSensorField() {
		return sensorField;
	}
	public void setSensorField(FlowPane sensorField) {
		this.sensorField = sensorField;
	}
	public VBox getVbox() {
		return vbox;
	}
	public void setVbox(VBox vbox) {
		this.vbox = vbox;
	}
	public Vector<VirtualNode> getEmulatorVector() {
		return emulatorVector;
	}
	public void setEmulatorVector(Vector<VirtualNode> emulatorVector) {
		this.emulatorVector = emulatorVector;
	}
    
    
}
