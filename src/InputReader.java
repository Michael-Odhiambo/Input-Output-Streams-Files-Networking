
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * This class represents a reader that can be used to read input expressed in human-readable format.
 * The InputStream from which the input is read from is specified in the constructor. Once created, input
 * should only be read from the input reader because the reader buffers some information internally and
 * that information is only available to the reader.
 */
public class InputReader implements AutoCloseable {

    public final char EOF = ( char ) 0xFFFF;  // Represents an end of file character.
    public final char EOLN = '\n';  // Represents an end of line.


    /**
     * This class represents an end-of-stream exception, which is thrown when an attempt is made to read
     * data after the end of the input stream is reached. It is a checked exception because it is a subclass
     * of IOException. It must be called inside a try...catch block.
     */
    public static class EndOfStreamException extends IOException {
        public EndOfStreamException() {
            super( "Attempt to read past end of stream." );
        }
    }

    /**
     * This class represents a bad data exception, which can occur when the character that is read from the
     * input stream is not of the correct type.
     */
    public static class BadDataException extends IOException {
        public BadDataException( String errorMessage ) {
            super( errorMessage );
        }
    }

    public InputReader( Reader s ) {
        if ( s == null )
            throw new NullPointerException( "Can't create an InputReader for a null stream." );
        if ( s instanceof BufferedReader )
            in = ( BufferedReader )s;
        else
            in = new BufferedReader( s );
    }

    public InputReader( InputStream s ) {
        this( new InputStreamReader( s ) );
    }

    public void close() {
        try {
            in.close();
        }
        catch ( IOException e ) {

        }
    }

    /**
     * Skip over any whitespace characters, including any end of lines. After this method is called, we will
     * be sure that the next character in the input is either and EOF or a non-whitespace character.
     */
    public void skipWhitespaces() throws IOException {
        char ch = lookChar();

        while ( ch != EOF && Character.isWhitespace( ch ) ) {
            readChar();
            ch = lookChar();
        }
    }

    public int getInt() throws IOException {
        return ( int )readInteger( Integer.MIN_VALUE, Integer.MAX_VALUE );

    }

    public double getDouble() throws IOException {
        return readDouble( Double.MIN_VALUE, Double.MAX_VALUE );
    }

    public float getFloat() throws IOException {
        return ( float ) readDouble( Float.MIN_VALUE, Float.MAX_VALUE );
    }






    // **************************************** PRIVATE IMPLEMENTATION DETAILS. ****************************************

    private BufferedReader in;  // The actual source of the input.
    private Matcher integerMatcher;
    private Matcher floatMatcher;
    private final Pattern integerRegex = Pattern.compile( "(\\+|-)?[0-9]+" );
    private final Pattern floatRegex = Pattern.compile( "(\\+|-)?(([0-9]+(\\.[0-9]*)?)|(\\.[0-9]+))((e|E)(\\+|-)?[0-9]+)?" );

    private String buffer = null;  // One line read from the input stream.
    private int pos = 0;  // Position of next char in buffer that has not yet been processed.

    private String readRealString() throws IOException {
        // Read characters from input following the syntax of real number.
        skipWhitespaces();

        if ( lookChar() == EOF )
            return null;
        if ( floatMatcher == null )
            floatMatcher = floatRegex.matcher( buffer );
        floatMatcher.region( pos, buffer.length() );

        if ( floatMatcher.lookingAt() ) {
            String str = floatMatcher.group();
            pos = floatMatcher.end();
            return str;
        }
        else
            return null;
    }

    private double readDouble( double minValue, double maxValue ) throws IOException {
        double x = 0.0;

        while ( true ) {
            String str = readRealString();
            if ( str == null )
                errorMessage( "Floating point number not found in input.", " Real number in the range " + minValue + " to " + maxValue );

            else {
                try {
                    x = Double.parseDouble( str );
                }
                catch ( NumberFormatException e ) {
                    errorMessage( "Illegal floating point input, " + str, "Real number in the range " + minValue + " to " + maxValue );
                }
                if ( Double.isInfinite( x ) )
                    errorMessage( "Floating point input outside of legal range, " + str + ".", " Real number in the range " + minValue + " to " + maxValue );
            }
            break;


        }
        return x;
    }

    private long readInteger( long min, long max ) throws IOException {
        // Read long integer, limited to the specified range.
        long x = 0;

        while ( true ) {
            String s = readIntegerString();
            if ( s == null ) {
                errorMessage( "Integer value not found in input.", "Integer in the range " + min + " to " + max );
            }
            else {
                try {
                    x = Long.parseLong(s);
                } catch (NumberFormatException e) {
                    errorMessage("Illegal integer input, " + s, " Integer in the range " + min + " to " + max);

                }

                if (x < min || x > max) {
                    errorMessage("Integer input outside of legal range, " + s + ".", " Integer in the range " + min + " to " + max);

                }
                break;  // An integer has been successfully read. Break out of the while loop.

            }

        }
        return x;
    }

    private String readIntegerString() throws IOException {
        // Reads characters from input following syntax of integers.
        skipWhitespaces();

        if ( lookChar() == EOF )
            return null;
        if ( integerMatcher == null )
            integerMatcher = integerRegex.matcher( buffer );
        integerMatcher.region( pos, buffer.length() );

        if ( integerMatcher.lookingAt() ) {
            String str = integerMatcher.group();
            pos = integerMatcher.end();
            return str;
        }
        else
            return null;
    }

    private char readChar() throws IOException {
        // Return and discard the next character from input.
        char ch = lookChar();
        if ( ch == EOF )
            throw new EndOfStreamException();
        pos++;
        return ch;
    }

    private char lookChar() throws IOException {
        // Return the next character from input.
        if ( buffer == null || pos > buffer.length() )
            fillBuffer();
        if ( buffer == null )
            return EOF;
        else if ( pos == buffer.length() )
            return '\n';
        else
            return buffer.charAt( pos );

    }

    private void fillBuffer() throws IOException {
        // Wait for the user to type a line and press return.
        buffer = in.readLine();
        pos = 0;
        floatMatcher = null;
        integerMatcher = null;

    }

    private void emptyBuffer() throws IOException {
        // Discard the rest of the current line of input.
        buffer = null;
    }

    private void errorMessage( String message, String expecting ) throws IOException {
        // Report error on input.
        throw new BadDataException( "Error in input: " + message + " Expecting: " + expecting );
    }

}
