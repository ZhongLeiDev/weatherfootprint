package com.zl.weatherfootprint.announcementutils;

import java.lang.reflect.Field;

import com.zl.weatherfootprint.announcementutils.AnnounceUtils.InjectView;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

/**
 * 注解解析类
 * @author ZhongLeiDev 2017/8/24
 *
 */
public class InjectMethods {
	
	/**
	 * 以注解的方式将控件自动加载到 Activity 中
	 * @param activity
	 */
	public static void autoInjectAllField(Activity activity)
	  {
		//得到Activity对应的Class
	    Class<? extends Context> clazz=activity.getClass();
	  //得到该Activity的所有字段
	    Field []fields=clazz.getDeclaredFields();
	    Log.v("autoInjectAllField", "fields size-->"+fields.length);
	    for(Field field :fields)
	    {
	    	//判断字段是否标注InjectView
	      if(field.isAnnotationPresent(InjectView.class))
	      {
	        Log.v("autoInjectAllField", "is injectView");
	      //如果标注了，就获得它的id
	        InjectView inject=field.getAnnotation(InjectView.class);
	        int id=inject.id();
	        Log.v("autoInjectAllField", "id--->"+id);
	        if(id>0)
	        {
	        	//反射访问私有成员，必须加上这句
	          field.setAccessible(true);
	        //然后对这个属性复制
	          try {
				field.set(activity, activity.findViewById(id));//等效于activity.filedName = activity.findViewById(id)
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        }
	      }
	    }
	  }

}
