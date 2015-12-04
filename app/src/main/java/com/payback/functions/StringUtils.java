package com.payback.functions;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	
public static String replaceWords(String phoneNumber){
		
		String added_phoneNo =   phoneNumber.replace(" ","").replace("+","").replace("-","").replace("(","").replace(")","");
//		if(added_phoneNo.length() > 10) {
//			added_phoneNo = added_phoneNo.substring(added_phoneNo.length() - 10);
//			
//		}
		return added_phoneNo;
		
		
		
	}


	public static boolean isValidPassword(final String password) {

		Pattern pattern;
		Matcher matcher;

    	final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{6,}$";

    	pattern = Pattern.compile(PASSWORD_PATTERN);
    	matcher = pattern.matcher(password);

    	return matcher.matches();

	}

	public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate){

		Date parsed = null;
		String outputDate = "";

		SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
		SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

		try {
			parsed = df_input.parse(inputDate);
			outputDate = df_output.format(parsed);

		} catch (ParseException e) {
			Log.e("TAG Date", "ParseException - dateFormat");
		}

		return outputDate;

	}


}
