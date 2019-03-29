package metsl.spiro;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.ArrayList;

/**
 * Created by manishak on 8/20/15.
 */
public class DoctorDetailsDbAdapter {


    private Context context;
    private DatabaseHelper dbHelper;
    public static final String KEY_SP_ID = "docid";
    private static final String KEY_PATIENT_ID = "_id";
    // get our dao
    private Dao<DoctorDetailsCL, Integer> doctorDetailsDao = null;

    public DoctorDetailsDbAdapter(Context context) {
        this.context = context;
    }

    public boolean open() {
        try {
            doctorDetailsDao = getHelper().getDoctorDetailsCLDao();

        } catch (java.sql.SQLException e) {

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

    // Create a new todo. If the todo is successfully created return the new
    // rowId for that note, otherwise return a -1 to indicate failure.

    public boolean InsertDoctorDetails(DoctorDetailsCL spdetails) {
        boolean retval = false;
        try {
            retval = doctorDetailsDao.create(spdetails) > 0;
        } catch (java.sql.SQLException e) {

            e.printStackTrace();

        }

        return retval;
    }

    // Update the todo
    public boolean UpdateDoctorDetails(DoctorDetailsCL spdetails) {
        int retval = -1;
        try {
            open();
            retval = doctorDetailsDao.update(spdetails);
        } catch (java.sql.SQLException e) {

            e.printStackTrace();
        }
        return (retval > 0);
    }

    private int Get_SP_Count() {

        int total = 0;
        try {
            total = (int) doctorDetailsDao.countOf(doctorDetailsDao.queryBuilder()
                    .setCountOf(true).where().eq("_id", 1).prepare());
        } catch (java.sql.SQLException e) {

            e.printStackTrace();
        }

        return total;

    }

    public boolean InsertUpdateSPDetails(DoctorDetailsCL spDetails) {
        int count = Get_SP_Count();
        boolean retval;
        if (count == 0) {
            retval = InsertDoctorDetails(spDetails);
        } else {
            spDetails.ID = 1;
            retval = UpdateDoctorDetails(spDetails);
        }
        return retval;
    }

    public DoctorDetailsCL getDoctorDetails() {
        DoctorDetailsCL spDetails = null;
        try {
            spDetails = doctorDetailsDao.queryForId(1);
        } catch (java.sql.SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return spDetails;
    }

    public DoctorDetailsCL GetDetailsbyDoctorDetailsID() {
        DoctorDetailsCL patientDetailsCL = null;
        try {
            if (doctorDetailsDao == null)
                open();
            QueryBuilder<DoctorDetailsCL, Integer> qb = doctorDetailsDao
                    .queryBuilder();
            qb.where().eq(KEY_PATIENT_ID, 1);

            // prepare our sql statement
            PreparedQuery<DoctorDetailsCL> preparedQuery;

            preparedQuery = qb.prepare();

            // query for all accounts that have "qwerty" as a password
            ArrayList<DoctorDetailsCL> PatientDetailsArrayList = (ArrayList<DoctorDetailsCL>) doctorDetailsDao
                    .query(preparedQuery);

            if (PatientDetailsArrayList.size() > 0)
                patientDetailsCL = PatientDetailsArrayList.get(0);



        } catch (java.sql.SQLException e) {

            e.printStackTrace();
        }

        return patientDetailsCL;

    }






}
