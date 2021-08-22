package prog02_picture_bmp;

import java.util.Scanner;

//вспомогательные методы для красоты и удобства
public class My {

    private My(){
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int inputInt(Scanner sc, String text, int min, int max){
        while(true) {
            System.out.print(text);
            String cmd = sc.next();

            if(isInteger(cmd)) {
                int num = Integer.parseInt(cmd);
//                if(num < min) {
//                    System.out.println("Число не может быть меньше " + min);
//                }
//                else if(num > max) {
//                    System.out.println("Число не может быть больше " + max);
//                }
                if(num >= min && num <= max) {
                    return num;
                }
            }
        }
    }

    public static int inputInt(Scanner sc, String text){
        return inputInt(sc, text, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static char inputCharLowerCase(Scanner sc, String text, char... arr) {
        while(true) {
            System.out.print(text);
            String cmd = sc.next().toLowerCase();

            if(cmd.length() == 1) {
                char ch = cmd.charAt(0);
                ch = Character.toLowerCase(ch);
                for (char tmp : arr) {
                    tmp = Character.toLowerCase(tmp);
                    if(tmp == ch) {
                        return tmp;
                    }
                }
            }
        }
    }


    private static final String MSG_ENTER_Y = "Введите 'y' для продолжения: ";

    public static void inputCharToContinue(Scanner sc, String msg) {
        if(!msg.isEmpty()) {
            msg += ". ";
        }
        msg += MSG_ENTER_Y;
        My.inputCharLowerCase(sc, msg,'y');
        System.out.println();
    }

    public static void inputCharToContinue(Scanner sc) {
        inputCharToContinue(sc,"");
    }




}
