package com.otipass.adt67;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import models.Constants;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.otipass.sql.Msg;
import com.otipass.sql.MuseumDbAdapter;
import com.otipass.sql.Param;
import com.otipass.swdownload.SwDownload;
import com.otipass.synchronization.SynchronizationService;
import com.otipass.tools.Messages;
import com.otipass.tools.StoppableRunnable;
import com.otipass.tools.tools;
import com.otipass.adt67.R;

@SuppressLint("NewApi")
public class BaseFragment extends Fragment{

	private View rootView;
	private TextView tvMessages, tvIndex;
	private RelativeLayout rl;
	private int indexMsg;
	private Button btn_prev, btn_next;
	private Messages messages;
	private List<Msg> displayedMessages;
	private Msg m;
	private ProgressDialog progressDialog;
	private MuseumDbAdapter dbAdapter;
	private Footer footer;
	private SwDownload swDownload;
	private static final int cDownload = 1;
	private static final int cSynchro = 2;
	private static final int speed = 10000;
	private StoppableRunnable mTask,mTaskRefresh;
	private Handler mHandler = new Handler();
	private int userProfile;
	private Fragment fragment = null;
	private Bundle bundle;
	private FragmentManager fragmentManager;

	public BaseFragment() {
		// Empty constructor required for fragment subclasses
	}
	public BaseFragment(Footer footer) {
		this.footer = footer;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_base, container, false);
		tvMessages = (TextView) rootView.findViewById(R.id.containerMessages);
		tvIndex = (TextView) rootView.findViewById(R.id.index);
		btn_prev = (Button) rootView.findViewById(R.id.btn_previous);
		btn_next = (Button) rootView.findViewById(R.id.btn_next);
		fragmentManager = getFragmentManager();
		btn_prev.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				onStopMessages();
				animateMessages(1);
			}
		});		

		btn_next.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				onStopMessages();
				animateMessages(2);
			}
		});		


		dbAdapter = new MuseumDbAdapter(getActivity());
		dbAdapter.open();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		userProfile = prefs.getInt(Constants.CATEGORY_KEY, 0);
		
		messages = Messages.getInstance();
		refreshView();
		getMessages(rootView);
		displayMessages();

		return rootView;
	}

	
	public void onResume()
	{
		super.onResume();
		refreshView();
	}
	
	private void displayMessages() {
		int nb;
		try {
			Button btnFunction = (Button) rootView.findViewById(R.id.btn_function);
			RelativeLayout rl = (RelativeLayout) rootView.findViewById(R.id.function_bar);
			RelativeLayout rl2 = (RelativeLayout) rootView.findViewById(R.id.layout_messages);
			LinearLayout ln1 = (LinearLayout) rootView.findViewById(R.id.layout_carte);
			if ((nb = displayedMessages.size())  > 0) {
				ln1.setVisibility(RelativeLayout.GONE);
				rl.setVisibility(RelativeLayout.VISIBLE);
				rl2.setVisibility(RelativeLayout.VISIBLE);
				tvMessages.setVisibility(TextView.VISIBLE);
				m  = displayedMessages.get(indexMsg);
				tvMessages.setText(m.getMsg());
				switch (m.getType()) {
				case Messages.cMsgDB:
					btnFunction.setText(getString(R.string.menu_synchro));
					btnFunction.setVisibility(Button.VISIBLE);
					btnFunction.setOnClickListener(new Button.OnClickListener(){
						@Override
						public void onClick(View v) {
							onStopMessages();
							footer.synchronise(SynchronizationService.cGetTotalWL);
							Synchro synchro = new Synchro(cSynchro);
							synchro.execute();
							refreshView();
						}
					});		
					break;

				case Messages.cMsgStock:
					btnFunction.setText(getString(R.string.order));
					btnFunction.setVisibility(Button.VISIBLE);
					btnFunction.setOnClickListener(new Button.OnClickListener(){
						@Override
						public void onClick(View v) {
							onStopMessages();
							fragment = new AccessWebFragment();
							bundle = new Bundle();
							bundle.putString(Constants.ACTION_KEY, Constants.WEB_CMD_ACTION);
							fragment.setArguments(bundle);
							if (fragment != null) {
								fragmentManager.beginTransaction()
								.replace(R.id.frame_container, fragment).commit();
							}
						}
					});		
					break;

				case Messages.cMsgSWversion:
					btnFunction.setText(getString(R.string.download));
					btnFunction.setVisibility(Button.VISIBLE);
					btnFunction.setOnClickListener(new Button.OnClickListener(){
						@Override
						public void onClick(View v) {
							onStopMessages();
							Param param = dbAdapter.getParam(1L);
							String newVersionName = param.getSoftwareVersion();
							swDownload = new SwDownload(getActivity());
							progressDialog = new ProgressDialog(getActivity());
							progressDialog.setMessage(getString(R.string.Communication_en_cours));
							progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
							progressDialog.setProgress(0);
							progressDialog.show();
							swDownload.downloadSW(newVersionName);
							Synchro synchro = new Synchro(cDownload);
							synchro.execute();
						}
					});		
					break;
				case Messages.cMsgSynchro:
					btnFunction.setText(getString(R.string.menu_synchro));
					btnFunction.setVisibility(Button.VISIBLE);
					btnFunction.setOnClickListener(new Button.OnClickListener(){
						@Override
						public void onClick(View v) {
							onStopMessages();
							footer.synchronise(SynchronizationService.cGetPartialWL);
							Synchro synchro = new Synchro(cSynchro);
							synchro.execute();
							refreshView();
						}
					});		
					break;
				case Messages.cMsgGeneral:
					btnFunction.setText("");
					btnFunction.setVisibility(Button.VISIBLE);
					btnFunction.setOnClickListener(new Button.OnClickListener(){
						@Override
						public void onClick(View v) {
						}
					});		
					break;
				default:
					btnFunction.setVisibility(Button.GONE);
					break;
				}
				tvIndex.setText(String.valueOf(indexMsg + 1) + " / " + String.valueOf(nb));
				if (indexMsg == 0) {
					btn_prev.setVisibility(Button.GONE);
				} else {
					btn_prev.setVisibility(Button.VISIBLE);
				}
				if (indexMsg == (nb - 1)) {
					btn_next.setVisibility(Button.GONE);
				} else {
					btn_next.setVisibility(Button.VISIBLE);
				}
			} else {
				rl2.setVisibility(RelativeLayout.GONE);
				rl.setVisibility(RelativeLayout.GONE);
				ln1.setVisibility(RelativeLayout.VISIBLE);
				btnFunction.setVisibility(Button.GONE);
				tvMessages.setVisibility(TextView.GONE);
			}
		} catch (Exception ex) {
			onStopMessages();
		}

	}

	private int checkMessages() {
		Messages messages = Messages.getInstance();
		return messages.checkMessages(getActivity(), dbAdapter, userProfile, footer);
	}

	public void onStopMessages() {
		if (mTask != null) {
			mTask.stop();   
			if (mHandler != null) {
				mHandler.removeCallbacks(mTask);
			}
		}
	}


	public void onStartMessages(long delayMillis) {
		mHandler.postDelayed(mTask, delayMillis);
	}

	public void onStartRefresh(long delayMillis) {
		mHandler.postDelayed(mTaskRefresh, delayMillis);
	}

	private void refreshView() {
		checkMessages();
		rootView.invalidate();
		// since the view is invalidated, need to start the messages animation from here ...
		int nb = getMessages(rootView);
		displayMessages();
		if (nb > 1) {
			mTask = new StoppableRunnable() {
				public void stoppableRun() {     
					animateMessages(3);
					onStartMessages(speed);                
				}
			}; 
			onStartMessages(speed);  
		} 

	}

	private class Synchro extends AsyncTask<Void, Integer, Void>
	{
		private int type;

		public Synchro(int type) {
			this.type = type;
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Integer... values){
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			int status;
			int lastProgress, progress, delta;
			switch(type) {
			case cDownload:
				status = SwDownload.cPending;
				progress = lastProgress = delta = 0;
				do {
					status = swDownload.getDownloadStatus();
					progress = swDownload.getProgress();
					if (progress > lastProgress) {
						delta = progress - lastProgress;
						lastProgress = progress;
					}
					if (delta > 0) {
						progressDialog.incrementProgressBy(delta);
					}
				} while (status == SwDownload.cPending);
				break;
			case cSynchro:
				status = SynchronizationService.cComPending;
				do {
					status = footer.getCommunicationSequenceStatus();
				} while (status == SynchronizationService.cComPending);
				break;
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			switch(type) {
			case cDownload:
				progressDialog.dismiss();
				if (swDownload.getDownloadStatus() == SwDownload.cOK) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					Uri uri = Uri.fromFile(new File(swDownload.getApkPath()));
					intent.setDataAndType(uri, "application/vnd.android.package-archive");
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				} else {
					tools.showAlert(getActivity(), getString(R.string.download_error), tools.cError);
				}
				break;
			case cSynchro:
				if (footer.getCommunicationSequenceStatus() == SynchronizationService.cComOK) {
					messages.clearMessage(m.getId());
					//onStartRefresh(1000);
					refreshView();
				} 
				break;
			}
		}
	}

	private void animateMessages(int type) {
		int nb;
		try {
			if ((nb = displayedMessages.size())  > 0) {
				switch (type) {
				case 1:
					if (indexMsg > 0) {
						indexMsg--;
					}
					displayMessages();
					onStartMessages(speed); 
					break;
				case 2:
					if (indexMsg < (nb - 1)) {
						indexMsg++;
					}
					displayMessages();
					onStartMessages(speed); 
					break;
				default:
					if (indexMsg == (nb - 1)) {
						indexMsg = 0;
					} else {
						indexMsg++;
					}
					displayMessages();
					break;
				}
			} else {
				displayMessages();
				onStopMessages();
			}
		} catch (Exception ex) {
			onStopMessages();
		}
	}

	private int getMessages(View view) {
		int nbMsgs = 0;
		try {
			List<Msg> msgs = messages.getMessages(userProfile);
			List<Msg> msgsDwnld = new ArrayList<Msg>(); 
			List<Msg> msgsStock = new ArrayList<Msg>();
			List<Msg> msgsGeneral = new ArrayList<Msg>(); 
			List<Msg> msgsSynchro = new ArrayList<Msg>(); 
			TextView tvTitle;
			tvTitle = (TextView ) view.findViewById(R.id.titleMessages);
			indexMsg = 0;
			displayedMessages = new ArrayList<Msg>();

			if (msgs.size() > 0) {
				for (Msg msg : msgs) {

					if (msg.getType() == Messages.cMsgStock) {
	            		msgsStock.add(msg);
	            	} else if (msg.getType() == Messages.cMsgSWversion) {
	            		msgsDwnld.add(msg);
	            	} else if (msg.getType() == Messages.cMsgSynchro) {
	            		msgsSynchro.add(msg);
	            	} else {
	            		msgsGeneral.add(msg);
	            	}
				}
				// start with synchro messages
				for (Msg msg : msgsSynchro) {
					if (userProfile >= msg.getMinProfile()) {
						nbMsgs++;
						displayedMessages.add(msg);
					}
				}
				// then download messages
				for (Msg msg : msgsDwnld) {
					if (userProfile >= msg.getMinProfile()) {
						nbMsgs++;
						displayedMessages.add(msg);
					}
				}
	        	// then stock messages 
	            for (Msg msg : msgsStock) {
	            	if (userProfile >= msg.getMinProfile()) {
	                	nbMsgs++;
	                	displayedMessages.add(msg);
	            	}
	            }
				// finally general messages
				for (Msg msg : msgsGeneral) {
					if (userProfile >= msg.getMinProfile()) {
						nbMsgs++;
						displayedMessages.add(msg);
					}
				}
			}

			switch (displayedMessages.size()) {
			case 0:
				tvTitle.setText(getActivity().getString(R.string.no_messages));
				break;
			case 1:
				tvTitle.setText(getActivity().getString(R.string.new_message));
				break;
			default:
				tvTitle.setText(getActivity().getString(R.string.new_messages, nbMsgs));
				break;
			}
		} catch (Exception ex) {
			onStopMessages();
		}
		return nbMsgs;
	}
}
