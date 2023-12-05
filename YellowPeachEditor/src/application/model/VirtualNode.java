package application.model;

import java.io.IOException;

import application.Main;
import application.view.EmulatorController;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class VirtualNode extends VBox{
	@FXML private Button close;
	@FXML private Label nameLabel;
	@FXML private Label typeLabel;
	@FXML private Label topicLabel;
	@FXML private Label minLabel;
	@FXML private Label maxLabel;
	
	private TextField nameTextField;
	private TextField typeTextField;
	private TextField topicTextField;
	private TextField minTextField;
	private TextField maxTextField;
	private TextArea memoTextArea;
	private FlowPane sensorField;
	
	private EmulatorController emCtr;

	private String name;
	private String type;
	private String topic;
	private double min;
	private double max;
	private String memo;
	
	private final VirtualNode self;
	
	public VirtualNode(EmulatorController emCtr) {
		this.emCtr = emCtr; 
		
		// fxml파일 연동
		FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/VirtualNode.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		
		this.nameTextField = emCtr.getNameTextField();
		this.typeTextField = emCtr.getTypeTextField();
		this.topicTextField = emCtr.getTopicTextField();
		this.minTextField = emCtr.getMinTextField();
		this.maxTextField = emCtr.getMaxTextField();
		this.memoTextArea = emCtr.getMemoTextArea();
		this.sensorField = emCtr.getSensorField();
		
		name = nameTextField.getText();
		type = typeTextField.getText();
		topic = topicTextField.getText();
		min = Double.parseDouble(minTextField.getText());
		max = Double.parseDouble(maxTextField.getText());
		memo = memoTextArea.getText();
		
		self = this;
		
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
		
		nameLabel.setText(name);
		typeLabel.setText(type);
		topicLabel.setText(topic);
		minLabel.setText(Double.toString(min));
		maxLabel.setText(Double.toString(max));
		
		//System.out.println(name+", "+type+", "+topic+", "+min+", "+max+", "+memo);
	}
	
	@FXML
	private void initialize() {
		
	}

	
	@FXML
    private void handleVBox() {
		nameTextField.setText(name);
		typeTextField.setText(type);
		topicTextField.setText(topic);
		minTextField.setText(Double.toString(min));
		maxTextField.setText(Double.toString(max));
		memoTextArea.setText(memo);
	}
	
	
	@FXML
    private void handleCancel() {
		emCtr.getEmulatorVector().remove(self);
		sensorField.getChildren().remove(self);
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public double getMin() {
		return min;
	}
	public void setMin(double min) {
		this.min = min;
	}
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
		this.max = max;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
}