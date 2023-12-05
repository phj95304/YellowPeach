package application.view;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import javax.swing.plaf.synth.SynthSplitPaneUI;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;

import application.Main;
import application.util.Connect;
import application.util.MqttSubscriber;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class DatabaseOptionDialogController {
	@FXML private TextField urlTextField;
	@FXML private TextField userTextField;
	@FXML private TextField passwordTextField;
	@FXML private TextField hostTF;
	@FXML private TextField clientIdTf;
	@FXML private JFXButton apply;
	@FXML private JFXButton cancel;
	@FXML private TabPane tabPane;
	
	@FXML private ListView<String> systemListView;
	@FXML private ListView<String> selectedListView;
	@FXML private JFXButton addBt;
	@FXML private JFXButton removeBt;
	
	@FXML private TextArea logTextArea;
	@FXML private Label currentSystemLabel;
	@FXML private Button startBt;
	
	private Stage dialogStage;
	private Main main;
	
	private ObservableList<String> systemList;
    private ObservableList<String> selectedList;
    
    private boolean flag = true;
    private String host = null;
    private String clientId = null;
    
    private String topicSet = null;
    
	@FXML
	private void initialize() {
		FileReader file;
		// json parser
        JSONParser parser = new JSONParser();
        try {        	
        	file = new FileReader("dbData.json");
        	Object obj = parser.parse(file);
			JSONObject jsonObject = (JSONObject) obj;
			urlTextField.setText((String)jsonObject.get("url"));
			userTextField.setText((String)jsonObject.get("user"));
			passwordTextField.setText((String)jsonObject.get("pass"));
			hostTF.setText((String)jsonObject.get("host"));
			clientIdTf.setText((String)jsonObject.get("clientId"));
			host = (String)jsonObject.get("host");
			clientId = (String)jsonObject.get("clientId");
        } catch (Exception e) {
        	e.printStackTrace();
		}
        
        systemList = FXCollections.observableArrayList();
        selectedList = FXCollections.observableArrayList();
	
		
		if(main.isDbConnect) {
			setList();
		}
		systemListView.setItems(systemList);
		selectedListView.setItems(selectedList);
	
	}
	
	private void setList() {
		systemList = FXCollections.observableArrayList();
		selectedList = FXCollections.observableArrayList();
		
		Vector<String> v = main.connect.getRealIoTSystemList();
		for (int i = 0; i < v.size(); i++) {
			systemList.add(v.get(i));
		}
	}
	@FXML
	private void handleAdd() {
		String selected = systemListView.getSelectionModel().getSelectedItem();
		if(selected != null) {
			selectedListView.getSelectionModel().clearSelection();
			systemList.remove(selected);
			selectedList.add(selected);
		}
	}
	
	@FXML
	private void handleRemove() {
		String selected = selectedListView.getSelectionModel().getSelectedItem();
		if(selected != null) {
			systemListView.getSelectionModel().clearSelection();
			selectedList.remove(selected);
			systemList.add(selected);
		}
	}
	
	@FXML
	private void handleStart() {
		if(selectedList.size() == 0)
			return;
		MqttSubscriber ms = new MqttSubscriber(this);
		ms.setHost(host);
		ms.setClientId(clientId);
		System.out.println(host+"\n");
		for (int i = 0; i < selectedList.size(); i++) {
			flag = true;
			if(main.connect.isExistTable(selectedList.get(i))) {
				// 이미 테이블이 존재할 때.
				Alert alertCon = new Alert(AlertType.CONFIRMATION);
				alertCon.setTitle("Confirmation Dialog");
				alertCon.setHeaderText("이미 존재하는 토픽 테이블입니다.");
				alertCon.setContentText("덮어씌우시겠습니까?");
				
				Optional<ButtonType> resultCon = alertCon.showAndWait();
				// 덮어 씌우기
				if(resultCon.get() == ButtonType.OK) {
					
					// sub토픽 가져오기 ==============================================================================
					ms.setSubIoTSystemTopic("subscribe/"+selectedList.get(i));
					ms.run();
					while(flag) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					try {
						ms.getMqttClient().disconnect();
						System.out.println("MQTT Broker Disconnected");
					} catch (MqttException e) {
						e.printStackTrace();
					}
					if(topicSet == null || topicSet.length() == 0) {
						Alert alertin = new Alert(AlertType.INFORMATION);
	        			alertin.setTitle("Information Dialog");
	        			alertin.setHeaderText("There are no topics.");
	        			alertin.setContentText("토픽이 발견되지 않았습니다.");

	        			alertin.showAndWait();
						continue;
					}
					StringTokenizer st = new StringTokenizer(topicSet);
					Vector<String> subTopics = new Vector<String>();
					while(st.hasMoreTokens()) {
						String nt = st.nextToken();
						subTopics.add(nt);
						logTextArea.appendText(nt+"\n");
					}
					
					//pub 토픽 가져오기 =================================================================================
					ms.setSubIoTSystemTopic("publish/"+selectedList.get(i));
					ms.run();
					while(flag) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					try {
						ms.getMqttClient().disconnect();
						System.out.println("MQTT Broker Disconnected");
					} catch (MqttException e) {
						e.printStackTrace();
					}
					if(topicSet == null || topicSet.length() == 0) {
						Alert alertin = new Alert(AlertType.INFORMATION);
	        			alertin.setTitle("Information Dialog");
	        			alertin.setHeaderText("There are no topics.");
	        			alertin.setContentText("토픽이 발견되지 않았습니다.");

	        			alertin.showAndWait();
						continue;
					}
					StringTokenizer stt = new StringTokenizer(topicSet);
					Vector<String> pubTopics = new Vector<String>();
					while(st.hasMoreTokens()) {
						String nt = st.nextToken();
						pubTopics.add(nt);
						logTextArea.appendText(nt+"\n");
					}
					
					// DB에 저장하기 ==============================================================
					main.connect.dropIoTTopicTable(selectedList.get(i));	//일단 테이블 삭제
					main.connect.createIoTTopicTable(selectedList.get(i));	// 다시 새롭게 태이블 생성
					
					if(subTopics.size() >= pubTopics.size()) {
						main.connect.insertTopicToIoTTable(selectedList.get(i), "topic", subTopics);	
						Vector<Integer> asc = main.connect.getAsc(selectedList.get(i));
						main.connect.updateTopic(selectedList.get(i), "ctrTopic", pubTopics, asc);
					}else {	//pubTopics.size() >= subTopics.size()
						main.connect.insertTopicToIoTTable(selectedList.get(i), "ctrTopic", pubTopics);	
						Vector<Integer> asc = main.connect.getAsc(selectedList.get(i));
						main.connect.updateTopic(selectedList.get(i), "topic", subTopics, asc);
					}
				}else {
					continue;
				}
			}else {	// 새로운 테이블 생성과 토픽 삽입
				// sub토픽 가져오기 ==============================================================================
				ms.setSubIoTSystemTopic("subscribe/"+selectedList.get(i));
				ms.run();
				while(flag) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				try {
					ms.getMqttClient().disconnect();
					System.out.println("MQTT Broker Disconnected");
				} catch (MqttException e) {
					e.printStackTrace();
				}
				if(topicSet == null || topicSet.length() == 0) {
					Alert alertin = new Alert(AlertType.INFORMATION);
        			alertin.setTitle("Information Dialog");
        			alertin.setHeaderText("There are no topics.");
        			alertin.setContentText("토픽이 발견되지 않았습니다.");

        			alertin.showAndWait();
					continue;
				}
				StringTokenizer st = new StringTokenizer(topicSet);
				Vector<String> subTopics = new Vector<String>();
				while(st.hasMoreTokens()) {
					String nt = st.nextToken();
					subTopics.add(nt);
					logTextArea.appendText(nt+"\n");
				}
				
				//pub 토픽 가져오기 =================================================================================
				ms.setSubIoTSystemTopic("publish/"+selectedList.get(i));
				ms.run();
				while(flag) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				try {
					ms.getMqttClient().disconnect();
					System.out.println("MQTT Broker Disconnected");
				} catch (MqttException e) {
					e.printStackTrace();
				}
				if(topicSet == null || topicSet.length() == 0) {
					Alert alertin = new Alert(AlertType.INFORMATION);
        			alertin.setTitle("Information Dialog");
        			alertin.setHeaderText("There are no topics.");
        			alertin.setContentText("토픽이 발견되지 않았습니다.");

        			alertin.showAndWait();
					continue;
				}
				StringTokenizer stt = new StringTokenizer(topicSet);
				Vector<String> pubTopics = new Vector<String>();
				while(st.hasMoreTokens()) {
					String nt = st.nextToken();
					pubTopics.add(nt);
					logTextArea.appendText(nt+"\n");
				}
				
				// DB에 저장하기 ==============================================================
				main.connect.createIoTTopicTable(selectedList.get(i));	// 새롭게 태이블 생성
				
				if(subTopics.size() >= pubTopics.size()) {
					main.connect.insertTopicToIoTTable(selectedList.get(i), "topic", subTopics);	
					Vector<Integer> asc = main.connect.getAsc(selectedList.get(i));
					main.connect.updateTopic(selectedList.get(i), "ctrTopic", pubTopics, asc);
				}else {	//pubTopics.size() >= subTopics.size()
					main.connect.insertTopicToIoTTable(selectedList.get(i), "ctrTopic", pubTopics);	
					Vector<Integer> asc = main.connect.getAsc(selectedList.get(i));
					main.connect.updateTopic(selectedList.get(i), "topic", subTopics, asc);
				}
			}
		}
		Alert alertin2 = new Alert(AlertType.INFORMATION);
		alertin2.setTitle("Information Dialog");
		alertin2.setHeaderText("Finish.");
		alertin2.setContentText("완료되었습니다.");

		alertin2.showAndWait();
	}
	
	@FXML
	private void handleApply() {
		if(tabPane.getSelectionModel().getSelectedItem().getId().equals("databaseTab")) {
			if(urlTextField.getText() == null || urlTextField.getText().length() == 0)
				urlTextField.setText(null);
			else 
				main.connect.getPool().set_url(urlTextField.getText());
			
			if(userTextField.getText() == null || userTextField.getText().length() == 0)
				userTextField.setText(null);
			else 
				main.connect.getPool().set_user(userTextField.getText());
			
			if(passwordTextField.getText() == null || passwordTextField.getText().length() == 0)
				passwordTextField.setText(null);
			else
				main.connect.getPool().set_password(passwordTextField.getText());
			
			if(hostTF.getText() == null || hostTF.getText().length() == 0)
				hostTF.setText(null);
			
			if(clientIdTf.getText() == null || clientIdTf.getText().length() == 0)
				clientIdTf.setText(null);
			
			// json파일로 저장
			JSONObject obj = new JSONObject();
			obj.put("url", urlTextField.getText());
			obj.put("user", userTextField.getText());
			obj.put("pass", passwordTextField.getText());
			obj.put("host", hostTF.getText());
			obj.put("clientId", clientIdTf.getText());
			
			try(FileWriter file = new FileWriter("dbData.json")){
				file.write(obj.toJSONString());
				file.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 다시 연결시도해준다
			main.checkDBConnect();
			main.connect.setHost(hostTF.getText());
			main.connect.setClientId(clientIdTf.getText());
		}else {
			
		}
		
		dialogStage.close();
	}
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	
	public void setMain(Main main) {
		this.main = main;
	}
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public String getTopicSet() {
		return topicSet;
	}

	public void setTopicSet(String topicSet) {
		this.topicSet = topicSet;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

}
