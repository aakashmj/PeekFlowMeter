#include <jni.h>
#include <string>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <math.h>
char *string1 = "PARAMETER           Pred    Pre   %Pred   Post   %Pred  %Diff ";
char *string2 = "PARAMETER           Pred    Pre   %Pred";
char *newinterpretstring[12] = {"Interpretation:",
                                "Spirometry within normal limits",
                                "Possible early airway obstruction",
                                "Possible restriction",
                                "Pure obstruction",
                                "Combined obstruction & restriction",
                                "Mild restriction",
                                "Moderate restriction",
                                "Severe restriction",
                                "Mild obstruction",
                                "Moderate Obstruction",
                                "Sever obstruction"};
char *versionstr = "(c) Maestros 2018: Ver 1.0.0";
char *paramtitle[32] = {"FVC","FEV1","PEF","FIVC","MVV","RR","SVC","ERV","IRV","Vt","FEV.5","FEV3","FEV.5/FVC",
                        "FEV1/FVC","FEV3/FVC","FEF25%","FEF50%","FEF75%","FEF25-75%","FEF75-85%","FEF.2-1.2",
                        "FVCtime","FMFT","PIF","VE","MVt","Rf","Ti","Te","Vt/Ti","Ti/Ttot","IC"};
char *paramunit[32] = {"(L)","(L)","(L/sec)","(L)","(L)","(1/min)","(L)","(L)","(L)","(L)","(L)","(L)","(%)","(%)",
                       "(%)","(L/sec)","(L/sec)","(L/sec)","(L/sec)","(L/sec)","(L/sec)","(sec)","(sec)","(L/sec)","(L/min)",
                       "(L)","(1/min)","(sec)","(sec)","   ","   ","(L)"};

char returnstring[9] = {' ',' ',' ',' ',' ',' ',' ',' ',0x00};
char asciivalue[10] = {'0','1','2','3','4','5','6','7','8','9'};
char str_arr[2450];
char headerarray[477];	    //477 = 53 * 9
char stringarr[20] = {0x2d,0x31,0x32,0x33,0x2e,0x30,0x35,0x39,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
char DispTestScrnBuff[100]; //12.1.2018
char ActualValBuff[100];    //12.1.2018


const unsigned char fptarray[18] = {0,10,1,11,12,13,14,15,16,17,18,19,20,2,23,3,21,22};
const unsigned char fptarray3[11] = {6,7,8,9,24,26,27,28,29,30,31};
unsigned char ErasingPatientDataFlag = 0;//2.5.2018
unsigned char interpretvalue = 0;
unsigned char iflag = 0;
unsigned char ccnt = 0,yaxiscnt = 0;
unsigned char xcolumncnt = 0;
unsigned char xscale = 0;
unsigned char xaxis_mark = 0;
unsigned char xmarkcnt = 0;
unsigned char bold =0 ;
unsigned char plus_flag = 0,minus_flag = 0,adcflg = 0,tbit = 0,initflg = 0,dotflg=0;
unsigned char printflg=0,djprintflg=0;
unsigned char error_code=0,pre_post_vtc=0,pre_post_flag=0;
unsigned char bold3 = 0,bold4 = 0,fontcnt4 = 0;
unsigned char vollimit = 0;
unsigned char mvv_flg = 0;//8.3.2018
unsigned char fvc_row = 0,fvc_off = 0;
unsigned char sts = 0,buildflg = 0,axisflg = 0;
unsigned char voltimeflg=0,voltimeprint=0,initvoltime=0,dj_volprint=0,dj_flowprint=0;
unsigned char flowvarflg=0,flowvolprint=0,initflowvar=0;
unsigned char pre_vtc=0,pre_post_fvc=0,pre_fvc=0,plus_flag1=0,minus_flag1=0;
unsigned char pre_mvv=0,pre_post_mvv=0,pre_svc=0,pre_post_svc=0;
unsigned char printerrorflag = 0;
unsigned char midreturnflag = 0;
unsigned char djflg = 0;
unsigned char deskjetflag = 0;
unsigned char reportprinted = 0;
unsigned char cleartable = 0;
unsigned char novolumeflag = 0;
unsigned char BestEffortDataBuffer[62000]  ;//20.3.2018
unsigned char TestDataBuffer[62000]  ;
unsigned char plotflag = 0;
unsigned char upperlevellimit = 4;
unsigned char storeeffortno = 1;
unsigned char ReviewoldtestBuffer[200];
unsigned char overlapflag = 0;//17.1.2018
unsigned char repeatflag = 0;//17.1.2018
unsigned char lastid = 0;//17.1.2018
unsigned char currentid = 0;
unsigned char usagetype = 5;
unsigned char currentefforttype = 0;
unsigned char currenteffortno = 0;
unsigned char previouseffortno = 0;
unsigned char besteffortno = 0;
unsigned char currentbestflag = 0;
unsigned char prevflag = 0;
unsigned char nextflag = 0;
unsigned char endflag = 0;
unsigned char dispprintflag = 0;
unsigned char printflag = 0;
unsigned char tptcount = 0;
unsigned char resetvalflag = 0;
unsigned char start_arr[4] = {0,0,4,6};
unsigned char stop_arr[4] = {0,3,5,9};
unsigned char div_factor = 0;
unsigned char serialdatabyte = 0;
unsigned char onesamplereceived = 0;
unsigned char lsbreceived = 0;
unsigned char msbreceived = 0;
unsigned char elapsedtime = 0;
unsigned char e_clk_msec = 0,e_clk_sec = 0;
unsigned char displaymark = 0;
unsigned char autozeroflag = 0;
unsigned char autozeropointer = 0;
unsigned char startplotting = 0;
unsigned char plotnextflag = 0;
unsigned char plotFVCflag = 0;
unsigned char plotMVVflag = 0;
unsigned char plotSVCflag = 0;
unsigned char calibflag = 0;
unsigned char currenttesttype = 1;
unsigned char clk_sec = 0;
unsigned char DrawMarkFlag = 0;

unsigned short prevx = 10;
unsigned short nextx = 10;
unsigned short prevy = 0;
unsigned short nexty = 0;
unsigned short labelcnt = 0,labelcnt1 =0 ;

int PrintGuiDelay = 0;
//int Curr1Buff[1000],Curr2Buff[1000],Curr3Buff[1000],Curr4Buff[1000],CurrDataCounter=0;//21.4.2018 only for debugging
int maxcount = 0;
int volumepointer = 0;//2.1.2018
int serialflag = 0;
int serialreadpointer = 0;
int serialwritepointer = 0;
int serialdatabytelsb = 0;
int serialdatabytemsb = 0;
int serialdataint = 0;
int autozeroarray[240];
int zerovalue = 0;
int clk_msec = 0;
int SerialDataMVVBuffer[4000];
int testdataarray[4000] ;        //3000 //16.4.2018 to avoid corruption
int testvolumedataarray[4000] ;  //3000 //16.4.2018 to avoid corruption
int testdataarraypre[1000] ;  //2.1.2018 ((int *) 0xa060)//1000 int values
int testvolumedataarraypre[1000] ;  //2.1.2018 ((int *) 0xa830)//1000 int values
//int Teststartaddress [3000] @ "ERAM";  //13.3.2018 only for debugging
int BestEffortdataarray[3000] ;
int bankdata;
int mvar10SecRR = 0;
int mvar6SecRR = 0;
int mvar3SecRR = 0;
int rr_p_vol = 0;
int rr_cnt = 0;
int cur_data3 = 0,cur_data_3 = 0,cur_data4 = 0,cur_data_4 = 0,prev_data3 = 0,prev_data4 = 0;
int cur_data1 = 0,cur_data_1 = 0,cur_data2 = 0,cur_data_2 = 0,prev_data1 = 0,prev_data2 = 0;
int step_1, step_2, step_3, step_4;//26.4.2018
int printdata[8] = {0,0,0,0,0,0,0,0};//1.3.2018 earlier 4bytes only now send upto 8bytes of sample
int trace1 = 0, trace2 = 0, trace3 = 0,trace4 = 0;
int p_trace1 = 0, p_trace2 = 0, p_trace3 = 0,p_trace4 = 0;
int p_p_trace1 = 0, p_p_trace2 = 0, p_p_trace3 = 0,p_p_trace4 = 0;
int flowvolthrdcnt =0 ;
int* dataarray, *dataarray_1;
int i1 = 0,i2 = 0,i3,j3,j4,i4,j2 = 0;//27.4.2018   for negative slope FVC

unsigned int tpt1 = 0;
unsigned int tpt2 = 0;
unsigned int tpt3 = 0;
unsigned int pos25 = 0;
unsigned int pos50 = 0;
unsigned int pos75 = 0;
unsigned int pos85 = 0;
unsigned int positions[8] = {0,0,0,0,0,0,0,0};
unsigned int positions1[8] = {0,0,0,0,0,0,0,0};
unsigned int positions2[8] = {0,0,0,0,0,0,0,0};
unsigned int nextindex = 0;
unsigned int nextindexMVV =0; //16.5.2018
unsigned int fvc_org = 0,fvc_org1 = 0;
unsigned int vol_xoff = 0,offset = 0;
unsigned int label_cnt = 0;
//unsigned int i1 = 0,j1 = 0,i2 = 0,i3,j3,j4,i4,j2 = 0;//27.4.2018   // for negative slope FVC
unsigned int maxstart = 0,maxend = 0,minstart = 0,minend = 0,time_out = 0;
unsigned int maxstart1 = 0,maxend1 = 0,minstart1 = 0,minend1 = 0;
unsigned int maxstart2,maxend2,maxstart3,maxend3;
unsigned int yaxis_mark =0;

long autozerosum = 0;
long EffortStorageHeaderAddress = 0;
long EffortStorageAddress = 0;
long EffortStorageReadAddress = 0;
long EffortStoragewriteAddress = 0;
long DummyAddress =0;
long ReviewOldReadAddress =0;
long EffortPredictionwriteAddress = 0;
long EffortPredictionReadAddress = 0;
long EffortCalibrationwriteAddress = 0;
long EffortCalibrationReadAddress = 0;

//float stor_arr1 = 0.0,stor_arr2 = 0.0,stor_arr3 = 0.0,stor_arr4 = 0.0;//23.4.2018
//float slope_1, slope_2, slope_3, slope_4;

double tempanal1, tempanal2, tempanal3, tempanal4,tempanal5;
double flow_rate3 = 0.0,flow_rate4 = 0.0,maxsvcvol = 0.0,maxsvcvol1 = 0.0; //stor_arr3 = 0.0,stor_arr4 = 0.0,
double flow_rate1 = 0.0,flow_rate2 = 0.0;                                  //stor_arr1 = 0.0,stor_arr2 = 0.0,
double stor_arr1 = 0.0,stor_arr2 = 0.0,stor_arr3 = 0.0,stor_arr4 = 0.0;  //23.4.2018
double slope_1, slope_2, slope_3, slope_4;                               //23.4.2018
double volume,vol_cal,vol1,vol2,vol3,vol4,vol5=0,vol6=0,vol7=0,vol8=0;
double tempvolume = 0.00;
double patheightdouble = 68.89;
double inspvol = 0.0;
double expvol = 0.0;
double besteffort = 0.0;
double currentbest = 0.0;
double tempvolume25 = 0;
double tempvolume50 = 0;
double tempvolume75 = 0;
double tempvolume85 = 0;
double Ttot = 0.0;
volatile double predictedvals[32] = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,
                                     0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};//10.4.2018
volatile double actualvals[32] = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,
                                  0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
volatile double actualvals1[32] = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,
                                   0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
volatile double *actual;//10.4.2018
double mvar10SecVol = 0.0;
double mvar6SecVol = 0.0;
double mvar3SecVol = 0.0;
double mvartSecVol = 0.0;

void ResetPrint(void);//26.4.2018
void displayOldTest(unsigned char oldpatientno, unsigned char ReviewEffortNo);
void ConductTestNextHandle(unsigned char effortno);//23.1.2018
void InitNewPatientVariable(void);
void GetAllPatientDetails(void);//19.1.2018
void GetPatientDetails(unsigned char RepeatePatID);//23.1.2018
void GetPatientData(unsigned char RepeatePatID,unsigned char PatientEffortNO);   //23.1.2018
void writeSPIROHeader(unsigned char *data_arr,long addr,int len);
void writeSPIROData(unsigned char *data_arr,long Addr, int len);
void readSPIROData(unsigned char *data_arr,long Addr, int len);
void eraseSPIRODataBlock(short recNo);
void dispOverlapPrompt(unsigned char highlightoption);
void SpiroDataParser(void);
void newWave(unsigned char highlightoption);
void plotFvsVONext();
void plotVvsTNext();
void drawMark(void);
void getVolumeArray(unsigned int s, unsigned int e, int *source, int *dest);
void buildSVCTest();
void dispActValues(void);
void getActValues(unsigned char parameter);
void plotFvsVF(void);
void getPeakData(int *thisintarray, unsigned int *thispositionarray);
void endOfWave(unsigned char highlightoption);
void testAccepted(unsigned char highlightoption);
void storeEffort(unsigned char highlightoption);
void dispStoreEffort(unsigned char highlightoption);
void dispStopCalib(unsigned char highlightoption);
void getInspExp(void);
void dispTestScreen(unsigned char ShowTestType ,unsigned char ShowEffortNo );
void dispReviewOldTestScreen(void);
void InitAutoZero(void);
void getPredVals(void);
void StorePredictionTable(void);
void ReadPredictionTable(void);
void StoreCalibrationTable(void);
void ReadCalibrationTable(void);
void ErasePatientData(void);
void PopulateData(unsigned char pat_id, unsigned char c_testtype, unsigned char printreport_flg);
void printReport(void);//print complete report for this patient
void printEffort(unsigned char highlightoption,unsigned char PrintEffortNo);//print effort selected
void paperFeed(unsigned char count);
void resetvoltime(void);
void resetflowvol(void);
void buildyaxis(void);
void plotflowvol(void);
void plotvoltime(void);
void dispPatDetails(void);
void buildTable(unsigned char index);
void printPre(void);
void plotflowvol_thrd(void);
void plotvoltime_thrd(void);
void GetPatientPreTestData(unsigned char RepeatePatID,unsigned char PreEffortNo);//9.4.2018
void lstrcpy(char *dest,char *source);
void copyData(unsigned char para, unsigned char counter1);
void formParamTitle(unsigned char position , int *ctr);
void dtos(double d,int i,unsigned char flag,char *thisstring);
void SendbuildTable(unsigned char index);
void build_YaxisMarkTable(void);
void storeBestEffort(void);//20.3.2018

unsigned char getString(int intvalue, unsigned char j,char *string);

int getSteps(double volume, double vol, double vol_cal);
int getFlow(int* array, int offset, double prn_factor, int prn_offset);


double getVol(int *array, int offset, int div_factor, unsigned char abs_flag);
double getSlope(int *array, int offset, int steps, double prn_factor, double *init_value, signed char incr); //23.4.2018
double getPercent(double num, double den);
double getDouble(unsigned int offset);


int start;
int stop;
int i = 0;

int t = 0;
int startaddress;
extern char* GenderTable[];

extern const unsigned char feed_data[3];
extern const unsigned char start_data[3];
extern const unsigned char start_cal_pulse[3];
extern unsigned char MAXPATIENTS ;
extern unsigned char updateSetting;
extern unsigned char PerformTestStatus;
extern unsigned char PrintId ;
extern unsigned char prn_error;
extern unsigned char prn_stop_reason;
extern unsigned char CurrentScreenState ;
extern unsigned char ReviewOldTestFlag ;
extern unsigned char SavingEffortFlag ;   //23.4.2018
extern unsigned char LowBattPrintBlockFlag ;//25.5.2018

extern int GrapghIndex;
extern int IdValue,AgeValue,GenderValue,HeightValue,WeightValue,SmokerValue;
extern int ReviewOldSelectValue,ReviewOldPageValue;
extern int TestTickCountA,CountA,TestTickCountB,CountB,TestTickCountC,CountC;

extern float PredictionTableArray[1345];
extern float CalibrationTableArray[4096];

extern void InitPreViewPoints(int NumPoints);
extern void _InitPoints(int NumPoints);
extern void initECGPrint();
extern void build_HeaderTable(void);
extern void build_FVCTable(void);
extern void build_MVVTable(void);
extern void build_SVCTable(void);
extern void SendPrinterGraphType(unsigned char GraphIndex);
extern void startPrint();
extern void endPrint(unsigned char flag);
extern void handlePrnSerial();//26.4.2018
extern void Process_key(unsigned char value);//27.4.2018
extern unsigned char Get_key();

////////////////////////////////////////////////
//Functions
////////////////////////////////////////////////
void getPeakData(int *thisintarray, unsigned int *thispositionarray)//get peak data for test
{
    int i = 0;
    int maxpeakvalue;
    int minpeakvalue;
    for (i = 0; i < 8; i++)
        positions[i] = 0;
    positions[6] = thispositionarray[6];
    positions[7] = thispositionarray[7];
    maxpeakvalue = 0;
    minpeakvalue = 0;
    positions[0] = thispositionarray[6];
    positions[1] = thispositionarray[6];
    positions[2] = thispositionarray[7];
    positions[3] = thispositionarray[7];
    for (i = positions[6]; i < positions[7]; i++)//from start to end of test
    {
        if ((thisintarray[i]) > maxpeakvalue)//get max peak value and position
        {
            maxpeakvalue = (thisintarray[i]);
            positions[4] = i;
        }
        if ((thisintarray[i]) < minpeakvalue)//get min peak value and position
        {
            minpeakvalue = (thisintarray[i]);
            positions[5] = i;
        }
    }
    for (i = positions[4]; i > 0; i--)//back track from max peak to get start position of positive wave
    {
        if ((thisintarray[i]) <= 0)//detect cross over from positive value to negative
        {
            positions[0] = i;
            // (thisintarray.Length + i) = 0;
            thisintarray[i] = 0;
            break;
        }
    }
    for (i = positions[4]; i < positions[7]; i++)//from max peak to end of test
    {
        if ((thisintarray[i]) <= 0)//detect cross over from positive to negative to get end of positive wave
        {
            positions[2] = i;
            //  (thisintarray.Length + i) = 0;
            thisintarray[i] = 0;
            break;
        }
    }
    for (i = positions[5]; i > 0; i--)//back track from min peak to get start position of negative wave
    {
        if ((thisintarray[i]) >= 0)//detect cross over from negative value to positive
        {
            positions[1] = i;
            // (thisintarray.Length + i) = 0;
            thisintarray[i] = 0;
            break;
        }
    }
    for (i = positions[5]; i < positions[7]; i++)//from min peak to end of test
    {
        if ((thisintarray[i]) >= 0)//detect cross over from negative value to positive
        {
            positions[3] = i;
            //  (thisintarray.Length + i) = 0;
            thisintarray[i] = 0;
            break;
        }
    }
    for (i = 0; i < 8; i++)
        thispositionarray[i] = positions[i];
}




void SpiroDataParser(void)
{
    unsigned char i;
    unsigned int t;
    int startaddress;

    serialflag--;
    if (serialreadpointer > 999)
        serialreadpointer = 0;
    if (onesamplereceived)//disard every alternate sample because handset transmits data at 200 samples per second and PoP receives the same at 100 samples per second
    {
        onesamplereceived = 0;
        return;
    }
    else
    {
        if ((serialdatabyte & 0x80) == 0x00)//if MSb of this byte is not set, this byte is LSB of data value
        {
            lsbreceived = 1;//indicate that LSB was received so that next byte can be taken as a valid MSB value
            serialdatabytelsb = (int)(serialdatabyte & 0x7f);
        }
        else//if MSb of this byte is set, this byte is MSB of data value
        {
            if (lsbreceived)//if LSB was received for this data value, then this is a valid data value, hence go ahead
            {
                lsbreceived = 0;
                onesamplereceived = 1;//indicate that one sample was received so that next value can be discarded
                ++elapsedtime;
                if( elapsedtime > 99)//update elapsed time
                {
                    elapsedtime = 0;
                    e_clk_sec++;
                    displaymark = 1;

                }

                serialdataint = (serialdatabyte & 0x1f) * 128 + serialdatabytelsb;

                if (autozeroflag)//if autozeroing is going on
                {
                    if(e_clk_sec >= 2)//19.12.2017
                    {
                        autozeroflag =0;
                        if (currenttesttype == 1)
                        {
                            prevx = 100;//20.3.2018
                            nextx = 100;
                            prevy = 150;
                            nexty = 150;

                        }
                        else if(currenttesttype == 2)
                        {
                            //DMA_ITConfig(DMA1_Stream3, DMA_IT_TC | DMA_IT_HT, DISABLE);//3.4.2018 IN MVV test ploting is started after take pressed till end of test or 29 sec count
                            startplotting = 1;
                            nextindexMVV  = 0;//16.5.2018
                            prevx = 0;//3.4.2018//100;
                            nextx = 0;//100;
                            prevy = 150;//88;
                            nexty = 150;//88;
                            tempvolume = 0.0;
                            serialwritepointer = 0;
                            serialreadpointer = 0;
                            serialflag = 0;

                        }
                        else if(currenttesttype == 3)
                        {
                            prevx = 0;//3.4.2018 //100;
                            nextx = 0;//3.4.2018 //100;
                            prevy = 150;
                            nexty = 150;

                            elapsedtime = 0;
                        }
                        else if(currenttesttype == 4)
                        {
                            //  DMA_ITConfig(DMA1_Stream3, DMA_IT_TC | DMA_IT_HT, DISABLE);
                            //BUTTON_SetText(hButtonCalibrationStart, "REPEAT" ); //12.3.2018 calibration auto zeroing complete
                            //TEXT_SetText(LableSVCPOPup, " ");//3.1.2018
                            elapsedtime = 0;
                            // TEXT_SetText(LabelCalibrationPopUp," START BLOWING ");//3.4.2018
                        }

                        //PerformTestStatus = TEST_AUTOZERO_COMPLETED;

                        autozeroflag = 0;
                        autozerosum = 0;
                        t = 0;
                        for (i=0;i<autozeropointer;i++)//calculate zero value as average of data obtained in the above 2 seconds of data acquisition
                            autozerosum += autozeroarray[i];

                        t = autozerosum / autozeropointer;
                        zerovalue = t;
                        clk_msec = 0;
                        clk_sec = 0;
                        e_clk_sec = 0;
                        elapsedtime = 0;
                        if(calibflag)
                        {
                            //   DMA_ITConfig(DMA1_Stream3, DMA_IT_TC | DMA_IT_HT, ENABLE);
                            nextindex = 0;
                        }
                    }
                    else
                    {
                        autozeroarray[autozeropointer] = serialdataint;//store this data in autozero array for calculating zero value
                        autozeropointer++;

                    }
                }
                else//if data acquisition is going on
                {
                    startaddress = serialdataint - zerovalue;//get address in calibration table from where data value should be picked up for this pressure value (with zero value subtracted)
                    if (startaddress < 0)//negative values in calibration table start from 0
                        startaddress = -(startaddress);
                    else//positive values in calibration table start from 2048
                        startaddress = startaddress + 2048;

                    if (startplotting)//if MVV is being plotted but not stored
                    {
                        if(startaddress >= 2048)
                            serialdataint = (int)(CalibrationTableArray [startaddress]*1000);//*(CALIBRATIONTABLE + startaddress);
                        else
                            serialdataint =(int)( -( CalibrationTableArray[startaddress]*1000));
                        SerialDataMVVBuffer[nextindexMVV] = serialdataint;//16.5.2018 this buffer used for smoothness of MVV waveform
                        //serialdataint = CALIBRATIONTABLE [startaddress];
                        plotnextflag++;//indicate that another value for plotting is now available
                        nextindexMVV++;//16.5.2018
                    }
                    if ((plotFVCflag || plotMVVflag) || (plotSVCflag || calibflag))//if a test data is being acquired and stored, or calibration is being tested
                    {
                        if(startaddress >= 2048)
                            *(testdataarray + nextindex) = (int)(CalibrationTableArray [startaddress]*1000);//20.1.2018//get flow value in array
                        else
                            *(testdataarray + nextindex) = (int)(-(CalibrationTableArray [startaddress]*1000));//20.1.2018//get flow value in array
                        //Teststartaddress[nextindex] =  startaddress;  //20.3.2018 only for debugging
                        //*(testdataarray + nextindex) = CALIBRATIONTABLE [ startaddress];//get flow value in array
                        nextindex++;//indicate that another value is available for plotting and storing
                        plotnextflag++;
                    }
                }//if data acquisition was going on
            }//if LSB was received previously
        }//if MSB is received
    }//if this sample was not to be discarded
}


 void displayActualValue()
{
  start = start_arr[currenttesttype];
  stop = stop_arr[currenttesttype];
  i = start;
    char actval1 = actualvals[i] ;
    char actval2 = actualvals[i+1] ;
    char actval3 = actualvals[i+2] ;
    char actval4 = actualvals[i+3] ;


}


extern "C" JNIEXPORT jstring JNICALL
Java_metsl_spiro_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
