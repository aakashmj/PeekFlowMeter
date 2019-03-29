package metsl.spiro;

/**
 * Created by aakash on 8/21/15.
 */
public class DataReader {


    private int ArrayRowSize;

    private int CHANNELS = 13;

     private int ECGResolution = 2;

     int NoOfLeads = 12;

    float ParameterWidth = 1.0f;
    private int ReferenceVoltage = 2500;
    private int NoOfBits = 16;
    private float ADCMultiplier = 2.44f;// = Convert.ToSingle((ReferenceVoltage / (2 ^ NoOfBits)));
    private int MachineOffset = 3000;//410;
    //SamplingRate = Convert.ToSingle(150 / 2);
    private int SamplingRate = 240;
    private int TransferRate = 256;//not used
    private float HardwareGain;

    private String[] LeadOrder;


    private int L1_CODE = 0;
    private int L2_CODE = 1;
    private int L3_CODE = 2;
    private int aVr_CODE = 3;
    private int aVl_CODE = 4;
    private int aVf_CODE = 5;
    private int V1_CODE = 6;
    private int V2_CODE = 7;
    private int V3_CODE = 8;
    private int V4_CODE = 9;
    private int V5_CODE = 10;
    private int V6_CODE = 11;
    public DataReader(Machine machine)
    {
        ArrayRowSize = 0;
        LeadOrder = new String[CHANNELS];
        LeadOrder[L1_CODE] = "L1";
        LeadOrder[L2_CODE] = "L2";
        LeadOrder[L3_CODE] = "L3";
        LeadOrder[aVf_CODE] = "aVf";
        LeadOrder[aVl_CODE] = "aVl";
        LeadOrder[aVr_CODE] = "aVr";
        LeadOrder[V1_CODE] = "V1";
        LeadOrder[V2_CODE] = "V2";
        LeadOrder[V3_CODE] = "V3";
        LeadOrder[V4_CODE] = "V4";
        LeadOrder[V5_CODE] = "V5";
        LeadOrder[V6_CODE] = "V6";


        ReferenceVoltage = machine.ReferenceVoltage;
        NoOfBits = machine.NoOfBits;
        ADCMultiplier = machine.ADCMultiplier;// = Convert.ToSingle((ReferenceVoltage / (2 ^ NoOfBits)));
        MachineOffset = machine.MachineOffset;//410;
        //SamplingRate = Convert.ToSingle(150 / 2);
        SamplingRate = (int)machine.SamplingRate;
        TransferRate = machine.TransferRate;//not used
        HardwareGain = machine.HardwareGain;
    }


    private int ArrayLimitFinder(byte[] ECGArray)
    {

        if (ECGArray == null)
        {
            return 0;
        }

        int counter = 0;
        int position = 0;
        while (position < ECGArray.length)
        {
            byte getByte = ECGArray[position];
            position++;
            if ((getByte &0xFF) == 0xF1)
                counter++;
        }

        return counter;

    }

    private int ReadECGData(byte[] ECGArray, int length, int[][] lead_array)
    {
        if (length == 0)
        {

            return 0;
        }



        int counter = 0;
        int no_of_bytes = 0, command;
        int[] lead_data = new int[ECGResolution];
        int lead_counter = 0;
        int row_counter;
        row_counter = 0;
        command = 0;
        int pos = 0;
        while (row_counter < length)
        {
            byte getByte = ECGArray[pos];
            pos++;
            if ((getByte&0xFF) > 0xF0)
            {
                command = (getByte&0xFF);
                no_of_bytes = 0;
                counter = 0;
            }
            else
            {
                if (command > 0)
                {
                    switch (command)
                    {
                        case 0xF1:
                        {
                            if (counter == 0)
                            {
                                no_of_bytes = (getByte&0xFF);
                                lead_counter = 0;
                                row_counter++;
                            }
                            else
                            {
                                lead_data[lead_counter] = (getByte&0xFF);
                                lead_counter++;
                                if (lead_counter >= ECGResolution)
                                {

                                    int data = (lead_data[0] * 128 + lead_data[1]) - MachineOffset;//qrsDetect.RealTimeLowPassFilter((lead_data[0] * 128 + lead_data[1]) - MachineOffset, Convert.ToInt32(counter / 2) - 1);
                                    data = (int)(data * ADCMultiplier / HardwareGain);
                                    lead_array[row_counter - 1][(counter / 2) - 1] = data;
                                    lead_counter = 0;
                                }
                            }
                            counter++;
                            if (counter > no_of_bytes)
                            {
                                command = 0;
                            }

                        }

                        break;
                        default:
                            command = 0;
                            break;
                    }
                }
            }
        }


        return 1;
    }
 public    int rows;
    public  int channels;

    public int[][] GetECGChannels(byte[] ECGArray )
    {
        this.ArrayRowSize = this.ArrayLimitFinder(ECGArray);//("D:\\Data\\ECG\\REM\\Bin\\bypass.bin");
        int[][]   ECG_Array = new int[this.ArrayRowSize][ CHANNELS];
        rows = this.ArrayRowSize;
        channels = CHANNELS;

        if (this.ArrayRowSize == 0)
            return null;

       // Console.WriteLine("Row Size: " + Convert.ToString(this.ArrayRowSize));

        int[][] Raw_Array = new int[this.ArrayRowSize][ 9];


        if (this.ReadECGData(ECGArray, this.ArrayRowSize,  Raw_Array) == 1)
            //Console.WriteLine("Done");
                System.out.println("Done");

        // Calculate Other Leads
        for (int col = 0; col < this.ArrayRowSize; col++)
        {
            int l2 = Raw_Array[col][ 0];
            int l3 = Raw_Array[col] [1];
            int v1 = Raw_Array[col][ 2];
            int v2 = Raw_Array[col][ 3];
            int v3 = Raw_Array[col][ 4];
            int v4 = Raw_Array[col][ 5];
            int v5 = Raw_Array[col][ 6];
            int v6 = Raw_Array[col][ 7];

            int l1 = l2 - l3;
            // Lead1 needs to be calculated
            ECG_Array[col][ L1_CODE] = l2 - l3;
            ECG_Array[col][ L2_CODE] = l2;
            ECG_Array[col][ L3_CODE] = l3;
            // AVf needs to be calculated
            ECG_Array[col][ aVf_CODE] = ((l2 + l3) / 2);
            // AVL needs to be calculated
            ECG_Array[col][ aVl_CODE] = ((l1 - l3) / 2);
            // AVr needs to be calculated
            ECG_Array[col][ aVr_CODE] = ((-l1 - l2) / 2);
            ECG_Array[col][V1_CODE] = v1;
            ECG_Array[col][V2_CODE] = v2;
            ECG_Array[col][V3_CODE] = v3;
            ECG_Array[col][ V4_CODE] = v4;
            ECG_Array[col][ V5_CODE] = v5;
            ECG_Array[col][ V6_CODE] = v6;
        }

        return ECG_Array;

    }

}
