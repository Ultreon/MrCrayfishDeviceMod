package com.ultreon.devices.cef;

import java.awt.*;
import java.io.FileOutputStream;

public class BMPFile extends Component {

    //--- Private constants
    private final static int BITMAPFILEHEADER_SIZE = 14;
    private final static int BITMAPINFOHEADER_SIZE = 40;

    //--- Private variable declaration

    //--- Bitmap file header
    private final byte[] bfType = {(byte) 'B', (byte) 'M'};
    private int bfSize = 0;

    //--- Bitmap info header
    private int biWidth = 0;
    private int biHeight = 0;
    private int biSizeImage = 0x030000;

    //--- Bitmap raw data
    private int[] bitmap;

    //--- File section
    private FileOutputStream fo;

    //--- Default constructor
    public BMPFile() {

    }


    public void saveBitmap(String parFilename, int[] imagePix, int parWidth, int parHeight) {

        try {
            fo = new FileOutputStream(parFilename);
            save(imagePix, parWidth, parHeight);
            fo.close();
        } catch (Exception saveEx) {
            saveEx.printStackTrace();
        }

    }


    /*
     * The saveMethod is the main method of the process. This method
     * will call the convertImage method to convert the memory image to
     * a byte array; method writeBitmapFileHeader creates and writes
     * the bitmap file header; writeBitmapInfoHeader creates the
     * information header; and writeBitmap writes the image.
     *
     */
    private void save(int[] imagePix, int parWidth, int parHeight) {

        try {
            convertImage(imagePix, parWidth, parHeight);
            writeBitmapFileHeader();
            writeBitmapInfoHeader();
            writeBitmap();
        } catch (Exception saveEx) {
            saveEx.printStackTrace();
        }
    }


    /*
     * convertImage converts the memory image to the bitmap format (BRG).
     * It also computes some information for the bitmap info header.
     *
     */
    private boolean convertImage(int[] imagePix, int parWidth, int parHeight) {

        int pad;
        bitmap = imagePix;

        pad = (4 - parWidth * 3 % 4) * parHeight;

        if (4 - parWidth * 3 % 4 == 4) pad = 0;

        biSizeImage = parWidth * parHeight * 3 + pad;
        bfSize = biSizeImage + BITMAPFILEHEADER_SIZE + BITMAPINFOHEADER_SIZE;
        biWidth = parWidth;
        biHeight = parHeight;

        return true;
    }

    /*
     * writeBitmap converts the image returned from the pixel grabber to
     * the format required. Remember: scan lines are inverted in
     * a bitmap file!
     *
     * Each scan line must be padded to an even 4-byte boundary.
     */
    private void writeBitmap() {

        int size;
        int value;
        int j;
        int i;
        int rowCount;
        int rowIndex;
        int lastRowIndex;
        int pad;
        int padCount;
        byte[] rgb = new byte[3];


        size = biWidth * biHeight - 1;
        pad = 4 - biWidth * 3 % 4;

        //The following bug correction will cause the bitmap to be unreadable by
        //GIMP.  It must be there for the bitmap to be readable by most other
        //graphics packages.
        if (pad == 4) { // <==== Bug correction
            pad = 0;
        }// <==== Bug correction


        rowCount = 1;
        padCount = 0;
        rowIndex = size - biWidth;
        lastRowIndex = rowIndex;

        try {
            // The following three lines of code are a correction supplied
            // by Alin Arsu, Feb 2003.  The original code set the top-right
            // pixel in the image to black, and also shifted the bottom row
            // of the image by one pixel.
            // The original code was the following two lines:
            // for (j = 0; j < size; j++) {
            //     value = bitmap [rowIndex];
            // This is replaced by the three lines that appear next.
            for (j = 0; j < size + 1; j++) {
                if (j < biWidth) {
                    value = bitmap[rowIndex + 1];
                } else {
                    value = bitmap[rowIndex];
                }

                rgb[0] = (byte) (value & 0xFF);
                rgb[1] = (byte) (value >> 8 & 0xFF);
                rgb[2] = (byte) (value >> 16 & 0xFF);
                fo.write(rgb);
                if (rowCount == biWidth) {
                    padCount += pad;
                    for (i = 1; i <= pad; i++) {
                        fo.write(0x00);
                    }
                    rowCount = 1;
                    rowIndex = lastRowIndex - biWidth;
                    lastRowIndex = rowIndex;
                } else rowCount++;
                rowIndex++;
            }

            //--- Update the size of the file
            bfSize += padCount - pad;
            biSizeImage += padCount - pad;
        } catch (Exception wb) {
            wb.printStackTrace();
        }

    }

    /*
     * writeBitmapFileHeader writes the bitmap file header to the file.
     *
     */
    private void writeBitmapFileHeader() {

        try {
            fo.write(bfType);
            fo.write(intToDWord(bfSize));
            int bfReserved1 = 0;
            fo.write(intToWord(bfReserved1));
            int bfReserved2 = 0;
            fo.write(intToWord(bfReserved2));
            int bfOffBits = BITMAPFILEHEADER_SIZE + BITMAPINFOHEADER_SIZE;
            fo.write(intToDWord(bfOffBits));

        } catch (Exception wbfh) {
            wbfh.printStackTrace();
        }

    }

    /*
     *
     * writeBitmapInfoHeader writes the bitmap information header
     * to the file.
     *
     */

    private void writeBitmapInfoHeader() {

        try {
            fo.write(intToDWord(BITMAPINFOHEADER_SIZE));
            fo.write(intToDWord(biWidth));
            fo.write(intToDWord(biHeight));
            int biPlanes = 1;
            fo.write(intToWord(biPlanes));
            int biBitCount = 24;
            fo.write(intToWord(biBitCount));
            int biCompression = 0;
            fo.write(intToDWord(biCompression));
            fo.write(intToDWord(biSizeImage));
            int biXPelsPerMeter = 0x0;
            fo.write(intToDWord(biXPelsPerMeter));
            int biYPelsPerMeter = 0x0;
            fo.write(intToDWord(biYPelsPerMeter));
            int biClrUsed = 0;
            fo.write(intToDWord(biClrUsed));
            int biClrImportant = 0;
            fo.write(intToDWord(biClrImportant));
        } catch (Exception wbih) {
            wbih.printStackTrace();
        }

    }


    /*
     *
     * intToWord converts an int to a word, where the return
     * value is stored in a 2-byte array.
     *
     */
    private byte[] intToWord(int parValue) {

        byte[] retValue = new byte[2];

        retValue[0] = (byte) (parValue & 0x00FF);
        retValue[1] = (byte) (parValue >> 8 & 0x00FF);

        return retValue;

    }

    /*
     *
     * intToDWord converts an int to a double word, where the return
     * value is stored in a 4-byte array.
     *
     */
    private byte[] intToDWord(int parValue) {

        byte[] retValue = new byte[4];
        retValue[0] = (byte) (parValue & 0x00FF);
        retValue[1] = (byte) (parValue >> 8 & 0x000000FF);
        retValue[2] = (byte) (parValue >> 16 & 0x000000FF);
        retValue[3] = (byte) (parValue >> 24 & 0x000000FF);

        return retValue;

    }

}