package com.stone.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.junit.Test;

public class MetaDataTest {

	/**
	 * ResultSetMetaData：描述结果集的元数据
	 * 可以得到结果集中的基本信息：结果集中有哪些列，列名，列的别名等。
	 */
	@Test
	public void testResultSetMetaData() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = JDBCTools.getConnection();
			String sql = "select id,name,email,birth from customers";
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			ResultSetMetaData metaData = resultSet.getMetaData();
			
			//得到列的个数
			int columnCount = metaData.getColumnCount();
			System.out.println(columnCount);
			
			for (int i = 0; i < columnCount; i++) {
				//得到列名
				String columnName = metaData.getColumnName(i + 1);
				
				//得到列的别名
				String columnLabel = metaData.getColumnLabel(i + 1);
				
				System.out.println(columnName + " , " + columnLabel);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(resultSet, preparedStatement, connection);
		}
	}
	
	/**
	 * DatabaseMetaData 是描述数据库的元数据对象
	 */
	@Test
	public void testDatabaseMetaData() {
		Connection connection = null;
		ResultSet resultSet = null;
		try {
			connection = JDBCTools.getConnection();
			DatabaseMetaData metaData = connection.getMetaData();
			
			//可以得到数据库本身的一些基本信息
			System.out.println(metaData.getDatabaseProductVersion());
			System.out.println(metaData.getUserName());
			resultSet = metaData.getCatalogs();
			while (resultSet.next()) {
				System.out.println(resultSet.getString(1));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(resultSet, null, connection);
		}
	}

}
