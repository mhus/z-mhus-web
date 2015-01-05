package de.mhus.osgi.tutorial.websampleapp.taglib;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.lib.core.util.MNls;
import de.mhus.osgi.cherry.api.VirtualApplication;
import de.mhus.osgi.tutorial.websampleapp.SampleApp;
import de.mhus.osgi.tutorial.websampleapp.SampleContext;

public class I18nTagHandler extends TagSupport {

	private String key;
	
	public int doStartTag() throws JspException {
        
        try {
            //Get the writer object for output.
            JspWriter out = pageContext.getOut();
            Locale l = pageContext.getRequest().getLocale();
            
            SampleContext app = (SampleContext)pageContext.getRequest().getAttribute(VirtualApplication.CENTRAL_CONTEXT_KEY);
            ResourceNode lres = app.getHost().getResource("/locale_" + l.getLanguage() + ".properties");
            if (lres == null) {
                lres = app.getHost().getResource("/locale.properties");
            }
            Properties properties = new Properties();
            if (lres != null) properties.load(lres.getInputStream());
            MNls nls = new MNls(properties, null);
            
            //Perform substr operation on string.
            // out.println("[" + key + ":" + l.getLanguage() + "]" );
            out.println(nls.find(key) );

            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return SKIP_BODY;
    }

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
}
