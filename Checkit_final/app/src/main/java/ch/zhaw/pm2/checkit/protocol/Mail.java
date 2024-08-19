package ch.zhaw.pm2.checkit.protocol;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible for the mail address
 */
public class Mail {
    private static Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    /**
     * Validates the mail address
     * @param mailAddress provided mail address from user
     * @return true if mail address matches with regex, else false
     */
    public static boolean validateAddress(String mailAddress){
        Matcher match = VALID_EMAIL_ADDRESS_REGEX.matcher(mailAddress);
        return match.find();
    }
}
