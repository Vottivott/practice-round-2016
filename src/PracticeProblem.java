import simpleascii.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

import simpleascii.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class PracticeProblem {

    static class Point {
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int x, y;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Point point = (Point) o;

            if (x != point.x) return false;
            return y == point.y;

        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }
    }


    enum State {
        DONT_PAINT,
        DO_PAINT,
        PAINTED
    }

    static int w, h;
    static State[][] picture;
    static List<Command> commands = new ArrayList<>();
    static List<List<Command>> squareGroupings;
    static List<Command> oneXoneSquares;

    public static void main(String[] args) {
        solve("learn_and_teach.in");
        solve("right_angle.in");
        solve("logo.in");
    }

    private static void resetStaticState(boolean resetCells) {
        w = -1;
        h = -1;
        picture = null;
        commands = new ArrayList<>();
        squareGroupings = new ArrayList<>();
        if (resetCells) {
            oneXoneSquares = new ArrayList<>();
        }
    }

    public static void solve(String filename) {

        resetStaticState(true);
        parseData(filename);

        //
        // Create 1x1 squares
        //

        oneXoneSquares = new ArrayList<>();
        squareGroupings.add(oneXoneSquares);

        for (int r = 0; r < w; r++) {
            for (int c = 0; c < h; c++) {

                // Fill every paint cell with a 1x1 command
                if (picture[c][r] == State.DO_PAINT) {
                    oneXoneSquares.add(new PaintSquare(r, c, 0));
                }

            }
        }

        //
        // FIND square groups
        //

        int size = 1;
        while (true) {

            List<Command> sizeXsizeSquares = new ArrayList<>();
            squareGroupings.add(sizeXsizeSquares);

            List<Command> previousSize = squareGroupings.get(squareGroupings.size() - 2);
            for (Command c: previousSize) {

                boolean possible = true;
                for (int x = c.getX() - size; x <= c.getX() + size; x++) {
                    int yTop = c.getY() - size;
                    int yBot = c.getY() + size;

                    if (x < 0 || x >= w || yTop < 0 || yBot >= h) {
                        possible = false;
                        break ;
                    }

                    if (picture[yBot][x] == State.DONT_PAINT || picture[yTop][x] == State.DONT_PAINT) {
                        possible = false;
                        break;
                    }
                }

                if (!possible) {
                    continue;
                }

                for (int y = c.getY() - size + 1; y <= c.getY() + size - 1; y++) {
                    int xLeft = c.getX() - size;
                    int xRight = c.getX() + size;

                    if (y < 0 || y >= h || xLeft < 0 || xRight >= w) {
                        possible = false;
                        break ;
                    }

                    if (picture[y][xLeft] == State.DONT_PAINT || picture[y][xRight] == State.DONT_PAINT) {
                        possible = false;
                        break;
                    }
                }

                // A sizeXsize square is possible
                if (possible) {
                    sizeXsizeSquares.add(new PaintSquare(c.getX(), c.getY(), size));
                }
            }

            // All possible commandos for size size have been added

            if (sizeXsizeSquares.size() == 0) {
                // All squares have been identified
                break;
            }

            // Try next size
            size += 1;
        }



        fillSquareGroups();
        findAndFillLines();
        // Fill all empty spots
        for (Command cell: oneXoneSquares) {
            if (picture[cell.getY()][cell.getX()] == State.DO_PAINT) {
                cell.paint(picture);
                commands.add(cell);
            }
        }

        // Reset state
        ArrayList<Command> squareLineSolution = new ArrayList<>(commands);
        resetStaticState(false);
        parseData(filename);

        findAndFillLines();
        // Fill all empty spots
        for (Command cell: oneXoneSquares) {
            if (picture[cell.getY()][cell.getX()] == State.DO_PAINT) {
                cell.paint(picture);
                commands.add(cell);
            }
        }

        ArrayList<Command> lineSolution = new ArrayList<>(commands);


        System.out.println("Square line solution: " + squareLineSolution.size());
        System.out.println("Line solution: " + lineSolution.size());

        if (squareLineSolution.size() > lineSolution.size()) {
            commands = squareLineSolution;
        }

        System.out.println("Finally:");
        printPicture();

        writeData(filename + ".out");
    }

    public static boolean linesFinished(List<Command> cells) {
        for (Command cell : cells) {
            if (picture[cell.getY()][cell.getX()] == State.DO_PAINT) {
                if (cell.getY() + 1 < h && picture[cell.getY() + 1][cell.getX()] == State.DO_PAINT) {
                    return false;
                }
                if (cell.getX() + 1 < w && picture[cell.getY()][cell.getX() + 1] == State.DO_PAINT) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void findAndFillLines() {
        do {
            List<PaintLine> possibleLines = new ArrayList<>();

            HashSet<Point> partOfHorizontalLine = new HashSet<>();
            HashSet<Point> partOfVerticalLine = new HashSet<>();

            // Horizontal
            for (Command cell : oneXoneSquares) {

                Point p = new Point(cell.getX(), cell.getY());

                // If this is already a line or not possible ...
                if (picture[p.y][p.x] != State.DO_PAINT || partOfHorizontalLine.contains(p)) {
                    continue; // to the next cell
                }

                Point pRight = new Point(p.x + 1, p.y);

                if (pRight.x >= w) {
                    continue; // to the next cell
                }

                if (picture[pRight.y][pRight.x] == State.DO_PAINT) {

                    // There is AT LEAST a 2x1 line (of p and pRight)
                    partOfHorizontalLine.add(p);
                    partOfHorizontalLine.add(pRight);

                    // Try building the longest possible line
                    int x;
                    for (x = pRight.x + 1; x < w && picture[p.y][x] == State.DO_PAINT; x++) {
                        partOfHorizontalLine.add(new Point(x, p.y));
                    }

                    // Add the horizontal line
                    possibleLines.add(new PaintLine(p.x, p.y, x - 1, p.y));
                }
            }

            // Vertical
            for (Command cell : oneXoneSquares) {

                Point p = new Point(cell.getX(), cell.getY());

                // If this is already a line or not possible...
                if (picture[p.y][p.x] != State.DO_PAINT || partOfVerticalLine.contains(p)) {
                    continue; // to the next cell
                }

                Point pDown = new Point(p.x, p.y + 1);

                if (pDown.y >= h) {
                    continue; // to the next cell
                }

                if (picture[pDown.y][pDown.x] == State.DO_PAINT) {

                    // There is AT LEAST a 1x2 line (of p and pDown)
                    partOfVerticalLine.add(p);
                    partOfVerticalLine.add(pDown);

                    // Try building the longest possible line
                    int y;
                    for (y = pDown.y + 1; y < h && picture[y][p.x] == State.DO_PAINT; y++) {
                        partOfVerticalLine.add(new Point(y, p.x));
                    }

                    // Add the vertical line
                    possibleLines.add(new PaintLine(p.x, p.y, p.x, y - 1));
                }
            }

            //
            // Sort all lines descending, depending on its length
            //
            Collections.sort(possibleLines, (l0, l1) -> l1.getLengthSquared() - l0.getLengthSquared());

            //
            // Fill in complete lines
            //
            fillLines:
            for (PaintLine c : possibleLines) {

                // Check if still possible
                if (c.isVertical()) {
                    int x = c.getX();
                    for (int y = c.getY(); y < Math.sqrt(c.getLengthSquared()); y++) {
                        if (picture[y][x] != State.DO_PAINT) {
                            continue fillLines;
                        }
                    }
                } else {
                    int y = c.getY();
                    for (int x = c.getX(); x < Math.sqrt(c.getLengthSquared()); x++) {
                        if (picture[y][x] != State.DO_PAINT) {
                            continue fillLines;
                        }
                    }
                }

                // Do fill line
                c.paint(picture);
                commands.add(c);

                //System.out.println(" - " + (int) Math.sqrt(c.getLengthSquared()) + 1 + " long line added");
                //printPicture();

            }
        } while (!linesFinished(oneXoneSquares));
    }

    public static void fillSquareGroups() {
        final int smallestSizeToInclude = 3;
        for (int i = squareGroupings.size() - 1; i >= smallestSizeToInclude - 2; i--) {

            List<Command> squaresToFill = squareGroupings.get(i);
            for (int j = 0; j < squaresToFill.size(); j++) {
                //for (int j = squaresToFill.size() - 1; j >= 0; j--) {
                Command c = squaresToFill.get(j);

                // Check if it's still possible to place this one (only corners are enough)
                if (picture[c.getY() - c.getS()][c.getX() + c.getS()] == State.DO_PAINT &&
                        picture[c.getY() + c.getS()][c.getX() + c.getS()] == State.DO_PAINT &&
                        picture[c.getY() + c.getS()][c.getX() - c.getS()] == State.DO_PAINT &&
                        picture[c.getY() - c.getS()][c.getX() - c.getS()] == State.DO_PAINT) {

                    // Paint this square (in picture and add command)
                    commands.add(c);
                    c.paint(picture);

                    //int actualSize = i + 1 + i;
                    //System.out.println(" - " + actualSize + "x" + actualSize + " square added:");
                    //printPicture();

                }

            }

        }
    }

    public static void parseData(String filename) {
        AsciiReader reader = new AsciiReader(filename);
        try {
            reader.performRead();
        } catch (IOException e) {
            System.out.println("Could not read input file!");
            System.exit(1);
        }
        List<Integer> ints = null;
        List<Integer> dimension = reader.nextLine().splitBy(" ").getSectionsAsInts(Radix.Decimal);
        h = dimension.get(0);
        w = dimension.get(1);
        picture = new State[h][w];
        for (int y = 0; y < h; y++) {
            List<State> list = reader.nextLine().splitBy("").getSectionsAs((s) ->
                s.equals("#") ? State.DO_PAINT : State.DONT_PAINT
            );
            for (int x = 0; x < w; x++) {
                picture[y][x] = list.get(x);
            }
        }
    }

    public static void writeData(String filename) {
        AsciiWriter writer = new AsciiWriter(filename);
        writer.write(commands.size());
        for (Command command : commands) {
            writer.newline().write(command.toString());
        }
        try {
            writer.performWrite();
        } catch(IOException e) {
            System.out.println("Could not write to file for some reason!");
            System.exit(1);
        }
    }

    public static void printPicture() {
        StringBuilder s = new StringBuilder();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                s.append(picture[y][x] == State.PAINTED  ? "O" : (picture[y][x] == State.DO_PAINT ? "." : " "));
            }
            s.append("\n");
        }
        System.out.println(s.toString());
    }
}



//
// Shit <code></code>
//

/*
            // Group 1x1 to 3x3
            {
                List<Command> possible3x3Groupings = new ArrayList<>();
                int x = c.getX();
                int y = c.getY();
                for (int i = 0; i < 9; i++) {
                    if (i == 4) continue;
                    int xn = x + (i / 3) * 2 - 1;
                    int yn = y + (i % 3) * 2 - 1;

                    if (!picture[yn][xn]) {
                        continue commandLoop;
                    }

                    // If this line is reached, a 3x3 is possible
                    possible3x3Groupings.add(new PaintSquare(x, y, 1));
                }

                // Replace all
            }
            */