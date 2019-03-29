#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <math.h>
#include "bsp.h"
#include "ECG.h"
#include "global_includes.h"

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
unsigned char interpretflag = NO;
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
unsigned char printgraph = YES;
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
unsigned char BestEffortDataBuffer[62000] @ "ERAM" ;//20.3.2018
unsigned char TestDataBuffer[62000] @ "ERAM" ;
unsigned char PreviewDataBuffer[EFFORT_DATA_SIZE] @ "ERAM" ;  //23.1.2018
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
unsigned char printertype = THERMAL;
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
int SerialDataMVVBuffer[4000] @ "ERAM";
int testdataarray[4000] @ "ERAM";        //3000 //16.4.2018 to avoid corruption
int testvolumedataarray[4000] @ "ERAM";  //3000 //16.4.2018 to avoid corruption
int testdataarraypre[1000] @ "ERAM";  //2.1.2018 ((int *) 0xa060)//1000 int values
int testvolumedataarraypre[1000] @ "ERAM";  //2.1.2018 ((int *) 0xa830)//1000 int values
//int Teststartaddress [3000] @ "ERAM";  //13.3.2018 only for debugging
int BestEffortdataarray[3000] @ "ERAM";
int NORFlashEraseEventBlock1Status = NOR_ERROR;
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
int i1 = 0,j1 = 0,i2 = 0,i3,j3,j4,i4,j2 = 0;//27.4.2018   for negative slope FVC

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

short readSPIROHeader(short recNo,  RECORD_HEADER *hdr);

int getSteps(double volume, double vol, double vol_cal);
int getFlow(int* array, int offset, double prn_factor, int prn_offset);


double getVol(int *array, int offset, int div_factor, unsigned char abs_flag);
double getSlope(int *array, int offset, int steps, double prn_factor, double *init_value, signed char incr); //23.4.2018
double getPercent(double num, double den);
double getDouble(unsigned int offset);

extern RTC_DateTypeDef RTC_DateStructure;
extern RTC_TimeTypeDef  RTC_TimeStructure;
extern SPIRO_SETTING SpiroSettingStruct ;    //17.1.2018
extern PAT_DETAILS_STRUCT  PatDetailsStruct; //16.1.2018
extern PAT_DETAILS_STRUCT  ReviewOldPatDetailsStruct[TOTAL_PATIENTS]; //16.1.2018
extern TypedefSPIROStruct SPIROStruct;

extern GUI_POINT _aPoints[MAX_GRAPH_NUM_POINTS];

extern WM_HWIN hGraphFVCBroncho,hGraphMVVBroncho,hGraphSVCBroncho;
extern WM_HWIN hWinMVVBronchodilutionScreen;

extern GRAPH_DATA_Handle _hDataXY;//16.4.2018 static removed        

extern BUTTON_Handle hButtonFVCBronchoTake,hButtonFVCBronchoNext,hButtonFVCBronchoPrevious,hButtonFVCBronchoPrint,hButtonFVCBronchoExit;
extern BUTTON_Handle hButtonMVVBronchoTake,hButtonMVVBronchoNext,hButtonMVVBronchoPrevious,hButtonMVVBronchoPrint,hButtonMVVBronchoExit;
extern BUTTON_Handle hButtonSVCBronchoTake,hButtonSVCBronchoNext,hButtonSVCBronchoPrevious,hButtonSVCBronchoPrint,hButtonSVCBronchoExit;
extern BUTTON_Handle hButtonCalibrationStart;

extern TEXT_Handle LabelFVCPatDetails,LabelFVCEffort,LabelFVCTestType,LabelFVCVal,LableFEV1Val,LablePEFVal,LableFIVCVal,LableFVCPOPup;
extern TEXT_Handle LabelFVCActualVal,LableFEV1ActualVal,LablePEFActualVal,LableFIVCActualVal;
extern TEXT_Handle LabelMVVPatDetails,LabelMVVEffort,LabelMVVTestType,LabelMVVVal,LablePRVal,LableMVVPOPup;
extern TEXT_Handle LabelMVVActualVal,LablePRActualVal; //6.4.2018
extern TEXT_Handle LabelSVCPatDetails,LabelSVCEffort,LabelSVCTestType,LabelSVCVal,LableERVVal,LableIRVVal,LableTVVal,LableSVCPOPup;
extern TEXT_Handle LabelSVCActualVal,LableERVActualVal,LableIRVActualVal,LableTVActualVal;//6.4.2018
extern TEXT_Handle LabelInspirationValue,LabelExpirationValue,LabelCalibrationPopUp;
extern TEXT_Handle LabelReviewOldPatDetails,LabelReviewOldTestType,LabelReviewOldParameter1,LabelReviewOldParameter2,LabelReviewOldParameter3,LabelReviewOldParameter4,LableReviewOldPOPup;
extern TEXT_Handle LabelReviewOldParaActual1,LabelReviewOldParaActual2,LabelReviewOldParaActual3,LabelReviewOldParaActual4;//6.4.2018

extern char* GenderTable[];

extern const unsigned char feed_data[3];
extern const unsigned char start_data[3];
extern const unsigned char start_cal_pulse[3];
extern unsigned char MAXPATIENTS ;
extern unsigned char updateSetting; 
extern unsigned char PatNameString[MAX_PAT_NAME_LEN+1];
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
extern void FVCGraphUserDraw(WM_HWIN hWin, int Stage);
extern void MVVGraphUserDraw(WM_HWIN hWin, int Stage);
extern void SVCGraphUserDraw(WM_HWIN hWin, int Stage);
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

void SpiroDataParser(void)
{
  unsigned char i;
  unsigned int t;
  int startaddress;
        
  serialflag--;
  serialdatabyte = SPIROStruct.ucaSerialSPIROBuffer[SPIROStruct.ulSPIROReadPtr];//serialbuffer[serialreadpointer++];//get next byte that was serially received from handset
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
            if (currenttesttype == FVC)
            {  
              DMA_ITConfig(DMA1_Stream3, DMA_IT_TC | DMA_IT_HT, DISABLE);                                       
              BUTTON_SetText(hButtonFVCBronchoTake, "START" );  
              TEXT_SetText(LableFVCPOPup, " ");//3.1.2018                                       
              prevx = 100;//20.3.2018
              nextx = 100;
              prevy = 150;
              nexty = 150;
              
            }
            else if(currenttesttype == MVV)
            {
              //DMA_ITConfig(DMA1_Stream3, DMA_IT_TC | DMA_IT_HT, DISABLE);//3.4.2018 IN MVV test ploting is started after take pressed till end of test or 29 sec count                                                                                 
              BUTTON_SetText(hButtonMVVBronchoTake, "START" );  
              TEXT_SetText(LableMVVPOPup, " ");//3.1.2018
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
              SPIROStruct.ulSPIROReadPtr = 0;//6.1.2018
              SPIROStruct.ulSPIROWritePtr =0;
              SPIROStruct.ulSPIRODiffPtr =0;     

            }  
            else if(currenttesttype == SVC)
            {
              DMA_ITConfig(DMA1_Stream3, DMA_IT_TC | DMA_IT_HT, DISABLE);                                                                                
              BUTTON_SetText(hButtonSVCBronchoTake, "START" );  
              TEXT_SetText(LableSVCPOPup, "START BLOWING AND PRESS START");//17.4.2018
              prevx = 0;//3.4.2018 //100;
              nextx = 0;//3.4.2018 //100;
              prevy = 150;
              nexty = 150;

              elapsedtime = 0;                                         
            }
            else if(currenttesttype == CALIBRATION_TEST)
            {
              DMA_ITConfig(DMA1_Stream3, DMA_IT_TC | DMA_IT_HT, DISABLE);                                                                                
              //BUTTON_SetText(hButtonCalibrationStart, "REPEAT" ); //12.3.2018 calibration auto zeroing complete 
              //TEXT_SetText(LableSVCPOPup, " ");//3.1.2018
              elapsedtime = 0;    
              TEXT_SetText(LabelCalibrationPopUp," START BLOWING ");//3.4.2018                        
            }  
            
            PerformTestStatus = TEST_AUTOZERO_COMPLETED;
            
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
              DMA_ITConfig(DMA1_Stream3, DMA_IT_TC | DMA_IT_HT, ENABLE);                                       
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

void newWave(unsigned char highlightoption)//start plotting new wave
{
  unsigned int i;
  
  serialwritepointer = 0;
  serialreadpointer = 0;
  serialflag = 0;
  SPIROStruct.ulSPIROReadPtr = 0;//6.1.2018
  SPIROStruct.ulSPIROWritePtr =0;
  SPIROStruct.ulSPIRODiffPtr =0;
  onesamplereceived = 0;
  
  for (i=0;i<3000;i++)//clear array 
    *(testdataarray + i) = 0;
  
  if (currenttesttype == FVC)
  {
    prevx = 100;//get starting co-ordinates for plotting wave - at 0,0 (meeting point of both axes)
    nextx = 100;
    prevy = 150;
    nexty = 150;
    _aPoints[0].x = 100; //4.4.2018   
    _aPoints[0].y = 150;    
    _aPoints[1].x = 100;//4.4.2018    
    _aPoints[1].y = 150;            
    plotFVCflag = 1;//for main function
    positions1[6] = 0;
    DMA_ITConfig(DMA1_Stream3, DMA_IT_TC | DMA_IT_HT, ENABLE); 
    GrapghIndex = 0; //4.4.2018  // need to avoid garbage while plotFvsVONext  
  }
  
  if (currenttesttype == MVV)
  {
    startplotting = 0;//stop just plotting
    nextindexMVV  = 0;//16.5.2018                
    plotMVVflag = 1;//start plotting and storing
    //drawMark();//draw start of test mark on waveform
    DrawMarkFlag = 1;//3.4.2018    
    //DMA_ITConfig(DMA1_Stream3, DMA_IT_TC | DMA_IT_HT, ENABLE); //3.4.2018  not required in MVV test  as it continue to dispaly baseline after auto zero
    _InitPoints(MAX_GRAPH_NUM_POINTS);//17.1.2018
    GrapghIndex = 0; //17.4.2018  // need to avoid garbage while plotFvsVONext  
    prevx = 0;//get starting co-ordinates for plotting wave - at 0,0 (meeting point of both axes)
    nextx = 0;
    prevy = 150;
    nexty = 150;
    _aPoints[0].x = 0; //17.4.2018   
    _aPoints[0].y = 150;    
    _aPoints[1].x = 0;//17.4.2018    
    _aPoints[1].y = 150;            
    //17.4.2018 check to reset nextindex and plotnextflag to zero
  }
  
  if (currenttesttype == SVC)
  {
    
    plotSVCflag = 1;
    _InitPoints(MAX_GRAPH_NUM_POINTS);//17.1.2018
    GrapghIndex = 0; //17.4.2018  // need to avoid garbage while plotFvsVONext  
    prevx = 0;//get starting co-ordinates for plotting wave - at 0,0 (meeting point of both axes)
    nextx = 0;
    prevy = 150;
    nexty = 150;
    _aPoints[0].x = 0; //17.4.2018   
    _aPoints[0].y = 150;    
    _aPoints[1].x = 0;//17.4.2018    
    _aPoints[1].y = 150;            
    positions1[6] = nextindex;
    
    DMA_ITConfig(DMA1_Stream3, DMA_IT_TC | DMA_IT_HT, ENABLE);   
    //17.4.2018 check to reset nextindex and plotnextflag to zero    

  }
  
  
  clk_msec = 0;
  clk_sec = 0;
  e_clk_sec = 0;
  
}

void plotFvsVONext()//plot FVC runtime
{
  int flowvalue = 0;
  int xvalue = 0;
  short i=0;
  while (plotnextflag > 0)
  {
      flowvalue = testdataarray[nextindex - plotnextflag];//get next flow value
      //tempvolume = tempvolume + (((double)(flowvalue)) / SPIRO_SAMPLING_RATE);//8.1.2018 //SAMPLINGRATEF//get next volume value
      tempvolume = tempvolume + ((double)(flowvalue)) / SPIRO_SAMPLING_RATE;//8.1.2018 //SAMPLINGRATEF//get next volume value    
      //if (plotflag)//if next value can be drawn according to speed
      if(plotnextflag %8 ==0)
      {
        plotflag = 0;
        //nexty = ((WAVEHEIGHT / 2) +(flowvalue / 87)) + 8 ;//19.3.2018 //175 //8.1.2017 //175//scale flow value to get new y value
        nexty = ((WAVEHEIGHT / 2) - (flowvalue / 87)) ;//scale flow value to get new y value      
        //xvalue = (int)(tempvolume / 56.00);//5.4.2018//80.00//scale volume value to get new x value
        //nextx = (xvalue + 100);//6.4.2017      
        nextx = (tempvolume / 56.0) + 100;//13.4.2018           
        if (nextx > 478) //478//8.1.2018//126)
          nextx = 478;   //478//8.1.2018//126;
        if (nextx < 1)
          nextx = 1;
        if (nexty > 299)//8.1.2018 //159)
          nexty = 299;//8.1.2018//159;
        if (nexty < 9)
          nexty = 9;
        _aPoints[ GrapghIndex].x =  nextx;//100+     //20.12.2017
        _aPoints[ GrapghIndex].y =  nexty;//150+                        
        //GRAPH_SetUserDraw(hGraphFVCBroncho, FVCGraphUserDraw); //5.4.2018 
        GrapghIndex++; //16.4.2018  
        if (GrapghIndex > GUI_COUNTOF(_aPoints)-1) GrapghIndex = GUI_COUNTOF(_aPoints)-1;  //16.4.2018      
        //GRAPH_DATA_XY_AddPoint(_hDataXY, &_aPoints[GrapghIndex]);
        //WM_SetStayOnTop(hGraphFVCBroncho, 1);                          
        prevx = nextx;
        prevy = nexty;
        

        
      }
      plotnextflag--;

  }
  GRAPH_SetUserDraw(hGraphFVCBroncho, FVCGraphUserDraw);         //5.4.2018
  GUI_Exec();//3.4.2018    
}


void plotVvsTNext()//calculate and plot volume vs time (real time)
{
  while (plotnextflag > 0)//while samples are available
  {
    if (nextx > 478)//9.1.2018 //126)//limit x value
    {
      nextx = 0;
    }
    if (startplotting)//if data is not being stored for MVV
    {
      //tempvolume = tempvolume + ((double)(serialdataint) /SPIRO_SAMPLING_RATE);//8.3.2018 //2               //9.1.2018 //use serialdataint value to calculate volume value      
      tempvolume = tempvolume + ((double)(SerialDataMVVBuffer[nextindexMVV - plotnextflag]) /SPIRO_SAMPLING_RATE);//8.3.2018 //2      //9.1.2018//use array value to calculate volume value      
      
    }
    else//if data is being stored for MVV or SVC
    {
      //tempvolume = tempvolume + ((double)(testdataarray[nextindex - plotnextflag]) / SAMPLINGRATEF);//9.1.2018//use array value to calculate volume value
      tempvolume = tempvolume + ((double)(testdataarray[nextindex - plotnextflag]) /SPIRO_SAMPLING_RATE);//8.3.2018 //2      //9.1.2018//use array value to calculate volume value      
      testvolumedataarray[volumepointer] = (int)tempvolume;
      volumepointer++;
    }
    
    //if (plotflag == 1)//16.4.2018 if next sample can be plotted (according to required speed)
    if(plotnextflag % 4 == 0) //17.4.2018 for runtime display we have to skip data
    {
      plotflag = 0;
      if (plotSVCflag)//if SVC test is going on
        nexty = (int)(tempvolume / 50.0) + 150+8;//8.3.2018//9.1.2018 //(tempvolume /50.0) + 88;//plot expiratory wave negative
      else//if MVV test is going on
        //nexty = (WAVEHEIGHT - ((int)(tempvolume / 50.0) + 80)) + 8;//plot expiratory wave positive
        nexty = (WAVEHEIGHT - ((int)(tempvolume / 50.0) + 150)) + 8;//8.3.2018//9.1.2018 //(tempvolume / 50.0)) +80//plot expiratory wave positive      
      if (nexty > 299)//9.1.2018 167)//limit y value in waveform
        nexty = 299;//9.1.2018 //167;
      if (nexty < 8)
        nexty = 8;
      //vdda(nextx,prevy,nexty);//draw line
      _aPoints[ GrapghIndex].x =  nextx;      //20.12.2017
      _aPoints[ GrapghIndex].y =  nexty;         
      
//      if(plotSVCflag)     //13.4.2018
//      {
//        GRAPH_SetUserDraw(hGraphSVCBroncho, SVCGraphUserDraw);       
//        //WM_SetStayOnTop(hGraphSVCBroncho, 1);                          
//      }
//      else
//      {
//        GRAPH_SetUserDraw(hGraphMVVBroncho, MVVGraphUserDraw);       
//        //WM_SetStayOnTop(hGraphMVVBroncho, 1);                          
//      }
     
      prevy = nexty;//store this value for next line
      prevx = nextx;
      GrapghIndex++; //17.4.2018  
      if (GrapghIndex > GUI_COUNTOF(_aPoints)-1) 
      {
        //GrapghIndex = GUI_COUNTOF(_aPoints)-1;  //17.4.2018            
        _InitPoints(MAX_GRAPH_NUM_POINTS);     //7.4.2018
        GrapghIndex = 0;  
        nextx =0;       
      }  
      nextx++;//increment x position

    }
    plotnextflag--;//decrement no of samples available
  }
  if(plotSVCflag)//13.4.2018
  {
    GRAPH_SetUserDraw(hGraphSVCBroncho, SVCGraphUserDraw);       
    //WM_SetStayOnTop(hGraphSVCBroncho, 1);                          
  }
  else
  {
    GRAPH_SetUserDraw(hGraphMVVBroncho, MVVGraphUserDraw);       
    //WM_SetStayOnTop(hGraphMVVBroncho, 1);                          
  }  
  GUI_Exec();//3.4.2018      
}

void drawMark(void)//draw 3, 6 and 9 seconds mark on MVV waveform being plotted at runtime
{
  //drawLine(prevx,prevy-4,prevx,nexty+4);//this is useful in case of negative going wave
  //drawLine(prevx,prevy+4,prevx,nexty-4);//this is useful in case of positive going wave
}


void endOfWave(unsigned char highlightoption)//stop test data acquisition, plot test as will be stored and get actual values of parameters
{
  int i;
  
  nextx = 0;
  if (nextindex > 0)//last position in test
    positions1[7] = nextindex - 1;
  else
    positions1[7] = 0;
  if(repeatflag ==1)
  {
    positions1[6] = 0;            
    positions1[7] = 1000;//24.1.2018
  }  
  if (currenttesttype == FVC)
  {
    
    plotFVCflag = 0;
    getPeakData(testdataarray,positions1);
    _InitPoints(MAX_GRAPH_NUM_POINTS);//3.1.2018  to draw effort
    GrapghIndex = 0; //3.1.2018             
    plotFvsVF();
    getActValues(FVC);
    dispActValues();
    if ((actual[0] + actual[1]) > besteffort)//if this is best effort
    {
      currentbest = actual[0] + actual[1];//indicate best effort on LCD
      //putText(6,168,"*",0);
      currentbestflag = 1;
    }
  }
  if (currenttesttype == SVC)
  {
    plotSVCflag = 0;
    buildSVCTest();
    tempvolume = 0.0;
    _InitPoints(MAX_GRAPH_NUM_POINTS);//9.1.2018  to draw effort
    GrapghIndex = 0; //3.1.2018             
    for (i=positions1[6];i<=positions1[7];i++)//plot test as will be stored
    {
      if (((i - positions1[6]) % 3) == 0)//17.4.2018  asper QC Suggestion %8
      {
        //nexty = (testvolumedataarray[i] / 50) + 88;//9.1.2018
        nexty = (testvolumedataarray[i] /50) + 150+8;  //14.3.2018              
        //vdda(nextx,prevy,nexty);
        _aPoints[ GrapghIndex].x =  nextx;      //9.1.2018
        _aPoints[ GrapghIndex].y =  nexty;                        
        GrapghIndex++; //17.4.2018  endofwave SVC Graph
        if (GrapghIndex > GUI_COUNTOF(_aPoints)-1) GrapghIndex = GUI_COUNTOF(_aPoints)-1;  //17.4.2018      
        prevy = nexty;   //17.4.2018
        nextx++;
      }
      //_aPoints[ GrapghIndex].x =  nextx;      //9.1.2018
      //_aPoints[ GrapghIndex].y =  nexty;                        
      //GRAPH_SetUserDraw(hGraphSVCBroncho, SVCGraphUserDraw);       
      //WM_SetStayOnTop(hGraphSVCBroncho, 1);                          
      //GUI_Exec();              
    }
    
    GRAPH_SetUserDraw(hGraphSVCBroncho, SVCGraphUserDraw);       
    //WM_SetStayOnTop(hGraphSVCBroncho, 1);                          
    GUI_Exec();                           
    
    getActValues(SVC);
    dispActValues();
    if (actual[6] > besteffort)//indicate if best effort
    {
      currentbest = actual[6];
      //putText(6,168,"*",0);
      currentbestflag = 1;
    }
  }
  if (currenttesttype == MVV)
  {	
    plotMVVflag = 0;
    //getPeakData(testdataarray,positions1);//9.1.2018            
    _InitPoints(MAX_GRAPH_NUM_POINTS);//9.1.2018  to draw effort
    GrapghIndex = 0; //3.1.2018             
    if (nextindex < 501)//get end of test position for storage
      i = positions1[6];
    else
      i = nextindex - 500;
    while (i < nextindex)//plot test as will be stored
    {
      if ((i % 3) == 0) //17.4.2018  asper QC Suggestion %8 //25.1.2018 //%4 by santosh for same as display old
      {
        //nexty = (WAVEHEIGHT - ((testvolumedataarray[i] / 50) + 80)) + 8;//9.2018
        nexty = (WAVEHEIGHT - ((testvolumedataarray[i] /50) + 150))+8 ;  //14.3.2018              
        //vdda(nextx,prevy,nexty);
        _aPoints[ GrapghIndex].x =  nextx;      //9.1.2018
        _aPoints[ GrapghIndex].y =  nexty;
        GrapghIndex++; //17.4.2018  
        if (GrapghIndex > GUI_COUNTOF(_aPoints)-1) GrapghIndex = GUI_COUNTOF(_aPoints)-1;  //17.4.2018      
        prevy = nexty;  //17.4.2018
        nextx++;
        //GRAPH_SetUserDraw(hGraphMVVBroncho, MVVGraphUserDraw);       
        //WM_SetStayOnTop(hGraphMVVBroncho, 1);                          
        //GUI_Exec();                           
      }
      i++;
      
      //GRAPH_SetUserDraw(hGraphMVVBroncho, MVVGraphUserDraw);       
      //WM_SetStayOnTop(hGraphMVVBroncho, 1);                          
      //GUI_Exec();           
    }
    GRAPH_SetUserDraw(hGraphMVVBroncho, MVVGraphUserDraw);       
    //WM_SetStayOnTop(hGraphMVVBroncho, 1);                          
    GUI_Exec();              
    getVolumeArray(0,1000,testdataarray,testvolumedataarray);//new_vd
    getActValues(MVV);
    dispActValues();
  }
}

void getPeakData(int *thisintarray, unsigned int *thispositionarray)//get peak data for test
{
  unsigned int i = 0;
  int maxpeakvalue;
  int minpeakvalue;
  for (i=0;i<8;i++)
    positions[i] = 0;
  positions[6] = thispositionarray[6];
  positions[7] = thispositionarray[7];
  maxpeakvalue = 0;
  minpeakvalue = 0;
  positions[0] = thispositionarray[6];
  positions[1] = thispositionarray[6];
  positions[2] = thispositionarray[7];
  positions[3] = thispositionarray[7];
  for (i=positions[6];i<positions[7];i++)//from start to end of test
  {
    if (*(thisintarray + i) > maxpeakvalue)//get max peak value and position
    {
      maxpeakvalue = *(thisintarray + i);
      positions[4] = i;
    }
    if (*(thisintarray + i) < minpeakvalue)//get min peak value and position
    {
      minpeakvalue = *(thisintarray + i);
      positions[5] = i;
    }
  }
  for (i=positions[4];i>0;i--)//back track from max peak to get start position of positive wave
  {
    if (*(thisintarray + i) <= 0)//detect cross over from positive value to negative
    {	
      positions[0] = i;
      *(thisintarray + i) = 0;
      break;
    }
  }
  for (i=positions[4];i<positions[7];i++)//from max peak to end of test 
  {
    if (*(thisintarray + i) <= 0)//detect cross over from positive to negative to get end of positive wave
    {	
      positions[2] = i;
      *(thisintarray + i) = 0;
      break;
    }
  }
  for (i=positions[5];i>0;i--)//back track from min peak to get start position of negative wave
  {
    if (*(thisintarray + i) >= 0)//detect cross over from negative value to positive
    {	
      positions[1] = i;
      *(thisintarray + i) = 0;
      break;
    }
  }
  for (i=positions[5];i<positions[7];i++)//from min peak to end of test 
  {
    if (*(thisintarray + i) >= 0)//detect cross over from negative value to positive
    {	
      positions[3] = i;
      *(thisintarray + i) = 0;
      break;
    }
  }
  for (i=0;i<8;i++)
    thispositionarray[i] = positions[i];
}


void plotFvsVF(void)//plot FVC curve from test taken
{
  unsigned int i = 0;
  tempvolume = 0.0;
  //	drawLine(0,88,127,88);//draw axes
  //	drawLine(3,8,3,168);
  
  prevx = 100;//3;
  nextx = 100;//3;
  prevy = 150;//88;
  nexty = 150;//88;
  
  dataarray = testdataarray;
  
  for (i=positions1[0];i<=positions1[2];i++)//form start of positive wave to end of positive wave
  {
    //nexty = ((WAVEHEIGHT / 2) + (((*(dataarray + i) ))/87)) ;//6.4.2018 //175 //+ 8;//8.1.2018 //175//get next flow value to be plotted and scale it to next y value
    nexty = ((WAVEHEIGHT / 2) - (*(dataarray + i) / 87)) ;    
    //nexty = ((WAVEHEIGHT / 2) - (unsigned char)(*(dataarray + i) / 87)) + 8; //6.4.2018 //original       
    tempvolume = tempvolume + ((double)(*(dataarray + i))) / SPIRO_SAMPLING_RATE;//8.1.2018//SAMPLINGRATEF;//get next volume value to be plotted and scale it to next x value
    //nextx = (unsigned char)(tempvolume / 56.0) + 100;//8.1.2018 //56.0 )+3;
    nextx = (tempvolume / 56.0) + 100;//6.4.2018   
    if (nextx > 478)//223//8.1.2018 126)
      nextx = 478;//223//9.1.2018//126;
    if (nextx < 100) //< 1//9.1.2018
      nextx = 100;//1;//9.1.2018
    if (nexty > 299)//8.1.2018//159)
      nexty = 299;//8.1.2018;//159;
    if (nexty < 9)
      nexty = 9;
    //		drawLine(prevx,prevy,nextx,nexty);//draw line to next F and V values
    _aPoints[ GrapghIndex].x =  nextx;      //20.12.2017
    _aPoints[ GrapghIndex].y =  nexty;  
    GrapghIndex++; //17.4.2018  
    if (GrapghIndex > GUI_COUNTOF(_aPoints)-1) GrapghIndex = GUI_COUNTOF(_aPoints)-1;  //17.4.2018      
    //GRAPH_SetUserDraw(hGraphFVCBroncho, FVCGraphUserDraw);       
    //WM_SetStayOnTop(hGraphFVCBroncho, 1);                          
    //GUI_Exec();
    prevx = nextx;
    prevy = nexty;
  }
  //GRAPH_SetUserDraw(hGraphFVCBroncho, FVCGraphUserDraw);       
  //WM_SetStayOnTop(hGraphFVCBroncho, 1);                          
  //GUI_Exec();
  
  prevx = 100;//3;
  prevy = 150;//88;
  
  tempvolume = 0.00;
  //9.1.2018 joint between postive peak and negative peak to be draw
  for (i=positions1[3];i>=positions1[1];i--)//from start to end of negative wave
  {
    //nexty = ((WAVEHEIGHT / 2) + (((*(dataarray + i) ))/87));//19.3.2018 175//+ 8;//8.1.2018 //175 //get next flow value to be plotted and scale it to next y value
    nexty = ((WAVEHEIGHT / 2) - (*(dataarray + i) / 87)) ;    
    //nexty = ((WAVEHEIGHT / 2) - (unsigned char)(*(dataarray + i) / 87)) + 8; //6.4.2018 //original       
    tempvolume = tempvolume + ((double)(0 - *(dataarray + i))) / SPIRO_SAMPLING_RATE;//8.1.2018 //SAMPLINGRATEF;//get next volume value to be plotted and scale it to next x value
    //nextx = (unsigned char)(tempvolume / 56.0)+100;//8.1.2018 56.0) + 3;
    nextx = (tempvolume / 56.0) + 100;//6.4.2018       
    if (nextx > 478)//223//8.1.2018126)
      nextx = 478;//223//126;
    if (nextx < 100)//<1//9.1.2018
      nextx = 100;//1;//9.1.2018
    if (nexty > 299)//8.1.2018 //159)
      nexty = 299;//8.1.2018 //159;
    if (nexty < 9)
      nexty = 9;
    //		drawLine(prevx,prevy,nextx,nexty);//draw line to next F and V values
    _aPoints[ GrapghIndex].x =   nextx;//100+      //20.12.2017
    _aPoints[ GrapghIndex].y =   nexty;//150+ 
        GrapghIndex++; //17.4.2018  
        if (GrapghIndex > GUI_COUNTOF(_aPoints)-1) GrapghIndex = GUI_COUNTOF(_aPoints)-1;  //17.4.2018      
    //GRAPH_SetUserDraw(hGraphFVCBroncho, FVCGraphUserDraw);       
    //WM_SetStayOnTop(hGraphFVCBroncho, 1);                          
    //GUI_Exec();
    prevx = nextx;
    prevy = nexty;
    if (i == 0)
      break;
  }
  GRAPH_SetUserDraw(hGraphFVCBroncho, FVCGraphUserDraw);       
  //WM_SetStayOnTop(hGraphFVCBroncho, 1);                          
  GUI_Exec(); //31.3.2018 hardfault occurs in occupatioanla mode
  
}


void getActValues(unsigned char parameter)//get actual values of parameters
{
  double returnvalue = 0.0;
  unsigned int i = 0;
  unsigned int j = 0;
  unsigned int k = 0;
  double d = 0.0;
  unsigned int s = 0;
  unsigned char inhibit = 5, end_cnt = 0;
  unsigned char disable10 = 0;
  unsigned char disable6 = 0;
  unsigned char disable3 = 0;
  int slope1 = 0;
  int slope2 = 0;
  
  
  for (i=0;i<8;i++)
    positions[i] = 0;
  
  slope1 = 0;
  slope2 = 0;
  tpt1 = 0;
  tpt2 = 0;
  tpt3 = 0;
  tptcount = 0;
  tempvolume25 = 0;
  tempvolume50 = 0;
  tempvolume75 = 0;
  tempvolume85 = 0;
  pos25 = 0;
  pos50 = 0;
  pos75 = 0;
  pos85 = 0;
  Ttot = 0.0;
  
  if (printflag)//different arrays to be used for peak positions and their starts and ends while printing is going on and while it is not going on
  {
    for (i=0;i<8;i++)
      positions[i] = positions2[i];
    actual = actualvals1;//different arrays also to be used for storing the calculated actual values
    if (parameter == FVC)//flow vs time array used for FVC
      dataarray = testdataarraypre;
    else//volume vs time array used for SVC and MVV
    {
      dataarray = testvolumedataarraypre;
      dataarray_1 = 	testdataarraypre;
    }
  }
  else
  {
    for (i=0;i<8;i++)
      positions[i] = positions1[i];
    actual = actualvals;
    if (parameter == FVC)
      dataarray = testdataarray;
    else
    {
      dataarray = testvolumedataarray;
      dataarray_1 = 	testdataarray;
    }
  }
  s = positions[0];//get start position of data in array 
  
  if (resetvalflag == 0)//if printing report is not started, clear actual values previously calculated
  {
    for (i=0;i<32;i++)
    {
      actual[i] = 0.0;
      if (printertype == DESKJET)  //3.4.2018
      {
        actualvals[i] = 0.0;
        actualvals1[i] = 0.0;
      }
    }
  }
  if (parameter == FVC)
  {
    volumepointer = 0;
    d = 0.0;
    for (i=s;i<=positions[2];i++)//from start to end of positive wave
    {
      d = d + ((double)(dataarray[i]) / SAMPLINGRATEF);//integrate flow values to get volume values
      if (i == (SAMPLINGRATED / 2 + s))//if half second data has been integrated
        actual[10] = d / 1000.0;//get FEV0.5
      if (i == (SAMPLINGRATED + s))//if 1 second data has been integrated
        actual[1] = d / 1000.0;//get FEV1
      if (i == (SAMPLINGRATED * 3 + s))//if 3 second data has been integrated
        actual[11] = d / 1000.0;//get FEV3
      testvolumedataarray[volumepointer] = (int)d;//store volume values
      volumepointer++;
      if ((i >= (s + 20)) && (i <= (s + 120)))//get FEV0.2-1.2
        actual[20] = actual[20] + (double)(dataarray[i]);
    }
    actual[20] = (actual[20] / 100.0) / 1000.0;//get FEV0.2-1.2 in L/sec
    actual[0] = d / 1000.0;//get FVC in L/sec
    d = 0.0;
    for (i=positions[1];i<=positions[3];i++)//from start of negative wave to end of negative wave
      d = d + (fabs((double)(dataarray[i])) / SAMPLINGRATEF);//integrate flow values to get volume values
    actual[3] = d / 1000.0;//get FIVC
    actual[2] = ((double)(dataarray[positions[4]])) / 1000.0;//PEF
    actual[23] = fabs(((double)(dataarray[positions[5]])) / 1000.0);//PIF
    tempvolume25 = 250.0 * actual[0];// * 10.0;//25 % of FVC
    tempvolume50 = 500.0 * actual[0];// * 10.0;//50 % of FVC
    tempvolume75 = 750.0 * actual[0];// * 10.0;//75 % of FVC
    tempvolume85 = 850.0 * actual[0];// * 10.0;//85 % of FVC
    
    for (i=0;i<volumepointer;i++)//from start of positive wave to FVC
    {
      if (!pos25 && testvolumedataarray[i] >= (int)tempvolume25)//if 25% of FVC is equalled or exceeded
        pos25 = i;//index for reaching 25% of FVC
      if (!pos50 && testvolumedataarray[i] >= (int)tempvolume50)//if 50 % of FVC is equalled or exceeded
        pos50 = i;//index for reaching 50 % of FVC
      if (!pos75 && testvolumedataarray[i] >= (int)tempvolume75)//if 75 % of FVC is equalled or exceeded
        pos75 = i;//index for reaching 75 % of FVC
      if (!pos85 && testvolumedataarray[i] >= (int)tempvolume85)//if 85 % of FVC is equalled or exceeded
        pos85 = i;//index for reaching 85 % of FVC
    }
    
    /*
    for (i=0;i<volumepointer;i++)//from start of positive wave to FVC
    {
    if (testvolumedataarray[i] >= (int)tempvolume25)//if 25% of FVC is equalled or exceeded
    {
    pos25 = i;//index for reaching 25% of FVC
    break;
  }
  }
    for (i=pos25;i<volumepointer;i++)//25 % of FVC to FVC
    {
    if (testvolumedataarray[i] >= (int)tempvolume50)//if 50 % of FVC is equalled or exceeded
    {
    pos50 = i;//index for reaching 50 % of FVC
    break;
  }
  }
    for (i=pos50;i<volumepointer;i++)//50 % of FVC to FVC
    {
    if (testvolumedataarray[i] >= (int)tempvolume75)//if 75 % of FVC is equalled or exceeded
    {
    pos75 = i;//index for reaching 75 % of FVC
    break;
  }
  }
    for (i=pos75;i<volumepointer;i++)//75 % of FVC to FVC
    {
    if (testvolumedataarray[i] >= (int)tempvolume85)//if 85 % of FVC is equalled or exceeded
    {
    pos85 = i;//index for reaching 85 % of FVC
    break;
  }
  }
    */
    d = 0.0;
    for (i=(s+pos25);i<=(s+pos75);i++)//from 25 % to 75 % of FVC
    {
      d = d + (double)(dataarray[i]);//get flow rate
    }
    actual[18] = (d / (double)((pos75 - pos25) + 1)) / 1000.0;//get mean flow rate from 25 % to 75 % of FVC (FEF25-75)
    d = 0.0;
    for (i=(s+pos75);i<=(s+pos85);i++)//from 75 % to 85 % of FVC
    {
      d = d + (double)(dataarray[i]);//get flow rate
    }
    actual[19] = (d / (double)((pos85 - pos75) + 1)) / 1000.0;//get mean flow rate from 75 % to 85 % of FVC (FEF75-85)
    actual[15] = (double)(dataarray[s+pos25]) / 1000.0;//FEF25%
    actual[16] = (double)(dataarray[s+pos50]) / 1000.0;//FEF50%
    actual[17] = (double)(dataarray[s+pos75]) / 1000.0;//FEF75%
    if (actual[0] > 0)//if FVC > 0
    {
      //  			actual[12] = (actual[10] / actual[0]) * 100.0;//FEV05/FVC
      // 		    	actual[13] = (actual[1] / actual[0]) * 100.0;//FEV1/FVC
      //  			actual[14] = (actual[11] / actual[0]) * 100.0;//FEV3/FVC
      actual[12] = getPercent(actual[10], actual[0]);//FEV05/FVC
      actual[13] = getPercent(actual[1] , actual[0]) ;//FEV1/FVC
      actual[14] = getPercent(actual[11], actual[0]);//FEV3/FVC
      
    }
    //  		if (positions[2] != positions[1])//if end of positive wave is not the same as start of negative wave (does not work properly if this is not done)
    {
      actual[21] = ((double)(positions[2] - s) + 1) / SAMPLINGRATEF;//time taken to reach FVC (FVCtime)
      actual[22] = ((double)(pos75 - pos25) + 1) / SAMPLINGRATEF;//time taken from 25% to 75% of FVC (FMFT)
    }
  }
  if (parameter == MVV)
  {
    returnvalue = 0.0;
    mvartSecVol = 0.0;
    mvar3SecVol = 0.0;
    mvar6SecVol = 0.0;
    mvar10SecVol = 0.0;
    mvar10SecRR = 0;
    mvar6SecRR = 0;
    mvar3SecRR = 0;
    rr_cnt = 0;
    actual[4] = 0.0;
    actual[5] = 0.0;  //3.4.2018
    actual[25] = 0.0; //3.4.2018
    //		for (i=4;i<(positions[7]-8);i++)//from start to just before end of test
    for (i=1;i<999;i++)//changed due to value diff in stored and acq vals : 19.4.2004
    {
      //			if(dataarray_1[i] > 0)
      //				actual[4] += dataarray_1[i] / 100000.0; //(1000 * samplingrate)
      actual[4] += fabs(dataarray_1[i] / 100000.0); //(1000 * samplingrate)
      
      slope1 = dataarray[i - 1] - dataarray[i];
      slope2 = dataarray[i] - dataarray[i + 1];
      
      if(!slope1)
        end_cnt++;
      else
        end_cnt = 0;
      if(end_cnt > 4) //21.3.2014 removed for testing otherwise returns if starting 4 valus are zero.
        break;        //to be check later by santosh
      if(inhibit) --inhibit;
      if (!inhibit && (slope1 * slope2) < 0 && slope1 != 0)//if change in slope is detected
      {
        inhibit = 5;
        if (tptcount == 0)//every alternate turning point
        {
          tpt1 = i ;//get turning point
          tptcount = 1;
        }
        else//every alternate turning point
        {
          tpt2 = i;//get turning point
          if ((tpt1 > 1) && (tpt2 > 1))//if this is not start of data	
          {
            actual[25] = actual[25] + fabs((((double)(dataarray[tpt2] - dataarray[tpt1])) / 1000.0));//tidal volume MVt
            rr_cnt++;//respiration rate count
            tptcount = 0;
            if (!disable6 && i >= (SAMPLINGRATED * 6))//if 6 seconds volume value is reached
            {
              //			            	if (disable6 == 0)
              //		      		      	{
              disable6 = 1;
              mvar6SecRR = rr_cnt;//get respiration count at 6 seconds(for MRR)
              mvar6SecVol = actual[4];//get volume at 6 seconds (for MVV)
              //							}
            } //if (i >= (SAMPLINGRATED * 6))//if 6 seconds volume value is reached
            if (!disable3 && i >= (SAMPLINGRATED * 3))//if 3 seconds volume value is reached
            {
              //		      		      	if (disable3 == 0)
              //	 						{
              disable3 = 1;
              mvar3SecRR = rr_cnt;//get respiration count at 3 seconds(for MRR)
              mvar3SecVol = actual[4];//get volume at 3 seconds (for MVV)
              //							}
            }//if (i >= (SAMPLINGRATED * 3))//if 3 seconds volume value is reached
          }//if ((tpt1 > 1) && (tpt2 > 1))//if this is not start of data	
        }// else tptcount
      } // slope 
      if (!disable10 && i >= ((SAMPLINGRATED * 10) - 3))//if 10 seconds volume value is reached
      {
        //            	if (disable10 == 0)
        //            	{
        disable10 = 1;
        mvar10SecRR = rr_cnt;//get respiration count at 10 seconds(for MRR)
        mvar10SecVol = actual[4];//get volume at 10 seconds (for MVV)
        //				}
      }
    } // for 
    
    mvartSecVol = actual[4];
    if(rr_cnt >0)  //3.4.2018 by santosh to be check with vinayak sir
    actual[25] = actual[25] / (double)rr_cnt;//MVt
    div_factor = 0;
    if (mvar3SecRR > 0)//extrapolate MRR and MVV over 1 minute using 3, 6 and 10 seconds data (whichever is available
      div_factor++;
    if (mvar6SecRR > 0)
      div_factor++;
    if (mvar10SecRR > 0)
      div_factor++;
    if (div_factor > 0)
    {
      actual[5] = (((double)(mvar3SecRR * 20 + mvar10SecRR * 6 + mvar6SecRR * 10)) / (double)div_factor);
      //			actual[4] = 0.5 * ((mvar3SecVol * 20.0 + mvar6SecVol * 10.0 + mvar10SecVol * 6.0) / (double)div_factor);//same as commented above with .5 in the equ;
      // 			actual[4] = ((mvar3SecVol * 20.0 + mvar6SecVol * 10.0 + mvar10SecVol * 6.0) / (double)div_factor);//3.0;
      
      if(i > 300)
        actual[4] = mvartSecVol * 3000 / i; // *.5 is taken care by mulitply 3000 instead of 6000
      else
        actual[4] = 0.0;
      
    }
    else 
      actual[4] = 0.0;
  }
  if (parameter == SVC)
  {
    actual[6] = (fabs((double)(dataarray[positions[4]] - dataarray[positions[5]]))) / 1000.0;//SVC
    tptcount = 0;
    tpt1 = 0;
    tpt2 = 0;
    tpt3 = 0;
    for (i=positions[1];i>8;i--)//start of negative wave to start of test
    {
      slope1 = slope2 = 0;
      j = 4;
      while (slope1 == 0)//while slope is not changed
      {
        slope1 = dataarray[i] - dataarray[i-j];	//get new slope using data values spaced by 4 samples
        j++;
        if ((i - j) <= 9)//if end of test will be reached stop
        {
          i = 8;
          j = 4;
          break;
        }
      }
      k = 4;
      while (slope2 == 0)//while next slope is not changed
      {
        slope2 = dataarray[i-j] - dataarray[(i-j)-k];//get new slope using data values spaced by 4 samples
        k++;
        if (((i - j) - k) <= 9)//if end of test will be reached stop
        {
          i = 8;
          k = 4;
          break;
        }
      }
      if ((slope1 * slope2) < 0)//if change in slope is detected
      {
        if (tptcount == 2)//get turning point
        {
          tpt3 = i - j;
          break;
        }
        if (tptcount == 1)
        {
          tpt2 = i - j;
          tptcount = 2;
          i -= j;
        }
        if (tptcount == 0)
        {
          tpt1 = i - j;
          tptcount = 1;
          i = i - j;
        }
      }
    }
    if (tpt1 > 1)//from first turning point
      actual[7] = fabs(((double)(dataarray[positions[4]] - dataarray[tpt1])) / 1000.0);//get ERV
    if (tpt2 > 1)//from second turning point
    {
      actual[8] = fabs(((double)(dataarray[tpt2] - dataarray[positions[5]])) / 1000.0);//get IRV
      actual[28] = ((double)((tpt1 - tpt2) + 1)) / SAMPLINGRATEF;//Te (time for expiration)
      actual[9] = fabs(((double)(dataarray[tpt1] - dataarray[tpt2])) / 1000.0);//TV
    }
    if (tpt3 > 1)//from 3rd turning point
    {
      actual[27] = ((double)((tpt2 - tpt3) + 1)) / SAMPLINGRATEF;//Ti (time for inspiration)
      Ttot = ((double)((tpt1 - tpt3) + 1)) / SAMPLINGRATEF;//Ttotal (total time for inspiration + expiration)
      actual[9] = ((double)(abs((dataarray[tpt1] - dataarray[tpt2])) + abs((dataarray[tpt2] - dataarray[tpt3])))) / 2000.0;//if one inspiratory and one expiratory cycle is present calculate tidal volume as average of the 2
      if (actual[27] > 0)//if Ti is calculated
        actual[29] = actual[9] / actual[27];//TV/Ti
      if (Ttot > 0)
      {
        actual[30] = actual[27] / Ttot;//Ti/Ttot
        actual[26] = 60.0 / Ttot;//Rf
      }
    }
    actual[31] = fabs((double)dataarray[positions[5]] / 1000.0);//IC
    if(actual[28] > 0) //4.4.2018
      actual[24] = (((double)dataarray[tpt1] / 1000.0) * 60.0) / actual[28];//VE (expiratory volume over a minute)
  }
}

void dispActValues(void)//display calculated values of parameters
{
  unsigned char start;
  unsigned char stop;
  unsigned char i = 0;
 
  start = start_arr[currenttesttype];
  stop = stop_arr[currenttesttype];

  i= start;
  if(currenttesttype == FVC)
  {  
    memset(ActualValBuff,0x00,sizeof(ActualValBuff));          
    //sprintf(ActualValBuff,"FVC      %1.2f             %1.2f",predictedvals[i],actualvals[i]);
    //TEXT_SetText(LabelFVCVal,ActualValBuff);
    sprintf(ActualValBuff,"%1.2f",predictedvals[i]);//6.4.2018
    TEXT_SetText(LabelFVCVal,ActualValBuff);    
    memset(ActualValBuff,0x00,sizeof(ActualValBuff));              
    sprintf(ActualValBuff,"%1.2f",actualvals[i]);   //6.4.2018
    TEXT_SetText(LabelFVCActualVal,ActualValBuff);
    
    memset(ActualValBuff,0x00,sizeof(ActualValBuff));                    
    //sprintf(ActualValBuff,"FEV1    %1.2f             %1.2f",predictedvals[i+1],actualvals[i+1]);
    //TEXT_SetText(LableFEV1Val,ActualValBuff);
    sprintf(ActualValBuff,"%1.2f",predictedvals[i+1]);
    TEXT_SetText(LableFEV1Val,ActualValBuff);
    memset(ActualValBuff,0x00,sizeof(ActualValBuff));                        
    sprintf(ActualValBuff,"%1.2f",actualvals[i+1]);
    TEXT_SetText(LableFEV1ActualVal,ActualValBuff);   //6.4.2018
    
    memset(ActualValBuff,0x00,sizeof(ActualValBuff));                    
    //sprintf(ActualValBuff,"PEF      %1.2f             %1.2f",predictedvals[i+2],actualvals[i+2]);
    //TEXT_SetText(LablePEFVal,ActualValBuff);
    sprintf(ActualValBuff,"%1.2f",predictedvals[i+2]);
    TEXT_SetText(LablePEFVal,ActualValBuff);
    memset(ActualValBuff,0x00,sizeof(ActualValBuff));                    
    sprintf(ActualValBuff,"%1.2f",actualvals[i+2]);
    TEXT_SetText(LablePEFActualVal,ActualValBuff);    //6.4.2018
    
    memset(ActualValBuff,0x00,sizeof(ActualValBuff));                    
    //sprintf(ActualValBuff,"FIVC     %1.2f             %1.2f",predictedvals[i+3],actualvals[i+3]);
    //TEXT_SetText(LableFIVCVal,ActualValBuff);        
    sprintf(ActualValBuff,"%1.2f",predictedvals[i+3]);
    TEXT_SetText(LableFIVCVal,ActualValBuff);        
    memset(ActualValBuff,0x00,sizeof(ActualValBuff));                    
    sprintf(ActualValBuff,"%1.2f",actualvals[i+3]);
    TEXT_SetText(LableFIVCActualVal,ActualValBuff);   //6.4.2018         
    
  }
  else if(currenttesttype == MVV)
  {  
    memset(ActualValBuff,0x00,sizeof(ActualValBuff));          
    //sprintf(ActualValBuff,"MVV      %1.2f             %1.2f",predictedvals[i+0],actualvals[i+0]);
    //TEXT_SetText(LabelMVVVal,ActualValBuff);
    sprintf(ActualValBuff,"%1.2f",predictedvals[i+0]);
    TEXT_SetText(LabelMVVVal,ActualValBuff);
    memset(ActualValBuff,0x00,sizeof(ActualValBuff));          
    sprintf(ActualValBuff,"%1.2f",actualvals[i+0]);
    TEXT_SetText(LabelMVVActualVal,ActualValBuff);     //6.4.2018

    memset(ActualValBuff,0x00,sizeof(ActualValBuff));          
    //sprintf(ActualValBuff,"PR         %1.2f             %1.2f",predictedvals[i+1],actualvals[i+1]);
    //TEXT_SetText(LablePRVal,ActualValBuff);
    sprintf(ActualValBuff,"%1.2f",predictedvals[i+1]);
    TEXT_SetText(LablePRVal,ActualValBuff);
    memset(ActualValBuff,0x00,sizeof(ActualValBuff));          
    sprintf(ActualValBuff,"%1.2f",actualvals[i+1]);
    TEXT_SetText(LablePRActualVal,ActualValBuff);     //6.4.2018
    
  }
  else if(currenttesttype == SVC)
  {  
    memset(ActualValBuff,0x00,sizeof(ActualValBuff));                    
    //sprintf(ActualValBuff,"SVC      %1.2f             %1.2f",predictedvals[i+0],actualvals[i+0]);
    //TEXT_SetText(LabelSVCVal,ActualValBuff);
    sprintf(ActualValBuff,"%1.2f",predictedvals[i+0]);
    TEXT_SetText(LabelSVCVal,ActualValBuff);
    memset(ActualValBuff,0x00,sizeof(ActualValBuff));                    
    sprintf(ActualValBuff,"%1.2f",actualvals[i+0]);
    TEXT_SetText(LabelSVCActualVal,ActualValBuff);  //6.4.2018  
    
    memset(ActualValBuff,0x00,sizeof(ActualValBuff));                    
    //sprintf(ActualValBuff,"ERV      %1.2f             %1.2f",predictedvals[i+1],actualvals[i+1]);
    //TEXT_SetText(LableERVVal,ActualValBuff);
    sprintf(ActualValBuff,"%1.2f",predictedvals[i+1]);
    TEXT_SetText(LableERVVal,ActualValBuff);
    memset(ActualValBuff,0x00,sizeof(ActualValBuff));                    
    sprintf(ActualValBuff,"%1.2f",actualvals[i+1]);
    TEXT_SetText(LableERVActualVal,ActualValBuff);  //6.4.2018
    
    memset(ActualValBuff,0x00,sizeof(ActualValBuff));                    
    //sprintf(ActualValBuff,"IRV       %1.2f             %1.2f",predictedvals[i+2],actualvals[i+2]);
    //TEXT_SetText(LableIRVVal,ActualValBuff);
    sprintf(ActualValBuff,"%1.2f",predictedvals[i+2]);
    TEXT_SetText(LableIRVVal,ActualValBuff);
    memset(ActualValBuff,0x00,sizeof(ActualValBuff));                    
    sprintf(ActualValBuff,"%1.2f",actualvals[i+2]);
    TEXT_SetText(LableIRVActualVal,ActualValBuff);    //6.4.2018
    
    memset(ActualValBuff,0x00,sizeof(ActualValBuff));                    
    //sprintf(ActualValBuff,"TV         %1.2f             %1.2f",predictedvals[i+3],actualvals[i+3]);
    //TEXT_SetText(LableTVVal,ActualValBuff);        
    sprintf(ActualValBuff,"%1.2f",predictedvals[i+3]);
    TEXT_SetText(LableTVVal,ActualValBuff);        
    memset(ActualValBuff,0x00,sizeof(ActualValBuff));                    
    sprintf(ActualValBuff,"%1.2f",actualvals[i+3]);
    TEXT_SetText(LableTVActualVal,ActualValBuff);     //6.4.2018       
  }

}


void buildSVCTest()//build data to plot final SVC waveform and to calculate values of parameters
{
  int difference;
  int tempdifference;
  unsigned int s = 0,e = 0;
  getPeakData(testvolumedataarray,positions1);//get positive and negative peaks, start and end of positive and negative waves and start and end position of test for storing
  if (positions1[1] > positions1[0])//if negative wave occurs after positive wave
  {
    s = positions1[0];//start position of test is start of positive wave
    e = positions1[3];//end position of test is end of negative wave
  }
  else//if negative wave occurs before positive wave
  {
    s = positions1[1];//start position of test is start of negative wave
    e = positions1[2];//end position of test is end of positive wave
  }
  difference = (e - s) + 1;	//get length of test from start to end of waves
  if (difference < 1000)//if this length does not occupy the whole test
  {
    tempdifference = 1000 - difference;//get no of more samples that can be stored
    if (s > tempdifference)//if samples available before current start of test is greater than no of samples that can be stored
      s -= tempdifference;//do not store from start of actual test
    else//if samples available before current start of test is less than no of samples that can be stored
      s = 0;//store from actual start of test
    difference = (e - s) + 1;//get new length of test that will be stored
  }
  if (difference > 1000)//if length of test stored is greater than that which can be stored
  {
    tempdifference = difference - 1000;//get excess no of samples
    s += tempdifference;//move start position of test to be stored further by excess no of samples
  }
  positions1[6] = s;//start of test
  positions1[7] = e;//end of test
}

void getVolumeArray(unsigned int s, unsigned int e, int *source, int *dest)//get volume vs time values from flow vs time values in case of MVV or SVC tests
{
  unsigned int i;
  double tempvol = 0.0;
  for (i=s;i<e;i++)//from start to end of flow value array
  {
    tempvol += ((double)(*(source + i)) / SPIRO_SAMPLING_RATE);//8.3.2018 //25.1.2018 //SAMPLINGRATEF);//integrate flow value and divide by sampling rate		
    *(dest + i) = (int)tempvol;
  }
}

double getPercent(double num, double den)
{
  if(den > 0)                   //27.4.2018   by santosh to avoid division by zero
    return((num / den) * 100);
  else
    return 0.0;                 //27.4.2018
}

void testAccepted(unsigned char highlightoption)//current effort was accepted
{
  //unsigned int i = 0;
  //unsigned int j = 0;
  unsigned int startposition = 0;
  unsigned int endposition = 0;
  // 	unsigned char temp;
  //	disableInt();
  //	operation = 18;
  //  	disableAll();
  if (currenttesttype == FVC)
  {
    if (positions1[0] < positions1[1])//if positive wave occurs before negative wave
    {
      startposition = positions1[0];//start position for storage is start of positive wave
      endposition = positions1[3];//end position for storage is end of negative wave
    }
    else//if positive wave occurs after negative wave
    {
      startposition = positions1[1];//start position for storage is start of negative wave
      endposition = positions1[2];//end position for storage is end of positive wave
    }
  }
  if (currenttesttype == SVC)
  {
    startposition = positions1[6];//start of test
    endposition = positions1[7];//end of test
  }
  if (currenttesttype == MVV)
  {
    startposition = 0;//positions1[6];//start of test
    endposition = positions1[7];//end of test
    storeeffortno = 1;//only one effort to be stored
  }
  //	clearScreen();
  if (((currenttesttype == MVV) && (currenteffortno > MAX_MVV_EFFORT)) || ((currenttesttype != MVV) &&(currenteffortno > MAX_FVC_SVC_EFFORT)))//if max no of efforts have been accepted for this test type
  {
    //3.1.2017  to display popup "CANNOT STORE MORE EFFORTS"
    //  		putText(30,76,"CANNOT STORE",0);//display message
    //  		putText(30,84,"MORE EFFORTS",0);
    //  		enableInt();
    //  		wait(1);
    if (currenttesttype == FVC)//return to test acquisition screen
      //  			dispFVCTest(1);
      TEXT_SetText(LableFVCPOPup, "CANNOT STORE MORE EFFORTS");                                    
    if (currenttesttype == MVV)
      //  			dispMVVTest(1);
      TEXT_SetText(LableMVVPOPup, "CANNOT STORE MORE EFFORTS");                                    
    if (currenttesttype == SVC)
      //  			dispSVCTest(1);
      TEXT_SetText(LableSVCPOPup, "CANNOT STORE MORE EFFORTS");                                    
    return;
  }
  //	currentoffset = (currenteffortno - 1) * RAM_OFFSET;// + 0x0100;//get address in scratch pad where this effort can be stored
  
  //  j = 0;
  //  for (i=startposition;i<=endposition;i++)//from start to end of test
  //  {
  //    bankdata = testdataarray[i];//store data in scratch pad RAM
  //    //	    writeToRAM(6);
  //    j++;
  //  }
  //  while (j < 1000)//0 padding in case test is not 1000 samples long
  //  {
  //    bankdata = 0;
  //    //          writeToRAM(6);
  //    j++;
  //  }
  //	enableInt();
  if ((currenttesttype != MVV) && (currentbestflag))//if this was best effort
  {
    currentbestflag = 0;//update best effort no
    besteffort = currentbest;
    besteffortno = currenteffortno;		
  }
  currenteffortno++;//get next effort no for new test
  if (currenttesttype == FVC)//return to test acquisition screen
  {
    //          dispFVCTest(1);
    storeEffort(1);  //18.1.2018  
  }  
  if (currenttesttype == MVV)
  {
    besteffortno = 1;//12.4.2018 //for MVV besteffort no is always 1 currenteffortno;		
    storeEffort(1);
    //            dispTestSel(1); // go back to test display screen
  }
  //  		dispMVVTest(1);
  if (currenttesttype == SVC)
  {
    //          dispSVCTest(1);
    storeEffort(1);//18.1.2018    
  }  
  
}

void storeEffort(unsigned char highlightoption)//store this effort in RAM
{
  unsigned int i; //startaddress
  unsigned char tempnoofpats; // bankno
  int len =0;
  unsigned int startposition = 0;
  unsigned int endposition = 0;        
  
  if (currenteffortno > 0)//if valid effort no is obtained
  {
    if (currenttesttype == FVC)
    {
      if (positions1[0] < positions1[1])//if positive wave occurs before negative wave
      {
        startposition = positions1[0];//start position for storage is start of positive wave
        endposition = positions1[3];//end position for storage is end of negative wave
      }
      else//if positive wave occurs after negative wave
      {
        startposition = positions1[1];//start position for storage is start of negative wave
        endposition = positions1[2];//end position for storage is end of positive wave
      }
    }
    if (currenttesttype == SVC)
    {
      startposition = positions1[6];//start of test
      endposition = positions1[7];//end of test
    }
    if (currenttesttype == MVV)
    {
      startposition = 0;//positions1[6];//start of test
      endposition = positions1[7];//end of test
      storeeffortno = 1;//only one effort to be stored
    }          
    
    if (repeatflag == 0)//if new test for new patient was taken
    {
      SpiroSettingStruct.LastPatId = currentid;//store id 
      tempnoofpats = SpiroSettingStruct.LastPatId ;
      if (tempnoofpats <= MAXPATIENTS)
      {
        if (tempnoofpats < MAXPATIENTS)
          SpiroSettingStruct.LastPatId = currentid;
        if (tempnoofpats == MAXPATIENTS)
          SpiroSettingStruct.LastPatId = MAXPATIENTS ;//+ 1;
      }
      lastid = SpiroSettingStruct.LastPatId ;// 22.1.2018
      if(overlapflag)
        SpiroSettingStruct.TotalReviewRecord = MAXPATIENTS; //5.4.2018        
      else  
        SpiroSettingStruct.TotalReviewRecord = currentid; //5.4.2018
    }
    //read 
    memset(TestDataBuffer ,0x00,sizeof(TestDataBuffer));    
    EffortStorageReadAddress = ((currentid - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS ;  
    int sizetestbuff = sizeof(TestDataBuffer);    
    //for( len =0 ;len < sizetestbuff;len+=1)
    {
      if(NOR_GetStatus(10) == NOR_SUCCESS)//23.1.2018
      {
        asm("nop");
      }        
      //readSPIROData((unsigned char*)&TestDataBuffer[len],EffortStorageReadAddress,1);  
      readSPIROData((unsigned char*)&TestDataBuffer[0],EffortStorageReadAddress,sizetestbuff);        
      //EffortStorageReadAddress +=1 ;
    }
    
    eraseSPIRODataBlock(currentid);//erase current id
    
    PatDetailsStruct.P_Signature =0x55;
    PatDetailsStruct.PatientID = currentid;
    //PatDetailsStruct.PatientNameArray =;
    if(repeatflag == 0)
    {  
      memcpy(PatDetailsStruct.PatientNameArray,&PatNameString[0],sizeof(PatNameString));
      PatDetailsStruct.PatientAge = AgeValue ;
      PatDetailsStruct.PatientGender = GenderValue ;
      PatDetailsStruct.PatientHeight = HeightValue;
      PatDetailsStruct.PatientWeight = WeightValue ;
      PatDetailsStruct.PatientSmokeStaus = SmokerValue;
    }    
    PatDetailsStruct.PatientEffortNo = currenteffortno - 1;
    PatDetailsStruct.PatientEffortDate = RTC_DateStructure.RTC_Date ; //19.1.2018
    PatDetailsStruct.PatientEffortMonth = RTC_DateStructure.RTC_Month;
    PatDetailsStruct.PatientEffortYear = RTC_DateStructure.RTC_Year;
    PatDetailsStruct.PatientEffortHour = RTC_TimeStructure.RTC_Hours ;
    PatDetailsStruct.PatientEffortMinutes = RTC_TimeStructure.RTC_Minutes;      
    if(currenttesttype == FVC )
    {  
      if(SpiroSettingStruct.UsageType == OCCUPATIONAL) //occupational
      {
          currentefforttype = PRE_EFFORT;//2.5.2018 for occupational first saving error
      }
      if(currentefforttype == PRE_EFFORT)
      {  
        //PatDetailsStruct.PatientFVCPreStaus[PatDetailsStruct.PatientEffortNo - 1]  = 1;
        PatDetailsStruct.PatientFVCPreEfforts  = currenteffortno - 1;//23.1.2018
        EffortStorageAddress = ((currentid - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_FVC_EFFORT_1_PRE +((PatDetailsStruct.PatientEffortNo - 1)*0x2000) );   
      }  
      else
      {  
        //PatDetailsStruct.PatientFVCPostStaus[PatDetailsStruct.PatientEffortNo - 1] = 1;
        PatDetailsStruct.PatientFVCPostEfforts  = currenteffortno - 1;//23.1.2018        
        EffortStorageAddress = ((currentid - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_FVC_EFFORT_1_POST +((PatDetailsStruct.PatientEffortNo - 1)*0x2000) );               
      }  
    }
    if(currenttesttype == MVV )
    {  
      if(currentefforttype == PRE_EFFORT)          
      {
        //PatDetailsStruct.PatientMVVPreStaus[PatDetailsStruct.PatientEffortNo - 1]  = 1;
        PatDetailsStruct.PatientMVVPreEfforts  = currenteffortno - 1;//23.1.2018                
        EffortStorageAddress = ((currentid - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_MVV_EFFORT_1_PRE +((PatDetailsStruct.PatientEffortNo - 1)*0x2000) );             
      }
      else
      {  
        //PatDetailsStruct.PatientMVVPostStaus[PatDetailsStruct.PatientEffortNo - 1] = 1;
        PatDetailsStruct.PatientMVVPostEfforts  = currenteffortno - 1;//23.1.2018                        
        EffortStorageAddress = ((currentid - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_MVV_EFFORT_1_POST +((PatDetailsStruct.PatientEffortNo - 1)*0x2000) );                         
      }
    }
    if(currenttesttype == SVC )
    {  
      if(currentefforttype == PRE_EFFORT)          
      {
        //PatDetailsStruct.PatientSVCPreStaus[PatDetailsStruct.PatientEffortNo - 1]  = 1;
        PatDetailsStruct.PatientSVCPreEfforts  = currenteffortno - 1;//23.1.2018                        
        EffortStorageAddress = ((currentid - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_SVC_EFFORT_1_PRE +((PatDetailsStruct.PatientEffortNo - 1)*0x2000) );               
      }  
      else
      {
        //PatDetailsStruct.PatientSVCPostStaus[PatDetailsStruct.PatientEffortNo - 1] = 1;
        PatDetailsStruct.PatientSVCPostEfforts  = currenteffortno - 1;//23.1.2018                        
        EffortStorageAddress = ((currentid - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_SVC_EFFORT_1_POST +((PatDetailsStruct.PatientEffortNo - 1)*0x2000) );                           
      } 
    }  
    
    //store test data at respective address
    DummyAddress =0;
    DummyAddress = EffortStorageAddress -( ((currentid - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS);
    for(i=0;i<1000; i++)
    {
      if((i>=startposition )&&(i<=endposition))
      {
        
        TestDataBuffer[DummyAddress]   =  (testdataarray[i] >> 24) & 0xFF;
        TestDataBuffer[DummyAddress+1] = (testdataarray[i] >> 16) & 0xFF;
        TestDataBuffer[DummyAddress+2] = (testdataarray[i] >> 8) & 0xFF;
        TestDataBuffer[DummyAddress+3] = testdataarray[i] & 0xFF;
      }
      else
      {
        
        TestDataBuffer[DummyAddress+0] = 0;
        TestDataBuffer[DummyAddress+1] = 0;
        TestDataBuffer[DummyAddress+2] = 0;
        TestDataBuffer[DummyAddress+3] = 0;        
      }
      DummyAddress += sizeof(int);
    }
    
    
    EffortStoragewriteAddress = ((currentid - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS ; 
    memcpy((unsigned char*)&TestDataBuffer[0],(unsigned char*)&PatDetailsStruct,sizeof(PatDetailsStruct));
    for( len =0 ;len < sizetestbuff;len +=2) 
    {
      if(NOR_GetStatus(10) == NOR_SUCCESS)//23.1.2018
      {
        asm("nop");
      }       
      writeSPIROData(TestDataBuffer +len,EffortStoragewriteAddress,2);              
      EffortStoragewriteAddress += 2;//sizeof(int) ;//2
    }          
    updateSetting =1; // to store patient id      //17.1.2018         
    if ((currentid == MAXPATIENTS) && (repeatflag == 0))//if max patients are done with, display overlap prompt
    {
      //enableInt();
      dispOverlapPrompt(1);//5.4.2018 to be checked if it is needed
      return;
    }

  }
}

void dispStoreEffort(unsigned char highlightoption)//get effort no to be stored
{
  //	IE &= 0xdf;
  //  	operation = 19;
  //	enup = 0;
  //	endown = 0;
  //	enleft = 1;
  //	enright = 1;
  //	enselect = 1;
  //	onKeyLftPtr = dispStoreEffort;
  //	onKeyRhtPtr = dispStoreEffort;
  //	onKeySelPtr = storeEffort;
  //	if (optionselected)
  {
    //		clearWave();
    //		putText(6,80,"STORE EFFORT NO: ",0);
    storeeffortno = besteffortno;//display best effort no by default
  }
  //	putNum(108,80,storeeffortno,0);//display effort no selected
  //	IE |= 0x20;
}

void dispStopCalib(unsigned char highlightoption)//when stroke during calibration check was stopped
{
  char InsExpBuff [50];
  
  upperlevellimit = 2;
  
  elapsedtime = 0;
  DMA_ITConfig(DMA1_Stream3, DMA_IT_TC | DMA_IT_HT, DISABLE);                                          
  calibflag = 0;		//disable further entering of data from handset
  if(autozeroflag == 0)  
  {  
    positions1[7] = nextindex;//get end index in flow array
    getInspExp();		//calculate inspiratory and expiratory volume values and display the same
    //putFloat(66,144,inspvol,0);
    //putFloat(60,160,expvol,0);
    memset(InsExpBuff,0x00,sizeof(InsExpBuff));//27.4.2018
    sprintf(InsExpBuff,"%2.2f ",inspvol);
    TEXT_SetText(LabelInspirationValue, InsExpBuff);      
    memset(InsExpBuff,0x00,sizeof(InsExpBuff));//27.4.2018  
    sprintf(InsExpBuff,"%2.2f ",expvol);                
    TEXT_SetText(LabelExpirationValue, InsExpBuff);   
  }
  else
  {
    TEXT_SetText(LabelInspirationValue, "-");//17.5.2018                    
    TEXT_SetText(LabelExpirationValue, "-"); //17.5.2018                           
  }  
  BUTTON_SetText(hButtonCalibrationStart, "REPEAT" );                
  e_clk_sec = 0; //3.4.2018
  TEXT_SetText(LabelCalibrationPopUp,"  ");//3.4.2018                          
}

void getInspExp(void)//get inspiratory volume and expiratory volume for calibration check
{
  unsigned int i = 0;
  positions1[6] = 0;//start
  currenttesttype = FVC;
  printflag = 0;
  getPeakData(testdataarray,positions1);
  inspvol = 0.0;
  for (i=positions1[1];i<=positions1[3];i++)//from start of negative wave to end of negative wave
    inspvol = inspvol + (fabs((double)(testdataarray[i])) / SPIRO_SAMPLING_RATE);
    //inspvol = inspvol + (((double)(testdataarray[i])) /SPIRO_SAMPLING_RATE);//8.3.2018 //*10//9.1.2018 fabs not working so removed //integrate flow values to get inspiratory volume
  inspvol = inspvol  / 1000.0;
  expvol = 0.0;
  for (i=positions1[0];i<=positions1[2];i++)//from start of positive wave to end of positive wave
    expvol = expvol + ((double)(testdataarray[i])  /SPIRO_SAMPLING_RATE);//8.3.2018 //*10//integrate flow values to get expiratory volume
  expvol = expvol / 1000.0;
}

void dispTestScreen(unsigned char ShowTestType ,unsigned char ShowEffortNo )//display test screen
{
  unsigned char start;
  unsigned char stop;
  unsigned char y = 184;
  unsigned char i;
  
  
  //	clearScreen();
  //	putText(0,0,patname,0);//display patient's name, age and sex on LCD
  
  
  start = start_arr[ShowTestType];//[currenttesttype];
  stop = stop_arr[ShowTestType];//[currenttesttype];
  
  for (i=start;i<=stop;i++,y+=8)//display predicted values of parameters for this test on LCD
  {
    //putText(12,y,teststrings[i],0);
    //putFloat(48,y,predictedvals[i],0);
    asm("nop");
  }
  if(ShowTestType == FVC) //currenttesttype
  {  
    memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));        
    sprintf(DispTestScrnBuff,"ID: %d/%6s/AGE:%d",PatDetailsStruct.PatientID,GenderTable[PatDetailsStruct.PatientGender],PatDetailsStruct.PatientAge);//23.1.2018
    TEXT_SetText(LabelFVCPatDetails, DispTestScrnBuff);          
    
    memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));                
    sprintf(DispTestScrnBuff,"EFFORT NO: %d",ShowEffortNo);//currenteffortno);
    TEXT_SetText(LabelFVCEffort, DispTestScrnBuff);                
    
    if(predictedvals[start +0]>0)
      //sprintf(DispTestScrnBuff,"FVC      %1.2f      ",predictedvals[start +0]);
      sprintf(DispTestScrnBuff,"%1.2f",predictedvals[start +0]); //6.4.2018   
    else
      //sprintf(DispTestScrnBuff,"FVC      ----       ");            
      sprintf(DispTestScrnBuff,"----"); //6.4.2018               
    TEXT_SetText(LabelFVCVal,DispTestScrnBuff);
    TEXT_SetText(LabelFVCActualVal," ");//6.4.2018    
    if(predictedvals[start +1]>0)
      //sprintf(DispTestScrnBuff,"FEV1    %1.2f      ",predictedvals[start + 1]);
      sprintf(DispTestScrnBuff,"%1.2f",predictedvals[start + 1]); //6.4.2018   
    else
      //sprintf(DispTestScrnBuff,"FEV1     ----       ");            
      sprintf(DispTestScrnBuff,"----");//6.4.2018                
    TEXT_SetText(LableFEV1Val,DispTestScrnBuff);
    TEXT_SetText(LableFEV1ActualVal," ");   //6.4.2018    

    if(predictedvals[start +2]>0)          
      //sprintf(DispTestScrnBuff,"PEF      %1.2f      ",predictedvals[start + 2]);
      sprintf(DispTestScrnBuff,"%1.2f",predictedvals[start + 2]); //6.4.2018   
    else
      //sprintf(DispTestScrnBuff,"PEF     ----       ");                        
      sprintf(DispTestScrnBuff,"----");   //6.4.2018                         
    TEXT_SetText(LablePEFVal,DispTestScrnBuff);
    TEXT_SetText(LablePEFActualVal," ");    //6.4.2018
    
    if(predictedvals[start +3]>0)
      //sprintf(DispTestScrnBuff,"FIVC     %1.2f      ",predictedvals[start + 3]);
      sprintf(DispTestScrnBuff,"%1.2f",predictedvals[start + 3]);    //6.4.2018
    else
      //sprintf(DispTestScrnBuff,"FIVC     ----       ");                                    
      sprintf(DispTestScrnBuff,"----");   //6.4.2018                                     
    TEXT_SetText(LableFIVCVal,DispTestScrnBuff);        
    TEXT_SetText(LableFIVCActualVal," ");   //6.4.2018             
  }
  else if(ShowTestType == MVV)  //currenttesttype
  {  
    memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));        
    sprintf(DispTestScrnBuff,"ID: %d/%6s/AGE:%d",PatDetailsStruct.PatientID,GenderTable[PatDetailsStruct.PatientGender],PatDetailsStruct.PatientAge);//23.1.2018
    TEXT_SetText(LabelMVVPatDetails, DispTestScrnBuff);          
    
    memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));                
    sprintf(DispTestScrnBuff,"EFFORT NO: %d",currenteffortno);
    TEXT_SetText(LabelMVVEffort, DispTestScrnBuff);
    
    if(predictedvals[start ]>0)          
      //sprintf(DispTestScrnBuff,"MVV      %1.2f      ",predictedvals[start +0]);
      sprintf(DispTestScrnBuff,"%1.2f",predictedvals[start +0]);//6.4.2018    
    else
      //sprintf(DispTestScrnBuff,"MVV      ----       ");                                                
      sprintf(DispTestScrnBuff,"----");//6.4.2018                                                    
    TEXT_SetText(LabelMVVVal,DispTestScrnBuff);
    TEXT_SetText(LabelMVVActualVal," ");     //6.4.2018
    
    if(predictedvals[start +1]>0)          
      //sprintf(DispTestScrnBuff,"PR       %1.2f      ",predictedvals[start+ 1]);
      sprintf(DispTestScrnBuff,"%1.2f",predictedvals[start+ 1]); //6.4.2018   
    else
      //sprintf(DispTestScrnBuff,"PR       ----       ");                                    
      sprintf(DispTestScrnBuff,"----");    //6.4.2018                                    
    TEXT_SetText(LablePRVal,DispTestScrnBuff);
    TEXT_SetText(LablePRActualVal," ");     //6.4.2018    
  }
  else if( ShowTestType == SVC )   //currenttesttype
  { 
    memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));        
    sprintf(DispTestScrnBuff,"ID: %d/%6s/AGE:%d",PatDetailsStruct.PatientID,GenderTable[PatDetailsStruct.PatientGender],PatDetailsStruct.PatientAge);//23.1.2018
    TEXT_SetText(LabelSVCPatDetails, DispTestScrnBuff);          
    
    memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));                
    sprintf(DispTestScrnBuff,"EFFORT NO: %d",currenteffortno);
    TEXT_SetText(LabelSVCEffort, DispTestScrnBuff);                          
    
    if(predictedvals[start ]>0)                    
      //sprintf(DispTestScrnBuff,"SVC      %1.2f      ",predictedvals[start+0]);
      sprintf(DispTestScrnBuff,"%1.2f",predictedvals[start+0]);   //6.4.2018 
    else
      //sprintf(DispTestScrnBuff,"SVC      ----       ");                                                
      sprintf(DispTestScrnBuff,"----");  //6.4.2018                                                  
    TEXT_SetText(LabelSVCVal,DispTestScrnBuff);
    TEXT_SetText(LabelSVCActualVal," ");  //6.4.2018  
    
    if(predictedvals[start +1]>0)                    
      //sprintf(DispTestScrnBuff,"ERV      %1.2f      ",predictedvals[start+1]);
      sprintf(DispTestScrnBuff,"%1.2f",predictedvals[start+1]);   //6.4.2018 
    else
      //sprintf(DispTestScrnBuff,"ERV      ----       ");                                                
      sprintf(DispTestScrnBuff,"----"); //6.4.2018                                                   
    TEXT_SetText(LableERVVal,DispTestScrnBuff);
    TEXT_SetText(LableERVActualVal," ");  //6.4.2018  
    
    if(predictedvals[start +2]>0)          
      //sprintf(DispTestScrnBuff,"IRV      %1.2f      ",predictedvals[start+2]);
      sprintf(DispTestScrnBuff,"%1.2f",predictedvals[start+2]);  //6.4.2018  
    else
      //sprintf(DispTestScrnBuff,"IRV      ----       ");                                                
      sprintf(DispTestScrnBuff,"----"); //6.4.2018                                                   
    TEXT_SetText(LableIRVVal,DispTestScrnBuff);
    TEXT_SetText(LableIRVActualVal," ");  //6.4.2018  
    
    if(predictedvals[start +3]>0)          
      //sprintf(DispTestScrnBuff,"TV       %1.2f      ",predictedvals[start+3]);
      sprintf(DispTestScrnBuff,"%1.2f",predictedvals[start+3]);   //6.4.2018 
    else
      //sprintf(DispTestScrnBuff,"TV       ----       ");                                                
      sprintf(DispTestScrnBuff,"----"); //6.4.2018                                                   
    TEXT_SetText(LableTVVal,DispTestScrnBuff);        
    TEXT_SetText(LableTVActualVal," ");  //6.4.2018      
  }        
}


void dispReviewOldTestScreen(void)//display test screen
{
  unsigned char start;
  unsigned char stop;
  unsigned char y = 184;
  unsigned char i;
  
  
  start = start_arr[currenttesttype];
  stop = stop_arr[currenttesttype];
  
  //for (i=start;i<=stop;i++,y+=8)//display predicted values of parameters for this test on LCD
  //{
  //  asm("nop");
  //}
  if(currenttesttype == FVC)
  {  
    memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));        
    sprintf(DispTestScrnBuff,"ID: %d/%6s/AGE:%d",PatDetailsStruct.PatientID,GenderTable[PatDetailsStruct.PatientGender],PatDetailsStruct.PatientAge);//23.1.2018
    TEXT_SetText(LabelReviewOldPatDetails, DispTestScrnBuff);          
    
    if(SpiroSettingStruct.UsageType == OCCUPATIONAL)  //13.2.2018
    {
      TEXT_SetText(LabelReviewOldTestType,"FVC ");                    
    }
    else
    {  
      if(currentefforttype == PRE_EFFORT)
        TEXT_SetText(LabelReviewOldTestType,"PRE FVC  ");        
      else
        TEXT_SetText(LabelReviewOldTestType,"POST FVC  ");        
    }
    if(predictedvals[start +0]>0)
    {
      //sprintf(DispTestScrnBuff,"FVC      %1.2f             %1.2f",predictedvals[start],actualvals[start]);          
      memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));              
      sprintf(DispTestScrnBuff,"%1.2f",predictedvals[start]);          
      TEXT_SetText(LabelReviewOldParameter1,DispTestScrnBuff);      
    }  
    else
    {  
      memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));        
      sprintf(DispTestScrnBuff,"----");          
      TEXT_SetText(LabelReviewOldParameter1,DispTestScrnBuff);            
    }  
    //TEXT_SetText(LabelReviewOldParameter1,DispTestScrnBuff);
    memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));        
    sprintf(DispTestScrnBuff,"%1.2f",actualvals[start]);              
    TEXT_SetText(LabelReviewOldParaActual1,DispTestScrnBuff);   //6.4.2018   
      
    if(predictedvals[start +1]>0)
    {  
      memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));              
      //sprintf(DispTestScrnBuff,"FEV1    %1.2f             %1.2f",predictedvals[start+1],actualvals[start+1]);                      
      sprintf(DispTestScrnBuff,"%1.2f",predictedvals[start+1]);                      
      TEXT_SetText(LabelReviewOldParameter2,DispTestScrnBuff);      
    }  
    else
    {  
      memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));              
      //sprintf(DispTestScrnBuff,"FEV1     ----             %1.2f",actualvals[start +1]);                      
      sprintf(DispTestScrnBuff,"----");                            
      TEXT_SetText(LabelReviewOldParameter2,DispTestScrnBuff);            
    }  
    //TEXT_SetText(LabelReviewOldParameter2,DispTestScrnBuff);
    memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));            
    sprintf(DispTestScrnBuff,"%1.2f",actualvals[start +1]);              
    TEXT_SetText(LabelReviewOldParaActual2,DispTestScrnBuff);  //6.4.2018                
      
    if(predictedvals[start +2]>0)   
    {  
      //sprintf(DispTestScrnBuff,"PEF      %1.2f             %1.2f",predictedvals[start + 2],actualvals[start + 2]);                      
      memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));                  
      sprintf(DispTestScrnBuff,"%1.2f",predictedvals[start + 2]);                            
      TEXT_SetText(LabelReviewOldParameter3,DispTestScrnBuff);      //6.4.2018
    }  
    else
    {  
      //sprintf(DispTestScrnBuff,"PEF       ----             %1.2f",actualvals[start + 2]);                                  
      memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));                  
      sprintf(DispTestScrnBuff,"----");                                        
      TEXT_SetText(LabelReviewOldParameter3,DispTestScrnBuff);      //6.4.2018      
    }  
    //TEXT_SetText(LabelReviewOldParameter3,DispTestScrnBuff);
    memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));            
    sprintf(DispTestScrnBuff,"%1.2f",actualvals[start + 2]);              
    TEXT_SetText(LabelReviewOldParaActual3,DispTestScrnBuff);       //6.4.2018                    
    
    if(predictedvals[start +3]>0)
    {
      memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));                  
      //sprintf(DispTestScrnBuff,"FIVC      %1.2f             %1.2f",predictedvals[start + 3],actualvals[start + 3]);                                  
      sprintf(DispTestScrnBuff,"%1.2f",predictedvals[start + 3]);   
     TEXT_SetText(LabelReviewOldParameter4,DispTestScrnBuff);              
    }  
    else
    {  
      memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));                  
      //sprintf(DispTestScrnBuff,"FIVC       ----             %1.2f",actualvals[start + 3]);                                  
      sprintf(DispTestScrnBuff,"----");                                        
     TEXT_SetText(LabelReviewOldParameter4,DispTestScrnBuff);              
    }  
    //TEXT_SetText(LabelReviewOldParameter4,DispTestScrnBuff);        
    memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));            
    sprintf(DispTestScrnBuff,"%1.2f",actualvals[start + 3]);              
    TEXT_SetText(LabelReviewOldParaActual4,DispTestScrnBuff);       //6.4.2018    
  }
  else if(currenttesttype == MVV)
  {  
    memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));        
    sprintf(DispTestScrnBuff,"ID: %d/%6s/AGE:%d",PatDetailsStruct.PatientID,GenderTable[PatDetailsStruct.PatientGender],PatDetailsStruct.PatientAge);//23.1.2018
    TEXT_SetText(LabelReviewOldPatDetails, DispTestScrnBuff);          
    
    if(currentefforttype == PRE_EFFORT)
      TEXT_SetText(LabelReviewOldTestType,"PRE MVV  ");        
    else
      TEXT_SetText(LabelReviewOldTestType,"POST MVV  ");        
 
    memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));                
    if(predictedvals[start ]>0)          
    {
      //sprintf(DispTestScrnBuff,"MVV      %1.2f             %1.2f",predictedvals[start ],actualvals[start ]);                                              
      sprintf(DispTestScrnBuff,"%1.2f",predictedvals[start ]);                                                    
    }  
    else
    {
      //sprintf(DispTestScrnBuff,"MVV       ----             %1.2f",actualvals[start ]);                                              
      sprintf(DispTestScrnBuff,"----");                                                          
    }  
    //TEXT_SetText(LabelReviewOldParameter1,DispTestScrnBuff);
    TEXT_SetText(LabelReviewOldParameter1,DispTestScrnBuff);  //6.4.2018
    memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));                    
    sprintf(DispTestScrnBuff,"%1.2f",actualvals[start ]);                                                        
    TEXT_SetText(LabelReviewOldParaActual1,DispTestScrnBuff); //6.4.2018
    
    memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));                    
    if(predictedvals[start +1]>0)          
    {
      //sprintf(DispTestScrnBuff,"PR         %1.2f             %1.2f",predictedvals[start +1 ],actualvals[start +1 ]);                                                          
      sprintf(DispTestScrnBuff,"%1.2f",predictedvals[start +1 ]);                                                                
    }  
    else
    {
      //sprintf(DispTestScrnBuff,"PR         ----              %1.2f",actualvals[start + 1]);                                                          
      sprintf(DispTestScrnBuff,"----");                                                                
    }  
    //TEXT_SetText(LabelReviewOldParameter2,DispTestScrnBuff);
    TEXT_SetText(LabelReviewOldParameter2,DispTestScrnBuff);  //6.4.2018
    memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));                    
    sprintf(DispTestScrnBuff,"%1.2f",actualvals[start + 1]);                                                        
    TEXT_SetText(LabelReviewOldParaActual2,DispTestScrnBuff); //6.4.2018
    
    //TEXT_SetText(LabelReviewOldParameter2,DispTestScrnBuff);    
  }
  else if(currenttesttype == SVC)
  { 
    memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));        
    sprintf(DispTestScrnBuff,"ID: %d/%6s/AGE:%d",PatDetailsStruct.PatientID,GenderTable[PatDetailsStruct.PatientGender],PatDetailsStruct.PatientAge);//23.1.2018
    TEXT_SetText(LabelReviewOldPatDetails, DispTestScrnBuff);          
    
    if(currentefforttype == PRE_EFFORT)
      TEXT_SetText(LabelReviewOldTestType,"PRE SVC  ");        
    else
      TEXT_SetText(LabelReviewOldTestType,"POST SVC  ");        
    
    memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));                    
    if(predictedvals[start ]>0)                    
    {
      //sprintf(DispTestScrnBuff,"SVC        %1.2f             %1.2f",predictedvals[start ],actualvals[start ]);                                                                            
      sprintf(DispTestScrnBuff,"%1.2f",predictedvals[start ]);                                                                      
    }  
    else
    {
      //sprintf(DispTestScrnBuff,"SVC        ----             %1.2f",actualvals[start ]);                                                                      
      sprintf(DispTestScrnBuff,"----");                                                                            
    }  
    //TEXT_SetText(LabelReviewOldParameter1,DispTestScrnBuff);
    TEXT_SetText(LabelReviewOldParameter1,DispTestScrnBuff);  //6.4.2018
    memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));                    
    sprintf(DispTestScrnBuff,"%1.2f",actualvals[start ]);                                                        
    TEXT_SetText(LabelReviewOldParaActual1,DispTestScrnBuff); //6.4.2018
    
    memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));                        
    if(predictedvals[start +1]>0)                    
    {
      //sprintf(DispTestScrnBuff,"ERV        %1.2f             %1.2f",predictedvals[start +1],actualvals[start +1]);                                                                                  
      sprintf(DispTestScrnBuff,"%1.2f",predictedvals[start +1]);                                                                                        
    }  
    else
    {
      //sprintf(DispTestScrnBuff,"ERV        ----             %1.2f",actualvals[start +1]);                                                                                  
      sprintf(DispTestScrnBuff,"----");                                                                                        
    }  
    //TEXT_SetText(LabelReviewOldParameter2,DispTestScrnBuff);
    TEXT_SetText(LabelReviewOldParameter2,DispTestScrnBuff);  //6.4.2018
    memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));                    
    sprintf(DispTestScrnBuff,"%1.2f",actualvals[start + 1]);                                                        
    TEXT_SetText(LabelReviewOldParaActual2,DispTestScrnBuff); //6.4.2018
    
    memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));                    
    if(predictedvals[start +2]>0)          
    {  
      //sprintf(DispTestScrnBuff,"IRV        %1.2f             %1.2f",predictedvals[start +2],actualvals[start +2]);                                                                                              
      sprintf(DispTestScrnBuff,"%1.2f",predictedvals[start +2]);                                                                                                    
    }  
    else
    {
      //sprintf(DispTestScrnBuff,"IRV         ----             %1.2f",actualvals[start +2]);                                                                                              
      sprintf(DispTestScrnBuff,"----");                                                                                                    
    }  
    //TEXT_SetText(LabelReviewOldParameter3,DispTestScrnBuff);
    TEXT_SetText(LabelReviewOldParameter3,DispTestScrnBuff);  //6.4.2018
    memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));                    
    sprintf(DispTestScrnBuff,"%1.2f",actualvals[start + 2]);                                                        
    TEXT_SetText(LabelReviewOldParaActual3,DispTestScrnBuff); //6.4.2018
    
    memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));                    
    if(predictedvals[start +3]>0)          
    {  
      //sprintf(DispTestScrnBuff,"TV         %1.2f             %1.2f",predictedvals[start +3],actualvals[start +3]);                                                                                              
      sprintf(DispTestScrnBuff,"%1.2f",predictedvals[start +3]);                                                                                                    
    }  
    else
    {  
      sprintf(DispTestScrnBuff,"----");                                                                                              
    }  
    //TEXT_SetText(LabelReviewOldParameter4,DispTestScrnBuff);        
    TEXT_SetText(LabelReviewOldParameter4,DispTestScrnBuff);  //6.4.2018
    memset(DispTestScrnBuff,0x00,sizeof(DispTestScrnBuff));                    
    sprintf(DispTestScrnBuff,"%1.2f",actualvals[start +3]);                                                        
    TEXT_SetText(LabelReviewOldParaActual4,DispTestScrnBuff); //6.4.2018    
  }        
}

void InitAutoZero(void)
{
  tempvolume = 0.0;
  nextindex = 0;
  volumepointer = 0;
  autozeropointer = 0;  
  autozeroflag = 1;

  //TEXT_SetText(LabelCalibrationPopUp,"AUTO-ZEROING"); //19.5.2018         

  serialwritepointer = 0;
  serialreadpointer = 0;
  serialflag = 0;
  SPIROStruct.ulSPIROReadPtr = 0;//6.1.2018
  SPIROStruct.ulSPIROWritePtr =0;
  SPIROStruct.ulSPIRODiffPtr =0;    
  clk_msec = 0;
  clk_sec = 0;
  e_clk_sec = 0;
  elapsedtime = 0; 
  _InitPoints(MAX_GRAPH_NUM_POINTS);//2.1.2018

  if(currenttesttype == FVC)             ////21.3.2018
  {
    nextx = 100;
    nexty = 150;
    _aPoints[0].x = 100;    
    _aPoints[0].y = 150;    
    _aPoints[1].x = 100;    
    _aPoints[1].y = 150;        
    GRAPH_SetUserDraw(hGraphFVCBroncho, FVCGraphUserDraw);       
  }  
  else if(currenttesttype == MVV)
  {
    nextx = 0;  //3.4.2018 //100
    nexty = 150;
    _aPoints[0].x = 0;//3.4.2018 //100    
    _aPoints[0].y = 150;    
    GRAPH_SetUserDraw(hGraphMVVBroncho, MVVGraphUserDraw);       
  }  
  else if(currenttesttype == SVC)
  {
    nextx = 0;  //3.4.2018 //100
    nexty = 150;
    _aPoints[0].x = 0;//3.4.2018 //100    
    _aPoints[0].y = 150;        
    GRAPH_SetUserDraw(hGraphSVCBroncho, SVCGraphUserDraw);       
  }  
  GUI_Exec();
  //GUI_Delay(GUI_EXIT_DELAY_COUNT);//19.5.2018  
}  


void dispOverlapPrompt(unsigned char highlightoption)//display warning when previously stored tests are going to be overlapped with new ones (when max no of patients' test has been taken)
{
  //unsigned char i;
  //unsigned char end;
  //unsigned char abcoffset;
  //	disableAll();//disable all navigation keys except for select
  //	enselect = 1;
  //	clearScreen();
  if (currentid == MAXPATIENTS)//if last patient's test was stored
  {
    //		end = 7;
    //		abcoffset = 0;
    //	  	if (usagetype == CLINICAL)//after prompt, user intervention will cause returning to test selection screen in case of clinical mode
    //			onKeySelPtr = dispTestSel;
    //		else//after prompt, user intervention will cause returning to main menu in case of occupational health mode
    //			onKeySelPtr = dispMainMenu;
  }
  if (currentid == 1)//if storing new patient's test will cause overlapping of previous patient's test (this if patient 1 after max patients' tests have been taken)
  {
    //		end = 4;
    //		abcoffset = 7;
    //		onKeySelPtr = dispTestSel;//user intervention will cause returning to test selection screen
  }
  //	for (i=0;i<end;i++)//display warning string
  //		putText(0,80+8*i,overlapstring[abcoffset+i],0);
  //	putText(54,144,"OK",1);
}

void writeSPIROHeader(unsigned char *data_arr,long addr,int len)
{
  if( NOR_WriteBuffer((uint16_t*)data_arr, addr, len / 2) != NOR_SUCCESS)
  {
    showStatusMsg("Write Delay", 'I');
  }

}

short readSPIROHeader(short recNo,  RECORD_HEADER *hdr)
{
  long addr = (recNo - 1 * 0x20000 ) + NOR_SPIRODATA_ADDRESS ;
  NOR_ReadBuffer((uint16_t*)hdr, addr, sizeof(RECORD_HEADER) / 2);
  
  return(1);
}

void writeSPIROData(unsigned char *data_arr,long Addr, int len)
{
  int NORFlashWriteEventDataStatus = NOR_ERROR;  
  short WriteEventTrialCount =0;
  
  WriteEventTrialCount =0;
  while(  WriteEventTrialCount < 10)//1.2.2017
  {
    NORFlashWriteEventDataStatus = NOR_GetStatus(10);// == NOR_SUCCESS
    if( NORFlashWriteEventDataStatus == NOR_SUCCESS )
      break;  
    WriteEventTrialCount++;
    
  }    
  NOR_WriteBuffer((uint16_t*)data_arr, Addr, len / 2);  
  //writeAddr += len; 
}

void readSPIROData(unsigned char *data_arr,long Addr, int len)
{
  int NORFlashReadEventDataStatus = NOR_ERROR ;
  short ReadEventDataTrialCount =0;
  //short ReadTrendStatus = 0;//21.2.2017
  
  NOR_ReturnToReadMode();//3.2.2017
  ReadEventDataTrialCount =0;
  while(  ReadEventDataTrialCount < 10)//1.2.2017
  {
    NORFlashReadEventDataStatus = NOR_GetStatus(10);// == NOR_SUCCESS
    if( NORFlashReadEventDataStatus == NOR_SUCCESS )
      break;  
    ReadEventDataTrialCount++;
    
  }  
  if( NORFlashReadEventDataStatus == NOR_SUCCESS )  
    NOR_ReadBuffer((uint16_t*)data_arr, Addr, len / 2);       
  //readAddr += len; 
}

void eraseSPIRODataBlock(short recNo)
{
  short status = 0;
  short EraseEventBlock1TrialCount = 0;
  
  uint32_t addr = ((recNo - 1 )* 0x20000 ) + NOR_SPIRODATA_ADDRESS;
  
  NORFlashEraseEventBlock1Status = NOR_ERROR;  
  EraseEventBlock1TrialCount =0;
  while(  EraseEventBlock1TrialCount < 10)//1.2.2017
  {
    NORFlashEraseEventBlock1Status = NOR_GetStatus(10);// == NOR_SUCCESS
    if( NORFlashEraseEventBlock1Status == NOR_SUCCESS )
      break;  
    EraseEventBlock1TrialCount++;
    
  }   
  if(NORFlashEraseEventBlock1Status == NOR_SUCCESS)                  
  {
    NOR_ReturnToReadMode();
    status = NOR_EraseBlock(addr);//23.1.2018(uint32_t)((recordNo * NOR_EVENTRECORD_BLOCKSIZE ) + NOR_EVENTRECORD_ADDRESS));  
    if( status != NOR_SUCCESS)
    {
      showStatusMsg("Block Erase Fail", 'E');
    }                      
    
    // initiaiise writeAdddr
    //WriteEventDataAddr = (recordNo * NOR_EVENTRECORD_BLOCKSIZE ) + NOR_EVENTRECORD_ADDRESS + RECORD_HEADER_SIZE;
  }
  else
  {
    showStatusMsg("Event Erase Failed", 'E');    
  }  
}

void GetAllPatientDetails(void)
{
  int i=0,len =0;
  int sizetestbuff =0;   

  //for(i=0;i < lastid ;i++)
  for(i=0;i < SpiroSettingStruct.TotalReviewRecord ;i++)  //5.4.2018  
  {  
    ReviewOldReadAddress = (i * 0x20000 ) + NOR_SPIRODATA_ADDRESS ;  
    sizetestbuff = sizeof(ReviewOldPatDetailsStruct[0]);    
    //for( len =0 ;len < sizetestbuff;len+=1)
    //{
    //readSPIROData((unsigned char *)&ReviewOldPatDetailsStruct[i],ReviewOldReadAddress,sizetestbuff);  
    if(NOR_GetStatus(10) == NOR_SUCCESS)//23.1.2018
    {
      asm("nop");
    }        
    readSPIROData(ReviewoldtestBuffer,ReviewOldReadAddress,sizetestbuff);        
    //  ReviewOldReadAddress +=1 ;
    //} 
    memcpy((unsigned char *)&ReviewOldPatDetailsStruct[i],(unsigned char*)&ReviewoldtestBuffer[0],sizetestbuff);      
  }
} 

void GetPatientDetails(unsigned char RepeatePatID)
{
  //int i=0,len =0;
  int sizetestbuff =0;   
  
  ReviewOldReadAddress = ((RepeatePatID-1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS ;  
  sizetestbuff = sizeof(PatDetailsStruct);    
  if(NOR_GetStatus(10) == NOR_SUCCESS)//23.1.2018
  {
    asm("nop");
  }            
  readSPIROData(ReviewoldtestBuffer,ReviewOldReadAddress,sizetestbuff);        
  memcpy((unsigned char *)&PatDetailsStruct,(unsigned char*)&ReviewoldtestBuffer[0],sizetestbuff);      
  patheightdouble = (double)PatDetailsStruct.PatientHeight / 2.54;//get height of patient in inches
} 

void GetPatientData(unsigned char RepeatePatID,unsigned char PatientEffortNo)
{
  //int i=0,len =0;
  int sizetestbuff =0;   
  
  if(NOR_GetStatus(10) == NOR_SUCCESS)//23.1.2018
  {
    asm("nop");
  }            
  
  //read 
  memset(TestDataBuffer ,0x00,sizeof(TestDataBuffer));    
  EffortStorageReadAddress = ((RepeatePatID - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS ;  
  sizetestbuff = sizeof(TestDataBuffer);    
  //for( len =0 ;len < sizetestbuff;len+=1)
  {
    if(NOR_GetStatus(10) == NOR_SUCCESS)//23.1.2018
    {
      asm("nop");
    }        
    readSPIROData((unsigned char*)&TestDataBuffer[0],EffortStorageReadAddress,sizetestbuff);  
    //EffortStorageReadAddress +=1 ;
  }
  asm("nop");  
  if(currenttesttype == FVC )
  {  
    if(currentefforttype == PRE_EFFORT)
    {  
      //PatDetailsStruct.PatientFVCPreStaus[PatDetailsStruct.PatientEffortNo - 1]  = 1;
      //PatDetailsStruct.PatientFVCPreEfforts  = currenteffortno - 1;//9.4.2018
      EffortStorageReadAddress = ((RepeatePatID - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_FVC_EFFORT_1_PRE +((PatientEffortNo  - 1)*0x2000) );   //9.4.2018 PatDetailsStruct.PatientEffortNo 
    }  
    else
    {  
      //PatDetailsStruct.PatientFVCPostStaus[PatDetailsStruct.PatientEffortNo - 1] = 1;
      //PatDetailsStruct.PatientFVCPostEfforts  = currenteffortno - 1;//9.4.2018     
      EffortStorageReadAddress = ((RepeatePatID - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_FVC_EFFORT_1_POST +((PatientEffortNo - 1)*0x2000) );  //9.4.2018 PatDetailsStruct.PatientEffortNo           
    }  
  }
  if(currenttesttype == MVV )
  {  
    if(currentefforttype == PRE_EFFORT)          
    {
      //PatDetailsStruct.PatientMVVPreStaus[PatDetailsStruct.PatientEffortNo - 1]  = 1;
      //PatDetailsStruct.PatientMVVPreEfforts  = currenteffortno - 1;//9.4.2018               
      EffortStorageReadAddress = ((RepeatePatID - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_MVV_EFFORT_1_PRE +((PatientEffortNo - 1)*0x2000) );   //9.4.2018 PatDetailsStruct.PatientEffortNo          
    }
    else
    {  
      //PatDetailsStruct.PatientMVVPostStaus[PatDetailsStruct.PatientEffortNo - 1] = 1;
      //PatDetailsStruct.PatientMVVPostEfforts  = currenteffortno - 1;//9.4.2018                       
      EffortStorageReadAddress = ((RepeatePatID - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_MVV_EFFORT_1_POST +((PatientEffortNo - 1)*0x2000) );  //9.4.2018 PatDetailsStruct.PatientEffortNo                     
    }
  }
  if(currenttesttype == SVC )
  {  
    if(currentefforttype == PRE_EFFORT)          
    {
      //PatDetailsStruct.PatientSVCPreStaus[PatDetailsStruct.PatientEffortNo - 1]  = 1;
      //PatDetailsStruct.PatientSVCPreEfforts  = currenteffortno - 1;//9.4.2018                        
      EffortStorageReadAddress = ((RepeatePatID - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_SVC_EFFORT_1_PRE +((PatientEffortNo - 1)*0x2000) );    //9.4.2018 PatDetailsStruct.PatientEffortNo         
    }  
    else
    {
      //PatDetailsStruct.PatientSVCPostStaus[PatDetailsStruct.PatientEffortNo - 1] = 1;
      //PatDetailsStruct.PatientSVCPostEfforts  = currenteffortno - 1;//9.4.2018                        
      EffortStorageReadAddress = ((RepeatePatID - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_SVC_EFFORT_1_POST +((PatientEffortNo - 1)*0x2000) );  //9.4.2018 PatDetailsStruct.PatientEffortNo                        
    } 
  }        
  if(NOR_GetStatus(10) == NOR_SUCCESS)//23.1.2018
  {
    asm("nop");
  }        
  sizetestbuff = 1000 * sizeof(int);//      sizeof(PreviewDataBuffer);      
  //for(int j=0;j<1000;j++)
  {
    readSPIROData(PreviewDataBuffer,EffortStorageReadAddress,sizetestbuff);  //23.1.2018    
    //readSPIROData((unsigned char *)(PreviewDataBuffer+(j*(sizeof(int)))),EffortStorageReadAddress,sizeof(int));  //23.1.2018
    //EffortStorageReadAddress += sizeof(int);
  }  
  InitPreViewPoints(1000);  
} 

void  InitNewPatientVariable(void)
{

}  

void InitPreViewPoints(int NumPoints) 
{
  int i,j;

  if(currenttesttype == FVC)
  {  
    memset(testdataarray,0x00,sizeof(testdataarray));
    for (i = 0,j=0; i <= NumPoints - 1; i++,j+=4) 
    {
        testdataarray[i] = ((uint32_t)PreviewDataBuffer[j+0] << 24 )|
                           ((uint32_t)PreviewDataBuffer[j+1] << 16 )|
                           ((uint32_t)PreviewDataBuffer[j+2] << 8  )|
                           ((uint32_t)PreviewDataBuffer[j+3]);        
    }
  }
  else
  {
    memset(testvolumedataarray,0x00,sizeof(testvolumedataarray));    
    for (i = 0,j=0; i <= NumPoints - 1; i++,j+=4) 
    {    
      //testvolumedataarray[i]
        testdataarray[i]= ((uint32_t)PreviewDataBuffer[j+0] << 24 )|
                          ((uint32_t)PreviewDataBuffer[j+1] << 16 )|
                          ((uint32_t)PreviewDataBuffer[j+2] << 8  )|
                          ((uint32_t)PreviewDataBuffer[j+3]);    
    }    
  }  
  
}

void ConductTestNextHandle(unsigned char effortno)
{
  unsigned char testEffortno =0;
    if(currenttesttype == FVC )
    {  
      if(currentefforttype == PRE_EFFORT)
      {  
        testEffortno = PatDetailsStruct.PatientFVCPreEfforts ;//23.1.2018
      }  
      else
      {  
        testEffortno = PatDetailsStruct.PatientFVCPostEfforts ;//23.1.2018        
      }  
    }
    if(currenttesttype == MVV )
    {  
      if(currentefforttype == PRE_EFFORT)          
      {
        testEffortno =PatDetailsStruct.PatientMVVPreEfforts ;//23.1.2018                
      }
      else
      {  
        testEffortno = PatDetailsStruct.PatientMVVPostEfforts ;//23.1.2018                        
      }
    }
    if(currenttesttype == SVC )
    {  
      if(currentefforttype == PRE_EFFORT)          
      {
        testEffortno = PatDetailsStruct.PatientSVCPreEfforts ;//23.1.2018                        
      }  
      else
      {
        testEffortno = PatDetailsStruct.PatientSVCPostEfforts ;//23.1.2018                        
      } 
    }    
  
}  

void displayOldTest(unsigned char oldpatientno, unsigned char ReviewEffortNo)//load this test's data for this patient and display waveform on LCD
{
  unsigned int  i;//startaddress = 0,

  GetPatientDetails(oldpatientno);
  getPredVals();  //10.4.2018

  if(currenttesttype == FVC)
  {
    //getActValues(FVC); //13.2.2018
    if(currentefforttype == PRE_EFFORT)
    {
      if((ReviewEffortNo <= PatDetailsStruct.PatientFVCPreEfforts )&&( ReviewEffortNo <= MAX_FVC_SVC_EFFORT))
      {  
          //Tempeffortno =  ReviewEffortNo;  //currenteffortno 9.4.2018  //need to chk whether next effort incremented     
          GetPatientData(oldpatientno ,ReviewEffortNo);        //9.4.2018 
          if(ReviewOldTestFlag == 1) //31.3.2018 repeatflag
            TEXT_SetText(LableReviewOldPOPup, " "); //20.3.2018 hardfault to be chk
          else
            TEXT_SetText(LableFVCPOPup, " ");
      }
      else
      {
        // show popup no efforts
        if(ReviewOldTestFlag == 1) //31.3.2018 repeatflag
          TEXT_SetText(LableReviewOldPOPup, "NO RECORDS");
        else
          TEXT_SetText(LableFVCPOPup, "NO RECORDS");
        return;        
      }  
    }
    if(currentefforttype == POST_EFFORT)
    {
      if((ReviewEffortNo <= PatDetailsStruct.PatientFVCPostEfforts )&&( ReviewEffortNo <= MAX_FVC_SVC_EFFORT))
      {  
        //Tempeffortno =  ReviewEffortNo;  //currenteffortno 9.4.2018        
        GetPatientData(oldpatientno,ReviewEffortNo);        //9.4.2018
        if(ReviewOldTestFlag == 1) //31.3.2018 repeatflag
          TEXT_SetText(LableReviewOldPOPup, " ");
        else
         TEXT_SetText(LableFVCPOPup, " ");        
      }
      else
      {
        // show popup no efforts       
        if(ReviewOldTestFlag == 1) //31.3.2018 repeatflag
          TEXT_SetText(LableReviewOldPOPup, "No RECORDS");
        else
          TEXT_SetText(LableFVCPOPup, "NO RECORDS");
        return;        
      }  
    }    
  } 
  else if(currenttesttype == MVV)
  {
    //getActValues(MVV); //13.2.2018
    if(currentefforttype == PRE_EFFORT)
    {
      if((ReviewEffortNo <= PatDetailsStruct.PatientMVVPreEfforts )&&( ReviewEffortNo <= MAX_FVC_SVC_EFFORT))
      { 
          //Tempeffortno =  ReviewEffortNo;  //currenteffortno 9.4.2018                
          GetPatientData(oldpatientno ,ReviewEffortNo);        //9.4.2018  
          if(ReviewOldTestFlag == 1) //31.3.2018 repeatflag
            TEXT_SetText(LableReviewOldPOPup, " ");
          else
            TEXT_SetText(LableMVVPOPup, " ");          
      }
      else
      {
        // show popup no efforts
        if(ReviewOldTestFlag == 1) //31.3.2018 repeatflag
          TEXT_SetText(LableReviewOldPOPup, "NO RECORDS ");
        else
          TEXT_SetText(LableMVVPOPup, "NO RECORDS");
        return;        
      }  
    }
    if(currentefforttype == POST_EFFORT)
    {
      if((ReviewEffortNo <= PatDetailsStruct.PatientMVVPostEfforts )&&( ReviewEffortNo <= MAX_FVC_SVC_EFFORT))
      {  
          //Tempeffortno =  ReviewEffortNo;  //currenteffortno 9.4.2018              
          GetPatientData(oldpatientno ,ReviewEffortNo);        //9.4.2018
          if(ReviewOldTestFlag == 1) //31.3.2018 repeatflag
            TEXT_SetText(LableReviewOldPOPup, " "); //13.3.2018 hardfault occurs at this point
          else
            TEXT_SetText(LableMVVPOPup, " ");          
      }
      else
      {
        // show popup no efforts
        if(ReviewOldTestFlag == 1) //31.3.2018 repeatflag
          TEXT_SetText(LableReviewOldPOPup, "NO RECORDS");
        else
          TEXT_SetText(LableMVVPOPup, "NO RECORDS");
        return;
      }  
    }        
  }
  else if(currenttesttype == SVC)
  {
    //getActValues(SVC); //13.2.2018
    if(currentefforttype == PRE_EFFORT)
    {
      if((ReviewEffortNo <= PatDetailsStruct.PatientSVCPreEfforts )&&( ReviewEffortNo <= MAX_FVC_SVC_EFFORT))
      {  
          //Tempeffortno =  ReviewEffortNo;  //currenteffortno 9.4.2018               
          GetPatientData(oldpatientno,ReviewEffortNo);        //9.4.2018  
          if(ReviewOldTestFlag == 1) //31.3.2018 repeatflag
            TEXT_SetText(LableReviewOldPOPup, " ");
          else
            TEXT_SetText(LableSVCPOPup, " ");          
      }
      else
      {
        // show popup no efforts
        if(ReviewOldTestFlag == 1) //31.3.2018 repeatflag
          TEXT_SetText(LableReviewOldPOPup, "NO RECORDS ");
        else
          TEXT_SetText(LableSVCPOPup, "NO RECORDS");
        return;        
      }  
    }
    if(currentefforttype == POST_EFFORT)
    {
      if((ReviewEffortNo <= PatDetailsStruct.PatientSVCPostEfforts )&&( ReviewEffortNo <= MAX_FVC_SVC_EFFORT))//18.4.2018
      {  
          //Tempeffortno =  ReviewEffortNo;  //currenteffortno 9.4.2018               
          GetPatientData(oldpatientno,ReviewEffortNo);        //9.4.2018
          if(ReviewOldTestFlag == 1) //31.3.2018 repeatflag
            TEXT_SetText(LableReviewOldPOPup, " ");
          else
            TEXT_SetText(LableSVCPOPup, " ");          
      }
      else
      {
        // show popup no efforts  
        if(ReviewOldTestFlag == 1) //31.3.2018 repeatflag
          TEXT_SetText(LableReviewOldPOPup, "NO RECORDS");
        else
          TEXT_SetText(LableSVCPOPup, "NO RECORDS");
        return;
      }  
    }        
  }    
  positions1[6] = 0;//get start point of data in array
  positions1[7] = 1000;//get end index of data in array
  if (currenttesttype == FVC)//for FVC test
  {
    getPeakData(testdataarray,positions1);//get max and min peak positions and values and their starting and ending positions in array
    _InitPoints(MAX_GRAPH_NUM_POINTS);//3.1.2018  to draw effort
    GrapghIndex = 0; //3.1.2018              
    plotFvsVF();//plot flow vs volume curve on LCD
  }
  if ((currenttesttype == SVC) || (currenttesttype == MVV))//if test is MVV or SVC
  {
    
    nextx = 0;
    getVolumeArray(0,1000,testdataarray,testvolumedataarray);//get volume vs time array
    _InitPoints(MAX_GRAPH_NUM_POINTS);//9.1.2018  to draw effort
    GrapghIndex = 0; //3.1.2018              
    for (i=0;i<=1000;i++)//plot volume vs time waveform on LCD
    {
      if ((i % 3) == 0)////17.4.2018  as per QC Suggestion %8
      {
        if (currenttesttype == SVC)
          //nexty = (int)(testvolumedataarray[i] / 50.0) + 88;
                nexty = (testvolumedataarray[i] /50.0) + 150+8; //2 //9.3.2018                        
        else
          //nexty = (WAVEHEIGHT - ((int)(testvolumedataarray[i] / 50.0) + 80)) + 8;
                nexty = (WAVEHEIGHT - ((testvolumedataarray[i] /50.0) + 150)) +8;  // 2 //9.3.2018                      
        if(nexty < 8) nexty = 8;
        //vdda(nextx,prevy,nexty);
        _aPoints[ GrapghIndex].x =  nextx;      //9.1.2018
        _aPoints[ GrapghIndex].y =  nexty;                        
        GrapghIndex++; //17.4.2018  
        if (GrapghIndex > GUI_COUNTOF(_aPoints)-1) GrapghIndex = GUI_COUNTOF(_aPoints)-1;  //17.4.2018      
        prevy = nexty;
        nextx++;
      }
    }
    if (currenttesttype == SVC)    
      GRAPH_SetUserDraw(hGraphSVCBroncho, SVCGraphUserDraw);       
    else if (currenttesttype == MVV)
      GRAPH_SetUserDraw(hGraphMVVBroncho, MVVGraphUserDraw);       
    GUI_Exec();                               

    if (currenttesttype == SVC)//for SVC test
      getPeakData(testvolumedataarray,positions1);//get max and min peak positions and values and their starting and ending positions in array
  }
  getActValues(currenttesttype);//31.3.2018 //get actual values of parameters
  if(ReviewOldTestFlag == 0 )//for DISPLAY_OLD_TEST_SCREEN there is diiferent function to display actual values
    dispActValues();//31.3.2018 //display calculated values  
}

float stringtodouble(void)//convert no represented by string to decimal representation (this was implemented because hex file was not being generated if atof was being used. Try to do it using atof
{
	unsigned char j = 0;
	unsigned char count = 0;
	int multfactor = 1;
	int intpart = 0;
	int fracpart = 0;
	int s = 1;
	int stores = 1;
	unsigned char i = 0;
	unsigned char numericval = 0;
	float d1,d;
	if (stringarr[j] == '-')//if no represented by string is negative (string starts with '-')
	{
		multfactor = -1;//multiplying factor is -1
		j++;
	}
	while ((stringarr[j] != '.') && (stringarr[j] != 0x00))//while decimal point or end of string is not encountered
	{
		j++;
		count++;//get no of digits
		s *= 10;//get multiplying factor to extract digits from string
	}
	s /= 10;//get multiplying factor to extract digits from string (because one order extra was calculted in above loop)
	j -= count;//get start position for extracting highest order digit from string
	for (i=0;i<count;i++)//for all digits counted in above logic
	{
		numericval = (unsigned char)(stringarr[j] - 0x30);//get numerical value from this ascii value
		intpart += ((int)numericval) * s;//integer part = previously calculated integer part + (current digit value * its decimal weight)
		s /= 10;//get next multiplying factor (decimal place)
		j++;
	}
	intpart *= multfactor;//multiply this integer part of number by multiplying factor (+1 or -1) as calculated before
	if (stringarr[j] != 0x00)//if this is a fractional no and not a whole no
	{
		j++;
		count = 0;//reinitialize all counts 
		s = 1;
		while (stringarr[j] != 0x00)//for all digits after decimal point to end of string
		{
			j++;
			count++;//get no of digits after decimal point
			s *= 10;//get multiplying factor for fractional part
		}
		stores = s;//store this multiplying factor (order of fractional part) temporarily
		s /= 10;//get multiplying factor to extract digits from string (because one order extra was calculted in above loop)
		j -= count;//get index of start position of fractional part in string
		for (i=0;i<count;i++)//for all digits in fractional part
		{
			numericval = (unsigned char)(stringarr[j] - 0x30);//get digit from ascii value of this character in string
			fracpart += ((int)numericval) * s;//fractional part = previously calculated fractional part + (current digit value * its decimal weight)
			s /= 10;//get next decimal weight
			j++;
		}
	}
//this fractional part calculated was in the form of integer
	d1 = (float)fracpart;
	d1 *= multfactor;//multiply the fractional part by multiplying factor (+1 or -1) as calculated before
	d1 = d1 / (float)stores;//get true fractional part 
	d = ((float)intpart) + d1;//add this fractional part to integer part to get the complete no.
//	tempptrdictionData = d;
        return d;
}

//---------------------------------------------------------------------------------------------
double getDouble(unsigned int offset)//get predicted value of parameter from constants in prediction table
{
//	unsigned char i = 0;
	double tdouble[3];
	double returnvalue = 0.0;
	double tempdouble = 0.0;

//	for (i=0;i<3;i++)//get 3 co-efficients for this parameter from prediction table
//		tdouble[i] = *(PREDICTIONTABLE + offset + i);

	// VD
		tdouble[0] = *(PredictionTableArray + offset );   //*(PREDICTIONTABLE + offset);
		tdouble[1] = *(PredictionTableArray + offset + 1);//*(PREDICTIONTABLE + offset + 1);
		tdouble[2] = *(PredictionTableArray + offset + 2);//*(PREDICTIONTABLE + offset + 2);
	
	if (SpiroSettingStruct.PredictionTableType == KNUDSON)//for Knudson table height is taken in inches
   		tempdouble = patheightdouble;
	else
		if (SpiroSettingStruct.PredictionTableType == INDIAN)//for Indian table height is taken in cm
			tempdouble = (double)PatDetailsStruct.PatientHeight;
		else
			if (SpiroSettingStruct.PredictionTableType == ECCS)//for ECCS table height is taken in m
				tempdouble = ((double)PatDetailsStruct.PatientHeight) / 100.0;
	returnvalue = tempdouble * tdouble[0] + (double)PatDetailsStruct.PatientAge * tdouble[1] + tdouble[2];//predicted value = height x height co-efficient + age x age co-efficient + constant
	returnvalue = returnvalue * ((double)SpiroSettingStruct.EthCorr / 100.0);//get corrected value using ethnic correction value
	return fabs(returnvalue);//return absolute value from that calculated (why absolute? I don't remember)
}
//---------------------------------------------------------------------------------------------

//---------------------------------------------------------------------------------------------
void getPredVals(void)//get predicted values from prediction table
{
	unsigned int startaddress = 0;
	unsigned char temppatage = 0;
	unsigned char slotno = 0;
	unsigned char i = 0,j = 0;
	for (i=0;i<32;i++)//clear array
		predictedvals[i] = 0.0;
	
	if (SpiroSettingStruct.PredictionTableType == ZEPTAL)//Zeptal table not entered yet
	{
		return;
	}
	temppatage = PatDetailsStruct.PatientAge;

	if (SpiroSettingStruct.PredictionTableType == INDIAN)//get offset in prediction table for given age and sex and table
	{
		if (PatDetailsStruct.PatientAge < 7)//for indian table, minimum age is 7 years
    		return;
    	if (PatDetailsStruct.PatientGender == FEMALE)//get offset in prediction table for given age and sex
			startaddress = startaddress + (96 * 2);
    	if (PatDetailsStruct.PatientAge > 29)
    		startaddress = startaddress + 96;
	}
	if (SpiroSettingStruct.PredictionTableType == ECCS)
	{
		if (PatDetailsStruct.PatientAge < 18)//for ECCS table type minimum age is 18 years
			return;
		if (PatDetailsStruct.PatientAge < 25)//for ages between 18 and 25, equations have to be obtained on the basis of a 25 year old individual
			PatDetailsStruct.PatientAge = 25;   //8.2.2018 check this condition is valid or not
		startaddress = startaddress + 96 * 4;//start of ECCS table
		if (PatDetailsStruct.PatientGender == FEMALE)
			startaddress = startaddress + 96;
	}
	if (SpiroSettingStruct.PredictionTableType == KNUDSON)//for Knudson table type minimum age is 6 years and height is 44 inches
	{
		if (PatDetailsStruct.PatientAge < 6)
			return;
		if (patheightdouble < 44)
			return;
		startaddress = startaddress +  96 * 6;		//start of Knudson table
		if (PatDetailsStruct.PatientGender == FEMALE)
		{
			startaddress = startaddress + 96 * 4;
			if (((PatDetailsStruct.PatientAge > 5) && (PatDetailsStruct.PatientAge < 11)) && ((patheightdouble > 41) && (patheightdouble < 59)))
				slotno = 1;
			if (((PatDetailsStruct.PatientAge > 10) && (PatDetailsStruct.PatientAge < 18)) && ((patheightdouble > 51) && (patheightdouble < 73)))
				slotno = 2;
			if (((PatDetailsStruct.PatientAge > 17) && (PatDetailsStruct.PatientAge < 21)) && ((patheightdouble > 51) && (patheightdouble < 73)))
				slotno = 3;
			if (((PatDetailsStruct.PatientAge > 20) && (PatDetailsStruct.PatientAge < 121)) && ((patheightdouble > 57) && (patheightdouble < 72)))
				slotno = 4;
		}
		if (PatDetailsStruct.PatientGender == MALE)
		{
			if (((PatDetailsStruct.PatientAge > 5) && (PatDetailsStruct.PatientAge < 12)) && ((patheightdouble > 43) && (patheightdouble < 62)))
				slotno = 1;
			if (((PatDetailsStruct.PatientAge > 11) && (PatDetailsStruct.PatientAge < 18)) && ((patheightdouble > 54) && (patheightdouble < 77)))
				slotno = 2;
			if (((PatDetailsStruct.PatientAge > 17) && (PatDetailsStruct.PatientAge < 26)) && ((patheightdouble > 54) && (patheightdouble < 77)))
				slotno = 3;
			if (((PatDetailsStruct.PatientAge > 25) && (PatDetailsStruct.PatientAge < 121)) && ((patheightdouble > 61) && (patheightdouble < 78)))
				slotno = 4;
		}
		if (slotno == 0)//if for this combination of age and height and sex no equation is available get out
			return;
		startaddress = startaddress + (96 * (slotno - 1));
	}
  	for (i=0, j =0;i<32;i++, j+=3)//get 32 values
		predictedvals[i] = getDouble(startaddress+(j)); //VD
   		//predictedvals[i] = getDouble(startaddress+(3*i));
   	if (predictedvals[0] > 0)
   	{
//   		predictedvals[13] = (predictedvals[1] / predictedvals[0]) * 100.0;
//  		predictedvals[12] = (predictedvals[10] / predictedvals[0]) * 100.0;
//   		predictedvals[14] = (predictedvals[11] / predictedvals[0]) * 100.0;
   		predictedvals[13] = getPercent(predictedvals[1] , predictedvals[0]);
   		predictedvals[12] = getPercent(predictedvals[10] , predictedvals[0]);
   		predictedvals[14] = getPercent(predictedvals[11] , predictedvals[0]);

   	}
	PatDetailsStruct.PatientAge = temppatage;
}

void StorePredictionTable(void)
{
  unsigned char x;
  unsigned int startingoffset,i,j;
  unsigned int k;
  float temp;
  float temp1;
  float tempintfloat;
  unsigned char PredictionWriteBuffer[36]; 
  int tempint =0;
  
  // erase block
  NOR_ReturnToReadMode();
  NOR_EraseBlock(NOR_PREDICTION_TABLE_ADDRESS); //8.2.2018
  
  EffortPredictionwriteAddress = NOR_PREDICTION_TABLE_ADDRESS;
  for (i=0;i<168;i++)//168 * 8 = 1344 for all bytes in prediction table
  {
    for (j=0;j<8;j++)
    {
      k = j * 4;
      temp = *(PredictionTableArray + (8 * i + j));//get next value in prediction table from RAM

      PredictionWriteBuffer[k] = ((unsigned char*)&temp)[0]; 
      k++;
      PredictionWriteBuffer[k] = ((unsigned char*)&temp)[1]; 
      k++;
      PredictionWriteBuffer[k] = ((unsigned char*)&temp)[2]; 
      k++;
      PredictionWriteBuffer[k] = ((unsigned char*)&temp)[3]; 
      asm("nop");      
    }
      if(NOR_GetStatus(10) == NOR_SUCCESS)//23.1.2018
      {
        asm("nop");
      }       
      writeSPIROData(PredictionWriteBuffer,EffortPredictionwriteAddress,32);              
      EffortPredictionwriteAddress += 32;//sizeof(int) ;//2    
  }
}  

void ReadPredictionTable(void)
{
  unsigned char PredictionReadBuffer[36];
  int i=0,j=0,k=0;
  int tint=0;
  float tempdouble = 0.0f;
  union 
  {
        char c[4];
        float f;
  } u;
   
  EffortPredictionReadAddress = NOR_PREDICTION_TABLE_ADDRESS;
  for (i=0;i<168;i++)
  {        
    readSPIROData(PredictionReadBuffer,EffortPredictionReadAddress,32);   
    EffortPredictionReadAddress +=32;   
    for (j=0;j<8;j++)
    {
       u.c[0] = PredictionReadBuffer[4*j+0];
       u.c[1] = PredictionReadBuffer[4*j+1];
       u.c[2] = PredictionReadBuffer[4*j+2];
       u.c[3] = PredictionReadBuffer[4*j+3];
       tempdouble = u.f;
        *(PredictionTableArray + k ) = tempdouble;
        k++;        
    }
 
  }
}        


void StoreCalibrationTable(void)
{
  unsigned char x;
  unsigned int startingoffset,i,j;
  unsigned int k;
  float temp;
  float temp1;
  float tempintfloat;
  unsigned char CalibrationWriteBuffer[36]; 
  int tempint =0;

  // erase block
  NOR_ReturnToReadMode();
  NOR_EraseBlock(NOR_CALIBRATION_TABLE_ADDRESS); //8.2.2018
  
  EffortCalibrationwriteAddress = NOR_CALIBRATION_TABLE_ADDRESS;
  for (i=0;i<512;i++)//512 * 8 = 4096 for all bytes in prediction table
  {
    for (j=0;j<8;j++)
    {
      k = j * 4;
      temp = *(CalibrationTableArray + (8 * i + j));//get next value in prediction table from RAM
      CalibrationWriteBuffer[k] = ((unsigned char*)&temp)[0]; 
      k++;
      CalibrationWriteBuffer[k] = ((unsigned char*)&temp)[1]; 
      k++;
      CalibrationWriteBuffer[k] = ((unsigned char*)&temp)[2]; 
      k++;
      CalibrationWriteBuffer[k] = ((unsigned char*)&temp)[3]; 
      asm("nop");      
    }
    if(NOR_GetStatus(10) == NOR_SUCCESS)//23.1.2018
    {
      asm("nop");
    }       
    writeSPIROData(CalibrationWriteBuffer,EffortCalibrationwriteAddress,32);              
    EffortCalibrationwriteAddress += 32;//sizeof(int) ;//2    
  }
}  

void ReadCalibrationTable(void)
{
  unsigned char CalibrationReadBuffer[36];
  int i=0,j=0,k=0;
  int tint=0;
  float tempdouble = 0.0f;
  union 
  {
        char c[4];
        float f;
  } u;
   
  EffortCalibrationReadAddress = NOR_CALIBRATION_TABLE_ADDRESS;
  for (i=0;i<512;i++)
  {        
    readSPIROData(CalibrationReadBuffer,EffortCalibrationReadAddress,32);   
    EffortCalibrationReadAddress +=32;   
    for (j=0;j<8;j++)
    {
//        tint = (((int)(PredictionReadBuffer[4*j+0])) << 8) | ((int)(PredictionReadBuffer[4*j+1]));//get integer part
//        tempdouble = (double)tint;
//        tint = (((int)(PredictionReadBuffer[4*j+2])) << 8) | ((int)(PredictionReadBuffer[4*j+3]));//get fractional part
//        tempdouble = tempdouble + ((double)tint) / 10000.0;//get value
        //*(PREDICTIONTABLE + ((8 * (i - 256)) + j)) = tempdouble;//store this in RAM
        //k = ((8 * (i - 256)) + j);
        //memcpy((unsigned char *)&tempdouble,(unsigned char *)PredictionReadBuffer[4*j+0],sizeof(float));
       u.c[0] = CalibrationReadBuffer[4*j+0];
       u.c[1] = CalibrationReadBuffer[4*j+1];
       u.c[2] = CalibrationReadBuffer[4*j+2];
       u.c[3] = CalibrationReadBuffer[4*j+3];
       tempdouble = u.f;
        *(CalibrationTableArray + k ) = tempdouble;
        k++;        
    }
 
  }
}

void ErasePatientData(void)
{
  short i =0;
  ErasingPatientDataFlag = 1;   //2.5.2018 for avoiding repeat erase
  for (i=1;i<= TOTAL_PATIENTS ;i++)//18.5.2018  // MAXPATIENTS
  eraseSPIRODataBlock(i);
  currentid = 1;
  SpiroSettingStruct.LastPatId = 0; //9.2.2018  check it should be 0 or 1
  lastid  = SpiroSettingStruct.LastPatId;
  SpiroSettingStruct.TotalReviewRecord =0;//5.4.2018 
  overlapflag = 0;
  updateSetting = 1;
  if (SpiroSettingStruct.UsageType == CLINICAL)
    MAXPATIENTS = TOTAL_PATIENTS_CLINICAL; //25
  else
  {
    currentefforttype = PRE_EFFORT;//27.4.2018    
    MAXPATIENTS = TOTAL_PATIENTS;
  } 
 
  memset(&PatDetailsStruct,0x00,sizeof(PatDetailsStruct));//27.4.2018
  memset(&ReviewOldPatDetailsStruct,0x00,sizeof(ReviewOldPatDetailsStruct));//18.5.2018
  besteffort  = 0.0; //2.5.2018 after changing operational mode reset best effort related flag
  currentbest = 0.0;
  besteffortno = 0;
  currentbestflag = 0; 
  ErasingPatientDataFlag = 0;   //2.5.2018 for avoiding repeat erase  
}  

void PopulateData(unsigned char pat_id, unsigned char c_testtype, unsigned char printreport_flg)
{
  unsigned char bankno;
  unsigned int i,startaddress;

  GetPatientData(pat_id,1);//currenteffortno);   //10.4.2018  for reportptinting currenteffortno is always 1
//  memcpy(testdataarray,PreviewDataBuffer ,1000);//7.3.2018
  positions1[6] = 0;//get start position of data in array
  positions1[7] = 1000;//get end position of data in array
  if (novolumeflag == 0)//if actual values have to be calculated for this test
  {
    if ((c_testtype == MVV) || (c_testtype == SVC))//for MVV or SVC test
    {
      //				tempvolume = 0.0;
      getVolumeArray(0,1000,testdataarray,testvolumedataarray);//get volume vs time array
      getPeakData(testvolumedataarray,positions1);//get positive and negative peaks and their corresponding starting and ending positions in volume vs time array
    }
    else//for FVC test
      getPeakData(testdataarray,positions1);//get positive and negative peaks and their corresponding starting and ending positions in flow vs time array
    getActValues(c_testtype);//get calculated values of parameters
    if (printreport_flg && printertype == DESKJET)	//for deskjet printing
    {
      if (currentefforttype == PRE_EFFORT)//only for effort pre
      {
        buildTable(4);		//build parameter table
        cleartable = 1;//set flag to indicate that table should not be cleared during next call to buildTable function
      }
      resetvalflag = 1;//set flag to indicate that actual vals array should not be cleared in next call to getActVals function
    }
  }
  else//if actual parameter values are not to be calculated for this test
  {
    if (c_testtype == FVC)//for FVC
      getPeakData(testdataarray,positions1);//get positive and negative peaks and their corresponding starting and ending positions in volume vs time array
  }
}

void printReport(void)//print complete report for this patient
{
  unsigned int startaddress;//,i;
  unsigned char j,k;//bankno;
 
  if(LowBattPrintBlockFlag == 1)
  {
    showStatusMsg("LOW BATT CONNECT CHARGER", 'E');//25.5.2018    
    return;
  }  
  //if(lastid ==0)       return; //18.5.2018 to avoid restart if no patient stored
  showStatusMsg("Printing ", 'I');//26.4.2018
  
  if(SpiroSettingStruct.PrinterType == 0 )
    printertype = THERMAL;
  else
    printertype = DESKJET;
  
  reportprinted = 1;//indicate that report is being printed
  GetPatientDetails(PrintId);//15.3.2018(ReviewOldSelectValue+1); //1.3.2018
  //getPredVals();//10.4.2018 prediction value updated in printeffort so not needed here 
  if (printertype == THERMAL)//in case of thermal printing print patient details and reset flags for thermal printing
  {
    djflg = 0;
    deskjetflag = 0;
    //dispPatDetails();  //2.4.2018

  }
  else
  {
    djflg = 1;
    deskjetflag = 1;//set this flag to indicate in printEffort function that printing is not to be started yet - only data is to be loaded and parameter values calculated
  }
  novolumeflag = 0;
  midreturnflag = 0;
  resetvalflag = 0;
  cleartable = 0;
  
  initECGPrint(); //3.3.2018
  build_HeaderTable();                      //16.3.2018
  PrintGuiDelay = 10000;
  while(PrintGuiDelay >0)
  {  
      handlePrnSerial();     //26.4.2018  to avoid printing in error condition       
      //HandleTouchScreen();   //26.4.2018  to abort print
      Process_key(Get_key());//26.4.2018  to abort print          
      GUI_Delay(1);      
      PrintGuiDelay--;
//      if(LowBattPrintBlockFlag == 1) //29.5.2018
//      {
//        showStatusMsg("LOW BATT CONNECT CHARGER", 'E');//25.5.2018    
//        printflag = 0; //25.5.5018 stop printing in low batt
//        endPrint(2);
//        PrintGuiDelay =0;              
//        return;
//      }      
      if((prn_error ==1 )&& (printertype == THERMAL))  //27.4.2018
      {
        endPrint(2);
        PrintGuiDelay =0;      
      }        
  }  
  //GUI_Delay(10000); //26.4.2018
  
//  if(currenttesttype == FVC )
//  {  
//      SendPrinterGraphType(currenttesttype);  
//      SendECGData((unsigned char*)start_cal_pulse, 3, 1);//17.3.2018 for build y axis
//      //  setPrinterParam();
//      //  GUI_Delay(500);
//      //  SendECGData((unsigned char*)start_data, 3, 1);  //26.3.2018  
//      GUI_Delay(1000);                                //26.3.2018      
//      endPrint(2);
//      //startPrint();   //3.3.2018 start waneform printing
//  }
  //startaddress = (currentid - 1) * 64 + 0x4022;//get address of info on which tests have been taken for this patient
  //1.3.2018 calculate patientdata address
  // startaddress = getEEPROMAddr(currentid, 34);
 // ReadE2prom1(startaddress,1,6,1);
  
  for (k=0;k<2;k++)//repeat the following twice: once for getting the actual values and once again for getting only waveforms
  {
    for (j=0;j<6;j+=2)//for all 3 types of tests
    {
      if(reportprinted == 0)//27.4.2018
      {
        resetvalflag = 0;//clear flags
        cleartable = 0;
        reportprinted = 0;
        printflag = 0;
        midreturnflag = 0;
        novolumeflag = 0;
        djflg = 0;
        deskjetflag = 0;
        clearStatusMsg();//26.4.2018 to clear printing message      
        endPrint(2);
        return; 
      } 
      if (printertype == DESKJET) //9.3.2018 by santosh
      {    
        currenttesttype = 0;
        currentefforttype = 3;
      }
      switch(j)
      {
      case 0:
            currenttesttype = FVC;
            if(PatDetailsStruct.PatientFVCPostEfforts >=1)
              currentefforttype = POST_EFFORT; 
            else
              currentefforttype = PRE_EFFORT;       
      break;  
      case 1:
            currenttesttype = FVC;
            currentefforttype = POST_EFFORT;       
      break;  
      case 2:
            currenttesttype = MVV;
            if(PatDetailsStruct.PatientMVVPostEfforts >=1)
              currentefforttype = POST_EFFORT; 
            else            
              currentefforttype = PRE_EFFORT;       
      break;  
      case 3:
            currenttesttype = MVV;
            currentefforttype = POST_EFFORT;       
      break;  
      case 4:
            currenttesttype = SVC;
            if(PatDetailsStruct.PatientSVCPostEfforts >=1)
              currentefforttype = POST_EFFORT; 
            else                        
              currentefforttype = PRE_EFFORT;       
      break;  
      case 5:
            currenttesttype = SVC;
            currentefforttype = POST_EFFORT;       
      break;        
        
      }  
      //add conditions for pre post status
      //if ((rdWrArr[j] == 1) || (rdWrArr[j+1] == 1))//1.3.2018//if pre or pre and post effort for this test is taken
      if(( j== 0 && ((PatDetailsStruct.PatientFVCPostEfforts >= 1) ||  (PatDetailsStruct.PatientFVCPreEfforts >= 1)))   //18.4.2018
          ||( j== 2 && ((PatDetailsStruct.PatientMVVPostEfforts >= 1)|| (PatDetailsStruct.PatientMVVPreEfforts >= 1)))
          ||( j== 4 && ((PatDetailsStruct.PatientSVCPostEfforts >= 1) || (PatDetailsStruct.PatientSVCPreEfforts >= 1)))  
         )
      {
        if(SpiroSettingStruct.UsageType == OCCUPATIONAL)//9.3.2018 by santosh
        {
            currenttesttype = FVC;
            currentefforttype = PRE_EFFORT; 
        }  
        //currenttesttype = FVC;//9.3.2018//(j / 2) + 1;//get test and effort type
        //if (rdWrArr[j] == 1) //1.3.2018 add condition for selection of currentefforttype
        //  currentefforttype = POST_EFFORT;
        //else
        //  currentefforttype = PRE_EFFORT; //9.3.2018
        PopulateData(PrintId, currenttesttype, 1);//ReviewOldSelectValue+1 //15.3.2018 
        if ((printertype == DESKJET) && ((currenttesttype == MVV) && (k == 1)))//for deskjet printing, if MVV test data has been loaded for printing set flag to indicate that testdataarrray is now in use and hence SVC test data should now be loaded in another array
          midreturnflag = 1;
        printEffort(currenttesttype,1);//9.4.2018 for printreport effort no is always 1 //for deskjet printing : during first run in outer loop, get actual values for pre and post efforts for this test and build table
        //during second run in outer loop, print header,table and FVC test or MVV and SVC tests
        //for thermal printing: print test and table for this test
        if (printertype == THERMAL)//for thermal printing advance paper to space out different tests
        {
          //paperFeed(2);
          //paperFeed(2);
          //break;//2.4.2018
        }
        
      }
      else//if no pre effort for this test has been taken
      {
        if (k == 1)//if this is the second run for printing on deskjet printer
        {
          if (j == 2)//if no pre MVV test was taken 
            midreturnflag = 1;//set flag to indicate that SVC test data should be loaded in testvolumedataarray for use in deskjet printing function
          if ((j == 0) || (j == 4))//if pre FVC or pre SVC was not taken
            printEffort(currenttesttype,1);//9.4.2018 for printreport effort no is always 1//printEffort to see if post efforts were taken - this was done before POST test was disabled if pre test is taken - not changed, but of not much use any more
        }
      }

        
    }
    deskjetflag = 0;//clear flag to allow printing from printEffort function
    novolumeflag = 1;//set this flag to indicate that now actual values are not to be calculated from test data values, only test data is to be loaded
    if (printertype == THERMAL)//only one run in this loop was required for thermal printing hence exit
    {
      k = 2;
      break;//8.3.2018
    }
    if(reportprinted == 0)//27.4.2018
    {
        resetvalflag = 0;//clear flags
        cleartable = 0;
        reportprinted = 0;
        printflag = 0;
        midreturnflag = 0;
        novolumeflag = 0;
        djflg = 0;
        deskjetflag = 0;
        clearStatusMsg();//26.4.2018 to clear printing message      
        endPrint(2);        
        return; 
    }  
  }
  
  resetvalflag = 0;//clear flags
  cleartable = 0;
  reportprinted = 0;
  printflag = 0;
  midreturnflag = 0;
  novolumeflag = 0;
  djflg = 0;
  deskjetflag = 0;
  clearStatusMsg();//26.4.2018 to clear printing message
}

//---------------------------------------------------------------------------------------------
void printEffort(unsigned char highlightoption,unsigned char PrintEffortNo)//print effort selected
{
  unsigned char bankno;
  unsigned int startaddress;
  unsigned int i;
  
  if(LowBattPrintBlockFlag == 1)
  {
    showStatusMsg("LOW BATT CONNECT CHARGER", 'E');//25.5.2018    
    return;
  }  
  if(ReviewOldTestFlag == 0)     //19.5.2018
  {
     if(SpiroSettingStruct.UsageType == CLINICAL)//clinical
     {
       if(currenttesttype == FVC)
       {
         if(currentefforttype == PRE_EFFORT && PatDetailsStruct.PatientFVCPreEfforts == 0)
         {
           TEXT_SetText(LableFVCPOPup, "NO RECORDS");
           return;
         }  
         if(currentefforttype == POST_EFFORT && PatDetailsStruct.PatientFVCPostEfforts == 0)
         {
           TEXT_SetText(LableFVCPOPup, "NO RECORDS");
           return;         
         }  
       }
       if(currenttesttype == MVV)
       {
         if(currentefforttype == PRE_EFFORT && PatDetailsStruct.PatientMVVPreEfforts == 0)
         {
           TEXT_SetText(LableMVVPOPup, "NO RECORDS");           
           return;
         }  
         if(currentefforttype == POST_EFFORT && PatDetailsStruct.PatientMVVPostEfforts == 0)
         {
           TEXT_SetText(LableMVVPOPup, "NO RECORDS");           
           return;                  
         }
       }
       if(currenttesttype == SVC)
       {
         if(currentefforttype == PRE_EFFORT && PatDetailsStruct.PatientSVCPreEfforts == 0)
         {
           TEXT_SetText(LableSVCPOPup, "NO RECORDS");           
           return;
         }  
         if(currentefforttype == POST_EFFORT && PatDetailsStruct.PatientSVCPostEfforts == 0)
         {
           TEXT_SetText(LableSVCPOPup, "NO RECORDS");           
           return;                  
         }  
         
       }       
     }
  }  
  
  if (printerrorflag )//if there is a printer error and report is not being printed
    return;
  
  showStatusMsg("Printing ", 'I');//26.4.2018
  if(SpiroSettingStruct.InterPretation == 1 ) //yes
    interpretflag = YES;  
  else
    interpretflag = NO;    
  printflag = 1;
  
  pre_post_fvc = 0;
  pre_fvc = 0;
  pre_post_vtc = 0;
  pre_vtc = 0;
  if (midreturnflag == 0)//in case of printing report, if MVV test data may be loaded previously, flags for the same should not be reset
  {
    pre_mvv = 0;
    pre_post_mvv = 0;
  }
  pre_svc = 0;
  pre_post_svc = 0;
  flowvarflg = 0;
  initflowvar = 0;
  voltimeflg = 0;
  initvoltime = 0;
  
  getPredVals();//get predicted values of parameters
  
  
  if (currentefforttype == POST_EFFORT)//this part is similar to print report function (for post effort)
  {
    GetPatientPreTestData(PrintId,PrintEffortNo);//15.3.2018 (ReviewOldSelectValue+1);    //12.3.2018 read prev test data
      //read data
    positions2[6] = 0;//start position of test
    positions2[7] = 1000;//end of test
    if (novolumeflag == 0)//if actual values have to be calculated
    {
      if ((currenttesttype == MVV) || (currenttesttype == SVC))
      {
        //				tempvolume = 0.0;
        getVolumeArray(0,1000,testdataarraypre,testvolumedataarraypre);
        getPeakData(testvolumedataarraypre,positions2);
      }
      else
        getPeakData(testdataarraypre,positions2);
      getActValues(currenttesttype);
      if ((printertype == DESKJET) && (reportprinted))//changed on 14/02/2002
      {
        buildTable(4);
        cleartable = 1;
      }
      resetvalflag = 1;//changed on 14/02/2002
    }
    else
    {
      if (currenttesttype == FVC)
        getPeakData(testdataarraypre,positions2);
    }
  } // currentefforttype == POST
  
  
  if (deskjetflag)//if only table was to be loaded 
  {
    printflag = 0;
    return;
  }
  
  
  if (currenttesttype == FVC)
  {
    if ((reportprinted) && (printertype == DESKJET));//if printing is to be done on thermal printer
    else
    {
      if (printgraph == YES)//if graph is to be printed
      {
        flowvarflg  = 1;
        initflowvar = 1;
      }
    }
    if (currentefforttype == PRE_EFFORT)
    {
      pre_fvc = 1;
      pre_vtc = 1;
    }
    else
    {
      pre_post_fvc = 1;
      pre_post_vtc = 1;
    }
  }
  
  if (currenttesttype == SVC)
  {
    if (currentefforttype == PRE_EFFORT)
      pre_svc = 1;
    else
      pre_post_svc = 1;
  }
  
  if (currenttesttype == MVV)
  {
    if (currentefforttype == PRE_EFFORT)
      pre_mvv = 1;
    else
      pre_post_mvv = 1;
    if (midreturnflag)//if SVC data is still to be loaded for deskjet printing return
      return;
  }
  
  if ((reportprinted) && (printertype == DESKJET))
  {
    if (midreturnflag == 0)//if FVC is to be printed along with header and table
    {
      resetvoltime();//reset volume time curve data and axis
      if (pre_vtc || pre_post_vtc)//volume time curve is to be plotted
        voltimeflg = 1;
      else//flow volume curve is to be plotted
        voltimeflg = 0;
      axisflg = 1;//set variable values for printing test
      fvc_org = 150;
      fvc_off = 12;
      fvc_row = 7;
      vollimit = 7;
      vol_xoff = 0;
      offset = 0;
      label_cnt = 0;
      bold3 = 0;
      buildTable(4);//build table
      dispPatDetails();//build patient details for header
    }
    else//if first part of deskjet printing is over
    {
      midreturnflag = 0;
      cleartable = 0;
      buildTable(5);//build interpretation strings
    }
    //disableInt();
    //handleDJPrint();//1.3.2018//print report on deskjet printer
    
    //enableInt();	  //1.3.2018
    return;
  }
  
  if (reportprinted == 0)//if report is not to be printed or if report is to be printed but patient details have already been printed
  {
    if((prn_error ==0 )&& (printertype == THERMAL)&&(reportprinted == 1|| printflag == 1))//26.4.2018            
      dispPatDetails();//display patient details 
  }
  
  if (flowvarflg)//FVC
  {
    if (initflowvar)//start of FVC
    {
      initflowvar = 0;
      if (pre_fvc)
      {
        asm ("nop");//1.3.2018//*(PIC_ADD) = 0xc1;//control word for pre FVC
        //delay(0x400);//delay(0x200);
      }
      else 
        if(pre_post_fvc)
        {
          asm ("nop");//1.3.2018//*(PIC_ADD) = 0xc3;//control word for post FVC
          //delay(0x400);//	delay(0x200);
        }
      //delay(60);//delay(20);
      resetflowvol();//reset flow volume curve data

      if(currenttesttype == FVC )////2.4.2018 && (reportprinted==1))
      {  
          if((prn_error ==0 )&& (printertype == THERMAL)&&(reportprinted == 1|| printflag == 1))//26.4.2018        
          {  
            SendPrinterGraphType(currenttesttype);  
            SendECGData((unsigned char*)start_cal_pulse, 3, 1);//17.3.2018 for build y axis
            //  setPrinterParam();
            //  GUI_Delay(500);
            //  SendECGData((unsigned char*)start_data, 3, 1);  //26.3.2018  
            GUI_Delay(700);                                //26.3.2018      
            endPrint(2);
            startPrint();   //3.3.2018 start waneform printing
          }
      }      

      asm ("nop");//1.3.2018//*(PIC_ADD) = 0xe0;//send control word to print axis
      //delay(0x60);//delay(0x20);
      buildyaxis();//build axis data
    }
    while (flowvarflg && printflag)//26.4.2018 //&& (reportprinted==1) )//1.3.2018 check for end condition//while flow volume curve is to be printed 
    {
      //flowvolprint =1;//1.3.2018 for testing only
      if (flowvolprint)//if PIC is ready to accept data
      {
        flowvolprint = 0;
        plotflowvol();//plot next part of flow volume curve
      }
      handlePrnSerial();     //26.4.2018  to avoid printing in error condition 
      //HandleTouchScreen();   //26.4.2018  to abort print
      Process_key(Get_key());//26.4.2018  to abort print   
//      if(LowBattPrintBlockFlag == 1)//29.5.2018
//      {
//        showStatusMsg("LOW BATT CONNECT CHARGER", 'E');//25.5.2018    
//        printflag = 0; //25.5.5018 stop printing in low batt
//        endPrint(2);
//        flowvarflg =0;//25.5.2018       
//        //PrintGuiDelay =0;              
//        return;
//      }      
      if((prn_error ==1 )&& (printertype == THERMAL))  //15.3.2018
      {
        flowvarflg =0;
        endPrint(2);
      }          
    }

    if((prn_error ==0 && (printertype == THERMAL))&&( (reportprinted == 1)|| (printflag == 1 )))//26.4.2018
    { 
      paperFeed(2);//9.3.2018                 
    }      

    GUI_Delay(1000); //9.4.2018   
  }	
//    if(reportprinted ==0)//2.4.2018
//    {
//      endPrint(2);
//      return;
//    }
  
    if (printgraph == YES)//printing v vs t curve 
    {
      voltimeflg = 1;
      initvoltime = 1;

    }
    buildflg = 1;//flag to build table after printing this waveform
  
  if (voltimeflg)//if volume vs time curve is to be printed
  {
    if (initvoltime)
    {
      initvoltime = 0;
      
      resetvoltime();
      if(pre_vtc  || pre_post_vtc)    //9.3.2018 by santosh
        vollimit = 7;
      else
        vollimit = 11;                //9.3.2018 by santosh
      vol_xoff = 0;              
      asm ("nop");//1.3.2018//*(PIC_ADD) = 0xe0;//1.3.2018//control word for axis
      //delay(0x60);//	delay(0x20);
      buildyaxis();
      //GUI_Delay(50);              
      
    }
    if((prn_error ==0 )&& (printertype == THERMAL)&&(reportprinted == 1|| printflag == 1))//26.4.2018
    {
      if(pre_vtc  || pre_post_vtc)    //9.3.2018 by santosh
        SendPrinterGraphType(VOLTIMEGRAPH);      
      else
        SendPrinterGraphType(currenttesttype); //9.3.2018 by santosh
      SendECGData((unsigned char*)start_cal_pulse, 3, 1);//23.3.2018 for build y axis
      //startPrint();    
      GUI_Delay(700);                         //23.3.2018      
      endPrint(2);
      startPrint();//9.3.2018 for start voltime graph
    }
    while (voltimeflg && printflag)//26.4.2018//&& (reportprinted==1))//while v vs time waveform is not printed  completely
    {
      if (voltimeprint)//print next part of volume time curve if PIC is ready
      {
        voltimeprint = 0;	
        plotvoltime();
      }	
      handlePrnSerial();     //26.4.2018  to avoid printing in error condition       
      //HandleTouchScreen();   //26.4.2018  to abort print
      Process_key(Get_key());//26.4.2018  to abort print  
//      if(LowBattPrintBlockFlag == 1)   //29.5.2018
//      {
//        showStatusMsg("LOW BATT CONNECT CHARGER", 'E');//25.5.2018    
//        printflag = 0; //25.5.5018 stop printing in low batt
//        endPrint(2);
//        voltimeflg = 0;       
//        //PrintGuiDelay =0;              
//        return;
//      }            
      if((prn_error ==1 )&& (printertype == THERMAL))  //15.3.2018
      {
       voltimeflg =0;
       endPrint(2);
      }  
    }
//    if(reportprinted ==0)//2.4.2018
//    {
//      endPrint(2);
//      return;
//    }
    //advance paper for space between two tests
    if((prn_error ==0 && (printertype == THERMAL))&&( (reportprinted == 1)|| (printflag == 1 )))//26.4.2018
    { 
      paperFeed(2);//9.3.2018                 
    }      
    GUI_Delay(1000); //9.4.2018   
  }  
  
  if (buildflg && printflag)//26.4.2018//&& (reportprinted==1))//build table for this test
  {
    //getPredVals();//get predicted values of parameters    
    buildflg = 0;
    //GUI_Delay(1000);
    buildTable(currenttesttype);//print table
    PrintGuiDelay = 10000;
    while(PrintGuiDelay >0)
    {  
      handlePrnSerial();     //26.4.2018  to avoid printing in error condition       
      //HandleTouchScreen();   //26.4.2018  to abort print
      Process_key(Get_key());//26.4.2018  to abort print          
      GUI_Delay(1);      
      PrintGuiDelay--;
//      if(LowBattPrintBlockFlag == 1)//29.5.2018
//      {
//        showStatusMsg("LOW BATT CONNECT CHARGER", 'E');//25.5.2018    
//        printflag = 0; //25.5.5018 stop printing in low batt
//        endPrint(2);
//        PrintGuiDelay =0;              
//        return;
//      }            
      if((prn_error ==1 )&& (printertype == THERMAL))  //27.4.2018
      {
        endPrint(2);
        PrintGuiDelay =0;        
      }        
    }  
    //GUI_Delay(10000); //26.4.2018 
    //advance paper
    if((prn_error ==0 && (printertype == THERMAL))&&( (reportprinted == 1)|| (printflag == 1 )))//26.4.2018
    { 
      paperFeed(8);//9.3.2018                 
    }      
    //paperFeed(2);
    GUI_Delay(1000);    
  }

  //if(reportprinted ==0)//10.4.2018   by santosh for testing only
  printflag = 0;
  if(reportprinted == 0 ) //26.4.2018  to clear printing message
  clearStatusMsg();//26.4.2018
}

void paperFeed(unsigned char count)//send data to PIC for printing on thermal printer - data to be sent line by line
{
  unsigned char i=0;
  
  //1.3.2018
  // paper feed routine
  for(i=0;i< count;i++)
  {  
    SendECGData((unsigned char*)feed_data, 3, 1);
    GUI_Delay(100);
  }  

}

void dispPatDetails(void)
{
  
  GetPatientDetails(PrintId);
  //getPredVals(); //10.4.2018 prediction value updated in printeffort so not needed here 
  initECGPrint(); 
  build_HeaderTable();                      //2.4.2018
  PrintGuiDelay = 10000;
  while(PrintGuiDelay >0)
  {  
      handlePrnSerial();     //26.4.2018  to avoid printing in error condition       
      //HandleTouchScreen();   //26.4.2018  to abort print
      Process_key(Get_key());//26.4.2018  to abort print          
      GUI_Delay(1);      
      PrintGuiDelay--;
//      if(LowBattPrintBlockFlag == 1)//29.5.2018
//      {
//        showStatusMsg("LOW BATT CONNECT CHARGER", 'E');//25.5.2018    
//        printflag = 0; //25.5.5018 stop printing in low batt
//        endPrint(2);
//        PrintGuiDelay =0;              
//        return;
//      }            
      if((prn_error ==1 )&& (printertype == THERMAL))  //27.4.2018
      {
       endPrint(2);
       PrintGuiDelay = 0;        
      }        
  }  
  //GUI_Delay(10000); //26.4.2018
  
}

void SendbuildTable(unsigned char index)
{
  if((prn_error ==1 )&& (printertype == THERMAL))   return;  //15.3.2018
  
  if(index == FVC)
    build_FVCTable();
  else if(index == MVV)
    build_MVVTable();
  else if(index == SVC)
    build_SVCTable();    
  
}

//--------------------------------------------------------------------------------------------------------------------------
void buildTable(unsigned char index)//build parameter table for required test type - Amit
{
	int i, counter = 2;

	for (i=0;i<2430;i++)
	{
		if (cleartable == 0)//in case of deskjet printing,when header and table is being printed, string array should not be cleared once it is loaded with parameters of a test
			str_arr[i] = 32;
		if (i < 477)
			headerarray[i] = 32;
	}

	iflag = 0;
	if (pre_post_fvc || pre_post_svc || pre_post_mvv || djflg)//if post effort is to be printed or if report is to be printed on deskjet printer larger table size is required
	{
		maxcount = 62;
		if (pre_post_fvc || pre_post_svc || pre_post_mvv)//get table title (different if there are no post efforts)
			lstrcpy(str_arr + maxcount,string1); 
		else
			lstrcpy(str_arr + maxcount,string2);  
	}
	else//if only pre effort will be printed , then table width will be lesser
	{
		maxcount = 39;
		lstrcpy(str_arr + maxcount,string2);  
	}
	
	if ((index == 1) || (index >= 4))//FVC test or report printing on deskjet
	{
		for (i=0;i<18;i++)//get parameter names and units
			formParamTitle(fptarray[i],&counter);
		if (tempanal1 > 0)//analysis - this algorithm is printed in PoP software file - Niral
		{
			if ((interpretflag == YES) && ((djflg == 0) || (index == 1)))//index ==5
			{
				lstrcpy(str_arr + counter * maxcount,newinterpretstring[0]);		//counter = 20 here
				counter += 1;
	     		if (tempanal2 >= 80)
				{
     				interpretvalue = 1;
     				if (tempanal3 < 80)
     					interpretvalue = 2;
     				if ((predictedvals[18] > 0) && (tempanal4 < 50))
						interpretvalue = 2;
				}
     			else
     			{
     				if (tempanal3 >= 80)	
	     			{
    	 				interpretvalue = 3;
     					if (tempanal1 >= 80)
     						interpretvalue = 1;
     					if (tempanal1 < 80)
     						interpretvalue = 6;
	     				if (tempanal1 < 60)
    	 					interpretvalue = 7;
     					if (tempanal1 < 50)
     						interpretvalue = 8;
	     			}	
    	 			else
     				{
     					if (tempanal1 >= 80)
     					{
     						interpretvalue = 4;
		   					if (tempanal3 < 80)
     							interpretvalue = 9;
     						if (tempanal3 < 60)
     							interpretvalue = 10;
	     					if (tempanal3 < 40)
    	 						interpretvalue = 11;
	     				}
    	 				else
     						interpretvalue = 5;
     				}
	     		}
				lstrcpy(str_arr + counter * maxcount,newinterpretstring[interpretvalue]);		//counter = 21 here
				if (index == 1) //index ==5
				{
					iflag = 1;
					lstrcpy(headerarray + 371,str_arr + 20 * maxcount);
					lstrcpy(headerarray + 424,str_arr + 21 * maxcount);
				}
			}	
		}	
	}
	if ((index == 2) || (index == 4))//MVV or report on deskjet
	{
		formParamTitle(4,&counter);
		formParamTitle(5,&counter);
		formParamTitle(25,&counter);
	}
	if ((index == 3) || (index == 4))//SVC or report on deskjet
	{ 
		for (i=0;i<11;i++)
			formParamTitle(fptarray3[i],&counter);
		
		lstrcpy(str_arr + counter * maxcount,versionstr);		//counter = 21 here
	}
  
	for (i=0;i<2430;i++)//ensure valid values
	{
		if (*(str_arr + i) < 32)
			*(str_arr + i) = 32;
	}
    //memset(str_arr, 32, 2430);

    SendbuildTable(index);        
	if (djflg)
		return;
//	paperFeed(maxcount);//17.3.2018 add delay before paper feed and after SendbuildTable
}

void resetvoltime(void)
{
  unsigned int i;
  int temp;
  if (djflg == 0)
  {
    prev_data1 = VOLTIME_GRAPH_BASELINE;//1.3.2018 for 3 inch//192;
    cur_data1  = VOLTIME_GRAPH_BASELINE;//1.3.2018 for 3 inch//192;
    prev_data2 = VOLTIME_GRAPH_BASELINE;//1.3.2018 for 3 inch//192;
    cur_data2  = VOLTIME_GRAPH_BASELINE;//1.3.2018 for 3 inch//192;
    cur_data_1 = VOLTIME_GRAPH_BASELINE;//1.3.2018 for 3 inch//192;
    cur_data_2 = VOLTIME_GRAPH_BASELINE;//1.3.2018 for 3 inch//192;
  }
  else
  {
    cur_data_1 = 1200;
    cur_data_2 = 1200;
    cur_data_3 = 1200;
    cur_data_4 = 1200;
    trace1 = 1200;
    trace2 = 1200; 
    p_trace1 = 1200;
    p_trace2 = 1200;
    p_trace3 = 600;
    p_trace4 = 600;
    p_p_trace1 = 1200;
    p_p_trace2 = 1200;
    p_p_trace3 = 600;
    p_p_trace4 = 600;
  }
  vol1 = 0.0;
  vol2 = 0.0;
  vol3 = 0.0;
  vol4 = 0.0;
  
  dotflg = 0;
  if ((pre_svc) || (pre_post_svc))		//Pre SVC
  {
    if (djflg == 0)
    {
      maxstart = positions1[6];
      maxend = positions1[7];
      i1 = maxstart;
      maxsvcvol = 0.0;
      //for (i=positions1[6];i<positions1[7];i++)		//finding max SVC vol
      for (i=maxstart;i<maxend;i++)		//finding max SVC vol
      {
        //				vol1 = vol1 + (((double)testdataarray[i]) / 1000.0) / SAMPLING_RATE;		//actual volume
        vol1 +=  getVol(testdataarray, i, 1000, 0);
        if (vol1 > maxsvcvol)
          maxsvcvol = vol1;
      } 
    }
    else
    {
      maxstart2 = 0;
      maxend2 = 1000;
      i3 = maxstart2;
      temp = (int)maxsvcvol;
      p_p_trace3 = temp * 100 + p_p_trace3;
      p_trace3 = p_p_trace3;
      maxsvcvol = 0.0;
      for (i=0;i<1000;i++)		//finding max SVC vol
      { 
        //vol1 = vol1 + (((double)testvolumedataarray[i]) / 1000.0) / SAMPLING_RATE;		//actual volume
        vol1 += getVol(testvolumedataarray, i, 1000, 0);
        if (vol1 > maxsvcvol)
          maxsvcvol = vol1;
      } 
    }
    if (pre_post_svc)
    {
      if (djflg == 0)
      {
        maxstart1 = positions2[6];//start of flow vs time curve for svc
        maxend1 = positions2[7];//end of flow vs time curve for svc
        i2 = maxstart1;
        maxsvcvol1 = 0.0;
        for (i=maxstart1;i<maxend1;i++)		//finding max SVC vol
        {
          //vol3 = vol3 + (((double)testdataarraypre[i]) / 1000.0) / SAMPLING_RATE;		//actual volume
          vol3 += getVol(testdataarraypre, i, 1000, 0);
          if (vol3 > maxsvcvol1)
            maxsvcvol1 = vol3;
        } 
      }
      else
      {
        dotflg = 1;
        maxstart3 = 0;
        maxend3 = 1000;
        j3 = maxstart3;  
        maxsvcvol1 = 0.0;
        for (i=maxstart3;i<maxend3;i++)		//finding max SVC vol
        {
          //vol3 = vol3 + (((double)testvolumedataarraypre[i]) / 1000.0) / SAMPLING_RATE;//change data arrayactual volume
          vol3 += getVol(testvolumedataarraypre, i, 1000, 0);
          if (vol3 > maxsvcvol1)
            maxsvcvol1 = vol3;
        } 
      }
    }
  }
  vol1 = 0.0;
  vol2 = 0.0;
  vol3 = 0.0;
  vol4 = 0.0;
  
  vol5 = 0.0;
  vol6 = 0.0;
  vol7 = 0.0;
  vol8 = 0.0;
  
  if (pre_vtc == 1 || pre_post_vtc == 1)		//pre FVC V vs T curve or pre and post FVC V vs T curve
  {
    maxstart = positions1[0];//maxstart of flow vs time curve for fvc
    maxend = positions1[2];//maxend 
    i1 = maxstart;
    if (pre_post_vtc)		//pre and post FVC V vs T curve
    {
      maxstart1 = positions2[0];//minstart of flow vs time curve for fvc
      maxend1 = positions2[2];
      i2 = maxstart1;
      dotflg  = 1;
    }
  }
  
  if (pre_mvv == 1 || pre_post_mvv == 1)
  {
    maxstart = positions1[6];		//start of flow vs time curve  for premvv
    maxend = positions1[7];	//end  of flow vs time curve for premvv
    i1 = maxstart;
    if (pre_post_mvv)
    {
      maxstart1 = positions2[6];  //start of flow vs time curve  for postmvv
      maxend1 = positions2[7];  //end  of flow vs time curve for postmvv
      i2 = maxstart1;
      dotflg = 1;
    }
  }
  
  ccnt = 0;        //8.3.2018
  yaxiscnt = 0;
  xcolumncnt = 0;
  xscale = 0;
  xaxis_mark = 0;
  xmarkcnt = 0;
  bold =0 ;
  xaxis_mark = 0;  
  labelcnt = 0;
  labelcnt1 = 0 ;  
  yaxis_mark = 0;    
}

void resetflowvol(void)
{
  maxstart = positions1[0];//maxstart of flow vs time curve of pre fvc curve
  maxend = positions1[2]; //maxend 
  minstart = positions1[1];//minstart of flow vs timr curve
  minend = positions1[3];
  
  
  if (djflg == 0)
  {
    prev_data1 = FLOWVOLUME_GRAPH_BASELINE;//1.3.2018 for 3 inch//384;
    cur_data1  = FLOWVOLUME_GRAPH_BASELINE;//1.3.2018 for 3 inch//384;
    prev_data2 = FLOWVOLUME_GRAPH_BASELINE;//1.3.2018 for 3 inch//384;
    cur_data2  = FLOWVOLUME_GRAPH_BASELINE;//1.3.2018 for 3 inch//384;
    cur_data_1 = FLOWVOLUME_GRAPH_BASELINE;//1.3.2018 for 3 inch//384;
    cur_data_2 = FLOWVOLUME_GRAPH_BASELINE;//1.3.2018 for 3 inch//384;
    vol_cal = 9.09;
  }
  else
  {
    p_p_trace1 = 1424;
    p_trace1   = 1424;
    p_p_trace2 = 1424;
    p_trace2   = 1424;
    trace1     = 1424;
    trace2     = 1424; 
    vol_cal = 12.50;
  }
  
  vol1 = 0.0;
  vol2 = 0.0;
  vol3 = 0.0;
  vol4 = 0.0;
  dotflg = 0;
  if (pre_post_fvc)		//this effort is post FVC
  {
    pre_post_flag = 0;
    dotflg = 1;
    if (djflg == 0)
    {
      prev_data3 = FLOWVOLUME_GRAPH_BASELINE;//1.3.2018 for 3 inch//384;
      cur_data3  = FLOWVOLUME_GRAPH_BASELINE;//1.3.2018 for 3 inch//384;
      prev_data4 = FLOWVOLUME_GRAPH_BASELINE;//1.3.2018 for 3 inch//384;
      cur_data4  = FLOWVOLUME_GRAPH_BASELINE;//1.3.2018 for 3 inch//384;
      cur_data_3 = FLOWVOLUME_GRAPH_BASELINE;//1.3.2018 for 3 inch//384;
      cur_data_4 = FLOWVOLUME_GRAPH_BASELINE;//1.3.2018 for 3 inch//384;
    }
    else
    {
      p_p_trace3 = 1424;
      p_trace3   = 1424;
      p_p_trace4 = 1424;
      p_trace4   = 1424;
    }
    
    
    maxstart1 = positions2[0];//maxstart of flow vs time curve of post fvc curve
    maxend1 = positions2[2];//maxend 
    minstart1 = positions2[1];//minstart of flow vs timr curve
    minend1 = positions2[3];
    i2 = maxstart1;
    j2 = minend1;
    //vol3 = vol3 + (fabs((double)testdataarraypre[i2])) / SAMPLING_RATE;
    //vol4 = vol4 + (fabs((double)testdataarraypre[j2])) / SAMPLING_RATE;
    vol3 += getVol(testdataarraypre, i2, 1, 1);
    vol4 += getVol(testdataarraypre, j2, 1, 1);
  }
  i1 = maxstart;
  j1 = minend;
  volume = vol_cal;
  //vol1 = vol1 + (fabs((double)testdataarray[i1])) / SAMPLING_RATE;
  //vol2 = vol2 + (fabs((double)testdataarray[j1])) / SAMPLING_RATE;
  vol1 += getVol(testdataarray, i1, 1, 1);
  vol2 += getVol(testdataarray, j1, 1, 1);
  
  ccnt = 0;
  yaxiscnt = 0;
  xcolumncnt = 0;
  xscale = 0;
  xaxis_mark = 0;
  xmarkcnt = 0;
  bold =0 ;
  xaxis_mark = 0;  
  labelcnt = 0;
  labelcnt1 = 0 ;  
  yaxis_mark = 0;  

  //CurrDataCounter =0;                      //21.4.2018
  //memset(Curr2Buff,0x00,sizeof(Curr2Buff));
  //memset(Curr4Buff,0x00,sizeof(Curr4Buff));  
}  

void buildyaxis(void)
{
  
}  

void plotflowvol(void)
{
  double prn_factor = 0.018; ;//7.3.2018 //0.024;
  int prn_offset = FLOWVOLUME_GRAPH_BASELINE;//1.3.2018for 3 inch printer //384;for 4 inch printer

  if(djprintflg) 
  {	
    prn_factor = 0.025;
    prn_offset = 1424;
  }	
  
  if (i1 <= maxend)
  { 
    if(volume >= vol1)
    {
      i1++;
      vol1 += getVol(testdataarray, i1, 1, 1);
      cur_data1 = getFlow(testdataarray,  i1, prn_factor ,prn_offset);
      step_1 = 0;
    }
    else
    {
      plus_flag = 1;	     
      //cur_data1 = (int)(prn_factor * (*(testdataarray + i1))) + prn_offset;
      if(step_1 <= 0) //21.4.2018 <=0
      {
        step_1 = getSteps(volume, vol1, vol_cal) ; //(int)((vol1 - volume) / vol_cal) + 1;
        slope_1 = getSlope(testdataarray, i1, step_1, prn_factor, &stor_arr1, 1);
      }
      else
      {
        stor_arr1 += slope_1;
        cur_data1 = (int)( stor_arr1) + prn_offset;//21.4.2018 there is issue in negative slope need to find out
        step_1--;				  
      }

    } // vd	

  }
  else
  {
    plus_flag = 1;
    cur_data1 = prn_offset;
  }	
  
  if (j1 > minstart)
  {   
    
    if (volume >= vol2)
    {
      j1--;
      vol2 += getVol(testdataarray, j1, 1, 1);
      cur_data2 = getFlow(testdataarray,  j1, prn_factor ,prn_offset);
      step_2 = 0;
    }
    else
    {
      minus_flag = 1;      	    
      //cur_data2 = getFlow(testdataarray,  j1, prn_factor ,prn_offset);
      
      if(step_2 <= 0) //27.4.2018 <=0
      {
        step_2 = getSteps(volume, vol2, vol_cal); 
        slope_2 = getSlope(testdataarray, j1, step_2, prn_factor, &stor_arr2, -1);
      }
      else
      {
        stor_arr2 += slope_2;// * prn_factor;
        //int tempCurr2 = 0;                         //23.4.2018
        //tempCurr2 = (int)(stor_arr2) + prn_offset; //23.4.2018
        //if(tempCurr2 <= cur_data2 ) //23.4.2018 for avoiding garbage printing in inspiration waveform
        cur_data2 = (int)(stor_arr2) + prn_offset;//21.4.2018 there is issue in negative slope need to find out
        //if(step_2 > 0)//24.4.2018
        step_2--;				  
      }
    }
        //Curr1Buff[CurrDataCounter] = step_2; 
        //Curr2Buff[CurrDataCounter] = slope_2; 
        //Curr3Buff[CurrDataCounter] = (int)stor_arr2; 
        //Curr4Buff[CurrDataCounter] = cur_data2;   
        
    //23.4.2018
  } 
  else
  { 
    minus_flag = 1;	    
    cur_data2 = prn_offset;  //23.4.2018
  }	  
  
  if (pre_post_fvc)
  {
    if (i2 <= maxend1)
    { 
      
      if (volume >= vol3)
      {
        i2++;
        vol3 += getVol(testdataarraypre, i2, 1, 1);
        //cur_data3 = (int)(prn_factor * (*(testdataarraypre + i2))) + prn_offset;
        cur_data3 = getFlow(testdataarraypre,  i2, prn_factor ,prn_offset);
        step_3 = 0;
        
      }	
      else
      {
        plus_flag1 = 1;	     
        //				cur_data3 = (int)(prn_factor * (*(testdataarraypre + i2))) + prn_offset;
        //				cur_data1 = (int)(prn_factor * (*(testdataarray + i1))) + prn_offset;
        
        
        if(step_3 <= 0) //21.4.2018 <=0
        {
          
          step_3 = getSteps(volume, vol3, vol_cal) ; //(int)((vol1 - volume) / vol_cal) + 1;
          slope_3 = getSlope(testdataarraypre, i2, step_3, prn_factor, &stor_arr3, 1);
          
        }
        else
        {
          stor_arr3 += slope_3;
          cur_data3 = (int)( stor_arr3) + prn_offset;//21.4.2018 there is issue in negative slope need to find out
          step_3--;
          
        }
      }
       
    }
    else
    {
      plus_flag1 = 1;
      cur_data3 = prn_offset;
    }	
    
    if (j2 > minstart1)
    {   
      
      if (volume >= vol4)
      {
        j2--;
        //         		vol4 = vol4 + (fabs((double)testdataarraypre[j2])) / SAMPLING_RATE;
        vol4 += getVol(testdataarraypre, j2, 1, 1);
        cur_data4 = getFlow(testdataarraypre,  j2, prn_factor ,prn_offset);
        step_4 = 0;
      }
      else
      {
        minus_flag1 = 1;      	    
        //	cur_data4 = (int)(prn_factor * (testdataarraypre[j2])) + prn_offset;
        if(step_4 <= 0) //27.4.2018 <=0
        {
          step_4 = getSteps(volume, vol4, vol_cal) ; 
          slope_4 = getSlope(testdataarraypre, j2, step_4, prn_factor, &stor_arr4, -1);
        }
        else
        {
          stor_arr4 += slope_4;// * prn_factor;
          //int tempCurr4 = 0;                         //23.4.2018
          //tempCurr4 = (int)(stor_arr4) + prn_offset; //23.4.2018
          //if(tempCurr4 <= cur_data4 ) //23.4.2018 for avoiding garbage printing in inspiration waveform          
          cur_data4 = (int)( stor_arr4) + prn_offset;//21.4.2018 there is issue in negative slope need to find out
          //if(step_4 > 0)//24.4.2018          
          step_4--;				  
        }
      }
    } 
    else
    { 
      minus_flag1 = 1;	    
      cur_data4 = prn_offset;
    }
  }
  
  if (minus_flag1 == 1 && plus_flag1 == 1 && pre_post_fvc == 1)
    pre_post_flag = 1;
  else 
    if (pre_post_fvc == 0)
      pre_post_flag = 1;
  if (minus_flag == 1 && plus_flag == 1 && pre_post_flag == 1)
  {
    minus_flag =0;
    plus_flag = 0;  
    pre_post_flag = 0;
    cur_data_1 = cur_data1;
    cur_data_2 = cur_data2;
    if(djprintflg && pre_post_fvc)
    {
      cur_data_3 = cur_data3;
      cur_data_4 = cur_data4;
    }
    if(djprintflg==0)
    {
      //static short rampdata=0;
      memset(printdata,0x00,sizeof(printdata)/sizeof(int));
      //printdata[0] = rampdata +3000;//cur_data_1+3000;
      //printdata[1] = (rampdata/2) +3000;
      //printdata[2] = (rampdata/4) +3000;
      //printdata[3] = (rampdata/8) +3000;
      printdata[0] = cur_data_1 + 3000;      
      printdata[1] = 3000 + prn_offset;//26.4.2018 prn_oddset added to avoid flat line at zero position                         //prev_data1 + 3000;
      printdata[2] = cur_data_2 + 3000;
      printdata[3] = 3000 + prn_offset;//26.4.2018 prn_oddset added to avoid flat line at zero position                         //prev_data2 + 3000;
      //rampdata++;
      //if(rampdata >=100) rampdata =0;
      //*(PSP_PORT) = 0xf0;
      //delay(30);//delay(8);
      //printPre();//1.3.2018
      if (pre_post_fvc)
      {
        cur_data_3 = cur_data3;
        cur_data_4 = cur_data4;
        //printdata[4] = cur_data_3+3000;
        //printdata[5] = 3000;//prev_data3+3000;
        //printdata[6] = cur_data_4+3000;
        //printdata[7] = 3000;//prev_data4+3000;
        //printPre();
        printdata[1] = cur_data_3 + 3000;  //12.3.2018  prev
        printdata[3] = cur_data_4 + 3000;  //12.3.2018    
        prev_data3 = cur_data_3;
        prev_data4 = cur_data_4;
      }

//////////////////only for testing      
      //Curr2Buff[CurrDataCounter] = stor_arr2 ;
      //Curr4Buff[CurrDataCounter] = slope_2;    
      //CurrDataCounter++;
//////////////////////////////////////////////////      
      printPre();
      plotflowvol_thrd();      
    }
    else
      dj_flowprint = 1;
    
    //      	prev_prev_data1 = cur_data_1;
    //			prev_prev_data2 = cur_data_2;         
    prev_data1 = cur_data_1;
    prev_data2 = cur_data_2;     
    
    volume = volume + vol_cal;
  }       
  else
  {
    flowvolprint = 1;
    dj_flowprint = 0;
  }  
  
}

void plotvoltime(void)
{
  int mul_factor = 60, add_factor = VOLTIME_GRAPH_BASELINE;//12.4.2018 mul_factor = 48 //31.3.2018 72,196     //9.3.2018 mul_factor = 96//1.3.2018 for 3 inch printer//192;
  if(djprintflg)
  {
    mul_factor = 100;
    add_factor = 1200;
  }
  
  if (i1 <= maxend)	   
  {
    //vol1 = vol1 + (((double)testdataarray[i1]) / 1000.0) / SPIRO_SAMPLING_RATE;		//volume
    vol1 += getVol(testdataarray, i1, 1000, 0);
    if (((pre_svc) || (pre_post_svc)) && (djprintflg==0))
      vol2 = maxsvcvol - vol1;
    else
      vol2 = vol1 ; //18.4.2018 to plot MVV graph above x axis   by Santosh
    vol2 *= mul_factor;
    cur_data1 = (int)vol2 + add_factor;
    i1++;
  } 
  else
  {	
    cur_data1 = add_factor;
    //p_prev_data1 = 
    prev_data1 = add_factor;
    
    p_p_trace1 = add_factor;
    p_trace1   = add_factor;
  }
  
  cur_data_1 = cur_data1;
  
  if(pre_post_mvv == 1 || pre_post_vtc == 1 || (pre_post_svc == 1 && djprintflg==0))			
  {
    if (i2 <= maxend1)
    {
      //vol3 = vol3 + (((double)testdataarraypre[i2]) / 1000.0) / SPIRO_SAMPLING_RATE;
      vol3 += getVol(testdataarraypre, i2, 1000, 0);
      if (pre_post_svc == 1 && djprintflg == 0)
        vol4 = maxsvcvol1 - vol3;
      else
        vol4 = vol3 ; //18.4.2018 to plot MVV graph above x axis   by Santosh
      
      vol4 *= mul_factor;
      cur_data2 = (int)vol4 + add_factor;
      
      i2++;
    }
    else
    {
      cur_data2 = add_factor;
      //p_prev_data2 = 
      prev_data2 = add_factor;
      
      p_p_trace2 = add_factor;
      p_trace2   = add_factor;
    }
  }
  else
  {
    cur_data2 = add_factor;
    //p_prev_data2 = 
    prev_data2 = add_factor;
    
    p_p_trace2 = add_factor;
    p_trace2   = add_factor;
    
  }
  
  cur_data_2 = cur_data2;
  if(djprintflg == 0)
  {
    memset(printdata,0x00,sizeof(printdata)/sizeof(int));
      
    printdata[0] = cur_data_1 + 3000;
    if(pre_post_mvv == 1 || pre_post_vtc == 1 || (pre_post_svc == 1 && djprintflg==0))			
      printdata[1] = cur_data_2 + 3000;
    else
      printdata[1] =  3000 +add_factor;      
    printdata[2] = 3000 + add_factor;
    printdata[3] = 3000 + add_factor;

    printPre();
    plotvoltime_thrd();
  }
  
  //    p_prev_data1 = prev_data1;
  //    p_prev_data2 = prev_data2;
  
  prev_data1 = cur_data_1;
  prev_data2 = cur_data_2;
  
}

void printPre(void)
{
  unsigned char i,j=0;
  unsigned char PrintDataBuffer[20];
  PrintDataBuffer[0] = 0xF1;
  if(pre_post_fvc)
     PrintDataBuffer[1] = 8;//16;  
  else
     PrintDataBuffer[1] = 8;  
  for (i=0,j=2;i< (PrintDataBuffer[1]/2);i++)//1.3.2018 if prepost then size is 8 otherwise 4
  {
    PrintDataBuffer[j] = (unsigned char)((printdata[i] >> 7) & 0x7f);
    j++;
    PrintDataBuffer[j] = (unsigned char)(printdata[i]) & 0x7f;
    j++;
  }
  SendECGData(PrintDataBuffer,PrintDataBuffer[1]+2,1);//1.3.2018 enable later 
}  

double getVol(int *array, int offset, int div_factor, unsigned char abs_flag)
{
  int t_val = *(array + offset) ;
  if(abs_flag)
    t_val = abs(t_val);
  
  return((double)(t_val)  / (div_factor * SPIRO_SAMPLING_RATE));//1.3.2018  for testing only //div_factor * SPIRO_SAMPLING_RATE
}

int getFlow(int* array, int offset, double prn_factor, int prn_offset)
{
  //return( (int)(100 * ((*(array + offset)))) + 0);
  return( (int)(prn_factor * (*(array + offset))) + prn_offset); //23.4.2018 
}

int getSteps(double volume, double vol, double vol_cal)
{
  return((int)((vol - volume) / vol_cal) + 1);
}

double getSlope(int *array, int offset, int steps, double prn_factor, double *init_value, signed char incr) //23.4.2018
{
    *init_value = prn_factor * (*(array + offset));//21.4.2018 original
    return ((prn_factor * ((*(array + offset + incr)) - (*(array + offset)))) / steps);
   
//  if(steps == 0)  //21.4.2018  by santosh
//  { 
//    //*init_value = 0.0;
//    *init_value = prn_factor * (*(array + offset));       
//    return (1.0);
//  }  
//  else
//  {  
//    *init_value = prn_factor * (*(array + offset));    
//    return((prn_factor * (*(array + offset + incr) - *(array + offset))) / steps);
//  }  
  
  //return(0.0);					
}

void plotflowvol_thrd(void)
{
  //unsigned char i,put,put1;
  flowvolthrdcnt ++;
  if(initflg)
  { 
    initflg = 0;
    xaxis_mark = 0;
    yaxis_mark = 0;
    
  }
  if(yaxis_mark<40)  // ==== yaxis mark  =====
  {
    //    for(i = 0;i<16;i++ ) 
    //      *(prn_data + 6 + (i*6))|= 0x80;//-*8changedto6
    yaxis_mark++;      
  }
  else if(labelcnt<276) // ==== label cnt=====
  {
    //    for(put1=0;put1<3; put1++)
    //      *(prn_data + 92 + put1)|= flow_tab[put1 + labelcnt];
    if(++bold > 1)
    {
      bold = 0;	
      labelcnt = labelcnt+3;
    }
  }
  else if(xmarkcnt >= 6 && labelcnt1<276) // ====  xmarkcnt ==== 
  {
    //    for(put1=0;put1<3; put1++)
    //      *(prn_data + 42 + put1)|= volume_tab[put1 + labelcnt1];
    if(++bold > 1)
    {
      bold = 0;	
      labelcnt1=labelcnt1+3;
    }
  }
  if(xaxis_mark>=110) //xaxis mark 160 -*5
  {	
    //    *(prn_data + 48)|= 0xff;  //-*6
    //    *(prn_data + 49)|= 0xff;
    xaxis_mark = 0;
    xscale = 1;
    xmarkcnt++;
  }
  else
    xaxis_mark++;	
  
  //  *(prn_data + 48)|= 0x80;
  if(xscale)
  {
    if(xcolumncnt<32)   //-*7
    {
      //      for(put = 0;put < 2;put++)
      //        *(prn_data + 46 + put)|= table[((*(xmark+(xmarkcnt-1))-0x20)*32) + xcolumncnt + put];
      if(++ccnt > 1)
      {
        ccnt =0;
        xcolumncnt +=2;
      }        
    }
    else
    {
      xscale = 0;
      xcolumncnt = 0;
      if(xmarkcnt>7)
      {
        xmarkcnt = 0;
        flowvarflg = 0;
        flowvolthrdcnt =0;     
        // stop print command to printer  
        endPrint(2); //3.3.2018     
      }
    }
  }
}

void plotvoltime_thrd(void)
{
  unsigned char i,put,put1;	
  
  //char tempBuf[50];      //27.4.2018
  //char printedGraph1 = 0;//27.4.2018
  //char printedGraph2 = 0;
  //char printedGraph3 = 0;
  
  if(initflg)
  {
    initflg = 0;
    xaxis_mark = 0;
    yaxis_mark = 0;
  }
  if(yaxis_mark<40)
  {
    //for(i = 1;i<8;i++ ) 
    //  *(prn_data + 12 + (i*12))|= 0x80;//-*4
    yaxis_mark++;      
  }
  else if(labelcnt<276)
  {
    //for(put1=0;put1<3; put1++)
    //  *(prn_data + 92 + put1)|= volume_tab[put1 + labelcnt];
    if(++bold > 1)
    {
      bold = 0;	
      labelcnt=labelcnt+3;
    }
  }
  else if((xmarkcnt >= vollimit-2) && labelcnt1<276)//7
  {
    //for(put1=0;put1<3; put1++)
    //  *(prn_data + 18 + put1)|= time_tab[put1 + labelcnt1];
    if(++bold > 1)
    {
      bold = 0;
      labelcnt1=labelcnt1+3;
    }
  }
  //*(prn_data + 24)|=0x80;
  
  if(xaxis_mark>=100) //xaxis mark  -*5
  {	
    //*(prn_data + 24)|= 0xff;  //-*6
    //*(prn_data + 25)|= 0xff;
    xaxis_mark=0;
    xscale = 1;
    xmarkcnt++;
  }
  else
    xaxis_mark++;	
  if(xscale)
  {
    if(xcolumncnt<32)   //-*7
    {
      //for(put = 0;put < 2;put++)
      //  *(prn_data + 22 + put)|= table[((*(vol_xmark+(xmarkcnt-1))-0x20)*32) + xcolumncnt + put]; 	
      if(++ccnt > 1)
      {
        ccnt =0;
        xcolumncnt +=2;
      }        
    }
    else
    {
      if((xmarkcnt>9)&&(mvv_flg==0))
      {
        xmarkcnt++;
        xscale = 1;
        xcolumncnt = 0;
        mvv_flg = 1;
      }
      else
      {
        xscale = 0;
        xcolumncnt = 0;
        mvv_flg = 0;
      }
      if(xmarkcnt > vollimit)//7
      {
        xmarkcnt = 0;
        voltimeflg = 0;
        // stop print command to printer  
        endPrint(2); //3.3.2018 
        
      }
    }
  }
  //voltimeprint = 1;//8.3.2018
}

void GetPatientPreTestData(unsigned char RepeatePatID,unsigned char PreEffortNo)//9.4.2018
{
  //int i=0,len =0;
  int sizetestbuff =0;   
  
  if(NOR_GetStatus(10) == NOR_SUCCESS)//23.1.2018
  {
    asm("nop");
  }            
  
  //read 
  memset(TestDataBuffer ,0x00,sizeof(TestDataBuffer));    
  EffortStorageReadAddress = ((RepeatePatID - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS ;  
  sizetestbuff = sizeof(TestDataBuffer);    
  //for( len =0 ;len < sizetestbuff;len+=1)
  {
    if(NOR_GetStatus(10) == NOR_SUCCESS)//23.1.2018
    {
      asm("nop");
    }        
    readSPIROData((unsigned char*)&TestDataBuffer[0],EffortStorageReadAddress,sizetestbuff);  
    //EffortStorageReadAddress +=1 ;
  }
  asm("nop");  
  if(currenttesttype == FVC )
  {  
    //if(currentefforttype == PRE_EFFORT)
    {  
      //PatDetailsStruct.PatientFVCPreEfforts  = currenteffortno - 1;//9.4.2018
      EffortStorageReadAddress = ((RepeatePatID - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_FVC_EFFORT_1_PRE +((PreEffortNo - 1)*0x2000) );   //9.4.2018 PatDetailsStruct.PatientEffortNo
    }  
//    else
//    {  
//
//      PatDetailsStruct.PatientFVCPostEfforts  = currenteffortno - 1;//23.1.2018        
//      EffortStorageReadAddress = ((RepeatePatID - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_FVC_EFFORT_1_POST +((PatDetailsStruct.PatientEffortNo - 1)*0x2000) );  //14.2.2018 currentid             
//    }  
  }
  if(currenttesttype == MVV )
  {  
//    if(currentefforttype == PRE_EFFORT)          
    {
      //PatDetailsStruct.PatientMVVPreEfforts  = currenteffortno - 1;//9.4.2018                
      EffortStorageReadAddress = ((RepeatePatID - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_MVV_EFFORT_1_PRE +((PreEffortNo - 1)*0x2000) );   //9.4.2018 PatDetailsStruct.PatientEffortNo          
    }
//    else
//    {  
//      PatDetailsStruct.PatientMVVPostEfforts  = currenteffortno - 1;//23.1.2018                        
//      EffortStorageReadAddress = ((RepeatePatID - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_MVV_EFFORT_1_POST +((PatDetailsStruct.PatientEffortNo - 1)*0x2000) );  //14.2.2018 currentid                       
//    }
  }
  if(currenttesttype == SVC )
  {  
//    if(currentefforttype == PRE_EFFORT)          
    {
      //PatDetailsStruct.PatientSVCPreEfforts  = currenteffortno - 1;//9.4.2018                        
      EffortStorageReadAddress = ((RepeatePatID - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_SVC_EFFORT_1_PRE +((PreEffortNo - 1)*0x2000) );   //9.4.2018 PatDetailsStruct.PatientEffortNo           
    }  
//    else
//    {
//      PatDetailsStruct.PatientSVCPostEfforts  = currenteffortno - 1;//23.1.2018                        
//      EffortStorageReadAddress = ((RepeatePatID - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_SVC_EFFORT_1_POST +((PatDetailsStruct.PatientEffortNo - 1)*0x2000) );   //14.2.2018 currentid                        
//    } 
  }        
  if(NOR_GetStatus(10) == NOR_SUCCESS)//23.1.2018
  {
    asm("nop");
  }        
  sizetestbuff = 1000 * sizeof(int);//      sizeof(PreviewDataBuffer);      
  //for(int j=0;j<1000;j++)
  {
    readSPIROData(PreviewDataBuffer,EffortStorageReadAddress,sizetestbuff);  //23.1.2018    

  }  

  int i,j;

if ((midreturnflag) && (currenttesttype == SVC))//if MVV effort has been loaded and SVC is to be loaded
  {  
    memset(testvolumedataarraypre,0x00,sizeof(testvolumedataarraypre));
    for (i = 0,j=0; i <= 1000 - 1; i++,j+=4) 
    {
        testvolumedataarraypre[i] = ((uint32_t)PreviewDataBuffer[j+0] << 24 )|
                           ((uint32_t)PreviewDataBuffer[j+1] << 16 )|
                           ((uint32_t)PreviewDataBuffer[j+2] << 8  )|
                           ((uint32_t)PreviewDataBuffer[j+3]);        
    }
  }
  else
  {
    memset(testdataarraypre,0x00,sizeof(testdataarraypre));    
    for (i = 0,j=0; i <= 1000 - 1; i++,j+=4) 
    {    
       testdataarraypre[i]= ((uint32_t)PreviewDataBuffer[j+0] << 24 )|
                          ((uint32_t)PreviewDataBuffer[j+1] << 16 )|
                          ((uint32_t)PreviewDataBuffer[j+2] << 8  )|
                          ((uint32_t)PreviewDataBuffer[j+3]);    
    }    
  }  
  
} 


void formParamTitle(unsigned char position , int *ctr)//copy parameter name and unit in string array to be sent to PIC for printing table
{
	lstrcpy(str_arr + *ctr * maxcount,paramtitle[position]);
	lstrcpy(str_arr + *ctr * maxcount + 12,paramunit[position]);
	copyData(position,*ctr);
	(*ctr) = (*ctr) + 1;
}

void copyData(unsigned char para, unsigned char counter1)//copy data for table
{
	unsigned char y = 27;
	double td,d = 0.0;

	dtos(predictedvals[para],0,1,str_arr + (counter1 * maxcount + 20));
	if (pre_post_fvc || pre_post_svc || pre_post_mvv || (djflg && (currentefforttype == POST_EFFORT))) 
	{
		dtos(actualvals1[para],0,1,str_arr + (counter1 * maxcount + y));//actual value for pre effort if pre and post both are to be printed
		y += 7;
		if (predictedvals[para] > 0)
			td = getPercent(*(actualvals1 + para) , *(predictedvals + para)); //(actualvals1[para] / predictedvals[para]) * 100.0;//% of pred
		else
			td = 0.0;
		d = td;
		dtos(td,0,1,returnstring);
		lstrcpy(str_arr + (counter1 * maxcount + y),returnstring);
		y += 22;
		if (actualvals1[para] > 0)
			{
				td = getPercent((double)(*(actualvals + para)  - *(actualvals1 + para)) , *(actualvals1 + para)); //((actualvals[para] - actualvals1[para]) / actualvals1[para]) * 100.0;//% diff
			}
		else
			td = 0.0;
		dtos(td,0,1,str_arr + (counter1 * maxcount + y));
		y -= 15;
	}
	dtos(actualvals[para],0,1,str_arr + (counter1 * maxcount + y));//actual value for this effort
	y += 7;
	if (predictedvals[para] > 0)
		td = getPercent(*(actualvals + para) , *(predictedvals + para)) ; //(actualvals[para] / predictedvals[para]) * 100.0;
	else
		td = 0.0;
	if (y == 34)
		d = td;
	dtos(td,0,1,str_arr + (counter1 * maxcount + y));
	if (para == 0)
		tempanal1 = d;
	if (para == 1)
		tempanal2 = d;
	if (para == 13)
		tempanal3 = d;
	if (para == 18)
		tempanal4 = d;
	if (para == 2)
		tempanal5 = d;
}

void lstrcpy(char *dest,char *source)
{
  unsigned int i = 0;
  while (*(source + i) != 0x00)
  {
    *(dest + i) = *(source + i);
    i++;
  }
}

void dtos(double d,int i,unsigned char flag,char *thisstring)//convert decimal no to string representation (this was implemented instead of sprintf because hex file was not being created if sprintf was being used in this project. Try to find out why)
{
  unsigned char j = 0;
  int dint = 0;
  double ddouble = 0.0;
  double tempintfloat = 0.0;
  for (j=0;j<8;j++)//clear string
    thisstring[j] = 0x20;	
  j = 0;
  
  if(flag == 1 && d == 0.0)
  {
    *(thisstring + 0) = '-';
    *(thisstring + 1) = '-';
    *(thisstring + 2) = '.';
    *(thisstring + 3) = '-';
    *(thisstring + 4) = '-';
  }
  else
  {
    if (flag == 1)//if fractional no is to be converted
      i = (int)(d);//get integer part of fractional no
    if (i < 0)//if this no is negative
    {
      *(thisstring + j++) = '-';//put a '-' in string
      i = abs(i);//get absolute value of this integer
    }
    j += getString(i,j,thisstring);//get string representation for this integer and get the no of characters for this integer part
    if (flag == 1)//if fractional no is to be converted
    {
      *(thisstring + j++) = '.';//add a decimal point to string
      ddouble = modf(d,&tempintfloat);//get fractional part of number
      ddouble = ddouble * 100.0;//get 2 significant digits after decimal point
      dint = abs((int)ddouble);//get absolute value of this fractional part in integer form
      j += getString(dint,j,thisstring);//get string representation of fractional part and no of characters in string for this fractional part
    }
    *(thisstring + j) = 0x00;//add null character at the end of string
  }
}

unsigned char getString(int intvalue, unsigned char j,char *string)//get string representation for this integer value used in dtos function
{
  int x;
  unsigned char flag;
  
  x = 10000;//max value that can be converted is 99999 (enough for our application) hence division factor to start with is 10000
  flag = 0;//indicated that no digit has been obtained yet
  while (x > 10)//for all but the last 2 digits of the number
  {
    if (((intvalue / x) > 0) || (((intvalue / x) == 0) && (flag > 0)))//if this digit is non zero or if it is zero but not the first digit (to avoid 0 padding in string representation)
    {
      flag++;//indicate that at least one digit has been converted
      *(string + j++) = asciivalue[(intvalue / x)];//get ascii value for this digit
      intvalue = intvalue % x;//get remainder of number
    }
    x = x / 10;//get new division factor
  }
  //above logic will work for fractional part also because fractional will be taken only for 2 significant digits
  *(string + j++) = asciivalue[(intvalue / x)];//last 2 digits will always be converted, even if the value is 0
  *(string + j++) = asciivalue[abs((intvalue % x))];//27.4.2018
  flag += 2;//get no of digits converted, i.e., no of characters in this string
  return flag;
}

void storeBestEffort(void)
{
  unsigned int i ,j; //startaddress
  unsigned char tempnoofpats; // bankno
  int len =0;
  unsigned int startposition = 0;
  unsigned int endposition = 0;        
  int sizetestbuff =0;
  char TestBuff[50];//6.4.2018
  //chk condition for exit without performing any test in conduct old patient test    
  //if(currenttesttype == MVV) return ; //20.3.2018 to be chk // for MVV  test only one effort allowed
  if(besteffortno >0)//20.3.2018 (storeeffortno > 0)//if valid effort no is obtained
  {
//    if (currenttesttype == FVC)
//    {
//      if (positions1[0] < positions1[1])//if positive wave occurs before negative wave
//      {
//        startposition = positions1[0];//start position for storage is start of positive wave
//        endposition = positions1[3];//end position for storage is end of negative wave
//      }
//      else//if positive wave occurs after negative wave
//      {
//        startposition = positions1[1];//start position for storage is start of negative wave
//        endposition = positions1[2];//end position for storage is end of positive wave
//      }
//    }
//    if (currenttesttype == SVC)
//    {
//      startposition = positions1[6];//start of test
//      endposition = positions1[7];//end of test
//    }
//    if (currenttesttype == MVV)
//    {
//      startposition = 0;//positions1[6];//start of test
//      endposition = positions1[7];//end of test
//      storeeffortno = 1;//only one effort to be stored
//    }          
    
    if (repeatflag == 0)//if new test for new patient was taken
    {
      SpiroSettingStruct.LastPatId = currentid;//store id 
      tempnoofpats = SpiroSettingStruct.LastPatId ;
      if (tempnoofpats <= MAXPATIENTS)
      {
        if (tempnoofpats < MAXPATIENTS)
          SpiroSettingStruct.LastPatId = currentid;
        if (tempnoofpats == MAXPATIENTS)
          SpiroSettingStruct.LastPatId = MAXPATIENTS;// + 1;
      }
      lastid = SpiroSettingStruct.LastPatId ;// 22.1.2018
      if(overlapflag)
        SpiroSettingStruct.TotalReviewRecord = MAXPATIENTS; //5.4.2018        
      else  
        SpiroSettingStruct.TotalReviewRecord = currentid; //5.4.2018      
    }
    //read 
    memset(TestDataBuffer ,0x00,sizeof(TestDataBuffer));    
    EffortStorageReadAddress = ((currentid - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS ;  

    sizetestbuff = sizeof(TestDataBuffer);    
    //for( len =0 ;len < sizetestbuff;len+=1)
    {
      if(NOR_GetStatus(10) == NOR_SUCCESS)//23.1.2018
      {
        asm("nop");
      }        
      //readSPIROData((unsigned char*)&TestDataBuffer[len],EffortStorageReadAddress,1);  
      readSPIROData((unsigned char*)&TestDataBuffer[0],EffortStorageReadAddress,sizetestbuff);        
      //EffortStorageReadAddress +=1 ;
    }
    if(currenttesttype == FVC )
    {  
      if(currentefforttype == PRE_EFFORT)
      {  
        //PatDetailsStruct.PatientFVCPreEfforts  = currenteffortno - 1;//23.1.2018
        EffortStorageReadAddress = ((currentid - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_FVC_EFFORT_1_PRE +((besteffortno - 1)*0x2000) );   
      }  
      else
      {  
        //PatDetailsStruct.PatientFVCPostEfforts  = currenteffortno - 1;//23.1.2018        
        EffortStorageReadAddress = ((currentid - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_FVC_EFFORT_1_POST +((besteffortno - 1)*0x2000) );               
      }  
    }
    if(currenttesttype == MVV )
    {  
      if(currentefforttype == PRE_EFFORT)          
      {
        //PatDetailsStruct.PatientMVVPreEfforts  = currenteffortno - 1;//23.1.2018                
        EffortStorageReadAddress = ((currentid - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_MVV_EFFORT_1_PRE +((besteffortno - 1)*0x2000) );             
      }
      else
      {  
        //PatDetailsStruct.PatientMVVPostEfforts  = currenteffortno - 1;//23.1.2018                        
        EffortStorageReadAddress = ((currentid - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_MVV_EFFORT_1_POST +((besteffortno - 1)*0x2000) );                         
      }
    }
    if(currenttesttype == SVC )
    {  
      if(currentefforttype == PRE_EFFORT)          
      {
        //PatDetailsStruct.PatientSVCPreEfforts  = currenteffortno - 1;//23.1.2018                        
        EffortStorageReadAddress = ((currentid - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_SVC_EFFORT_1_PRE +((besteffortno - 1)*0x2000) );               
      }  
      else
      {
        //PatDetailsStruct.PatientSVCPostEfforts  = currenteffortno - 1;//23.1.2018                        
        EffortStorageReadAddress = ((currentid - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_SVC_EFFORT_1_POST +((besteffortno - 1)*0x2000) );                           
      } 
    }
    
    //////////////////////////   read only besteffort slot      //////////////////
      sizetestbuff = sizeof(TestDataBuffer);      

      if(NOR_GetStatus(10) == NOR_SUCCESS)//23.1.2018
      {
        asm("nop");
      }        
      readSPIROData((unsigned char*)&BestEffortDataBuffer[0],EffortStorageReadAddress,sizetestbuff);        

        
    for (i = 0,j=0; i < 1000; i++,j+=4) 
    {
        BestEffortdataarray[i] = ((uint32_t)BestEffortDataBuffer[j+0] << 24 )|
                           ((uint32_t)BestEffortDataBuffer[j+1] << 16 )|
                           ((uint32_t)BestEffortDataBuffer[j+2] << 8  )|
                           ((uint32_t)BestEffortDataBuffer[j+3]);        

    }
    /////////////////////////////////////////////////////////////////////////////////
    eraseSPIRODataBlock(currentid);//erase current id
    
    PatDetailsStruct.P_Signature =0x55;
    PatDetailsStruct.PatientID = currentid;
    //PatDetailsStruct.PatientNameArray =;
    if(repeatflag == 0)
    {  
      memcpy(PatDetailsStruct.PatientNameArray,&PatNameString[0],sizeof(PatNameString));
      PatDetailsStruct.PatientAge = AgeValue ;
      PatDetailsStruct.PatientGender = GenderValue ;
      PatDetailsStruct.PatientHeight = HeightValue;
      PatDetailsStruct.PatientWeight = WeightValue ;
      PatDetailsStruct.PatientSmokeStaus = SmokerValue;
    }    
    PatDetailsStruct.PatientEffortNo = 1;//20.3.2018//best effort is always stored at first effort //currenteffortno - 1;
    PatDetailsStruct.PatientEffortDate = RTC_DateStructure.RTC_Date ; //19.1.2018
    PatDetailsStruct.PatientEffortMonth = RTC_DateStructure.RTC_Month;
    PatDetailsStruct.PatientEffortYear = RTC_DateStructure.RTC_Year;
    PatDetailsStruct.PatientEffortHour = RTC_TimeStructure.RTC_Hours ;
    PatDetailsStruct.PatientEffortMinutes = RTC_TimeStructure.RTC_Minutes;      
    if(currenttesttype == FVC )
    { 
        if(SpiroSettingStruct.UsageType == OCCUPATIONAL) //occupational
        {
          currentefforttype = PRE_EFFORT;//2.5.2018 for occupational first saving error
        }
      if(currentefforttype == PRE_EFFORT)
      {  

        PatDetailsStruct.PatientFVCPreEfforts  =  1;//20.3.2018 after storing best effort set pre effort valuae to 1
        EffortStorageAddress = ((currentid - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_FVC_EFFORT_1_PRE +((PatDetailsStruct.PatientEffortNo - 1)*0x2000) );   
      }  
      else
      {  
        PatDetailsStruct.PatientFVCPostEfforts  = 1;//20.3.2018 after storing best effort set pre effort valuae to 1       
        EffortStorageAddress = ((currentid - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_FVC_EFFORT_1_POST +((PatDetailsStruct.PatientEffortNo - 1)*0x2000) );               
      }  
    }
    if(currenttesttype == MVV )
    {  
      if(currentefforttype == PRE_EFFORT)          
      {
        PatDetailsStruct.PatientMVVPreEfforts  = 1;//20.3.2018 after storing best effort set pre effort valuae to 1                
        EffortStorageAddress = ((currentid - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_MVV_EFFORT_1_PRE +((PatDetailsStruct.PatientEffortNo - 1)*0x2000) );             
      }
      else
      {  
        PatDetailsStruct.PatientMVVPostEfforts  = 1;//20.3.2018  after storing best effort set pre effort valuae to 1                      
        EffortStorageAddress = ((currentid - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_MVV_EFFORT_1_POST +((PatDetailsStruct.PatientEffortNo - 1)*0x2000) );                         
      }
    }
    if(currenttesttype == SVC )
    {  
      if(currentefforttype == PRE_EFFORT)          
      {
        PatDetailsStruct.PatientSVCPreEfforts  = 1;//20.3.2018  after storing best effort set pre effort valuae to 1                      
        EffortStorageAddress = ((currentid - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_SVC_EFFORT_1_PRE +((PatDetailsStruct.PatientEffortNo - 1)*0x2000) );               
      }  
      else
      {
        PatDetailsStruct.PatientSVCPostEfforts  =  1;//23.1.2018  after storing best effort set pre effort valuae to 1                      
        EffortStorageAddress = ((currentid - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS + (TEST_RAM_OFFSET_SVC_EFFORT_1_POST +((PatDetailsStruct.PatientEffortNo - 1)*0x2000) );                           
      } 
    }  
    
    //store test data at respective address
    DummyAddress =0;
    DummyAddress = EffortStorageAddress -( ((currentid - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS);
  
    for(i=0;i<1000; i++)
    {
 // 20.3.2018 not required in storebest effort as 
       TestDataBuffer[DummyAddress]   = (BestEffortdataarray[i] >> 24) & 0xFF;
       TestDataBuffer[DummyAddress+1] = (BestEffortdataarray[i] >> 16) & 0xFF;
       TestDataBuffer[DummyAddress+2] = (BestEffortdataarray[i] >> 8) & 0xFF;
       TestDataBuffer[DummyAddress+3] = BestEffortdataarray[i] & 0xFF;
      
       DummyAddress += sizeof(int);
    }
    
    
    EffortStoragewriteAddress = ((currentid - 1) * 0x20000 ) + NOR_SPIRODATA_ADDRESS ; 
    memcpy((unsigned char*)&TestDataBuffer[0],(unsigned char*)&PatDetailsStruct,sizeof(PatDetailsStruct));
    for( len =0 ;len < sizetestbuff;len +=2) 
    {
      if(NOR_GetStatus(10) == NOR_SUCCESS)//23.1.2018
      {
        asm("nop");
      }       
      writeSPIROData(TestDataBuffer +len,EffortStoragewriteAddress,2);              
      EffortStoragewriteAddress += 2;//sizeof(int) ;//2
    }          
    if(currenttesttype  == FVC)      //6.4.2018
    {
      //sprintf(TestBuff,"SAVING EFFORT NO %1d",besteffortno);  
      //TEXT_SetText(LableFVCPOPup,TestBuff);     
    }  
    else if(currenttesttype  == MVV)
    {
      sprintf(TestBuff,"SAVING EFFORT NO %1d:PLEASE WAIT !!",besteffortno);        
      TEXT_SetText(LableMVVPOPup,TestBuff);     
    }  
    else if(currenttesttype  == SVC)
    {
      //sprintf(TestBuff,"SAVING EFFORT NO %1d",besteffortno);        
      //TEXT_SetText(LableSVCPOPup,TestBuff);     
    }  
    GUI_Exec();
    GUI_Delay(3000);
    updateSetting =1; // to store patient id      //17.1.2018         
    if ((currentid == MAXPATIENTS) && (repeatflag == 0))//if max patients are done with, display overlap prompt
    {
      //enableInt();
      dispOverlapPrompt(1);  //5.4.2018 to be checked if it is needed 
      //return;  //23.4.2018
    }

  }
  SavingEffortFlag = 0;//23.4.2018   for blocking multiple Saving and touchscreen hanging issue
}

void ResetPrint(void)
{
    reportprinted = 0;        //26.4.2018  to abort print
    printflag     = 0;        //26.4.2018
    endPrint(2);              //26.4.2018
    flowvarflg    = 0;        //26.4.2018
    voltimeflg    = 0;        //26.4.2018  
    PrintGuiDelay = 0;        //27.4.2018 to avoid delay
}  