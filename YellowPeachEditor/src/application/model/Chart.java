package application.model;

import java.util.Vector;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Chart {
	private String type;
	
	private String chartName;
	
	private Vector<String> topicVector;
	private Vector<String> topicLabelVector;
	private Vector<String> ctrTopicVector;
	
	private String unit;
	
	private int row;
	private int col;
	private int number;
	private int level;
	
	public Chart(String type) {
		this.type = type;
		this.chartName = "null";
		this.row = 0;
		this.col = 0;
		this.unit = "null";
		//this.publishTopic = "null";
		topicVector = new Vector<String>();
		topicLabelVector = new Vector<String>();
		ctrTopicVector = new Vector<String>();
	}

	public Vector<String> getTopicVector() {
		return topicVector;
	}

	public void setTopicVector(Vector<String> topicVector) {
		this.topicVector = topicVector;
	}

	public Vector<String> getTopicLabelVector() {
		return topicLabelVector;
	}

	public void setTopicLabelVector(Vector<String> topicLabelVector) {
		this.topicLabelVector = topicLabelVector;
	}

	public Vector<String> getCtrTopicVector() {
		return ctrTopicVector;
	}

	public void setCtrTopicVector(Vector<String> ctrTopicVector) {
		this.ctrTopicVector = ctrTopicVector;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getChartName() {
		return chartName;
	}

	public void setChartName(String chartName) {
		this.chartName = chartName;
	}

	/*
	public String getPublishTopic() {
		return publishTopic;
	}

	public void setPublishTopic(String publishTopic) {
		this.publishTopic = publishTopic;
	}
	*/

	public int getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}



}
