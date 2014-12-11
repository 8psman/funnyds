package com.eightpsman.funnyds.android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import com.eightpsman.funnyds.core.Constants;
import com.eightpsman.funnyds.core.Device;

import java.io.*;

/**
 * FunnyDS
 * Created by 8psman on 11/25/2014.
 * Email: 8psman@gmail.com
 */
public class AndroidUtils {

    public static byte[] convertBitmapToByte(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap createBitmapFromByteArray(byte[] data){
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public static Bitmap loadBitmapFromAssets(Context context, String name){
        try {
            InputStream is = context.getAssets().open(name);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            int height = Constants.ACTOR_MAX_HEIGHT;
            int width = (int)(height * (float)bitmap.getWidth() / (float)bitmap.getHeight());
            return getScaledBitmap(bitmap, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getScaledBitmap(Bitmap bitmap, int width, int height){
        return bitmap.createScaledBitmap(bitmap, width, height, false);
    }
    public static Bitmap loadBitmapFromStorage(String path){
        try {
            FileInputStream is = new FileInputStream(path);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            int height = Constants.ACTOR_MAX_HEIGHT;
            int width = (int)(height * (float)bitmap.getWidth() / (float)bitmap.getHeight());
            return getScaledBitmap(bitmap, width, height);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Device getLocalDevice(Activity context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int dpi = (int)(metrics.density * 160f);
        int width  = metrics.widthPixels;
        int height = metrics.heightPixels;

        Device device = new Device(dpi, 0f, 0f, (float)width, (float)height);
        device.os   = "Android";
        device.name = "unknown";
        device.ip   = "unknown";

        return device;
    }
}
