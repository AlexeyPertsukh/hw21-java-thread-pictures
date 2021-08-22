package prog02_picture_bmp;

import java.io.IOException;
import java.util.Scanner;

public class Prog {

    private final static String LOCAL_PATCH = "\\src\\prog02_picture_bmp\\pictures\\";
    private final static String FILENAME_BMP = "picture.bmp";

    private final Scanner sc;
    private final Bmp bmp;
    private int filterLevel;
    private final String loadFileName;
    private final String saveFileName;


    public  Prog() {
        sc = new Scanner(System.in);
        bmp = new Bmp();
        loadFileName = ForFile.getFilenameWithAbsolutePatch(LOCAL_PATCH, FILENAME_BMP);
        saveFileName = ForFile.addFileName(loadFileName, "_filtered");
    }

    // ======== ОСНОВНОЙ МЕТОД =======================
    public void go() {
        printOnStart();
        while(true) {
            if(!bmpLoad()) {
                break;
            }

            inputBmpFilterLevel();

            if(bmpFilter()) {       //отфильтровали?
                if(bmpSave()) {     //сохранили?
                    System.out.println("Оригинальный файл: " + loadFileName);
                    System.out.println("Отфильтрованный файл: " + saveFileName);
                }
            }

            if(!isRepeat()) {
                break;
            }
            System.out.println();
        }
    }
    //================================================

    private boolean bmpLoad() {
        try {
            bmp.loadFromFile(loadFileName);
        } catch (IOException ex) {
            System.out.printf("Не удалось загрузить файл: %s \n", loadFileName);
            return false;
        }
        catch (BmpException ex) {
            System.out.println(ex.getMessage() + "  " + loadFileName);
            return false;
        }

        System.out.printf("Файл \"%s\" загружен, параметры BMP: \n", FILENAME_BMP);
        System.out.println(". . . . . . . . . . . . . . . . . . . . .");
        System.out.println(bmp);
        System.out.println();
        return true;
    }

    private boolean bmpSave() {
        try {
            bmp.saveToFile(saveFileName);
            System.out.println("Файл сохранен");
            System.out.println();
            return true;
        } catch (IOException e) {
            System.out.println("Не удалось записать файл " + loadFileName);
            return false;
        }
    }

    private void inputBmpFilterLevel() {
        String message = String.format("Введите уровень фильтрации (%d - %d): ", Bmp.FILTER_MIN, Bmp.FILTER_MAX);
        filterLevel = My.inputInt(sc, message, Bmp.FILTER_MIN, Bmp.FILTER_MAX);
    }

    private boolean isRepeat() {
        char ch = My.inputCharLowerCase(sc,"Провести новую фильтрацию? (Y - да, N - нет): ", 'y','n');
        return ch == 'y';
    }

    private boolean bmpFilter() {
        try {
            bmp.filter(filterLevel);
        }
        catch (BmpException ex) {
            System.out.println(ex.getMessage() + "\n");
            return false;
        }

        return true;
    }

    private void printOnStart() {
        String[] task = { "(21/2) Задание *",
                "На жестком диске в файле размещено изображение(формат BMP).",
                "Нужно прочитать его, спросить у юзера о пороге фильрации.",
                "Например, юзер ввел 201, значит все цветовые насыщенности,",
                "превышающие 201 превращаются в 201(остальные без изменений)."
        };

        System.out.println("************************************************************************************************");
        for (String s : task) {
            System.out.println(s);
        }
        System.out.println("************************************************************************************************");
        System.out.println();
    }

}
