package com.stone.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

import org.junit.Test;

public class TestPreparedStatement {

	@Test
	public void test() {
		/**
		 * PreparedStatement：是Statement的子接口，可以传入带占位符的SQL语句， 并且提供了补充占位符变量的方法 可以有效防止SQL注入
		 * 提高性能
		 */
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JDBCTools.getConnection();
			String sql = "insert into customers(name,email,birth) values(?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, "ddd");
			ps.setString(2, "ddd@stone.com");
			ps.setDate(3, new java.sql.Date(new Date().getTime()));
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(null, ps, conn);
		}
	}

}
