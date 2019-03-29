package metsl.spiro;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by manisha on 5/12/2016.
 */
@DatabaseTable(tableName = "devices")
public class Devices {


    @DatabaseField(generatedId = true, columnName = "_id")
    public int ID;

    @DatabaseField(columnName = "deviceid" , unique = true)
    public String DEVICEID;


    @DatabaseField(columnName = "hospitalname" )
    public String HospitalName;

    @DatabaseField(columnName = "spid" )
    public String SPID;


    @DatabaseField(columnName = "status" )
    public String Status;



    @DatabaseField(columnName = "errordesp" )
    public String ErrorDesp;



    @DatabaseField(columnName = "techname" )
    public String TechName;


    @DatabaseField(columnName = "techmobile" )
    public String TechMobile;



    @DatabaseField(columnName = "techemail" )
    public String TechEmail;





    public Devices()
    {

    }




}
