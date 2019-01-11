package com.stone.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.Test;

public class TestTransaction {

	/**
	 * 关于事物：
	 * 1.如果多个操作，每个操作使用的是自己的单独的连接，则无法保证事物
	 * 2.具体步骤：
	 * 1）。事物操作开始时，开始事物：取消Connection的默认提交行为：connection.setAutoCommit(false)
	 * 2）。如果事物的操作都成功，则提交事物：connection.commit();
	 * 3）。如果出现异常，则回滚事物：connection.rollback();
	 */
	@Test
	public void test() {
		Connection connection = null;
		
		try {
			connection = JDBCTools.getConnection();
			
			//开始事物：默认取消提交
			connection.setAutoCommit(false);
			
			String sql = "update user set balance=balance-500 where id=1";
			update(connection, sql);
			
			sql = "update user set balance=balance+500 where id=2";
			update(connection, sql);
			
			//提交事物
			connection.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			JDBCTools.release(null, null, connection);
		}
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
