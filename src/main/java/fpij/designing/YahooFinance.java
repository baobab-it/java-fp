/** *
 * Excerpted from "Functional Programming in Java",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/vsjava8 for more book information.
 ** */
package fpij.designing;

import java.math.BigDecimal;
import java.net.URL;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Pattern;

public class YahooFinance {

    public static BigDecimal getPrice(final String ticker) {
        try {
            final URL url = new URL("http://ichart.finance.yahoo.com/table.csv?s=" + ticker);

            final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            final String data = reader.lines().skip(1).findFirst().get();
            final String[] dataItems = data.split(",");

            return new BigDecimal(dataItems[dataItems.length - 1]);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static BigDecimal getPrices(final String ticker) {
        final InputStream inputStream = YahooFinance.class
                .getClassLoader()
                .getResourceAsStream("fpij/designing/nasdaq-companylist.csv");

        final Reader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader reader = new BufferedReader(inputStreamReader);

        BigDecimal result = reader.lines()
                .filter(line -> line.startsWith("\"" + ticker + "\"")) // "RESULT"
                // https://stackoverflow.com/questions/29807947/how-to-catch-splitted-string-in-java-stream
                .flatMap(Pattern.compile(",")::splitAsStream) // str -> ...str
                .map(str -> str.replaceAll("^\"|\"$", "")) // remove trim start end "
                .filter(str -> str.startsWith("$")) // find $000.00A
                .map(str -> str.replaceAll("[^\\d\\.]", "")) // get price 000.00
                .map(BigDecimal::new) // str -> BigDecimal
                .findFirst()
                .orElse(BigDecimal.valueOf(0.00));

        return result;
    }

    public static void main(String[] args) {
        System.out.println(
                (new YahooFinance())
                        .getFileFromResources("fpij/designing/nasdaq-companylist.csv")
                        .getAbsolutePath()
        );

        YahooFinance.getPrices("GOOG");
    }

    // get file from classpath, resources folder
    private File getFileFromResources(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }

    }
}
