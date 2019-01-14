package com.stone.jdbc;

import java.sql.Connection;
import java.util.List;

/**
 * 访问数据的DAO接口
 * 定义访问数据表的各种方法
 * @param T：DAO处理实体类的类型
 */
public interface DBUtilsDAO<T> {
	
	/**
	 * INSERT,UPDATE,DELETE
	 * @param connection：数据库连接
	 * @param sql：SQL语句
	 * @param args：填充占位符的可变参数
	 */
	void update(Connection connection, String sql, Object ... args);
	
	/**
	 * 返回一个T的对象
	 * @param connection
	 * @param sql
	 * @param args
	 * @return
	 * @throws Exception 
	 */
	T get(Connection connection, String sql, Object ... args) throws Exception;
	
	/**
	 * 返回T的一个集合
	 * @param connection
	 * @param sql
	 * @param args
	 * @return
	 */
	List<T> getForList(Connection connection, String sql, Object ... args);
	
	/**
	 * 返回具体的一个值，例如总记录数
	 * @param connection
	 * @param sql
	 * @param args
	 * @return
	 */
	<E> E getForValue(Connection connection, String sql, Object ... args);
	
	/**
	 * 批量处理的方法
	 * @param connection
	 * @param sql
	 * @param args：填充占位符的Object[]类型的可变参数
	 */
	void batch(Connection connection, String sql, Object[] ... args);

}
