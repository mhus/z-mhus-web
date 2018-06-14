package de.mhus.cherry.web.core;

import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;

public class CherryServletPrintWriter extends PrintWriter {

	public CherryServletPrintWriter(ServletOutputStream outputStream) {
		super(outputStream);
	}

	@Override
	public void close() {
		flush();
	}

}
