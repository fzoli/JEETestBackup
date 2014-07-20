package hu.farcsal.cms.rewrite.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.omnifaces.filter.HttpFilter;

/**
 *
 * @author zoli
 */
abstract class AbstractHttpFilter extends HttpFilter {

    protected boolean isEnabled(HttpServletRequest request, HttpSession session) {
        return true;
    }
    
    protected abstract void doFilterIfEnabled(HttpServletRequest request, HttpServletResponse response, HttpSession session, FilterChain chain) throws ServletException, IOException;
    
    @Override
    public final void doFilter(HttpServletRequest request, HttpServletResponse response, HttpSession session, FilterChain chain) throws ServletException, IOException {
        if (isEnabled(request, session)) {
            doFilterIfEnabled(request, response, session, chain);
        }
        else {
            chain.doFilter(request, response);
        }
    }
    
}
