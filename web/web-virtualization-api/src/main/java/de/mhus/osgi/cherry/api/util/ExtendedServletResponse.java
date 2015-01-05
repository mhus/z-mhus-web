package de.mhus.osgi.cherry.api.util;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import de.mhus.osgi.cherry.api.central.CentralCallContext;

public class ExtendedServletResponse extends HttpServletResponseWrapper {

    private int httpStatus;

    public static void inject(CentralCallContext context) {
    	if (!(context.getResponse() instanceof ExtendedServletResponse))
    		context.setResponse(new ExtendedServletResponse(context.getResponse()));
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

	public static boolean isExtended(CentralCallContext context) {
		return context.getResponse() instanceof ExtendedServletResponse;
	}

	public static ExtendedServletResponse getExtendedResponse(
			CentralCallContext context) {
		return (ExtendedServletResponse) context.getResponse();
	}

}