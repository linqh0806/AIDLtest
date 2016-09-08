package com.example.aidltest.aidl;

import com.example.aidltest.aidl.Book;

interface IOnNewBookArrivedListener{
	void OnNewBookArrived(in Book book);
}