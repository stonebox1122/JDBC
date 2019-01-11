package com.stone.jdbc;

import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import org.junit.Test;

public class DAOTest {
	
	DAO dao = new DAO();
	DAO2 dao2 = new DAO2();

	@Test
	public void testUpdate() {
		String sql = "insert into customers(name,email,birth) values(?,?,?)";
		dao.update(sql, "tom", "tom@stone.com", new java.sql.Date(new Date().getTime()));
	}

	@Test
	public void testGet() {
		String sql = "select flow_id flowId,type,id_card idCard,exam_card examCard,student_name studentName,location,grade from examstudent2 where flow_id=?";
		Student student = dao2.get(Student.class, sql, 1);
		System.out.println(student);
	}

	@Test
	public void testGetForList() {
		String sql = "select flow_id flowId,type,id_card idCard,exam_card examCard,student_name studentName,location,grade from examstudent2";
		List<Student> students = dao2.getForList(Student.class, sql);
		System.out.println(students);
	}

	@Test
	public void testGetForValue() {
		String sql = "select exam_card examCard from examstudent2 where flow_id=?";
		String examCard = dao2.getForValue(sql, 1);
		System.out.println(examCard);
	}

}
