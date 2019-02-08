/**
================================================================================

    OTIPASS
    Pass Museum Application.

    SQLite package

    @copyright Copyright (c) Otipass 2011. All rights reserved.
    @author ED ($Author: ede $)

    @version $Rev: 4379 $
    $Id: MuseumDbAdapter.java 4379 2014-11-27 15:32:45Z ede $

================================================================================
 */
package com.otipass.sql;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import models.Constants;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.otipass.tools.EntrySale;
import com.otipass.tools.PersonalInfo;
import com.otipass.tools.tools;

public class MuseumDbAdapter {
	private static final String TAG = "Pass Alsace";
	private SQLiteDatabase database;
	private MuseumSQLiteHelper dbHelper;
	private String[] allOtipassColumns = { 
			MuseumSQLiteHelper.OTI_COL_NUMOTIPASS,
			MuseumSQLiteHelper.OTI_COL_SERIAL,
			MuseumSQLiteHelper.OTI_COL_STATUS,
			MuseumSQLiteHelper.OTI_COL_EXPIRY,
			MuseumSQLiteHelper.OTI_COL_TYPE,
			MuseumSQLiteHelper.OTI_COL_SERVICE,
			MuseumSQLiteHelper.OTI_COL_PID,
			MuseumSQLiteHelper.OTI_COL_FARE,
			MuseumSQLiteHelper.OTI_COL_OPTION
	};
	private String[] allUserColumns = { 
			MuseumSQLiteHelper.USER_COL_ID,
			MuseumSQLiteHelper.USER_COL_USERID,
			MuseumSQLiteHelper.USER_COL_PWD,
			MuseumSQLiteHelper.USER_COL_SALT,
			MuseumSQLiteHelper.USER_COL_PROFILE,
	};
	private String[] allEntryColumns = { 
			MuseumSQLiteHelper.ENTRY_COL_ID,
			MuseumSQLiteHelper.ENTRY_COL_DATE,
			MuseumSQLiteHelper.ENTRY_COL_OTIPASS,
			MuseumSQLiteHelper.ENTRY_COL_NB,
			MuseumSQLiteHelper.ENTRY_COL_EVENT,
			MuseumSQLiteHelper.ENTRY_COL_UPLOADED,
			MuseumSQLiteHelper.ENTRY_COL_SERVICE,
	};
	private String[] allParamColumns = { 
			MuseumSQLiteHelper.PARAM_COL_ID,
			MuseumSQLiteHelper.PARAM_COL_NAME,
			MuseumSQLiteHelper.PARAM_COL_CALL,
			MuseumSQLiteHelper.PARAM_COL_SOFT,
			MuseumSQLiteHelper.PARAM_COL_CATEGORY
	};
	private String[] allWarningColumns = {
			MuseumSQLiteHelper.WARNING_COL_ID,
			MuseumSQLiteHelper.WARNING_COL_DATE,
			MuseumSQLiteHelper.WARNING_COL_SERIAL,
			MuseumSQLiteHelper.WARNING_COL_EVENT,
	};
	private String[] allUpdateColumns = { 
			MuseumSQLiteHelper.UPDATE_COL_ID,
			MuseumSQLiteHelper.UPDATE_COL_DATE,
			MuseumSQLiteHelper.UPDATE_COL_TYPE,
			MuseumSQLiteHelper.UPDATE_COL_NUMOTIPASS,
			MuseumSQLiteHelper.UPDATE_COL_PID,
			MuseumSQLiteHelper.UPDATE_COL_NAME,
			MuseumSQLiteHelper.UPDATE_COL_FNAME,
			MuseumSQLiteHelper.UPDATE_COL_COUNTRY,
			MuseumSQLiteHelper.UPDATE_COL_EMAIL,
			MuseumSQLiteHelper.UPDATE_COL_POSTAL_CODE,
			MuseumSQLiteHelper.UPDATE_COL_NEWSLETTER,
			MuseumSQLiteHelper.UPDATE_COL_UPLOADED,
			MuseumSQLiteHelper.UPDATE_COL_FAREID,
			MuseumSQLiteHelper.UPDATE_COL_OPTIONID,
	};
	private String[] allTabletColumns = { 
			MuseumSQLiteHelper.TABLET_COL_ID,
			MuseumSQLiteHelper.TABLET_COL_NUMSEQUENCE,
			MuseumSQLiteHelper.TABLET_COL_UPLOAD_TIME,
			MuseumSQLiteHelper.TABLET_COL_DOWNLOAD_TIME,
			MuseumSQLiteHelper.TABLET_COL_BDD_MODEL
	};

	private String[] allStockColumns = { 
			MuseumSQLiteHelper.STOCK_COL_ID,
			MuseumSQLiteHelper.STOCK_COL_PROVIDER_ID,
			MuseumSQLiteHelper.STOCK_COL_NB_CARDS,
			MuseumSQLiteHelper.STOCK_COL_THRESHOLD,
			MuseumSQLiteHelper.STOCK_COL_ALERT,
	};

	private String[] allMessageColumns = { 
			MuseumSQLiteHelper.MSG_COL_ID,
			MuseumSQLiteHelper.MSG_COL_TEXT,
			MuseumSQLiteHelper.MSG_COL_LANG,
			MuseumSQLiteHelper.MSG_COL_START_DATE,
			MuseumSQLiteHelper.MSG_COL_END_DATE,
	};

	private String[] allDiscountColumns = { 
			MuseumSQLiteHelper.DISCOUNT_COL_ID,
			MuseumSQLiteHelper.DISCOUNT_COL_AMOUNT_EUR,
			MuseumSQLiteHelper.DISCOUNT_COL_AMOUNT_FCH,
			MuseumSQLiteHelper.DISCOUNT_COL_START_DATE,
			MuseumSQLiteHelper.DISCOUNT_COL_END_DATE,
	};

	private String[] allPackageColumns = { 
			MuseumSQLiteHelper.PACKAGE_COL_ID,
			MuseumSQLiteHelper.PACKAGE_COL_NAME,
			MuseumSQLiteHelper.PACKAGE_COL_CHILD,
			MuseumSQLiteHelper.PACKAGE_COL_DURATION,
			MuseumSQLiteHelper.PACKAGE_COL_PERIOD,
			MuseumSQLiteHelper.PACKAGE_COL_PRICE,
			MuseumSQLiteHelper.PACKAGE_COL_REF,
	};
	
	private String[] allWlColumns = { 
			MuseumSQLiteHelper.WL_COL_ID,
			MuseumSQLiteHelper.WL_COL_DATE,
			MuseumSQLiteHelper.WL_COL_NBSTEPS,
			MuseumSQLiteHelper.WL_COL_NBCARDS,
			MuseumSQLiteHelper.WL_COL_NUMSEQUENCE,
			MuseumSQLiteHelper.WL_COL_STATUS,
	};

	private String[] allFareColumns = {
			MuseumSQLiteHelper.FARE_COL_ID,
			MuseumSQLiteHelper.FARE_COL_NAME,
			MuseumSQLiteHelper.FARE_COL_REF,
	};
	private String[] allOptionColumns = {
			MuseumSQLiteHelper.OPTION_COL_ID,
			MuseumSQLiteHelper.OPTION_COL_NAME,
			MuseumSQLiteHelper.OPTION_COL_REF,
	};

	public static final int cSaveOK = 0;
	public static final int cSaveKO = 1;
	public static final int cOtipassKO = 2;
	public static final int cUpdateOtipassFailed = 3;

	public MuseumDbAdapter(Context context) {
		// dbHelper = new MuseumSQLiteHelper(context);
		dbHelper = MuseumSQLiteHelper.getInstance(context);
	}


	public void open() throws SQLException {
		try {
			database = dbHelper.getWritableDatabase();
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - open - " + e.getMessage());
		}
	}

	public void close() {
		dbHelper.close();
	}

	public void deleteDB(Context context) throws SQLException {
		context.deleteDatabase(MuseumSQLiteHelper.DATABASE_NAME);
	}

	public String getDBPath(Context context) {
		File dbFile = context.getDatabasePath(MuseumSQLiteHelper.DATABASE_NAME);
		String s = dbFile.getAbsolutePath();
		return s;
	}

	public Otipass insertOtipassObject(Otipass otipass) {
		try {
			ContentValues values = new ContentValues();
			values.put(MuseumSQLiteHelper.OTI_COL_NUMOTIPASS, otipass.getNumOtipass());
			values.put(MuseumSQLiteHelper.OTI_COL_SERIAL, otipass.getSerial());
			values.put(MuseumSQLiteHelper.OTI_COL_STATUS, otipass.getStatus());
			values.put(MuseumSQLiteHelper.OTI_COL_EXPIRY, otipass.getExpiry());
			values.put(MuseumSQLiteHelper.OTI_COL_TYPE, otipass.getType());
			values.put(MuseumSQLiteHelper.OTI_COL_PID, otipass.getPid());
			values.put(MuseumSQLiteHelper.OTI_COL_SERVICE, otipass.getService());
			if (otipass.getFareId() > -1) {
				values.put(MuseumSQLiteHelper.OTI_COL_FARE, otipass.getFareId());
				if (otipass.getOptionId() > -1) {
					values.put(MuseumSQLiteHelper.OTI_COL_OPTION, otipass.getOptionId());
				}
			}
			long id = database.insert(MuseumSQLiteHelper.OTIPASS_TABLE, null, values);
			if (id > 0) {
				otipass =  getOtipass(id);
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertOtipassObject - " + e.getMessage());
		}
		return otipass;
	}

	/**
	 * insert usage 
	 * @param usage
	 * @return
	 */
	public long insertUsage(Usage usage) {
		long id = 0;
		try {
			ContentValues values = new ContentValues();
			values.put(MuseumSQLiteHelper.USE_PASS_COL_USAGE_OTIPASS, usage.getNumOtipass());
			values.put(MuseumSQLiteHelper.USE_PASS_COL_USAGE_DATE, usage.getDate());
			id = database.insert(MuseumSQLiteHelper.USE_PASS_TABLE, null, values);

		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertUsage - " + e.getMessage());
		}
		return id;
	}
	
	public int insertUsageList(List<Usage> usageList) {
		Usage usage = null;
		long id;
		int i = 0;
		database.beginTransaction();
		try {
			ContentValues values = new ContentValues();
			for (i=0; i<usageList.size(); i++) {
				usage = usageList.get(i);
				values.clear();
				values.put(MuseumSQLiteHelper.USE_PASS_COL_USAGE_OTIPASS, usage.getNumOtipass());
				values.put(MuseumSQLiteHelper.USE_PASS_COL_USAGE_DATE, usage.getDate());
				id = database.insert(MuseumSQLiteHelper.USE_PASS_TABLE, null, values);
				if (id < 1L) {
					Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertUsageList - Cannot insert Otipass " + usage.getNumOtipass());
					break;
				}
			}
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertUsageList - " + e.getMessage());
		}
		database.endTransaction();
		return i;
	}
	

	/**
	 * get last day of pass'usage
	 * @param numotipass
	 * @return
	 */
	public String getLastDayUsage(int numotipass){

		String date = null;
		Cursor dCursor = null;
		

		final String MY_QUERY = "SELECT * FROM " + MuseumSQLiteHelper.USE_PASS_TABLE + " WHERE " + MuseumSQLiteHelper.USE_PASS_COL_USAGE_OTIPASS + " = " + numotipass + " ORDER BY " + MuseumSQLiteHelper.USE_PASS_COL_USAGE_ID + " DESC LIMIT 1";

		dCursor = database.rawQuery(MY_QUERY, null);

		if (dCursor != null) {
			if (dCursor.moveToFirst()) {
				do {
					date = dCursor.getString(dCursor.getColumnIndex(MuseumSQLiteHelper.USE_PASS_COL_USAGE_DATE));

				} while (dCursor.moveToNext());
			}
		}
		if (dCursor != null) {
			dCursor.close();
		}
		return date;

	}

	public int insertOtipassList(List<Otipass> otipassList) {
		Otipass otipass = null;
		long id;
		int i = 0;
		database.beginTransaction();
		try {
			ContentValues values = new ContentValues();
			for (i=0; i<otipassList.size(); i++) {
				otipass = otipassList.get(i);
				values.clear();
				values.put(MuseumSQLiteHelper.OTI_COL_NUMOTIPASS, otipass.getNumOtipass());
				values.put(MuseumSQLiteHelper.OTI_COL_SERIAL, otipass.getSerial());
				values.put(MuseumSQLiteHelper.OTI_COL_STATUS, otipass.getStatus());
				values.put(MuseumSQLiteHelper.OTI_COL_EXPIRY, otipass.getExpiry());
				values.put(MuseumSQLiteHelper.OTI_COL_TYPE, otipass.getType());
				values.put(MuseumSQLiteHelper.OTI_COL_PID, otipass.getPid());
				values.put(MuseumSQLiteHelper.OTI_COL_SERVICE, otipass.getService());
				if (otipass.getFareId() > -1) {
					values.put(MuseumSQLiteHelper.OTI_COL_FARE, otipass.getFareId());
				}
				if (otipass.getOptionId() > -1) {
					values.put(MuseumSQLiteHelper.OTI_COL_OPTION, otipass.getOptionId());
				}
				id = database.insert(MuseumSQLiteHelper.OTIPASS_TABLE, null, values);
				if (id < 1L) {
					Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertOtipassList - Cannot insert Otipass " + otipass.getNumOtipass());
					break;
				}
			}
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertOtipassList - " + e.getMessage());
		}
		database.endTransaction();
		return i;
	}

	public int createOtipassList(List<Create> createList) {
		Create create;
		long id;
		int i = 0;
		Otipass otipass;
		database.beginTransaction();
		try {
			ContentValues values = new ContentValues();
			for (i=0; i<createList.size(); i++) {
				create = createList.get(i);
				otipass = getOtipass(create.getNumotipass());
				if (otipass  == null) {
					// normal Pass insertion 
					values.clear();
					values.put(MuseumSQLiteHelper.OTI_COL_NUMOTIPASS, create.getNumotipass());
					values.put(MuseumSQLiteHelper.OTI_COL_SERIAL, create.getSerial());
					values.put(MuseumSQLiteHelper.OTI_COL_STATUS, create.getStatus());
					values.put(MuseumSQLiteHelper.OTI_COL_TYPE, create.getType());
					values.put(MuseumSQLiteHelper.OTI_COL_PID, create.getPid());
					values.put(MuseumSQLiteHelper.OTI_COL_SERVICE, create.getService());
					values.put(MuseumSQLiteHelper.OTI_COL_EXPIRY, create.getExpiry());
					if (create.getFareId() > -1) {
						values.put(MuseumSQLiteHelper.OTI_COL_FARE, create.getFareId());
					}
					if (create.getOptionId() > -1) {
						values.put(MuseumSQLiteHelper.OTI_COL_OPTION, create.getOptionId());
					}
					id = database.insert(MuseumSQLiteHelper.OTIPASS_TABLE, null, values);
					if (id < 1L) {
						Log.e(TAG, MuseumDbAdapter.class.getName() + " - createOtipassList - Cannot insert Otipass " + create.getNumotipass());
					}
				} else {
					// this otipass already exists, update it
					otipass.setStatus(create.getStatus());
					otipass.setType(create.getType());
					if (create.getFareId() > -1) {
						otipass.setFareId(create.getFareId());
					}
					if (create.getOptionId() > -1) {
						otipass.setOptionId(create.getOptionId());
					}
					int nb = updateOtipass(otipass);
					if (nb != 1) {
						Log.d(TAG, MuseumDbAdapter.class.getName() + " - createOtipassList - cannot update Otipass:" + create.getNumotipass());
					}
				}
			}
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - createOtipassList - " + e.getMessage());
		}
		database.endTransaction();
		return i;
	}


	public int deleteOtipassList(List<Otipass> otipassList) {
		Otipass otipass = null;
		int i = 0;
		try {
			for (i=0; i<otipassList.size(); i++) {
				otipass = otipassList.get(i);
				database.delete(MuseumSQLiteHelper.OTIPASS_TABLE, MuseumSQLiteHelper.OTI_COL_NUMOTIPASS + " = ?", new String[] { String.valueOf(otipass.getNumOtipass()) });
			}
		} catch (Exception e) {
		}
		return i;
	}


	public int updateOtipassList(List<Partial> updateList) {
		Partial update = null;
		Otipass otipass;
		int i = 0;
		database.beginTransaction();
		try {
			for (i=0; i<updateList.size(); i++) {
				update = updateList.get(i);
				otipass = getOtipass(update.getNumotipass());
				if (otipass != null) {
					otipass.setStatus((short)update.getStatus());
					if (update.getExpiry() != "") {
						otipass.setExpiry(update.getExpiry());
					}
					if (update.getPid() != -1) {
						otipass.setPid(update.getPid());
					}
					if (update.getFareId() > -1) {
						otipass.setFareId(update.getFareId());
					}
					if (update.getOptionId() > -1) {
						otipass.setOptionId(update.getOptionId());
					}

					if (updateOtipass(otipass) != 1) {
						Log.d(TAG, MuseumDbAdapter.class.getName() + " - updateOtipassList - cannot update Otipass:" + update.getNumotipass());
					}
				} else {
					Log.d(TAG, MuseumDbAdapter.class.getName() + " - updateOtipassList - cannot get Otipass:" + update.getNumotipass());
				}
			}
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - updateOtipassList - " + e.getMessage());
		}
		database.endTransaction();
		return i;
	}

	/**
	 * update otipass service
	 * @param updateList
	 * @return
	 */

	public int updateOtipassSrvList(List<PartialServiceCpt> updateList) {
		PartialServiceCpt update = null;
		Otipass otipass;
		String service = "";
		int i = 0;
		database.beginTransaction();
		try {
			for (i=0; i<updateList.size(); i++) {
				service = "";
				update = updateList.get(i);
				otipass = getOtipass(update.getNumOtipass());
				if (otipass != null) {
					String [] srv = update.getSrv().split(";");
					if (srv.length > 1) {
						otipass.setService(update.getSrv());
					}else {
						String [] srv_cpt = srv[0].split(":");
						String [] srv_ot = otipass.getService().split(";");

						for (int j = 0; j < srv_ot.length ; j++) {
							String [] serviceToUpdate = srv_ot[j].split(":");
							if (serviceToUpdate[0].equals(srv_cpt[0])) {
								serviceToUpdate[1] = srv_cpt[1];
							}
							service = service.concat(serviceToUpdate[0].toString().concat(":" + serviceToUpdate[1].toString() + ";"));
						}
						otipass.setService(service);
					}

					if (updateOtipass(otipass) != 1) {
						Log.d(TAG, MuseumDbAdapter.class.getName() + " - updateOtipassSrvList - cannot update Otipass:" + update.getNumOtipass());
					}
				} else {
					Log.d(TAG, MuseumDbAdapter.class.getName() + " - updateOtipassSrvList - cannot get Otipass:" + update.getNumOtipass());
				}
			}
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - updateOtipassSrvList - " + e.getMessage());
		}
		database.endTransaction();
		return i;
	}

	public int deleteOtipass(long numOtipass) {
		int nbRows = 0;
		try {
			nbRows = database.delete(MuseumSQLiteHelper.OTIPASS_TABLE, MuseumSQLiteHelper.OTI_COL_NUMOTIPASS + " = ?", new String[] { String.valueOf(numOtipass) });
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - deleteOtipass - " + e.getMessage());
		}
		return nbRows;
	}

	public int updateOtipass(Otipass otipass) {
		int nbRows = 0;
		try {
			ContentValues values = new ContentValues();
			values.put(MuseumSQLiteHelper.OTI_COL_NUMOTIPASS, otipass.getNumOtipass());
			values.put(MuseumSQLiteHelper.OTI_COL_SERIAL, otipass.getSerial());
			values.put(MuseumSQLiteHelper.OTI_COL_STATUS, otipass.getStatus());
			values.put(MuseumSQLiteHelper.OTI_COL_EXPIRY, otipass.getExpiry());
			values.put(MuseumSQLiteHelper.OTI_COL_PID, otipass.getPid());
			values.put(MuseumSQLiteHelper.OTI_COL_TYPE, otipass.getType());
			if (otipass.getFareId() > -1) {
				values.put(MuseumSQLiteHelper.OTI_COL_FARE, otipass.getFareId());
				if (otipass.getOptionId() > -1) {
					values.put(MuseumSQLiteHelper.OTI_COL_OPTION, otipass.getOptionId());
				}
			}
			Log.d("service", otipass.getService());
			values.put(MuseumSQLiteHelper.OTI_COL_SERVICE, otipass.getService());
			nbRows = database.update(MuseumSQLiteHelper.OTIPASS_TABLE, values, MuseumSQLiteHelper.OTI_COL_NUMOTIPASS + " = ?", new String[] { String.valueOf(otipass.getNumOtipass()) });
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - updateOtipass - " + e.getMessage());
		}
		return nbRows;
	}


	/**
	 * updates otipass when the entry is done
	 * @param service
	 * @param status
	 * @param pid
	 * @param numotipass
	 * @return
	 */
	public int updateOtipassAfterEntry(String service, int status, int pid, int numotipass){
		int nbRows = 0;
		try {
			ContentValues values = new ContentValues();
			values.put(MuseumSQLiteHelper.OTI_COL_PID, pid);
			values.put(MuseumSQLiteHelper.OTI_COL_STATUS, status);
			values.put(MuseumSQLiteHelper.OTI_COL_SERVICE, service);
			nbRows = database.update(MuseumSQLiteHelper.OTIPASS_TABLE, values, MuseumSQLiteHelper.OTI_COL_NUMOTIPASS + " = ?", new String[] { String.valueOf(numotipass) });
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - updateOtipassAfterEntry - " + e.getMessage());
		}
		return nbRows;
	}

	/**
	 * updates otipass when the sale is done
	 * @param service
	 * @param status
	 * @param pid
	 * @param numotipass
	 * @return
	 */
	public int updateOtipassAfterSale(String service, int status, int pid, int numotipass, int idfare, int idoption){
		int nbRows = 0;
		try {
			ContentValues values = new ContentValues();
			values.put(MuseumSQLiteHelper.OTI_COL_PID, pid);
			values.put(MuseumSQLiteHelper.OTI_COL_STATUS, status);
			values.put(MuseumSQLiteHelper.OTI_COL_SERVICE, service);
			if (idfare > -1) {
				values.put(MuseumSQLiteHelper.OTI_COL_FARE, idfare);
				values.put(MuseumSQLiteHelper.OTI_COL_OPTION, idoption);
			}
			nbRows = database.update(MuseumSQLiteHelper.OTIPASS_TABLE, values, MuseumSQLiteHelper.OTI_COL_NUMOTIPASS + " = ?", new String[] { String.valueOf(numotipass) });
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - updateOtipassAfterSale - " + e.getMessage());
		}
		return nbRows;
	}

	/**
	 * updates expriy date
	 * @param date
	 * @param numotipass
	 * @return
	 */
	public int updateOtipassExpiryDate(String date, int numotipass){
		int nbRows = 0;
		try {
			ContentValues values = new ContentValues();
			values.put(MuseumSQLiteHelper.OTI_COL_EXPIRY, date);
			nbRows = database.update(MuseumSQLiteHelper.OTIPASS_TABLE, values, MuseumSQLiteHelper.OTI_COL_NUMOTIPASS + " = ?", new String[] { String.valueOf(numotipass) });
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - updateOtipassExpiryDate - " + e.getMessage());
		}
		return nbRows;
	}

	public Otipass getOtipass(long numOtipass) {
		Otipass otipass = null;
		Cursor cursor = null;
		
		try {
			cursor = database.query(MuseumSQLiteHelper.OTIPASS_TABLE,
					allOtipassColumns, MuseumSQLiteHelper.OTI_COL_NUMOTIPASS + " = " + numOtipass, null, null, null, null);
			if (cursor.moveToFirst()) {
				otipass = new Otipass();
				otipass.setNumOtipass(cursor.getLong(cursor.getColumnIndex(MuseumSQLiteHelper.OTI_COL_NUMOTIPASS)));
				otipass.setSerial(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.OTI_COL_SERIAL)));
				otipass.setStatus(cursor.getShort(cursor.getColumnIndex(MuseumSQLiteHelper.OTI_COL_STATUS)));
				otipass.setExpiry(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.OTI_COL_EXPIRY)));
				otipass.setType(cursor.getShort(cursor.getColumnIndex(MuseumSQLiteHelper.OTI_COL_TYPE)));
				otipass.setPid(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.OTI_COL_PID)));
				otipass.setService(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.OTI_COL_SERVICE)));
				otipass.setFareId(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.OTI_COL_FARE)));
				otipass.setOptionId(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.OTI_COL_OPTION)));
			}
			if (cursor != null) { 
				cursor.close();
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getOtipass - " + e.getMessage());
		}
		return otipass;
	}

	/**
	 * returns package by package's id
	 * @param idPackage
	 * @return
	 */
	public PackageObject getPackageById(int idPackage){
		PackageObject po = null;
		Cursor cursor = null;
		
		try {
			cursor = database.query(MuseumSQLiteHelper.PACKAGE_TABLE,
					allPackageColumns, MuseumSQLiteHelper.PACKAGE_COL_ID + " = '" + idPackage + "'", null, null, null, null);
			if (cursor.moveToFirst()) { 
				do { 
					po = new PackageObject();
					po.setId(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_ID)));
					po.setName(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_NAME)));
					po.setNbChild(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_CHILD)));
					po.setDuration(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_DURATION)));
					po.setPeriod(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_PERIOD)));
					po.setPrice(cursor.getDouble(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_PRICE)));

				} while (cursor.moveToNext()); 
			} 
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getPackageById - " + e.getMessage());
		}
		if (cursor != null) { 
			cursor.close();
		}
		return po;
	}

	/**
	 * returns package by package's name
	 * @param package_name
	 * @return
	 */
	public PackageObject getPackageByName(String package_name){
		PackageObject po = null;
		Cursor cursor = null;
		
		try {
			cursor = database.query(MuseumSQLiteHelper.PACKAGE_TABLE,
					allPackageColumns, MuseumSQLiteHelper.PACKAGE_COL_NAME + " = '" + package_name + "'", null, null, null, null);
			if (cursor.moveToFirst()) { 
				do { 
					po = new PackageObject();
					po.setId(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_ID)));
					po.setName(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_NAME)));
					po.setNbChild(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_CHILD)));
					po.setDuration(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_DURATION)));
					po.setPeriod(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_PERIOD)));
					po.setPrice(cursor.getDouble(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_PRICE)));

				} while (cursor.moveToNext()); 
			} 
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getPackageByName - " + e.getMessage());
		}
		if (cursor != null) { 
			cursor.close();
		}
		return po;
	}

	/**
	 * returns package's name by package's id
	 * @param package_id
	 * @return
	 */
	public String getPackageNameById(int package_id){
		String package_name = null;
		Cursor cursor = null;
		
		try {
			String [] columns = {MuseumSQLiteHelper.PACKAGE_COL_NAME};
			cursor = database.query(MuseumSQLiteHelper.PACKAGE_TABLE,
					columns, MuseumSQLiteHelper.PACKAGE_COL_ID + " = '" + package_id + "'", null, null, null, null);
			if (cursor.moveToFirst()) { 
				do { 
					package_name = cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_NAME));
				} while (cursor.moveToNext()); 
			} 
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getPackageNameById - " + e.getMessage());
		}
		if (cursor != null) { 
			cursor.close();
		}
		return package_name;
	}

	/**
	 * returns service's id by service's name
	 * @param service_name
	 * @return
	 */
	public int getServiceIdByName(String service_name){
		int idservice = 0;
		Cursor dCursor = null;
		
		try {

			dCursor = database.rawQuery(
					"SELECT " + MuseumSQLiteHelper.SERVICE_COL_ID + " FROM " + MuseumSQLiteHelper.SERVICE_TABLE + " WHERE " + MuseumSQLiteHelper.SERVICE_COL_NAME + " = ? "
					, new String[]{service_name});
			if (dCursor != null) {
				if (dCursor.moveToFirst()) {
					do {
						idservice = dCursor.getInt(dCursor.getColumnIndex(MuseumSQLiteHelper.SERVICE_COL_ID));

					} while (dCursor.moveToNext());
				}
				dCursor.close();
			}

		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getServiceIdByName - " + e.getMessage());
		}
		return idservice;
	}

	/**
	 * get service name by service id
	 * @param idservice
	 * @return
	 */
	public String getServiceNameById(int idservice){
		String service_name = null;
		Cursor cursor = null;
		
		try {
			String [] columns = {MuseumSQLiteHelper.SERVICE_COL_NAME};
			cursor = database.query(MuseumSQLiteHelper.SERVICE_TABLE,
					columns, MuseumSQLiteHelper.SERVICE_COL_ID + " = '" + idservice + "'", null, null, null, null);
			if (cursor.moveToFirst()) { 
				do { 
					service_name = cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.SERVICE_COL_NAME));
				} while (cursor.moveToNext()); 
			} 
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getServiceNameById - " + e.getMessage());
		}
		if (cursor != null) { 
			cursor.close();
		}
		return service_name;
	}

	/**
	 * get package's price by idpackage
	 * @param idpackage
	 * @return
	 */
	public double getPriceByIdPackage(int idpackage){
		double price = 0;
		Cursor cursor = null;
		
		try {
			String [] columns = {MuseumSQLiteHelper.PACKAGE_COL_PRICE};
			cursor = database.query(MuseumSQLiteHelper.PACKAGE_TABLE,
					columns, MuseumSQLiteHelper.PACKAGE_COL_ID + " = '" + idpackage + "'", null, null, null, null);
			if (cursor.moveToFirst()) { 
				do { 
					price = cursor.getDouble(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_PRICE));
				} while (cursor.moveToNext()); 
			} 
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getServiceNameById - " + e.getMessage());
		}
		if (cursor != null) { 
			cursor.close();
		}
		return price;
	}

	public User getUser(long id) {
		User user = null;
		Cursor cursor = null;
		
		try {
			cursor = database.query(MuseumSQLiteHelper.USER_TABLE,
					allUserColumns, MuseumSQLiteHelper.USER_COL_ID + " = " + id, null, null, null, null);
			if (cursor.moveToFirst()) {
				user = new User();
				user.setId(cursor.getLong(0));
				user.setUserid(cursor.getString(1));
				user.setPassword(cursor.getString(2));
				user.setSalt(cursor.getString(3));
				user.setProfile(cursor.getShort(4));
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getUser - " + e.getMessage());
		}
		if (cursor != null) { 
			cursor.close();
		}
		return user;
	}

	public User getUserByLogin(String login) {
		User user = null;
		Cursor cursor = null;
		
		try {
			cursor = database.query(MuseumSQLiteHelper.USER_TABLE,
					allUserColumns, MuseumSQLiteHelper.USER_COL_USERID + " = '" + login + "'", null, null, null, null);
			if (cursor.moveToFirst()) {
				user = new User();
				user.setId(cursor.getLong(0));
				user.setUserid(cursor.getString(1));
				user.setPassword(cursor.getString(2));
				user.setSalt(cursor.getString(3));
				user.setProfile(cursor.getShort(4));
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getUserByLogin - " + e.getMessage());
		}
		if (cursor != null) { 
			cursor.close();
		}
		return user;
	}

	public User insertUser(int idUser, String userid, String password, String salt, short profile) {
		User user = null;
		try {
			ContentValues values = new ContentValues();
			values.put(MuseumSQLiteHelper.USER_COL_ID, idUser);
			values.put(MuseumSQLiteHelper.USER_COL_USERID, userid);
			values.put(MuseumSQLiteHelper.USER_COL_PWD, password);
			values.put(MuseumSQLiteHelper.USER_COL_SALT, salt);
			values.put(MuseumSQLiteHelper.USER_COL_PROFILE, profile);
			long id = database.insert(MuseumSQLiteHelper.USER_TABLE, null, values);
			if (id > 0) {
				return getUser(id);
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertUser - " + e.getMessage());
		}
		return user;
	}

	public int insertUserList(List<User> userList) {
		User user = null;
		long id;
		int i = 0;
		try {
			ContentValues values = new ContentValues();
			for (i=0; i<userList.size(); i++) {
				user = userList.get(i);
				values.clear();
				values.put(MuseumSQLiteHelper.USER_COL_USERID, user.getUserid());
				values.put(MuseumSQLiteHelper.USER_COL_PWD, user.getPassword());
				values.put(MuseumSQLiteHelper.USER_COL_SALT, user.getSalt());
				values.put(MuseumSQLiteHelper.USER_COL_PROFILE, user.getProfile());
				id = database.insert(MuseumSQLiteHelper.USER_TABLE, null, values);
				if (id < 1L) {
					Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertUserList - Cannot insert user" + user.getUserid());
					break;
				}
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertUserList - " + e.getMessage());
		}
		return i;
	}

	// Getting all the entries which have the flag uploaded = false;
	public List<Entry> getUnloadedEntries() { 
		List<Entry> entryList = new ArrayList<Entry>(); 
		Cursor cursor = null;
		
		try {
			String selectQuery = "SELECT  * FROM " + MuseumSQLiteHelper.ENTRY_TABLE + " WHERE " + MuseumSQLiteHelper.ENTRY_COL_UPLOADED + " = 0"; 
			cursor = database.rawQuery(selectQuery, null); 
			if (cursor.moveToFirst()) { 
				do { 
					Entry entry = new Entry(); 
					entry.setId(cursor.getLong(cursor.getColumnIndex(MuseumSQLiteHelper.ENTRY_COL_ID)));
					entry.setDate(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.ENTRY_COL_DATE)));
					entry.setNumotipass((int)cursor.getLong(cursor.getColumnIndex(MuseumSQLiteHelper.ENTRY_COL_OTIPASS)));
					entry.setNb(cursor.getShort(cursor.getColumnIndex(MuseumSQLiteHelper.ENTRY_COL_NB)));
					entry.setEvent(cursor.getShort(cursor.getColumnIndex(MuseumSQLiteHelper.ENTRY_COL_EVENT)));
					entry.setUploaded(false);
					entry.setService(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.ENTRY_COL_SERVICE)));
					entryList.add(entry);
				} while (cursor.moveToNext()); 
			} 
			cursor.close();
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getUnloadedEntries - " + e.getMessage());
		}
		return entryList; 
	} 

	public Entry getEntry(long id) {
		Entry entry = null;
		Cursor cursor = null;
		
		try {
			cursor = database.query(MuseumSQLiteHelper.ENTRY_TABLE,
					allEntryColumns, MuseumSQLiteHelper.ENTRY_COL_ID + " = " + id, null, null, null, null);
			if (cursor.moveToFirst()) {
				entry = new Entry();
				entry.setId(cursor.getLong(cursor.getColumnIndex(MuseumSQLiteHelper.ENTRY_COL_ID)));
				entry.setDate(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.ENTRY_COL_DATE)));
				entry.setNumotipass((int)cursor.getLong(cursor.getColumnIndex(MuseumSQLiteHelper.ENTRY_COL_OTIPASS)));
				entry.setNb(cursor.getShort(cursor.getColumnIndex(MuseumSQLiteHelper.ENTRY_COL_NB)));
				entry.setEvent(cursor.getShort(cursor.getColumnIndex(MuseumSQLiteHelper.ENTRY_COL_EVENT)));
				boolean uploaded = (cursor.getShort(5) == 1) ? true : false;
				entry.setUploaded(uploaded);
				entry.setService(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.ENTRY_COL_SERVICE)));
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getEntry - " + e.getMessage());
		}
		if (cursor != null) { 
			cursor.close();
		}
		return entry;
	}

	/**
	 * get entries for pass when date is today
	 * @param numotipass
	 * @param today
	 * @return
	 */
	public int getEntryByOtipassAndDay(int numotipass, String today)
	{
		String[] args = {String.valueOf(numotipass) , '%' + today + '%'};
		Cursor cursor = null;
		int nb = 0;
		try
		{
			cursor = database.query(
					MuseumSQLiteHelper.ENTRY_TABLE, 
					allEntryColumns, 
					MuseumSQLiteHelper.ENTRY_COL_OTIPASS + " = ? AND " + MuseumSQLiteHelper.ENTRY_COL_DATE  + " LIKE ?", 
					args, 
					null, 
					null, 
					null
					);
			nb =  cursor.getCount();
		}
		catch (Exception e)
		{
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getEntryByOtipassAndDay - " + e.getMessage());
		}
		if (cursor != null) { 
			cursor.close();
		}

		return nb;
	}

	/**
	 * check whether service exists
	 * @param idService
	 * @return
	 */
	public int isServiceExists(int idService){
		Cursor cursor = null;
		int exist = 0;
		try{
			String selectQuery = "SELECT  * FROM " + MuseumSQLiteHelper.SERVICE_TABLE + " WHERE " + MuseumSQLiteHelper.SERVICE_COL_ID + " = " + idService;
			cursor = database.rawQuery(selectQuery, null); 
			exist =  cursor.getCount();
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - isServiceExists - " + e.getMessage());
		}
		if (cursor != null) { 
			cursor.close();
		}
		return exist;
	}

	/**
	 * check whether otipass exists in use_pass table
	 * @param idotipass
	 * @return
	 */
	public int isOtipassExists(int numotipass){
		Cursor cursor = null;
		int exist = 0;

		try{
			String selectQuery = "SELECT  * FROM " + MuseumSQLiteHelper.USE_PASS_TABLE + " WHERE " + MuseumSQLiteHelper.USE_PASS_COL_USAGE_OTIPASS + " = " + numotipass;
			cursor = database.rawQuery(selectQuery, null); 
			exist = cursor.getCount();
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - isOtipassExists - " + e.getMessage());
		}
		if (cursor != null) { 
			cursor.close();
		}
		return exist;
	}

	/**
	 * check whether provider has services
	 * @return
	 */
	public int isProviderHasServices(){
		Cursor cursor = null;
		int exist = 0;

		try{
			String selectQuery = "SELECT  * FROM " + MuseumSQLiteHelper.PROVIDER_SERVICE_TABLE;
			cursor = database.rawQuery(selectQuery, null); 
			exist =  cursor.getCount();
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - isProviderHasServices - " + e.getMessage());
		}
		if (cursor != null) { 
			cursor.close();
		}
		return exist;
	}

	/**
	 * check whether provider has services
	 * @return
	 */
	public String getProviderPackageServices(int packageId){
		Cursor cursor = null;
		String srvs="";

		try{
			String selectQuery = "SELECT  * FROM " + MuseumSQLiteHelper.PROVIDER_SERVICE_TABLE 
					+ " WHERE " + MuseumSQLiteHelper.PROVIDER_SERVICE_COL_PACKAGE_ID + " = " + packageId;
			cursor = database.rawQuery(selectQuery, null); 
			if (cursor.moveToFirst()) {
				srvs = cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.PROVIDER_SERVICE_COL_SERVICE));
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getProviderPackageServices - " + e.getMessage());
		}
		if (cursor != null) { 
			cursor.close();
		}
		return srvs;
	}

	/**
	 * check whether package service exists
	 * @param idPackageService
	 * @param idService
	 * @return
	 */
	public int isPackageServiceExists(int idpackage, int idService){
		Cursor cursor = null;
		int exist = 0;

		try{
			String selectQuery = "SELECT  * FROM " + MuseumSQLiteHelper.PACKAGE_SERVICE_TABLE + " WHERE " + MuseumSQLiteHelper.PACKAGE_SERVICE_COL_SERVICE_ID + " = " + idService + "  AND " + MuseumSQLiteHelper.PACKAGE_SERVICE_COL_PACKAGE_ID + " = " + idpackage;
			cursor = database.rawQuery(selectQuery, null); 
			exist =  cursor.getCount();
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - isPackageServiceExists - " + e.getMessage());
		}
		if (cursor != null) { 
			cursor.close();
		}
		return exist;
	}

	/**
	 * check whether use pass exists in use_pass table
	 * @param numotipass
	 * @param date
	 * @return
	 */
	public int isUsePassExists(int numotipass, String date){
		Cursor cursor = null;
		int exist = 0;

		try{
			String selectQuery = "SELECT  * FROM " + MuseumSQLiteHelper.USE_PASS_TABLE + " WHERE " + MuseumSQLiteHelper.USE_PASS_COL_USAGE_OTIPASS + " = " + numotipass + "  AND " + MuseumSQLiteHelper.USE_PASS_COL_USAGE_DATE + " = " + date;
			cursor = database.rawQuery(selectQuery, null); 
			exist =  cursor.getCount();
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - isUsePassExists - " + e.getMessage());
		}
		if (cursor != null) { 
			cursor.close();
		}
		return exist;
	}

	public int getEntryByOtipassAndEvent(int numotipass, int event, String today)
	{
		Cursor cursor = null;
		int exist = 0;
		
		String[] args = {String.valueOf(numotipass) , String.valueOf(event), '%' + today + '%'};
		try
		{
			cursor = database.query(
					MuseumSQLiteHelper.ENTRY_TABLE, 
					allEntryColumns, 
					MuseumSQLiteHelper.ENTRY_COL_OTIPASS + " = ? AND " + MuseumSQLiteHelper.ENTRY_COL_EVENT  + " = ? AND " + MuseumSQLiteHelper.ENTRY_COL_DATE  + " LIKE ?", 
					args, 
					null, 
					null, 
					null
					);
			exist =  cursor.getCount();
		}
		catch (Exception e)
		{
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getEntryByOtipassAndEvent - " + e.getMessage());
		}
		if (cursor != null) { 
			cursor.close();
		}
		return exist;

	}

	public int deleteOldEntries(String today) {
		int nbRows = 0;
		try {
			nbRows = database.delete(MuseumSQLiteHelper.ENTRY_TABLE, MuseumSQLiteHelper.ENTRY_COL_UPLOADED + " = ? AND " + MuseumSQLiteHelper.ENTRY_COL_DATE  + " NOT LIKE ?", new String[] { "1",  '%' + today + '%'});
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - deleteOldEntries - " + e.getMessage());
		}
		return nbRows;
	}


	public int updateEntry(Entry entry) {
		int nbRows = 0;
		try {
			ContentValues values = new ContentValues();
			values.put(MuseumSQLiteHelper.ENTRY_COL_DATE, entry.getDate());
			values.put(MuseumSQLiteHelper.ENTRY_COL_OTIPASS, entry.getNumotipass());
			values.put(MuseumSQLiteHelper.ENTRY_COL_NB, entry.getNb());
			values.put(MuseumSQLiteHelper.ENTRY_COL_EVENT, entry.getEvent());
			values.put(MuseumSQLiteHelper.ENTRY_COL_UPLOADED, entry.getUploaded());
			values.put(MuseumSQLiteHelper.ENTRY_COL_SERVICE, entry.getService());
			nbRows = database.update(MuseumSQLiteHelper.ENTRY_TABLE, values, MuseumSQLiteHelper.ENTRY_COL_ID + " = ?", new String[] { String.valueOf(entry.getId()) });
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - updateEntry - " + e.getMessage());
		}
		return nbRows;
	}

	/**
	 * get services from service table where package_id passed as parameter 
	 * @param idpackage
	 * @return
	 */
	public ArrayList<ServicePass> getServicesByPackageId(int idpackage) {

		ArrayList<ServicePass> serviceList = new ArrayList<ServicePass>();
		ServicePass sp ; 

		final String MY_QUERY = "SELECT s." + MuseumSQLiteHelper.SERVICE_COL_NAME + ", s." + MuseumSQLiteHelper.SERVICE_COL_ID + ", s." + MuseumSQLiteHelper.SERVICE_COL_TYPE 
				+ " FROM " + MuseumSQLiteHelper.SERVICE_TABLE 
				+ " s INNER JOIN " + MuseumSQLiteHelper.PACKAGE_SERVICE_TABLE 
				+ " ps ON s." + MuseumSQLiteHelper.SERVICE_COL_ID + " = ps." + MuseumSQLiteHelper.PACKAGE_SERVICE_COL_SERVICE_ID 
				+ " INNER JOIN " + MuseumSQLiteHelper.PACKAGE_TABLE + " p ON p." + MuseumSQLiteHelper.PACKAGE_COL_ID + " = ps." + MuseumSQLiteHelper.PACKAGE_SERVICE_COL_PACKAGE_ID
				+ " WHERE p. " + MuseumSQLiteHelper.PACKAGE_COL_ID + "=?";

		Cursor dCursor = database.rawQuery(MY_QUERY, new String[]{String.valueOf(idpackage)});

		if (dCursor != null) {
			if (dCursor.moveToFirst()) {
				do {
					sp = new ServicePass();
					sp.setId(dCursor.getInt(dCursor.getColumnIndex(MuseumSQLiteHelper.SERVICE_COL_ID)));
					sp.setName(dCursor.getString(dCursor.getColumnIndex(MuseumSQLiteHelper.SERVICE_COL_NAME)));
					sp.setType(dCursor.getInt(dCursor.getColumnIndex(MuseumSQLiteHelper.SERVICE_COL_TYPE)));
					serviceList.add(sp);
				} while (dCursor.moveToNext());
			}
			dCursor.close();
		}
		return serviceList;
	}


	/**
	 * get service name from service table
	 * @param idservice
	 * @return
	 */
	public String getServiceNameByServiceId(int idservice) {
		Cursor dCursor = null;
		String service_name = null;
		try {
			final String MY_QUERY = "SELECT " + MuseumSQLiteHelper.SERVICE_COL_NAME  
					+ " FROM " + MuseumSQLiteHelper.SERVICE_TABLE 
					+ " WHERE " + MuseumSQLiteHelper.SERVICE_COL_ID + "=?";

			dCursor = database.rawQuery(MY_QUERY, new String[]{String.valueOf(idservice)});

			if (dCursor != null) {
				if (dCursor.moveToFirst()) {
					do {
						service_name = dCursor.getString(dCursor.getColumnIndex(MuseumSQLiteHelper.SERVICE_COL_NAME));
					} while (dCursor.moveToNext());
				}
				dCursor.close();
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getServiceNameByServiceId - " + e.getMessage());
		}
		return service_name;
	}

	/**
	 * returns service from provider service table where package_id is passed as parameter
	 * @param idpackage
	 * @return
	 */
	public String getServicebyPackageId(int idpackage) {
		Cursor cursor = null;
		String service = null;
		try {
			final String MY_QUERY = "SELECT " + MuseumSQLiteHelper.PROVIDER_SERVICE_COL_SERVICE  
					+ " FROM " + MuseumSQLiteHelper.PROVIDER_SERVICE_TABLE
					+ " WHERE " + MuseumSQLiteHelper.PROVIDER_SERVICE_COL_PACKAGE_ID + "=?";

			cursor = database.rawQuery(MY_QUERY, new String[]{String.valueOf(idpackage)});
			if (cursor.moveToFirst()) {
				service = cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.PROVIDER_SERVICE_COL_SERVICE));
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getServicebyPackageId - " + e.getMessage());
		}
		if (cursor != null) {
			cursor.close();
		}
		return service;
	}

	/**
	 * get pass duration by numotipass
	 * @param numotipass
	 * @return
	 */
	public int getDurationByNumOtipass(int numotipass) {

		int duration = 0;

		final String MY_QUERY = "SELECT p." + MuseumSQLiteHelper.PACKAGE_COL_DURATION
				+ " FROM " + MuseumSQLiteHelper.PACKAGE_TABLE 
				+ " p INNER JOIN " + MuseumSQLiteHelper.OTIPASS_TABLE
				+ " o ON p." + MuseumSQLiteHelper.PACKAGE_COL_ID + " = o." + MuseumSQLiteHelper.OTI_COL_PID
				+ " WHERE o. " + MuseumSQLiteHelper.OTI_COL_NUMOTIPASS + "=?";

		Cursor dCursor = database.rawQuery(MY_QUERY, new String[]{String.valueOf(numotipass)});

		if (dCursor != null) {
			if (dCursor.moveToFirst()) {
				do {
					duration = dCursor.getInt(dCursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_DURATION));
				} while (dCursor.moveToNext());
			}
			dCursor.close();
		}
		return duration;
	}

	/**
	 * get period by numotipass
	 * @param numotipass
	 * @return
	 */
	public int getPeriodByNumOtipass(int numotipass) {

		int period = 0;

		final String MY_QUERY = "SELECT p." + MuseumSQLiteHelper.PACKAGE_COL_PERIOD
				+ " FROM " + MuseumSQLiteHelper.PACKAGE_TABLE 
				+ " p INNER JOIN " + MuseumSQLiteHelper.OTIPASS_TABLE
				+ " o ON p." + MuseumSQLiteHelper.PACKAGE_COL_ID + " = o." + MuseumSQLiteHelper.OTI_COL_PID
				+ " WHERE o. " + MuseumSQLiteHelper.OTI_COL_NUMOTIPASS + "=?";

		Cursor dCursor = database.rawQuery(MY_QUERY, new String[]{String.valueOf(numotipass)});

		if (dCursor != null) {
			if (dCursor.moveToFirst()) {
				do {
					period = dCursor.getInt(dCursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_PERIOD));
				} while (dCursor.moveToNext());
			}
			dCursor.close();
		}
		return period;
	}

	/**
	 * returns service number from service_package table
	 * @param idservice
	 * @param idpackage
	 * @return
	 */
	public int getServiceNumber(int idservice, int idpackage) {
		Cursor cursor = null;
		int number = 0;
		try {
			final String MY_QUERY = "SELECT " + MuseumSQLiteHelper.PACKAGE_SERVICE_COL_NUMBER  
					+ " FROM " + MuseumSQLiteHelper.PACKAGE_SERVICE_TABLE
					+ " WHERE " + MuseumSQLiteHelper.PACKAGE_SERVICE_COL_SERVICE_ID + "=? AND " + MuseumSQLiteHelper.PACKAGE_SERVICE_COL_PACKAGE_ID + "=?";

			cursor = database.rawQuery(MY_QUERY, new String[]{String.valueOf(idservice), String.valueOf(idpackage)});
			if (cursor.moveToFirst()) {
				number = cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_SERVICE_COL_NUMBER));
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getServiceNumber - " + e.getMessage());
		}
		if (cursor != null) {
			cursor.close();
		}
		return number;
	}

	/**
	 * get service from otipass table
	 * @param numotipass
	 * @return
	 */
	public String getServicesByNumOtipass(int numotipass) {
		Cursor cursor = null;
		String service = null;
		try {
			final String MY_QUERY = "SELECT " + MuseumSQLiteHelper.OTI_COL_SERVICE
					+ " FROM " + MuseumSQLiteHelper.OTIPASS_TABLE 
					+ " WHERE " + MuseumSQLiteHelper.OTI_COL_NUMOTIPASS + "=?";

			cursor = database.rawQuery(MY_QUERY, new String[]{String.valueOf(numotipass)});
			if (cursor.moveToFirst()) {
				service = cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.OTI_COL_SERVICE));
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getServicesByNumOtipass - " + e.getMessage());
		}
		if (cursor != null) {
			cursor.close();
		}
		return service;
	}

	/**
	 * returns the number of times that the pass has used
	 * @param numotipass
	 * @return
	 */
	public int getNbUsePass(int numotipass) {
		Cursor cursor = null;
		int nbUse = 0;
		try {
			final String MY_QUERY = "SELECT * " 
					+ " FROM " + MuseumSQLiteHelper.USE_PASS_TABLE
					+ " WHERE " + MuseumSQLiteHelper.USE_PASS_COL_USAGE_OTIPASS + "=?";

			cursor = database.rawQuery(MY_QUERY, new String[]{String.valueOf(numotipass)});
			nbUse = cursor.getCount();
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getNbUsePass - " + e.getMessage());
		}
		if (cursor != null) {
			cursor.close();
		}
		return nbUse;
	}

	/**
	 * returns the id of entry
	 * @param numotipass
	 * @param date
	 * @return
	 */
	public int getEntryId(int numotipass, String date) {
		Cursor dCursor = null;
		int identry = 0;
		try {
			final String MY_QUERY = "SELECT " + MuseumSQLiteHelper.ENTRY_COL_ID
					+ " FROM " + MuseumSQLiteHelper.ENTRY_TABLE
					+ " WHERE " + MuseumSQLiteHelper.ENTRY_COL_OTIPASS + "=?"
					+ "  AND "  + MuseumSQLiteHelper.ENTRY_COL_DATE + "=?";

			dCursor = database.rawQuery(MY_QUERY, new String[]{String.valueOf(numotipass), date});
			if (dCursor != null) {
				if (dCursor.moveToFirst()) {
					do {
						identry = dCursor.getInt(dCursor.getColumnIndex(MuseumSQLiteHelper.ENTRY_COL_ID));
					} while (dCursor.moveToNext());
				}
				dCursor.close();
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getEntryId - " + e.getMessage());
		}

		return identry;
	}

	/**
	 * returns the id of update where numotipass and date are parameters
	 * @param numotipass
	 * @param date
	 * @return
	 */
	public int getUpdateId(int numotipass, String date) {
		Cursor dCursor = null;
		int idupdate = 0;
		try {
			final String MY_QUERY = "SELECT " + MuseumSQLiteHelper.UPDATE_COL_ID
					+ " FROM " + MuseumSQLiteHelper.UPDATE_TABLE
					+ " WHERE " + MuseumSQLiteHelper.UPDATE_COL_NUMOTIPASS + "=?"
					+ "  AND "  + MuseumSQLiteHelper.UPDATE_COL_DATE + "=?";

			dCursor = database.rawQuery(MY_QUERY, new String[]{String.valueOf(numotipass), date});
			if (dCursor != null) {
				if (dCursor.moveToFirst()) {
					idupdate = dCursor.getInt(dCursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_ID));
				}
				dCursor.close();
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getUpdateId - " + e.getMessage());
		}

		return idupdate;
	}
	
	/**
	 * returns the id of use_pass where numotipass is a parameter
	 * @param numotipass
	 * @return
	 */
	public int getUsePassId(int numotipass) {
		Cursor dCursor = null;
		int idUsePass = 0;
		try {
			final String MY_QUERY = "SELECT " + MuseumSQLiteHelper.USE_PASS_COL_USAGE_ID
					+ " FROM " + MuseumSQLiteHelper.USE_PASS_TABLE
					+ " WHERE " + MuseumSQLiteHelper.USE_PASS_COL_USAGE_OTIPASS + "=?";
			
			dCursor = database.rawQuery(MY_QUERY, new String[]{String.valueOf(numotipass)});
			if (dCursor != null) {
				if (dCursor.moveToFirst()) {
					idUsePass = dCursor.getInt(dCursor.getColumnIndex(MuseumSQLiteHelper.USE_PASS_COL_USAGE_ID));
				}
				dCursor.close();
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getUsePassId - " + e.getMessage());
		}
		
		return idUsePass;
	}


	/**
	 * get provider's services
	 * @return
	 */
	public ArrayList<ProviderService> getAllProviderService() {
		Cursor cursor = null;
		ArrayList<ProviderService> psList = new ArrayList<ProviderService>();
		ProviderService ps;
		try {
			final String MY_QUERY = "SELECT * FROM " + MuseumSQLiteHelper.PROVIDER_SERVICE_TABLE;

			cursor = database.rawQuery(MY_QUERY, null);

			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						ps = new ProviderService();
						ps.setPackageId(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.PROVIDER_SERVICE_COL_PACKAGE_ID)));
						ps.setService(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.PROVIDER_SERVICE_COL_SERVICE)));
						psList.add(ps);
					} while (cursor.moveToNext());
				}
				cursor.close();
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getAllProviderService - " + e.getMessage());
		}
		return psList;
	}

	public Entry insertEntry(String date, int numotipass, short nb, short event, boolean uploaded, int service) {
		Entry entry = null;
		try {
			ContentValues values = new ContentValues();
			values.put(MuseumSQLiteHelper.ENTRY_COL_DATE, date);
			values.put(MuseumSQLiteHelper.ENTRY_COL_OTIPASS, numotipass);
			values.put(MuseumSQLiteHelper.ENTRY_COL_NB, nb);
			values.put(MuseumSQLiteHelper.ENTRY_COL_EVENT, event);
			values.put(MuseumSQLiteHelper.ENTRY_COL_UPLOADED, uploaded);
			values.put(MuseumSQLiteHelper.ENTRY_COL_SERVICE, service);
			long id = database.insert(MuseumSQLiteHelper.ENTRY_TABLE, null, values);
			if (id > 0) {
				return getEntry(id);
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertEntry - " + e.getMessage());
		}
		return entry;
	}

	public Param getParam(long id) {
		Param param = null;
		try {
			Cursor cursor = database.query(MuseumSQLiteHelper.PARAM_TABLE,
					allParamColumns, MuseumSQLiteHelper.PARAM_COL_ID + " = " + id, null, null, null, null);
			if (cursor.moveToFirst()) {
				param = new Param();
				param.setId(cursor.getLong(cursor.getColumnIndex(MuseumSQLiteHelper.PARAM_COL_ID)));
				param.setName(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.PARAM_COL_NAME)));
				param.setCall(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.PARAM_COL_CALL)));
				param.setSoftwareVersion(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.PARAM_COL_SOFT)));
				param.setCategory(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.PARAM_COL_CATEGORY)));
			}
			cursor.close();
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getParam - " + e.getMessage());
		}
		return param;
	}

	public Param insertParam(String name, String time,  String softwareVersion, String currency, String country) {
		Param param = null;
		try {
			ContentValues values = new ContentValues();
			// only one param record
			values.put(MuseumSQLiteHelper.PARAM_COL_ID, 1L);
			values.put(MuseumSQLiteHelper.PARAM_COL_NAME, name);
			values.put(MuseumSQLiteHelper.PARAM_COL_CALL, time);
			values.put(MuseumSQLiteHelper.PARAM_COL_SOFT, softwareVersion);
			long id = database.insert(MuseumSQLiteHelper.PARAM_TABLE, null, values);
			if (id > 0) {
				return getParam(id);
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertParam - " + e.getMessage());
		}
		return param;
	}

	public int updateParam(Param param) {
		int nbRows = 0;
		try {
			ContentValues values = new ContentValues();
			values.put(MuseumSQLiteHelper.PARAM_COL_ID, 1L);
			values.put(MuseumSQLiteHelper.PARAM_COL_NAME, param.getName());
			values.put(MuseumSQLiteHelper.PARAM_COL_CALL, param.getCall());
			values.put(MuseumSQLiteHelper.PARAM_COL_SOFT, param.getSoftwareVersion());
			nbRows = database.update(MuseumSQLiteHelper.PARAM_TABLE, values, MuseumSQLiteHelper.PARAM_COL_ID + " = ?", new String[] { String.valueOf(1) });
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - updateParam - " + e.getMessage());
		}
		return nbRows;
	}

	public Param insertParamObject(Param param) {
		try {
			ContentValues values = new ContentValues();
			values.put(MuseumSQLiteHelper.PARAM_COL_ID, 1L);
			values.put(MuseumSQLiteHelper.PARAM_COL_NAME, param.getName());
			values.put(MuseumSQLiteHelper.PARAM_COL_CALL, param.getCall());
			values.put(MuseumSQLiteHelper.PARAM_COL_SOFT, param.getSoftwareVersion());
			values.put(MuseumSQLiteHelper.PARAM_COL_CATEGORY, param.getCategory());
			long id = database.insert(MuseumSQLiteHelper.PARAM_TABLE, null, values);
			if (id > 0) {
				return getParam(id);
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertParamObject - " + e.getMessage());
		}
		return param;
	}

	public Warning getWarning(long id) {
		Warning warning = null;
		Cursor cursor = null;
		
		try {
			cursor = database.query(MuseumSQLiteHelper.WARNING_TABLE,
					allWarningColumns, MuseumSQLiteHelper.WARNING_COL_ID + " = " + id, null, null, null, null);
			if (cursor.moveToFirst()) {
				warning = new Warning();
				warning.setId(cursor.getLong(0));
				warning.setDate(cursor.getString(1));
				warning.setSerial(cursor.getString(2));
				warning.setEvent(cursor.getShort(3));
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getWarning - " + e.getMessage());
		}
		if (cursor != null) {
			cursor.close();
		}
		return warning;
	}

	public List<Warning> getWarningList() { 
		List<Warning> warningList = new ArrayList<Warning>(); 
		Warning warning;
		Cursor cursor = null;
		
		try {
			String selectQuery = "SELECT  * FROM " + MuseumSQLiteHelper.WARNING_TABLE ; 
			cursor = database.rawQuery(selectQuery, null); 
			if (cursor.moveToFirst()) { 
				do { 
					warning = new Warning();
					warning.setId(cursor.getLong(0));
					warning.setDate(cursor.getString(1));
					warning.setSerial(cursor.getString(2));
					warning.setEvent(cursor.getShort(3));
					warningList.add(warning);
				} while (cursor.moveToNext()); 
			} 
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getWarningList - " + e.getMessage());
		}
		if (cursor != null) {
			cursor.close();
		}
		return warningList; 
	} 

	/**
	 * get all otipass list in db
	 * @return
	 */
	public List<Otipass> getOtipassList(){
		List<Otipass> otipassList = new ArrayList<Otipass>();
		Otipass otipass;
		Cursor cursor = null;
		try {
			String selectQuery = "SELECT  * FROM " + MuseumSQLiteHelper.OTIPASS_TABLE ; 
			cursor = database.rawQuery(selectQuery, null); 
			if (cursor.moveToFirst()) { 
				do { 
					otipass = new Otipass();
					otipass.setNumOtipass(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.OTI_COL_NUMOTIPASS)));
					otipass.setExpiry(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.OTI_COL_EXPIRY)));
					otipass.setPid(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.OTI_COL_PID)));
					otipass.setSerial(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.OTI_COL_SERIAL)));
					otipass.setService(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.OTI_COL_SERVICE)));
					otipassList.add(otipass);
				} while (cursor.moveToNext()); 
			} 
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getOtipassList - " + e.getMessage());
		}
		if (cursor != null) {
			cursor.close();
		}
		return otipassList;
	}

	public int deleteWarning(long id) {
		int nbRows = 0;
		try {
			nbRows = database.delete(MuseumSQLiteHelper.WARNING_TABLE, MuseumSQLiteHelper.WARNING_COL_ID + " = ?", new String[] { String.valueOf(id) });
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - deleteWarning - " + e.getMessage());
		}
		return nbRows;
	}

	public Warning insertWarning(String date, String serial, short event) {
		Warning warning = null;
		try {
			ContentValues values = new ContentValues();
			values.put(MuseumSQLiteHelper.WARNING_COL_ID, 1L);
			values.put(MuseumSQLiteHelper.WARNING_COL_DATE, date);
			values.put(MuseumSQLiteHelper.WARNING_COL_SERIAL, serial);
			values.put(MuseumSQLiteHelper.WARNING_COL_EVENT, event);
			long id = database.insert(MuseumSQLiteHelper.WARNING_TABLE, null, values);
			if (id > 0) {
				return getWarning(id);
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertWarning - " + e.getMessage());
		}
		return warning;
	}

	public Update getUpdate(long id) {
		Update update = null;
		Cursor cursor = null;
		try {
			cursor = database.query(MuseumSQLiteHelper.UPDATE_TABLE,
					allUpdateColumns, MuseumSQLiteHelper.UPDATE_COL_ID + " = " + id, null, null, null, null);
			if (cursor.moveToFirst()) {
				update = new Update();
				update.setId(cursor.getLong(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_ID)));
				update.setDate(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_DATE)));
				update.setType(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_TYPE)));
				update.setNumotipass(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_NUMOTIPASS)));
				update.setPid(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_PID)));
				update.setName(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_NAME)));
				update.setFname(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_FNAME)));
				update.setEmail(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_EMAIL)));
				update.setCountry(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_COUNTRY)));
				update.setPostalCode(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_POSTAL_CODE)));
				update.setNewsletter(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_NEWSLETTER)));
				update.setTwin(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_TWIN)));
				try {
					update.setFareId(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_FAREID)));
					update.setOptionId(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_OPTIONID)));
				} catch (Exception ex){};
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getUpdate - " + e.getMessage());
		}
		if (cursor != null) {
			cursor.close();
		}
		return update;
	}

	public List<Update> getUpdateList() { 
		List<Update> updateList = new ArrayList<Update>(); 
		Update update;
		Cursor cursor = null;
		try {
			String selectQuery = "SELECT  * FROM " + MuseumSQLiteHelper.UPDATE_TABLE ; 
			cursor = database.rawQuery(selectQuery, null); 
			if (cursor.moveToFirst()) { 
				do { 
					update = new Update();
					update.setId(cursor.getLong(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_ID)));
					update.setDate(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_DATE)));
					update.setType(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_TYPE)));
					update.setNumotipass(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_NUMOTIPASS)));
					update.setPid(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_PID)));
					update.setName(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_NAME)));
					update.setFname(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_FNAME)));
					update.setEmail(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_EMAIL)));
					update.setCountry(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_COUNTRY)));
					update.setPostalCode(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_POSTAL_CODE)));
					update.setNewsletter(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_NEWSLETTER)));
					update.setTwin(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_TWIN)));
					try {
						update.setFareId(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_FAREID)));
						update.setOptionId(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.UPDATE_COL_OPTIONID)));
					} catch (Exception ex){};
					updateList.add(update);
				} while (cursor.moveToNext()); 
			} 
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getUpdateList - " + e.getMessage());
		}
		if (cursor != null) {
			cursor.close();
		}
		return updateList; 
	} 




	public long insertUpdate(String date, int type, int numotipass, int pid, PersonalInfo persoInfo, int twin, int fare_id, int option_id) {
		long id = 0;
		try {
			ContentValues values = new ContentValues();
			values.put(MuseumSQLiteHelper.UPDATE_COL_DATE, date);
			values.put(MuseumSQLiteHelper.UPDATE_COL_TYPE, type);
			values.put(MuseumSQLiteHelper.UPDATE_COL_NUMOTIPASS, numotipass);
			values.put(MuseumSQLiteHelper.UPDATE_COL_PID, pid);
			if (persoInfo != null) {
				values.put(MuseumSQLiteHelper.UPDATE_COL_NAME, persoInfo.getName());
				values.put(MuseumSQLiteHelper.UPDATE_COL_FNAME, persoInfo.getFirstName());
				values.put(MuseumSQLiteHelper.UPDATE_COL_EMAIL, persoInfo.getEmail());
				values.put(MuseumSQLiteHelper.UPDATE_COL_COUNTRY, persoInfo.getCountry());
				values.put(MuseumSQLiteHelper.UPDATE_COL_POSTAL_CODE, persoInfo.getPostalCode());
				values.put(MuseumSQLiteHelper.UPDATE_COL_NEWSLETTER, persoInfo.getNewsletter());
			}

			values.put(MuseumSQLiteHelper.UPDATE_COL_TWIN, twin);
			if (fare_id > -1) {
				values.put(MuseumSQLiteHelper.UPDATE_COL_FAREID, fare_id);
				values.put(MuseumSQLiteHelper.UPDATE_COL_OPTIONID, option_id);
			}
			id = database.insert(MuseumSQLiteHelper.UPDATE_TABLE, null, values);

		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertUpdate - " + e.getMessage());
		}
		return id;
	}

	public int deleteUpdate(long id) {
		int nbRows = 0;
		try {
			nbRows = database.delete(MuseumSQLiteHelper.UPDATE_TABLE, MuseumSQLiteHelper.UPDATE_COL_ID + " = ?", new String[] { String.valueOf(id) });
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - deleteUpdate - " + e.getMessage());
		}
		return nbRows;
	}

	/**
	 * deletes entry where id is a parameter
	 * @param id
	 * @return
	 */
	public int deleteEntry(long id) {
		int nbRows = 0;
		try {
			nbRows = database.delete(MuseumSQLiteHelper.ENTRY_TABLE, MuseumSQLiteHelper.ENTRY_COL_ID + " = ?", new String[] { String.valueOf(id) });
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - deleteEntry - " + e.getMessage());
		}
		return nbRows;
	}
	
	/**
	 * deletes a row of use_pass where id is a parameter
	 * @param id
	 * @return
	 */
	public int deleteUsePass(long id) {
		int nbRows = 0;
		try {
			nbRows = database.delete(MuseumSQLiteHelper.USE_PASS_TABLE, MuseumSQLiteHelper.USE_PASS_COL_USAGE_ID + " = ?", new String[] { String.valueOf(id) });
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - deleteUsePass - " + e.getMessage());
		}
		return nbRows;
	}


	public Tablet getTablet(long id) {
		Tablet tablet = null;
		Cursor cursor = null;
		try {
			cursor = database.query(MuseumSQLiteHelper.TABLET_TABLE,
					allTabletColumns, MuseumSQLiteHelper.TABLET_COL_ID + " = " + id, null, null, null, null);
			if (cursor.moveToFirst()) {
				tablet = new Tablet();
				tablet.setId(cursor.getLong(0));
				tablet.setNumSequence(cursor.getInt(1));
				tablet.setUploadTime(cursor.getString(2));
				tablet.setDownloadTime(cursor.getString(3));
				try {
					tablet.setDbModel(cursor.getInt(4));
				} catch (Exception ex) {};
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getTablet - " + e.getMessage());
		}
		if (cursor != null) {
			cursor.close();
		}
		return tablet;
	}

	public Tablet insertTablet(int numSequence, String uploadTime, String downloadTime) {
		Tablet tablet = null;
		try {
			ContentValues values = new ContentValues();
			values.put(MuseumSQLiteHelper.TABLET_COL_NUMSEQUENCE, numSequence);
			values.put(MuseumSQLiteHelper.TABLET_COL_UPLOAD_TIME, uploadTime);
			values.put(MuseumSQLiteHelper.TABLET_COL_DOWNLOAD_TIME, downloadTime);
			values.put(MuseumSQLiteHelper.TABLET_COL_BDD_MODEL, 0);
			long id = database.insert(MuseumSQLiteHelper.TABLET_TABLE, null, values);
			if (id > 0) {
				tablet =  getTablet(id);
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertTablet - " + e.getMessage());
		}
		return tablet;
	}

	public int updateTablet(Tablet tablet) {
		int nbRows = 0;
		try {
			ContentValues values = new ContentValues();
			values.put(MuseumSQLiteHelper.TABLET_COL_NUMSEQUENCE, tablet.getNumSequence());
			values.put(MuseumSQLiteHelper.TABLET_COL_UPLOAD_TIME, tablet.getDownloadTime());
			values.put(MuseumSQLiteHelper.TABLET_COL_DOWNLOAD_TIME, tablet.getDownloadTime());
			try {
				values.put(MuseumSQLiteHelper.TABLET_COL_BDD_MODEL, tablet.getDbModel());
			} catch (Exception ex){};
			nbRows = database.update(MuseumSQLiteHelper.TABLET_TABLE, values, MuseumSQLiteHelper.TABLET_COL_ID + " = ?", new String[] { String.valueOf(tablet.getId()) });
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - updateTablet - " + e.getMessage());
		}
		return nbRows;
	}

	public int updateWarning(Warning warning) {
		int nbRows = 0;
		try {
			ContentValues values = new ContentValues();
			values.put(MuseumSQLiteHelper.WARNING_COL_DATE, warning.getDate());
			values.put(MuseumSQLiteHelper.WARNING_COL_SERIAL, warning.getSerial());
			values.put(MuseumSQLiteHelper.WARNING_COL_EVENT, warning.getEvent());
			nbRows = database.update(MuseumSQLiteHelper.WARNING_TABLE, values, MuseumSQLiteHelper.WARNING_COL_ID + " = ?", new String[] { String.valueOf(warning.getId()) });
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - updateWarning - " + e.getMessage());
		}
		return nbRows;
	}

	public void flushTable(String table) {
		try {
			database.execSQL(MuseumSQLiteHelper.FLUSH_TABLE + table);
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - FlushTable - " + e.getMessage());
		}
	}

	public Stock getStock(long id) {
		Stock stock = null;
		Cursor cursor = null;
		try {
			cursor = database.query(MuseumSQLiteHelper.STOCK_TABLE,
					allStockColumns, MuseumSQLiteHelper.STOCK_COL_ID + " = " + id, null, null, null, null);
			if (cursor.moveToFirst()) {
				stock = new Stock();
				stock.setId(cursor.getLong(cursor.getColumnIndex(MuseumSQLiteHelper.STOCK_COL_ID)));
				stock.setProviderId(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.STOCK_COL_PROVIDER_ID)));
				stock.setNbCards(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.STOCK_COL_NB_CARDS)));
				stock.setThreshold(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.STOCK_COL_THRESHOLD)));
				stock.setAlert(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.STOCK_COL_ALERT)));
			}
			cursor.close();
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getStock - " + e.getMessage());
		}
		return stock;
	}

	public Stock insertStockObject(Stock stock) {
		try {
			ContentValues values = new ContentValues();
			values.put(MuseumSQLiteHelper.STOCK_COL_ID, 1L);
			values.put(MuseumSQLiteHelper.STOCK_COL_PROVIDER_ID, stock.getProviderId());
			values.put(MuseumSQLiteHelper.STOCK_COL_NB_CARDS, stock.getNbCards());
			values.put(MuseumSQLiteHelper.STOCK_COL_THRESHOLD, stock.getThreshold());
			values.put(MuseumSQLiteHelper.STOCK_COL_ALERT, stock.getAlert());
			long id = database.insert(MuseumSQLiteHelper.STOCK_TABLE, null, values);
			if (id > 0) {
				stock =  getStock(id);
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertStock - " + e.getMessage());
		}
		return stock;
	}

	public int getNbCards() {
		int nbCards = 0;
		Cursor cursor = null;
		cursor = database.query(MuseumSQLiteHelper.OTIPASS_TABLE,
				allOtipassColumns, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			nbCards = cursor.getCount();
		}
		if (cursor != null) {
			cursor.close();
		}
		
		return nbCards;
	}

	public Msg getMessage(long id) {
		Msg message = null;
		Cursor cursor = null;
		try {
			cursor = database.query(MuseumSQLiteHelper.MSG_TABLE,
					allMessageColumns, MuseumSQLiteHelper.MSG_COL_ID + " = " + id, null, null, null, null);
			if (cursor.moveToFirst()) {
				message = new Msg();
				message.setId((int)cursor.getLong(0));
				message.setMsg(cursor.getString(1));
				message.setLang(cursor.getString(2));
				message.setStartDate(cursor.getString(3));
				message.setEndDate(cursor.getString(4));
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getMessage - " + e.getMessage());
		}
		if (cursor != null) {
			cursor.close();
		}
		return message;
	}

	public List<Msg> getMessageList() { 
		List<Msg> messageList = new ArrayList<Msg>(); 
		Msg message;
		Cursor cursor = null;
		try {
			String selectQuery = "SELECT  * FROM " + MuseumSQLiteHelper.MSG_TABLE ; 
			cursor = database.rawQuery(selectQuery, null); 
			if (cursor.moveToFirst()) { 
				do { 
					message = new Msg();
					message.setId((int)cursor.getLong(0));
					message.setMsg(cursor.getString(1));
					message.setLang(cursor.getString(2));
					message.setStartDate(cursor.getString(3));
					message.setEndDate(cursor.getString(4));
					messageList.add(message);
				} while (cursor.moveToNext()); 
			} 
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getMessageList - " + e.getMessage());
		}
		if (cursor != null) {
			cursor.close();
		}
		return messageList; 
	} 


	public ArrayList<PackageObject> getPackage(){

		ArrayList<PackageObject> listPackages = new ArrayList<PackageObject>();
		PackageObject po;
		Cursor cursor = null;
		try {
			String selectQuery = "SELECT  * FROM " + MuseumSQLiteHelper.PACKAGE_TABLE ; 
			cursor = database.rawQuery(selectQuery, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						po = new PackageObject();
						po.setId(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_ID)));
						po.setName(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_NAME)));
						po.setNbChild(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_CHILD)));
						po.setDuration(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_DURATION)));
						po.setPeriod(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_PERIOD)));
						po.setPrice(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_PRICE)));
						po.setRef(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_REF)));
						listPackages.add(po);
					} while (cursor.moveToNext());

				}
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getPackage - " + e.getMessage());
		}

		if (cursor != null) {
			cursor.close();
		}
		return listPackages;
	}

	public ArrayList<PackageObject> getPackage2Sale(){

		ArrayList<PackageObject> listPackages = new ArrayList<PackageObject>();
		PackageObject po;
		Cursor cursor = null;
		try {
			String selectQuery = "SELECT  * FROM " + MuseumSQLiteHelper.PACKAGE_TABLE + " WHERE " + MuseumSQLiteHelper.PACKAGE_COL_REF + " NOT LIKE 'PR%' AND " + MuseumSQLiteHelper.PACKAGE_COL_REF + " NOT LIKE 'PD%' ORDER BY CASE WHEN " + MuseumSQLiteHelper.PACKAGE_COL_REF + " LIKE '%PH%' THEN -1 WHEN " + MuseumSQLiteHelper.PACKAGE_COL_REF + " LIKE '%PG%' THEN 0 ELSE "+MuseumSQLiteHelper.PACKAGE_COL_DURATION+" END desc ";
			cursor = database.rawQuery(selectQuery, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						po = new PackageObject();
						po.setId(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_ID)));
						po.setName(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_NAME)));
						po.setDuration(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_DURATION)));
						po.setPeriod(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_PERIOD)));
						po.setRef(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_COL_REF)));
						listPackages.add(po);
					} while (cursor.moveToNext());

				}
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getPackage2Sale - " + e.getMessage());
		}

		if (cursor != null) {
			cursor.close();
		}
		return listPackages;
	}


	public Msg insertMessageObject(Msg message) {
		try {
			ContentValues values = new ContentValues();
			values.put(MuseumSQLiteHelper.MSG_COL_TEXT, message.getMsg());
			values.put(MuseumSQLiteHelper.MSG_COL_LANG, message.getLang());
			values.put(MuseumSQLiteHelper.MSG_COL_START_DATE, message.getStartDate());
			values.put(MuseumSQLiteHelper.MSG_COL_END_DATE, message.getEndDate());
			long id = database.insert(MuseumSQLiteHelper.MSG_TABLE, null, values);
			if (id > 0) {
				message =  getMessage(id);
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertMessage - " + e.getMessage());
		}
		return message;
	}

	public int insertMessageList(List<Msg> messageList) {
		Msg message = null;
		long id;
		int i = 0;
		try {
			ContentValues values = new ContentValues();
			for (i=0; i<messageList.size(); i++) {
				message = messageList.get(i);
				values.clear();
				values.put(MuseumSQLiteHelper.MSG_COL_TEXT, message.getMsg());
				values.put(MuseumSQLiteHelper.MSG_COL_LANG, message.getLang());
				values.put(MuseumSQLiteHelper.MSG_COL_START_DATE, message.getStartDate());
				values.put(MuseumSQLiteHelper.MSG_COL_END_DATE, message.getEndDate());
				id = database.insert(MuseumSQLiteHelper.MSG_TABLE, null, values);
				if (id < 1L) {
					Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertUserList - Cannot insert Message list");
					break;
				}
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertMessageList - " + e.getMessage());
		}
		return i;
	}

	public long insertService(ServicePass service){
		long id = 0;
		try {
			ContentValues values = new ContentValues();
			values.put(MuseumSQLiteHelper.SERVICE_COL_ID, service.getId());
			values.put(MuseumSQLiteHelper.SERVICE_COL_TYPE, service.getType());
			values.put(MuseumSQLiteHelper.SERVICE_COL_NAME, service.getName());
			id = database.insert(MuseumSQLiteHelper.SERVICE_TABLE, null, values);
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertService - " + e.getMessage());
		}

		return id;
	}

	public long insertServiceList(List<ServicePass> serviceList){
		long id = 0;
		int i=0;
		ServicePass service;
		database.beginTransaction();
		
		try {
			ContentValues values = new ContentValues();
			for (i=0; i<serviceList.size(); i++) {
				service = serviceList.get(i);
				try { // instead of IsServiceExist()
					values.put(MuseumSQLiteHelper.SERVICE_COL_ID, service.getId());
					values.put(MuseumSQLiteHelper.SERVICE_COL_TYPE, service.getType());
					values.put(MuseumSQLiteHelper.SERVICE_COL_NAME, service.getName());
					id = database.insert(MuseumSQLiteHelper.SERVICE_TABLE, null, values);
				} catch (Exception e) {}
			}
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertServiceList - " + e.getMessage());
		}
		database.endTransaction();
		return i;
	}
	
	

	public long insertPackageList(List<PackageObject> packageList){
		long id = 0;
		int i=0;
		PackageObject packageObject;
		database.beginTransaction();
		
		try {
			ContentValues values = new ContentValues();
			for (i=0; i<packageList.size(); i++) {
				packageObject = packageList.get(i);
				values.put(MuseumSQLiteHelper.PACKAGE_COL_ID, packageObject.getId());
				values.put(MuseumSQLiteHelper.PACKAGE_COL_NAME, packageObject.getName());
				values.put(MuseumSQLiteHelper.PACKAGE_COL_CHILD, packageObject.getNbChild());
				values.put(MuseumSQLiteHelper.PACKAGE_COL_DURATION, packageObject.getDuration());
				values.put(MuseumSQLiteHelper.PACKAGE_COL_PERIOD, packageObject.getPeriod());
				try {
					values.put(MuseumSQLiteHelper.PACKAGE_COL_PRICE, packageObject.getPrice());
				} catch (Exception ex) {
					values.put(MuseumSQLiteHelper.PACKAGE_COL_PRICE, 0);
				}
				values.put(MuseumSQLiteHelper.PACKAGE_COL_REF, packageObject.getRef());
				id = database.insert(MuseumSQLiteHelper.PACKAGE_TABLE, null, values);
			}
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertPackageList - " + e.getMessage());
		}
		database.endTransaction();
		return i;
	}
	
	
	public long insertPackageService(PackageService ps){
		long id = 0;
		try {
			ContentValues values = new ContentValues();
			values.put(MuseumSQLiteHelper.PACKAGE_SERVICE_COL_PACKAGE_ID, ps.getPackageId());
			values.put(MuseumSQLiteHelper.PACKAGE_SERVICE_COL_SERVICE_ID, ps.getServiceId());
			values.put(MuseumSQLiteHelper.PACKAGE_SERVICE_COL_NUMBER, ps.getNumber());
			id = database.insert(MuseumSQLiteHelper.PACKAGE_SERVICE_TABLE, null, values);
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertPackage - " + e.getMessage());
		}

		return id;
	}

	public long insertPackageServiceList(List<PackageService> packageServiceList){
		long id = 0;
		int i=0;
		PackageService ps;
		database.beginTransaction();
		
		try {
			ContentValues values = new ContentValues();
			for (i=0; i<packageServiceList.size(); i++) {
				try {
					ps = packageServiceList.get(i);
					values.put(MuseumSQLiteHelper.PACKAGE_SERVICE_COL_PACKAGE_ID, ps.getPackageId());
					values.put(MuseumSQLiteHelper.PACKAGE_SERVICE_COL_SERVICE_ID, ps.getServiceId());
					values.put(MuseumSQLiteHelper.PACKAGE_SERVICE_COL_NUMBER, ps.getNumber());
					id = database.insert(MuseumSQLiteHelper.PACKAGE_SERVICE_TABLE, null, values);
				} catch (Exception e) {}
			}
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertPackageServiceList - " + e.getMessage());
		}
		database.endTransaction();
		return i;
	}
	

	
	public long insertProviderService(ProviderService ps){
		long id = 0;
		try {
			ContentValues values = new ContentValues();
			values.put(MuseumSQLiteHelper.PROVIDER_SERVICE_COL_PACKAGE_ID, ps.getPackageId());
			values.put(MuseumSQLiteHelper.PROVIDER_SERVICE_COL_SERVICE, ps.getService());
			id = database.insert(MuseumSQLiteHelper.PROVIDER_SERVICE_TABLE, null, values);
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertProviderService - " + e.getMessage());
		}

		return id;
	}

	public Discount getDiscount(long id) {
		Discount discount = null;
		Cursor cursor = null;
		try {
			cursor = database.query(MuseumSQLiteHelper.DISCOUNT_TABLE,
					allDiscountColumns, MuseumSQLiteHelper.DISCOUNT_COL_ID + " = " + id, null, null, null, null);
			if (cursor.moveToFirst()) {
				discount = new Discount();
				discount.setAmountEUR(cursor.getDouble(1));
				discount.setAmountFCH(cursor.getDouble(2));
				discount.setStartDate(cursor.getString(3));
				discount.setEndDate(cursor.getString(4));
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getDiscount - " + e.getMessage());
		}
		if (cursor != null) {
			cursor.close();
		}
		return discount;
	}

	public Discount insertDiscountObject(Discount discount) {
		try {
			ContentValues values = new ContentValues();
			values.put(MuseumSQLiteHelper.DISCOUNT_COL_ID, 1L);
			values.put(MuseumSQLiteHelper.DISCOUNT_COL_AMOUNT_EUR, discount.getAmountEUR());
			values.put(MuseumSQLiteHelper.DISCOUNT_COL_AMOUNT_FCH, discount.getAmountFCH());
			values.put(MuseumSQLiteHelper.DISCOUNT_COL_START_DATE, discount.getStartDate());
			values.put(MuseumSQLiteHelper.DISCOUNT_COL_END_DATE, discount.getEndDate());
			long id = database.insert(MuseumSQLiteHelper.DISCOUNT_TABLE, null, values);
			if (id > 0) {
				return getDiscount(id);
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertDiscountObject - " + e.getMessage());
		}
		return discount;
	}


	public void StartTransaction() {
		database.beginTransaction();
	}

	public void endTransaction(boolean success) {
		if (success) {
			database.setTransactionSuccessful();
		}
		database.endTransaction();
	}

	public void databaseBackupSDCard() {
		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			if (sd.canWrite()) {
				String currentDBPath = "//data//"
						+ "com.otipass.adt67" + "//databases//"
						+ MuseumSQLiteHelper.DATABASE_NAME;
				String backupDBPath = "//sqliteDatabase/" + MuseumSQLiteHelper.DATABASE_NAME;
				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(sd, backupDBPath);

				if (currentDB.exists()) {
					FileChannel src = new FileInputStream(currentDB)
					.getChannel();
					FileChannel dst = new FileOutputStream(backupDB)
					.getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
				}
			}

		} catch (Exception e) {
		}
	}

	public int cancelEntry(int numotipass, String date) {
		int result = cSaveOK;
		long update_nb;
		try {
			update_nb = insertUpdate(date, Constants.UPD_CANCEL_ENTRY, numotipass, -1, null, 0, -1, -1);
			if (update_nb < 1) {
				result = cSaveKO;
				Log.e(Constants.TAG, MuseumDbAdapter.class.getName() + " - cancelEntry failed");
			}

		} catch (Exception e) {
			result = cSaveKO;
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - cancelEntry - " + e.getMessage());
		}
		return result;
	}

	public int cancelOtipass(EntrySale entry_sale) {
		int result = cSaveOK, nb;
		long nb_update;
		try {
			if (entry_sale.getType() == Constants.SALE_TYPE) {

				String date = tools.formatSQLDate(entry_sale.getDate());
				nb_update = insertUpdate(date, Constants.UPD_CANCEL, entry_sale.getNumOtipass(), -1, null, 0, -1, -1);
				if (nb_update < 1) {
					result = cSaveKO;
					Log.e(Constants.TAG, MuseumDbAdapter.class.getName() + " - cancelOtipass failed");
				}else {
			
					// update Otipass
					Otipass otipass = getOtipass((long)entry_sale.getNumOtipass());
					if (otipass != null) {
						otipass.setStatus((short)Constants.PASS_CREATED);
						otipass.setService("");
						if ((nb = updateOtipass(otipass)) < 1) {
							result = cUpdateOtipassFailed;
						}
					}else {
						result = cOtipassKO;
					}
				}
			}else if ((entry_sale.getType() == Constants.ENTRY_TYPE)){
				try {

					String date = tools.formatSQLDate(entry_sale.getDate());

					int idEntry = getEntryId(entry_sale.getNumOtipass(), date);

					nb_update = insertUpdate(date, Constants.UPD_CANCEL_ENTRY, entry_sale.getNumOtipass(), -1, null, 0, -1, -1);
					if (nb_update < 1) {
						result = cSaveKO;
						Log.e(Constants.TAG, MuseumDbAdapter.class.getName() + " - cancelEntry failed");
					}else {
						if (idEntry > 0) {
							deleteEntry((int)idEntry);
						}

						// update Otipass
						Otipass otipass = getOtipass((long)entry_sale.getNumOtipass());
						if (otipass != null) {

							String otipass_service = otipass.getService();
							String [] services = otipass_service.split(";");

							otipass_service = "";
							for (int i = 0; i < services.length; i++) {
								String [] serviceToUpdate = services[i].split(":");
								if (serviceToUpdate[0].equals(String.valueOf(entry_sale.getServiceId()))) {
									serviceToUpdate[1] = String.valueOf(Integer.valueOf(serviceToUpdate[1]) + 1);
								}
								otipass_service = otipass_service.concat(serviceToUpdate[0].toString().concat(":" + serviceToUpdate[1].toString() + ";"));
							}
							int nb_entry = isOtipassExists((int)otipass.getNumOtipass());
							if (nb_entry == 0) {
								otipass.setStatus((short)Constants.PASS_CREATED);
								otipass.setExpiry("");
							}
							otipass.setService(otipass_service);
							if ((nb = updateOtipass(otipass)) < 1) {
								result = cUpdateOtipassFailed;
							}
						}else {
							result = cOtipassKO;
						}
					}

				} catch (Exception e) {
					result = cSaveKO;
					Log.e(TAG, MuseumDbAdapter.class.getName() + " - cancelEntry - " + e.getMessage());
				}
			}

		} catch (Exception e) {
			result = cSaveKO;
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - cancelOtipass - " + e.getMessage());
		}
		return result;
	}
	public Wl insertWl(Wl w) {
		Wl wl = null;
		try {
			ContentValues values = new ContentValues();

			values.put(MuseumSQLiteHelper.WL_COL_ID, w.getId());
			values.put(MuseumSQLiteHelper.WL_COL_DATE, w.getDate());
			values.put(MuseumSQLiteHelper.WL_COL_NBSTEPS, w.getNbsteps());
			values.put(MuseumSQLiteHelper.WL_COL_NBCARDS, w.getNbcards());
			values.put(MuseumSQLiteHelper.WL_COL_NUMSEQUENCE, w.getNumsequence());
			values.put(MuseumSQLiteHelper.WL_COL_STATUS, w.getStatus());
			long id = database.insert(MuseumSQLiteHelper.WL_TABLE, null, values);
			if (id > 0) {
				return getWl(id);
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertWl - " + e.getMessage());
		}
		return wl;
	}


	public Wl getWl(long id) {
		Wl wl = null;
		Cursor cursor = null;

		try {
			cursor = database.query(MuseumSQLiteHelper.WL_TABLE,
					allWlColumns, MuseumSQLiteHelper.WL_COL_ID + " = " + id, null, null, null, null);
			if (cursor.moveToFirst()) {
				wl = new Wl();
				wl.setId(cursor.getLong(0));
				wl.setDate(cursor.getString(1));
				wl.setNbsteps(cursor.getInt(2));
				wl.setNbcards(cursor.getInt(3));
				wl.setNumsequence(cursor.getInt(4));
				wl.setStatus(cursor.getInt(5));
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getWl - " + e.getMessage());
		}
		if (cursor != null) {
			cursor.close();
		}

		return wl;
	}

	public int deleteWl(long id) {
		int nbRows = 0;
		try {
			nbRows = database.delete(MuseumSQLiteHelper.WL_TABLE, MuseumSQLiteHelper.WL_COL_ID + " = ?", new String[] { String.valueOf(id) });
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - deleteWl - " + e.getMessage());
		}
		return nbRows;
	}

	public int updateWl(Wl wl) {
		int nbRows = 0;
		try {
			ContentValues values = new ContentValues();
			values.put(MuseumSQLiteHelper.WL_COL_DATE, wl.getDate());
			values.put(MuseumSQLiteHelper.WL_COL_NBSTEPS, wl.getNbsteps());
			values.put(MuseumSQLiteHelper.WL_COL_NBCARDS, wl.getNbcards());
			values.put(MuseumSQLiteHelper.WL_COL_NUMSEQUENCE, wl.getNumsequence());
			values.put(MuseumSQLiteHelper.WL_COL_STATUS, wl.getStatus());
			nbRows = database.update(MuseumSQLiteHelper.WL_TABLE, values, MuseumSQLiteHelper.WL_COL_ID + " = ?", new String[] { String.valueOf(wl.getId()) });
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - updateWl - " + e.getMessage());
		}
		return nbRows;
	}

	public long insertPackageFareList(List<PackageFare> packageFareList){
		long id = 0;
		int i=0;
		PackageFare packageFare;
		database.beginTransaction();

		try {
			ContentValues values = new ContentValues();
			for (i=0; i<packageFareList.size(); i++) {
				packageFare = packageFareList.get(i);
				values.put(MuseumSQLiteHelper.PACKAGE_FARE_COL_PACKAGE_ID, packageFare.getPackageId());
				values.put(MuseumSQLiteHelper.PACKAGE_FARE_COL_FARE_ID, packageFare.getFareId());
				values.put(MuseumSQLiteHelper.PACKAGE_FARE_COL_OPTION_ID, packageFare.getOptionId());
				values.put(MuseumSQLiteHelper.PACKAGE_FARE_COL_PRICE, packageFare.getPrice());
				id = database.insert(MuseumSQLiteHelper.PACKAGE_FARE_TABLE, null, values);
			}
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertPackageFareList - " + e.getMessage());
		}
		database.endTransaction();
		return i;
	}

	public long insertFareList(List<Fare> fareList){
		long id = 0;
		int i=0;
		Fare fare;
		database.beginTransaction();

		try {
			ContentValues values = new ContentValues();
			for (i=0; i<fareList.size(); i++) {
				fare = fareList.get(i);
				values.put(MuseumSQLiteHelper.FARE_COL_ID, fare.getId());
				values.put(MuseumSQLiteHelper.FARE_COL_NAME, fare.getName());
				values.put(MuseumSQLiteHelper.FARE_COL_REF, fare.getReference());
				id = database.insert(MuseumSQLiteHelper.FARE_TABLE, null, values);
			}
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertFareList - " + e.getMessage());
		}
		database.endTransaction();
		return i;
	}

	public long insertOptionList(List<Option> optionList){
		long id = 0;
		int i=0;
		Option option;
		database.beginTransaction();

		try {
			ContentValues values = new ContentValues();
			for (i=0; i<optionList.size(); i++) {
				option = optionList.get(i);
				values.put(MuseumSQLiteHelper.OPTION_COL_ID, option.getId());
				values.put(MuseumSQLiteHelper.OPTION_COL_NAME, option.getName());
				values.put(MuseumSQLiteHelper.OPTION_COL_REF, option.getReference());
				id = database.insert(MuseumSQLiteHelper.OPTION_TABLE, null, values);
			}
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - insertOptionList - " + e.getMessage());
		}
		database.endTransaction();
		return i;
	}

	public List<PackageFare> getPackageFareList(){
		List<PackageFare> packageFareList = new ArrayList<PackageFare>();
		PackageFare packageFare;
		Cursor cursor = null;
		try {
			String selectQuery = "SELECT  * FROM " + MuseumSQLiteHelper.PACKAGE_FARE_TABLE ;
			cursor = database.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {
					packageFare = new PackageFare();
					packageFare.setPackageId(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_FARE_COL_PACKAGE_ID)));
					packageFare.setFareId(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_FARE_COL_FARE_ID)));
					packageFare.setOptionId(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_FARE_COL_OPTION_ID)));
					packageFare.setPrice(cursor.getDouble(cursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_FARE_COL_PRICE)));
					packageFareList.add(packageFare);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getPackageFareList - " + e.getMessage());
		}
		if (cursor != null) {
			cursor.close();
		}
		return packageFareList;
	}

	public List<Fare> getFareList(){
		List<Fare> fareList = new ArrayList<Fare>();
		Fare fare;
		Cursor cursor = null;
		try {
			String selectQuery = "SELECT  * FROM " + MuseumSQLiteHelper.FARE_TABLE ;
			cursor = database.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {
					fare = new Fare();
					fare.setId(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.FARE_COL_ID)));
					fare.setName(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.FARE_COL_NAME)));
					fare.setReference(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.FARE_COL_REF)));
					fareList.add(fare);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getFareList - " + e.getMessage());
		}
		if (cursor != null) {
			cursor.close();
		}
		return fareList;
	}

	public List<Option> getOptionList(){
		List<Option> optionList = new ArrayList<Option>();
		Option option;
		Cursor cursor = null;
		try {
			String selectQuery = "SELECT  * FROM " + MuseumSQLiteHelper.OPTION_TABLE ;
			cursor = database.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {
					option = new Option();
					option.setId(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.OPTION_COL_ID)));
					option.setName(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.OPTION_COL_NAME)));
					option.setReference(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.OPTION_COL_REF)));
					optionList.add(option);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getOptionList - " + e.getMessage());
		}
		if (cursor != null) {
			cursor.close();
		}
		return optionList;
	}

	public double getPackagePrice(int idpackage, int idfare, int optionfare) {
		Cursor dCursor = null;
		double price = -1;
		try {
			final String MY_QUERY = "SELECT " + MuseumSQLiteHelper.PACKAGE_FARE_COL_PRICE
					+ " FROM " + MuseumSQLiteHelper.PACKAGE_FARE_TABLE
					+ " WHERE " + MuseumSQLiteHelper.PACKAGE_FARE_COL_PACKAGE_ID + "=?"
					+ "  AND "  + MuseumSQLiteHelper.PACKAGE_FARE_COL_FARE_ID + "=?"
					+ "  AND "  + MuseumSQLiteHelper.PACKAGE_FARE_COL_OPTION_ID + "=?"
					;

			dCursor = database.rawQuery(MY_QUERY, new String[]{String.valueOf(idpackage), String.valueOf(idfare), String.valueOf(optionfare)});
			if (dCursor != null) {
				if (dCursor.moveToFirst()) {
					price = dCursor.getDouble(dCursor.getColumnIndex(MuseumSQLiteHelper.PACKAGE_FARE_COL_PRICE));
				}
				dCursor.close();
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getPackageFare - " + e.getMessage());
		}
		return price;
	}

	public Fare getFare(int id) {
		Fare fare = null;
		Cursor cursor = null;

		try {
			cursor = database.query(MuseumSQLiteHelper.FARE_TABLE,
					allFareColumns, MuseumSQLiteHelper.FARE_COL_ID + " = " + id, null, null, null, null);
			if (cursor.moveToFirst()) {
				fare = new Fare();
				fare.setId(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.FARE_COL_ID)));
				fare.setName(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.FARE_COL_NAME)));
				fare.setReference(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.FARE_COL_REF)));
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getFare - " + e.getMessage());
		}
		if (cursor != null) {
			cursor.close();
		}
		return fare;
	}
	public Option getOption(int id) {
		Option option = null;
		Cursor cursor = null;

		try {
			cursor = database.query(MuseumSQLiteHelper.OPTION_TABLE,
					allOptionColumns, MuseumSQLiteHelper.OPTION_COL_ID + " = " + id, null, null, null, null);
			if (cursor.moveToFirst()) {
				option = new Option();
				option.setId(cursor.getInt(cursor.getColumnIndex(MuseumSQLiteHelper.OPTION_COL_ID)));
				option.setName(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.OPTION_COL_NAME)));
				option.setReference(cursor.getString(cursor.getColumnIndex(MuseumSQLiteHelper.OPTION_COL_REF)));
			}
		} catch (Exception e) {
			Log.e(TAG, MuseumDbAdapter.class.getName() + " - getOption - " + e.getMessage());
		}
		if (cursor != null) {
			cursor.close();
		}
		return option;
	}

}
