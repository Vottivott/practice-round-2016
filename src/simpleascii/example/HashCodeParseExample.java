package simpleascii.example;

import simpleascii.AsciiReader;
import simpleascii.LineParser;
import simpleascii.Radix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Simon on 2016-01-21.
 *
 * An example on how SimpleAscii could have been used to parse the in-data
 * for the Google Hash Code qualifying round 2015.
 *
 * Input file specification can be found here:
 *     https://hashcode.withgoogle.com/2015/tasks/hashcode2015_qualification_task.pdf
 *
 */

class ServerPos {

    int row;
    int rowSlot;

    public ServerPos(int row, int rowSlot) {
        this.row = row;
        this.rowSlot = rowSlot;
    }
}

class Server {

    int size;
    int capacity;

    public Server(int size, int capacity) {
        this.size = size;
        this.capacity = capacity;
    }

}

public class HashCodeParseExample {

    public static void main(String[] args) {

        AsciiReader reader = new AsciiReader("hash_code_2015.in");

        try {
            reader.performRead();
        } catch (IOException e) {
            System.out.println("Could not read input file!");
        }

        //
        // Read first "header" line
        //
        assert reader.hasNextLine(): "Input file must have header line.";
        LineParser header = reader.nextLine().defaultSectionSplit().assertSectionCount(5);
        List<Integer> headerValues = header.getSectionsAsInts(Radix.Decimal);

        int rowCount = headerValues.get(0);
        int numSlotsPerRow = headerValues.get(1);
        int unavailableSlotCount = headerValues.get(2);
        int requiredPoolCount = headerValues.get(3);
        int serverCount = headerValues.get(4);

        //
        // Get coordinates for unavailable slots
        //
        //noinspection MismatchedQueryAndUpdateOfCollection
        List<ServerPos> unavailableSlots = new ArrayList<>();

        assert reader.hasNextLines(unavailableSlotCount): "Not enough unavailable slots text-lines";
        for(LineParser line : reader.nextLines(unavailableSlotCount)) {
            line.defaultSectionSplit().assertSectionCount(2);
            unavailableSlots.add(new ServerPos(
                    line.getSectionAtIndexAsInt(0, Radix.Decimal),
                    line.getSectionAtIndexAsInt(1, Radix.Decimal)
            ));
        }

        //
        // Get servers
        //
        //noinspection MismatchedQueryAndUpdateOfCollection
        List<Server> servers = new ArrayList<>();

        assert reader.hasNextLines(serverCount): "Not enough server text-lines";
        for(LineParser line : reader.nextLines(serverCount)) {
            line.defaultSectionSplit().assertSectionCount(2);
            servers.add(new Server(
                    line.getSectionAtIndexAsInt(0, Radix.Decimal),
                    line.getSectionAtIndexAsInt(1, Radix.Decimal)
            ));
        }

        System.out.println("Done! Time to perform actual calculations");

    }

}
