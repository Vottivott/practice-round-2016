package simpleascii;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * Created by Simon Moos on 2016-01-19.
 *
 * A <tt>LineParser</tt> is a parser for simple one liners.
 *
 */
public class LineParser {

    @NotNull private String line;
    private List<String> sections;

    /**
     *
     * Constructs a <tt>LineParser</tt> for a specific line. This should only be created
     * from the AsciiReader class.
     *
     * @param line the line to parse from.
     *
     */
    protected LineParser(String line) {
        if (line == null) {
            throw new NullPointerException();
        }
        this.line = line;
    }

    /**
     *
     * Trims any whitespace of the ends of the line. This will also invalidate any previous sections,
     * since the original line is changed.
     *
     * @return this instance.
     *
     */
    public LineParser trimWhitespace() {
        line = line.trim();
        invalidatePreviousSections();
        return this;
    }

    /**
     *
     * Splits the line into sections. Each call to this function will split the original line and overwrite
     * previously divided sections; i.e. it will <i>not</i> divide the sections again (subdivision).
     *
     * @param regex regex specifying the delimiter of the line.
     * @return this instance.
     *
     */
    public LineParser splitBy(String regex) {
        this.sections = Arrays.asList(line.split(regex));
        return this;
    }

    /**
     *
     * Removes empty sections. Can be useful since the splitBy function can create empty sections
     * if there are multiple consecutive instances of the delimiter.
     *
     * @return this instance.
     *
     */
    public LineParser removeEmptySections() {
        this.sections = this.sections.stream()
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        return this;
    }

    /**
     *
     * Performs a default section split, i.e.:
     *
     * {@code
     *      lineParser.trimWhitespace().splitBy(" ").removeEmptySections();
     * }
     *
     * @return this instance.
     *
     */
    public LineParser defaultSectionSplit() {
        trimWhitespace().splitBy(" ").removeEmptySections();
        return this;
    }

    /**
     *
     * Returns the number of sections that the line is divided into. If no divisions has yet to be done,
     * this will return 1, i.e. it's only one section since it hasn't been split.
     *
     * @return number of sections that the line is divided into.
     *
      */
    public int getSectionCount() {
        return (sections == null) ? 1 : sections.size();
    }

    /**
     *
     * Assert that the section count is equal to the supplied sectionCount.
     *
     * <b>Note:</b> this requires that the JVM has assertions enabled. This
     * can be done with the "-ae" flag.
     *
     * @param sectionCount section count to assert.
     * @return this instance.
     *
     */
    public LineParser assertSectionCount(int sectionCount) {
        assert sectionCount == getSectionCount(): "assertSectionCount(" + sectionCount + ") is false." +
                "The actual section count is " + getSectionCount();
        return this;
    }

    /**
     *
     * Returns a list of all the sections in the line. If the line hasn't been split, there is only one
     * section that is the complete line.
     *
     * @return all sections of the line.
     *
     */
    public List<String> getSections() {
        createSectionFromLineIfRequired();
        return this.sections;
    }

    /**
     *
     * Returns the sections as a list of anything. The conversion from String sections to sections of T
     * is done through the supplied mappingFunction.
     *
     * @param mappingFunction function that maps strings to instances of T.
     * @param <T> the type that the mappingFunction maps to.
     * @return a list of the sections mapped to type T.
     *
     */
    public <T> List<T> getSectionsAs(Function<String, T> mappingFunction) {
        List<String> sections = getSections();
        return sections.stream()
                .map(mappingFunction)
                .collect(Collectors.toList());
    }

    /**
     *
     * Returns the sections as a list of integers if possible.
     *
     * @param radix number base to use for parsing.
     * @throws NumberFormatException if one or more sections can't be converted into ints.
     * @return a list of the sections as integers.
     *
     */
    public List<Integer> getSectionsAsInts(Radix radix) throws NumberFormatException {
        return getSectionsAs(s -> Integer.parseInt(s, radix.asInt()));
    }

    /**
     *
     * Returns the sections as a list of doubles.
     *
     * @throws NumberFormatException if one or more sections can't be converted into doubles.
     * @return a list of the sections as doubles.
     *
     */
    public List<Double> getSectionsAsDoubles() {
        return getSectionsAs(Double::parseDouble);
    }

    /**
     *
     * Returns the section at the specified index.
     *
     * @param index section index.
     * @throws IndexOutOfBoundsException if (<tt>index &lt; 0 || index &gt;= getSectionCount()</tt>)
     * @return the section at the specified index.
     *
     */
    public String getSectionAtIndex(int index) throws IndexOutOfBoundsException {
        List<String> sections = getSections();
        if (index < 0 || index >= sections.size()) {
            throw new IndexOutOfBoundsException();
        }
        return sections.get(index);
    }

    /**
     *
     * Returns the section at the specified index as type T. The conversion from a String section to a T
     * is done through the supplied mappingFunction.
     *
     * @param index section index.
     * @param mappingFunction function that maps strings to instances of T.
     * @param <T> the type that the mappingFunction maps to.
     * @throws IndexOutOfBoundsException if (<tt>index &lt; 0 || index &gt;= getSectionCount()</tt>)
     * @return the section at the specified index mapped through the mapping function.
     *
     */
    public <T> T getSectionAtIndexAs(int index, Function<String, T> mappingFunction) throws IndexOutOfBoundsException {
        String section = getSectionAtIndex(index);
        return mappingFunction.apply(section);
    }

    /**
     *
     * Returns the section at the specified index as an int if possible.
     *
     * @param index section index.
     * @param radix number base to use for parsing.
     * @throws NumberFormatException if the section can't be converted into an int.
     * @throws IndexOutOfBoundsException if (<tt>index &lt; 0 || index &gt;= getSectionCount()</tt>)
     * @return the section the specified index as an int.
     *
     */
    public int getSectionAtIndexAsInt(int index, Radix radix) throws IndexOutOfBoundsException, NumberFormatException {
        return getSectionAtIndexAs(index, s -> Integer.parseInt(s, radix.asInt()));
    }

    /**
     *
     * Returns the section at the specified index as a double if possible.
     *
     * @param index section index.
     * @throws NumberFormatException if the section can't be converted into a double.
     * @throws IndexOutOfBoundsException if (<tt>index &lt; 0 || index &gt;= getSectionCount()</tt>)
     * @return the section the specified index as a double.
     *
     */
    public double getSectionAtIndexAsDouble(int index) throws IndexOutOfBoundsException, NumberFormatException {
        return getSectionAtIndexAs(index, Double::parseDouble);
    }

    /**
     *
     * If there are no sections, make the whole line a section.
     *
     */
    private void createSectionFromLineIfRequired() {
        if (sections == null) {
            sections = new ArrayList<>();
            sections.add(line);
        }
    }

    /**
     *
     * If changes are made to the original line (like trimming whitespace), the sections will have to
     * be invalidated since they no longer represent sections of the line.
     *
     */
    private void invalidatePreviousSections() {
        sections = null;
    }

    /**
     *
     * {@inheritDoc}
     *
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LineParser that = (LineParser) o;

        return line.equals(that.line) && getSections().equals(that.getSections());
    }

    /**
     *
     * {@inheritDoc}
     *
     */
    @Override
    public int hashCode() {
        int result = line.hashCode();
        result = 31 * result + sections.hashCode();
        return result;
    }
}
