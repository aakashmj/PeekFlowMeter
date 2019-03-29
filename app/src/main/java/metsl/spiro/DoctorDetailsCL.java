package metsl.spiro;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by manishak on 8/20/15.
 */

@DatabaseTable(tableName = "DoctorDetails")
public class DoctorDetailsCL {

    @DatabaseField(generatedId = true, columnName = "_id")
    public int ID;

    @DatabaseField(columnName = "docid")
    public int DOCID;

    @DatabaseField(columnName = "name")
    public String NAME;

    @DatabaseField(columnName = "registrationno")
    public String REGNO;

    @DatabaseField(columnName = "phone1")
    public String PHONE1;

    @DatabaseField(columnName = "phone2")
    public String PHONE2;

    @DatabaseField(columnName = "phone3")
    public String PHONE3;

    @DatabaseField(columnName = "addresshome")
    public String ADDRESS_HOME;

    @DatabaseField(columnName = "addressoffice")
    public String ADDRESS_OFFICE;

    @DatabaseField(columnName = "email1")
    public String EMAIL1;

    @DatabaseField(columnName = "email2")
    public String EMAIL2;

    @DatabaseField(columnName = "email3")
    public String EMAIL3;

    @DatabaseField(columnName = "otherdetails")
    public String OTHERDETAILS;

    @DatabaseField(columnName = "username")
    public String USERNAME;

    @DatabaseField(columnName = "imageBytes", dataType = DataType.BYTE_ARRAY)
    public byte[] imageBytes;

    @DatabaseField(columnName = "modifieddate")
    public String ModifiedDate;

    public DoctorDetailsCL() {

    }
}
