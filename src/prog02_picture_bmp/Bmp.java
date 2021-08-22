/*
Структура BMP:
https://clck.ru/V85ip
 */
package prog02_picture_bmp;

import java.io.*;

public class Bmp {

    public final static int FILTER_MIN = 0;
    public final static int FILTER_MAX = 255;

    private final static int SUPPORT_BIT_COUNT = 24;    //поддерживаем только BMP 24 бита на точку (3 байта: Red, Green, Blue)
    private final static int HEADER_SIZE = 54;          //размер заголовка BMP
    private final static int OFFSET_RED = 0;
    private final static int OFFSET_GREEN = 1;
    private final static int OFFSET_BLUE = 2;

    //расположение данных в заголовке BMP
    private final static int IDX_SIZE = 2;
    private final static int IDX_OFFSET_IMAGE_BITS = 10;
    private final static int IDX_WIDTH = 18;
    private final static int IDX_HIGH = 22;
    private final static int IDX_BIT_COUNT = 28;
    private final static int IDX_COMPRESSION = 30;

    private byte[] bytes ;

    private int filterLevel;

    //header bmp
    private String type;    // для BMP должен быть "BM"
    private int size;
    private int width;
    private int high;
    private int bitCount;   //количество бит на один пиксель bmp (битность)
    private int offsetBits; //смещение изображения от начала файла
    private int compression;

    public Bmp() {
    }


    public void loadFromFile(String fileName) throws IOException {
        //считываем файл в массив байтов
        fileToBytes(fileName);

        //обрабатываем массив байт
        loadHeader();
        try {
            validateBmpStruct();
        }
        catch (BmpException ex) {
            clear();
            throw ex;
        }
    }

    public void saveToFile(String fileName) throws IOException {
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(bytes, 0, bytes.length);
            fos.close();
    }

    private void fileToBytes(String fileName) throws IOException
    {
        int length;
        byte[] tmp = new byte[256];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream( );

        InputStream in = new FileInputStream(fileName);

        while( (length = in.read( tmp )) >= 0 )
        {
            byteArrayOutputStream.write( tmp, 0, length );
        }

        bytes =  byteArrayOutputStream.toByteArray( );
        in.close();
    }

    private void clear() {
        bytes = null;
        type =null;
        offsetBits = 0;
        width = 0;
        high =  0;
        bitCount = 0;
        compression = 0;
    }

    private void validateBmpStruct() {
        if(bytes.length < HEADER_SIZE || !type.equals("BM")) {
            throw new BmpException("Invalid BMP structure. This is not a BMP file");
        }
        if(bitCount != SUPPORT_BIT_COUNT) {
            throw new BmpException("Invalid BMP structure. This bitness is not supported: " + bitCount);
        }
    }

    public void validateFilter(int filterLevel) {
        if(filterLevel < FILTER_MIN || filterLevel > FILTER_MAX) {
            String message = String.format("Invalid value for filtering level (valid values %d - %d): %d", FILTER_MIN, FILTER_MAX, filterLevel);
            throw new BmpException(message);
        }

        if(isEmpty()) {
            throw new BmpException("BMP file not loaded");
        }
    }

    private void loadHeader() {
        if(bytes.length > HEADER_SIZE) {
            type = String.format("%c%c",bytes[0],bytes[1]);
            size = bytesToInt(bytes, IDX_SIZE, 4);
            offsetBits = bytesToInt(bytes, IDX_OFFSET_IMAGE_BITS, 4);
            width = bytesToInt(bytes, IDX_WIDTH, 4);
            high =  bytesToInt(bytes, IDX_HIGH, 4);
            bitCount = bytesToInt(bytes, IDX_BIT_COUNT, 2);
            compression = bytesToInt(bytes, IDX_COMPRESSION, 4);
        }
        validateBmpStruct();
    }

    public void filter(int filterLevel ) {

        validateFilter(filterLevel);

        this.filterLevel = filterLevel;
        filterWithThread();
    }

    //фильтрация потоками
    private void filterWithThread() {

        Thread threadFilterColorRed = new Thread(this::filterColorRed);
        Thread threadFilterColorGreen = new Thread(this::filterColorGreen);
        Thread threadFilterColorBlue = new Thread(this::filterColorBlue);

        threadFilterColorRed.start();
        threadFilterColorGreen.start();
        threadFilterColorBlue.start();

        try {
            threadFilterColorRed.join();
            threadFilterColorGreen.join();
            threadFilterColorBlue.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Фильтрация по конкретному цвету, цвет задается смещением относительно
    //начала последовательности расположения цветовых байтов в массиве : Red(0), Green(1), Blue(2), Red(0), Green(1), Blue(3)...
    //Например, offsetColor = 2  соответствует Blue
    private void filterColor(int offsetColor) {
        byte byteFilterLevel = (byte)filterLevel;
        for (int i = offsetBits; i <= bytes.length - 3; i += 3) {
            if( Byte.toUnsignedInt(bytes[i + offsetColor]) > filterLevel) {
                bytes[i + offsetColor] = byteFilterLevel;
            }
        }
    }

    private void filterColorRed() {
        filterColor(OFFSET_RED);
    }

    private void filterColorGreen() {
        filterColor(OFFSET_GREEN);
    }

    private void filterColorBlue() {
        filterColor(OFFSET_BLUE);
    }

    public boolean isEmpty() {
        return bytes == null;
    }

    public int getWidth() {
        return width;
    }

    public int getHigh() {
        return high;
    }

    public int getBitCount() {
        return bitCount;
    }

    private int bytesToInt(byte[] bytes, int indexStart, int length) {
        int out = 0;
        for (int i = indexStart + length - 1; i >= indexStart  ; i--) {
            out <<= 8;
            out += Byte.toUnsignedInt(bytes[i]);
        }
        return out;
    }

    public String toString() {
        return "Type: " + type + "\n" +
               "Size: " + size + " bytes \n" +
               "Compression: " + compression + "\n" +
               "BitCount: " + bitCount + " bit \n" +
               "Resolution: " + width + "*" + high +" px \n" +
               "OffsetBits: " + offsetBits;
    }

}
