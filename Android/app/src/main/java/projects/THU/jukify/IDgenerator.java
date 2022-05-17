package projects.THU.jukify;

import java.util.Random;

/**
 * Class responsible for userId generation
 */
public class IDgenerator implements IDgeneratorInterface {

    /**
     * Alphabet
     */
    private static char[] BASE62CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    /**
     * Random
     */
    private static Random rand = new Random();

    /**
     * Getsr random string for given length
     * @param length Length of the string
     * @return User ID String
     */
    public static String getBase62(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i=0; i<length; ++i)
            sb.append(BASE62CHARS[rand.nextInt(62)]);
        return sb.toString();
    }
}
