package de.mhus.cherry.renderer.jsp;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.impl.CherryCallContext;

public class ExtendedServletResponse extends HttpServletResponseWrapper {

    private int httpStatus;

    public static void inject(CallContext context) {
    	if (!(context.getHttpResponse() instanceof ExtendedServletResponse))
    		((CherryCallContext)context).setHttpResponse(new ExtendedServletResponse(context.getHttpResponse()));
    }
    
    public ExtendedServletResponse(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void sendError(int sc) throws IOException {
        httpStatus = sc;
        super.sendError(sc);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        httpStatus = sc;
        super.sendError(sc, msg);
    }


    @Override
    public void setStatus(int sc) {
        httpStatus = sc;
        super.setStatus(sc);
    }

    public int getStatus() {
        return httpStatus;
    }

	public static boolean isExtended(CallContext context) {
		return context.getHttpResponse() instanceof ExtendedServletResponse;
	}

	public static ExtendedServletResponse getExtendedResponse(
			CallContext context) {
		return (ExtendedServletResponse) context.getHttpResponse();
	}

}