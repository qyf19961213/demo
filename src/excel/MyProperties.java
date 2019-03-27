package excel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MyProperties extends Properties{
	private static MyProperties myProperties;
	private MyProperties(){
		try {
			InputStream iis=MyProperties.class.getClassLoader()
					.getResourceAsStream("db.properties");
				this.load(iis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	public static MyProperties getInstance(){
		if( myProperties==null ){
			myProperties=new MyProperties();
		}
		return myProperties;
	}
}
