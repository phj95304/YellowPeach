package chart;

public class HistoryDTO {

	private String sDatetime;
	private String sData;
	private String topicName;

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getsDatetime() {
		return sDatetime;
	}

	public void setsDatetime(String sDatetime) {
		this.sDatetime = sDatetime;
	}

	public String getsData() {
		return sData;
	}

	public void setsData(String sData) {
		this.sData = sData;
	}

}