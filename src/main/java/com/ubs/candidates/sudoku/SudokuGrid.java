package com.ubs.candidates.sudoku;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by svd on 26/03/2015.
 */
public class SudokuGrid {
    private static String linePattern = "(\\d?),(\\d?),(\\d?),(\\d?),(\\d?),(\\d?),(\\d?),(\\d?),(\\d?)";
    private static Pattern patLine = Pattern.compile(linePattern);
    private final int[][] grid = new int[9][9];

    {
        for(int[] row : grid)
        Arrays.fill(row, -1);
    }

    /**
     * read single line of textual sudoku
     * @param line representation in format: 1,2,3,4,5,6,7,8,9
     * @param lineNumber zero based line number, represents row in grid
     */
    public final void readLine(String line, int lineNumber) {
        Matcher matcher = patLine.matcher(line);
        if(!matcher.matches()) {
            throw new SudokuException("Line " + line + " does not containe 9 digits");
        }

        for(int i=0;i<9;i++) {
            String cell = matcher.group(i + 1);
            if(cell.trim().length()>0) {
                int value = Integer.parseInt(cell);
                //paranoid check, regex prevents this. but code more robust
                if(value<1 || value>9) throw new SudokuException("line: " + lineNumber+1 + " column " + i+1 + " invalid: " + value);
                setValue(lineNumber, i, value);
            }
        }
    }

    /**
     * reads a whole sudoku grid
     * @param r a buffer containing grid in for 1,2,3,4,5,6,7,8,9\\n1,,3,,4,,2,,\\n... spurious content is ignored
     * @throws IOException
     */
    public final void readBuffer(BufferedReader r) throws IOException {
        String line = null;
        int lc = -1;
        while((line = r.readLine()) !=null && lc<8) {
            lc++;
            //System.out.println(""+lc+": "+line);
            readLine(line, lc);
        }
        assert lc == 8 : lc;
    }

    public int value(int row, int col) {
        return grid[row][col];
    }

    public void setValue(int row, int col, int value) {
        assert value <= 9 && value >= 1 : "Invalid value: " + value;
        grid[row][col] = value;
    }

    public int[] row(int row) {
        return Arrays.copyOf(grid[row], 9);
    }

    public boolean validate() {
        //TODO: set is reused since method extraction noisy and increased readability
        //TODO: but should be refactored
        Set<Integer> area = new HashSet<Integer>(9);

        for (int row = 0; row < 9; row++) {
            area.clear();
            for (int col = 0; col < 9; col++) {
                int v = value(row, col);
                if (v == -1) continue;
                if (area.contains(v)) {
                    //System.out.println("rows: " + row + ":" + col + " = " + v);
                    return false;
                }
                area.add(v);
            }
        }


        for(int col = 0;col<9;col++) {
            area.clear();
            for(int row = 0;row<9;row++) {
                int v = value(row, col);
                if(v==-1) continue;
                if(area.contains(v)) {
                    //System.out.println("cols "+row+":"+col+" = "+v);
                    return false;
                }
                area.add(v);
            }
        }

        //block rows
        for(int br=0;br<9;br+=3) {
            for(int bc=0;bc<9;bc+=3) {
                area.clear();
                for(int row=0;row<3;row++) {
                    for(int col=0;col<3;col++) {
                        int v=value(row,col);
                        if(v==-1) continue;
                        if(area.contains(v)) {
                            //System.out.println("blocks: "+row+":"+col+" = "+v);
                            return false;
                        }
                        area.add(v);
                    }
                }
            }
        }

        return true;
    }

    public boolean solved() {
        if(validate()) {
            for(int row=0;row<9;row++) {
                int sum = 0;
                for(int col=0;col<9;col++) {
                    sum += value(row, col);
                }
                if(sum!=45) return false;
            }
        }

        return validate();
    }

    public void loadFile(File file) throws IOException {
        assert  file.exists() && file.isFile() && file.canRead();
        BufferedReader br = new BufferedReader(new FileReader(file));
        readBuffer(br);
    }

    public String status() {
        StringBuilder sb = new StringBuilder();
        if(validate()) {
            sb.append("VALID");
            if(solved()) {
                sb.append(" (SOLVED)");
            }
        } else {
            sb.append("INVALID");
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        for( String fileName :  args) {
            File file = new File(fileName);
            assert  file.exists() && file.isFile() && file.canRead();
            SudokuGrid grid = new SudokuGrid();
            try {
                grid.loadFile(file);
                System.out.println(""+file+": " + grid.status());
            } catch (IOException e) {
                throw new IllegalStateException("bad file " + file, e);
            }
        }
    }


    public static class SudokuException extends RuntimeException {
        public SudokuException(String message) {
            super(message);
        }

        public SudokuException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
