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
 * Created by aakashj on 8/3/15.
 */
public class RecordDbAdapter{
    private Context context;
    private DatabaseHelper dbHelper;
    public static final String KEY_IS_UPLOADED = "uploaded";
    public static final String KEY_IS_CORRUPTED = "corrupted";
    public static final String KEY_EVENT_REC_ID = "_id";

    // get our dao
    Dao<Record, Integer> recordDao = null;
    Dao<PatientDetailsCL, Integer> ptDao = null;

    public RecordDbAdapter(Context context) {
        this.context = context;
    }

    public boolean open() {
        try {
            recordDao = getHelper().getRecordDao();
            ptDao = getHelper().getPatientDetailsDao();

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

    public void DeleteRecord(int rid) {
        DeleteBuilder<Record, Integer> deleteBuilder = recordDao
                .deleteBuilder();
        try {
            deleteBuilder.where().eq("PatientID", rid);
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Create a new todo. If the todo is successfully created return the new
    // rowId for that note, otherwise return a -1 to indicate failure.
    public boolean InsertRecord(Record record) {
        boolean retval = false;
        try {
            retval = recordDao.create(record) > 0;
        } catch (SQLException e) {

            e.printStackTrace();

        }

        return retval;
    }

    // Update the todo
    private boolean UpdateRecord(Record record) {
        int retval = -1;
        try {
            retval = recordDao.update(record);
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return (retval > 0);
    }

    public int Get_Total_Reocrds() {

        int total = 0;
        try {
            total = (int) recordDao.countOf();
        } catch (SQLException e) {

            e.printStackTrace();
        }

        return total;

    }

    public int Get_Viewed_Count() {

        int total = 0;
        try {
            total = (int) recordDao.countOf(recordDao.queryBuilder()
                    .setCountOf(true).where().eq("isviewed", 1).prepare());
        } catch (SQLException e) {

            e.printStackTrace();
        }

        return total;

    }

    public int Get_UnViewed_Count() {

        int total = 0;
        try {
            total = (int) recordDao.countOf(recordDao.queryBuilder()
                    .setCountOf(true).where().eq("isviewed", 0).prepare());
        } catch (SQLException e) {

            e.printStackTrace();
        }

        return total;

    }

    public int Get_CountbyRecId(int recID) {

        int total = 0;
        try {
            total = (int) recordDao.countOf(recordDao.queryBuilder()
                    .setCountOf(true).where().eq("_id", recID).prepare());
        } catch (SQLException e) {

            e.printStackTrace();
        }

        return total;

    }

    public boolean InsertUpdateRecordDetails(Record record) {
        int count = Get_CountbyRecId(record.ID);
        boolean retval;
        if (count == 0) {
            retval = InsertRecord(record);
        } else {
            // record.ID = 1;
            retval = UpdateRecord(record);
        }
        return retval;
    }

    public Record GetDetailsbyRecordID(int recid) {
        Record record = null;
        try {
            QueryBuilder<Record, Integer> qb = recordDao.queryBuilder();
            qb.where().eq("_id", recid);

            // prepare our sql statement
            PreparedQuery<Record> preparedQuery;

            preparedQuery = qb.prepare();

            // query for all accounts that have "qwerty" as a password
            ArrayList<Record> recordArrayList = (ArrayList<Record>) recordDao
                    .query(preparedQuery);
            if (recordArrayList.size() > 0) {
                record = recordArrayList.get(0);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }

        return record;

    }

    public ArrayList<Record> GetAllRecords() {
        ArrayList<Record> EventStorageArrayList = null;
        try {
            QueryBuilder<Record, Integer> qb = recordDao.queryBuilder();
            qb.orderBy("_id", false);
            qb.orderBy("devicerecordeddt", false);
            // prepare our sql statement
            PreparedQuery<Record> preparedQuery;
            preparedQuery = qb.prepare();
            // query for all accounts that have "qwerty" as a password
            EventStorageArrayList = (ArrayList<Record>) recordDao
                    .query(preparedQuery);
            // get our dao
            Dao<PatientDetailsCL, Integer> patientDao = dbHelper
                    .getPatientDetailsDao();
            for (int i = 0; i < EventStorageArrayList.size(); i++) {
                patientDao.refresh(EventStorageArrayList.get(i)
                        .getPatientDetails());
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
        return EventStorageArrayList;
    }
    public ArrayList<Record> GetAllRecordsByPatientID(int patient_id) {
        ArrayList<Record> EventStorageArrayList = new ArrayList<>();
        try {
            QueryBuilder<Record, Integer> qb = recordDao.queryBuilder();
            qb.where().eq("PatientID",patient_id);
            qb.orderBy("_id", false);
            qb.orderBy("devicerecordeddt", false);
            // prepare our sql statement
            PreparedQuery<Record> preparedQuery;
            preparedQuery = qb.prepare();
            // query for all accounts that have "qwerty" as a password
            EventStorageArrayList = (ArrayList<Record>) recordDao
                    .query(preparedQuery);
            // get our dao
            Dao<PatientDetailsCL, Integer> patientDao = dbHelper
                    .getPatientDetailsDao();
            for (int i = 0; i < EventStorageArrayList.size(); i++) {
                patientDao.refresh(EventStorageArrayList.get(i)
                        .getPatientDetails());
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
        return EventStorageArrayList;
    }
    public boolean UpdateViewed(int id) {
        try {
            UpdateBuilder<Record, Integer> updateBuilder = recordDao
                    .updateBuilder();
            // update the password to be "none"
            updateBuilder.updateColumnValue("isviewed", 1);
            // only update the rows where password is null
            updateBuilder.where().eq("_id", id);
            updateBuilder.update();

        } catch (Exception e) {
            return false;
        }
        return true;

    }


    public boolean Update_comments(int id,String comments) {
        try {
            UpdateBuilder<Record, Integer> updateBuilder = recordDao.updateBuilder();
            // update the password to be "none"
            updateBuilder.updateColumnValue("comments", comments);
            // only update the rows where password is null
            updateBuilder.where().eq("_id", id);
            updateBuilder.update();

        } catch (Exception e) {
            return false;
        }
        return true;

    }

    public void alterTable() {

        try {
            recordDao
                    .executeRaw("ALTER TABLE `record` ADD COLUMN devicename varchar;");

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public Record SetCommentbyRecordID(String entercomment, int recid) {
        Record record = null;
        try {
            QueryBuilder<Record, Integer> qb = recordDao.queryBuilder();
            qb.where().eq("_id", recid);

            // prepare our sql statement
            PreparedQuery<Record> preparedQuery;

            preparedQuery = qb.prepare();

            // query for all accounts that have "qwerty" as a password
            ArrayList<Record> recordArrayList = (ArrayList<Record>) recordDao
                    .query(preparedQuery);
            if (recordArrayList.size() > 0) {
                record = recordArrayList.get(0);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }

        return record;

    }
    public ArrayList<Record> getUnUploadedECG() {
        ArrayList<Record> ecgArrayList = null;
        try {
            if (recordDao == null)
                open();

            QueryBuilder<Record, Integer> recordQb = recordDao.queryBuilder();
            QueryBuilder<PatientDetailsCL, Integer> ptQb = ptDao.queryBuilder();
            // join with the order query
            recordQb.join(ptQb.selectColumns("PatientID"));
            // start the order statement query

            QueryBuilder<Record, Integer> qb = recordDao.queryBuilder();
            qb.where().eq(KEY_IS_UPLOADED, 0).and().eq(KEY_IS_CORRUPTED, 0);

            PreparedQuery<Record> preparedQuery;

            preparedQuery = qb.prepare();

            // query for all accounts that have "qwerty" as a password
            ecgArrayList = (ArrayList<Record>) recordDao.query(preparedQuery);

//
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ecgArrayList;
    }

    public boolean SetUploaded(int recid) {

        try {
            if (recordDao == null)
                open();
            UpdateBuilder<Record, Integer> updateBuilder = recordDao.updateBuilder();
            // update the password to be "none"
            updateBuilder.updateColumnValue(KEY_IS_UPLOADED, 1);
            // only update the rows where password is null
            updateBuilder.where().eq(KEY_EVENT_REC_ID, recid);
            updateBuilder.update();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }




    public Record SetInterpretationbyRecordID(String enterinterpretation,
                                              int recid) {
        Record record = null;
        try {
            QueryBuilder<Record, Integer> qb = recordDao.queryBuilder();
            qb.where().eq("_id", recid);

            // prepare our sql statement
            PreparedQuery<Record> preparedQuery;

            preparedQuery = qb.prepare();

            // query for all accounts that have "qwerty" as a password
            ArrayList<Record> recordArrayList = (ArrayList<Record>) recordDao
                    .query(preparedQuery);
            if (recordArrayList.size() > 0) {
                record = recordArrayList.get(0);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }

        return record;

    }
}
