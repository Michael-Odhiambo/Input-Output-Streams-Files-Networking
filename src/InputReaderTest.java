
import java.io.*;

public class InputReaderTest {

    public static void main( String[] args ) {
        InputReader reader = new InputReader( System.in );

        int firstInteger;
        float firstFloat;
        double firstDouble;

        try {
            firstInteger = reader.getInt();
            System.out.println( "The first integer is : " + firstInteger );
            firstFloat = reader.getFloat();
            System.out.println( "The first float is: " + firstFloat );
            firstDouble = reader.getDouble();
            System.out.println( "The first double is: " + firstDouble );

        }
        catch ( IOException e ) {
            System.out.println( e.getMessage() );
        }
    }
}
