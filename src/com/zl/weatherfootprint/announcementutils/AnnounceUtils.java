package com.zl.weatherfootprint.announcementutils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解方法类
 * @author ZhongLeiDev 2017/8/24
 *
 */
public class AnnounceUtils {
	
	@Target(ElementType.METHOD)//作用于方法
	@Retention(RetentionPolicy.RUNTIME)//在运行时有效（即运行时保留)
	public @interface TestPerson
	{
	  //name是属性而不是方法，gavin是它的默认值，在定义的时候可以不用给定默认值
	  String name() default "gavin";
	}
	 
	@Target(ElementType.TYPE)//作用于类上
	@Retention(RetentionPolicy.RUNTIME)//在运行时有效（即运行时保留)
	public @interface ClassFunction
	{
	  String value() default "";
	}
	
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface InjectView 
	{
	 //id就是控件id，在某一个控件上使用注解标注其id
	  int id() default -1;
	}

}
