package application.view;

import java.util.Vector;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import application.Main;
import application.model.Chart;
import application.model.SystemInfo;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class StreammingDialogController {
	@FXML private Label titleLabel;
	@FXML private TextField streammingTitle;
	@FXML private TextField streammingUrl;
	@FXML private JFXButton apply;
	@FXML private JFXButton cancel;
	@FXML private VBox labelVbox;
	@FXML private VBox textVbox;
	
	private Label publishLabel;
	private TextField publishTextField;
	
	private Main main;
	private Stage dialogStage;
	private Chart chart;
	private boolean applyClicked = false;
	
	private TabPane tabPane = null;
    private Tab tab = null;
	
	public void setMain(Main main) {
		this.main = main;
		tabPane = main.getBoardLayoutController().getTabPane();
		tab = tabPane.getSelectionModel().getSelectedItem();
	}
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	private void makePublishField() {
		publishLabel = new Label("Publish Topic");
		publishTextField = new TextField();
		
		publishTextField.setPrefWidth(460);
		
		labelVbox.getChildren().add(publishLabel);
		textVbox.getChildren().add(publishTextField);
	}
	// 다이얼로그에서 변경될 차트를 선택한다.
	public void setChart(Chart chart) {
		this.chart = chart;
		if(main.isDbConnect) {
			if(chart.getType().equals("camera")) {
				dialogStage.setTitle("Camera Dialog");
				titleLabel.setText("Camera Title");
				makePublishField();
			}else {
				dialogStage.setTitle("Video Dialog");
				titleLabel.setText("Video Title");
				//publishBox.setVisible(false);
			}
		} else {
			if(chart.getType().equals("camera")) {
				dialogStage.setTitle("Camera Dialog - Database isn`t setting");
				titleLabel.setText("Camera Title");
				makePublishField();
			}else {
				dialogStage.setTitle("Video Dialog - Database isn`t setting");
				titleLabel.setText("Video Title");
				//publishBox.setVisible(false);
			}
		}
		
		if(chart.getChartName() != "null")
			streammingTitle.setText(chart.getChartName());
		if(chart.getTopicVector().size() != 0)
			streammingUrl.setText(chart.getTopicVector().get(0));
		if(chart.getCtrTopicVector().size() != 0)
			publishTextField.setText(chart.getCtrTopicVector().get(0));
		
		streammingTitle.requestFocus();
	}
		
	/**
     * 사용자가 Apply를 클릭하면 true를, 그 외에는 false를 반환한다.
     *
     * @return
     */
    public boolean isApplyClicked() {
        return applyClicked;
    }
    @FXML
	private void handleApply() {
		if(inInputValid()) {
			chart.setChartName(streammingTitle.getText());
			
			if(chart.getTopicVector().size() == 0)
				chart.getTopicVector().add(streammingUrl.getText());
			else
				chart.getTopicVector().set(0, streammingUrl.getText());
			
			if(chart.getType().equals("camera")) {
				int size = publishTextField.getText().length();
				if(size != 0) {
					if(chart.getCtrTopicVector().size() == 0)
						chart.getCtrTopicVector().add(publishTextField.getText());
					else
						chart.getCtrTopicVector().set(0, publishTextField.getText());
				}
			}
			
			if(!tab.getText().contains("*"))
    			tab.setText("* "+tab.getText());
			applyClicked = true;
			dialogStage.close();
		}
	}
	
	/**
     * 사용자가 cancel을 클릭하면 호출된다.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
    
    // 사용자의 입력검사
    private boolean inInputValid() {
    	String errorMessage = "";
    	
    	if(streammingTitle.getText() == null || streammingTitle.getText().length() == 0) {
    		errorMessage += "No valid Streamming Title\n";
    	}
    	
    	BoardLayoutController blc = main.getBoardLayoutController();
    	Tab tab = blc.getTabPane().getSelectionModel().getSelectedItem(); 
    	// 차트 이름이 중복이면 생성하지 않는다.
		for (int j = 0; j < main.getDashBoardHashMap().get(tab.getId()).size(); j++) {
			if(streammingTitle.getText().equals(chart.getChartName()) == false && streammingTitle.getText().equals(main.getDashBoardHashMap().get(tab.getId()).get(j).getChartName())) {
				errorMessage += "이름이 중복됩니다.";
			}
		}
    	
    	if(errorMessage.length() == 0) {
    		return true;
    	}else {
    		 // 오류 메시지를 보여준다.
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
    	}
    	
    }
	
}
