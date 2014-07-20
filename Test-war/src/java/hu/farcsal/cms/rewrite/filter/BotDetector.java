package hu.farcsal.cms.rewrite.filter;

import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author zoli
 */
public class BotDetector {
    
    public static final String DEFAULT_BOT_PATTERN = "bot|crawler|spider";

    private static final String USER_AGENT_HEADER = "User-Agent";

    private static Pattern pattern = Pattern.compile(DEFAULT_BOT_PATTERN);

    /**
     * Sets the regular expression that is used to check whether a User-Agent
     * belongs to a robot/spider/crawler. The pattern does not need to match the
     * whole User-Agent header, it is checked whether the patter is
     * <i>included</i> within the string. Note that the pattern is
     * <b>case sensitive</b> and the header is <b>converted to lower case</b>
     * before the pattern is checked. The default is
     * <code>bot|crawler|spider</code>.
     * @param pattern
     */
    public static void setPattern(String pattern) {
        BotDetector.pattern = Pattern.compile(pattern);
    }
    
    /**
     * Returns <code>true</code> if the botPattern is found in the User-Agent
     * header. Note that the header is converted to lower case before the
     * regular expression is checked.
     *
     * @param request
     * @return
     */
    public static boolean isBot(HttpServletRequest request) {
        String agent = request.getHeader(USER_AGENT_HEADER);
        if (agent != null) {
            return pattern.matcher(agent.toLowerCase()).find();
        }
        return false;
    }
    
}
