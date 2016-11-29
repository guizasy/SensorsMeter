package com.taberu.sensorsmeter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import static android.support.v4.content.FileProvider.getUriForFile;


/**
 * Created by Taberu on 11/11/2016.
 */

// https://medium.com/@benexus/dealing-with-permissions-when-sharing-files-android-m-cee9ecc287bf#.erm1fy377
// https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en

public class FileServices {
    final static String sFileName = "driver_data.csv";


    boolean appendFile(String sBody) {
        File backupDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), sFileName);
        FileOutputStream fos;

        try {
            fos = new FileOutputStream(backupDB, true);
            fos.write(sBody.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    boolean clearFiles() {
        File dirFiles = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File fileRemove;

        String[] listFiles = dirFiles.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".csv");
            }
        });

        if (listFiles.length != 0) {
            for (String strFile : listFiles) {
                fileRemove = new File(dirFiles, strFile);
                fileRemove.delete();
            }
        }

        return true;
    }

    void shareFiles(Context context) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        ArrayList<Uri> uris = new ArrayList<>();

        File shareFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), sFileName);
        Uri contentUri = getUriForFile(context, "com.taberu.sensorsmeter.fileprovider", shareFile);

        uris.add(contentUri);

        shareIntent.setType("text/plain");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "CSV driver data");
        shareIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[]{"email-address you want to send the file to"});

        try {
            context.startActivity(Intent.createChooser(shareIntent, "Email:")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context,
                    "Sorry no email Application was found",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
