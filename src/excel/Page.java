package excel;

import java.util.List;

public class Page<T> {
     private long total;
     
     private List<T> rows;

     
	public Page(List<T> rows, long total) {
		this.total = total;
		this.rows = rows;
	}

	public long getTotal() {
		return total;
	}
  
	public void setTotal(long total) {
		this.total = total;
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}
}
