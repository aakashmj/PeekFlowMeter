package metsl.spiro;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by aakash on 8/1/15.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    public static final String DATABASE_NAME = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "eUNO_WiFi"
            + File.separator + "eUNO_wifi.db";

    private static final int DATABASE_VERSION = 1;

    private Dao<PatientDetailsCL, Integer> patientdetailsDao = null;
    private Dao<Record, Integer> RecordDao = null;
    private Dao<DoctorDetailsCL, Integer> DoctorDetailsCLDao = null;
    private Dao<Devices, Integer> DevicesDao = null;


    // we do this so there is only one helper
    private static DatabaseHelper helper = null;

    private static final AtomicInteger usageCounter = new AtomicInteger(0);

    private DatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    /**
     * Get the helper, possibly constructing it if necessary. For each call to
     * this method, there should be 1 and only 1 call to {@link #close()}.
     */
    public static synchronized DatabaseHelper getHelper(Context context) {

        if (helper == null) {
            helper = new DatabaseHelper(context);
        }

        usageCounter.incrementAndGet();
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

        try {
            TableUtils.createTable(connectionSource, PatientDetailsCL.class);
            TableUtils.createTable(connectionSource, Record.class);
            TableUtils.createTable(connectionSource, DoctorDetailsCL.class);
            TableUtils.createTable(connectionSource, Devices.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

        try {
            TableUtils
                    .dropTable(connectionSource, PatientDetailsCL.class, true);
            TableUtils
                    .dropTable(connectionSource, Record.class, true);
            TableUtils
                    .dropTable(connectionSource, DoctorDetailsCL.class, true);
            TableUtils
                    .dropTable(connectionSource, Devices.class, true);

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


    public Dao<PatientDetailsCL, Integer> getPatientDetailsDao()
            throws SQLException {
        if (patientdetailsDao == null) {
            patientdetailsDao = getDao(PatientDetailsCL.class);
        }
        return patientdetailsDao;
    }


    @Override
    public void close() {
        if (usageCounter.decrementAndGet() == 0) {
            super.close();

            if (patientdetailsDao != null){
                patientdetailsDao.clearObjectCache();
                patientdetailsDao = null;
            }
            if(DevicesDao != null){
                DevicesDao.clearObjectCache();
                DevicesDao = null;
            }
            helper = null;

        }
    }

    /**
     * Returns the Database Access Object (DAO) for our Record class. It will
     * create it or just give the cached value.
     */
    public Dao<Record, Integer> getRecordDao() throws SQLException {
        if (RecordDao == null) {
            RecordDao = getDao(Record.class);
        }
        return RecordDao;
    }

    public Dao<DoctorDetailsCL, Integer> getDoctorDetailsCLDao() throws SQLException {
        if (DoctorDetailsCLDao == null) {
            DoctorDetailsCLDao = getDao(DoctorDetailsCL.class);
        }
        return DoctorDetailsCLDao;
    }

    public Dao<Devices, Integer> getDevicesDao() throws SQLException {
        if (DevicesDao == null) {
            DevicesDao = getDao(Devices.class);
        }
        return DevicesDao;
    }



}



