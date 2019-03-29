package metsl.spiro;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by manishak on 8/4/15.
 */
public class ECGPreviewStrip {
    private String ecgFName;
    private int end_pt = 320;
    private int display_page = 1;

    private static final int L1 = 0;
    private static final int L2 = 1;
    private static final int L3 = 2;
    private static final int AVR = 3;
    private static final int AVL = 4;
    private static final int AVF = 5;
    private static final int V1 = 6;
    private static final int V2 = 7;
    private static final int V3 = 8;
    private static final int V4 = 9;
    private static final int V5 = 10;
    private static final int V6 = 11;
    public String ServerURL;
    public String RECID;
    private Bitmap back_bmp;
    private int cur_x;
    private int prev_x;// , cur_y;//, prev_y, y_offset;
    private int[] p_ecg_y = new int[13];

    Point touch_pt = new Point(0, 0);
    private byte[][] ecg_buff;// = new byte[810][20]; // allocate only the bits to be
    // taken
    // on memory
    private String[] leads = {"  I", "  II", "III", "Avr", "AvL", "Avf", "V1","V2","V3","V4","V5","V6"};
    // variables for display
    boolean plot_ok;
    byte display_mode = 0;
    private int FrameLenght;
    private int SkipCount = 2;

    byte major_version, minor_version;
    private long blit_start_pt = 0;
    private long blit_end_pt = 400;
    boolean start_flag;
    byte d_flg = 0;

    //float[] gain = { 0.5f / 2, 1.0f / 2, 1.5f / 2, 2.0f / 2 }; changed by v sir 9may2016

    private float[] gain = { 0.5f , 1.0f , 1.5f , 2.0f };
    private int gain_val = 1;

    int m_euno_offset;
    int blit_start = 0, blit_end = 4500;
    int ecg_position;
    private int offsetArray[];
    private int _scrWidth, _scrHeight;
    private int readdataperpage = 0;
    private int numberofpages = 0;
    private int LeadConfig = 2;
    private long filelength = 0;
    private int devicetype;
    private SettingsManager _SettingsManager;
    public ECGPreviewStrip(int width, int height) {
        _scrWidth = width;
        _scrHeight = height;
        end_pt = (_scrWidth) - 25; // Total pixels per lead

    }

    public void SetWidthHeight(int width, int height) {
        _scrWidth = width;
        _scrHeight = height;

    }

    public int initPlotter(Context context, String ecgfilename, int devicetype) {
        _SettingsManager = new SettingsManager(context);
        this.devicetype = devicetype;
        switch (devicetype) {
            case 0:
                // r10
                SkipCount = 2;
                LeadConfig = 2;
                FrameLenght = 20;
                break;
            case 1:
                //  For euno-Wi-Fi
                SkipCount = 2;
                LeadConfig = 2;
                FrameLenght = 18;
                break;
            case 2:
                // elr
                SkipCount = 2;
                LeadConfig = 1;
                FrameLenght = 8;
                break;
            case 3:
                // For eUno-USB Ecg
                SkipCount = 1;
                LeadConfig = 2;
                FrameLenght = 18;
                break;

            default:
                break;
        }
        int[] lead1 = new int[12];

        ecgFName = ecgfilename;
        File fconn;
        fconn = new File(ecgFName);
        long totalframes = 0;
        filelength = fconn.length();
        if (fconn.exists()) {
            totalframes = filelength / FrameLenght;
        }
        fconn = null;


        double actualplottingframe = totalframes / SkipCount;
        numberofpages = (int) Math.ceil(actualplottingframe / end_pt);
        System.out.println("Number of Pages : " + numberofpages);
        readdataperpage = end_pt * SkipCount * FrameLenght;
        System.out.println("Read Data Per Page : " + readdataperpage);
        blit_end_pt = readdataperpage;
        return numberofpages;
    }

	/*
	 * private Byte BitStatus(byte number, byte mask) { return (byte) (number &
	 * (1 << mask)); }
	 */

    private void readFile() {
        int length = end_pt;
        if ((blit_end_pt - blit_start_pt) < readdataperpage) {
            length = (int) ((blit_end_pt - blit_start_pt) / FrameLenght) / SkipCount;
        }
        ecg_buff = new byte[length][20];
        // device_type = devType;
        // display_pos = displaypos;
        File fconn = null;
        try {
            // String f_name = "file:///" + ecgFName;
            // Dialog.alert(f_name);

            fconn = new File(ecgFName);
            // If no exception is thrown, then the URI is valid, but the file
            // may or may not exist.

            if (fconn.exists()) {

                // resetSlate(); // clear the view and redraw
                InputStream ip = new BufferedInputStream(new FileInputStream(
                        fconn));// openFileInput(fconn.getAbsolutePath());
                ip.skip(blit_start_pt);
                int readcount = (FrameLenght * SkipCount);
                byte[] ecg_byte = new byte[readcount];
                int endread = readdataperpage / readcount;
                // read the ecg data from file and display on screen
                for (int i = 0; i < endread; i++) {
                    // read 5 frames and take only 1 frame effectively
                    // taking 1 in 5
                    if (ip.read(ecg_byte, 0, readcount) == readcount) {
                        System.arraycopy(ecg_byte, 0, ecg_buff[i], 0, FrameLenght);
                    }
                }

                ecg_byte = null;
                ip.close();
                fconn = null;
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();

        }

        finally {
            if (fconn != null) {
                fconn = null;
            }
        }
    }

    /*
     * Reset slate
     */
    private void resetSlate() {
        // reset plot points
        cur_x = 0;
        prev_x = 0;
        for (short i = 0; i < 12; i++) {
            p_ecg_y[i] = 0;
        }
        CreateGrid(); // draw grid afresh
    }

    private void CreateGrid() {
        if (back_bmp == null) {
            back_bmp = Bitmap.createBitmap(_scrWidth, _scrHeight,
                    Bitmap.Config.ARGB_8888);// (800, 600);
            offsetArray = new int[13];
            offsetArray[0] = _scrHeight /3;
            offsetArray[1] = offsetArray[0]+ _scrHeight /3;

        }
        Canvas grp = new Canvas(back_bmp);
        grp.drawColor(Color.WHITE);

        Paint gridcolor = new Paint(Paint.ANTI_ALIAS_FLAG);

        gridcolor.setColor(Color.rgb(255, 229, 229));
        MinorGrid(back_bmp.getWidth(), back_bmp.getHeight(), grp, gridcolor);

        gridcolor.setColor(Color.parseColor("#F5B5C5"));
        MajorGrid(back_bmp.getWidth(), back_bmp.getHeight(), grp, gridcolor);

        gridcolor.setColor(Color.parseColor("#8A2BE2"));

        gridcolor.setTextSize(15.0f);
        grp.drawText(leads[_SettingsManager.first_lead], 0, offsetArray[0], gridcolor);
        grp.drawText(leads[_SettingsManager.second_lead], 0, offsetArray[1], gridcolor);

        // draw standardisation in
        gridcolor.setColor(Color.RED);
        gridcolor.setStrokeWidth(2);

        grp.drawLine(5, 140, 10, 140, gridcolor);
        grp.drawLine(20, 140, 25, 140, gridcolor);
        grp.drawLine(10, 140, 10, 140 - 25 * gain[gain_val] * 4, gridcolor);
        grp.drawLine(20, 140, 20, 140 - 25 * gain[gain_val] * 4, gridcolor);
        grp.drawLine(10, 140 - 25 * gain[gain_val] * 4, 20,
                140 - 25 * gain[gain_val] * 4, gridcolor);
        grp.drawBitmap(back_bmp, 0, 0, null);
        cur_x += 25;
        System.arraycopy(offsetArray, 0, p_ecg_y, 0, offsetArray.length);
        prev_x = cur_x;
    }

    /*
     * Draw Minor grid
     */
    private void MinorGrid(int width, int height, Canvas g, Paint gridcolor) {
        for (int x = 0; x < width; x += 10) {
            // if (x % 5 == 0)
            {
                g.drawLine(x, 0, x, height, gridcolor);
            }
        }

        for (int y = 0; y < height; y += 10) {
            g.drawLine(0, y, width, y, gridcolor);
        }

    }

    /*
     * Draw Major Grid
     */
    private void MajorGrid(int width, int height, Canvas g, Paint gridcolor) {
        // for large squares
        for (int x = 0; x < width; x += 50) {
            g.drawLine(x, 0, x, height, gridcolor);
        }

        for (int y = 0; y < height; y += 50) {
            g.drawLine(0, y, width, y, gridcolor);
        }
    }

    private void BlitECG() {
        Canvas g = new Canvas(back_bmp);
        int[] ecg_data = new int[13];

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        p.setStrokeWidth(1);

        p.setAntiAlias(true);

        p.setColor(Color.BLACK);

        p.setFilterBitmap(true);

        Paint lead = new Paint(Paint.ANTI_ALIAS_FLAG);

        lead.setStrokeWidth(1);

        lead.setAntiAlias(true);

        lead.setColor(Color.RED);

        lead.setFilterBitmap(true);

        // plot ecg here
        int length = end_pt;
        if ((blit_end_pt - blit_start_pt) < readdataperpage) {
            length = (int) ((blit_end_pt - blit_start_pt) / FrameLenght) / SkipCount;
        }
        for (int i = 0; i < length; i++) {
            switch (devicetype) {
                case 0:
                    ecg_data[L2] = (int) (((long) (ecg_buff[i][2] << 7)
                            + ecg_buff[i][3] - 3000));
                    ecg_data[L3] = (int) (((long) (ecg_buff[i][4] << 7)
                            + ecg_buff[i][5] - 3000));
                    ecg_data[V1] = (int) (((long) (ecg_buff[i][6] << 7)
                            + ecg_buff[i][7] - 3000));
                    ecg_data[V2] = (int) (((long) (ecg_buff[i][8] << 7)
                            + ecg_buff[i][9] - 3000));
                    ecg_data[V3] = (int) (((long) (ecg_buff[i][10] << 7)
                            + ecg_buff[i][11] - 3000));
                    ecg_data[V4] = (int) (((long) (ecg_buff[i][12] << 7)
                            + ecg_buff[i][13] - 3000));
                    ecg_data[V5] = (int) (((long) (ecg_buff[i][14] << 7)
                            + ecg_buff[i][15] - 3000));
                    ecg_data[V6] = (int) (((long) (ecg_buff[i][16] << 7)
                            + ecg_buff[i][17] - 3000));

                    ecg_data[L1] = (ecg_data[L2] - ecg_data[L3]);
                    ecg_data[AVF] = ((ecg_data[L2] + ecg_data[L3]) / 2);// 1.732F ;
                    ecg_data[AVL] = ((ecg_data[L1] + -ecg_data[L3]) / 2);// 1.732F ;
                    ecg_data[AVR] = ((-ecg_data[L1] + -ecg_data[L2]) / 2);// 1.732F ;
                    ecg_data[12] = (ecg_buff[i][18] & 0xFF) - 90;
                    break;
                case 1:
                    ecg_data[L2] = (int) (((long) (ecg_buff[i][2] << 7)
                            + ecg_buff[i][3] - 3000));
                    ecg_data[L3] = (int) (((long) (ecg_buff[i][4] << 7)
                            + ecg_buff[i][5] - 3000));
                    ecg_data[V1] = (int) (((long) (ecg_buff[i][6] << 7)
                            + ecg_buff[i][7] - 3000));
                    ecg_data[V2] = (int) (((long) (ecg_buff[i][8] << 7)
                            + ecg_buff[i][9] - 3000));
                    ecg_data[V3] = (int) (((long) (ecg_buff[i][10] << 7)
                            + ecg_buff[i][11] - 3000));
                    ecg_data[V4] = (int) (((long) (ecg_buff[i][12] << 7)
                            + ecg_buff[i][13] - 3000));
                    ecg_data[V5] = (int) (((long) (ecg_buff[i][14] << 7)
                            + ecg_buff[i][15] - 3000));
                    ecg_data[V6] = (int) (((long) (ecg_buff[i][16] << 7)
                            + ecg_buff[i][17] - 3000));

                    ecg_data[L1] = (ecg_data[L2] - ecg_data[L3]);
                    ecg_data[AVF] = ((ecg_data[L2] + ecg_data[L3]) / 2);// 1.732F ;
                    ecg_data[AVL] = ((ecg_data[L1] + -ecg_data[L3]) / 2);// 1.732F ;
                    ecg_data[AVR] = ((-ecg_data[L1] + -ecg_data[L2]) / 2);// 1.732F ;
                    ecg_data[12] = (ecg_buff[i][18] & 0xFF) - 90;
                    break;
                case 2:
                    ecg_data[L2] = (int) (((long) (ecg_buff[i][2] << 7)
                            + ecg_buff[i][3] - 3000));
                    ecg_data[L3] = (int) (((long) (ecg_buff[i][4] << 7)
                            + ecg_buff[i][5] - 3000));
                    ecg_data[V1] = (int) (((long) (ecg_buff[i][6] << 7)
                            + ecg_buff[i][7] - 3000));

                    ecg_data[L1] = (ecg_data[L2] - ecg_data[L3]);
                    ecg_data[AVF] = ((ecg_data[L2] + ecg_data[L3]) / 2);// 1.732F ;
                    ecg_data[AVL] = ((ecg_data[L1] + -ecg_data[L3]) / 2);// 1.732F ;
                    ecg_data[AVR] = ((-ecg_data[L1] + -ecg_data[L2]) / 2);// 1.732F ;
                    ecg_data[12] = (ecg_buff[i][18] & 0xFF) - 90;
                    break;
                case 3:
                    ecg_data[L2] = (int) (((long) (ecg_buff[i][2] << 7)
                            + ecg_buff[i][3] - 3000));
                    ecg_data[L3] = (int) (((long) (ecg_buff[i][4] << 7)
                            + ecg_buff[i][5] - 3000));
                    ecg_data[V1] = (int) (((long) (ecg_buff[i][6] << 7)
                            + ecg_buff[i][7] - 3000));
                    ecg_data[V2] = (int) (((long) (ecg_buff[i][8] << 7)
                            + ecg_buff[i][9] - 3000));
                    ecg_data[V3] = (int) (((long) (ecg_buff[i][10] << 7)
                            + ecg_buff[i][11] - 3000));
                    ecg_data[V4] = (int) (((long) (ecg_buff[i][12] << 7)
                            + ecg_buff[i][13] - 3000));
                    ecg_data[V5] = (int) (((long) (ecg_buff[i][14] << 7)
                            + ecg_buff[i][15] - 3000));
                    ecg_data[V6] = (int) (((long) (ecg_buff[i][16] << 7)
                            + ecg_buff[i][17] - 3000));

                    ecg_data[L1] = (ecg_data[L2] - ecg_data[L3]);
                    ecg_data[AVF] = ((ecg_data[L2] + ecg_data[L3]) / 2);// 1.732F ;
                    ecg_data[AVL] = ((ecg_data[L1] + -ecg_data[L3]) / 2);// 1.732F ;
                    ecg_data[AVR] = ((-ecg_data[L1] + -ecg_data[L2]) / 2);// 1.732F ;
                    ecg_data[12] = (ecg_buff[i][18] & 0xFF) - 90;

                    break;
                default:
                    break;
            }


            plotWave(ecg_data, g, p); // draw the wave on the screen
        }

        ecg_data = null;

        p.setTextSize(20.0f);
        p.setColor(Color.RED);

        if (display_page > numberofpages)
            display_page = numberofpages;
        String str_pg = "Page : " + String.valueOf(display_page) + "/"
                + String.valueOf(numberofpages);
        // g.drawText(str_pg, _scrWidth / 2, 20, p);

    }

    public Bitmap GetBlitEcg(int gainpos) {
        gain_val = gainpos;
        // blit_end = readdataperpage;
        resetSlate(); // redraw the bmp again


            readFile();

        BlitECG();
        return back_bmp;

    }

    private void plotWave(int[] in_pt, Canvas g, Paint p) {
        int[] ecg_y = new int[13];

        cur_x++;

        int end = 12;
        switch (LeadConfig) {
            case 0:
                end = 6;
                break;
            case 1:
                end = 7;
                break;
            default:
                break;
        }
        int offset;
        int j =   _SettingsManager.first_lead;

        offset = offsetArray[0];

        ecg_y[j] = (short) (offset - (in_pt[j] * gain[gain_val]) );

       /* if (Lead[j] == 1) {
            g.drawLine(prev_x, p_ecg_y[j], cur_x, ecg_y[j], lead);
        } else {*/
        g.drawLine(prev_x, p_ecg_y[j], cur_x, ecg_y[j], p);
        //   }

        p_ecg_y[j] = ecg_y[j];


        j = _SettingsManager.second_lead;
        offset = offsetArray[1];

        ecg_y[j] = (short) (offset - (in_pt[j] * gain[gain_val]) );
        g.drawLine(prev_x, p_ecg_y[j], cur_x, ecg_y[j], p);

        p_ecg_y[j] = ecg_y[j];

        prev_x = cur_x;
    }

    public Bitmap GetNextPage() {
        if (display_page < numberofpages) {
            ++display_page;
            blit_start_pt += readdataperpage; // end_pt - 5;

            blit_end_pt = blit_start_pt + readdataperpage;
            if (blit_end_pt > filelength)
                blit_end_pt = filelength;
            if (blit_start_pt >= filelength)
                blit_start_pt = (blit_end_pt - (blit_start_pt - readdataperpage));

            resetSlate(); // redraw the bmp again
            try {
                readFile();
            } catch (Exception e) {
                e.printStackTrace();

            }
            BlitECG();
        }
        return back_bmp;

    }

    public Bitmap SetDisplayPage(int pagenumber) {
        display_page = pagenumber;

        blit_end_pt = readdataperpage * pagenumber;

        blit_start_pt = blit_end_pt - readdataperpage;

        if (blit_end_pt > filelength) {
            blit_end_pt = filelength;
            // blit_start_pt = blit_end_pt - (readdataperpage * (numberofpages -
            // 1));
        }

        if (blit_start_pt < 0)
            blit_start_pt = 0;

        resetSlate(); // redraw the bmp again
        try {
            readFile();
        } catch (Exception e) {
            e.printStackTrace();

        }
        BlitECG();

        return back_bmp;
    }

    public int getCurrentPage() {
        return display_page;
    }

    public Bitmap GetPreviousPage() {

        blit_start_pt -= readdataperpage;// 320 + 5;
        if (blit_start_pt < 0)
            blit_start_pt = 0;
        blit_end_pt = blit_start_pt + readdataperpage;

        if (display_page > 1)
            --display_page;

        resetSlate(); // redraw the bmp again
        try {
            readFile();
        } catch (Exception e) {
            e.printStackTrace();

        }
        BlitECG();

        return back_bmp;
    }


}
