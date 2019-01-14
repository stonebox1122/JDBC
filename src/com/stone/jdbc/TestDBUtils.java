package com.stone.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.Test;

/**
 * 测试DBUtils工具类
 *
 */
public class TestDBUtils {


	/**
	 * ScalarHandler：把结果集转为一个数值（可以是任意基本数据类型）返回
	 */
	@Test
	public void testScalarHandler() {
		QueryRunner queryRunner = new QueryRunner();
		Connection conn = null;
		try {
			conn  = JDBCTools.getConnection();
			String sql = "select name from customers where id=?";
			Object name = queryRunner.query(conn, sql, new ScalarHandler(),2);
			System.out.println(name);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(null, null, conn);
		}
	}
	
	
	/**
	 * MapListHandler：将结果集转为一个Map的List
	 * Map对应查询的一条记录：键：SQL查询的列名（不是列的别名），值：列的值
	 * 而MapListHandler：返回多条记录对应的Map集合
	 */
	@Test
	public void testMapListHandler() {
		QueryRunner queryRunner = new QueryRunner();
		Connection conn = null;
		try {
			conn  = JDBCTools.getConnection();
			String sql = "select id,name,email,birth from customers";
			List<Map<String, Object>> customers = queryRunner.query(conn, sql, new MapListHandler());
			System.out.println(customers);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(null, null, conn);
		}
	}
	
	
	/**
	 * MapHandler：返回SQL对应的第一条记录对应的Map对象
	 * 键：SQL查询的列名（不是列的别名），值：列的值
	 */
	@Test
	public void testMapHandler() {
		QueryRunner queryRunner = new QueryRunner();
		Connection conn = null;
		try {
			conn  = JDBCTools.getConnection();
			String sql = "select id,name,email,birth from customers";
			Map<String, Object> customers = queryRunner.query(conn, sql, new MapHandler());
			System.out.println(customers);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(null, null, conn);
		}
	}
	
	
	/**
	 * BeanListHandler：把结果集转为List，该List不为null，但可能为空集合（size()方法返回0）
	 * 若SQL语句的确能够查询到记录，List中存放创建BeanListHandler传入的class对象对应的对象
	 */
	@Test
	public void testBeanListHandler() {
		QueryRunner queryRunner = new QueryRunner();
		Connection conn = null;
		try {
			conn  = JDBCTools.getConnection();
			String sql = "select id,name,email,birth from customers";
			@SuppressWarnings("unchecked")
			List<Customers> customers = (List<Customers>) queryRunner.query(conn, sql, new BeanListHandler(Customers.class));
			System.out.println(customers);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(null, null, conn);
		}
	}
	
	/**
	 * BeanHandler：把结果集的第一条记录转为创建BeanHandler对象时传入的class参数对应的对象
	 */
	@Test
	public void testBeanHandler() {
		QueryRunner queryRunner = new QueryRunner();
		Connection conn = null;
		try {
			conn  = JDBCTools.getConnection();
			String sql = "select id,name,email,birth from customers where id=?";
			@SuppressWarnings("unchecked")
			Customers customer = (Customers) queryRunner.query(conn, sql, new BeanHandler(Customers.class), 2);
			System.out.println(customer);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(null, null, conn);
		}
	}
	
	
	class MyResultSetHandler implements ResultSetHandler{

		@Override
		public Object handle(ResultSet rs) throws SQLException {
			List<Customers> customers = new ArrayList<>();
			while (rs.next()) {
				int id = rs.getInt(1);
				String name = rs.getString(2);
				String email = rs.getString(3);
				Date birth = rs.getDate(4);
				Customers customer = new Customers(id, name, email, birth);
				customers.add(customer);
			}
			return customers;
		}
	}
	
	/**
	 * queryRunner的query方法的返回值取决于其ResultSetHandler参数的handle方法的返回值
	 */
	@Test
	public void testQuery() {
		// 1.创建QueryRunner的实现类
		QueryRunner queryRunner = new QueryRunner();
		Connection conn = null;
		
		try {
			conn = JDBCTools.getConnection();
			String sql = "select id,name,email,birth from customers";
			Object obj = queryRunner.query(conn, sql, new MyResultSetHandler());
			System.out.println(obj);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(null, null, conn);
		}
	}

	/**
	 * 测试QueryRunner类的update方法 该方法可用于insert，delete和update，类似于前面编写的Dao方法。
	 */
	@Test
	public void testQueryRunnerUpdate() {
		// 1.创建QueryRunner的实现类
		QueryRunner queryRunner = new QueryRunner();

		// 2.使用其update方法
		String sql = "delete from customers where id in (?,?)";
		Connection conn = null;
		try {
			conn = JDBCTools.getConnection();
			queryRunner.update(conn, sql, 10, 11);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(null, null, conn);
		}
	}

}
