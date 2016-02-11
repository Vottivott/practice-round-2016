package simpleascii;

import com.sun.istack.internal.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * Created by Simon Moos on 2016-01-19.
 *
 * <tt>AsciiReader</tt> is an Ascii text reader with some simple line parsing capabilities.
 *
 */
public class AsciiReader implements Iterator<LineParser> {

    private @NotNull File inputFile;

    private boolean isRead = false;
    private List<String> lines;
    private int iteratorIndex = 0;

    /**
     *
     * Constructs a <tt>AsciiReader</tt> reading and parsing from the passed in file. The method
     * {@code performRead()} must be called before any other method call.
     *
     * @param inputFile file to read from.
     *
     */
    public AsciiReader(File inputFile) {
        if (inputFile == null) {
            throw new NullPointerException();
        }
        this.inputFile = inputFile;
    }

    /**
     *
     * Constructs a <tt>AsciiReader</tt> reading and parsing from the file at file path filePath. The method
     * {@code performRead()} must be called before any other method call.
     *
     * @param filePath location of input file
     *
     */
    public AsciiReader(String filePath) {
        this(new File(filePath));
    }

    /**
     *
     * Reads in the file to memory. This is done in a separate function from the constructor to avoid
     * a big try/catch tree.
     *
     * A downside to reading the file into memory all at once (in comparison to streaming) is that
     * there is a limitation on the size of the file, because computer memory. The reason it's done
     * this way is so the user can query for how many rows there are left and other convenient
     * information like that.
     *
     * @throws IOException if the file doesn't exist, is unreadable for whatever reason, or can't close.
     *
     */
    public void performRead() throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(inputFile));

        // Read all lines lazily into a Stream of lines
        Stream<String> lineStream = fileReader.lines();

        // Collect all lines (non-lazily) into the lines list
        this.lines = lineStream.collect(Collectors.toList());

        fileReader.close();
        this.isRead = true;
    }

    /**
     *
     * Returns true if there is a next line to get.
     *
     * @return true if there is a next line.
     *
     */
    public boolean hasNextLine() {
        return hasNextLines(1);
    }

    /**
     *
     * Checks if count number of lines are available.
     *
     * @param count number of lines to check availability for. Must be greater than 0.
     * @return true if count number of lines are available.
     *
     */
    public boolean hasNextLines(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException();
        }
        return isRead() && ((iteratorIndex + count - 1) < this.lines.size());
    }

    /**
     *
     * Returns the <tt>LineParser</tt> for the next line.
     *
     * @return a <tt>LineParser</tt> for the next line if {@code hasNextLine() == true}, else it will return {@code null}.
     *
     */
    public LineParser nextLine() {
        if (!isRead()) {
            return null;
        }
        if (hasNext()) {
            String line = this.lines.get(iteratorIndex);
            iteratorIndex += 1;
            return new LineParser(line);
        } else {
            return null;
        }
    }

    /**
     *
     * Returns a <tt>List</tt> of <tt>LineParser</tt>s for the next {@code count} lines.
     *
     * @param count number of lines to get. Must be greater than 0.
     * @return a <tt>List</tt> of <tt>LineParser</tt>s if {@code hasNextLines(count) == true}, else it will return {@code null}.
     *
     */
    public List<LineParser> nextLines(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException();
        }
        if (hasNextLines(count)) {
            List<String> nextLines = this.lines.subList(iteratorIndex, iteratorIndex + count);
            iteratorIndex += count;
            return nextLines.stream()
                    .map(LineParser::new)
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    /**
     *
     * Returns true if the file has been read and lines can be accessed. If this returns false,
     * the {@code peformRead()} must be called before anything else.
     *
     * @return true if the file has been read.
     *
     */
    public boolean isRead() {
        return isRead;
    }

    /**
     *
     * Implemented for Iterator support. Is equivalent to {@code hasNextLine()}.
     *
     * {@inheritDoc}
     *
     */
    @Override
    public boolean hasNext() {
        return hasNextLine();
    }

    /**
     *
     * Implemented for Iterator support. Is equivalent to {@code nextLine()}.
     *
     * {@inheritDoc}
     *
     */
    @Override
    public LineParser next() {
        return nextLine();
    }
}
