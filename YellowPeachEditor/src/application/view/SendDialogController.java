package application.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SendDialogController {
	@FXML private ListView<String> jsonList;
	@FXML private Button addDashBoard;
	@FXML private Button send;
	@FXML private Button cancle;
	
	private Main main;
	
    private Stage dialogStage;
    private boolean okClicked = false;
    
    private Vector<File> fileVector;
    private ObservableList<String> fileName = FXCollections.observableArrayList();
    
    // ������
	public SendDialogController() {
		fileVector = new Vector<File>();
	}
	// ���̾�α� �������� ����
	public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
	
	@FXML
	private void initialize() {

	}
	
	public void setMain(Main main) {
		this.main = main;
	}
    
    /**
     * ����ڰ� cancel�� Ŭ���ϸ� ȣ��ȴ�.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
    
	
	   /**
     * ����ڰ� OK�� Ŭ���ϸ� true��, �� �ܿ��� false�� ��ȯ�Ѵ�.
     *
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }
	
	/**
	 * ���� �����ͺ��̽��� json���ϵ��� ������ �޼��� ����
	 */
	@FXML
	private void handleSend() {
		if(!main.isDbConnect) {
			Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("�����ͺ��̽� ���� ����");
            alert.setHeaderText("Please check database setting");
            alert.setContentText("�����ͺ��̽��� ����Ǿ����� �ʽ��ϴ�.");

            alert.showAndWait();
            return;
		} else if(fileVector.size() == 0) {
			Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Alert");
            alert.setHeaderText("���õ� ������ �����ϴ�.");
            alert.setContentText("������ �������ּ���.");
            
            alert.showAndWait();
            return;
		}
		
		// json parser
        JSONParser parser = new JSONParser();
        try {
        	for (int i = 0; i < fileVector.size(); i++) {
        		Object obj = parser.parse(new FileReader(fileVector.get(i).getAbsolutePath()));
    			JSONObject jsonObject = (JSONObject) obj;
    			System.out.println("version" + " : "+(String)jsonObject.get("version"));
    			
    			// ��ú��� ������ ��
    			if((String)jsonObject.get("version") == "PC" || (String)jsonObject.get("version") == "Mobile") {
    				// �̹� �ִ� ��ú��� ���� �̸� Ȯ���Ͽ� update
        			if(main.connect.isDashBoardExist((String)jsonObject.get("version"), fileName.get(i))) {
        				Alert alert = new Alert(AlertType.CONFIRMATION);
        				alert.setTitle("Confirmation Dialog");
        				alert.setHeaderText("�̹� �ִ� ��ú��� ���� �̸��Դϴ�.");
        				alert.setContentText("�����ðڽ��ϱ�?");
        				
        				Optional<ButtonType> result = alert.showAndWait();
        				if(result.get() == ButtonType.OK) {
        					main.connect.updateDashboard((String)jsonObject.get("version"), fileName.get(i), jsonObject, (boolean)jsonObject.get("isVirtual"), (String)jsonObject.get("systemName"));
        				}else{
        					Alert a = new Alert(AlertType.INFORMATION);
    			            a.setTitle("cancel");
    			            a.setContentText(fileName.get(i)+"�� ������ ��ҵǾ����ϴ�.");
        					return;
        				}
        			}
        			else {	// ���ο� ��ú��� �����̸� �ٷ� insert
        				main.connect.insertDashboard((String)jsonObject.get("version"), fileName.get(i), jsonObject, (boolean)jsonObject.get("isVirtual"), (String)jsonObject.get("systemName"));
        			}
    			}
    			else {	// ���ķ����� �ý��� ������ �� / version == virtual
    				String replacefileName = fileName.get(i);
					if(fileName.get(i).contains(" "))
						replacefileName = fileName.get(i).replaceAll(" ", "");
    				if(main.connect.isIoTSystemExist(replacefileName)){		// �̹� �ִ� �ý����϶� 
        				Alert alert = new Alert(AlertType.CONFIRMATION);
        				alert.setTitle("Confirmation Dialog");
        				alert.setHeaderText("�̹� �ִ� �ý������� �̸��Դϴ�.");
        				alert.setContentText("�����ðڽ��ϱ�?");
        				
        				Optional<ButtonType> result = alert.showAndWait();
        				if(result.get() == ButtonType.OK) {
        					main.connect.updateEmulatorSystem(replacefileName, jsonObject);
        					main.connect.updateIoTSystemList(replacefileName, true);
        					main.connect.dropEmulatorTopicTable(replacefileName);
        					// ���ο� ���ķ����� �������̺� ����
            				Vector<String> topics = getSystemTopic(jsonObject);
            				main.connect.createEmulatorTable(replacefileName);
            				main.connect.insertTopicToEmulatorTable(replacefileName, topics);
        					
        				}else{
        					System.out.println("���");
        					Alert a = new Alert(AlertType.INFORMATION);
    			            a.setTitle("cancel");
    			            a.setContentText(fileName.get(i)+"�� ������ ��ҵǾ����ϴ�.");
        					return;
        				}
    				}
    				else {  // ���ο� ����ý����� ��
    					main.connect.insertEmulatorSystem(replacefileName, jsonObject);
    					main.connect.insertIoTSystemList(replacefileName, true);
    					// ���ο� ���ķ����� �������̺� ����
        				Vector<String> topics = getSystemTopic(jsonObject);
        				main.connect.createEmulatorTable(replacefileName);
        				main.connect.insertTopicToEmulatorTable(replacefileName, topics);
    				}
    			}
    			Alert alert = new Alert(AlertType.INFORMATION);
    	        alert.setTitle("Dadabase�� �����Ͽ����ϴ�.");
    	        alert.setHeaderText("Dadabase�� �����Ͽ����ϴ�.");
    	        alert.setContentText("Dadabase�� �����Ͽ����ϴ�.");

    	        alert.showAndWait();
    	        
    	        System.out.println("DB�� ���� �Ǿ����ϴ�.");
    			okClicked = true;
    			dialogStage.close();  
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.INFORMATION);
	        alert.setTitle("Dadabase�� �����������߽��ϴ�.");
	        alert.setHeaderText("Dadabase�� �����������߽��ϴ�.");
	        alert.setContentText("�ٽ� Ȯ�����ּ���");

	        alert.showAndWait();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.INFORMATION);
	        alert.setTitle("Dadabase�� �����������߽��ϴ�.");
	        alert.setHeaderText("Dadabase�� �����������߽��ϴ�.");
	        alert.setContentText("�ٽ� Ȯ�����ּ���");
	        alert.showAndWait();
			return;
		} catch (ParseException e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.INFORMATION);
	        alert.setTitle("Dadabase�� �����������߽��ϴ�.");
	        alert.setHeaderText("Dadabase�� �����������߽��ϴ�.");
	        alert.setContentText("�ٽ� Ȯ�����ּ���");
	        alert.showAndWait();
			return;
		}
        
	}
	
	@FXML
	private void handleAddDashBoard() {
		FileChooser fileChooser = new FileChooser();

        // Ȯ���� ���͸� �����Ѵ�.
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        // Save File Dialog�� �����ش�.
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            fileVector.add(file);
            fileName.add(file.getName().substring(0, file.getName().lastIndexOf('.')));
            jsonList.setItems(fileName);
        }
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
    
}
