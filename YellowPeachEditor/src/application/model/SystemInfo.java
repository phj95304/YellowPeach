package application.model;

public class SystemInfo {
	String name;
	boolean isVirtual;
	String version;
	public SystemInfo() {}
	public SystemInfo(String name, boolean isVirtual, String version) {
		this.name = name;
		this.isVirtual = isVirtual;
		this.version = version;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isVirtual() {
		return isVirtual;
	}
	public void setVirtual(boolean isVirtual) {
		this.isVirtual = isVirtual;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
}
