package hu.farcsal.cms.rewrite.filter;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * A class to wrap a normal ServletResponse so that the result can be
 * cached.
 */
public class CacheResponseWrapper extends HttpServletResponseWrapper {
    
    /** the caching stream */
    private CacheOutputStream outStream;
    
    /** the replacement output stream and writer */
    private ServletOutputStream stream;
    private PrintWriter writer;
    
    /**
     * A class to wrap a ByteArrayOutputStream as a servlet output
     * stream
     */
    class CacheOutputStream extends ServletOutputStream {
        
        /** the byte array output stream */
        private final ByteArrayOutputStream bos;
        
        /**
         * Constructor.
         */
        CacheOutputStream() {
            bos = new ByteArrayOutputStream();
        }
        
        /**
         * Write data to the underlying byte array output stream
         */
        @Override
        public void write(int param) throws IOException {
            bos.write(param);
        }
        
        /**
         * Write data to the underlying byte array output stream
         */
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            bos.write(b, off, len);
        }
        
        /**
         * Get the data we've written as a byte array
         */
        protected byte[] getBytes() {
            return bos.toByteArray();
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            ;
        }
        
    }
    
    /** 
     * Constructor 
     * @param original
     */
    public CacheResponseWrapper(HttpServletResponse original) {
        super(original);
    }
    
    /**
     * Create an instance of a CacheOutputStream to use
     * @return 
     * @throws java.io.IOException
     */
    protected ServletOutputStream createOutputStream() 
        throws IOException
    {
        outStream = new CacheOutputStream();
        return outStream;
    }
    
    /**
     * Get the replacement output stream
     * @return 
     * @throws java.io.IOException
     */
    @Override
    public ServletOutputStream getOutputStream()
        throws IOException 
    {
        if (stream != null) {
            return stream;
        }
        
        // make sure writer has not already been initialized
        if (writer != null) {
            throw new IOException("Writer already in use");
        }
        
        stream = createOutputStream();
        return stream;
    }
    
    /**
     * Get the replacement writer
     * @return 
     * @throws java.io.IOException
     */
    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer != null) {
            return writer;
        }
        
        // make sure output stream has not already been initialized
        if (stream != null) {
            throw new IOException("OutputStream already in use");
        }
        
        writer = new PrintWriter(new OutputStreamWriter(createOutputStream()));
        return writer;
    }

    @Override
    public void addHeader(String name, String value) {
        System.out.println("add header: " + name + " ; " + value);
        super.addHeader(name, value);
    }
    
    /**
     * Get the cached data from this stream
     * @return 
     * @throws java.io.IOException
     */
    protected byte[] getBytes() throws IOException {
        if (outStream != null) {
            return outStream.getBytes();
        }
        
        return null;
    }
    
}
