package com.stone.jdbc;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.Test;

public class TestDBCP {

	/**
	 * 1.加载dbcp的properties配置文件，配置文件中的键需要来自于BasicDataSource的属性
	 * 2.调用BasicDataSourceFactory.createDataSource()方法创建DataSource实例
	 * 3.从DataSource中获取数据库连接
	 * @throws Exception
	 */
	@Test
	public void testDBCPWithDataSourceFactory() throws Exception {
		
		Properties properties = new Properties();
		InputStream inputStream = TestDBCP.class.getClassLoader().getResourceAsStream("dbcp.properties");
		properties.load(inputStream);
		DataSource dataSource = BasicDataSourceFactory.createDataSource(properties);
		System.out.println(dataSource.getConnection());
		
		BasicDataSource basicDataSource = (BasicDataSource) dataSource; 
		System.out.println(basicDataSource.getMaxTotal());
	}
	
	/**
	 * 使用DBCP连接池
	 * 1.加入jar包：commons-dbcp2-2.5.0.jar,commons-pool2-2.6.0.jar
	 * 2.创建数据库连接池
	 * @throws Exception 
	 */
	@Test
	public void test() throws Exception {		
		//BasicDataSource dataSource = null;
		//1.创建DBCP数据源实例
		//dataSource = new BasicDataSource();
		final BasicDataSource dataSource = new BasicDataSource();
		
		//2.为数据源实例指定必须的属性
		dataSource.setUsername("stest1");
		dataSource.setPassword("P@ssw0rd1");
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://172.30.60.14:3306/mybatis");
		
		//3.指定数据源实例可选属性
		//1)。指定数据库连接池中初始化连接数
		dataSource.setInitialSize(5);
		
		//2).指定最大的连接数：同一时刻可以同时向数据库申请的连接数
		dataSource.setMaxTotal(5);
		
		//3).指定最小空闲连接数：在数据库连接池中保存的最少的空闲连接的数量
		dataSource.setMinIdle(5);
		
		//4).等待数据库连接池分配连接的最长时间，单位为毫秒，超出时间将抛出异常
		dataSource.setMaxWaitMillis(1000 * 5);
		
		//4.从数据源中获取数据库连接
		Connection connection = dataSource.getConnection();
		System.out.println(connection.getClass());
		
		connection = dataSource.getConnection();
		System.out.println(connection.getClass());
		
		connection = dataSource.getConnection();
		System.out.println(connection.getClass());
		
		connection = dataSource.getConnection();
		System.out.println(connection.getClass());
		
		Connection connection2 = dataSource.getConnection();
		System.out.println(connection2.getClass());
		
		new Thread() {
			public void run() {
				Connection conn;
				try {
					conn = dataSource.getConnection();
					System.out.println(conn.getClass());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			};
		}.start();
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		connection2.close();
	}
}