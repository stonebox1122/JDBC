package com.stone.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

public class DAO2 {

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
		List<T> result = getForList(clazz, sql, args);
		if (result.size() > 0) {
			return result.get(0);
		}
		return null;
	}

	// 查询多条记录，返回对应的对象集合
	public <T> List<T> getForList(Class<T> clazz, String sql, Object... args) {
		List<T> list = new ArrayList<>();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			//1.得到结果集
			connection = JDBCTools.getConnection();
			preparedStatement = connection.prepareStatement(sql);
			for (int i = 0; i < args.length; i++) {
				preparedStatement.setObject(i + 1, args[i]);
			}
			resultSet = preparedStatement.executeQuery();
			
			//2.处理结果集，得到Map的List，其中一个Map对象，就是一条记录。Map的key为resultSet中列的别名，Map的value为列的值
			List<Map<String, Object>> values = handleResultSetToMapList(resultSet);
			
			//3.把Map的List转为clazz对应的List，其中Map的key即为clazz对应对象的propertyName，Map的value即为clazz对应对象的propertyValue
			list = transferMapListToBeanList(clazz, values);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(resultSet, preparedStatement, connection);
		}
		return list;
	}

	public <T> List<T> transferMapListToBeanList(Class<T> clazz, List<Map<String, Object>> values)
			throws Exception {
		List<T> result = new ArrayList<>();
		T bean = null;
		if (values.size() > 0) {
			for (Map<String, Object> m : values) {
				for (Map.Entry<String, Object> entry : m.entrySet()) {
					String propertyName = entry.getKey();
					Object propertyValue = entry.getValue();
					bean = clazz.newInstance();
					BeanUtils.setProperty(bean, propertyName, propertyValue);
				}
				result.add(bean);
			}
		}
		return result;
	}

	/**
	 * 处理结果集，得到Map的一个List，其中一个Map对象对应一条记录
	 * @param resultSet
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public List<Map<String, Object>> handleResultSetToMapList(ResultSet resultSet) throws Exception, SQLException {
		//3.创建一个List<Map<String, Object>>对象，一个Map对象对应一条记录，
		//键：SQL查询的列的别名，值：列的值
		List<Map<String, Object>> values = new ArrayList<>();
		List<String> columnLabels = getColumnLabels(resultSet);
		Map<String, Object> map = null;
		
		//4.处理结果集，利用 ResultSetMetaData 填充3对应的Map对象
		while(resultSet.next()) {
			map = new HashMap<>();
			for (String columnLabel : columnLabels) {
				Object columnValue = resultSet.getObject(columnLabel);
				map.put(columnLabel, columnValue);
			}
			values.add(map);
		}
		return values;
	}
	
	/**
	 * 获取结果集的ColumnLabel的list
	 * @param resultSet
	 * @return
	 * @throws Exception
	 */
	private List<String> getColumnLabels(ResultSet resultSet) throws Exception{
		List<String> labels = new ArrayList<>();
		
		ResultSetMetaData metaData = resultSet.getMetaData();
		int columnCount = metaData.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			labels.add(metaData.getColumnLabel(i + 1));
		}
		
		return labels;
	}

	// 返回某条记录的某一个字段的值或一个统计的值（一共有多少条记录等）
	public <E> E getForValue(String sql, Object... args) {
		//1.得到结果集：应该只有一行，且只有一列
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = JDBCTools.getConnection();
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
}
