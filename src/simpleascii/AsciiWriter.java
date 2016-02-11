package simpleascii;

import com.sun.istack.internal.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

/**
 *
 * Created by Simon on 2016-01-19.
 *
 * <tt>AsciiWriter</tt> is an Ascii text writer with some simple formatting capabilities.
 * No write to the output file is done until the method {@code performWrite()} is called.
 * After this is called, this <tt>AsciiWriter</tt> instance is closed and <b>can no longer
 * perform any more writes</b>.
 *
 */
public class AsciiWriter {

    private static final String WHITESPACE_CHARACTER = " ";
    private static final String TAB_CHARACTER = "\t";

    private @NotNull String lineSeparator = "\n";

    private boolean isClosed = false;
    private @NotNull StringBuilder outputString = new StringBuilder();
    private @NotNull File outputFile;

    /**
     *
     * Constructs a <tt>AsciiWriter</tt> from a supplied output file.
     *
     * @param outputFile file to write to.
     *
     */
    public AsciiWriter(File outputFile) {
        if (outputFile == null) {
            throw new NullPointerException();
        }
        this.outputFile = outputFile;
    }

    /**
     *
     * Constructs a <tt>AsciiWriter</tt> writing to the file at file path filePath.
     *
     * @param filePath location of output file
     *
     */
    public AsciiWriter(String filePath) {
        this(new File(filePath));
    }

    /**
     *
     * Constructs a <tt>AsciiWriter</tt> writing to the file with the name of the current timestamp
     * formatted as an ISO8601 date-time string prefixed.
     *
     */
    public AsciiWriter() {
        // Replace ':' with some other character since it's an invalid character in a filename in Windows.
        this(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace(':', '_') + ".txt");
    }

    /**
     *
     * Perform actual write to file. If this is not called, nothing will have been written out
     * to the out file. <b>No more calls can be done to this <tt>AsciiWriter</tt> instance after
     * this method is called!</b>
     *
     * @throws IOException if file cannot be written to for some reason.
     *
     */
    public void performWrite() throws IOException {
        if (isClosed()) {
            return;
        }
        // Construct the BufferedWriter with append mode set to false (it will overwrite)
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(outputFile, false));
        fileWriter.write(outputString.toString());
        fileWriter.close();
        this.isClosed = true;
    }

    /**
     *
     * Writes a string to the file.
     *
     * @param string string to write.
     * @return this instance.
     *
     */
    public AsciiWriter write(String string) {
        if (isClosed()) {
            return this;
        }
        outputString.append(string);
        return this;
    }

    /**
     *
     * Writes a newline to the file. The line separator to use can be set using {@code setLineSeparator()}.
     *
     * @return this instance.
     *
     */
    public AsciiWriter newline() {
        write(this.lineSeparator);
        return this;
    }

    /**
     *
     * Writes multiple newlines to the file. The line separator to use can be set using {@code setLineSeparator()}.
     *
     * @param count number of spaces to write. Must be greater than 0.
     * @return this instance.
     *
     */
    public AsciiWriter newlines(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException();
        }
        write(String.join("", Collections.nCopies(count, this.lineSeparator)));
        return this;
    }

    /**
     *
     * Writes a space character ' ' to the file.
     *
     * @return this instance.
     *
     */
    public AsciiWriter space() {
        write(WHITESPACE_CHARACTER);
        return this;
    }

    /**
     *
     * Writes multiple space characters ' ' to the file.
     *
     * @param count number of spaces to write. Must be greater than 0.
     * @return this instance.
     *
     */
    public AsciiWriter spaces(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException();
        }
        write(String.join("", Collections.nCopies(count, WHITESPACE_CHARACTER)));
        return this;
    }

    /**
     *
     * Writes a tab character '\t' to the file.
     *
     * @return this instance.
     *
     */
    public AsciiWriter tab() {
        write(TAB_CHARACTER);
        return this;
    }

    /**
     *
     * Writes multiple tab characters '\t' to the file.
     *
     * @param count number of tabs to write. Must be greater than 0.
     * @return this instance.
     *
     */
    public AsciiWriter tabs(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException();
        }
        write(String.join("", Collections.nCopies(count, TAB_CHARACTER)));
        return this;
    }

    /**
     *
     * Writes an int value to the file.
     *
     * @param intValue value to write.
     * @return this instance.
     *
     */
    public AsciiWriter write(int intValue) {
        write(String.valueOf(intValue));
        return this;
    }

    /**
     *
     * Writes a long value to the file.
     *
     * @param longValue value to write.
     * @return this instance.
     *
     */
    public AsciiWriter write(long longValue) {
        write(String.valueOf(longValue));
        return this;
    }

    /**
     *
     * Writes a double value to the file with a specific number of decimals. If writeZeroDecimals is true, decimalCount
     * is the exact decimal count, otherwise it's the maximum amount of decimals.
     *
     * @param doubleValue value to write.
     * @param decimalCount max number of decimals to include. Must be greater than or equal to 0.
     * @param writeZeroDecimals boolean flag indicating if unnecessary zero decimals should be written or not.
     * @return this instance.
     *
     */
    public AsciiWriter write(Double doubleValue, int decimalCount, boolean writeZeroDecimals) throws IllegalArgumentException {
        if (decimalCount < 0) {
            throw new IllegalArgumentException();
        }

        // See format description here: https://docs.oracle.com/javase/7/docs/api/java/text/DecimalFormat.html
        String formatSpecifier = "#";
        if (decimalCount > 0) {
            String decimalSpecifier = (writeZeroDecimals) ? "0" : "#";
            formatSpecifier += "." + String.join("", Collections.nCopies(decimalCount, decimalSpecifier));
        }
        String formattedDouble = new DecimalFormat(formatSpecifier).format(doubleValue);
        write(formattedDouble);
        return this;
    }

    /**
     *
     * Getter for the line separator to use when calling {@code newline()}.
     *
     * @return the line separator.
     *
     */
    public String getLineSeparator() {
        return lineSeparator;
    }

    /**
     *
     * Setter for the line separator to use when calling {@code newline()}.
     *
     * @param lineSeparator the line separator to use.
     *
     */
    public void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    /**
     *
     * Check if this <tt>AsciiWriter</tt> instance is closed.
     *
     * @return true if this is closed.
     *
     */
    public boolean isClosed() {
        return isClosed;
    }
}
