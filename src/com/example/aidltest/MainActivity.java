package com.example.aidltest;

import java.util.List;

import com.example.aidltest.aidl.Book;
import com.example.aidltest.aidl.BookManagerService;
import com.example.aidltest.aidl.IBookManager;
import com.example.aidltest.aidl.IOnNewBookArrivedListener;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	private static final String TAG = "BookManagerActivity";

	private IBookManager bookManager;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				Log.e(TAG, "reveive new book:" + msg.obj);
				break;

			default:
				break;
			}

		}
	};

	private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
		@Override
		public void binderDied() {
			if (bookManager == null)
				return;
			bookManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
			bookManager = null;
			Intent intent = new Intent(MainActivity.this, BookManagerService.class);
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		}
	};

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			bookManager = IBookManager.Stub.asInterface(service);
			try {
				service.linkToDeath(mDeathRecipient, 0);
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
			try {
				List<Book> list = bookManager.getBookList();
				Log.e(TAG, "query book list ,list type:" + list.getClass().getCanonicalName());
				Log.e(TAG, "query list:" + list.toString());
				bookManager.addBook(new Book(3, "Œ“µƒ≈Û”—Ω–≈Â≈Â"));
				List<Book> newlist = bookManager.getBookList();
				Log.e(TAG, "query list:" + newlist.toString());
				bookManager.registerListener(mOnNewBookArrivedListener);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};

	private IOnNewBookArrivedListener mOnNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {

		@Override
		public void OnNewBookArrived(Book book) throws RemoteException {
			// TODO Auto-generated method stub
			mHandler.obtainMessage(1, book).sendToTarget();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent intent = new Intent(this, BookManagerService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unbindService(mConnection);
		super.onDestroy();
	}
}
