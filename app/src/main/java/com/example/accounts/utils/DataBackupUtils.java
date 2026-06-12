package com.example.accounts.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.widget.Toast;

import com.example.accounts.db.DBOpenHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class DataBackupUtils {
    
    private static final String BACKUP_DIR = "PersonalAccountBackup";
    private static final String BACKUP_FILE = "account_backup.db";
    
    public static boolean backupData(Context context) {
        File currentDB = context.getDatabasePath(DBOpenHelper.DB_NAME);
        File backupDir = getBackupDir(context);
        if (backupDir == null) {
            return false;
        }
        
        File backupFile = new File(backupDir, BACKUP_FILE);
        
        FileChannel src = null;
        FileChannel dst = null;
        
        try {
            src = new FileInputStream(currentDB).getChannel();
            dst = new FileOutputStream(backupFile).getChannel();
            dst.transferFrom(src, 0, src.size());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (src != null) src.close();
                if (dst != null) dst.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static boolean restoreData(Context context) {
        File currentDB = context.getDatabasePath(DBOpenHelper.DB_NAME);
        File backupDir = getBackupDir(context);
        if (backupDir == null) {
            return false;
        }
        
        File backupFile = new File(backupDir, BACKUP_FILE);
        if (!backupFile.exists()) {
            return false;
        }
        
        FileChannel src = null;
        FileChannel dst = null;
        
        try {
            src = new FileInputStream(backupFile).getChannel();
            dst = new FileOutputStream(currentDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (src != null) src.close();
                if (dst != null) dst.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static boolean clearAllData(Context context) {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        
        try {
            // 清除记账表数据
            db.delete("accounttb", null, null);
            // 清除搜索历史表数据
            db.delete("searchhistorytb", null, null);
            // 清除SharedPreferences中的预算数据
            context.getSharedPreferences("budget", Context.MODE_PRIVATE).edit().clear().apply();
            context.getSharedPreferences("spfRecord", Context.MODE_PRIVATE).edit().clear().apply();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }
    
    public static boolean hasBackup(Context context) {
        File backupDir = getBackupDir(context);
        if (backupDir == null) {
            return false;
        }
        File backupFile = new File(backupDir, BACKUP_FILE);
        return backupFile.exists();
    }
    
    private static File getBackupDir(Context context) {
        File backupDir;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            backupDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), BACKUP_DIR);
        } else {
            backupDir = new File(context.getFilesDir(), BACKUP_DIR);
        }
        
        if (!backupDir.exists()) {
            if (!backupDir.mkdirs()) {
                return null;
            }
        }
        
        return backupDir;
    }
}
