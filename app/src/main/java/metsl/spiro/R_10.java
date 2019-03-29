package metsl.spiro;

/**
 * Created by manishak on 8/21/15.
 */
public class R_10 extends Machine  {

    public R_10()
    {
        ReferenceVoltage = 2500;
        NoOfBits = 10;
        ADCMultiplier =
                (
                        (ReferenceVoltage / (2 ^ NoOfBits))
                );
        MachineOffset = 3000;//410;
        SamplingRate = (500);
        TransferRate = 256;//not used
        HardwareGain = (260 * 1.25f);
    }
}
