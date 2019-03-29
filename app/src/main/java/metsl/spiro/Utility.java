package metsl.spiro;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Utility {

    public static void showmessage(Context context, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public static String timeZone()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
        String   timeZone = new SimpleDateFormat("Z").format(calendar.getTime());
        return timeZone.substring(0, 3) + ":"+ timeZone.substring(3, 5);
    }
    public static String GetDisplayDate(Date date) {
        // Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy",
                Locale.getDefault());

        return df.format(date);
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception ignored) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        return dir != null && dir.delete();
    }

    public static String GetFormatedDateTime(String date) {
        String formattedDate = "";
        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy  HH:mm:ss");
            c.setTime(df.parse(date));
            df = new SimpleDateFormat("MMM dd,yyyy  HH:mm:ss");
            formattedDate = df.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return formattedDate;
    }
    public static String GetFormatedDateTime(Date date) {
        String formattedDate = "";
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            SimpleDateFormat	df = new SimpleDateFormat("MMM dd,yyyy  HH:mm:ss");
            formattedDate = df.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return formattedDate;
    }
    public static String ConvertDisplaydatetodatabasedate(String date) {
        String formattedDate = "";
        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("MMM dd,yyyy");
            c.setTime(df.parse(date));
            df = new SimpleDateFormat("MM/dd/yyyy");
            formattedDate = df.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return formattedDate;
    }

    public static <T> void startnewActivity(Context context, Class<T> classname) {

        try {
            Intent myIntent = new Intent(context, classname);
            context.startActivity(myIntent);
            ((Activity) context).finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean CompareDate(String ownmodifieddate,
                                      String servermodifieddate) {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss",
                Locale.getDefault());
        if (ownmodifieddate.trim().isEmpty()) {
            return true;
        }

        if (ownmodifieddate.trim().equals("null")) {
            return true;
        }
        try {
            Date ownmodi = df.parse(ownmodifieddate);
            Date servermodi = df.parse(servermodifieddate);
            if (servermodi.after(ownmodi))
                return true;

        } catch (ParseException e) {

            e.printStackTrace();
        }

        return false;

    }

    public static String GetLocaleDatetime(String datetime) {
        Date date = new Date();

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss",
                Locale.getDefault());

        try {
            date = dateFormat.parse(datetime);
            TimeZone tz = TimeZone.getDefault();
            int gmtOffset = tz.getRawOffset()
                    + (tz.inDaylightTime(date) ? tz.getDSTSavings() : 0);

            date.setTime(date.getTime() + gmtOffset);
        } catch (ParseException ignored) {

        }

        dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm a",
                Locale.getDefault());

        return dateFormat.format(date);
    }

    public static Date GetLocaleDate(String datetime) {
        Date date = new Date();

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss",
                Locale.getDefault());

        try {
            date = dateFormat.parse(datetime);
			/*TimeZone tz = TimeZone.getDefault();
			int gmtOffset = tz.getRawOffset()
					+ (tz.inDaylightTime(date) ? tz.getDSTSavings() : 0);*/

            //date.setTime(date.getTime() + gmtOffset);
        } catch (ParseException ignored) {

        }

        return date;
    }

    public static String GetSchedularDate(Date datetime) {

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy",
                Locale.getDefault());

        return dateFormat.format(datetime);
    }

    public static String GetLocalTime(String time) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("HH:mm",
                    Locale.getDefault());

            Date date = df.parse(time);
            TimeZone tz = TimeZone.getDefault();
            int gmtOffset = tz.getRawOffset()
                    + (tz.inDaylightTime(date) ? tz.getDSTSavings() : 0);
            date.setTime(date.getTime() + gmtOffset);
            df = new SimpleDateFormat("HH:mm a", Locale.getDefault());
            return df.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    public static String currentUTCDateTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss",
                Locale.UK);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        return df.format(calendar.getTime());
    }

    // Calculate EDD
    public static String calculateEDD(Date LMP) {

        // DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        // DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",
        // Locale.getDefault());

        Calendar cal = Calendar.getInstance();
        cal.setTime(LMP);
        // Add 1 year to date
        cal.add(Calendar.YEAR, 1);

        // Subtract 3 months to date
        cal.add(Calendar.MONTH, -3);

        // Add 7 days to date
        cal.add(Calendar.DATE, 7);

        DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy",
                Locale.getDefault());
        return dateFormat.format(cal.getTime());
    }

    public static int GetMaximumAppointments(String starttime, String endtime,
                                             int timeslot) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm",
                Locale.getDefault());
        int maxappointment = 0;
        try {
            Date date1 = simpleDateFormat.parse(starttime);
            Date date2 = simpleDateFormat.parse(endtime);
            long difference = date2.getTime() - date1.getTime();
            maxappointment = (int) (TimeUnit.MILLISECONDS.toMinutes(difference) / timeslot);
        } catch (ParseException e) {

            e.printStackTrace();
        }
        return maxappointment;
    }

    public static String converttoUTCTime(String time) {

        String formattedDate = "";
        try {

            SimpleDateFormat df = new SimpleDateFormat("HH:mm",
                    Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(df.parse(time));
            calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            formattedDate = df.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formattedDate;
    }



    public static Intent createExplicitFromImplicitIntent(Context context,
                                                          Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent,
                0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }


}
