package com.artsuo.teachersassistant.backend;

import java.io.FileNotFoundException;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

public class DataAccessor {

	private static DataAccessor instance;
	private FileHandler io;
	
	private DataAccessor() {
		this.io = new FileHandler();
	}
	
	public static DataAccessor getInstance() {
		if (instance == null) {
			instance = new DataAccessor();
		}
		return instance;
	}
	
	// Check if saved data exists for a certain Page
	public boolean dataExists(Context context, String filename, int pageNr) {
		if (io.dataExists(context, filename + ".txt", pageNr)) {
			return true;
		} else {
			return false;
		}
	}
	
	public void savePage(Context context, String filename, String string) {
		io.writeLine(context, filename + ".txt", string);
	}
	
	public String[] readPage(Context context, String filename, int pageNr) {
		return io.readLine(context, filename + ".txt", pageNr);
	}
	
	public void deletePage(Context context, String filename, int pageNr) {
		io.deleteLine(context, filename + ".txt", pageNr);
	}
	
	public boolean deletePresentation(Context context, String filename) {
		return io.deleteFile(context, filename + ".txt");
	}
	
	public String readFileToString(Context context, String filename) {
		return io.readWholeFile(context, filename + ".txt");
	}
	
	public void replaceExistingPageData(Context context, String filename, int pageNr, String newPageData) {
		io.replaceExistingPageData(context, filename + ".txt", pageNr, newPageData);
	}
	
	public Bitmap decodeUri(Context context, Uri selectedImage) throws FileNotFoundException {
        return io.decodeUri(context, selectedImage);
    }
}
