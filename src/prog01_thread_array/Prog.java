package prog01_thread_array;

import java.util.Arrays;
import java.util.Scanner;
import java.util.zip.CRC32;

public class Prog {
    private final Scanner sc;
    private int[] arr;
    private int arrSum;
    private long arrChecksum;

    public Prog() {
        sc = new Scanner(System.in);
    }

    //=========== ОСНОВНОЙ МЕТОД ==========================================================================
    public void go() {
        printOnStart();

        int length = inputArrLength();
        arr = new int[length];

        Thread threadArrLoad = new Thread(this::arrLoad);
        threadArrLoad.start();
        //ждать завершение работы потока загрузки массива
        try {
            threadArrLoad.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
        printArr();

        //
        Thread threadSum = new Thread(this::calcArrSum);
        Thread threadCRC = new Thread(this::calcArrCRC);

        threadSum.start();
        threadCRC.start();

        try {
            threadSum.join();
            threadCRC.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Сумма всех чисел: " + arrSum);
        System.out.printf("Контрольная сумма CRC32: %d (dec),  %s (hex)  \n", arrChecksum, Long.toHexString(arrChecksum) );
        System.out.println("Проверка CRC32: https://emn178.github.io/online-tools/crc32.html");
        System.out.println("               (выбрать input type = HEX (НЕ TEXT!) и ввести каждый int массива )");
        System.out.println("               (в виде HEX числа, напр. 76 = 0000004c                           )");
    }
    //======================================================================================================

    private void arrLoad() {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = random(1000);
        }
    }

    private void calcArrSum() {
        int sum = 0;
        for (int val : arr) {
            sum += val;
        }
        arrSum = sum;
    }

    //для рассчета контрольной суммы CRC32 в джаве есть специальный класс java.util.zip.CRC32
    //но рассчитать crc32 для массива int напрямую нельзя- нужно каждый int из массива перевести в byte[]
    //что бы преобразовать сразу весь массив int[] -> byte[] можно использовать ByteBuffer
    //но тут я преобразую отдельно каждый int массива через свой метод, что бы нагрузить поток
    private void calcArrCRC() {
        CRC32 crc32 = new CRC32();
        crc32.reset();
        byte[] bytes;
        for (int val : arr) {
            bytes = intToBytes(val);
            crc32.update(bytes);
        }
        arrChecksum = crc32.getValue();
    }

    public static byte[] intToBytes(int val) {
        return new byte[] {
                (byte)(val >> 24),
                (byte)(val >> 16),
                (byte)(val >> 8),
                (byte)val };
    }


    private void printArr() {
        System.out.println("Массив случайных чисел");
        System.out.println("----------------------");

        if(arr.length < 20) {
            System.out.println(Arrays.toString(arr));
        }
        else {
            for (int i = 1; i <= arr.length; i++) {
                System.out.printf("%-4d    ", arr[i - 1]);
                if(i % 10 == 0 || i == arr.length)
                {
                    System.out.println();
                }

            }
        }
        System.out.println();
    }

    private int inputArrLength() {
        int length = 0;
        while (length < 2) {
            System.out.print("Введите размер массива (>1): ");
            length = sc.nextInt();
        }
        return length;
    }


    public static int random(int min, int max) {
        if(min > max) {
            int tmp = min;
            min = max;
            max = tmp;
        }
        return (int) (Math.random() * (max - min)) + min;
    }

    public static int random(int max) {
        return random(0, max);
    }

    private void printOnStart() {
        String[] task = { "(21/1) Задание 1",
                          "При старте приложения запускаются три потока. Первый поток заполняет массив случайными числами. ",
                          "Два других потока ожидают заполнения. Когда массив заполнен оба потока запускаются.",
                          "Первый поток находит сумму элементов массива,",
                          "второй поток минимальное значение в массиве(CRC).",
                          "Полученный массив, сумма и среднеарифметическое возвращаются в метод main,",
                          "где должны быть отображены."};

        System.out.println("------------------------------------------------------------------------------------------------");
        for (String s : task) {
            System.out.println(s);
        }
        System.out.println("------------------------------------------------------------------------------------------------");
    }


}
