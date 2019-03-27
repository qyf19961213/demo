package excel;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class Excel {

	protected Shell shell;
	private Table table;
	public MyUtils myUtils=new MyUtils();
	private boolean flag = false;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Excel window = new Excel();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(1069, 591);
		shell.setText("订单查询");
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 11, SWT.NORMAL));
		lblNewLabel.setText("订单号：");
		lblNewLabel.setBounds(36, 23, 72, 28);
		
		Combo combo = new Combo(shell, SWT.NONE);
		combo.setBounds(126, 23, 144, 28);
		combo.add("WK171229A3");
		
		Button btnNewButton = new Button(shell, SWT.NONE);
		
		btnNewButton.setBounds(808, 23, 126, 38);
		btnNewButton.setText("查询");
		
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(36, 85, 982, 386);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText("ID");
		
		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_1.setWidth(114);
		tblclmnNewColumn_1.setText("模块号");
		
		TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_2.setWidth(150);
		tblclmnNewColumn_2.setText("订单号");
		
		TableColumn tblclmnNewColumn_3 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_3.setWidth(175);
		tblclmnNewColumn_3.setText("Test point");
		
		TableColumn tblclmnNewColumn_4 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_4.setWidth(166);
		tblclmnNewColumn_4.setText("Error");
		
		TableColumn tblclmnNewColumn_5 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_5.setWidth(171);
		tblclmnNewColumn_5.setText("Margin");
		
		TableColumn tblclmnNewColumn_6 = new TableColumn(table, SWT.CENTER);
		tblclmnNewColumn_6.setText("结果");
		tblclmnNewColumn_6.setWidth(100);
		
		Button btnNewButton_1 = new Button(shell, SWT.NONE);
		
		btnNewButton_1.setBounds(733, 487, 144, 47);
		btnNewButton_1.setText("导出Excel表");
		
		Label label = new Label(shell, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 11, SWT.NORMAL));
		label.setBounds(318, 23, 84, 28);
		label.setText("模块号：");
		
		Combo combo_1 = new Combo(shell, SWT.NONE);
		combo_1.setBounds(419, 23, 144, 28);
		combo_1.add("3518040004");
		combo_1.add("3518040036");
		
		//查询
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				table.removeAll();
				String sql = "select id,正向误差 as error,模块号 as models,订单号 as orders,结论 as result from fx1_data where 订单号=? and 模块号=?";
				List<Map<String,String>> list = DBHelper.findAll(sql,combo.getText(),combo_1.getText());
				if (list.isEmpty()) {
					myUtils.alert(shell,"提示信息","订单不存在");
				} else if(list.get(list.size()-1).get("result").equals("不合格")){
					myUtils.alert(shell,"提示信息","您查询的订单不合格");
				}else {
					TableItem ti;
					ti = new TableItem(table, SWT.NONE);
					String[] str = list.get(list.size()-1).get("error").split(",");
					String TestPoint = str[0]+","+str[1];
					String Error = str[4];
					String Margin = str[2]+" ~ "+str[3];
					ti.setText(new String[]{list.get(list.size()-1).get("id"),list.get(list.size()-1).get("models"),list.get(list.size()-1).get("orders"),
							TestPoint,Error,Margin,list.get(list.size()-1).get("result")});
					flag=true;
				}
			}
		});
		
		//导出
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(flag){
					JXLExample jxl = new JXLExample();
					int flag =  jxl.send(table);
					if(flag==1){
						myUtils.alert(shell,"提示信息","导出成功");
					}else{
						myUtils.alert(shell,"提示信息","导出失败");
					}
				}else{
					myUtils.alert(shell,"提示信息","表格为空");
				}
			}
		});
	}
}
