/**
================================================================================

    OTIPASS
    Pass Museum Application.

    package com.otipass.tools

    @copyright Copyright (c) Otipass 2011. All rights reserved.
    @author ED ($Author: ede $)

    @version $Rev: 3370 $
    $Id: Messages.java 3370 2014-03-31 08:17:23Z ede $

================================================================================
 */
package com.otipass.tools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import models.Constants;
import android.content.Context;
import android.util.Log;

import com.otipass.adt67.Footer;
import com.otipass.adt67.R;
import com.otipass.sql.Msg;
import com.otipass.sql.MuseumDbAdapter;
import com.otipass.sql.Stock;
import com.otipass.sql.Tablet;
import com.otipass.sql.Warning;

public class Messages {
	public static final int cMsgGeneral = 1; 
	public static final int cMsgStock = 2;
	public static final int cMsgSWversion = 3;
	public static final int cMsgSynchro = 4;
	public static final int cMsgDB = 5;

	// use mInstance to make a singleton
	private static Messages mInstance = null;

	private List<Msg> messagesList;

	// use this function to call an instance 
	public static Messages getInstance() {          
		if (mInstance == null) {      
			mInstance = new Messages();    
		}    
		return mInstance;  
	}

	// private constructor to use only getInstance() when creating an instance
	private Messages() {
		messagesList = new ArrayList<Msg>(); 
	}

	private int getMaxId() {
		int id = 0;
		for (Msg msg : messagesList) {
			if(msg.getId() > id){
				id = msg.getId();
			}
		}		
		return id;
	}

	public void addMessage(String msg, int type, int minProfile) {
		int id = getMaxId() + 1;
		Msg message = new Msg(msg, type, id, minProfile);
		messagesList.add(message);
	}

	public void clearMessages() {
		messagesList.clear();
	}

	public void clearMessage(int id) {
		int pos = 0;
		for (Msg msg : messagesList) {
			if(msg.getId() == id){
				messagesList.remove(pos);
				break;
			} else {
				pos++;
			}
		}		

	}

	public List<Msg> getMessages(int userProfile) {
		List<Msg> list = new ArrayList<Msg>();
		// return only messages according to profile
		if (messagesList.size() > 0) {
			for (Msg msg : messagesList) {
				if (msg.getMinProfile() <= userProfile) {
					list.add(msg);
				}
			}			
		}
		return list;
	}


	public int getImportantMessages(int userProfile) {
		int cpt = 0;
		// count only messages according to profile and not general
		if (messagesList.size() > 0) {
			for (Msg msg : messagesList) {
				if ((msg.getMinProfile() <= userProfile) && (msg.getType() > cMsgGeneral)){
					cpt++;
				}
			}			
		}
		return cpt;
	}


	public int checkMessages(Context context, MuseumDbAdapter dbAdapter, int userProfile, Footer footer) {
		String message;
		int nb = 0, nbCards = 0, wlStatus;
		clearMessages();
		try {
			// check DB model
			if (tools.getDbHasChangedModel(context)) {
				// major DB model evolution
				message = context.getString(R.string.msg_db_model) + "\n";
				if (footer.isOnline()) {
					message += context.getString(R.string.vous_devez_synchroniser);
				} else {
					message += context.getString(R.string.msg_pas_connecte);
					message += "\n";
					message += context.getString(R.string.vous_devez_connecter_et_synchroniser);
				}

				addMessage(message, Messages.cMsgDB, Constants.USR_CONTROLLER);
				nb++;
			}
		} catch (Exception ex) {
			Log.e(Constants.TAG, "HomeActivity.checkMessages() - synchro " + ex.getMessage());
		}
		try {
			// check synchronization
			Tablet tablet = dbAdapter.getTablet(1L);
			String time = tablet.getDownloadTime();
			Calendar now = Calendar.getInstance();
			Calendar download = tools.setCalendar(time);
			//now.add(Calendar.DAY_OF_MONTH, 1);
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			if (now.after(download)) {
				// no night synchronization today, set a synchronization message
				message = context.getString(R.string.msg_pas_de_synchro_nuit) + "\n";
				if (footer.isOnline()) {
					message += context.getString(R.string.vous_devez_synchroniser);
				} else {
					message += context.getString(R.string.msg_pas_connecte);
					message += "\n";
					message += context.getString(R.string.vous_devez_connecter_et_synchroniser);
				}

				addMessage(message, Messages.cMsgSynchro, Constants.USR_CONTROLLER);
				nb++;
			}
		} catch (Exception ex) {
			Log.e(Constants.TAG, "HomeActivity.checkMessages() - synchro " + ex.getMessage());
		}
		try {
			// check new software download
			boolean newVersion = tools.detectNewSoftwareVersion(context, dbAdapter);
			//    		newVersion = true;
			if (newVersion) {
				message = context.getString(R.string.nouvelle_version_dispo) + "\n";
				if (footer.isOnline()) {
					message += context.getString(R.string.vous_devez_telechrager);
				} else {
					message += context.getString(R.string.msg_pas_connecte);
					message += "\n";
					message += context.getString(R.string.vous_devez_connecter_et_telecharger);
				}
				addMessage(message, Messages.cMsgSWversion, Constants.USR_CONTROLLER);
				nb++;
			}
		} catch (Exception ex) {
			Log.e(Constants.TAG, "Messages.checkMessages() - download" + ex.getMessage());
		}
		
		if (userProfile != Constants.USR_CONTROLLER) {
	    	try {
	    		// check stock message
	    		Stock stock = dbAdapter.getStock(1L);
	    		if (stock != null) {
	    			if (stock.getAlert() > 0) {
						message = context.getString(R.string.you_have_actually_x_cards, stock.getNbCards()) + "\n";
	    				message += context.getString(R.string.to_order);
		    			addMessage(message, Messages.cMsgStock, Constants.USR_CASHIER);
		    			nb++;
	    			}
	    		}
	    	} catch (Exception ex) {
	    		Log.e(Constants.TAG, "Messages.checkMessages() - stock" + ex.getMessage());
	    	}
    	}

		try {
			// General messages
			Calendar startDate, endDate, now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 0);
			now.set(Calendar.MINUTE, 0);
			List<Msg> list = dbAdapter.getMessageList();
			if (list.size() > 0) {
				for (int i=0; i<list.size(); i++) {
					startDate = tools.setCalendar(list.get(i).getStartDate());
					endDate = tools.setCalendar(list.get(i).getEndDate());
					if (now.before(endDate) && now.after(startDate)) {
						message = list.get(i).getMsg();
						addMessage(message, Messages.cMsgGeneral, Constants.USR_CONTROLLER);
						nb++;
					}
				}
			}
		} catch (Exception ex) {
			Log.e(Constants.TAG, "Messages.checkMessages() - general message" + ex.getMessage());
		}
		return nb;
	}

}
