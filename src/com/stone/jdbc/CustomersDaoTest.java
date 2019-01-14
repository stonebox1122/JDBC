package com.stone.jdbc;

import static org.junit.Assert.fail;

import java.sql.Connection;

import org.junit.Test;

public class CustomersDaoTest {
	
	CustomersDao customersDao = new CustomersDao();

	@Test
	public void testUpdate() {
		fail("Not yet implemented");
	}

	@Test
	public void testGet() {
		Connection conn = null;
		try {
			conn = JDBCTools.getConnection();
			String sql = "select id,name customerName,email,birth from customers where id=?";
			Customers customer = (Customers) customersDao.get(conn, sql, 2);
			System.out.println(customer);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(null, null, conn);
		}
	}

	@Test
	public void testGetForList() {
		
	}

	@Test
	public void testGetForValue() {
		fail("Not yet implemented");
	}

	@Test
	public void testBatch() {
		fail("Not yet implemented");
	}

}
