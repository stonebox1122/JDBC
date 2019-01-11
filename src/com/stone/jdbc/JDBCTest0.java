package com.stone.jdbc;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;

import org.junit.Test;

public class JDBCTest0 {
	
	/**
	 * ResultSet：结果集。封装了使用JDBC进行查询的结果
	 * 1.调用Statement对象的executeQuery(sql)可以得到结果集
	 * 2.ResultSet返回的实际是就是一张数据表。有一个指针指向数据表的第一行的前面
	 * 可以调用next()方法检测下一行是否有效，若有效该方法返回true，且指针下移。
	 * 相当于Iterator对象的hasNext()和next()方法的结合体
	 * 3.当指针定位到一行时，可以通过调用getXxx(index)或getXxx(column)获取
	 * 每一列的值，例如：getInt(1)，getString("name")
	 * 4.ResultSet当前也需要进行关闭
	 */
	@Test
	public void testResultSet() {
		//获取id=4的customers数据表的记录，并打印
		Connection conn = null;
		Statement statement = null;
		ResultSet rs = null;
		
		try {
			//1.获取Connection
			conn = JDBCTools.getConnection();
			
			//2.获取Statement
			statement = conn.createStatement();
			
			//3.准备SQL
			String sql = "select id,name,email,birth from customers";
			
			//4.执行查询，得到ResultSet
			rs = statement.executeQuery(sql);
			
			//5.处理ResultSet
			while (rs.next()) {
				int id = rs.getInt(1);
				String name = rs.getString("name");
				String email = rs.getString(3);
				Date birth = rs.getDate(4);
				
				System.out.println(id);
				System.out.println(name);
				System.out.println(email);
				System.out.println(birth);
			}
			
			//6.关闭数据库资源
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(rs, statement, conn);
		}
	}
	
	/**
	 * 通用的更新的方法：包括insert,update,delete
	 * 版本1
	 */
	public void update(String sql) {
		Connection conn = null;
		Statement statement = null;
		
		try {
			conn = JDBCTools.getConnection();
			statement = conn.createStatement();
			statement.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(statement, conn);
		}
	}
	
	/**
	 * 通过JDBC向指定的数据表中插入一条记录
	 * 1.Statement：用于执行SQL语句的对象
	 * 1）。通过Connection的createStatement()方法来获取
	 * 2）。通过executeUpdate(sql)可以执行SQL语句
	 * 3）。传入的SQL可以是insert，update或delete，但不能是select
	 * 
	 * 2.Connection，Statement都是应用程序和数据库服务器的连接资源，使用后一定要关闭
	 * 需要在finally中关闭Connection和Statement对象。
	 * 
	 * 3.关闭的顺序是：先关闭后获取的，即先关闭Statement后再关闭Connection
	 * @throws Exception 
	 */
	@Test
	public void testStatement() throws Exception {
		Connection conn = null;
		Statement statement = null;
		
		try {
			//1.获取数据库连接
			conn = getConnection2();
			//3.准备插入的SQL语句
			String sql = null;
			//sql = "insert into customers(name,email,birth) values('bbb','bbb@stone.com','2019-01-01')";
			//sql = "delete from customers where id=3";
			sql="update customers set name='ccc' where id=2";
			
			//4.执行插入
			//1)。获取操作SQL语句的Statement对象：调用Connection的createStatement()方法来获取
			statement = conn.createStatement();
			//2).调用Statement对象的executeUpdate(sql) 执行SQL语句进行插入
			statement.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				//5.关闭Statement对象
				if (statement != null) {
					statement.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				//2.关闭连接
				if (conn != null) {
					conn.close();
				}
			}
		}
	}
	
	public Connection getConnection2() throws Exception {
		
		//1.读取类路径下的jdbc.properties文件
		Properties properties = new Properties();
		InputStream in = getClass().getClassLoader().getResourceAsStream("jdbc.properties");
		properties.load(in);
		String driverClass = properties.getProperty("driverClass");
		String jdbcUrl = properties.getProperty("jdbcUrl");
		String user = properties.getProperty("user");
		String password = properties.getProperty("password");
		
		//2.加载数据库驱动程序（对应的Driver实现类中有注册驱动的静态代码块）
		Class.forName(driverClass);
		
		//3.通过DriverManager的getConnection方法获取数据库连接
		return DriverManager.getConnection(jdbcUrl, user, password);
	}
	
	@Test
	public void testGetConnection2() throws Exception {
		System.out.println(getConnection2());
	}
	
	/**
	 * DriverManager是驱动的管理类
	 * 1）。可以通过重载的getConnection()方法获取数据库连接，较为方便
	 * 2）。可以同时管理多个驱动程序：若注册了多个数据库连接，则调用getConnection()
	 * 方法时传入的参数不同，即返回不同的数据库连接
	 * @throws Exception
	 */
	@Test
	public void testDriverManager() throws Exception {
		//1.准备连接数据库的4个字符串
		String driverClass = null;
		String jdbcUrl = null;
		String user = null;
		String password = null;
		
		//读取类路径下的jdbc.properties文件
		Properties properties = new Properties();
		InputStream in = getClass().getClassLoader().getResourceAsStream("jdbc.properties");
		properties.load(in);
		driverClass = properties.getProperty("driverClass");
		jdbcUrl = properties.getProperty("jdbcUrl");
		user = properties.getProperty("user");
		password = properties.getProperty("password");
		
		//2.加载数据库驱动程序（对应的Driver实现类中有注册驱动的静态代码块）
		Class.forName(driverClass);
		
		//3.通过DriverManager的getConnection方法获取数据库连接
		Connection connection = DriverManager.getConnection(jdbcUrl, user, password);
		System.out.println(connection);
	}
	
	
	/**
	 * Driver是一个接口：数据库厂商必须提供实现的接口，能从中获取数据库连接。
	 * 可以通过Driver的实现类对象获取数据库连接
	 * 1.加入MySQL驱动
	 * 1）。在当前项目下新建lib目录
	 * 2）。把mysql-connector-java-5.1.23-bin.jar复制到lib目录下
	 * 3）。右键build-path，add to buildpath 加入到类路径下。
	 * @throws SQLException 
	 */
	@Test
	public void testDriver() throws SQLException {
		//1.创建一个Driver实现类的对象
		Driver driver = new com.mysql.jdbc.Driver();
		
		//2.准备连接数据库的基本信息：url，user，password
		String url = "jdbc:mysql://172.30.60.14:3306/mybatis";
		Properties info = new Properties();
		info.put("user", "stest1");
		info.put("password", "P@ssw0rd1");
		
		//3.调用Dirver接口的connect(url,info)获取数据库连接
		Connection connection = driver.connect(url, info);
		System.out.println(connection);
	}
	
	/**
	 * 编写一个通用的方法，在不修改源程序的情况下，可以获取任何数据库的连接
	 * 解决方法：把数据库驱动Driver实现类的全类名，url，user，password放入一个配置文件中
	 * 通过修改配置文件的方式实现和具体数据库的解耦
	 */
	public Connection getConnection() throws Exception {
		String driverClass = null;
		String jdbcUrl = null;
		String user = null;
		String password = null;
		
		//读取类路径下的jdbc.properties文件
		InputStream in = getClass().getClassLoader().getResourceAsStream("jdbc.properties");
		Properties properties = new Properties();
		properties.load(in);
		driverClass = properties.getProperty("driverClass");
		jdbcUrl = properties.getProperty("jdbcUrl");
		user = properties.getProperty("user");
		password = properties.getProperty("password");
		
		//通过反射创建Driver对象
		Driver driver = (Driver) Class.forName(driverClass).newInstance();
		
		Properties info = new Properties();
		info.put("user", user);
		info.put("password", password);
		
		//通过Driver的connect方法获取数据库连接
		Connection connection = driver.connect(jdbcUrl, info);
		
		return connection;
	}
	
	@Test
	public void testGetConnection() throws Exception {
		System.out.println(getConnection());
	}
}