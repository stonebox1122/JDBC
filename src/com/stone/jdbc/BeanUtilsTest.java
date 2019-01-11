package com.stone.jdbc;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;

public class BeanUtilsTest {
	
	@Test
	public void testGetProperty() throws Exception {
		Object object = new Student();
		System.out.println(object);
		
		BeanUtils.setProperty(object, "idCard", "11111");
		System.out.println(object);
		
		String property = BeanUtils.getProperty(object, "idCard");
		System.out.println(property);
	}
	
	/**
	 * 在JavaEE中，Java类的属性通过getter，setter来定义：
	 * 对getter（或setter）方法去除get（或set）后，第一个字母小写即为Java类的属性。
	 * 通过工具包beanutils来操作Java类的属性。
	 * @throws Exception
	 */
	@Test
	public void testSetProperty() throws Exception {
		
		Object object = new Student();
		System.out.println(object);
		
		BeanUtils.setProperty(object, "idCard", "11111");
		System.out.println(object);
	}
}
