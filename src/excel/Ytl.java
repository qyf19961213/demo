package excel;

public class Ytl {
	private int id;
	private String models;
	private String orders;
	private String error;
	private String result;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getModels() {
		return models;
	}
	public void setModels(String models) {
		this.models = models;
	}
	public String getOrders() {
		return orders;
	}
	public void setOrders(String orders) {
		this.orders = orders;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	@Override
	public String toString() {
		return "Ytl [id=" + id + ", models=" + models + ", orders=" + orders + ", error=" + error + ", result=" + result
				+ "]";
	}
	
}
