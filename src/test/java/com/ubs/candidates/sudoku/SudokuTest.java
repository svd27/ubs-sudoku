package com.ubs.candidates.sudoku;

import com.ubs.candidates.sudoku.SudokuGrid;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import static org.junit.Assert.*;


/**
 * Created by svd on 26/03/2015.
 */
public class SudokuTest {
    @Test
    public void testLines() {
        String full = "1,2,3,4,5,6,7,8,9";
        String sparse = ",2,3,,5,6,,,9";
        int[] sparseRep = {-1, 2, 3, -1, 5, 6, -1, -1, 9};

        SudokuGrid grid = new SudokuGrid();
        grid.readLine(full, 0);
        grid.readLine(sparse, 1);
        for(int i=0;i<9;i++) assertEquals(i+1, grid.value(0, i));
        assertArrayEquals(sparseRep, grid.row(1));
    }

    @Test(expected = SudokuGrid.SudokuException.class)
    public void testBadValue() {
        String line = "11,22,33,44,55,66,77,88,99";
        SudokuGrid grid = new SudokuGrid();
        grid.readLine(line, 0);
    }

    @Test(expected = SudokuGrid.SudokuException.class)
    public void testBadLength() {
        String line = "1,2,3,4,6,7,8,9";
        SudokuGrid grid = new SudokuGrid();
        grid.readLine(line, 0);
    }

    @Test
    public void testFile() throws IOException {
        String full = "1,2,3,4,5,6,7,8,9";
        String sparse = ",2,3,,5,6,,,9";
        String empty = ",,,,,,,,";
        int[] sparseRep = {-1, 2, 3, -1, 5, 6, -1, -1, 9};

        StringBuffer sb = new StringBuffer();
        sb.append(full).append("\n");
        sb.append(sparse).append("\n");
        for(int i=2; i<=9;i++) {
            sb.append(empty).append("\n");
        }

        SudokuGrid grid = new SudokuGrid();
        grid.readBuffer(new BufferedReader(new StringReader(sb.toString())));
        for(int i=0;i<9;i++) assertEquals(i+1, grid.value(0, i));
        assertArrayEquals(sparseRep, grid.row(1));
    }

    private String goodSparse =
            "2,3,9,1,,,7,,\n" +
            ",8,,,,7,6,,1\n" +
            ",,7,5,,4,,,\n" +
            "4,,,,6,,3,9,\n" +
            ",7,2,,,,8,1,\n" +
            ",6,3,,1,,,,7\n"+
            ",,,6,,1,5,,\n" +
            "7,,6,3,,,,2,\n" +
            ",,5,,,2,9,6,3\n";

    private String goodSolved =
            "2,3,9,1,8,6,7,4,5\n"+
            "5,8,4,9,2,7,6,3,1\n"+
            "6,1,7,5,3,4,2,8,9\n"+
            "4,5,1,7,6,8,3,9,2\n"+
            "9,7,2,4,5,3,8,1,6\n"+
            "8,6,3,2,1,9,4,5,7\n"+
            "3,2,8,6,9,1,5,7,4\n"+
            "7,9,6,3,4,5,1,2,8\n"+
            "1,4,5,8,7,2,9,6,3\n";
    private String badRows;
    private String badCols;
    private String badBlocks;

    private static String EMPTY = ",,,,,,,,\n";

    @Before
    public void setup() {
        StringBuilder sb = new StringBuilder();
        sb.append("4,,5,,3,,5,,\n");
        for(int row = 1;row<9;row++) sb.append(EMPTY);
        badRows = sb.toString();
        sb = new StringBuilder();
        sb.append(",,,,5,,,,\n");
        sb.append(",,,,5,,,,\n");
        for(int row = 2;row<9;row++) sb.append(EMPTY);
        badCols = sb.toString();
        sb = new StringBuilder();
        sb.append(",2,,,,,,,\n");
        sb.append(",,2,,,,,,\n");
        for(int row = 2;row<9;row++) sb.append(EMPTY);
        badBlocks = sb.toString();
    }

    @Test
    public void testFull() throws IOException {
        SudokuGrid grid = new SudokuGrid();
        grid.readBuffer(new BufferedReader(new StringReader(badRows)));
        assertFalse(grid.validate());
        assertFalse(grid.solved());
        grid = new SudokuGrid();
        grid.readBuffer(new BufferedReader(new StringReader(badCols)));
        assertFalse(grid.validate());
        assertFalse(grid.solved());
        grid = new SudokuGrid();
        grid.readBuffer(new BufferedReader(new StringReader(badBlocks)));
        assertFalse(grid.validate());
        assertFalse(grid.solved());
        grid = new SudokuGrid();
        grid.readBuffer(new BufferedReader(new StringReader(goodSparse)));
        assertTrue(grid.validate());
        assertFalse(grid.solved());
        grid = new SudokuGrid();
        grid.readBuffer(new BufferedReader(new StringReader(goodSolved)));
        assertTrue(grid.validate());
        assertTrue(grid.solved());
    }

    @Test
    public void testFiles() throws IOException {
        SudokuGrid grid = new SudokuGrid();
        URL url1 = getClass().getResource("/test1.csv");
        grid.loadFile(new File(url1.getFile()));
        assertTrue(grid.validate());
        assertFalse(grid.solved());

        assertEquals("VALID", grid.status().trim());

        grid = new SudokuGrid();
        url1 = getClass().getResource("/test2.csv");
        grid.loadFile(new File(url1.getFile()));
        assertTrue(grid.validate());
        assertTrue(grid.solved());
        assertEquals("VALID (SOLVED)", grid.status().trim());
    }
}

