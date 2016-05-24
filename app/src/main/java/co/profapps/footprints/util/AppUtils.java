package co.profapps.footprints.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.Chronometer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import co.profapps.footprints.R;

public final class AppUtils {
    private AppUtils() {}

    public static void showDialog(@NonNull Context context, String title,
                                  @NonNull String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.dismiss), null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static String getFormattedDate(@NonNull Date date) {
        return new SimpleDateFormat("EEEE MMMM d, yyyy", Locale.getDefault()).format(date);
    }

    public static void setAppBarColorOnRecentWindows(@NonNull Activity activity) {
        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher);

        ActivityManager.TaskDescription taskDescription = new ActivityManager
                .TaskDescription(activity.getTitle().toString(), bitmap,
                ContextCompat.getColor(activity, R.color.colorDarkGray));

        activity.setTaskDescription(taskDescription);
    }

    public static boolean appIsInstalled(@NonNull Context context, @NonNull String packageName) {
        PackageManager packageManager = context.getPackageManager();

        try {
            return packageManager.getPackageInfo(packageName,
                    PackageManager.GET_ACTIVITIES) != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }
}
