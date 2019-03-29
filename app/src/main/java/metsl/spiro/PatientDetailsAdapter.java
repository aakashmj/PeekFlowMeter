package metsl.spiro;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by manishak on 8/1/15.
 */
public class PatientDetailsAdapter {
    // get our dao
    Dao<PatientDetailsCL, Integer> patientdetailsDao = null;
    private Context context;
    private DatabaseHelper dbHelper;
    public static final String NAME_PATIENT = "PatientName";
    public static final String KEY_PATIENT_ID = "_id";
    public static final String KEY_CREATED_DATE = "CreatedDate";


    public PatientDetailsAdapter(Context context) {
        this.context = context;
    }

    public void open() {
        try {
            patientdetailsDao = getHelper().getPatientDetailsDao();

        } catch (SQLException e) {

            e.printStackTrace();
        }
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


    public ArrayList<PatientDetailsCL> GetAllPatientDetails() {
        ArrayList<PatientDetailsCL> PatientDetailsArrayList = null;
        try {
            if (patientdetailsDao == null)
                open();
            QueryBuilder<PatientDetailsCL, Integer> qb = patientdetailsDao
                    .queryBuilder();


            // prepare our sql statement
            PreparedQuery<PatientDetailsCL> preparedQuery;

            preparedQuery = qb.prepare();

            // query for all accounts that have "qwerty" as a password
            PatientDetailsArrayList = (ArrayList<PatientDetailsCL>) patientdetailsDao.query(preparedQuery);

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return PatientDetailsArrayList;

    }

    public ArrayList<PatientDetailsCL> GetAllPatientDetails_By_DateTime() {
        ArrayList<PatientDetailsCL> PatientDetailsArrayList = null;
        try {
            if (patientdetailsDao == null)
                open();
            QueryBuilder<PatientDetailsCL, Integer> qb = patientdetailsDao
                    .queryBuilder();

             qb.orderBy(KEY_CREATED_DATE,false);

            // prepare our sql statement
            PreparedQuery<PatientDetailsCL> preparedQuery;

            preparedQuery = qb.prepare();

            // query for all accounts that have "qwerty" as a password
            PatientDetailsArrayList = (ArrayList<PatientDetailsCL>) patientdetailsDao.query(preparedQuery);

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return PatientDetailsArrayList;

    }

    public boolean InsertPatientDetails(PatientDetailsCL patientDetailsCL) {
        boolean retval = false;
        try {
            if (patientdetailsDao == null)
                open();

            retval = patientdetailsDao.create(patientDetailsCL) > 0;
        } catch (SQLException e) {

            e.printStackTrace();

        }

        return retval;
    }

    public void DeletePatient(int rid) {
        DeleteBuilder<PatientDetailsCL, Integer> deleteBuilder = patientdetailsDao
                .deleteBuilder();
        try {
            deleteBuilder.where().eq("_id", rid);
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public ArrayList<PatientDetailsCL> GetDetailsbyPatientIDBySearchLike(
            String name) {
        ArrayList<PatientDetailsCL> PatientDetailsArrayList = null;
        try {
            QueryBuilder<PatientDetailsCL, Integer> qb = patientdetailsDao
                    .queryBuilder();
            qb.where().like(NAME_PATIENT, "%" + name + "%");
            qb.orderBy(KEY_CREATED_DATE,false);

            // prepare our sql statement
            PreparedQuery<PatientDetailsCL> preparedQuery;

            preparedQuery = qb.prepare();

            // query for all accounts that have "qwerty" as a password
            PatientDetailsArrayList = (ArrayList<PatientDetailsCL>) patientdetailsDao
                    .query(preparedQuery);

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return PatientDetailsArrayList;

    }


    public PatientDetailsCL GetDetailsbyPatientDetailsID(int patientID) {
        PatientDetailsCL patientDetailsCL = null;
        try {
            if (patientdetailsDao == null)
                open();
            QueryBuilder<PatientDetailsCL, Integer> qb = patientdetailsDao
                    .queryBuilder();
            qb.where().eq(KEY_PATIENT_ID, patientID);

            // prepare our sql statement
            PreparedQuery<PatientDetailsCL> preparedQuery;

            preparedQuery = qb.prepare();

            // query for all accounts that have "qwerty" as a password
            ArrayList<PatientDetailsCL> PatientDetailsArrayList = (ArrayList<PatientDetailsCL>) patientdetailsDao
                    .query(preparedQuery);

            if (PatientDetailsArrayList.size() > 0)
                patientDetailsCL = PatientDetailsArrayList.get(0);



        } catch (SQLException e) {

            e.printStackTrace();
        }

        return patientDetailsCL;

    }

    public boolean UpdateImage(byte[] value, String patientdetailsid,
                               int isuploaded) {
        try {
            if (patientdetailsDao == null)
                open();

            UpdateBuilder<PatientDetailsCL, Integer> updateBuilder = patientdetailsDao
                    .updateBuilder();
            // update the password to be "none"
            updateBuilder.updateColumnValue("imageBytes", value);
            updateBuilder.updateColumnValue("modifieddate",
                    Utility.currentUTCDateTime());
            // only update the rows where password is null
            updateBuilder.where().eq(KEY_PATIENT_ID, patientdetailsid);
            updateBuilder.update();
        } catch (Exception e) {

            return false;

        }
        return true;

    }

    // Update the PatientDetails
    public boolean UpdatePatientDetails_Details(PatientDetailsCL patientDetailsCL) {
        int retval = -1;
        try {
            retval = patientdetailsDao.update(patientDetailsCL);
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return (retval > 0);
    }


}
