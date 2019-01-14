package com.stone.jdbc;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

/**
 * 使用QueryRunner提供其具体的实现
 * @param <T>：子类需要传入的泛型类型
 */
public class DBUtilsDAOImpl<T> implements DBUtilsDAO<T> {
	
	private QueryRunner queryRunner = null;
	private Class<T> type;
	public DBUtilsDAOImpl() {
		queryRunner = new QueryRunner();
		type = ReflectionUtils.getSuperGenericType(getClass());
	}

	@Override
	public void update(Connection connection, String sql, Object... args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public T get(Connection connection, String sql, Object... args) throws Exception {
		return queryRunner.query(connection, sql, new BeanHandler<>(type), args);
	}

	@Override
	public List<T> getForList(Connection connection, String sql, Object... args) {
		return null;
	}

	@Override
	public <E> E getForValue(Connection connection, String sql, Object... args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void batch(Connection connection, String sql, Object[]... args) {
		// TODO Auto-generated method stub
		
	}

}
