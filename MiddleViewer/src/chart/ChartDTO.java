package chart;

public class ChartDTO {
	String json;
	String name; //dashboard 이름 가져오는 부분
	int id; //대시보드 아이디 가져오는 부분
	int available; //삭제 여부 가져오는 부분 default는 1

	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAvailable() {
		return available;
	}
	public void setAvailable(int available) {
		this.available = available;
	}
	
}
