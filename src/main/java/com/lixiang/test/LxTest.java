package com.lixiang.test;

public class LxTest {

    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("");

    }

    public void test01() {
        System.out.println(3 >> 1);
    }

}
