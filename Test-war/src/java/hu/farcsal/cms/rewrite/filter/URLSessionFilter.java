package hu.farcsal.cms.rewrite.filter;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;

public class URLSessionFilter extends AbstractHttpFilter {

    @Override
    protected boolean isEnabled(HttpServletRequest request, HttpSession session) {
        return request.isRequestedSessionIdFromCookie() || BotDetector.isBot(request);
    }

    @Override
    protected void doFilterIfEnabled(HttpServletRequest request, HttpServletResponse response, HttpSession session, FilterChain chain) throws ServletException, IOException {
        chain.doFilter(request, new URLSessionWrapper(response));
    }

}
