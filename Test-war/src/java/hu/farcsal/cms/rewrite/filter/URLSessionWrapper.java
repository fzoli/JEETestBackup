package hu.farcsal.cms.rewrite.filter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 *
 * @author zoli
 */
public class URLSessionWrapper extends HttpServletResponseWrapper {

    public URLSessionWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return url;
    }

    @Override
    public String encodeRedirectURL(String url) {
        return url;
    }

    @Override
    public String encodeUrl(String url) {
        return url;
    }

    @Override
    public String encodeURL(String url) {
        return url;
    }

}
