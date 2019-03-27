package excel;

public class split {
	public static void main(String[] args) {
		String str = "wqe,ach,dh";
		//数据
		String[] str1 = str.split(",");
		for(String str2: str1){
		System.out.println(str2);
		}
	}
}
