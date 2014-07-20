package hu.farcsal.cms.rewrite.filter;

import hu.farcsal.cms.util.Pages;
import hu.farcsal.util.WebConfig;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.activation.MimetypesFileTypeMap;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

/**
 * A filter that caches pages after they are generated.
 */
public class CacheFilter extends AbstractHttpFilter {
    
    /** the cached data */
    private HashMap datacache;
    private HashMap mimecache;
    
    private static final MimetypesFileTypeMap MIME_TYPES = new MimetypesFileTypeMap();
    private static final String MIME_OCTET_STREAM = "application/octet-stream", MIME_XML = "application/xml";
    
    private static final String PARAM_DISABLED = "hu.farcsal.cms.rewrite.DISABLE_CACHE";
    
    private static Boolean disabled;
    
    /**
     * Constructor
     */
    public CacheFilter() {
    }

    @Override
    protected boolean isEnabled(HttpServletRequest request, HttpSession session) {
        return !isDisabled(request) && request.isRequestedSessionIdFromCookie() && !BotDetector.isBot(request);
    }
    
    private boolean isDisabled(HttpServletRequest request) {
        if (disabled != null) return disabled;
        return disabled = WebConfig.isTrue(request.getServletContext(), PARAM_DISABLED);
    }
    
    /**
     * Perform the actual caching.  If a given key is available in the
     * cache, return it immediately.  If not, cache the result using a 
     * CacheResponseWrapper
     * @param req
     * @param res
     * @param session
     * @param chain
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    protected void doFilterIfEnabled(HttpServletRequest req, HttpServletResponse res, HttpSession session, FilterChain chain) throws ServletException, IOException {
        String url = Pages.getRealRequestURI(req, true);
        
//        if (url.contains("javax.faces")) {
//            chain.doFilter(request, response);
//            return;
//        }
        
        // the cache key is the URI + query string
        String key = req.getRequestURI() + "?" + req.getQueryString();
        
        // only cache GET requests that contain cacheable data
        if (req.getMethod().equalsIgnoreCase("get") && isCacheable(key)) {
            // try to retrieve the data from the cache
            byte[] data = (byte[]) datacache.get(key);
            
            // on a cache miss, generate the result normally and add it to the
            // cache
            if (data == null) {
                CacheResponseWrapper crw = new CacheResponseWrapper(res);
                chain.doFilter(req, crw);
                data = crw.getBytes();
                if (data != null) {
                    datacache.put(key, data);
                    mimecache.put(key, getMimeType(url, data));
                    System.out.println("mime type: " + getMimeType(url, data) + " = " + url);
                }
            } 
            
            // if the data was found, use it to generate the result
            if (data != null) {
                String mime = (String) mimecache.get(key);
                if (mime != null) res.setContentType(mime);
                else res.setContentType("text/html");
                res.setContentLength(data.length);
                try {
                    OutputStream os = res.getOutputStream();
                    os.write(data);
                    os.flush();
                    os.close();
                }
                catch(IOException ex) {
                    ;
                }
            }
        } else {
            // generate the data normally if it was not cacheable
            chain.doFilter(req, res);
        }
    }
    
    /**
     * Return whether the given URI + query string is cacheable.  A real
     * implementation should use some kind of policy here.
     */
    private boolean isCacheable(String key) {
        return true;
    }
    
    /**
     * Initialize the cache
     * @param filterConfig
     */
    @Override
    public void init(FilterConfig filterConfig) {
        datacache = new HashMap();
        mimecache = new HashMap();
    }
    /**
     * Destroy the cache
     */
    @Override
    public void destroy() {
        datacache.clear();
        mimecache.clear();
        
        datacache = null;
        mimecache = null;
    }
    
    private static String getMimeType(String url, byte[] data) {
        String type = MIME_TYPES.getContentType(url);
        if (!MIME_OCTET_STREAM.equals(type)) return type;
        try {
            String t = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(data));
            if (t != null && !t.equals(MIME_XML)) return t;
        }
        catch (IOException ex) {
            ;
        }
        if (url.contains(".js")) {
            return "text/javascript";
        }
        if (url.contains(".css")) {
            return "text/css";
        }
        return "text/html";
    }
    
}
