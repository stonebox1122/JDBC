package com.stone.jdbc;

import javax.sql.DataSource;
import org.junit.Test;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class TestC3P0 {
	
	/**
	 * 创建c3p0-config.xml配置文件
	 * @throws Exception
	 */
	@Test
	public void testC3P0WithConfigFile() throws Exception {
		DataSource dataSource = new ComboPooledDataSource("mysql");
		System.out.println(dataSource.getConnection());
		ComboPooledDataSource comboPooledDataSource = (ComboPooledDataSource) dataSource;
		System.out.println(comboPooledDataSource.getMaxStatements());
	}
	
	/**
	 * C3P0连接池
	 * 1.导入jar包：c3p0-0.9.5.2.jar，mchange-commons-java-0.2.11.jar
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {
		ComboPooledDataSource cpds = new ComboPooledDataSource();
		cpds.setDriverClass("com.mysql.jdbc.Driver");
		cpds.setJdbcUrl("jdbc:mysql://172.30.60.14:3306/mybatis");
		cpds.setUser("stest1");
		cpds.setPassword("P@ssw0rd1");
		System.out.println(cpds.getConnection());
	}

}
