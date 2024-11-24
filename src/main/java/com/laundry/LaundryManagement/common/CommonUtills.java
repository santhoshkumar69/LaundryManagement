/**
 * 
 */
package com.laundry.LaundryManagement.common;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.laundry.LaundryManagement.model.GlobalInfo;
import com.laundry.LaundryManagement.model.UserInfo;

/**
 * @author pandyarajan
 *
 * 26-Dec-2018
 */
public class CommonUtills {

	public static <T> void addDefaultCreateInfos(Object dtoObject,Class<?> classType, GlobalInfo globalInfo){
		try {
			Field field = null;
			Class<?> classHandle  = Class.forName(classType.getCanonicalName());
			field = classHandle.getDeclaredField("createdDate");
			field.setAccessible(true);
			field.set(dtoObject, new Date());
			
			field = classHandle .getDeclaredField("createdBy");
			field.setAccessible(true);
			if(globalInfo != null) {
				field.set(dtoObject, globalInfo.getUserName());
				field.set(dtoObject, globalInfo.getStatus());
			}
			
			field= classHandle.getDeclaredField("activeStatus");
			field.setAccessible(true);
			
			
		   //field = classHandle.getDeclaredField("userId");
		   //field.setAccessible(true);
		   //field.set(dtoObject, globalInfo.getUserId());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addDefaultUpdateInfos(Object dtoObject, Class<?> classType, GlobalInfo globalInfo) {
		try {
			Field field = null;
			
			Class<?> classHandle  = Class.forName(classType.getCanonicalName());
			field = classHandle .getDeclaredField("updatedDate");
			field.setAccessible(true);
			field.set(dtoObject, new Date());
			
			field = classHandle .getDeclaredField("updatedBy");
			field.setAccessible(true);
//			field.set(dtoObject, globalInfo.getUserName());
			
//			field = classHandle.getDeclaredField("userId");
//			field.setAccessible(true);
//			field.set(dtoObject, globalInfo.getUserId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static GlobalInfo createSessionObject(UserInfo userinfo) {
		if(userinfo!=null)
		 return new GlobalInfo.Builder().setUserId(userinfo.getUserId())
									.setUserName(userinfo.getUserName())
									.setUserRole(userinfo.getRole())
									.setLocationID(userinfo.getLocationID())
									.build();
		else return new GlobalInfo.Builder().build();
	}
	
	public static Date convertStringToDate(String dateString,String dateFormat) {
		try {
		SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
			return sf.parse(dateString);
		} catch (ParseException e) {
			System.out.println("invalid date format:"+e.getMessage());
			return new Date();
		}
	}
	

}
