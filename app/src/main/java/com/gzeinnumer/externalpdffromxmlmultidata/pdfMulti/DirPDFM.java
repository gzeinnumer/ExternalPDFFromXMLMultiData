package com.gzeinnumer.externalpdffromxmlmultidata.pdfMulti;

import android.os.Environment;
import android.util.Log;

import java.io.File;

public class DirPDFM {

    public static String getStorageCard = Environment.getExternalStorageDirectory().toString();
    public static String appFolder = "/ExternalPDFFromXMLMultiData";

    private static final String TAG = "FunctionGlobalDir_";

    public static void myLogD(String tag, String msg) {
        Log.d("MyZein", tag + "_" + msg);
    }

    public static boolean initFolder() {
        File folder;

        // create folder
        folder = new File(getStorageCard + appFolder);
        if (!folder.exists()) {
            return creatingFolder(folder);
        }
        return true;
    }

    private static boolean creatingFolder(File folder){
        try{
            if (folder.mkdirs()){
                myLogD(TAG, "Folder created");
            }
        } catch (Exception e){
            myLogD(TAG, "Folder not created");
            return false;
        }
        return true;
    }

    public static boolean isFileExists(String path){
        File file = new File(getStorageCard + path);
        return file.exists();
    }
}
