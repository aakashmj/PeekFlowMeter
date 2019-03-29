package metsl.spiro;

/**
 * Created by aakash on 8/21/15.
 */
public class eUno3 extends Machine {

    public eUno3()
    {
        ReferenceVoltage = 2500;
        NoOfBits = 10;
        ADCMultiplier =
                (
                        (ReferenceVoltage / (2 ^ NoOfBits))
                );
        MachineOffset = 3000;//410;
        SamplingRate = (100);
        TransferRate = 256;//not used
        HardwareGain = (260 * 1.25f);

    }

}
