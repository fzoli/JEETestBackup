package hu.farcsal.cms.rewrite.transform;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.ocpsoft.rewrite.config.DefaultOperationBuilder;
import org.ocpsoft.rewrite.config.OperationBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.exception.RewriteException;
import static org.ocpsoft.rewrite.servlet.config.Response.withOutputStreamWrappedBy;
import org.ocpsoft.rewrite.servlet.config.response.ResponseStreamWrapper;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.transform.Transformer;
import org.w3c.tidy.Tidy;

/**
 *
 * @author zoli
 */
public class TidyHtmlTransformer implements Transformer, ResponseStreamWrapper {

    private static final Tidy tidy = new Tidy();
    
    static {
        tidy.setForceOutput(true);
        tidy.setTidyMark(true);
        tidy.setHideComments(true);
        tidy.setHideEndTags(true);
        tidy.setSmartIndent(true);
        tidy.setTabsize(4);
        tidy.setSpaces(4);
    }
    
    public static void setConfiguration(String file) {
        File f = new File(file);
        if (f.isFile()) tidy.setConfigurationFromFile(f.getAbsolutePath());
    }
    
    public static OperationBuilder create() {
        return new DefaultOperationBuilder() {
            
            @Override
            public String toString() {
                return TidyHtmlTransformer.class.getCanonicalName() + "#create";
            }

            @Override
            public void perform(Rewrite rwrt, EvaluationContext ec) {
                withOutputStreamWrappedBy(new TidyHtmlTransformer()).perform(rwrt, ec);
            }
            
        };
    }
    
    public final static String STREAM_KEY = TidyHtmlTransformer.class.getName() + "_STREAM";

    private static class TidyOutputStream extends ByteArrayOutputStream {

        private final OutputStream outputStream;
        
        public TidyOutputStream(OutputStream outputStream) {
            this.outputStream = outputStream;
        }
        
        public void finish() throws IOException {
            tidy.parse(new ByteArrayInputStream(toByteArray()), outputStream);
            outputStream.flush();
            outputStream.close();
        }
        
    }
    
    @Override
    public OutputStream wrap(final HttpServletRewrite rewrite, OutputStream outputStream) {
        OutputStream stream = new TidyOutputStream(outputStream);
        rewrite.getRequest().setAttribute(STREAM_KEY, stream);
        return stream;
    }

    @Override
    public void finish(HttpServletRewrite rewrite) {
        try {
            TidyOutputStream stream = (TidyOutputStream) rewrite.getRequest().getAttribute(STREAM_KEY);
            if (stream != null) {
                stream.flush();
                stream.finish();
            }
        }
        catch (IOException e) {
            throw new RewriteException("Could not finish tidy html", e);
        }
    }
    
    @Override
    public void transform(HttpServletRewrite hsr, InputStream in, OutputStream out) throws IOException {
        tidy.parse(in, out);
    }

}
