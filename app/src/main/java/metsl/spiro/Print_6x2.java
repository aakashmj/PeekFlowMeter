package metsl.spiro;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Build;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by aakashj on 8/21/15.
 */
public class Print_6x2 {
    protected float mm25;
    protected float samplingrate;
    Bitmap myDocument;
    float blankDocuWidthInInches;
    float blankDocuHeightInInches;
    float blankDocuResolution;

    float penWidth;
    float LeadWidth;
    float LeadHeight;
    float LongBox;

    float mm1;

    float ActualVerticalResolution;
    float ActualHorizontalResolution;

    public double P_Axis = 0;
    public double PR_Interval = 0;
    public double QRS_Interval = 0;
    public double QTc = 0;
    public double R_Axis = 0;
    public double R_To_R_Interval = 0;
    public double T_Axis = 0;
    public double QT_Interval = 0;
    public double ST_Interval = 0;
    public int HR = 0;

    protected float DpiY=200;

    public Print_6x2() {
        mm25 = 25f;//0.98425f;
        samplingrate = 240;
        float bufferSize = samplingrate * 8;
    }

    private void DrawPulse(Canvas g, Paint p, float gain, float sweep) {
        float x = LeadWidth * 3;
        float y = 0.25f;
        float y1 = 0.25f;
        float width = blankDocuWidthInInches - x - 0.004f;
        float height = LeadHeight * 4;
        g.drawRect(x, y, width, height, p);

        x += mm1 * 2;
        float mm20 = mm1 * 20;
        y = y1 + mm20;
        pulse(g, p, x, y, gain, sweep);

        float mm50 = (mm1 * 50);
        y = (y1 + mm50);
        pulse(g, p, x, y, gain, sweep);

        float mm80 = (mm1 * 80);
        y = (y1 + mm80);
        pulse(g, p, x, y, gain, sweep);

        float mm110 = (mm1 * 110);
        y = (y1 + mm110);
        pulse(g, p, x, y, gain, sweep);
    }

    private void pulse(Canvas g, Paint p, float x, float y, float gain, float sweep) {
        Paint pn = new Paint();
        pn.setColor(Color.BLUE);
        pn.setStrokeWidth(0.02f);
        float mm2 = (mm1 * 2);
        float mm3 = (mm1 * 3);
        switch (String.valueOf(sweep)) {
            case "12.5":
                mm3 = (mm3 / 2);
                break;
            case "25":
                break;
            case "50":
                mm3 = (mm3 * 2);
                break;
        }

        float mm10 = (mm1 * 10 * gain);

        PointF[] pf = new PointF[6];
        pf[0] = new PointF(x, y);

        pf[1] = new PointF(x + mm2, y);
        pf[2] = new PointF(x + mm2, y - mm10);
        pf[3] = new PointF(x + mm2 + mm3, y - mm10);
        pf[4] = new PointF(x + mm2 + mm3, y);
        pf[5] = new PointF(x + mm2 + mm3 + mm2, y);
        //draw line

    }


    private void DrawSignature(Canvas g, Paint p, float x, float y, String SpName, Image signImage) {
        float remainingWidth = (blankDocuWidthInInches - LongBox - 0.004f);
        float remainingHeight = (float) (blankDocuHeightInInches - y - 0.178);

        g.drawRect(x, y, remainingWidth, remainingHeight, p);

    }


    private void DrawComments(Canvas g, Paint p, float x, float y, String comments) {
        float remainingHeight = (float) (blankDocuHeightInInches - y - 0.179);

        //RectangleF rf = new RectangleF(x, y, LongBox, remainingHeight);

        g.drawRect(x, y, LongBox, remainingHeight, p);
        p.setColor(Color.BLACK);
        p.setTextSize(10);
        g.drawText(comments, x, y, p);

    }

    private void DrawAxis(Canvas g, Paint p, float x, float y, String HR, String PastHistory, String Bp,
                          String SpO2, double pAxis, double QRsAxis, double TAxis, double PR, double QRS, double QTc,
                          double FVC, double FEV1, double PEF) {
        float remainingWidth = (blankDocuWidthInInches - LongBox - 0.004f);
        float oneThirdRemainingWidth = (remainingWidth / 3);
        float halfRemainingWidth = (remainingWidth / 2);
        float dividedHt = LeadHeight / 6;
        p.setColor(Color.BLACK);
        p.setTextSize(9);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        Paint p2 = new Paint();
        p2.setColor(Color.BLACK);
        p2.setTextSize(9);
        //for Axis

        g.drawRect(x, y, remainingWidth, LeadHeight, p);

        g.drawText("HR: ", x, y, p);
        Rect bounds = new Rect();
        p.getTextBounds("HR: ", 0, "HR: ".length(), bounds);
        int height = bounds.height();
        int width = bounds.width();
        g.drawText(HR, x + width, y, p2);

        g.drawText("BP: ", x + oneThirdRemainingWidth, y, p);
        bounds = new Rect();
        p.getTextBounds("BP: ", 0, "BP: ".length(), bounds);
        height = bounds.height();
        width = bounds.width();
        g.drawText(Bp, x + oneThirdRemainingWidth + width, y, p2);

        g.drawText("SaO: ", x + (2 * oneThirdRemainingWidth), y, p);
        bounds = new Rect();
        p.getTextBounds("SaO: ", 0, "SaO: ".length(), bounds);
        height = bounds.height();
        width = bounds.width();
        g.drawText(SpO2, x + (2 * oneThirdRemainingWidth) + width, y, p2);


        //for axis
        g.drawText("Axis (in degree)", x, y + dividedHt, p);


        g.drawText("P: ", x, y + (dividedHt * 2), p);
        bounds = new Rect();
        p.getTextBounds("P: ", 0, "P: ".length(), bounds);
        height = bounds.height();
        width = bounds.width();
        g.drawText(String.valueOf(pAxis), x + width, y + (dividedHt * 2), p2);


        g.drawText("QRs: ", x, y + (dividedHt * 3), p);
        bounds = new Rect();
        p.getTextBounds("QRs: ", 0, "QRs: ".length(), bounds);
        height = bounds.height();
        width = bounds.width();
        g.drawText(String.valueOf(QRsAxis), x + width, y + (dividedHt * 3), p2);


        g.drawText("T: ", x, y + (dividedHt * 4), p);
        bounds = new Rect();
        p.getTextBounds("T: ", 0, "T: ".length(), bounds);
        height = bounds.height();
        width = bounds.width();
        g.drawText(String.valueOf(TAxis), x + width, y + (dividedHt * 4), p2);


        //for Interval

        g.drawText("Interval (in sec)", x + halfRemainingWidth, y + dividedHt, p);


        g.drawText("PR: ", x + halfRemainingWidth, y + (dividedHt * 2), p);
        bounds = new Rect();
        p.getTextBounds("PR: ", 0, "PR: ".length(), bounds);
        height = bounds.height();
        width = bounds.width();
        g.drawText(String.valueOf(PR), x + halfRemainingWidth + width, y + (dividedHt * 2), p2);


        g.drawText("QRS: ", x + halfRemainingWidth, y + (dividedHt * 3), p);
        bounds = new Rect();
        p.getTextBounds("QRS: ", 0, "QRS: ".length(), bounds);
        height = bounds.height();
        width = bounds.width();
        g.drawText(String.valueOf(QRS), x + halfRemainingWidth + width, y + (dividedHt * 3), p2);


        g.drawText("QTc: ", x + halfRemainingWidth, y + (dividedHt * 4), p);
        bounds = new Rect();
        p.getTextBounds("QTc: ", 0, "QTc: ".length(), bounds);
        height = bounds.height();
        width = bounds.width();
        g.drawText(String.valueOf(QTc), x + halfRemainingWidth + width, y + (dividedHt * 4), p2);


        //for spiromentry


        g.drawText("FVC: ", x, y + (dividedHt * 5), p);
        bounds = new Rect();
        p.getTextBounds("FVC: ", 0, "FVC: ".length(), bounds);
        height = bounds.height();
        width = bounds.width();
        g.drawText(String.valueOf(FVC), x + width, y + (dividedHt * 5), p2);


        g.drawText("FEV1: ", x + oneThirdRemainingWidth, y + (dividedHt * 5), p);
        bounds = new Rect();
        p.getTextBounds("FEV1: ", 0, "FEV1: ".length(), bounds);
        height = bounds.height();
        width = bounds.width();
        g.drawText(String.valueOf(FEV1), x + oneThirdRemainingWidth + width, y + (dividedHt * 5), p2);


        g.drawText("PEF: ", x + (2 * oneThirdRemainingWidth), y + (dividedHt * 5), p);
        bounds = new Rect();
        p.getTextBounds("PEF: ", 0, "PEF: ".length(), bounds);
        height = bounds.height();
        width = bounds.width();
        g.drawText(String.valueOf(PEF), x + (2 * oneThirdRemainingWidth) + width, y + (dividedHt * 5), p2);


    }

    private void DrawLongLead(Canvas g, Paint p, float x, float y, List<Float> data, float xIncrement,
                              float leadOffset, float pixpermmVert, boolean IsGrid, float gain, float sweep, String LowPassFilt,
                              String HighPassFilt, String MedianFilt, String PaceMaker) {
        try {
            LongBox += 0.15f;
            //  RectangleF rf = new RectangleF(x, y, LongBox, LeadHeight);
            g.drawRect(x, y, LongBox, LeadHeight, p);

            p.setColor(Color.BLACK);
            p.setTextSize(10);


            Paint p3 = new Paint();
            p3.setColor(Color.BLACK);
            p3.setTextSize(10);
            p3.setTypeface(Typeface.DEFAULT_BOLD);

            g.drawText("II", x, y, p3);


            pulse(g, p, x, y + mm1 * 15, gain, sweep);
            p.setTextSize(8);

            String str = "Gain: " + gain * 10 + "mm/mV   ";

            g.drawText(str, x, y + mm25, p);


            float xshift = 0;

            Rect bounds = new Rect();
            p.getTextBounds(str, 0, str.length(), bounds);

            int height = bounds.height();
            int width = bounds.width();
            xshift += width;
            str = "Paper Speed: " + sweep + "mm/sec  ";
            g.drawText(str, x + xshift, y + mm25, p);


            p.getTextBounds(str, 0, str.length(), bounds);

            height = bounds.height();
            width = bounds.width();
            xshift += width;
            //if (LowPassFilt == 0)
            //{
            //    sb.AppendFormat("{0:-4}", "No");
            //}
            //else
            //{
            //    sb.AppendFormat("{0:-4}", LowPassFilt.ToString());
            //}
            str = "Low Pass Filt: " + LowPassFilt;//sb.ToString();
            g.drawText(str, x + xshift, y + mm25, p);

            p.getTextBounds(str, 0, str.length(), bounds);

            height = bounds.height();
            width = bounds.width();
            xshift += width;
            //if (HighPassFilt == 0)
            //{
            //    sb.AppendFormat("{0:-4}", "No");
            //}
            //else
            //{
            //    sb.AppendFormat("{0:-4}", HighPassFilt.ToString());
            //}
            str = "High Pass Filt: " + HighPassFilt;//sb.ToString();
            g.drawText(str, x + xshift, y + mm25, p);


            p.getTextBounds(str, 0, str.length(), bounds);

            height = bounds.height();
            width = bounds.width();
            xshift += width;

            str = "Median Filt: " + MedianFilt;//sb.ToString();
            g.drawText(str, x + xshift, y + mm25, p);


            p.getTextBounds(str, 0, str.length(), bounds);

            height = bounds.height();
            width = bounds.width();
            xshift += width;
            str = "Pacemaker: " + PaceMaker;

            g.drawText(str, x + xshift, y + mm25, p);

            p.setTextSize(6.5f);

            g.drawText("Maestros Electronics & Telecommunications Systems Limited 1.2.0.0", x, y + LeadHeight, p);


            if (IsGrid) {
                MinorGridLogic(g, p, x, y,
                        (x + LongBox), (y + LeadHeight));
            }
            p.setStyle(Paint.Style.FILL);

            float count = (LongBox / xIncrement);
            PointF prevPt = new PointF(x, data.get(0));
            prevPt.y = (leadOffset - (data.get(0) * pixpermmVert));
            //prevPt.Y = Convert.ToSingle(leadOffset - data[0]);
            PointF currPt = new PointF(x, data.get(0));
            x += xIncrement;
            p.setAntiAlias(true);

            float currPWidth = p.getStrokeWidth();
            p.setStrokeWidth(2 * currPWidth);
            try {
                for (int i = 1; i < count; i++, x += xIncrement) {
                    currPt.x = x;
                    currPt.y = (leadOffset - (data.get(i) * pixpermmVert));
                    //currPt.Y = Convert.ToSingle(leadOffset - data[i]);
                    g.drawLine(prevPt.x, prevPt.y, currPt.x, currPt.y, p);

                    prevPt = currPt;
                }
            } catch (Exception ignored) {

            }
            p.setStrokeWidth(currPWidth);
        } catch (Exception ignored) {

        }
    }


    private void DrawLead(Canvas g, Paint p, float x, float y,
                          String leadSymbol, List<Float> data, float xIncrement, float leadOffset,
                          float pixpermmVert, boolean IsGrid, float gain, float sweep) {
        float currPWidth = 0;
        try {
            p.setAntiAlias(true);
            //  RectangleF rf = new RectangleF(x, y, LeadWidth, LeadHeight);
            g.drawRect(x, y, LeadWidth, LeadHeight, p);

            //draw pulse.
            //float pX = rf.X;
            //float pY = rf.Y + mm1 * 15;
            //pulse(g, p, pX, pY, gain, sweep);
            p.setColor(Color.BLACK);
            p.setTextSize(10);
            p.setTypeface(Typeface.DEFAULT_BOLD);
            g.drawText(leadSymbol, x, y, p);


            if (IsGrid) {
                MinorGridLogic(g, p, x, y,
                        (x + LeadWidth), (y + LeadHeight));
            }
            p.setStyle(Paint.Style.FILL);

            float count = (LeadWidth / xIncrement);
            PointF prevPt = new PointF(x, data.get(0));
            prevPt.y = (leadOffset - (data.get(0) * pixpermmVert));
            //prevPt.Y = Convert.ToSingle(leadOffset - data[0]);
            PointF currPt = new PointF(x, data.get(0));
            x += xIncrement;

            currPWidth = p.getStrokeWidth();
            p.setStrokeWidth(2 * currPWidth);
            //g.PageUnit = GraphicsUnit.Display;
            for (int i = 1; i < count; i++, x += xIncrement) {
                currPt.x = x;
                currPt.y = (leadOffset - (data.get(i) * pixpermmVert));
                //currPt.Y = Convert.ToSingle(leadOffset -data[i] );
                g.drawLine(prevPt.x, prevPt.y, currPt.x, currPt.y, p);

                prevPt = currPt;
            }
            //g.PageUnit = GraphicsUnit.Inch;
            p.setStrokeWidth(currPWidth);

        } catch (Exception ex) {
            p.setStrokeWidth(currPWidth);
        }
    }

    void MinorGridLogic(Canvas g, Paint p, float rectX, float rectY,
                        float width, float height) {
        p.setStyle(Paint.Style.STROKE);

        p.setAntiAlias(true);
        int testCondi = 0;
        float pwid = p.getStrokeWidth();

        p.setStrokeWidth(2 * pwid);
        float dark = mm25 * 1;
        float blank = mm25 * 4;// 7.15f / 2;
        //  mPen.DashPattern = new Single[] { dark, blank };//,mm1,num,mm1,num,mm1,2*num};
        p.setPathEffect(new DashPathEffect(new float[]{dark, blank}, 0));
        for (float x = rectX; x < width; x += mm1, testCondi++) {
            g.drawLine(x, rectY, x, height, p);
        }
        testCondi = 0;

        //for medium squares
        float currPWidth = p.getStrokeWidth();
        p.setPathEffect(new DashPathEffect(new float[]{mm25, mm25}, 0));

        testCondi = 0;
        for (float x = rectX; x < width; x += mm1, testCondi++) {
            if (testCondi % 5 == 0) {
                g.drawLine(x, rectY, x, height, p);
            }
        }
        testCondi = 0;
        for (float y = rectY; y < height; y += mm1, testCondi++) {
            if (testCondi % 5 == 0) {
                g.drawLine(rectX, y, width, y, p);
            }
        }
        p.setStrokeWidth(pwid);

    }

    private void DrawGPInformation(Canvas g, Paint p, float x, float y,
                                   String recDTatGP, String recNo, String GPName
            , String Age, String Gender, String Weight, String Height,
                                   String PatientName, String SpName) {

        Date dtFromGP = new Date();


        StringBuilder sb = new StringBuilder();

        if (PatientName.isEmpty()) {

            sb.append(" ", 0, 41);
        } else {
            if (PatientName.length() > 41) {
                PatientName = PatientName.substring(0, 41);
            }
            sb.append(PatientName, 0, 41);
        }
        p.setTextSize(8);
        p.setColor(Color.BLACK);
        float width = blankDocuWidthInInches - 0.004f;
        float dividedWidth = width / 4;

        //    RectangleF rf = new RectangleF(x, y, width, 0.25f);
        g.drawRect(x, y, width, 0.25f, p);
        g.drawText(sb.toString(), x, y, p);

        sb.delete(0, sb.length());
        sb.append("Rep By : ");
        if (SpName.isEmpty()) {
            sb.append(" ", 0, 41);
        } else {
            if (SpName.length() > 41) {
                SpName = SpName.substring(0, 41);
            }
            sb.append(SpName, 0, 41);
        }
        g.drawText(sb.toString(), dividedWidth, y, p);

        sb.delete(0, sb.length());
        sb.append("Rep On : ");
        sb.append(DateFormat.getDateInstance().format(new Date()));
        g.drawText(sb.toString(), 2 * dividedWidth, y, p);

        sb.delete(0, sb.length());
        sb.append("ID: ");
        sb.append("                    ", 0, 20);
        sb.append(Age).append(" yrs, ").append(Gender).append(", ").append(Weight).append(" kg, ").append(Height).append(" cm ");

        float y2 = y + (0.25f / 2);
        g.drawText(sb.toString(), x, y2, p);

        sb.delete(0, sb.length());
        sb.append("Sent By : ");
        if (GPName.isEmpty()) {
            sb.append(" ", 0, 41);
        } else {
            if (GPName.length() > 41) {
                GPName = GPName.substring(0, 41);
            }
            sb.append(GPName, 0, 41);
        }

        g.drawText(sb.toString(), dividedWidth, y2, p);

        sb.delete(0, sb.length());
        sb.append("Rec On : ");
        sb.append(recDTatGP);
        g.drawText(sb.toString(), 2 * dividedWidth, y2, p);


    }

    private void DrawOuterRectangle(Canvas g, Paint p, float x, float y) {
        //float rw = Convert.ToSingle(myDocument.Width - 0.004f);
        //float rh = Convert.ToSingle(myDocument.Height - 0.004f);
        //g.PageUnit = GraphicsUnit.Pixel;
        g.drawColor(Color.WHITE);

        //g.DrawRectangle(p, x,y, rw, rh);
        // g.PageUnit = GraphicsUnit.Inch;

    }


    public Bitmap GetImage(Date dtFromGP, Date recDTatGP, String PatientID, String GPName, String comments
            , String Age, String Gender, String Weight, String Height, String PastHistory, String Bp,
                           String spName, String PatientName, byte[] ECGData, String symtoms, String Analysis,
                           String Interpretation, String LowPassFilt, String HighPassFilt, String MedianFilt, String Pacemaker,
                           String Heading, String GPPhone, String GPFax, String Diabetis, String SpO2, double FVC, double FEV1, double PEF, String DeviceID, String DeviceType) {
        Image SignImage = null;

        MakeA4sizeImageOnDisk();

        DataReader dataread = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Objects.equals(DeviceType, "R-10")) {
                R_10 r10 = new R_10();
                dataread = new DataReader(r10);
                samplingrate = 500;
            } else {
                eUno3 euno = new eUno3();
                dataread = new DataReader(euno);
                samplingrate = 250;
            }
        }
        int nrows = 0;
        int nchannels = 0;
        int[][] ECGChan;
        ECGChan = dataread != null ? dataread.GetECGChannels(ECGData) : new int[0][];
        nrows = dataread.rows;
        nchannels = dataread.channels;
        //  ax.PerformAxisIntervalAnalysis(ECGChan, nrows, nchannels, (int)samplingrate);

        Queue<List<Float>> ArrangedLeads = ArrangeAllLeads(ECGChan, samplingrate * 2.5f);
        Queue<List<Float>> ArrangedLeads8Sec = ArrangeAllLeads(ECGChan, samplingrate * 10);

        float printXIncrement = GetPrintXIncrement();
        P_Axis = 0;
        PR_Interval = 0;
        QRS_Interval = 0;
        QTc = 0;
        R_Axis = 0;
        R_To_R_Interval = 0;
        T_Axis = 0;
        QT_Interval = 0;
        ST_Interval = 0;

        HR = 0;

        PrintProcedure(ArrangedLeads, ArrangedLeads8Sec, printXIncrement,
                dtFromGP, PatientID, GPName, comments, Age, Gender, Weight, Height, Diabetis, Bp, String.valueOf(HR), spName, PatientName,
                SpO2, P_Axis, R_Axis, T_Axis, PR_Interval, QRS_Interval, QTc, QT_Interval, ST_Interval, R_To_R_Interval,
                FVC, FEV1, PEF, symtoms, Analysis, Interpretation,
                LowPassFilt, HighPassFilt, MedianFilt, Pacemaker, Heading, GPPhone, GPFax, DeviceID, DeviceType);
        return myDocument;
    }


    private void DrawpatientInfo(Canvas g, Paint p, float currX, float currY, String PatientName, String spName, String gpName,
                                 String Gender, String Age, Date dtFromGP, String HR, String PastHistory, String Bp, String SpO2, double pAxis,
                                 double QRsAxis, double TAxis, double PR, double QRS, double QTc, double QT_Interval, double ST_Interval, double R_To_R_Interval,
                                 double FVC, double FEV1, double PEF, String Comments, Image img, String symtoms, String Analysis, String Interpretation,
                                 String GPPhone, String GPFax, String DeviceID, String DeviceType, String PatientID) {
        p.setColor(Color.BLACK);
        p.setAntiAlias(true);

        float width = blankDocuWidthInInches - 0.1016f;
        //.2 is the heading height.
        currY += 5.0f;
        // RectangleF rf = new RectangleF(currX, currY, width, .5f);//.7 is patient rectangle height.


        p.setTypeface(Typeface.DEFAULT);
        p.setTextSize(9f);


        g.drawText("Patient Details", currX, currY, p);

        g.drawText("Physicians Details", currX + (width * 10.0f), currY, p);

        g.drawText("Response", currX + (width * 19.0f), currY, p);


        Rect bounds = new Rect();
        p.getTextBounds("Patient", 0, "Patient".length(), bounds);
        int height = bounds.height();
        int width_one = bounds.width();

        //  g.DrawLine(p, new PointF(currX, currY + smallheadingsize.Height), new PointF(currX + width, currY + smallheadingsize.Height));
        g.drawLine(currX, currY + height, currX + width, currY + height, p);


        Paint p_small = new Paint();
        p_small.setTextSize(8f);

        Paint p_small1 = new Paint();
        p_small1.setTextSize(8f);
        p_small1.setTypeface(Typeface.DEFAULT);


        bounds = new Rect();
        p_small.getTextBounds("Name", 0, "Name".length(), bounds);
        height = bounds.height();
        width_one = bounds.width();


        StringBuilder sb = new StringBuilder();
        float dist = 0.762f;


        if (PatientName.isEmpty()) {
            sb.append("", 0, 41);
        } else {
            if (PatientName.length() > 41) {
                PatientName = PatientName.substring(0, 41);
            }
            sb.append(PatientName);
        }
        String name = String.valueOf(sb).trim() + " ( " + Age + " yrs  " + Gender + " )";


        g.drawText(name, currX, currY + height, p_small);


        g.drawText("Dr: " + gpName, currX + (width * 10.16f), currY + height, p_small);

        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());


        String time = df.format(new Date());
        g.drawText("Time:   " + time, currX + (width * 19.05f), currY + height, p_small);


        //  float timeStringWidth = g.MeasureString("Time:   " + time, smallFont).Width;

        bounds = new Rect();
        p_small.getTextBounds("Time:   " + time, 0, ("Time:   " + time).length(), bounds);
        height = bounds.height();
        float timeStringWidth = bounds.width();


        // g.DrawString("Date:   " + DateTime.Now.ToShortDateString(), smallFont, brush, currX + (width * .75f) + timeStringWidth + .05f,  //0.5f distance betwwen two string
        //       currY + smallheadingsize.Height);

        Date date = new Date();

        String d = date.toString();

        g.drawText("Date:   " + d, currX + (width * 19.05f) + timeStringWidth + 1.27f, currY + height, p_small);

        // g.DrawString("ID: " + PatientID, smallFont, brush, currX, currY + smallheadingsize.Height + smallstringsize.Height);

        g.drawText("ID: " + PatientID, currX, currY + height + height, p_small);

        //   g.DrawString("Ph No: " + GPPhone, smallFont, brush, currX + (width * 0.4f), currY + smallheadingsize.Height + smallstringsize.Height);

        g.drawText("Ph No: " + GPPhone, currX + (width * 10.16f), currY + height + height, p_small);


        //   g.DrawString("FAX: " + GPFax, smallFont, brush, currX + (width * 0.55f), currY + smallheadingsize.Height + smallstringsize.Height);

        g.drawText("FAX: " + GPFax, currX + (width * 13.97f), currY + height + height, p_small);


        //g.DrawString("Remote ID:", smallFont, brush, currX, currY + smallheadingsize.Height + 2 * smallstringsize.Height);
        //g.DrawString("Age:   " + Age + " Yrs", smallFont, brush, currX + (width * .25f), currY + smallheadingsize.Height + 2 * smallstringsize.Height);
        //g.DrawString("Refering:", smallFont, brush, currX + (width * .5f), currY + smallheadingsize.Height + 2 * smallstringsize.Height);

        //g.DrawString("Reason for monitoring:", smallFont, brush, currX, currY + smallheadingsize.Height + 3 * smallstringsize.Height);
        //g.DrawString("Phone:", smallFont, brush, currX + (width * .5f), currY + smallheadingsize.Height + 3 * smallstringsize.Height);

        currY += 12.0f;
        // RectangleF rf2 = new RectangleF(currX, currY, width, 0.7f);//0.7 is height of rectangle.

        //  g.DrawString("Comments", smallheadingFont, brush, rf2.X, rf2.Y);

        g.drawText("Comments", currX, currY, p);

        DrawMeasurement(currX, currY +5.0f, p, g, width, HR, PastHistory, Bp, SpO2, pAxis, QRsAxis, TAxis, PR,
                QRS, QTc, QT_Interval, ST_Interval, R_To_R_Interval, FVC, FEV1, PEF, img);

        // g.DrawLine(p, new PointF(currX, currY + smallheadingsize.Height), new PointF(currX + width * .73f, currY + smallheadingsize.Height));

        g.drawLine(currX, currY + height, currX + width * 18.54f, currY + height, p);

        currY += 1.27f;


        p.setTextSize(8f);
        p.setTypeface(Typeface.DEFAULT);

        //float xshift = g.MeasureString("Interpretation: ", smallboldFont).Width;

        bounds = new Rect();
        p.getTextBounds("Interpretation: ", 0, "Interpretation: ".length(), bounds);

        float xshift = bounds.width();


        // float height = g.MeasureString("Interpretation: ", smallboldFont).Height;


        bounds = new Rect();
        p_small.getTextBounds("Interpretation: ", 0, "Interpretation: ".length(), bounds);
        float Height = bounds.height();


        //  g.DrawString("Symtoms: ", smallboldFont, brush, currX, currY + smallheadingsize.Height);

        g.drawText("Symtoms: ", currX, currY + height, p);


        //  RectangleF rfSym = new RectangleF(currX + xshift, currY + smallheadingsize.Height, width * 0.73f - xshift, height);
        // g.DrawString(symtoms, smallFont, brush, rfSym);

        g.drawText(symtoms, currX + xshift, currY + height, p_small);

        //g.DrawString("Analysis: ", smallboldFont, brush, currX, currY + smallheadingsize.Height + 1 * smallstringsize.Height + dist);

        g.drawText("Analysis: ", currX, currY + height + height + dist, p);

        // RectangleF rfAna = new RectangleF(currX + xshift, currY + smallheadingsize.Height + 1 * smallstringsize.Height + dist,
        //         width * 0.73f - xshift, height);


        //  g.DrawString(Analysis, smallFont, brush, rfAna);

        g.drawText(Analysis, (currX + xshift), currY + height + height + dist, p_small);

        //g.DrawString("Interpretation: ", smallboldFont, brush, currX, currY + smallheadingsize.Height + 2 * smallstringsize.Height + 2 * dist);
        g.drawText("Interpretation: ", currX, currY + height + 2 * height + 2 * dist, p_small);


     /*   RectangleF rfIntPre = new RectangleF(currX + xshift, currY + smallheadingsize.Height + 2 * smallstringsize.Height + 2 * dist,
                width * 0.73f - xshift, height + 5);*/

        // smallFont = new Font("Arial", 5.5f);
        p.setTextSize(5.5f);
        //  g.DrawString(Interpretation, smallFont, brush, rfIntPre);

        g.drawText(Interpretation, currX + xshift, currY + height + 2 * height + 2 * dist, p);


        //smallFont = new Font("Arial", 8f);

        p.setTextSize(8f);

        // g.DrawString("Remarks: ", smallboldFont, brush, currX, currY + smallheadingsize.Height + 3 * smallstringsize.Height + 3 * dist);

        g.drawText("Remarks: ", currX, currY + height + 3 * height + 3 * dist, p);

        //  RectangleF recf = new RectangleF(currX + xshift, currY + smallheadingsize.Height + 3 * smallstringsize.Height + 3 * dist, width * 0.73f - xshift, 0.3f);//0.3 is height for remark rectangle.


        // g.DrawString(Comments, smallFont, brush, recf);

        g.drawText(Comments, currX + xshift, currY + height + 3 * height + 3 * dist, p);

        //g.DrawString("Physician Sign: ", smallFont1, brush, currX + 0.75f * width, currY + .85f);

        g.drawText("Physician Sign: ", currX + 19.0f * width, currY + 22.0f, p);

        //g.DrawLine(p, new PointF(currX + 0.76f * width + g.MeasureString("Physician Signature: ", smallFont).Width, currY + 0.65f + g.MeasureString("Physician Signature: ", smallFont).Height),
        //    new PointF(currX + .95f * width, currY + 0.65f + g.MeasureString("Physician Signature: ", smallFont).Height));

        // RectangleF recPhyName = new RectangleF(currX + 0.75f * width, currY + 1f, 0.9f, g.MeasureString(spName, smallFont).Height);
        // g.DrawString(spName, smallFont, brush, recPhyName);


        g.drawText(spName, currX + 19.05f * width, currY + 1f, p);

        currY += 26.0f;

        //calculate available width for this row
        float avaiWidth = width * 18.54f;
        float divWidth = avaiWidth / 4;

        // string str = "Device Type: " + DeviceType;
        //g.DrawString(str, smallFont1, brush, currX, currY);

        g.drawText("Device Type: " + DeviceType, currX, currY, p_small1);


        //   str = "DeviceID: " + DeviceID;
        // g.DrawString(str, smallFont1, brush, currX + divWidth, currY);

        g.drawText("DeviceID: " + DeviceID, currX + divWidth, currY, p_small1);

        //   DateTime dtGP = DateTime.Now;
        Date dtGP = new Date();


       /* if (!DateTime.TryParse(dtFromGP, out dtGP))
        {
            dtGP = DateTime.Now;
            MessageBox.Show("As the rec date time received from gp is not in correct format the current date and time is being assigned for printing ");
        }*/

        //  str = "Data Recorded Time: " + dtGP.toString();
        // g.DrawString(str, smallFont1, brush, currX + 2 * divWidth, currY);

        g.drawText("Data Recorded Time: " + dtGP.toString(), currX + 2 * divWidth, currY, p_small1);

        //  str = "Data Recorded Date: " + dtGP.ToShortDateString();
        // g.DrawString(str, smallFont1, brush, currX + 3 * divWidth, currY);

        g.drawText("Data Recorded Date: " + dtGP.toString(), currX + 3 * divWidth, currY, p_small1);


    }


    private Queue<List<Float>> ArrangeAllLeads(int[][] ECGChan, float p) {
        BlockingQueue<List<Float>> ArrangedLeads = new LinkedBlockingQueue<List<Float>>();


        for (int j = 0; j < 12; j++) {


            List<Float> lif = new ArrayList<>();
            for (int i = 0; i < p; i++) {
                try {
                    lif.add((float) ECGChan[i][j]);
                } catch (Exception ignored) {


                }
            }
            ArrangedLeads.add(lif);
        }


        return ArrangedLeads;

    }

    private void MakeA4sizeImageOnDisk() {

        mm1 = (mm25 / 25);


        blankDocuWidthInInches = 11.69f;//10.69f;//8.35f;//9.51f;//
        blankDocuHeightInInches = 8.27f;// 7.27f;//7.20f;
        blankDocuResolution = 196f;
        penWidth = 0.004f;
        float dpiX = 2338 / blankDocuWidthInInches;
        DpiY = 1654/blankDocuHeightInInches;
        LeadWidth = (dpiX *mm1 * 60);//mohan 80);// 3.563f;//1.1811f; //30mm //2.78f;
        LeadHeight = (dpiX *mm1 * 30);//1.17f;
        //LongBox = Convert.ToSingle(mm1 * 200);
        LongBox = (dpiX *mm1 * 240);
        // myDocument = new Bitmap(path + blankDocu);
        myDocument = Bitmap.createBitmap(2338, 1654, Bitmap.Config.ARGB_8888);//Bitmap(2000, 1400);
        ActualVerticalResolution = myDocument.getHeight() / blankDocuHeightInInches;
        ActualHorizontalResolution = myDocument.getWidth() / blankDocuWidthInInches;

    }

    private float GetPrintXIncrement() {
        float printXIncrement = 0;

        printXIncrement = (mm25 / samplingrate);
        return printXIncrement;
    }

    private void DrawHeading(Canvas g, Paint p, float currX, float currY, String Heading) {

        p.setAntiAlias(true);
        p.setTextSize(12);
        p.setColor(Color.BLACK);
        Rect bounds = new Rect();
        p.getTextBounds(Heading, 0, Heading.length(), bounds);

        int height = bounds.height();
        int width = bounds.width();

        //  RectangleF rf = new RectangleF(currX, currY, width + 0.5f, .175f);
        g.drawRect(currX, currY, width + 12.7f, 5.0f, p);
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.GRAY);
        g.drawRect(currX, currY, width + 12.7f, 5.0f, p);

        p.setColor(Color.BLACK);
        g.drawText(Heading, currX + 6.0f, currY, p);
    }


    private void DrawMeasurement(float currX, float currY, Paint p, Canvas g, float width, String HR,
                                 String PastHistory, String Bp, String SpO2, double pAxis, double QRsAxis, double TAxis, double PR,
                                 double QRS, double QTc, double QT_Interval, double ST_Interval, double R_To_R_Interval,
                                 double FVC, double FEV1, double PEF, Image signImage) {
        //RectangleF rf = new RectangleF(currX + (width * 0.75f), currY - 0.2f, 2.1f, .75f);//2 width,0.7 height ,-0.15 adjustment

        g.drawRect(currX + (width * 0.75f), currY - 0.2f, 2.1f, .75f, p);

        // g.DrawRectangle(p, rf.X, rf.Y, rf.Width, rf.Height);

        //RectangleF signrect = new RectangleF(currX + (width * 0.75f) + 0.9f, currY + 0.6f, 1.2f, .35f);// 1.2 width 0.35 height,0.8,0.6 adjustment to shift x and y co-ordinate
        //g.DrawRectangle(p, signrect.X, signrect.Y, signrect.Width, signrect.Height);

        //g.DrawImage(signImage, signrect.X + 0.05f, signrect.Y + 0.05f, signrect.Width - 0.05f, signrect.Height - 0.05f);//0.05 adjustment.


        currY += 0.06f;

        float oneThirdRemainingWidth = 2.1f / 3;
        float dividedHt = (.75f - 0.06f) / 6;
        float halfRemainingWidth = 2.1f / 2;


        p.setTextSize(6.3f);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setColor(Color.BLACK);


        Rect bounds = new Rect();
        p.getTextBounds("Measures", 0, "Measures".length(), bounds);
        int height = bounds.height();
        int width_one = bounds.width();

        Paint p2 = new Paint();

        p2.setTextSize(0.04f);

        p2.setColor(Color.WHITE);


        g.drawLine(currX + (width * 0.75f + 1 - (width_one / 2)), currY - .15f, currX + (width * 0.75f + 1 + (width_one / 2)), currY - .2f, p2);


        g.drawText("Measures", currX + (width * 0.75f + 1 - (width_one / 2)), currY - .2f - 0.05f, p2);


        //==

        //for Axis

        p.setTextSize(6.3f);
        g.drawText("HR: ", currX + (width * 0.75f), currY - 0.2f, p);


        p.getTextBounds("HR: ", 0, "HR: ".length(), bounds);
        height = bounds.height();
        width_one = bounds.width();

        g.drawText(HR, currX + (width * 0.75f) + width_one, currY - 0.2f, p);

        g.drawText("BP: ", currX + (width * 0.75f) + oneThirdRemainingWidth, currY - 0.2f, p);


        p.getTextBounds("BP: ", 0, "BP: ".length(), bounds);
        height = bounds.height();
        width_one = bounds.width();


        g.drawText(Bp, currX + (width * 0.75f) + oneThirdRemainingWidth + width_one, currY - 0.2f, p);


        g.drawText("SaO: ", currX + (width * 0.75f) + (2 * oneThirdRemainingWidth), currY - 0.2f, p);


        p.getTextBounds("SaO: ", 0, "SaO: ".length(), bounds);
        height = bounds.height();
        width_one = bounds.width();


        if (!SpO2.isEmpty())


            g.drawText(SpO2 + "%", currX + (width * 0.75f) + (2 * oneThirdRemainingWidth) + width_one, currY - 0.2f, p);

        //for spiromentry


        g.drawText("FVC: ", currX + (width * 0.75f), (currY - 0.2f) + dividedHt, p);


        p.getTextBounds("FVC: ", 0, "FVC: ".length(), bounds);
        height = bounds.height();
        width_one = bounds.width();


        g.drawText(String.valueOf(FVC), currX + (width * 0.75f) + width_one, (currY - 0.2f) + dividedHt, p);


        g.drawText("FEV1: ", currX + (width * 0.75f) + oneThirdRemainingWidth, (currY - 0.2f) + dividedHt, p);


        p.getTextBounds("FEV1: ", 0, "FEV1: ".length(), bounds);
        height = bounds.height();
        width_one = bounds.width();


        g.drawText(String.valueOf(FEV1), currX + (width * 0.75f) + oneThirdRemainingWidth + width_one, (currY - 0.2f) + dividedHt, p);


        g.drawText("PEF: ", currX + (width * 0.75f) + (2 * oneThirdRemainingWidth), (currY - 0.2f) + dividedHt, p);


        p.getTextBounds("PEF: ", 0, "PEF: ".length(), bounds);
        height = bounds.height();
        width_one = bounds.width();


        g.drawText(String.valueOf(PEF), currX + (width * 0.75f) + (2 * oneThirdRemainingWidth) + width_one, (currY - 0.2f) + dividedHt, p);

        //for axis


        g.drawText("Axis (in deg)", currX + (width * 0.75f), (currY - 0.2f) + dividedHt * 2, p);


        g.drawText("P: ", currX + (width * 0.75f), (currY - 0.2f) + (dividedHt * 3), p);


        p.getTextBounds("P: ", 0, "P: ".length(), bounds);
        height = bounds.height();
        width_one = bounds.width();


        g.drawText(String.valueOf(pAxis), currX + (width * 0.75f) + width_one, (currY - 0.2f) + (dividedHt * 3), p);


        g.drawText("QRs: ", currX + (width * 0.75f), (currY - 0.2f) + (dividedHt * 4), p);


        p.getTextBounds("QRs: ", 0, "QRs: ".length(), bounds);
        height = bounds.height();
        width_one = bounds.width();


        g.drawText(String.valueOf(QRsAxis), currX + (width * 0.75f) + width_one, (currY - 0.2f) + (dividedHt * 4), p);


        g.drawText("T: ", currX + (width * 0.75f), (currY - 0.2f) + (dividedHt * 5), p);


        p.getTextBounds("T: ", 0, "T: ".length(), bounds);
        height = bounds.height();
        width_one = bounds.width();


        g.drawText(String.valueOf(TAxis), currX + (width * 0.75f) + width_one, (currY - 0.2f) + (dividedHt * 5), p);

        //for Interval

        /*g.DrawString("Interval (in sec)", f, b, rf.X + oneThirdRemainingWidth, rf.Y + dividedHt * 2);

        g.DrawString("PR: ", f, b, rf.X + oneThirdRemainingWidth, rf.Y + dividedHt * 3);
        strSize = g.MeasureString("PR: ", f);
        g.DrawString(PR.ToString("F"), frd, b, rf.X + oneThirdRemainingWidth + strSize.Width, rf.Y + dividedHt * 3);

        g.DrawString("QRS: ", f, b, rf.X + oneThirdRemainingWidth, rf.Y + dividedHt * 4);
        strSize = g.MeasureString("QRS: ", f);
        g.DrawString(QRS.ToString("F"), frd, b, rf.X + oneThirdRemainingWidth + strSize.Width, rf.Y + dividedHt * 4);


        g.DrawString("QTc: ", f, b, rf.X + oneThirdRemainingWidth, rf.Y + dividedHt * 5);
        strSize = g.MeasureString("QTc: ", f);
        g.DrawString(QTc.ToString("F"), frd, b, rf.X + oneThirdRemainingWidth + strSize.Width, rf.Y + dividedHt * 5);



        g.DrawString("ST: ", f, b, rf.X + (2 * oneThirdRemainingWidth), rf.Y + dividedHt * 3);
        strSize = g.MeasureString("ST: ", f);
        g.DrawString(ST_Interval.ToString("F"), frd, b, rf.X + (2 * oneThirdRemainingWidth) + strSize.Width, rf.Y + dividedHt * 3);

        g.DrawString("QT: ", f, b, rf.X + (2 * oneThirdRemainingWidth), rf.Y + dividedHt * 4);
        strSize = g.MeasureString("QT: ", f);
        g.DrawString(QT_Interval.ToString("F"), frd, b, rf.X + (2 * oneThirdRemainingWidth) + strSize.Width, rf.Y + dividedHt * 4);


        g.DrawString("R-R: ", f, b, rf.X + (2 * oneThirdRemainingWidth), rf.Y + dividedHt * 5);
        strSize = g.MeasureString("R-R: ", f);
        g.DrawString(R_To_R_Interval.ToString("F"), frd, b, rf.X + (2 * oneThirdRemainingWidth) + strSize.Width, rf.Y + dividedHt * 5);



        b.Dispose();
        b = null;
        f.Dispose();
        f = null;*/
    }


    protected void PrintProcedure(Queue<List<Float>> ArrangedLeads, Queue<List<Float>> ArrangedLeads8Sec, float xIncrement,
                                  Date recDTatGP, String PatientID, String GPName, String comments
            , String Age, String Gender, String Weight, String Height, String PastHistory, String Bp,
                                  String HR, String SpName, String PatientName, String SpO2, double pAxis, double QRsAxis, double TAxis,
                                  double PR, double QRS, double QTc, double QT_Interval, double ST_Interval, double R_To_R_Interval,
                                  double FVC, double FEV1, double PEF, String symtoms, String Analysis, String Interpretation,
                                  String LowPassFilt, String HighPassFilt, String MedianFilt, String PaceMaker, String Heading, String GPPhone, String GPFax, String DeviceID, String DeviceType) {
        float pixpermmVert;
        float adjustmentValue = 2;

        //pixpermmVert = Convert.ToSingle((ActualVerticalResolution * mm25) / (10 * mm1) * gain);

      //  myDocument.SetResolution(196, 204);
        //pixpermmVert = Convert.ToSingle(pixpermmVert * gain);
      //  Graphics g = Graphics.FromImage(myDocument);
        Canvas g =  new Canvas(myDocument);
      //  g.PageUnit = GraphicsUnit.Inch;
      //  g.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;

        //Pen p = new Pen(Color.Black);

        Paint p = new Paint();
        p.setColor(Color.BLACK);

        p.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeWidth(penWidth);

       /* p.LineJoin = System.Drawing.Drawing2D.LineJoin.Round;
        p.SetLineCap(System.Drawing.Drawing2D.LineCap.Round,
                System.Drawing.Drawing2D.LineCap.Round,
                System.Drawing.Drawing2D.DashCap.Round);

        p.Width = penWidth;*/

        float currX = 200.0f;
        float currY = 30.0f;
        DrawOuterRectangle(g, p, currX, currY);
        DrawHeading(g, p, currX, currY, Heading);
        DrawpatientInfo(g, p, currX, currY, PatientName, SpName, GPName, Gender, Age, recDTatGP, HR, PastHistory, Bp, SpO2, pAxis, QRsAxis,
                TAxis, PR, QRS, QTc, QT_Interval, ST_Interval, R_To_R_Interval, FVC, FEV1, PEF, comments, null, symtoms, Analysis, Interpretation,
                GPPhone, GPFax, DeviceID, DeviceType, PatientID);

      /*  // DrawGPInformation(g, p, currX, currY, recDTatGP, recNo, GPName,
        //Age, Gender, Weight, Height,PatientName,SpName);
        currY += 1.9f;//1.75f;
        float smallDist = 0.05f;
        boolean IsGrid = Boolean.parseBoolean(IswithGrid);

         //====
        DrawLead(g, p, currX, currY, "I", ArrangedLeads.remove(), xIncrement, 2.5f, pixpermmVert, IsGrid, gain, sweep);
        currY += LeadHeight + smallDist;
        DrawLead(g, p, currX, currY, "II", ArrangedLeads.remove(), xIncrement, 3.7f, pixpermmVert, IsGrid, gain, sweep);
        currY += LeadHeight + smallDist;
        DrawLead(g, p, currX, currY, "III", ArrangedLeads.remove(), xIncrement, 4.9f, pixpermmVert, IsGrid, gain, sweep);


        currY = 1.9f;//1.75f;
        currX += LeadWidth;
        currX += smallDist;
        DrawLead(g, p, currX, currY, "aVR", ArrangedLeads.remove(), xIncrement, 2.5f, pixpermmVert, IsGrid, gain, sweep);
        currY += LeadHeight + smallDist;
        DrawLead(g, p, currX, currY, "aVL", ArrangedLeads.remove(), xIncrement, 3.7f, pixpermmVert, IsGrid, gain, sweep);
        currY += LeadHeight + smallDist;
        DrawLead(g, p, currX, currY, "aVF", ArrangedLeads.remove(), xIncrement, 4.9f, pixpermmVert, IsGrid, gain, sweep);


        currY = 1.9f;//1.75f;
        currX += LeadWidth + smallDist;
        DrawLead(g, p, currX, currY, "V1", ArrangedLeads.remove(), xIncrement, 2.5f, pixpermmVert, IsGrid, gain, sweep);
        currY += LeadHeight + smallDist;
        DrawLead(g, p, currX, currY, "V2", ArrangedLeads.remove(), xIncrement, 3.7f, pixpermmVert, IsGrid, gain, sweep);
        currY += LeadHeight + smallDist;
        DrawLead(g, p, currX, currY, "V3", ArrangedLeads.remove(), xIncrement, 4.9f, pixpermmVert, IsGrid, gain, sweep);


        currY = 1.9f;//1.75f;
        currX += LeadWidth + smallDist;
        DrawLead(g, p, currX, currY, "V4", ArrangedLeads.remove(), xIncrement, 2.5f, pixpermmVert, IsGrid, gain, sweep);
        currY += LeadHeight + smallDist;
        DrawLead(g, p, currX, currY, "V5", ArrangedLeads.remove(), xIncrement, 3.7f, pixpermmVert, IsGrid, gain, sweep);
        currY += LeadHeight + smallDist;
        DrawLead(g, p, currX, currY, "V6", ArrangedLeads.remove(), xIncrement, 4.9f, pixpermmVert, IsGrid, gain, sweep);
        currX = 0.05f;
        currY += LeadHeight;


        List<Float> LongLead = ArrangedLeads8Sec.remove();//this is lead 1
        LongLead.clear();
        LongLead = null;
        LongLead = ArrangedLeads8Sec.remove();//this is lead 2
        DrawLongLead(g, p, currX, currY + smallDist, LongLead, xIncrement, 6.3f, pixpermmVert, IsGrid, gain, sweep,
                LowPassFilt, HighPassFilt, MedianFilt, PaceMaker);
        currX += LongBox;
        //DrawAxis(g, p, currX, currY, HR, PastHistory, Bp, SpO2, pAxis, QRsAxis, TAxis, PR, QRS, QTc, FVC, FEV1,PEF);
        currX = 0.05f;
        currY += LeadHeight;
        //DrawComments(g, p, currX, currY, comments);
        currX += LongBox;
        //DrawSignature(g, p, currX, currY,SpName,signImage);

        // DrawPulse(g, p, gain, sweep);

*/
    }


}
