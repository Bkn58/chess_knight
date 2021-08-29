import static org.junit.Assert.*;

public class BoardTest {

    @org.junit.Test
    public void doJump() {
        Board ex = new Board();
        for (int i=0;i<8;i++) {
            System.out.println(ex.ValidIncrement[i][0]+","+ex.ValidIncrement[i][1]);
        }
    }
    @org.junit.Test
    public void PrintAllJumps() {
        Board ex = new Board();
        ex.Jumping[1][0]=3;
        ex.Jumping[1][1]=4;
        ex.numJump = 5;
        ex.PrintAllJumps();
    }

    @org.junit.Test
    public void isCoordinateCorrect() {

        Board ex = new Board();
        ex.BoardState[0][0] = 0;
        ex.BoardState[7][7] = 0;
        ex.BoardState[4][3] = 1;

        boolean res = ex.IsCoordinateCorrect(0,0);
        assertEquals(true,res);

        res = ex.IsCoordinateCorrect(7,7);
        assertEquals(true,res);

        res = ex.IsCoordinateCorrect(4,3);
        assertEquals(false,res);

        res = ex.IsCoordinateCorrect(-1,8);
        assertEquals(false,res);

        res = ex.IsCoordinateCorrect(5,-2);
        assertEquals(false,res);

        res = ex.IsCoordinateCorrect(3,9);
        assertEquals(false,res);

        res = ex.IsCoordinateCorrect(8,4);
        assertEquals(false,res);

    }
}