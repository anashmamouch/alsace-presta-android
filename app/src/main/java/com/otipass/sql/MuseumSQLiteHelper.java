package com.otipass.sql;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MuseumSQLiteHelper extends SQLiteOpenHelper {
	// use to make a singleton, only one instance class
	private static MuseumSQLiteHelper mInstance = null;

	private static final String TAG = "Alsace";
	public static final String DATABASE_NAME = "adt67.db";
	private static final int DATABASE_VERSION = 5;

	// Otipass table creation
	public static final String OTIPASS_TABLE = "otipass";
	public static final String OTI_COL_NUMOTIPASS = "numotipass";
	public static final String OTI_COL_SERIAL = "serial";
	public static final String OTI_COL_STATUS = "status";
	public static final String OTI_COL_EXPIRY = "expiry";
	public static final String OTI_COL_TYPE = "type";
	public static final String OTI_COL_PID = "pid";
	public static final String OTI_COL_SERVICE = "service";

	// modification plateforme tarification
	public static final String OTI_COL_OPTION = "option_id";
	public static final String OTI_COL_FARE = "fare_id";
	public static final String OPTION_TABLE = "option";
	public static final String OPTION_COL_ID = "idOption";
	public static final String OPTION_COL_NAME = "name";
	public static final String OPTION_COL_REF = "reference";
	public static final String FARE_TABLE = "fare";
	public static final String FARE_COL_ID = "idFare";
	public static final String FARE_COL_NAME = "name";
	public static final String FARE_COL_REF = "reference";

	private static final String OTIPASS_TABLE_CREATE = 
			"CREATE TABLE " + OTIPASS_TABLE
			+ " (" + OTI_COL_NUMOTIPASS + " INTEGER primary key, " 
			+ OTI_COL_SERIAL + " TEXT not null," 
			+ OTI_COL_STATUS + " INTEGER not null," 
			+ OTI_COL_EXPIRY + " TEXT not null,"
			+ OTI_COL_TYPE + " INTEGER not null," 
			+ OTI_COL_PID + " INTEGER not null," 
			+ OTI_COL_SERVICE + " TEXT not null,"
	// modification plateforme tarification
			+ OTI_COL_FARE + " INTEGER null,"
			+ OTI_COL_OPTION + " INTEGER null)";

	private static final String OTIPASS_TABLE_INDEX = 
			"CREATE INDEX otipass_serial_idx ON " + OTIPASS_TABLE + "(" + OTI_COL_SERIAL + ");" 
			;

	// User table creation
	public static final String USER_TABLE = "user";
	public static final String USER_COL_ID = "id";
	public static final String USER_COL_USERID = "userid";
	public static final String USER_COL_PWD = "password";
	public static final String USER_COL_SALT = "salt";
	public static final String USER_COL_PROFILE = "profile";

	private static final String USER_TABLE_CREATE = 
			"CREATE TABLE " + USER_TABLE
			+ " (" + USER_COL_ID + " INTEGER primary key, " 
			+ USER_COL_USERID + " TEXT not null," 
			+ USER_COL_PWD + " TEXT not null," 
			+ USER_COL_SALT + " TEXT not null," 
			+ USER_COL_PROFILE + " INTEGER not null)"
			;
	private static final String USER_TABLE_INDEX = 
			"CREATE INDEX user_login_idx ON " + USER_TABLE + "(" + USER_COL_USERID + ");" 
			;

	// Param table creation
	public static final String PARAM_TABLE = "param";
	public static final String PARAM_COL_ID = "id";
	public static final String PARAM_COL_NAME = "name";
	public static final String PARAM_COL_CALL = "call";
	public static final String PARAM_COL_SOFT = "soft";
	public static final String PARAM_COL_CATEGORY = "category";

	private static final String PARAM_TABLE_CREATE = 
			"CREATE TABLE " + PARAM_TABLE
			+ " (" + PARAM_COL_ID + " INTEGER primary key, " 
			+ PARAM_COL_NAME + " TEXT not null," 
			+ PARAM_COL_CALL + " TEXT not null," 
			+ PARAM_COL_SOFT + " TEXT not null,"
			+ PARAM_COL_CATEGORY+ " Integer not null)";
	;


	// Entry table creation
	public static final String ENTRY_TABLE = "entry";
	public static final String ENTRY_COL_ID = "id";
	public static final String ENTRY_COL_DATE = "date";
	public static final String ENTRY_COL_OTIPASS = "numotipass";
	public static final String ENTRY_COL_NB = "nb";
	public static final String ENTRY_COL_EVENT = "event";
	public static final String ENTRY_COL_SERVICE = "service";
	public static final String ENTRY_COL_UPLOADED = "uploaded";
	

	private static final String ENTRY_TABLE_CREATE = 
			"CREATE TABLE " + ENTRY_TABLE
			+ " (" + ENTRY_COL_ID + " INTEGER primary key, " 
			+ ENTRY_COL_DATE + " TEXT not null," 
			+ ENTRY_COL_OTIPASS + " INTEGER not null," 
			+ ENTRY_COL_NB + " INTEGER not null," 
			+ ENTRY_COL_EVENT + " INTEGER not null," 
			+ ENTRY_COL_UPLOADED + " INTEGER not null,"
			+ ENTRY_COL_SERVICE + " INTEGER not null)";
	;
	private static final String ENTRY_TABLE_INDEX = 
			"CREATE INDEX entry_serial_idx ON " + ENTRY_TABLE + "(" + ENTRY_COL_OTIPASS + ");" 
					+ "CREATE INDEX entry_date_idx ON " + ENTRY_TABLE + "(" + ENTRY_COL_DATE + ");"  
					;

	// Warning table creation
	public static final String WARNING_TABLE = "warning";
	public static final String WARNING_COL_ID = "id";
	public static final String WARNING_COL_DATE = "date";
	public static final String WARNING_COL_SERIAL = "serial";
	public static final String WARNING_COL_EVENT = "event";

	// Tablet table creation
	public static final String TABLET_TABLE = "state";
	public static final String TABLET_COL_ID = "id";
	public static final String TABLET_COL_NUMSEQUENCE = "numsequence";
	public static final String TABLET_COL_UPLOAD_TIME = "upload";
	public static final String TABLET_COL_DOWNLOAD_TIME = "download";
	public static final String TABLET_COL_BDD_MODEL = "model";
	private static final String TABLET_TABLE_CREATE =
			"CREATE TABLE " + TABLET_TABLE
			+ " (" + TABLET_COL_ID + " INTEGER primary key, " 
			+ TABLET_COL_NUMSEQUENCE + " INTEGER not null," 
			+ TABLET_COL_UPLOAD_TIME + " TEXT not null," 
			+ TABLET_COL_DOWNLOAD_TIME + " TEXT not null,"
	// modif plateforme tarification
			+ TABLET_COL_BDD_MODEL + " INTEGER DEFAULT 1)"
	;

	// modif plateforme tarification
	private static final String ALTER_TABLE_TABLET1 =
			"ALTER TABLE " + TABLET_TABLE
					+ " ADD COLUMN " + TABLET_COL_BDD_MODEL + " INTEGER DEFAULT 1"
			;

	// Update table creation
	public static final String UPDATE_TABLE           = "updates";
	public static final String UPDATE_COL_ID          = "id";
	public static final String UPDATE_COL_DATE        = "date";
	public static final String UPDATE_COL_TYPE        = "type";
	public static final String UPDATE_COL_NUMOTIPASS  = "numotipass";
	public static final String UPDATE_COL_PID         = "pid";
	public static final String UPDATE_COL_NAME        = "name";
	public static final String UPDATE_COL_FNAME       = "fname";
	public static final String UPDATE_COL_EMAIL       = "email";
	public static final String UPDATE_COL_POSTAL_CODE = "postal_code";
	public static final String UPDATE_COL_COUNTRY     = "country";
	public static final String UPDATE_COL_NEWSLETTER  = "newsletter";
	public static final String UPDATE_COL_TWIN        = "twin";
	public static final String UPDATE_COL_UPLOADED    = "uploaded";
	public static final String UPDATE_COL_FAREID      = "fare_id";
	public static final String UPDATE_COL_OPTIONID    = "option_id";


	private static final String UPDATE_TABLE_CREATE = 
			"CREATE TABLE " + UPDATE_TABLE
			+ " (" + UPDATE_COL_ID + " INTEGER primary key, " 
			+ UPDATE_COL_DATE + " TEXT not null," 
			+ UPDATE_COL_TYPE + " INTEGER not null," 
			+ UPDATE_COL_NUMOTIPASS + " INTEGER not null," 
			+ UPDATE_COL_PID + " INTEGER not null,"
			+ UPDATE_COL_NAME + " TEXT,"
			+ UPDATE_COL_FNAME + " TEXT," 
			+ UPDATE_COL_EMAIL + " TEXT," 
			+ UPDATE_COL_POSTAL_CODE + " TEXT," 
			+ UPDATE_COL_COUNTRY + " TEXT," 
			+ UPDATE_COL_NEWSLETTER + " INTEGER," 
			+ UPDATE_COL_TWIN + " INTEGER," 
			+ UPDATE_COL_UPLOADED + " INTEGER  not null DEFAULT 0,"
	// modif plateforme tarification
			+ UPDATE_COL_FAREID + " INTEGER null,"
			+ UPDATE_COL_OPTIONID + " INTEGER null)";
	;
	// modif plateforme tarification
	private static final String ALTER_TABLE_UPDATE1 =
			"ALTER TABLE " + UPDATE_TABLE
				+ " ADD COLUMN " + UPDATE_COL_FAREID + " INTEGER null;";
	private static final String ALTER_TABLE_UPDATE2 =
			" ALTER TABLE " + UPDATE_TABLE
				+ " ADD COLUMN " + UPDATE_COL_OPTIONID + " INTEGER null;";

	private static final String WARNING_TABLE_CREATE = 
			"CREATE TABLE " + WARNING_TABLE
			+ " (" + WARNING_COL_ID + " INTEGER primary key, " 
			+ WARNING_COL_DATE + " TEXT not null," 
			+ WARNING_COL_SERIAL + " TEXT not null," 
			+ WARNING_COL_EVENT + " INTEGER not null)"; 
	;

	// Stock table creation
	public static final String STOCK_TABLE = "stock";
	public static final String STOCK_COL_ID = "id";
	public static final String STOCK_COL_PROVIDER_ID = "provider_id";
	public static final String STOCK_COL_NB_CARDS = "nb_cards";
	public static final String STOCK_COL_THRESHOLD = "threshold";
	public static final String STOCK_COL_ALERT = "alert";

	private static final String STOCK_TABLE_CREATE = 
			"CREATE TABLE " + STOCK_TABLE
			+ " (" + STOCK_COL_ID + " INTEGER primary key, " 
			+ STOCK_COL_PROVIDER_ID + " INTEGER not null," 
			+ STOCK_COL_NB_CARDS + " INTEGER not null," 
			+ STOCK_COL_THRESHOLD + " INTEGER not null," 
			+ STOCK_COL_ALERT + " INTEGER not null)";
	;

	// Message table creation
	public static final String MSG_TABLE = "message";
	public static final String MSG_COL_ID = "id";
	public static final String MSG_COL_TEXT = "message";
	public static final String MSG_COL_LANG = "lang";
	public static final String MSG_COL_START_DATE = "start_date";
	public static final String MSG_COL_END_DATE = "end_date";

	private static final String MSG_TABLE_CREATE = 
			"CREATE TABLE " + MSG_TABLE
			+ " (" + MSG_COL_ID + " INTEGER primary key, " 
			+ MSG_COL_TEXT + " TEXT not null," 
			+ MSG_COL_LANG + " TEXT not null," 
			+ MSG_COL_START_DATE + " TEXT not null," 
			+ MSG_COL_END_DATE + " TEXT not null)";
	;

	// Discount table creation
	public static final String DISCOUNT_TABLE = "discount";
	public static final String DISCOUNT_COL_ID = "id";
	public static final String DISCOUNT_COL_AMOUNT_EUR = "eur";
	public static final String DISCOUNT_COL_AMOUNT_FCH = "fch";
	public static final String DISCOUNT_COL_START_DATE = "start_date";
	public static final String DISCOUNT_COL_END_DATE = "end_date";

	private static final String DISCOUNT_TABLE_CREATE = 
			"CREATE TABLE " + DISCOUNT_TABLE
			+ " (" + DISCOUNT_COL_ID + " INTEGER primary key, " 
			+ DISCOUNT_COL_AMOUNT_EUR + " REAL not null," 	      
			+ DISCOUNT_COL_AMOUNT_FCH + " REAL not null," 	      
			+ MSG_COL_START_DATE + " TEXT not null," 
			+ MSG_COL_END_DATE + " TEXT not null)";
	;

	// Service table creation
	public static final String SERVICE_TABLE      = "service";
	public static final String SERVICE_COL_ID     = "id";
	public static final String SERVICE_COL_TYPE   = "type";
	public static final String SERVICE_COL_NAME   = "name";
	

	private static final String SERVICE_TABLE_CREATE = 
			"CREATE TABLE " + SERVICE_TABLE
			+ " (" + SERVICE_COL_ID + " INTEGER primary key, " 
			+ SERVICE_COL_TYPE + " INTEGER not null," 	      
			+ SERVICE_COL_NAME + " TEXT not null)" ;

	// Package table creation 
	public static final String PACKAGE_TABLE          = "package";
	public static final String PACKAGE_COL_ID         = "id";
	public static final String PACKAGE_COL_NAME       = "name";
	public static final String PACKAGE_COL_CHILD      = "child";
	public static final String PACKAGE_COL_DURATION   = "duration";
	public static final String PACKAGE_COL_PERIOD     = "period";
	public static final String PACKAGE_COL_PRICE 	  = "price";
	public static final String PACKAGE_COL_REF 	  = "ref";

	private static final String PACKAGE_TABLE_CREATE = 
			"CREATE TABLE " + PACKAGE_TABLE
			+ " (" + PACKAGE_COL_ID + " INTEGER primary key, "
			+ PACKAGE_COL_NAME + " TEXT not null,"
			+ PACKAGE_COL_CHILD + " Integer not null,"
			+ PACKAGE_COL_DURATION + " Integer not null,"
			+ PACKAGE_COL_PERIOD + " Integer not null,"
			+ PACKAGE_COL_PRICE + " REAL not null,"
			+ PACKAGE_COL_REF + " TEXT not null)";

	private static final String ALTER_TABLE_PACKAGE_REF = 
			"ALTER TABLE " + PACKAGE_TABLE
			+ " ADD COLUMN " + PACKAGE_COL_REF + " TEXT";
	
	// Package service table creation
	public static final String PACKAGE_SERVICE_TABLE = "package_service";
	public static final String PACKAGE_SERVICE_COL_PACKAGE_ID = "package_id";
	public static final String PACKAGE_SERVICE_COL_SERVICE_ID = "service_id";
	public static final String PACKAGE_SERVICE_COL_NUMBER = "number";
	
	private static final String PACKAGE_SERVICE_TABLE_CREATE = 
			"CREATE TABLE " + PACKAGE_SERVICE_TABLE
			+ " (" + PACKAGE_SERVICE_COL_PACKAGE_ID + " INTEGER not null, "
			+ PACKAGE_SERVICE_COL_SERVICE_ID + " INTEGER not null,"
			+ PACKAGE_SERVICE_COL_NUMBER + " INTEGER not null,"
			+ " FOREIGN KEY (" + PACKAGE_SERVICE_COL_PACKAGE_ID + ") REFERENCES "+ PACKAGE_TABLE +" (" + PACKAGE_COL_ID + "),"
			+ " FOREIGN KEY (" + PACKAGE_SERVICE_COL_SERVICE_ID + ") REFERENCES "+ SERVICE_TABLE +" (" + SERVICE_COL_ID + "),"
			+ " PRIMARY KEY (" + PACKAGE_SERVICE_COL_PACKAGE_ID + " , " + PACKAGE_SERVICE_COL_SERVICE_ID +"))";
	
	// Provider service table creation
	public static final String PROVIDER_SERVICE_TABLE = "provider_service";
	public static final String PROVIDER_SERVICE_COL_PACKAGE_ID = "package_id";
	public static final String PROVIDER_SERVICE_COL_SERVICE = "service";
	
	private static final String PROVIDER_SERVICE_TABLE_CREATE = 
			"CREATE TABLE " + PROVIDER_SERVICE_TABLE
			+ " (" + PROVIDER_SERVICE_COL_PACKAGE_ID + " INTEGER not null, "
			+ PROVIDER_SERVICE_COL_SERVICE + " TEXT not null,"
			+ " PRIMARY KEY (" + PROVIDER_SERVICE_COL_PACKAGE_ID + " , " + PROVIDER_SERVICE_COL_SERVICE +"))";
	
	// Usage table creation
	public static final String USE_PASS_TABLE = "use_pass";
	public static final String USE_PASS_COL_USAGE_ID = "idUse";
	public static final String USE_PASS_COL_USAGE_OTIPASS = "numotipass";
	public static final String USE_PASS_COL_USAGE_DATE = "date";
	
	private static final String USE_PASS_TABLE_CREATE = 
			"CREATE TABLE " + USE_PASS_TABLE
			+ " (" + USE_PASS_COL_USAGE_ID + " INTEGER primary key AUTOINCREMENT, "
			+ USE_PASS_COL_USAGE_OTIPASS + " INTEGER not null,"
			+ USE_PASS_COL_USAGE_DATE + " TEXT not null)";
	
	// Wl table creation
	public static final String WL_TABLE = "wl";
	public static final String WL_COL_ID = "id";
	public static final String WL_COL_DATE = "date";
	public static final String WL_COL_NBSTEPS = "nbsteps";
	public static final String WL_COL_NBCARDS = "nbcards";
	public static final String WL_COL_NUMSEQUENCE = "numsequence";
	public static final String WL_COL_STATUS = "status";

	private static final String WL_TABLE_CREATE = 
			"CREATE TABLE " + WL_TABLE
			+ " (" + WL_COL_ID + " INTEGER primary key, " 
			+ WL_COL_DATE + " TEXT," 
			+ WL_COL_NBSTEPS + " INTEGER DEFAULT 0," 
			+ WL_COL_NBCARDS + " INTEGER DEFAULT 0," 
			+ WL_COL_NUMSEQUENCE + " INTEGER DEFAULT 0," 
			+ WL_COL_STATUS + " INTEGER DEFAULT 0)";
	;
	
	public static final String FLUSH_TABLE = "DELETE FROM ";


	/*----------------------------- Modifications pour plateforme tarification -------------------*/
	// Option table creation
	private static final String OPTION_TABLE_CREATE =
			"CREATE TABLE " + OPTION_TABLE
					+ " (" + OPTION_COL_ID + " INTEGER primary key, "
					+ OPTION_COL_NAME + " TEXT,"
					+ OPTION_COL_REF + " TEXT)"
	;

	// Fare table creation
	private static final String FARE_TABLE_CREATE =
			"CREATE TABLE " + FARE_TABLE
					+ " (" + FARE_COL_ID + " INTEGER primary key, "
					+ FARE_COL_NAME + " TEXT,"
					+ FARE_COL_REF + " TEXT)"
			;

	// Packages Prices table creation
	public static final String PACKAGE_FARE_TABLE = "package_fare";
	public static final String PACKAGE_FARE_COL_PACKAGE_ID = "package_id";
	public static final String PACKAGE_FARE_COL_FARE_ID = "fare_id";
	public static final String PACKAGE_FARE_COL_OPTION_ID = "option_id";
	public static final String PACKAGE_FARE_COL_PRICE = "price";
	private static final String PACKAGE_FARE_TABLE_CREATE =
			"CREATE TABLE " + PACKAGE_FARE_TABLE
					+ " (" + PACKAGE_FARE_COL_PACKAGE_ID + " INTEGER not null, "
					+ PACKAGE_FARE_COL_FARE_ID + " INTEGER not null,"
					+ PACKAGE_FARE_COL_OPTION_ID + " INTEGER not null,"
					+ PACKAGE_FARE_COL_PRICE + " double not null default 0,"
					+ " FOREIGN KEY (" + PACKAGE_FARE_COL_PACKAGE_ID + ") REFERENCES "+ PACKAGE_TABLE +" (" + PACKAGE_COL_ID + "),"
					+ " FOREIGN KEY (" + PACKAGE_FARE_COL_FARE_ID + ") REFERENCES "+ FARE_TABLE +" (" + FARE_COL_ID + "),"
					+ " PRIMARY KEY (" + PACKAGE_FARE_COL_PACKAGE_ID + " , " + PACKAGE_FARE_COL_FARE_ID + " , " + PACKAGE_FARE_COL_OPTION_ID + "))";


	private static final String ALTER_TABLE_OTIPASS1 =
			"ALTER TABLE " + OTIPASS_TABLE
					+ " ADD COLUMN " + OTI_COL_FARE + " INTEGER DEFAULT NULL;";
	private static final String ALTER_TABLE_OTIPASS2 =
			" ALTER TABLE " + OTIPASS_TABLE
					+ " ADD COLUMN " + OTI_COL_OPTION + " INTEGER DEFAULT NULL;";

	private static final String OTIPASS_TABLE_INDEX2 =
			"CREATE INDEX otipass_fare_idx ON " + OTIPASS_TABLE + "(" + OTI_COL_FARE + "); " +
			" CREATE INDEX otipass_option_idx ON " + OTIPASS_TABLE + "(" + OTI_COL_OPTION + ");"
			;

	// use this function to call an instance 
	public static MuseumSQLiteHelper getInstance(Context ctx) {
		if (mInstance == null) {      
			mInstance = new MuseumSQLiteHelper(ctx.getApplicationContext());    
		}    
		return mInstance;  
	}

	// private constructor to use only getInstance() when creating an instance
	private MuseumSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			// otipass
			db.execSQL(OTIPASS_TABLE_CREATE);
			db.execSQL(OTIPASS_TABLE_INDEX);
			// user
			db.execSQL(USER_TABLE_CREATE);
			db.execSQL(USER_TABLE_INDEX);
			// param
			db.execSQL(PARAM_TABLE_CREATE);
			// entry
			db.execSQL(ENTRY_TABLE_CREATE);
			db.execSQL(ENTRY_TABLE_INDEX);
			// warning
			db.execSQL(WARNING_TABLE_CREATE);
			// update
			db.execSQL(UPDATE_TABLE_CREATE);
			// state
			db.execSQL(TABLET_TABLE_CREATE);
			// stock
			db.execSQL(STOCK_TABLE_CREATE);
			// message
			db.execSQL(MSG_TABLE_CREATE);
			// service
			db.execSQL(SERVICE_TABLE_CREATE);
			// package
			db.execSQL(PACKAGE_TABLE_CREATE);
			// package_service
			db.execSQL(PACKAGE_SERVICE_TABLE_CREATE);
			// provider_service
			db.execSQL(PROVIDER_SERVICE_TABLE_CREATE);
			// usage 
			db.execSQL(USE_PASS_TABLE_CREATE);
			// wl 
			db.execSQL(WL_TABLE_CREATE);
			// fare and option
			db.execSQL(FARE_TABLE_CREATE);
			db.execSQL(OPTION_TABLE_CREATE);
			db.execSQL(PACKAGE_FARE_TABLE_CREATE);
			db.execSQL(OTIPASS_TABLE_INDEX2);
		} catch (Exception e) {
			Log.e(TAG, MuseumSQLiteHelper.class.getName() + " - " + e.getMessage());			
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "onUpgrade SQL: old="+oldVersion+" new="+newVersion);
		if (oldVersion <3) {
			try {
				db.execSQL(ALTER_TABLE_PACKAGE_REF);
			} catch (Exception e) {
				Log.e(TAG, MuseumSQLiteHelper.class.getName() + " - " + e.getMessage());
			} 
		}
		if (oldVersion < 4) {
			db.execSQL(WL_TABLE_CREATE);
		}
		if (oldVersion < 5) {
			try {
				db.execSQL(FARE_TABLE_CREATE);
				db.execSQL(OPTION_TABLE_CREATE);
				db.execSQL(PACKAGE_FARE_TABLE_CREATE);
				db.execSQL(ALTER_TABLE_OTIPASS1);
				db.execSQL(ALTER_TABLE_OTIPASS2);
				db.execSQL(OTIPASS_TABLE_INDEX2);
				db.execSQL(ALTER_TABLE_TABLET1);
				db.execSQL(ALTER_TABLE_UPDATE1);
				db.execSQL(ALTER_TABLE_UPDATE2);
			} catch (Exception e) {
				Log.e(TAG, MuseumSQLiteHelper.class.getName() + " - " + e.getMessage());
			}
		}
	}

}

