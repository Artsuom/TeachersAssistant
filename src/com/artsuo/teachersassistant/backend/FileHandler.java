package com.artsuo.teachersassistant.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;

public class FileHandler {
	
	private FileInputStream fis;
	private FileOutputStream fos;
	
	public FileHandler() {
		this.fis = null;
		this.fos = null;
	}
	
	// Check if saved data exists for a certain Page
	public boolean dataExists(Context context, String filename, int pageNr) {
		try {
			fis = context.openFileInput(filename);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			
			String readString = br.readLine();
			while (readString != null) {
				if (readString.contains("Page" + Integer.toString(pageNr))) {
					return true;
				}
				readString = br.readLine();
			}
			br.close();
			isr.close();
			fis.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	public void writeLine(Context context, String filename, String string) {
		try {
			fos = context.openFileOutput(filename, Context.MODE_APPEND);
			fos.write(string.getBytes());
			fos.write(System.getProperty("line.separator").getBytes());
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String[] readLine(Context context, String filename, int pageNr){
		StringBuffer sBuff = new StringBuffer("");
		try {
			fis = context.openFileInput(filename);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			
			String readString = br.readLine();
			while (readString != null) {
				if (readString.contains("Page" + Integer.toString(pageNr))) {
					sBuff.append(readString);
				}
				readString = br.readLine();
			}
			br.close();
			isr.close();
			fis.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return sBuff.toString().split(Constants.FILE_FIELD_SEPARATOR);
	}
	
	public void deleteLine(Context context, String filename, int pageNr) {
		try {
			File dir = context.getFilesDir();
			File inFile = new File(dir, filename);
			fis = context.openFileInput(filename);
			fos = context.openFileOutput(Constants.TEMP_FILENAME, Context.MODE_APPEND);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			
			String readString = br.readLine();
			while (readString != null) {
				if (!readString.contains("Page" + Integer.toString(pageNr))) {				
					fos.write(readString.getBytes());
					fos.write(System.getProperty("line.separator").getBytes());
				}
				readString = br.readLine();
			}
			br.close();
			isr.close();
			fos.close();
			fis.close();
			
			inFile.delete();
			inFile = new File(dir, Constants.TEMP_FILENAME);
			inFile.renameTo(new File(dir, filename));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public String readWholeFile(Context context, String filename) {
		StringBuffer sBuff = new StringBuffer("");
		try {
			fis = context.openFileInput(filename);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			
			String readString = br.readLine();
			while (readString != null) {
				sBuff.append(readString);
				readString = br.readLine();
			}
			br.close();
			isr.close();
			fis.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return sBuff.toString();
	}
	
	// Replace one line in the data file with the given line. 
	// Reads from the original file and writes to a temp file, renaming files in the end
	public void replaceExistingPageData(Context context, String filename, int pageNr, String newPageData) {
		try {
			File dir = context.getFilesDir();
			File inFile = new File(dir, filename);
			fis = context.openFileInput(filename);
			fos = context.openFileOutput(Constants.TEMP_FILENAME, Context.MODE_APPEND);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			
			String readString = br.readLine();
			while (readString != null) {
				if (!readString.contains("Page" + Integer.toString(pageNr))) {
					fos.write(readString.getBytes());
					fos.write(System.getProperty("line.separator").getBytes());
				} else {
					fos.write(newPageData.getBytes());
					fos.write(System.getProperty("line.separator").getBytes());
				}
				readString = br.readLine();
			}
			br.close();
			isr.close();
			fos.close();
			fis.close();
			
			inFile.delete();
			inFile = new File(dir, Constants.TEMP_FILENAME);
			inFile.renameTo(new File(dir, filename));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public Bitmap decodeUri(Context context, Uri selectedImage) throws FileNotFoundException {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();      
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o);
        // The new size we want to scale to
        final int REQUIRED_SIZE = 140;	// 140
        
        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
               || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        
        return rotateBitmap(context, BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o2), selectedImage);
    }
	
	public Bitmap rotateBitmap(Context context, Bitmap source, Uri selectedImage)
	{
		String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
        Cursor cur = context.getContentResolver().query(selectedImage, orientationColumn, null, null, null);
        int orientation = -1;
        if (cur != null && cur.moveToFirst()) {
            orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
        }  
        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
	    return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}
	
	// Deletes one file from the internal storage
	public boolean deleteFile(Context context, String filename) {
		File dir = context.getFilesDir();
		File file = new File(dir, filename);
		return file.delete();
	}
	
}
