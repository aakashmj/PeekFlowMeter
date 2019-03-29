package metsl.spiro;

/**
 * Created by manishak on 8/3/15.
 */

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "record")
public class Record {

    public static final String PATIENT_ID = "PatientID";


    @DatabaseField(generatedId = true, columnName = "_id")
    public int ID;

    @DatabaseField(columnName = "deviceid")
    public String DEVICEID;

    @DatabaseField(columnName = "devicetype")
    public int DEVICE_TYPE;

    @DatabaseField(columnName = "bpsys")
    public int BPSYS;
    @DatabaseField(columnName = "bpdia")
    public int BPDIA;


    @DatabaseField(columnName = "ecgfile")
    public String ECGFILE;


    @DatabaseField(columnName = "devicerecordeddt", dataType = DataType.DATE_STRING,
            format = "MM/dd/yyyy HH:mm:ss")
    public Date DEVICE_RECORDED_DATETIME;

    @DatabaseField(columnName = "location")
    public String LOCATION;

    @DatabaseField(columnName = "autointerpretation")
    public String AUTO_INTERPRETATION;
    @DatabaseField(columnName = "analysis")
    public String ANALYSIS;
    @DatabaseField(columnName = "sao2")
    public int SAO2;
    @DatabaseField(columnName = "respiration")
    public int RESPIRATION;
    @DatabaseField(columnName = "pulserate")
    public int PULSERATE;
    @DatabaseField(columnName = "temprature")   // treated as doctor name while fetching comments
    public String TEMPRATURE;
    @DatabaseField(columnName = "bpmean")
    public int BPMEAN;
    @DatabaseField(columnName = "spirovalue")
    public int SPIROVALUE;

    @DatabaseField(columnName = "uploaded")
    public int ISUPLOADED;

    @DatabaseField(columnName = "corrupted")
    public int ISCORRUPTED;

    @DatabaseField(columnName = "isviewed") // as treated gain value plz make it sure while using this value
    public int ISVIEWED;

    @DatabaseField(columnName = "comments")
    public String COMMENTS;
    @DatabaseField(columnName = "interpretation")
    public String INTERPRETATION;

    // Foreign Key Collector_ID
    @DatabaseField(foreign = true, columnName = PATIENT_ID, foreignAutoCreate = true, foreignAutoRefresh = true, columnDefinition = "integer references  PatientDetails(_id)")
    private PatientDetailsCL patientDetails;

    public Record() {

    }
    public Record(PatientDetailsCL patientDetails) {
        this.patientDetails = patientDetails;
    }

    public PatientDetailsCL getPatientDetails( ) {
        return this.patientDetails;
    }

    public void setPatientDetails(PatientDetailsCL patientDetails) {
        this.patientDetails = patientDetails;
    }

}

