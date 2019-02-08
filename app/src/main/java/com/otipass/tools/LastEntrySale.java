package com.otipass.tools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import models.Constants;
import android.content.Context;
import android.util.Log;

import com.otipass.adt67.R;
import com.otipass.sql.MuseumDbAdapter;

public class LastEntrySale {
	public static final int cOK = 0;
	public static final int cCancelTooLate = 1;
	public static final int cNoSaleToCancel = 2;
	public static final int cNoEntryToCancel = 3;
	public static final int cSaveFailed = 4;


	private Calendar saleEntryDate;
	private boolean saleEntry2Cancel = false;
	private List<OtipassCard> cardSold = null;
	private List<EntrySale> entrySaleList = null;
	private Context context;
	private EntrySale es;

	// use to make a singleton, only one instance class
	private static LastEntrySale mInstance = null;

	public LastEntrySale(Context context) {
		this.context = context;
	}

	// use this function to call an instance 
	public static LastEntrySale getInstance(Context context) {          
		if (mInstance == null) {      
			mInstance = new LastEntrySale(context);    
		}    
		return mInstance;  
	}

	public void setEntrySale2Cancel(boolean val) {
		saleEntry2Cancel = val;
	}

	public boolean getEntrySale2Cancel() {
		return saleEntry2Cancel;
	}

	public void recordLastEntrySale(EntrySale entry_sale) {

		if (this.entrySaleList == null) {
			this.entrySaleList = new ArrayList<EntrySale>();
		}
		this.entrySaleList.add(entry_sale);

		saleEntry2Cancel = true;
	}

	public String getLastEntrySale(Calendar date) {
		String s = "";
		if (saleEntry2Cancel) {
			if (entrySaleList.size() > 0) {

				EntrySale entry_sale = entrySaleList.get(entrySaleList.size() - 1);
				es = entrySaleList.get(entrySaleList.size() - 1);
				Calendar tmpDate = (Calendar) entry_sale.getDate().clone();
				// sale or entry can be cancelled maximum 15 minutes after it occured
				tmpDate.add(Calendar.MINUTE, Constants.CANCEL_DELAY);
				if (date.before(tmpDate)) {
					if (entry_sale.getType() == Constants.ENTRY_TYPE) {
						s = context.getString(R.string.entry_to_cancel);
					}else {
						s = context.getString(R.string.sale_to_cancel);
					}
					
		    		s += ' ' +  String.valueOf(entry_sale.getNumOtipass());
				}else {
					saleEntry2Cancel = false;
					entrySaleList.remove(entrySaleList.size() - 1);
				}
			}
		} 
		return s;
	}

	public int cancelLastEntrySale() {
		int error = cOK, result = cOK;
		MuseumDbAdapter dbAdapter = new MuseumDbAdapter(context);
		dbAdapter.open();
		try {
			dbAdapter.StartTransaction();
			EntrySale entry_sale = entrySaleList.get(entrySaleList.size() - 1);
			result = dbAdapter.cancelOtipass(entry_sale);
			
			if (result != cOK) {
				error = cSaveFailed;
			}else {
				this.entrySaleList.remove(entrySaleList.size() - 1);
			} 
		} catch (Exception ex) {
			Log.e(Constants.TAG, LastEntrySale.class.getName() + " - cancelLastEntrySale - " + ex.getMessage());
			error = cSaveFailed;
		}
		finally {
			boolean commit = (error == cOK);
			dbAdapter.endTransaction(commit);
		}
		return error;
	}
	
	public EntrySale getEntrySale(){
		return es;
	}
}
