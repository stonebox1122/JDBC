package com.stone.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

import org.junit.Test;

public class TestCallableStatement {

	/**
	 * 如何使用JDBC调用存储在数据库中的函数或存储过程
	 */
	@Test
	public void test() {
		Connection conn = null;
		CallableStatement callableStatement = null;
		
		try {
			conn = JDBCTools.getConnection();
			
			//1.通过Connection对象的prepareCall()方法创建一个CallableStatement对象的实例，
			//在使用Connection对象的prepareCall()方法时，需要传入一个String类型的字符串，该字符串用于指明如何调用存储过程
			String sql = "{?= call sum_salary(?,?)}";
			callableStatement = conn.prepareCall(sql);
			
			//2.通过CallableStatement对象的registerOutParameter方法注册OUT参数
			callableStatement.registerOutParameter(1, Types.NUMERIC);
			callableStatement.registerOutParameter(3, Types.NUMERIC);
			
			//3.通过CallableStatement对象的setXxx()方法设定IN或IN OUT参数
			//若想将参数默认值设为null，可以使用setNull()方法
			callableStatement.setInt(2, 80);
			
			//4.通过CallableStatement对象的execute()方法执行存储过程
			callableStatement.execute();
			
			//5.如果所调用的是带返回参数的存储过程，还需要通过CallableStatement对象的getXxx()方法获取其返回值
			double sumSalary = callableStatement.getDouble(1);
			long empCount = callableStatement.getLong(3);
			
			System.out.println(sumSalary);
			System.out.println(empCount);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(null, callableStatement, conn);
		}
	}

}
