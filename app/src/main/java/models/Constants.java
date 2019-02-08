package models;


public class Constants {
	public static final String plateform = "www";
//	public static final String plateform = "test";
//	public static final String plateform = "test2";
	public static final String ADTPlateform = "https://" + plateform;
	public static final String OTIPASS_DOMAIN = "otipass.net";
    public static final String OTIPASS2_DOMAIN = "otipass.net";

	// tablet models
	public static final String NEXUS_7 = "Nexus 7";
	public static final String SHIELD = "SHIELD Tablet K1";
	public static final String FAMOCO = "FX100,7";
	public static final String Blackview = "BV9000Pro";

	public static final int OTI_ORDERED = 1;
	public static final int OTI_CONFIRMED = 2;
	public static final int OTI_CREATED = 3;
	public static final int OTI_SENT = 4;
	public static final int OTI_DELIVERED = 5;
	public static final int OTI_VALID = 6;
	public static final int OTI_INVALID = 7;
	public static final int OTI_EXPIRED = 8;
	public static final int OTI_AWAITING_RENEWAL = 9;
	public static final int OTI_INACTIVE = 10;
	public static final int OTI_ACTIVE = 11;	

	public static final int PASS_CREATED = 1;
	public static final int PASS_INACTIVE = 2;
	public static final int PASS_ACTIVE = 3;
	public static final int PASS_EXPIRED = 4;
	public static final int PASS_INVALID = 5;
	public static final int PASS_UNDEFINED = 6;

	public static final int PASS_BLOCKED = 6;
	public static final int PASS_ALREADY_CHECKED = 7;
	public static final int PASS_OUT_OF_SYSTEM = 8;
	public static final int PASS_DETECTED_EXPIRED = 9;

	public static final String USER_ID_TAG = "iduser";

	// user profiles
	public static final int USR_OTIPASS_SUPERADMIN = 12;
	public static final int USR_OTIPASS_ADMIN = 11;
	public static final int USR_ISSUER_ADMIN = 10;
	public static final int USR_ISSUER_OP = 9;
	public static final int USR_SITES_MANAGER = 8;
	public static final int USR_MANAGER = 7;
	public static final int USR_STOCK_MANAGER = 6;
	public static final int USR_CASHIER = 5;
	public static final int USR_CONTROLLER = 4; 
	public static final int USR_PROVIDER = 3;
	public static final int USR_CLIENT = 2;
	public static final int USR_BUYER = 1;
	public static final int USR_GUEST = 0;

	// user categories
	public static final int USR_PROVIDER_CATEGORY     = 3;
	public static final int USR_POS_CATEGORY          = 4;
	public static final int USR_PROVIDER_POS_CATEGORY = 5;


	// date formats
	public static final String SQL_FULL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String FULL_DATE_FORMAT_FR = "dd/MM/yyyy HH:mm:ss";
	public static final String DATE_FORMAT_FR_NOSEC = "dd/MM/yyyy HH:mm";
	public static final String EN_DATE_FORMAT = "yyyy-MM-dd";
	public static final String FR_DATE_FORMAT = "dd/MM/yyyy";
	public static final String DE_DATE_FORMAT = "dd.MM.yyyy";
	public static final String EXPIRY_DATE = " 23:59:59";
	public static final String DAY_DATE ="dd";
	public static final String TIME_FORMAT = "HH:mm:ss";

	// Pass types
	public static final int PASS_TEST = 0;
	public static final int PASS_INITIAL = 1;
	public static final int PASS_FREE = 2;
	public static final int PASS_SHORT_DURATION = 3;
	public static final int PASS_OLD_PASS = 4;
	public static final int PASS_MARKETING = 7;

	// entry types
	public static final int ENTRY_MUSEUM = 1;
	public static final int ENTRY_PARTNER = 2;
	public static final int ENTRY_EVENT = 3;

	// OTHER FUNCTIONS types 
	public static final int FUNC_RENEW = 1;
	public static final int FUNC_EXPIRED = 2;
	public static final int FUNC_SHORT_DURATION = 3;
	public static final int FUNC_OLD_PASS = 4;
	public static final int FUNC_SCAN_MANUALLY = 5;
	public static final int FUNC_EXCHANGE = 6;
	public static final int FUNC_MANUALLY_SCANNED = 7;
	public static final int FUNC_STOCK_CMD = 8;
	public static final int FUNC_ENTRY = 9;
	public static final int FUNC_SALE = 10;
	public static final int FUNC_NEW_PASS = 11;
	public static final int FUNC_ENTER_DISCOUNT = 12;
	public static final int FUNC_CARD_UPDATED = 13;
	public static final int FUNC_CARD_DENIED = 14;

	// Otipass ID for Short duration Pass
	public static final int SHORT_DURATION_PASS_ID = 1;	

	// Otipass ID for old Pass
	public static final int OLD_PASS_ID = 1;	

	// flag for complete download
	public static final int WL_DONE = 10000;

	public static final String TAG = "adt67 ";

	// for the alarm service
	public static final String WAKE_UP_STR = "WAKE_UP";
	public static final String ALARM_UPLOAD_STR = "UPLOAD";
	public static final String ALARM_DOWNLOAD_STR = "DOWNLOAD";
	public static final String ALARM_SYNCHRO_PERIOD = "alarm_synchro_period";
	public static final String ALARM_UP_PERIOD = "alarm_up_period";

	// timeout for the sale processes
	public static final int timeoutProcess = 60000;

	// SERVICE'S ACTIONS
	public static final String SEND_ENTRY         = "send_entry";
	public static final String SEND_UPDATES       = "send_updates";
	public static final String GET_MESSAGES       = "get_messages";
	public static final String GET_SOFTWARE       = "get_software";
	public static final String GET_PARTIAL_WL     = "get_partial_wl";
	public static final String DO_PARTIAL_SYNCHRO = "do_partial_synchro";
	public static final String DO_TOTAL_SYNCHRO   = "do_total_synchro";
	public static final String DO_INIT            = "do_init";
	public static final String CHECK_OTIPASS      = "check_otipass";
	public static final String DO_NIGHT_DOWNLOAD  = "do_night_download";
	public static final String DO_NIGHT_UPLOAD    = "do_night_upload";
	public static final String DO_PERIODIC_SYNCHRO= "do_periodic_synchro";
	public static final String CLOSE_SUPPORT      = "close_support";
	

	// fragment tag
	public static final String SCAN_FRAGMENT_TAG  = "scan_fragment";
	public static final String MENU_FRAGMENT_TAG  = "menu_fragment";
	public static final String BASE_FRAGMENT_TAG  = "base_fragment";

	// bundle key
	public static final String OTIPASS_CARD_KEY   = "otipass_card";
	public static final String COUNTRY_KEY        = "country_key";
	public static final String MESSAGE_KEY        = "message_key";
	public static final String PASS_KO_KEY        = "pass_ko_key";
	public static final String PASS_OK_KEY        = "pass_ok_key";
	public static final String TEXT_FOOTER_KEY    = "text_footer_key";
	public static final String EXTERN_CALL_KEY    = "extern";

	// Person key
	public static final String PERSON_KEY         = "key";

	// Sharedpreferences key
	public static final String PACKAGE_ID_KEY     = "package_id_key";
	public static final String SCAN_TYPE_KEY      = "scan_type_key";
	public static final String RAPID_MODE_KEY     = "rapid_mode_key";
	public static final String CATEGORY_KEY       = "category_key";
	//public static final String HEADER_KEY         = "header_key";   
	public static final String TWIN_KEY           = "twin_key";
	public static final String USER_KEY			  = "user_key";
	public static final String NUMOTIPASS_KEY     = "numotipass_key";   
	public static final String SERVICE_KEY        = "service_key";
	public static final String PERIODIC_CALL      = "periodic_call";
	public static final String ID_PARENT_KEY      = "idparent_key";
	public static final String ID_REQUEST_KEY     = "id_request_key";
	public static final String ENTRY_KEY     = "entry_key";
	public static final String ENTRY_KEY_TXT     = "entry_key_txt";
    public static final String DOMAIN_KEY     = "domain_key";
	public static final String DB_CHANGED__KEY     = "db_changed_key";
	public static final String NEW_TARIFICATION__KEY     = "new_tarification_key";
	public static final String FARE_ID_KEY     = "fare_id_key";
	public static final String OPTION_ID_KEY     = "option_id_key";

	// Categories those are used by user profile !
	public static final int POS_CATEGORY          = 5;
	public static final int PROVIDER_CATEGORY     = 4;
	public static final int POS_PROVIDER_CATEGORY = 6;

	// those reflect the establishment category
	public static final int PROVIDER_ESTABLISHMENT     = 3;
	public static final int POS_ESTABLISHMENT          = 4;
	public static final int POS_PROVIDER_ESTABLISHMENT = 5;

	// Scan type 
	public static final String SCAN_ENTRY         = "scan_entry";
	public static final String SCAN_SALE          = "scan_sale";

	// Entry confirmation key
	public static final String ENTRY_CONFIRM_KEY  = "confirm_entry";

	// Forced Entry key
	public static final String FORCED_ENTRY_KEY   = "forced_entry_key";

	// Cancel last action
	public static final String CANCEL_LAST_ACTION = "cancel_last_action"; 
	public static final String CANCEL_ACTION_KO   = "cancel_action_ko";  
	public static final String CANCEL_ACTION_OK   = "cancel_action_ok";  

	// Entry's values
	public static final int NORMAL_ENTRY          = 1;
	public static final int FORCED_ENTRY          = 2;

	// Navigation drawer list POS_PROVIDER
	public static final int HOME                  = 1;
	public static final int ENTRY_SALE            = 3;
	public static final int STAT_ENTRIES          = 5;
	public static final int STAT_SALES            = 6;
	public static final int ORDER_STOCK           = 8;
	public static final int INITIALIZATION        = 10;
	public static final int SYNCHRONIZATION       = 11;
	public static final int CANCEL_OPERATION      = 12;
	public static final int CONFIGURATION         = 12;

	// Navigation drawer list POS
	public static final int HOME_POS              = 1;
	public static final int SALE_POS              = 3;
	public static final int STAT_SALES_POS        = 5;
	public static final int ORDER_STOCK_POS       = 7;
	public static final int INITIALIZATION_POS    = 9;
	public static final int SYNCHRONIZATION_POS   = 10;
	public static final int CANCEL_OPERATION_POS  = 11;
	public static final int CONFIGURATION_POS     = 12;

	// Navigation drawer list PROVIDER
	public static final int HOME_PROVIDER            = 1;
	public static final int ENTRY_PROVIDER           = 3;
	public static final int STAT_ENTRIES_PROVIDER    = 5;
	public static final int INITIALIZATION_PROVIDER  = 7;
	public static final int SYNCHRONIZATION_PROVIDER = 8;
	public static final int CANCEL_OPERATION_PROVIDER= 9;
	public static final int CONFIGURATION_PROVIDER   = 12;

	// web action
	public static final String ACTION_KEY   = "ACTION_KEY";
	public static final String WEB_ENTRY_ACTION   = "ENTRY_STATS";
	public static final String WEB_SALE_ACTION   = "SALE_STATS";
	public static final String WEB_CMD_ACTION   = "PASS_CMD";

	// nb use pass authorized
	public static final int NB_USE_AUTHORIZED     = 3;

	// image key
	public static final String IMG_HEADER_KEY     = "img_header_key";
	public static final String IMG_FOOTER_KEY     = "img_footer_key";

	// scan sale key
	public static final String SCAN_SALE_KEY      = "scan_sale_key";

	// Text size dynamic button
	public static final float TEXT_SIZE_BUTTON    = 35;

	// Another sale key
	public static final String ANOTHER_SALE_KEY   = "another_sale";

	// Personal Info key
	public static final String PI_NAME_KEY        = "pi_name_key";
	public static final String PI_FIRST_NAME_KEY  = "pi_first_name_key";
	public static final String  PI_EMAIL_KEY      = "pi_email_key";
	public static final String PI_PC_KEY          = "pi_pc_key";
	public static final String PI_COUNTRY_KEY     = "pi_country_key";
	public static final String PI_NEWSLETTER_KEY  = "pi_newsletter_key";

	// URL key
	public static final String URL_KEY            = "url_key";

	// updates type
	public static final int UPD_CANCEL            = 8;
	public static final int UPD_CANCEL_ENTRY      = 10;

	// action (entry or sale) type
	public static final int ENTRY_TYPE 			  = 1;
	public static final int SALE_TYPE  			  = 2;

	// cancel delay
	public static final int CANCEL_DELAY          = 15;

	// synchronization service period
	public static final int SYNCHRO_PERIOD        = 2;


	
}
