package com.taberu.sensorsmeter;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * Created by Taberu on 11/11/2016.
 */

public class FileServices {

    public boolean appendFile(Context context, String sFileName, String sBody) {
        FileOutputStream outputStream;
        File myFile;

        myFile = new File(context.getFilesDir(), sFileName);

        try {
            outputStream = new FileOutputStream(myFile, true);
            outputStream.write(sBody.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean clearFiles(Context context) {
        File dirFiles = context.getFilesDir();

        String[] listFiles = dirFiles.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".csv");
            }
        });

        for (String strFile : listFiles) {
            // strFile is the file name
            context.deleteFile(strFile);
        }

        return true;
    }
}
