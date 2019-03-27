package excel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;





//eclipse 消失  黯然失色  漆黑
public class DBHelper{
	
	/*开始封装   jdbc的原生写法太麻烦  要写好多次 而且   还容易出错
	 * 
	 * 思路：  
	 * 		加载驱动，绝对有   而且不变的的  而且呢  我们想要程序一运行就加载  并且贯穿整个程序
	 * 
	 * 
	 * 
	 * */
	/*static {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	//获取连接   有返回值   每一个里面都要有
	/*public static Connection getCon(){
		Connection con=null;
		try {
			 con=DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:orcl","scott","a");
			Context ctx = new InitialContext();
			// 查找指定的命名服务 java:comp/env/ + 你的服务名
			// 返回的对象就是数据源：引用转型，强制类型转换
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/mysql/bbs");
			// 获取连接
			con = ds.getConnection();
		} catch (SQLException | NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return con;
	}*/
	static{
		try {
			Class.forName(MyProperties.getInstance().getProperty("driverClass"));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static Connection getCon(){
		Connection con=null;
		try {
			con=DriverManager.getConnection(MyProperties.getInstance().getProperty("url"),
					MyProperties.getInstance());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return con;
	}
	//开始封装功能
	//增删改  返回值都一样
	public static int doinsert(String sql,Object...params) throws SQLException{
		Connection con = getCon();
		PreparedStatement pstm = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		System.out.println("SQL：" + sql);
		doParams(pstm,params);
		pstm.executeUpdate();
		ResultSet rs = pstm.getGeneratedKeys();
		rs.next();
		int key = rs.getInt(1);
		return key;
	
	}
	public static int doUpdate(String sql,Object...params){
		//定义返回值
		int result=-1;
		Connection con=getCon();
		//获取连接
		try {
			//预处理
			PreparedStatement pstm=con.prepareStatement(sql);
			System.out.println("SQL：" + sql);
			//设置参数   多个地方使用同一个功能  再封装 
			doParams(pstm,params);
			//ִ执行sql 
			result = pstm.executeUpdate();
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			//关闭连接
			closeAll(null, null, null, con);
		}
		//
		return result;
		
	}
	
	
	//查询
	//问题  返回类型      参数
	// 可变参数数组 Object...params
	public static List<Map<String,String>> findAll(String sql, Object...params ){
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		//这些跟前面都是一样的
		try{
			Connection con=getCon();
			PreparedStatement pstm=con.prepareStatement(sql);
			System.out.println("SQL：" + sql);
			doParams(pstm,params);
			ResultSet rs=pstm.executeQuery();
			//
			ResultSetMetaData rsmd=rs.getMetaData();
			//刚才看到，列名是以数组的形式存在的
			String[] columnName=new String[rsmd.getColumnCount()  ];
			for(int i=0;i<columnName.length ;i++){
				// 存列名                                        注意：这里不是从0开始的  而是从1开始的
				columnName[i]=rsmd.getColumnName(i+1);
			}
			
			while(rs.next() ){
				Map<String,String> map=new HashMap<String,String>();
				//根据列名来获取值
				for(int i=0;i<columnName.length ;i++){
					
					String cn=columnName[i];
					//获取值ֵ
					String value=rs.getString(cn);
					//键有了  值也有了  存map中
					map.put(cn, value);
				}
				//一个next遍历完毕   意味着一个map存完
				list.add(map);
			}
			closeAll(pstm,null,rs,con);
			
			//到了这一步  就开始不一样了
			//分析：根据原生的jdbc  最终是通过get方法，对应键  获取值  -> Map<String,String>
			//我们不可能只有一条数据  很多数据  ->List
			//融合  List<Map<String,String>>
			//返回类型确定
			//现在的难点  如何确定这个键  换句话说  如何确定一个表里面所有的列
		}catch(SQLException e){
			e.printStackTrace();
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	private static void doParams(PreparedStatement pstm, Object...params) {
		try {
			int i=1;
			for(Object o:params){
				if(o!=null&&o instanceof Collection){
					for(Object p:(Collection<Object>) o){
						System.out.println("参数"+i+":"+p);
						pstm.setObject(i++, p);
					}
				}else if(o!=null && o.getClass().isArray()){
					for(Object p:(Object[]) o){
						System.out.println("参数"+i+":"+p);
						pstm.setObject(i++, p);
					}
				}else{
					System.out.println("参数"+i+":"+o);
					pstm.setObject(i++, o);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}	
	
	//查询  基于对象的查询
	//问题 ？  对象确定不了   泛型<T>
	//返回类型？  list对象
	//参数？  sql  params  查询的对象
	// 参数：sql sql语句 
	//    params  注入的参数
	//    c  用户传进来的对象
	
	
	public static <T> List<T> find(String sql, Class<T> c, Object...params){
		List<T> list = new ArrayList<T>();
		Connection con=getCon();
		PreparedStatement pstm = null;
		//查询得到的结果
		ResultSet rs=null;
		
		try {
			pstm = con.prepareStatement(sql);
			System.out.println("SQL：" + sql);
			doParams(pstm,params);
			rs = pstm.executeQuery();
			
			//拿到元数据，取出一个列名
			//System.out.println(rs);
			ResultSetMetaData rsmd=rs.getMetaData();
			//存到一个数据里面去
			//System.out.println(rsmd);
			String[] columnName=new String[rsmd.getColumnCount() ];
			for(int i=0;i<columnName.length;i++){
				//取值存值                                                     注意 +1；
				columnName[i] = rsmd.getColumnName(i + 1);
				//System.out.println(rsmd.getColumnName(1  ));
			}
			//到这里开始   就不一样了
			//思考  上面的方法  到了这一步   开始存值了
			//现在是对象  对象存值用的是set方法          setUsid  setUname  setPhoto
			//现在我们知道  有哪些方法 但其实   我们是不知道的   所以 我们就可以同过传进来的对象  来获取它里面的所有方法
			
			Method[] ms=c.getMethods();
			//System.out.println(ms);
			
			//定义变量
			T t;
			String mname;   //方法名
			String cname;  //列名字
			String ctypename;  //类型名
			
			
			while(rs.next() ){
				//第一步，得到一个对象实例
				
					t=(T)c.newInstance();   //  UserInfo t=(UserInfo)c.newInstance();
				
				//循环取名字，得到方法名
				for(int i=0;i<columnName.length ;i++){
					cname=columnName[i];    
					//开始转换  usid  ->   setUid()   setUname setPhoto
					cname="set"+cname.substring(0, 1).toUpperCase()+cname.substring(1).toLowerCase();
					//System.out.println(cname);
					//容错处理
					if(ms!=null && ms.length>0){
						for(Method m : ms){
							//开始比较
							mname=m.getName();
							//在找到方法名的同时 还得保证 它的值不为空
							if(cname.equals(mname)  &&  rs.getObject(columnName[i] )!=null ){
								//触发set方法  问题  不能直接调用  必须反向激活
								//m.invoke(t,rs.getString(columnName[i]) );
								//不能直接这么写  我们还得判断数据类型
								/**
								 * 获取 set 方法的参数类型
								 */
								ctypename = m.getParameters()[0].getType().getName();
								/**
								 * 屏蔽
								 * ctypename=rs.getObject(columnName[i] ).getClass().getName();
								 */
								//System.out.println(ctypename);
								//不能直接这么写  我们还得判断数据类型
								/**
								 * 改动
								 */
								if("java.lang.Integer".equals(ctypename) ){
									m.invoke(t,rs.getInt(columnName[i]) );
								}else if("java.lang.String".equals(ctypename) ){
									m.invoke(t,rs.getString(columnName[i]) );
								}else if("java.lang.Long".equals(ctypename)){
									m.invoke(t,rs.getLong(columnName[i]) );
								}else if("java.lang.Byte".equals(ctypename)){
									m.invoke(t,rs.getByte(columnName[i]) );
								}else if("java.lang.Short".equals(ctypename)){
									m.invoke(t,rs.getShort(columnName[i]) );
								}else if("java.lang.Float".equals(ctypename)){
									m.invoke(t,rs.getFloat(columnName[i]) );
								}else if("java.lang.Double".equals(ctypename)){
									m.invoke(t,rs.getDouble(columnName[i]) );
								}else if("java.lang.Boolean".equals(ctypename)){
									m.invoke(t,rs.getBoolean(columnName[i]) );
								}else if("java.lang.Character".equals(ctypename)){
									m.invoke(t,rs.getCharacterStream(columnName[i]) );
								}else if("java.sql.Date".equals(ctypename)){
									m.invoke(t,rs.getDate(columnName[i]) );
								/*}else if("oracle.sql.BLOB".equals(ctypename)){
									//吧oracle里面的blob类型的值  转换为byte[] 的值
									BufferedInputStream is=null;
									byte[] bytes=null;
									Blob blob=rs.getBlob(columnName[i] );
									is=new BufferedInputStream(blob.getBinaryStream() );
									// blob.getBinaryStream()  二进制流转化   数据库中 存图片等文件
									bytes=new byte[(int) blob.length() ];
									is.read(bytes);
									m.invoke(t,bytes);*/
								}else{
									//invoke  反向激活
									m.invoke(t,rs.getObject(columnName[i]) );
								}
							}
							
						}
					}
				}	
				list.add(t);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			/**
			 * 改动的地方
			 */
		/*} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();*/
		}
		
		closeAll(pstm, null, rs, con);
		return list;
		
	}
	public static void closeAll(PreparedStatement pstm,Statement stmt,ResultSet rs,Connection con){
	try {
		//封装的话  就一定要考虑兼容问题
		if(pstm!=null){
			pstm.close();
		}
		if(stmt!=null){
			stmt.close();
		} 
		if(rs!=null){
			rs.close();
		}
		if(con!=null){
			con.close();
		}
	}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static Page<Map<String, Object>> pageForMysql(String sql, Integer page, Integer size,
			 Object...params) {
		//查询分页数据
		String querySql=sql+" limit ?,?";
		List<Map<String, Object>> data=DBHelper.select(querySql,params,size*(page-1),size);
		//查询总的记录bc数
		String countSql=" select count(*) cnt from ("+sql+")a ";
		long total=Long.parseLong((String) unique(countSql,"cnt",params)) ;
		return new Page<Map<String, Object>>(data,total);
	}
	private static List<Map<String, Object>> select(String sql, Object...params) {
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		//这些跟前面都是一样的
		try{
			Connection con=getCon();
			PreparedStatement pstm=con.prepareStatement(sql);
			System.out.println("SQL====：" + sql);
			doParams(pstm,params);
			ResultSet rs=pstm.executeQuery();
			//
			ResultSetMetaData rsmd=rs.getMetaData();
			//刚才看到，列名是以数组的形式存在的
			String[] columnName=new String[rsmd.getColumnCount()  ];
			for(int i=0;i<columnName.length ;i++){
				// 存列名                                        注意：这里不是从0开始的  而是从1开始的
				columnName[i]=rsmd.getColumnName(i+1);
			}
			
			while(rs.next() ){
				Map<String,Object> map=new HashMap<String,Object>();
				//根据列名来获取值
				for(int i=0;i<columnName.length ;i++){
					
					String cn=columnName[i];
					//获取值ֵ
					String value=rs.getString(cn);
					//键有了  值也有了  存map中
					map.put(cn, value);
				}
				//一个next遍历完毕   意味着一个map存完
				list.add(map);
			}
			closeAll(pstm,null,rs,con);
			
			//到了这一步  就开始不一样了
			//分析：根据原生的jdbc  最终是通过get方法，对应键  获取值  -> Map<String,String>
			//我们不可能只有一条数据  很多数据  ->List
			//融合  List<Map<String,String>>
			//返回类型确定
			//现在的难点  如何确定这个键  换句话说  如何确定一个表里面所有的列
		}catch(SQLException e){
			e.printStackTrace();
		}
		return list;
	}
	private static Object unique(String sql, String column, Object...params) {
		List<Map<String, Object>> data=DBHelper.select(sql,  params);
		if(data==null ||data.isEmpty()){
			return null;
		}else if(data.size()>1){
			throw new RuntimeException("返回结果不是唯一的");
		}else{
			return data.get(0).get(column);
		}
		
	}

	
}
