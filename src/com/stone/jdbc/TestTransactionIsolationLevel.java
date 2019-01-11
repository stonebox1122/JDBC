package com.stone.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

public class TestTransactionIsolationLevel {

	@Test
	public void testUpdate() {
		Connection connection = null;
		
		try {
			connection = JDBCTools.getConnection();
			connection.setAutoCommit(false);
			
			String sql = "update user set balance=balance-500 where id=1";
			update(connection, sql);
			
			connection.commit();			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				connection.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			JDBCTools.release(null, null, connection);
		}
	}
	
	@Test
	public void testRead() {
		String sql = "select balance from user where id=1";
		int balance = getForValue(sql);
		System.out.println(balance);
	}
	
	public <E> E getForValue(String sql, Object... args) {
		//1.得到结果集：应该只有一行，且只有一列
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = JDBCTools.getConnection();
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			preparedStatement = connection.prepareStatement(sql);
			for (int i = 0; i < args.length; i++) {
				preparedStatement.setObject(i + 1, args[i]);
			}
			resultSet = preparedStatement.executeQuery();
			
			if (resultSet.next()) {
				return (E) resultSet.getObject(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(resultSet, preparedStatement, connection);
		}
		return null;
	}
	
	public void update(Connection conn, String sql, Object... args) {
		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < args.length; i++) {
				ps.setObject(i + 1, args[i]);
			}
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(null, ps, null);
		}
	}

}
