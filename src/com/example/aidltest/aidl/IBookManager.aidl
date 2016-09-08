package com.example.aidltest.aidl;

import com.example.aidltest.aidl.Book;
import com.example.aidltest.aidl.IOnNewBookArrivedListener;

interface IBookManager{
	List<Book> getBookList();
	void addBook (in Book book);
	void registerListener(IOnNewBookArrivedListener listener);
	void unregisterListener(IOnNewBookArrivedListener listener);
}