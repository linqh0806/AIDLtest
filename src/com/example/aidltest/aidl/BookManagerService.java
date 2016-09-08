package com.example.aidltest.aidl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager.OnNetworkActiveListener;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

public class BookManagerService extends Service {

	private static final String TAG = "BMS";

	private AtomicBoolean mServiceDestoryed = new AtomicBoolean(false);
	private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<Book>();
	private CopyOnWriteArrayList<IOnNewBookArrivedListener> mListenerList = new CopyOnWriteArrayList<IOnNewBookArrivedListener>();

	private Binder mBinder = new IBookManager.Stub() {

		@Override
		public List<Book> getBookList() throws RemoteException {
			// TODO Auto-generated method stub
			return mBookList;
		}

		@Override
		public void addBook(Book book) throws RemoteException {
			// TODO Auto-generated method stub
			mBookList.add(book);
		}

		@Override
		public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
			// TODO Auto-generated method stub
			if (!mListenerList.contains(listener)) {
				mListenerList.add(listener);
			} else {
				Log.e(TAG, "already exists.");
			}
			Log.e(TAG, "registerListener,Size:" + mListenerList.size());
		}

		@Override
		public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
			// TODO Auto-generated method stub
			if (mListenerList.contains(listener)) {
				mListenerList.remove(listener);
				Log.e(TAG, "unregisterListener successful");
			} else {
				Log.e(TAG, "unregisterListener not find");
			}
			Log.e(TAG, "registerListener,Size:" + mListenerList.size());
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mBookList.add(new Book(1,"Android"));
		mBookList.add(new Book(2,"IOS"));
		
		new Thread(new ServiceWorker()).start();
	}
	
	private void OnNewBookArrived(Book book) throws RemoteException{
		mBookList.add(book);
		for(int i=0;i<mListenerList.size();i++){
			IOnNewBookArrivedListener listener=mListenerList.get(i);
			Log.e(TAG, "OnNewBookArrived,notify listener:"+listener);
			listener.OnNewBookArrived(book);
		}
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mServiceDestoryed.set(true);
		super.onDestroy();
	}

	private class ServiceWorker implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (!mServiceDestoryed.get()) {
				try{
					Thread.sleep(5000);
				}catch (InterruptedException e){
					e.printStackTrace();
				}
				int bookId=mBookList.size()+1;
				Book book=new Book(bookId,"new book#"+bookId);
				try{
					OnNewBookArrived(book);
				}catch(RemoteException e){
					e.printStackTrace();
				}
			}
		}
	}

}
