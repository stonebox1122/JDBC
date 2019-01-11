package com.stone.jdbc;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;
import javax.xml.crypto.Data;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 操作JDBC的工具类，其中封装了一些工具方法 Version 1
 *
 */
public class JDBCTools {
	
	//提交事物
	public static void commit(Connection connection) {
		if (connection != null) {
			try {
				connection.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	//回滚事物
	public static void rollback(Connection connection) {
		if (connection != null) {
			try {
				connection.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	//开始事物
	public static void beginTx(Connection connection) {
		if (connection != null) {
			try {
				connection.setAutoCommit(false);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 执行SQL语句，使用PreparedStatement
	 * @param sql
	 * @param args：填写SQL占位符的可变参数
	 */
	public static void update(String sql, Object ... args) {
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = JDBCTools.getConnection();
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < args.length; i++) {
				ps.setObject(i + 1, args[i]);
			}
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(null, ps, conn);
		}
	}
	
	/**
	 * 通用的更新的方法：包括insert,update,delete
	 * 版本1
	 */
	public static void update(String sql) {
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
	 * 关闭ResultSet,Statement和Connection
	 * @param statement
	 * @param conn
	 */
	public static void release(ResultSet rs, Statement statement, Connection conn) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 关闭Statement和Connection
	 * @param statement
	 * @param conn
	 */
	public static void release(Statement statement, Connection conn) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (conn != null) {
			try {
				//数据库连接池的Connection对象进行close时并不是真的进行关闭，而是把数据库连接还给连接池中
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static DataSource dataSource = null;
	//数据库连接池应只被初始化一次。
	static {
		dataSource = new ComboPooledDataSource("mysql");
	}
	
	public static Connection getConnection() throws Exception {
		return dataSource.getConnection();
	}

	/**
	 * 1.获取连接的方法。 通过读取配置文件从数据库服务器获取一个连接
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnection2() throws Exception {
		// 1.读取类路径下的jdbc.properties文件
		Properties properties = new Properties();
		InputStream in = JDBCTools.class.getClassLoader().getResourceAsStream("jdbc.properties");
		properties.load(in);
		String driverClass = properties.getProperty("driverClass");
		String jdbcUrl = properties.getProperty("jdbcUrl");
		String user = properties.getProperty("user");
		String password = properties.getProperty("password");

		// 2.加载数据库驱动程序（对应的Driver实现类中有注册驱动的静态代码块）
		Class.forName(driverClass);

		// 3.通过DriverManager的getConnection方法获取数据库连接
		return DriverManager.getConnection(jdbcUrl, user, password);
	}

}
