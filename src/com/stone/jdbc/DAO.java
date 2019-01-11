package com.stone.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

public class DAO {

	// insert,update,delete操作都可以包含在其中
	public void update(String sql, Object... args) {
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = JDBCTools.getConnection();
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < args.length; i++) {
				ps.setObject(i + 1, args[i]);
			}
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(null, ps, conn);
		}
	}

	// 查询一条记录，返回对应的对象
	public <T> T get(Class<T> clazz, String sql, Object... args) {
		T entity = null;

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			//1.得到ResultSet对象
			connection = JDBCTools.getConnection();
			preparedStatement = connection.prepareStatement(sql);
			for (int i = 0; i < args.length; i++) {
				preparedStatement.setObject(i + 1, args[i]);
			}
			resultSet = preparedStatement.executeQuery();
			
			//2.得到ResultSetMetaData对象
			ResultSetMetaData metaData = resultSet.getMetaData();
			
			//3.创建一个Map<String, Object>对象，键：SQL查询的列的别名，值：列的值
			Map<String, Object> values = new HashMap<>();
			
			//4.处理结果集，利用 ResultSetMetaData 填充3对应的Map对象
			if (resultSet.next()) {
				for (int i = 0; i < metaData.getColumnCount(); i++) {
					String columnLabel = metaData.getColumnLabel(i + 1);
					Object columnValue = resultSet.getObject(columnLabel);
					values.put(columnLabel, columnValue);
				}
			}
			
			//5.若Map不为空集，利用反射创建clazz对应的对象
			if (values.size() > 0) {
				entity = clazz.newInstance();
				
				//6.遍历Map对象，利用反射为Clazz对象的对应属性赋值
				for(Map.Entry<String, Object> entry : values.entrySet()) {
					String fieldName = entry.getKey();
					Object fieldValue = entry.getValue();
					//ReflectionUtils.setFieldValue(entity, fieldName, fieldValue);
					BeanUtils.setProperty(entity, fieldName, fieldValue);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(resultSet, preparedStatement, connection);
		}

		return entity;
	}

	// 查询多条记录，返回对应的对象集合
	public <T> List<T> getForList(Class<T> clazz, String sql, Object... args) {
		List<T> list = new ArrayList<>();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			//1.得到ResultSet对象
			connection = JDBCTools.getConnection();
			preparedStatement = connection.prepareStatement(sql);
			for (int i = 0; i < args.length; i++) {
				preparedStatement.setObject(i + 1, args[i]);
			}
			resultSet = preparedStatement.executeQuery();
			
			//2.得到ResultSetMetaData对象
			ResultSetMetaData metaData = resultSet.getMetaData();
			int columnCount = metaData.getColumnCount();
			
			//3.创建一个List<Map<String, Object>>对象，一个Map对象对应一条记录，
			//键：SQL查询的列的别名，值：列的值
			List<Map<String, Object>> values = new ArrayList<>();
			Map<String, Object> map = null;
			
			//4.处理结果集，利用 ResultSetMetaData 填充3对应的Map对象
			while(resultSet.next()) {
				map = new HashMap<>();
				for (int i = 0; i < columnCount; i++) {
					String columnLabel = metaData.getColumnLabel(i + 1);
					Object columnValue = resultSet.getObject(columnLabel);
					map.put(columnLabel, columnValue);
				}
				values.add(map);
			}
			
			//5.判断List使用为空集合，若不为空，则遍历List，得到多个Map对象，
			//再把每一个Map对象转为Class参数对应的Object对象
			T bean = null;
			if (values.size() > 0) {
				for (Map<String, Object> m : values) {
					for (Map.Entry<String, Object> entry : m.entrySet()) {
						String propertyName = entry.getKey();
						Object propertyValue = entry.getValue();
						bean = clazz.newInstance();
						BeanUtils.setProperty(bean, propertyName, propertyValue);
					}
					list.add(bean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(resultSet, preparedStatement, connection);
		}
		return list;
	}

	// 返回某条记录的某一个字段的值或一个统计的值（一共有多少条记录等）
	public <E> E getForValue(String sql, Object... args) {

		return null;
	}

}
