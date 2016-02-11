package simpleascii.example;

import simpleascii.*;

import java.io.IOException;
import java.util.List;

public class Example {

    public static void main(String[] args) {

        AsciiReader reader = new AsciiReader("test.txt");

        try {
            // Perform actual read
            reader.performRead();
        } catch (IOException e) {
            System.out.println("Could not read input file!");
            System.exit(1);
        }


        // Read values
        List<Integer> ints = null;
        if (reader.hasNextLine()) {
            LineParser line = reader.nextLine();
            ints = line.trimWhitespace().splitBy(" ").getSectionsAsInts(Radix.Hexadecimal);
        }

        // Write values
        AsciiWriter writer = new AsciiWriter("test_out.txt");
        writer.write("BEGIN numbers:").newline().tab();
        assert ints != null;
        for (int i: ints) {
            writer.write(i).space();
        }
        writer.newline().write("END");

        try {
            // Perform actual write
            writer.performWrite();
        } catch(IOException e) {
            System.out.println("Could not write to file for some reason!");
            System.exit(1);
        }
    }
}
