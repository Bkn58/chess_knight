import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Date;

/**
 * @author Bkn58
 * Программа поиска вариантов ходов конем по шахматной доске -
 * необходимо покрыть всю доску, не попадая в одну ячейку дважды.
 * После нахождения варианта выводится строка с найденными ходами в файл и на консоль.
 * В окне рисуется траетория движения коня по клеткам. После этого программа ожидает ввода символа с консоли
 * для продолжения поиска других вариантов.
 */
public class Board  extends JFrame {
    int [][] BoardState = new int[8][8]; // состояние доски: 0 - ячейка свободная, 1 - ячейка занята
    int numJump=0;                       // номер хода (1....64)
    int [][] Jumping = new int [64][2]; // последовательность ходов
//    int [][] ValidIncrement = {{1,2},{1,-2},{-1,2},{-1,-2},{2,1},{2,-1},{-2,1},{-2,-1}}; // возможные дельты координат
    int [][] ValidIncrement = {{2,1},{1,-2},{1,2},{-1,-2},{-1,2},{2,-1},{-2,-1},{-2,1}}; // возможные дельты координат
    long step = 0;                      // количество переборов
    FileWriter oStream;                 // csv-файл chess_result с результатами (перед координатами выводится количество переборов и время поиска в мс.
    long duration;                      // продолжительность одного поиска
    long totalT;                        // общая продолжительность

    void ClearBoard () {
        for (int i=0;i<8;i++) {
            for (int j=0;j<8;j++)
                BoardState[i][j] = 0;
        }
    }

    /**
     * Делает очередной ход конем
     * @param x - координата (строка)
     * @param y - координата (колонка)
     * @return - true - найден очередной валидный ход - найдена свободная клетка, false - не нашлось свободной клетки
     */
    boolean DoJump (int x,int y){

        if (step>80000000) return false;

        BoardState[x][y] = 1; // ячейка занята
        Jumping[numJump][0] = x;
        Jumping[numJump][1] = y;
        numJump++;
        step++;

        if (numJump==64) return true; // финиш
//        if (numJump<=40) PrintAllJumps();
        for (int i=0;i<8;i++){
            int newx = x + ValidIncrement[i][0];
            int newy = y + ValidIncrement[i][1];
            if (IsCoordinateCorrect(newx,newy)){
                if (DoJump(newx,newy)) {return true;}
            }
        }
        // не нашлось новых ходов
        BoardState[x][y] = 0; // ячейка свободна
        numJump--;
        return false;
    }

    /**
     * Запускает процесс поиска вариантов ходов для всех 64 клеток
     * @throws IOException
     */
    public void Start () throws IOException {
        Date curTime;
        long start, totalTime;
        curTime = new Date();
        totalTime = curTime.getTime();
        for (int i=0;i<8;i++){
            for (int j=0;j<8;j++){
                //System.out.println("\n\rStart="+i+","+j);
                numJump = 0;
                step = 0;
                ClearBoard();
                curTime = new Date();
                start = curTime.getTime(); // запоминаем время старта
                if (DoJump (i,j)) {
                    //System.out.println("\nУспешное завершение на "+ step + " ходу !");
                    curTime = new Date();
                    duration = (curTime.getTime() - start); // фиксируем продолжительность поиска
                    PrintAllJumps();
                    this.setTitle("Press Enter in console to continue");
                    System.in.read(); // нажмите Enter для продолжения
                    this.setTitle("Process is GO! Wait....");
                    numJump = 0;
                    step = 0;
                    //return;
                }
            }
        }
        curTime = new Date();
        totalT = curTime.getTime() - totalTime;
        System.out.println("\nКонец ! Общее время="+totalT);
    }

    /**
     * Выводит строку результата на консоль и в файл chess_result
     */
    public void PrintAllJumps() {
        System.out.print("\r\nход="+step+" время="+duration + " ");
        try {
            oStream.append("\n"+step+","+duration);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i =0;i<numJump;i++) {
            System.out.print("("+Jumping[i][0]+Jumping[i][1]+")");
            try {
                oStream.append(","+Jumping[i][0]+Jumping[i][1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.repaint();
        try {
            oStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Проверка координаты на валидность
     * @param x - новая кордината
     * @param y - новая координата
     * @return - true, false
     */
    boolean IsCoordinateCorrect (int x, int y) {

        return (x>=0)&&(x<8)&&(y>=0)&&(y<8)&&(BoardState[x][y]==0);

    }

    /**
     * отрисовка траектории в окне
     * @param g - графический контекст, переданный системой
     */
    public void paint (Graphics g) {
        int scale = 50;
        int shift = scale/2;
        g.clearRect(0,0,400,500);
        g.setColor(Color.BLACK);
        for (int i = 0; i < 8; i++) {
            g.drawLine(0, i * scale+50, 400, i * scale+50);
            g.drawLine(i * scale, 0+50, i * scale, 400+50);
            int ovalX1 = i*scale+shift-2;
            for (int j=0;j<8;j++) {
                int ovalY1 = j*scale+shift-2+50;
                g.fillOval (ovalX1,ovalY1,4,4);
            }
        }

        if (numJump==64) {
            g.setColor(Color.BLUE);
            g.fillOval(Jumping[0][0] * scale + shift - 4, Jumping[0][1] * scale + shift - 4+50, 8, 8);
            g.setColor(Color.RED);
            for (int i = 1; i < numJump; i++) {
                g.fillOval(Jumping[i][0] * scale + shift - 2, Jumping[i][1] * scale + shift - 2+50, 4, 4);
                g.drawLine(Jumping[i - 1][0] * scale + shift, Jumping[i - 1][1] * scale + shift+50, Jumping[i][0] * scale + shift, Jumping[i][1] * scale + shift+50);
            }
            g.fillOval(Jumping[63][0] * scale + shift - 4, Jumping[63][1] * scale + shift - 4+50, 8, 8);
        }
    }

    public static void main (String[] args ){
        Board chess = new Board();

        chess.setSize(400, 460);
        chess.setTitle("Process is GO! Wait....");
        chess.setVisible(true);
        chess.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        chess.setLocation(500,500);

        try {
            chess.oStream = new FileWriter ("chess_result", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            chess.Start();
            chess.setTitle("Finish !");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            chess.oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
