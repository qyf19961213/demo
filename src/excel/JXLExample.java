package excel;
 
import java.io.File;
import java.io.FileOutputStream;   
import java.io.OutputStream;   
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.swt.widgets.Table;
import jxl.*;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.DateFormat;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;   
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;   
    public class JXLExample {   
   
    /** 
      * 数据库导出至Excel表格 
    */ 
     public Integer send(Table table) {   
        // 准备设置excel工作表的标题   
    	String[] title1 = {"Model："+table.getItem(0).getText(1)};
    	String[] title2 = {"Seria number："+table.getItem(0).getText(2)};
        String[] title = {"Test point","Error(%)","Margin(%)"};   
        try {   
        	JFileChooser jf=new JFileChooser("d:/");
    		FileNameExtensionFilter filter = new FileNameExtensionFilter(
    				".xls", "jpg", "gif");
    				//设置文件类型
    		jf.setFileFilter(filter);
    		int value=jf.showSaveDialog(null);
    		File getPath = null;
    		if(value==JFileChooser.APPROVE_OPTION){ //判断窗口是否点的是打开或保存

    		getPath=jf.getSelectedFile(); //取得路径
    		//用流在将你的数据输出
    		}else{
    		// 没有选择，即点了窗口的取消
    		 //点了取消后有要做些什么
    		}
             // 获得开始时间   
             long start = System.currentTimeMillis();   
            // 输出的excel的路径   
             String filePath = getPath.getAbsolutePath()+jf.getFileFilter().getDescription();   
             // 创建Excel工作薄   
             WritableWorkbook wwb;   
            // 新建立一个jxl文件,即在e盘下生成testJXL.xls   
             OutputStream os = new FileOutputStream(filePath);   
             wwb=Workbook.createWorkbook(os);    
           // 添加第一个工作表并设置第一个Sheet的名字   
           WritableSheet sheet = wwb.createSheet("Quality Report", 0);   
             Label label;   
             label = new Label(0,0,title1[0]);   
             sheet.addCell(label); 
             label = new Label(0,1,title2[0]);   
             sheet.addCell(label); 
             for(int i=0;i<title.length;i++){   
                 // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z   
               // 在Label对象的子对象中指明单元格的位置和内容   
                label = new Label(i,3,title[i],getDataCellFormat(CellType.LABEL));   
                // 将定义好的单元格添加到工作表中   
               sheet.addCell(label);   
            }   
            // 下面是填充数据   
             /*   
              * 保存数字到单元格，需要使用jxl.write.Number 
              * 必须使用其完整路径，否则会出现错误 
              * */ 
             for(int i=0;i<title.length;i++){   
                 // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z   
               // 在Label对象的子对象中指明单元格的位置和内容   
                label = new Label(i,4,table.getItem(0).getText(i+3),getDataCellFormat(CellType.LABEL));   
                // 将定义好的单元格添加到工作表中   
               sheet.addCell(label);   
            }      
            // 写入数据   
            wwb.write();   
             // 关闭文件   
            wwb.close(); 
            os.close();
             long end = System.currentTimeMillis();   
            System.out.println("----完成该操作共用的时间是:"+(end-start)/1000);   
            return 1; 
         } catch (Exception e) {   
             System.out.println("---出现异常---");
             return 0;   
         }  
     }   
     public static WritableCellFormat getDataCellFormat(CellType type) {
 		WritableCellFormat wcf = null;
 		try {
 			// 字体样式
 			if (type == CellType.NUMBER || type == CellType.NUMBER_FORMULA) {// 数字
 				NumberFormat nf = new NumberFormat("#.00");
 				wcf = new WritableCellFormat(nf);
 			} else if (type == CellType.DATE) {// 日期
 				DateFormat df = new DateFormat("yyyy-MM-dd hh:mm:ss");
 				wcf = new WritableCellFormat(df);
 			} else {
 				WritableFont wf = new WritableFont(WritableFont.TIMES, 10,
 						WritableFont.NO_BOLD, false);
 				// 字体颜色
 				wf.setColour(Colour.RED);
 				wcf = new WritableCellFormat(wf);
 			}
 			// 对齐方式
 			wcf.setAlignment(Alignment.CENTRE);
 			wcf.setVerticalAlignment(VerticalAlignment.CENTRE);
 			// 设置上边框
 			wcf.setBorder(Border.TOP, BorderLineStyle.THIN);
 			// 设置下边框
 			wcf.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
 			// 设置左边框
 			wcf.setBorder(Border.LEFT, BorderLineStyle.THIN);
 			// 设置右边框
 			wcf.setBorder(Border.RIGHT, BorderLineStyle.THIN);
 			// 设置背景色
 			wcf.setBackground(Colour.WHITE);
 			// 自动换行
 			wcf.setWrap(true);
 		} catch (WriteException e) {
 			e.printStackTrace();
 		}
 		return wcf;
 	}
  
} 
 