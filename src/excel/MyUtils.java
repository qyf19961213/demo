package excel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class MyUtils {
	public void alert(Shell shell,String title,String message){
		MessageBox mb=new MessageBox(shell,SWT.NONE);
		mb.setText(title);
		mb.setMessage(message);
		mb.open();
	}
}
