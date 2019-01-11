package com.stone.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.junit.Test;

public class JDBCTest1 {

	/**
	 * ResultSetMetaData：是描述ResultSet的元数据对象，即从中可以获取到结果集中有多少列，列名是什么
	 * 通过调用ResultSet的getMetaData()方法获取ResultSetMetaData对象。 ResultSetMetaData好用的方法：
	 * >int getColumnCount():SQL语句中包含哪些列 
	 * >String getColumnLabel(int column):获取指定的列的别名，其中索引从1开始
	 */
	@Test
	public void testResultSetMetaData() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			String sql = "select flow_id flowId,type,id_card idCard,exam_card examCard,student_name studentName,location,grade from examstudent2 where flow_id=?";
			connection = JDBCTools.getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, 1);
			resultSet = preparedStatement.executeQuery();
			Map<String, Object> values = new HashMap<String, Object>();

			// 1.得到ResultSetMetaData对象
			ResultSetMetaData metaData = resultSet.getMetaData();
			
			while (resultSet.next()) {
				// 2.获取每一列的列名
				for (int i = 0; i < metaData.getColumnCount(); i++) {
					String columnLabel = metaData.getColumnLabel(i + 1);
					Object columnValue = resultSet.getObject(columnLabel);
					values.put(columnLabel, columnValue);
				}
			}
			
			System.out.println(values);
			
			Class clazz = Student.class;
			Object object = clazz.newInstance();
			
			for(Map.Entry<String, Object> entry: values.entrySet()) {
				String fieldName = entry.getKey();
				Object fieldValue = entry.getValue();
				System.out.println(fieldName + " : " + fieldValue);
				ReflectionUtils.setFieldValue(object, fieldName, fieldValue);
			}
			
			System.out.println(object);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(resultSet, preparedStatement, connection);
		}
	}

	@Test
	public void testGet() {
		String sql = "select id,name,email,birth from customers where id=?";
		Customers customers = get(Customers.class, sql, 4);
		System.out.println(customers);

		sql = "select flow_id flowId,type,id_card idCard,exam_card examCard,student_name studentName,location,grade from examstudent2 where flow_id=?";
		Student student = get(Student.class, sql, 1);
		System.out.println(student);
	}

	/**
	 * 编写通用的查询方法，可以根据传入的SQL，Class对象返回SQL对应的记录的对象
	 * 具体步骤： 
	 * 1.先利用 SQL 进行查询，得到结果集 
	 * 2.利用反射创建实体类的对象：创建 Student 对象
	 * 3.获取结果集的列的别名：idCard、studentName 
	 * 4.再获取结果集的每一列的值， 结合 3 得到一个Map，键：列的别名，值：列的值：{flowId:5, type:6, idCard: xxx ……} 
	 * 5.再利用反射为 2 的对应的属性赋值：属性即为Map 的键，值即为 Map 的值
	 * @param clazz：描述对象的类型
	 * @param sql：SQL语句，可能带占位符
	 * @param args：填充占位符的可变参数
	 * @return
	 */
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
					ReflectionUtils.setFieldValue(entity, fieldName, fieldValue);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(resultSet, preparedStatement, connection);
		}

		return entity;
	}

	public Customers getCustomers(String sql, Object... args) {
		Customers customers = null;

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
				customers = new Customers();
				customers.setId(resultSet.getInt(1));
				customers.setName(resultSet.getString(2));
				customers.setEmail(resultSet.getString(3));
				customers.setBirth(resultSet.getDate(4));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(resultSet, preparedStatement, connection);
		}

		return customers;
	}

	public Student getStudent(String sql, Object... args) {
		Student student = null;

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
				student = new Student();
				student.setFlowId(resultSet.getInt(1));
				student.setType(resultSet.getInt(2));
				student.setIdCard(resultSet.getString(3));
				student.setExamCard(resultSet.getString(4));
				student.setStudentName(resultSet.getString(5));
				student.setLocation(resultSet.getString(6));
				student.setGrade(resultSet.getInt(7));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(resultSet, preparedStatement, connection);
		}

		return student;
	}

	public void addNewStudent2(Student student) {
		String sql = "insert into examstudent(flowid,type,idcard,examcard,studentname,location,grade) values(?,?,?,?,?,?,?)";
		JDBCTools.update(sql, student.getFlowId(), student.getType(), student.getIdCard(), student.getExamCard(),
				student.getStudentName(), student.getLocation(), student.getGrade());
	}

	/**
	 * PreparedStatement：是Statement的子接口，可以传入带占位符的SQL语句， 并且提供了补充占位符变量的方法 可以有效防止SQL注入
	 * 提高性能
	 */
	@Test
	public void testPreparedStatement() {
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

	@Test
	public void testGetStudent() {
		// 1.得到查询的类型
		int searchType = getSearchTypeFromConsole();

		// 2.具体查询学生信息
		Student student = searchStudent(searchType);

		// 3.打印学生信息
		printStudent(student);
	}

	/**
	 * 打印学生信息：若学生存在则打印具体信息，若不存在，打印插入此人
	 * 
	 * @param student
	 */
	private void printStudent(Student student) {
		if (student != null) {
			System.out.println(student);
		} else {
			System.out.println("查无此人");
		}

	}

	/**
	 * 具体查询学生信息，返回一个student对象，若不存在，则返回null
	 * 
	 * @param searchType
	 *            ：1 或者 2
	 * @return
	 */
	private Student searchStudent(int searchType) {

		String sql = "select flowid,type,idcard,examcard,studentname,location,grade from examstudent where ";
		Scanner scanner = new Scanner(System.in);

		// 1.根据输入的searchType，提示用户输入信息。
		// 若searchType为1，提示：请输入身份证号，若为2，提示输入准考证号
		// 2.根据searchType确定SQL
		if (searchType == 1) {
			System.out.println("请输入身份证号");
			String idCard = scanner.next();
			sql = sql + "idcard='" + idCard + "'";
		} else {
			System.out.println("请输入准考证号");
			String examCard = scanner.next();
			sql = sql + "examCard='" + examCard + "'";
		}

		// 3.执行查询
		Student student = getStudent(sql);

		// 4.若存在查询结果，把查询结果封装为Student对象
		return student;
	}

	/**
	 * 根据传入的SQL返回Student对象
	 * 
	 * @param sql
	 * @return
	 */
	private Student getStudent(String sql) {

		Student student = null;

		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			connection = JDBCTools.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			if (resultSet.next()) {
				student = new Student(resultSet.getInt(1), resultSet.getInt(2), resultSet.getString(3),
						resultSet.getString(4), resultSet.getString(5), resultSet.getString(6), resultSet.getInt(7));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCTools.release(resultSet, statement, connection);
		}

		return student;
	}

	/**
	 * 从控制台读入一个整数，确定要查询的类型
	 * 
	 * @return：1.用身份证查询。 2.用准考证号查询。 其他无效，并提示用户重新输入
	 */
	private int getSearchTypeFromConsole() {
		System.out.print("请输入查询类型：1.用身份证查询。 2.用准考证号查询");
		Scanner scanner = new Scanner(System.in);
		int type = scanner.nextInt();
		if (type != 1 && type != 2) {
			System.out.println("输入有误请重新输入！");
			throw new RuntimeException();
		}
		return type;
	}

	@Test
	public void testaddNewStudent() {
		Student student = getStudentFromConsole();
		this.addNewStudent2(student);
	}

	/**
	 * 从控制台输入学生的信息
	 * 
	 * @return
	 */
	private Student getStudentFromConsole() {
		Scanner scanner = new Scanner(System.in);
		Student student = new Student();

		System.out.print("FlowId:");
		student.setFlowId(scanner.nextInt());

		System.out.print("Type:");
		student.setType(scanner.nextInt());

		System.out.print("IdCard:");
		student.setIdCard(scanner.next());

		System.out.print("ExamCard:");
		student.setExamCard(scanner.next());

		System.out.print("StudentName:");
		student.setStudentName(scanner.next());

		System.out.print("Location:");
		student.setLocation(scanner.next());

		System.out.print("Grade:");
		student.setGrade(scanner.nextInt());

		return student;
	}

	public void addNewStudent(Student student) {
		// 1.准备一条SQL语句
		String sql = "insert into examstudent values(" + student.getFlowId() + "," + student.getType() + ",'"
				+ student.getIdCard() + "','" + student.getExamCard() + "','" + student.getStudentName() + "','"
				+ student.getLocation() + "'," + student.getGrade() + ")";
		System.out.println(sql);

		// 2.调用JDBCTools类的update(sql)方法执行插入操作
		JDBCTools.update(sql);
	}

}
