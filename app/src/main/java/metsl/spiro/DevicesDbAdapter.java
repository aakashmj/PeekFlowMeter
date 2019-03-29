package metsl.spiro;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by manisha on 5/12/2016.
 */
public class DevicesDbAdapter {

    private DatabaseHelper dbHelper;
    private Dao<Devices, Integer>  DevicesDao = null;
    Dao<String, Integer>  DevicesDao_list = null;
    private Context context;
    private static final String KEY_DEVICES_ID = "_id";

    private static final String KEY_DEVICES = "deviceid";
    private Devices devices_CL;


    public DevicesDbAdapter(Context context) {
        this.context = context;
    }


    public boolean open() {
        try {
            DevicesDao = getHelper().getDevicesDao();

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * You'll need this in your class to get the helper from the manager once
     * per class.
     */
    private DatabaseHelper getHelper() {
        if (dbHelper == null) {
            dbHelper = DatabaseHelper.getHelper(this.context);
        }
        return dbHelper;
    }

    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
            dbHelper = null;
        }
    }



    public boolean InsertDevices(Devices device) {
        boolean retval = false;
        try {
            retval = DevicesDao.create(device) > 0;
        } catch (SQLException e) {

            e.printStackTrace();

        }

        return retval;
    }

    // Update the todo
    private boolean UpdateDevices(Devices devices) {
        int retval = -1;
        try {
            open();
            retval = DevicesDao.update(devices);
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return (retval > 0);
    }

    public Devices GetDeviceBYID() {

        try {
            if(DevicesDao == null)
                open();
            QueryBuilder<Devices, Integer> qb = DevicesDao.queryBuilder();
             qb.where().eq(KEY_DEVICES_ID , "1");

            // prepare our sql statement
            PreparedQuery<Devices> preparedQuery;

            preparedQuery = qb.prepare();

            // query for all accounts that have "qwerty" as a password
            ArrayList<Devices> DevicesArrayList = (ArrayList<Devices>) DevicesDao.query(preparedQuery);

            if (DevicesArrayList.size() > 0)
                devices_CL = DevicesArrayList.get(0);



        } catch (SQLException e) {

            e.printStackTrace();
        }


        return devices_CL;

    }
    public ArrayList<Devices> GetAllDevices() {
        ArrayList<Devices> DeviceArrayList = null;
        try {
            QueryBuilder<Devices, Integer> qb = DevicesDao.queryBuilder();
            qb.orderBy("_id", false);
            // prepare our sql statement
            PreparedQuery<Devices> preparedQuery;
            preparedQuery = qb.prepare();
            DeviceArrayList = (ArrayList<Devices>) DevicesDao.query(preparedQuery);
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return DeviceArrayList;
    }


    public Devices GetSpBYDeviceID(String ID) {

        Devices devices = null;
        try {
            if(DevicesDao == null)
                open();
            QueryBuilder<Devices, Integer> qb = DevicesDao.queryBuilder();
            qb.where().eq(KEY_DEVICES, ID);

            // prepare our sql statement
            PreparedQuery<Devices> preparedQuery;

            preparedQuery = qb.prepare();

            // query for all accounts that have "qwerty" as a password
            ArrayList<Devices> DevicesArrayList = (ArrayList<Devices>) DevicesDao.query(preparedQuery);

            if (DevicesArrayList.size() > 0)
                devices_CL = DevicesArrayList.get(0);



        } catch (SQLException e) {

            e.printStackTrace();
        }


        return devices_CL;

    }


    public boolean InsertUpdateDevices(Devices devices) {
        boolean retval = false;
        if(devices != null)
         {
             retval = UpdateDevices(devices);
         }
        return retval;
    }


}
