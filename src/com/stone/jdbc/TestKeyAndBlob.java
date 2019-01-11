package com.stone.jdbc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import org.junit.Test;

public class TestKeyAndBlob {

	
	/**
	 * 读取Blob 数据
	 * 1.使用getBlob()方法读取到Blob对象
	 * 2.调用Blob的getBinaryStream()方法得到输入流，再使用IO操作即可。
	 */
	@Test
	public void testOracleReadBlob() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = JDBCTools.getConnection();
			String sql = "select name,email,birth,picture from customers2";
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			
			if (resultSet.next()) {
				Blob picture = resultSet.getBlob(4);
				InputStream in = picture.getBinaryStream();
				OutputStream out = new FileOutputStream("2.jpg");
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = in.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
				out.close();
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(resultSet, preparedStatement, connection);
		}
	}
	
	/**
	 * 插入BLOB类型的数据必须使用PreparedStatement，因为BLOB类型的数据是无法使用字符串拼接的
	 * 调用setBlob(int index, InputStream inputStream)方法
	 */
	@Test
	public void testOracleInsertBlob() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = JDBCTools.getConnection();
			String sql = "insert into customers2(name,email,birth,picture) values(?,?,?,?)";
			preparedStatement = connection.prepareStatement(sql);

			preparedStatement.setString(1, "jerry");
			preparedStatement.setString(2, "jerry@stone.com");
			preparedStatement.setDate(3, new Date(new java.util.Date().getTime()));
			InputStream in = new FileInputStream("1.jpg");
			preparedStatement.setBlob(4, in);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(resultSet, preparedStatement, connection);
		}
	}
	
	/**
	 * 读取Blob 数据
	 * 1.使用getBlob()方法读取到Blob对象
	 * 2.调用Blob的getBinaryStream()方法得到输入流，再使用IO操作即可。
	 */
	@Test
	public void testReadBlob() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = JDBCTools.getConnection();
			String sql = "select id,name,email,picture from customers2 where id=11";
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			
			if (resultSet.next()) {
				Blob picture = resultSet.getBlob(4);
				InputStream in = picture.getBinaryStream();
				OutputStream out = new FileOutputStream("2.jpg");
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = in.read(buffer)) != -1) {
					out.write(buffer, 0, len);
				}
				out.close();
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(resultSet, preparedStatement, connection);
		}
	}
	
	/**
	 * 插入BLOB类型的数据必须使用PreparedStatement，因为BLOB类型的数据是无法使用字符串拼接的
	 * 调用setBlob(int index, InputStream inputStream)方法
	 */
	@Test
	public void testInsertBlob() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = JDBCTools.getConnection();
			String sql = "insert into customers2(name,email,birth,picture) values(?,?,?,?)";
			preparedStatement = connection.prepareStatement(sql);

			preparedStatement.setString(1, "jerry");
			preparedStatement.setString(2, "jerry@stone.com");
			preparedStatement.setDate(3, new Date(new java.util.Date().getTime()));
			InputStream in = new FileInputStream("1.jpg");
			preparedStatement.setBlob(4, in);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(resultSet, preparedStatement, connection);
		}
	}

	@Test
	public void testGetKeyValue() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = JDBCTools.getConnection();
			String sql = "insert into customers(name,email,birth) values(?,?,?)";
			// preparedStatement = connection.prepareStatement(sql);

			// 使用重载的prepareStatement(sql,flag)来生成preparedStatement对象
			preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, "jerry");
			preparedStatement.setString(2, "jerry@stone.com");
			preparedStatement.setDate(3, new Date(new java.util.Date().getTime()));
			preparedStatement.executeUpdate();

			// 通过preparedStatement的getGeneratedKeys()获取包含了新生成的主键的ResultSet对象
			resultSet = preparedStatement.getGeneratedKeys();
			if (resultSet.next()) {
				System.out.println(resultSet.getObject(1));
			}

			// 在ResultSet中只有1列GENERATED_KEY，用于存放新生成的主键值
			ResultSetMetaData metaData = resultSet.getMetaData();
			for (int i = 0; i < metaData.getColumnCount(); i++) {
				System.out.println(metaData.getColumnName(i + 1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(resultSet, preparedStatement, connection);
		}
	}
}