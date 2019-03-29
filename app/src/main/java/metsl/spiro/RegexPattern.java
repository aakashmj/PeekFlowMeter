package metsl.spiro;

public class RegexPattern {

	public static String PasswordPattern = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{7,20})";//"((?=.*[a-z])(?=.*d)(?=.*[@#$%])(?=.*[A-Z]).{7,16})";// "^.*(?=.{7,})(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).*$";
	public static String FullnamePattern = "^[a-zA-Z0-9 \\s.\\-_',]+$";// "^([A-Z]+[a-zA-Z0-9 ]*)(\\s|\\-)?([A-Z]+[a-zA-Z]*)?(\\s|\\-)?([A-Z]+[a-zA-Z]*)?$";//
	// "^[a-zA-Z0-9]+$";
	public static String MobilenumberPattern = "^[0-9]{10,11}";// "^[0-9]{10,11}";
																// //
	public static String TelephoneNumberPattern = "^(\\d{8}|\\d{11})$"; // "^\\+(?:[0-9] ?){6,14}[0-9]$";
	public static String IPAddressPattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	public static String EmailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	public static String AgePattern = "0*([0-9]|[1-9][0-9]|100)";// "0*([1-9][0-9]|100)";
	public static String PhonenumberPattern = "^(\\d{8}|\\d{10}|\\d{11})$"; // //
	// "^(1[0-1]|[2-5][0-9]|6[0-5])$";
	// for 10 to 100
	// "^([1-9][0-9]|100) and for 1 to 100 ^[0-9]{1,2}$";
	public static String EmptyPattern = "\\A\\S+\\Z";
	// public static String BPPattern ="^\\d{1,3}\\/\\d{1,3}$"
	public static String MarriedyearsPattern = "^[1-9][0-9]?$|^100$";
	public static String MarriedmonthsPattern = "^(\\d|0*(1[0-2]))$";// "^[1-9][0-9]?$|^11$";
	public static String PregnancyAgePattern = "0*[0-9][0-9]?$|^99$";
	public static String SystolicPattern = "0*([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|250)$";
	public static String DiastolicPattern = "0*([0-9]{1,2}|1[0-4][0-9]|150)";// ^[1-9][0-9]?$|^150$";
	public static String BirthWeightPattern = "0*([0-9]{1,2}|1[0-4][0-9]|99)";
	public static String ChildAge = "^[0-9]{1,2}$";
	public static String DecimalVisits = "^[0-9]{1,2}+(\\.[0-9]{1,2})?$";
	public static String DatePattern = "\\b((J(an|un|ul))|Feb|Mar|(A(pr|ug))|May|Sep|Nov|Dec|Oct)\\s*(0?[1-9]|1[0-9]|2[0-9]|3[0-1])\\s*(\\,)\\s{1}\\d{4}$";// "^(Jan|Feb|Mar|Apr|May|Jun|Jul|Apr|Sep|Oct|Nov|Dec)\\s{1}\\d{2}\\s{1}\\d{4}$";
	public static String WeightPattern = "(?x)\\A(?:0*(?:300|[12][0-9]{2}|[1-9]?[0-9])(?:\\.[0-9]{1,2})?)\\z";// "^300([.][0]{1,3}|300)?$|^\\d{1,3}$|^\\d{1,3}([.]\\d{1,3}|300)$|^([.]\\d{1,3}|300)$";//"\\s*(0[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-9][0-9]|300)\\s*(\\.[0-9]{1,3})?$";//"([1-9]\\d?|[12]\\d{2}|3[0-5]\\d)(\\.\\d{1,2})?";//
	public static String EmptyWithCharacterPattern = "^[^^]+$";
	
}
