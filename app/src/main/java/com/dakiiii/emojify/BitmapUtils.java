package com.dakiiii.emojify;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BitmapUtils {
    private static final String FILE_PROVIDER_AUTHORITY = "com.dakiiii.emojify.fileprovider";

    static Bitmap resamplePic(Context context, String imagePath) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int targetH = displayMetrics.heightPixels;
        int targetW = displayMetrics.widthPixels;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        int photoW = options.outWidth;
        int photoH = options.outHeight;

        int scaleFacotr = Math.min(photoW / targetW, photoH / targetH);

        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFacotr;
        return BitmapFactory.decodeFile(imagePath);
    }

    static File createTempImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalCacheDir();
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    static boolean deleteImageFile(Context context, String imagePath) {
        File imageFile = new File(imagePath);

        boolean deleted = imageFile.delete();

        if (!deleted) {
            String errorMessage = "Error finding image";
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
        }
        return deleted;
    }

    private static void galleryAddPic(Context context, String imagePath) {

    }

    static String saveImage(Context context, Bitmap image) {
        String savedImagePath = null;

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/Emojify");
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }

        if (success) {
            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fOutputStream = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);
                fOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            galleryAddPic(context, savedImagePath);

            String savedMessage = context.getString(R.string.saved_message, savedImagePath);
            Toast.makeText(context, savedMessage, Toast.LENGTH_SHORT).show();
        }

        return savedImagePath;
    }

    static void shareImage(Context context, String imagePath) {
        File imageFile = new File(imagePath);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        Uri photoUri = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, imageFile);
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoUri);
        context.startActivity(shareIntent);
    }
}
