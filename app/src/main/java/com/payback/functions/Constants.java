package com.payback.functions;

public class Constants {


	public static String USER_ID ;
	public static String EMAIL ;
	public static String USERNAME ;
	public static String TOKENID = "payback" ;
	public static String PROFILEPIC;
	public static String CITY;
	public static String STATE;
	public static String ZIP;

	public static Boolean IS_USER_LOGIN;
	public static String NO_INTERNET = "No Internet Connection";
	public static String ERROR_MSG = "Something went wrong. Please try after sometime.";
	public static  String NAME;
	public static  String PHONE;
	public static  String ADDRESS;

	public static final String SENDER_ID = "972081743066" ;
	public static final String NotifyId = "NotifyID" ;
	public static final String APP_VERSION = "app_version" ;

	public static int connection_timeout=40*1000;
	public static final String LoginURL = "https://phphosting.osvin.net/paybackApp/index.php/api/user/login";
	public static final String RegisterUrl = "https://phphosting.osvin.net/paybackApp/index.php/api/user/signup";
	public static final String ForgotURL = "https://phphosting.osvin.net/paybackApp/index.php/api/user/forgotpassword";
	public static final String ChangeURL = "https://phphosting.osvin.net/paybackApp/index.php/api/user/changepassword";
	public static final String LogoutUrl = "https://phphosting.osvin.net/paybackApp/index.php/api/user/logout";
	public static final String RequestLendURL = "https://phphosting.osvin.net/paybackApp/index.php/api/user/sendLendMoneyRequest";
	public static final String ReceiveUrl = "https://phphosting.osvin.net/paybackApp/index.php/api/user/logout";
	public static final String CCUrl = "https://phphosting.osvin.net/paybackApp/index.php/api/user/addccinfo";
	public static final String LendlistURL = "https://phphosting.osvin.net/paybackApp/index.php/api/user/allLendMoneyRequestToMe";
	public static final String RequestlistURL = "https://phphosting.osvin.net/paybackApp/index.php/api/user/LendMoneyRequestByMe";
	public static final String UpdateProfileURL = "https://phphosting.osvin.net/paybackApp/index.php/api/user/updateprofile";
	public static final String ActionURL = "https://phphosting.osvin.net/paybackApp/index.php/api/user/approveLendMoneyRequest";


}
