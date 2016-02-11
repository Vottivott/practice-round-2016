import simpleascii.*;

import java.util.ArrayList;
import java.util.Collections;

import simpleascii.*;

import java.io.IOException;
import java.util.List;

public class PracticeProblem {

    static int w, h;
    static boolean[][] picture;
    static List<Command> commands = new ArrayList<>();

    public static void main(String[] args) {
        parseData("right_angle.in");
        Command cmd = new PaintSquare(15,8,5);
        //cmd.paint(picture);
        printPicture();
        commands.add(cmd);
        commands.add(new EraseCell(1,2));
        commands.add(new PaintLine(1,2,1,3));
        writeData("submission.txt");
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
        picture = new boolean[h][w];
        for (int y = 0; y < h; y++) {
            List<Boolean> list = reader.nextLine().splitBy("").getSectionsAs((s) -> s.equals("#"));
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
        String s = "";
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                s += picture[y][x] ? "O" : ".";
            }
            s += "\n";
        }
        System.out.println(s);
    }
}