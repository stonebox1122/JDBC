package com.stone.jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;

import org.junit.Test;

public class TestBatch {

	
	/**
	 * 向Oracle的customers数据表中插入10w条记录
	 * 测试如何插入，用时最短
	 * 2.使用Batch
	 */
	@Test
	public void testBatch() {
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		String sql = null;
		
		try {
			connection = JDBCTools.getConnection();
			sql = "insert into customers values(?,?,?)";
			preparedStatement = connection.prepareStatement(sql);
			Date date  = new Date(new java.util.Date().getTime());
			long begin = System.currentTimeMillis();
			
			JDBCTools.beginTx(connection);
			for (int i = 0; i < 100000; i++) {
				preparedStatement.setInt(1, i + 1);
				preparedStatement.setString(2, "name_" + i);
				preparedStatement.setDate(3, date);
				//preparedStatement.executeUpdate();
				//积攒SQL，当积攒到一定程度就统一执行一次，并清空先前积攒
				preparedStatement.addBatch();
				if ((i + 1) % 3000 == 0) {
					preparedStatement.executeBatch();
					preparedStatement.clearBatch();
				}
			}
			//若总条数不是批量数值的整数倍，则还需要在执行一次
			if (100000 % 300 != 0) {
				preparedStatement.executeBatch();
				preparedStatement.clearBatch();
			}
			JDBCTools.commit(connection);
			
			long end = System.currentTimeMillis();
			System.out.println("Time: " + (end - begin));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(null, preparedStatement, connection);
		}
	}
	
	/**
	 * 向Oracle的customers数据表中插入10w条记录
	 * 测试如何插入，用时最短
	 * 2.使用PreparedStatement
	 */
	@Test
	public void testBatchWithPreparedStatement() {
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		String sql = null;
		
		try {
			connection = JDBCTools.getConnection();
			sql = "insert into customers values(?,?,?)";
			preparedStatement = connection.prepareStatement(sql);
			Date date  = new Date(new java.util.Date().getTime());
			long begin = System.currentTimeMillis();
			
			JDBCTools.beginTx(connection);
			for (int i = 0; i < 100000; i++) {
				preparedStatement.setInt(1, i + 1);
				preparedStatement.setString(2, "name_" + i);
				preparedStatement.setDate(3, date);
				preparedStatement.executeUpdate();
			}
			JDBCTools.commit(connection);
			
			long end = System.currentTimeMillis();
			System.out.println("Time: " + (end - begin));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(null, preparedStatement, connection);
		}
	}
	
	/**
	 * 向Oracle的customers数据表中插入10w条记录
	 * 测试如何插入，用时最短
	 * 1.使用Statement
	 */
	@Test
	public void testBatchWithStatement() {
		
		Connection connection = null;
		Statement statement = null;
		String sql = null;
		
		try {
			connection = JDBCTools.getConnection();
			statement = connection.createStatement();
			long begin = System.currentTimeMillis();
			
			JDBCTools.beginTx(connection);
			for (int i = 0; i < 100000; i++) {
				sql = "insert into customers values(" + (i + 1) +",'name_" + i + "','11-JAN-19')";
				statement.executeUpdate(sql);
			}
			JDBCTools.commit(connection);
			
			long end = System.currentTimeMillis();
			System.out.println("Time: " + (end - begin));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(null, statement, connection);
		}
	}

}
